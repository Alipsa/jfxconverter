/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil;

import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

/**
 * Main class, only used to give some informations about the version of
 * the library on the command line.
 *
 * @version 1.2.7
 */
public class Main_LGPL {

   private Main_LGPL() {
   }

   public static void main(String[] args) throws IOException {
      URL url = Thread.currentThread().getContextClassLoader().getResource("org/mdiutil/resources/MDIUtilsGPL.properties");
      try {
         PropertyResourceBundle prb = new PropertyResourceBundle(url.openStream());
         String version = prb.getString("version");
         String date = prb.getString("date");
         System.out.println("MDIUtilities version " + version + " build on " + date);
         System.out.println("Distributed under the LGPL license");
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
