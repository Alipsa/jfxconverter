/*------------------------------------------------------------------------------
 * Copyright (C) 2011, 2013 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import org.mdiutil.lang.ResourceLoader;

/**
 * The SwingLoader is a ResourceLoader which is also able to load Swing resources like icons or images.
 *
 * @version 1.0
 */
public class ResourceUILoader extends ResourceLoader {
   /**
    * Creates a new resource loader, retrieving resources in a package.
    * Example:
    * <pre>
    * ResourceLoader loader = new ResourceLoader("svglab/resources");
    * </pre>
    * It is useful to retrieve bundled resources.
    *
    * @param pack the package
    */
   public ResourceUILoader(String pack) {
      super(pack);
   }

   /**
    * Creates a new resource loader, retrieving resources in a package.
    * Example:
    * <pre>
    * ResourceLoader loader = new ResourceLoader("svglab/resources", myClassLoader);
    * </pre>
    * It is useful to retrieve bundled resources.
    *
    * @param pack the package
    * @param loader the Class loader
    */
   public ResourceUILoader(String pack, ClassLoader loader) {
      super(pack, loader);
   }

   /**
    * Creates a new resource loader, fetching resources in a File, which can be a directory.
    * It is useful to retrieve use properties resources
    *
    * @param file the File
    */
   public ResourceUILoader(File file) {
      super(file);
   }

   /**
    * Creates a new resource loader, fetching resources in the <code>url</code> URL (which
    * can be a directory)
    *
    * @param url the URL
    */
   public ResourceUILoader(URL url) {
      super(url);
   }

   /**
    * Return the icon of the relative name <code>icon</code> in the resource package or URL.
    *
    * @param icon the icon name
    * @return the icon
    */
   public ImageIcon getIcon(String icon) {
      ImageIcon imgicon = null;
      try {
         if (pack != null) {
            URL url = getURL(icon);
            imgicon = new ImageIcon(url);
         } else {
            imgicon = new ImageIcon(new File(resourceURL.getFile(), icon).toURI().toURL());
         }
      } catch (MalformedURLException e) {
         System.out.print(e);
      }
      return imgicon;
   }

   /**
    * Return the Image of the selected absolute URL.
    *
    * @return the image
    */
   public Image getImage() {
      return Toolkit.getDefaultToolkit().getImage(resourceURL);
   }

   /**
    * Return the image of the relative name <code>image</code> in the resource package or URL.
    *
    * @param image the image name
    * @return the image
    */
   public Image getImage(String image) {
      Image img = null;
      try {
         if (pack != null) {
            img = Toolkit.getDefaultToolkit().getImage(getURL(image));
         } else {
            img = Toolkit.getDefaultToolkit().getImage(new File(resourceURL.getFile(), image).toURI().toURL());
         }
      } catch (MalformedURLException e) {
         System.out.println(e);
      }
      return img;
   }
}
