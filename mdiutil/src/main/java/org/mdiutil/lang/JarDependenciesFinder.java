/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.mdiutil.io.FileUtilities;

/**
 * This class allows to get the array or list of Class-Path dependencies of one jar file or a list of jar files. The
 * dependencies are retrieved from the jar file Manifest <code>Class-Path</code> property.
 *
 * Note that:
 * <ul>
 * <li>The class tries not to include twice the same jar file in the result</li>
 * <li>The resulting URLs are all absolute. Relative dependencies will be correctly handled</li>
 * </ul>
 *
 * For example, for the following jar file Manifest, for the following Jar file: <code>L:/my/JarFile.jar</code>:
 * <pre>
 * Class-Path: myJar1.jar \
 *             myJar2.jar
 * </pre>
 *
 * The {@link #getJarDependencies(URL, boolean)} will return the following URLs with <code>include</code> set to false:
 * <ul>
 * <li><code>L:/my/myJar1.jar</code></li>
 * <li><code>L:/my/myJar2.jar</code></li>
 * </ul>
 *
 * @version 1.2.8
 */
public class JarDependenciesFinder {
   private JarDependenciesFinder() {
   }

   /**
    * Return the array of Class-Path dependencies of a list of jar files.
    *
    * @param jarFiles the list of jar files
    * @param include true if the initial jar files must be included in the array
    * @return the array of Class-Path dependencies
    */
   public static URL[] getJarDependencies(List<URL> jarFiles, boolean include) {
      List<URL> list = new ArrayList<>();
      Set<URL> existingURLs = new HashSet<>();
      Iterator<URL> it = jarFiles.iterator();
      while (it.hasNext()) {
         URL jarFile = it.next();
         List<URL> childList = getJarDependenciesAsList(jarFile, include, existingURLs);
         list.addAll(childList);
      }
      URL[] urls = getURLFromList(list);
      return urls;
   }

   /**
    * Return the list of Class-Path dependencies of a list of jar files.
    *
    * @param jarFiles the list of jar files
    * @param include true if the initial jar files must be included in the array
    * @return the list of Class-Path dependencies
    */
   public static List<URL> getJarDependenciesAsList(List<URL> jarFiles, boolean include) {
      List<URL> list = new ArrayList<>();
      Set<URL> existingURLs = new HashSet<>();
      Iterator<URL> it = jarFiles.iterator();
      while (it.hasNext()) {
         URL jarFile = it.next();
         List<URL> childList = getJarDependenciesAsList(jarFile, include, existingURLs);
         list.addAll(childList);
      }
      return list;
   }

   private static URL[] getURLFromList(List<URL> list) {
      if (list.isEmpty()) {
         return null;
      } else {
         URL[] urls = new URL[list.size()];
         for (int i = 0; i < list.size(); i++) {
            urls[i] = list.get(i);
         }
         return urls;
      }
   }

   /**
    * Return the array of Class-Path dependencies of a jar file.
    *
    * @param jarFile the jar file
    * @param include true if the initial jar file must be included in the array
    * @return the array of Class-Path dependencies
    */
   public static URL[] getJarDependencies(URL jarFile, boolean include) {
      List<URL> list = getJarDependenciesAsList(jarFile, include);
      URL[] urls = getURLFromList(list);
      return urls;
   }

   /**
    * Return the list of Class-Path dependencies of a jar file.
    *
    * @param jarFile the jar file
    * @param include true if the initial jar file must be included in the array
    * @return the list of Class-Path dependencies
    */
   public static List<URL> getJarDependenciesAsList(URL jarFile, boolean include) {
      return getJarDependenciesAsList(jarFile, include, new HashSet<>());
   }

   private static List<URL> getJarDependenciesAsList(URL jarFile, boolean include, Set<URL> existingURLs) {
      List<URL> list = new ArrayList<>();
      if (include) {
         list.add(jarFile);
      }
      List<URL> dependencies = getDependencies(jarFile, existingURLs);
      list.addAll(dependencies);

      return list;
   }

   private static List<URL> getDependencies(URL jarFile, Set<URL> existingURLs) {
      URL parent = FileUtilities.getParentURL(jarFile);
      String classPath = getClassPathProperty(jarFile);
      if (classPath != null) {
         List<URL> list = new ArrayList<>();
         StringTokenizer tok = new StringTokenizer(classPath, " ");
         while (tok.hasMoreTokens()) {
            String path = tok.nextToken().trim();
            if (!path.isEmpty() && !path.equals("\\")) {
               URL url = FileUtilities.getChildURL(parent, path);
               if (!existingURLs.contains(url)) {
                  existingURLs.add(url);
                  list.add(url);
                  List<URL> childList = getDependencies(url, existingURLs);
                  if (childList != null) {
                     list.addAll(childList);
                  }
               }
            }
         }
         return list;
      } else {
         return null;
      }
   }

   private static String getClassPathProperty(URL jarFile) {
      // get Manifest
      try (JarInputStream stream = new JarInputStream(jarFile.openStream())) {
         Manifest manifest = stream.getManifest();
         if (manifest != null) {
            java.util.jar.Attributes attrs = manifest.getMainAttributes();
            String classPath = attrs.getValue("Class-Path");
            return classPath;
         } else {
            return null;
         }
      } catch (IOException e) {
         return null;
      }
   }
}
