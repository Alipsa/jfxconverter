/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2011, 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;

/**
 * Manages application resources. To use this class:
 * <ul>
 * <li>Create the ResourceLoader with a choice of:
 * <ul>
 * <li>the reference package (if you want to retrieve bundled resources)</li>
 * <li>or the reference URL (if you want to retrieve external resources). This reference URL can either be the URL of a file or
 * a directory</li>
 * </ul>
 * <li>get the different resources, which can be:
 * <ul>
 * <li>relative to the reference package or URL</li>
 * <li>absolute if the reference URL was a File</li>
 * </ul>
 * </li>
 * </ul>
 *
 * Remark: The resources can be: PropertyResourceBundles, Icons, or Images.
 *
 * <h1>ClassLoader</h1>
 * By default the ClassLoader used to retrieve the resources is the current ContextClassLoader. However one of the
 * constructors allows to create the class and specify which ClassLoader to use.
 *
 * It is important to consider that if you try to retrieve resources in jar files which are not part of the application or
 * library ClassPath (such as jar files loaded at runtime), you will need to create an <code>URLClassLoader</code> to load these resources,
 * because the original ContextClassLoader won't be able to load the resources in this jar file.
 *
 * <h2>Example</h2>
 * For example, suppose that we have a Jar File loaded at runtime with the following structure:
 * <pre>
 * theJarFile.jar:
 * ==&gt; the
 * ====&gt; package
 * ======&gt; resource.xml
 * </pre>
 *
 * We could use the following code to retrieve the <code>resource.xml</code> resource:
 * <pre>
 * URL url = theJarFile.toURI().toURL();
 * URL[] urls = new URL[1]
 * urls[0] = url;
 *
 * ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();
 * URLClassLoader loader = new URLClassLoader(urls, parentLoader);
 *
 * ResourceLoader loader = new ResourceLoader("the/package", loader);
 * URL resourceURL = loader.getURL("resource.xml");
 * </pre>
 *
 * @version 0.9.5
 */
public class ResourceLoader {
   protected URL resourceURL = null;
   /**
    * The package path.
    */
   protected String pack = null;
   /**
    * The class loader used to retrieve the resources.
    */
   protected ClassLoader loader = Thread.currentThread().getContextClassLoader();

   /**
    * Creates a new resource loader, retrieving resources in a package, using the current ContextClassLoader.
    *
    * Example:
    * <pre>
    * ResourceLoader loader = new ResourceLoader("svglab/resources");
    * </pre>
    * It is useful to retrieve bundled resources.
    *
    * @param pack the package
    */
   public ResourceLoader(String pack) {
      this.pack = pack;
   }

   /**
    * Creates a new resource loader, retrieving resources in the <code>pack</code> package, using a specified ClassLoader.
    *
    * Example:
    * <pre>
    * ResourceLoader loader = new ResourceLoader("mypack/resources", myClassLoader);
    * </pre>
    * It is useful to retrieve bundled resources.
    *
    * @param pack the package
    * @param loader the ClassLoader
    */
   public ResourceLoader(String pack, ClassLoader loader) {
      this.pack = pack;
      this.loader = loader;
   }

   /**
    * Creates a new resource loader, fetching resources in the <code>file</code> File, which can be a directory.
    * It is useful to retrieve use properties resources.
    *
    * @param file the File
    */
   public ResourceLoader(File file) {
      try {
         resourceURL = file.toURI().toURL();
      } catch (MalformedURLException e) {
         System.out.print(e);
      }
   }

   /**
    * Creates a new resource loader, fetching resources in the <code>url</code> URL (which can be a directory).
    *
    * @param url the URL
    */
   public ResourceLoader(URL url) {
      this.resourceURL = url;
   }

   /**
    * Return the resource URL.
    *
    * @return the resource URL
    */
   public URL getResourceURL() {
      return resourceURL;
   }

   /**
    * Return the ResourceLoader used package.
    *
    * @return the ResourceLoader used package
    */
   public String getPackage() {
      return pack;
   }

   /**
    * Return the default ClassLoader used by this Class.
    *
    * @return the default ClassLoader used by this Class
    */
   public ClassLoader getDefaultClassLoader() {
      return loader;
   }

   /**
    * Return the PropertyResourceBundle of the selected absolute URL.
    *
    * @return the PropertyResourceBundle
    */
   public PropertyResourceBundle getPropertyResourceBundle() {
      PropertyResourceBundle prb = null;
      try {
         prb = new PropertyResourceBundle(resourceURL.openStream());
      } catch (IOException e) {
         System.out.println(e);
      }
      return prb;
   }

   /**
    * Return the PropertyResourceBundle looking for the <code>bundle</code> in the resource package or URL.
    * This method will use the default ClassLoader.
    *
    * @param bundle the bundle
    * @return the PropertyResourceBundle
    */
   public PropertyResourceBundle getPropertyResourceBundle(String bundle) {
      PropertyResourceBundle prb = null;
      try {
         if (pack != null) {
            prb = new PropertyResourceBundle(getURL(bundle).openStream());
         } else {
            prb = new PropertyResourceBundle(new File(resourceURL.getFile(), bundle).toURI().toURL().openStream());
         }
      } catch (MalformedURLException e) {
         System.out.println(e);
      } catch (IOException e) {
         System.out.println(e);
      }
      return prb;
   }

   /**
    * Return the URL with a specified path, using the package provided at the class creation, and the default ClassLoader.
    * The package will be the package specified at the class creation (see {@link #getPackage()}).
    *
    * There are two cases for returning the URL:
    * <ul>
    * <li>If the package is null, return the URL as an absolute URL</li>
    * <li>If the package is not null, return the URL as an URL relative to the package</li>
    * </ul>
    *
    * @param path the path
    * @return the URL, or null if the class was not created specifying a package
    */
   public URL getURL(String path) {
      if (pack != null) {
         return loader.getResource(pack + "/" + path);
      } else {
         return null;
      }
   }

   /**
    * Return an URL for a resource, using the current ContextClassLoader.
    * The package will be the package specified at the class creation (see {@link #getPackage()}).
    *
    * @param pack the package
    * @param name the resource name
    * @return the resource URL, or null if the class was not created specifying a package
    */
   public static URL getURL(String pack, String name) {
      if (pack != null) {
         return Thread.currentThread().getContextClassLoader().getResource(pack + "/" + name);
      } else {
         return null;
      }
   }

   /**
    * Return an URL for a resource, using the a specified ClassLoader.
    * The package will be the package specified at the class creation (see {@link #getPackage()}).
    *
    * @param pack the package
    * @param name the resource name
    * @param loader the ClassLoader
    * @return the resource URL, or null if the class was not created specifying a package
    */
   public static URL getURL(String pack, String name, ClassLoader loader) {
      if (pack != null) {
         return loader.getResource(pack + "/" + name);
      } else {
         return null;
      }
   }

   /**
    * Return an InputStream for a resource, using the default ClassLoader.
    * The package will be the package specified at the class creation (see {@link #getPackage()}).
    *
    * @param name the resource name
    * @return the resource URL, or null if the class was not created specifying a package
    */
   public InputStream getResourceAsStream(String name) {
      if (pack != null) {
         return loader.getResourceAsStream(pack + "/" + name);
      } else {
         return null;
      }
   }

   /**
    * Return an InputStream for a resource, using the current ContextClassLoader.
    *
    * @param pack the package
    * @param name the resource name
    * @return the resource URL
    */
   public static InputStream getResourceAsStream(String pack, String name) {
      if (pack != null) {
         return Thread.currentThread().getContextClassLoader().getResourceAsStream(pack + "/" + name);
      } else {
         return null;
      }
   }

   /**
    * Return an InputStream for a resource, using the a specified ClassLoader.
    *
    * @param pack the package
    * @param name the resource name
    * @param loader the ClassLoader
    * @return the resource URL
    */
   public static InputStream getResourceAsStream(String pack, String name, ClassLoader loader) {
      if (pack != null) {
         return loader.getResourceAsStream(pack + "/" + name);
      } else {
         return null;
      }
   }
}
