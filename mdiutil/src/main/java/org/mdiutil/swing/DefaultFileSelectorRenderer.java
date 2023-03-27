/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.io.File;

/**
 * This class is a default file selector renderer.
 *
 * @since 1.2.19
 */
public class DefaultFileSelectorRenderer implements FileSelectorRenderer {
   /**
    * Render a list of files. It will show a String shpwing the absolute path of each file.
    *
    * @param files the files
    * @return the associated text
    */
   @Override
   public String render(File[] files) {
      if (files == null || files.length == 0) {
         return renderEmptyFileList();
      }
      StringBuilder text = new StringBuilder();
      for (int i = 0; i < files.length; i++) {
         if (i > 0) {
            text = text.append(" ");
         }
         // handle the case when one file in the array is null
         if (files[i] != null) {
            text = text.append("\"").append(files[i].getAbsolutePath()).append("\"");
         }
      }
      return text.toString();
   }

   /**
    * Render file.
    *
    * @param file the file
    * @return the associated text
    */
   @Override
   public String render(File file) {
      String text = file.getPath();
      return text;
   }
}
