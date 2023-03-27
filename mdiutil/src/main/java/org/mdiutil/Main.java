/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil;

import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

/**
 * Main class, only used to give some informations about the version of the library on the command line.
 *
 * @version 0.8
 */
public class Main {
   public static void main(String[] args) {
      URL url = Thread.currentThread().getContextClassLoader().getResource("org/mdiutil/resources/MDIUtils.properties");
      try {
         PropertyResourceBundle prb = new PropertyResourceBundle(url.openStream());
         String version = prb.getString("version");
         String date = prb.getString("date");
         System.out.println("MDIUtilities version " + version + " build on " + date);
         System.out.println("Distributed under both the APACHE and LGPL license");
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
