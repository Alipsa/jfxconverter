/*
 * Copyright 1997-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2010, 2019 Herve Girod
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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * An URL connection used in a jar file.
 *
 * @author Benjamin Renaud
 * @author Herve Girod
 * @version 1.2.4
 */
class JarURLConnection extends java.net.JarURLConnection {

   /* the Jar file factory. It handles both retrieval and caching.
    */
   private static final JarFileFactory FACTORY = new JarFileFactory();

   /* the url for the Jar file */
   private final URL jarFileURL;

   /* the permission to get this JAR file. This is the actual, ultimate,
     * permission, returned by the jar file factory.
    */
   private Permission permission;

   /* the url connection for the JAR file */
   private URLConnection _jarFileURLConnection;

   /* the entry name, if any */
   private final String entryName;

   /* the JarEntry */
   private JarEntry jarEntry;

   /* the jar file corresponding to this connection */
   private JarFile jarFile;

   /* the content type for this connection */
   private String contentType;

   public JarURLConnection(URL url) throws MalformedURLException, IOException {
      super(url);

      jarFileURL = getJarFileURL();
      _jarFileURLConnection = jarFileURL.openConnection();
      entryName = getEntryName();
   }

   /**
    * Return the jar file.
    *
    * @return the jar file
    * @throws IOException
    */
   @Override
   public JarFile getJarFile() throws IOException {
      connect();
      return jarFile;
   }

   /**
    * Return the jar entry.
    *
    * @return the jar entry
    * @throws IOException
    */
   @Override
   public JarEntry getJarEntry() throws IOException {
      connect();
      return jarEntry;
   }

   /**
    * Returns a permission object representing the permission necessary to make the connection represented by this object.
    */
   @Override
   public Permission getPermission() throws IOException {
      return _jarFileURLConnection.getPermission();
   }

   class JarURLInputStream extends java.io.FilterInputStream {
      JarURLInputStream(InputStream src) {
         super(src);
      }

      /**
       * Closes this input stream and releases any system resources associated with the stream.
       */
      @Override
      public void close() throws IOException {
         try {
            super.close();
         } finally {
            if (!getUseCaches()) {
               jarFile.close();
            }
         }
      }
   }

   /**
    * Advertise that this connection is connected.
    */
   @Override
   public void connect() throws IOException {
      if (!connected) {
         /* the factory call will do the security checks */
         jarFile = FACTORY.get(getJarFileURL(), getUseCaches());

         /* we also ask the factory the permission that was required
             * to get the jarFile, and set it as our permission.
          */
         if (getUseCaches()) {
            _jarFileURLConnection = FACTORY.getConnection(jarFile);
         }

         if ((entryName != null)) {
            jarEntry = (JarEntry) jarFile.getEntry(entryName);
            if (jarEntry == null) {
               try {
                  if (!getUseCaches()) {
                     jarFile.close();
                  }
               } catch (IOException e) {
               }
               throw new FileNotFoundException("JAR entry " + entryName + " not found in " + jarFile.getName());
            }
         }
         connected = true;
      }
   }

   /**
    * Creates the input stream for the URL.
    *
    * @return the input stream for the URL.
    * @exception IOException if an I/O error occurs while creating the input stream
    */
   @Override
   public InputStream getInputStream() throws IOException {
      connect();

      InputStream result = null;

      if (entryName == null) {
         throw new IOException("no entry name specified");
      } else {
         if (jarEntry == null) {
            throw new FileNotFoundException("JAR entry " + entryName + " not found in " + jarFile.getName());
         }
         result = new JarURLInputStream(jarFile.getInputStream(jarEntry));
      }
      return result;
   }

   /**
    * Returns the value of the {@code content-length} header field.
    */
   @Override
   public int getContentLength() {
      int result = -1;
      try {
         connect();
         if (jarEntry == null) {
            /* if the URL referes to an archive */
            result = _jarFileURLConnection.getContentLength();
         } else {
            /* if the URL referes to an archive entry */
            result = (int) getJarEntry().getSize();
         }
      } catch (IOException e) {
      }
      return result;
   }

   /**
    * Retrieves the contents of this URL connection.
    */
   @Override
   public Object getContent() throws IOException {
      Object result;

      connect();
      if (entryName == null) {
         result = jarFile;
      } else {
         result = super.getContent();
      }
      return result;
   }

   /**
    * Returns the value of the {@code content-type} header field.
    */
   @Override
   public String getContentType() {
      if (contentType == null) {
         if (entryName == null) {
            contentType = "x-java/jar";
         } else {
            try {
               connect();
               try (InputStream in = jarFile.getInputStream(jarEntry)) {
                  contentType = guessContentTypeFromStream(new BufferedInputStream(in));
               }
            } catch (IOException e) {
               // don't do anything
            }
         }
         if (contentType == null) {
            contentType = guessContentTypeFromName(entryName);
         }
         if (contentType == null) {
            contentType = "content/unknown";
         }
      }
      return contentType;
   }

   /**
    * Returns the value of the named header field.
    */
   @Override
   public String getHeaderField(String name) {
      return _jarFileURLConnection.getHeaderField(name);
   }

   /**
    * Sets the general request property.
    */
   @Override
   public void setRequestProperty(String key, String value) {
      _jarFileURLConnection.setRequestProperty(key, value);
   }

   /**
    * Returns the value of the named general request property for this
    * connection.
    */
   @Override
   public String getRequestProperty(String key) {
      return _jarFileURLConnection.getRequestProperty(key);
   }

   /**
    * Adds a general request property specified by a
    * key-value pair. This method will not overwrite
    * existing values associated with the same key.
    */
   @Override
   public void addRequestProperty(String key, String value) {
      _jarFileURLConnection.addRequestProperty(key, value);
   }

   /**
    * Returns an unmodifiable Map of general request
    * properties for this connection. The Map keys
    * are Strings that represent the request-header
    * field names. Each Map value is a unmodifiable List
    * of Strings that represents the corresponding
    * field values.
    *
    * @return a Map of the general request properties for this connection.
    */
   @Override
   public Map<String, List<String>> getRequestProperties() {
      return _jarFileURLConnection.getRequestProperties();
   }

   /**
    * Set the value of the <code>allowUserInteraction</code> field of
    * this <code>URLConnection</code>.
    */
   @Override
   public void setAllowUserInteraction(boolean allowuserinteraction) {
      _jarFileURLConnection.setAllowUserInteraction(allowuserinteraction);
   }

   /**
    * Returns the value of the <code>allowUserInteraction</code> field for
    * this object.
    */
   @Override
   public boolean getAllowUserInteraction() {
      return _jarFileURLConnection.getAllowUserInteraction();
   }

   /*
     * cache control
    */
   /**
    * Sets the value of the <code>useCaches</code> field of this
    * <code>URLConnection</code> to the specified value.
    * <p>
    * Some protocols do caching of documents. Occasionally, it is important
    * to be able to "tunnel through" and ignore the caches (e.g., the
    * "reload" button in a browser). If the UseCaches flag on a connection
    * is true, the connection is allowed to use whatever caches it can.
    * If false, caches are to be ignored.
    * The default value comes from DefaultUseCaches, which defaults to
    * true.
    */
   @Override
   public void setUseCaches(boolean usecaches) {
      _jarFileURLConnection.setUseCaches(usecaches);
   }

   /**
    * Returns the value of this <code>URLConnection</code>'s
    * <code>useCaches</code> field.
    */
   @Override
   public boolean getUseCaches() {
      return _jarFileURLConnection.getUseCaches();
   }

   /**
    * Sets the value of the <code>ifModifiedSince</code> field of
    * this <code>URLConnection</code> to the specified value.
    */
   @Override
   public void setIfModifiedSince(long ifmodifiedsince) {
      _jarFileURLConnection.setIfModifiedSince(ifmodifiedsince);
   }

   /**
    * Sets the default value of the <code>useCaches</code> field to the
    * specified value.
    *
    * @param defaultusecaches the new value.
    * @see URLConnection#useCaches
    */
   @Override
   public void setDefaultUseCaches(boolean defaultusecaches) {
      _jarFileURLConnection.setDefaultUseCaches(defaultusecaches);
   }

   /**
    * Returns the default value of a <code>URLConnection</code>'s
    * <code>useCaches</code> flag.
    * <p>
    * Ths default is "sticky", being a part of the static state of all
    * URLConnections. This flag applies to the next, and all following
    * URLConnections that are created.
    *
    */
   @Override
   public boolean getDefaultUseCaches() {
      return _jarFileURLConnection.getDefaultUseCaches();
   }
}
