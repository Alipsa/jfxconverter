/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.net;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A zip URLStreamHandlerFactory.
 *
 * @since 1.2.10
 */
public class ZipStreamHandlerFactory implements URLStreamHandlerFactory {
   private static ZipStreamHandlerFactory factory = null;

   @Override
   public URLStreamHandler createURLStreamHandler(String protocol) {
      if (protocol.equals("zip")) {
         return new ZipStreamHandler();
      } else {
         return null;
      }
   }

   /**
    * Install the factory.
    */
   public static void installFactory() {
      if (factory == null) {
         factory = new ZipStreamHandlerFactory();
         URL.setURLStreamHandlerFactory(factory);
      }
   }
}
