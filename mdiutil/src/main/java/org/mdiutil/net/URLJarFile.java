/*
 * Copyright 2001-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2010, 2016, 2019 Herve Girod
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package org.mdiutil.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * URL jar file is a common JarFile subtype used for JarURLConnection.
 *
 * @version 1.0
 */
class URLJarFile extends JarFile {
   /* Controller of the Jar File's closing */
   private JarFileFactory closeController = null;
   private static int BUF_SIZE = 2048;
   private Manifest superMan;
   private Attributes superAttr;
   private Map<String, Attributes> superEntries;

   static JarFile getJarFile(URL url) throws IOException {
      return getJarFile(url, null);
   }

   static JarFile getJarFile(URL url, JarFileFactory closeController) throws IOException {
      if (isFileURL(url)) {
         return new URLJarFile(url, closeController);
      } else {
         return retrieve(url, closeController);
      }
   }

   /*
    * Changed modifier from private to public in order to be able
    * to instantiate URLJarFile from sun.plugin package.
    */
   public URLJarFile(File file) throws IOException {
      this(file, null);
   }

   /*
    * Changed modifier from private to public in order to be able
    * to instantiate URLJarFile from sun.plugin package.
    */
   public URLJarFile(File file, JarFileFactory closeController) throws IOException {
      super(file, true, ZipFile.OPEN_READ | ZipFile.OPEN_DELETE);
      this.closeController = closeController;
   }

   private URLJarFile(URL url, JarFileFactory closeController) throws IOException {
      super(ParseUtil.decodePath(url.getFile()));
      this.closeController = closeController;
   }

   private static boolean isFileURL(URL url) {
      if (url.getProtocol().equalsIgnoreCase("file")) {
         /*
          * Consider this a 'file' only if it's a LOCAL file, because
          * 'file:' URLs can be accessible through ftp.
          */
         String host = url.getHost();
         if (host == null || host.equals("") || host.equals("~") || host.equalsIgnoreCase("localhost")) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the <code>ZipEntry</code> for the given entry name or <code>null</code> if not found.
    *
    * @param name the JAR file entry name
    * @return the <code>ZipEntry</code> for the given entry name or
    * <code>null</code> if not found
    * @see ZipEntry
    */
   @Override
   public ZipEntry getEntry(String name) {
      ZipEntry ze = super.getEntry(name);
      if (ze != null) {
         if (ze instanceof JarEntry) {
            return new URLJarFileEntry((JarEntry) ze);
         } else {
            throw new InternalError(super.getClass() + " returned unexpected entry type " + ze.getClass());
         }
      }
      return null;
   }

   @Override
   public Manifest getManifest() throws IOException {
      if (!isSuperMan()) {
         return null;
      }

      Manifest man = new Manifest();
      Attributes attr = man.getMainAttributes();
      attr.putAll((Map) superAttr.clone());

      // now deep copy the manifest entries
      if (superEntries != null) {
         Map<String, Attributes> entries = man.getEntries();
         Iterator<String> it = superEntries.keySet().iterator();
         while (it.hasNext()) {
            String key = it.next();
            Attributes at = (Attributes) superEntries.get(key);
            entries.put(key, (Attributes) at.clone());
         }
      }

      return man;
   }

   /**
    * If close controller is set the notify the controller about the pending close.
    */
   @Override
   public void close() throws IOException {
      if (closeController != null) {
         closeController.close(this);
      }
      super.close();
   }

   // optimal side-effects
   private synchronized boolean isSuperMan() throws IOException {

      if (superMan == null) {
         superMan = super.getManifest();
      }

      if (superMan != null) {
         superAttr = superMan.getMainAttributes();
         superEntries = superMan.getEntries();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Given a URL, retrieves a JAR file, caches it to disk, and creates a
    * cached JAR file object.
    */
   private static JarFile retrieve(final URL url) throws IOException {
      return retrieve(url, null);
   }

   /**
    * Given a URL, retrieves a JAR file, caches it to disk, and creates a
    * cached JAR file object.
    */
   private static JarFile retrieve(final URL url, final JarFileFactory closeController) throws IOException {
      JarFile result = null;

      /* get the stream before asserting privileges */
      NestableURLConnection con = new NestableURLConnection(url);
      final InputStream in = con.getInputStream();

      try {
         result = (JarFile) AccessController.doPrivileged(new PrivilegedExceptionAction() {
            @Override
            public Object run() throws IOException {
               OutputStream out = null;
               File tmpFile = null;
               try {
                  tmpFile = File.createTempFile("jar_cache", null);
                  tmpFile.deleteOnExit();
                  out = new FileOutputStream(tmpFile);
                  int read = 0;
                  byte[] buf = new byte[BUF_SIZE];
                  while ((read = in.read(buf)) != -1) {
                     out.write(buf, 0, read);
                  }
                  out.close();
                  out = null;
                  return new URLJarFile(tmpFile, closeController);
               } catch (IOException e) {
                  if (tmpFile != null) {
                     tmpFile.delete();
                  }
                  throw e;
               } finally {
                  if (in != null) {
                     in.close();
                  }
                  if (out != null) {
                     out.close();
                  }
               }
            }
         });
      } catch (PrivilegedActionException pae) {
         throw (IOException) pae.getException();
      }

      return result;
   }

   private class URLJarFileEntry extends JarEntry {
      private final JarEntry je;

      URLJarFileEntry(JarEntry je) {
         super(je);
         this.je = je;
      }

      @Override
      public Attributes getAttributes() throws IOException {
         if (URLJarFile.this.isSuperMan()) {
            Map e = URLJarFile.this.superEntries;
            if (e != null) {
               Attributes a = (Attributes) e.get(getName());
               if (a != null) {
                  return (Attributes) a.clone();
               }
            }
         }
         return null;
      }

      @Override
      public Certificate[] getCertificates() {
         Certificate[] certs = je.getCertificates();
         return certs == null ? null : certs.clone();
      }

      @Override
      public CodeSigner[] getCodeSigners() {
         CodeSigner[] csg = je.getCodeSigners();
         return csg == null ? null : csg.clone();
      }
   }

   public interface URLJarFileCloseController {
      public void close(JarFile jarFile);
   }
}
