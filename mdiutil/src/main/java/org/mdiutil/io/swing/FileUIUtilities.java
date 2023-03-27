/*------------------------------------------------------------------------------
* Copyright (C) 2011, 2016, 2019 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io.swing;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.mdiutil.io.FileUtilities;

/**
 * Various File utilities to deal with URLs. This version add the ability to retrieve swing images and icons from resources.
 *
 * @version 1.0
 */
public class FileUIUtilities extends FileUtilities {
   protected FileUIUtilities() {
      super();
   }

   /**
    * Return an Icon defined accessed by its a package.
    *
    * @param pack the package
    * @param name the name of the resource
    * @return the Icon
    */
   public static Icon getIcon(String pack, String name) {
      URL url = Thread.currentThread().getContextClassLoader().getResource(pack + "/" + name);
      return new ImageIcon(url);
   }
}
