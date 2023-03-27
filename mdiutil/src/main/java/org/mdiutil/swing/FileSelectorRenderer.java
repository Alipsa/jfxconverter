/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.io.File;

/**
 * A class responsible to render the selected files in the JFileSelector text editor.
 *
 * @since 1.2.19
 */
public interface FileSelectorRenderer {
   /**
    * Render a list of files.
    *
    * @param files the files
    * @return the associated text
    */
   public String render(File[] files);
   /**
    * Render file.
    *
    * @param file the file
    * @return the associated text
    */
   public String render(File file);
   /**
    * Render an empty file. Return an empty String by default.
    *
    * @return the associated text
    */
   public default String renderEmptyFileList() {
      return "";
   }
}
