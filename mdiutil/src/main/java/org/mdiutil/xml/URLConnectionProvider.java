/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import org.mdiutil.MDIUtilConfig;

/**
 * This class returns an URLConnection from an URL.
 *
 * @since 1.2.7
 */
class URLConnectionProvider {
   /**
    * Return an URLConnection from an URL
    *
    * @param allowNestableConnections true if nestable connections are allowed (ie, an xml file inside a zip file, for example).
    * @param url the URL
    * @return the URLConnection
    * @throws IOException
    */
   URLConnection getURLConnection(boolean allowNestableConnections, URL url) throws IOException {
      if (!allowNestableConnections) {
         return url.openConnection();
      } else if (MDIUtilConfig.getInstance().isLGPL()) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         try {
            Class<?> clazz = Class.forName("org.mdiutil.net.NestableURLConnection", true, loader);
            URLConnection con = (URLConnection) clazz.newInstance();
            Class[] types = new Class[1];
            types[0] = URL.class;
            Method m = clazz.getMethod("setURL", types);
            m.invoke(con, url);
            return con;
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
            | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            return url.openConnection();
         }
      } else {
         return url.openConnection();
      }
   }
}
