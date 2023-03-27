/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil;

import java.net.URL;

/**
 *
 * @since 1.2.7
 */
public class MDIUtilConfig {
   private static MDIUtilConfig config = null;
   private boolean isLGPL = true;

   private MDIUtilConfig() {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL url = loader.getResource("org/mdiutil/resources/MDIUtilsGPL.properties");
      if (url == null) {
         isLGPL = false;
      }
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static MDIUtilConfig getInstance() {
      if (config == null) {
         config = new MDIUtilConfig();
      }
      return config;
   }

   /**
    * Return true if this is the LGPL distribution.
    *
    * @return true if this is the LGPL distribution
    */
   public boolean isLGPL() {
      return isLGPL;
   }
}
