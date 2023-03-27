/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.mdiutil.io.FileUtilities;

/**
 * Return a reader from an URL with a specified charset.
 *
 * @since 1.2.8
 */
class ReaderPathConstructor {
   /**
    * Return a reader making sure the reader use the correct encoding and not the default platform encoding.
    *
    * @param url the URL the URL
    * @param charset the charset
    * @param the ClassLoader
    * @return the reader
    * @throws IOException
    */
   static Reader getReaderFromURL(URL url, Charset charset, ClassLoader loader) throws IOException {
      try {
         URL parentURL = FileUtilities.getParentURL(url);
         URI parentURI = getURI(parentURL, true);
         URI uri = getURI(url, false);
         FileSystem fileSystem;
         try {
            fileSystem = FileSystems.getFileSystem(parentURI);
         } catch (FileSystemNotFoundException e) {
            if (loader == null) {
               loader = Thread.currentThread().getContextClassLoader();
            }
            fileSystem = FileSystems.newFileSystem(parentURI, new HashMap<>(), loader);
         }
         Path path = fileSystem.provider().getPath(uri);
         Reader _reader = Files.newBufferedReader(path, charset);
         return _reader;
      } catch (URISyntaxException ex) {
         throw new IOException(ex);
      } catch (IllegalArgumentException ex) {
         throw ex;
      }
   }

   private static URI getURI(URL url, boolean isParent) throws URISyntaxException {
      String file = url.getFile();
      if (!file.startsWith("///")) {
         if (file.startsWith("/")) {
            file = "//" + file;
         } else if (file.startsWith("//")) {
            file = "/" + file;
         }
      }
      if (isParent) {
         file = file + "/";
      }
      String host = url.getHost();
      int port = url.getPort();
      String protocol = url.getProtocol();
      if (isParent && protocol.equals("file")) {
         return new URI("file:///");
      } else {
         try {
            url = new URL(protocol, host, port, file);
         } catch (MalformedURLException ex) {
         }

         return url.toURI();
      }
   }
}
