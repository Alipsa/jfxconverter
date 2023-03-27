/*------------------------------------------------------------------------------
 * Copyright (C) 2010, 2011, 2018, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * An URLConnection that can access URLs in nested archives (for example a resource in a jar nested in another jar).
 *
 * Note that to be able to use this connection with nested archives, you will need to install the
 * {@link ZipStreamHandlerFactory}, because by default Java does not accept to handle nested jar protocols (and the jar
 * protocol is used even for zip files).
 *
 * @version 1.2.10
 */
class NestableJarURLConnection extends NestableURLConnection {
   private JarFile jarFile;
   /* the JarEntry */
   private JarEntry jarEntry;
   /* the Jar file factory. It handles both retrieval and caching.
    */
   private static final JarFileFactory JARFACTORY = new JarFileFactory();
   /* the entry name, if any */
   private String entryName;

   /**
    * Constructs a new connection for the URL.
    *
    * @param url the URL of this connection.
    */
   public NestableJarURLConnection(URL url) throws MalformedURLException {
      super(url);
   }

   public URL getJarFileURL() {
      return getURL();
   }

   /**
    * Advertise that this connection is connected. The {@link #connected} field will be set to true.
    */
   @Override
   public void connect() throws IOException {
      if (!connected) {
         /* the factory call will do the security checks */
         jarFile = JARFACTORY.get(getJarFileURL(), getUseCaches());

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
    * Return the jar file.
    *
    * @return the jar file
    * @throws IOException
    */
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
   public JarEntry getJarEntry() throws IOException {
      connect();
      return jarEntry;
   }

}
