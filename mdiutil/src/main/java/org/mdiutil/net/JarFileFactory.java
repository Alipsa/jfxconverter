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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/*
 * A factory for cached JAR file. This class is used to both retrieve and cache Jar files.
 *
 * @author Benjamin Renaud
 * @version 1.2.4
 */
class JarFileFactory {
   /*
    * The url to file cache.
    */
   private static final Map<URL, JarFile> FILECACHE = new HashMap<>();

   /*
    * The file to url cache.
    */
   private static final Map<JarFile, URL> URLCACHE = new HashMap<>();

   URLConnection getConnection(JarFile jarFile) throws IOException {
      URL u = (URL) URLCACHE.get(jarFile);
      if (u != null) {
         NestableURLConnection con = new NestableURLConnection(u);
         return con;
      }

      return null;
   }

   public JarFile get(URL url) throws IOException {
      return get(url, true);
   }

   JarFile get(URL url, boolean useCaches) throws IOException {
      JarFile result;
      JarFile localResult;

      if (useCaches) {
         synchronized (this) {
            result = getCachedJarFile(url);
         }
         if (result == null) {
            localResult = URLJarFile.getJarFile(url, this);
            synchronized (this) {
               result = getCachedJarFile(url);
               if (result == null) {
                  FILECACHE.put(url, localResult);
                  URLCACHE.put(localResult, url);
                  result = localResult;
               } else {
                  if (localResult != null) {
                     localResult.close();
                  }
               }
            }
         }
      } else {
         result = URLJarFile.getJarFile(url, this);
      }
      if (result == null) {
         throw new FileNotFoundException(url.toString());
      }

      return result;
   }

   /**
    * Callback method of the URLJarFileCloseController to
    * indicate that the JarFile is close. This way we can
    * remove the JarFile from the cache
    */
   public void close(JarFile jarFile) {
      URL urlRemoved = URLCACHE.remove(jarFile);
      if (urlRemoved != null) {
         FILECACHE.remove(urlRemoved);
      }
   }

   private JarFile getCachedJarFile(URL url) {
      JarFile result = FILECACHE.get(url);

      /*
       * if the JAR file is cached, the permission will always be there
       */
      if (result != null) {
         Permission perm = getPermission(result);
         if (perm != null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
               try {
                  sm.checkPermission(perm);
               } catch (SecurityException se) {
                  // fallback to checkRead/checkConnect for pre 1.2
                  // security managers
                  if ((perm instanceof java.io.FilePermission) && perm.getActions().contains("read")) {
                     sm.checkRead(perm.getName());
                  } else if ((perm instanceof java.net.SocketPermission) && perm.getActions().contains("connect")) {
                     sm.checkConnect(url.getHost(), url.getPort());
                  } else {
                     throw se;
                  }
               }
            }
         }
      }
      return result;
   }

   private Permission getPermission(JarFile jarFile) {
      try {
         URLConnection uc = getConnection(jarFile);
         if (uc != null) {
            return uc.getPermission();
         }
      } catch (IOException ioe) {
      }

      return null;
   }
}
