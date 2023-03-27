/*
 * Copyright (c) 2016 Erik Håkansson, http://squark.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*
Copyright (c) 2021, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.lang.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * An URLClassLoader which is able to load classes in jars inside a jar.
 *
 * @since 1.2.23
 */
public class NestedURLClassLoader extends URLClassLoader {
   private final Map<String, Set<URL>> resources = new HashMap();
   private final Map<String, byte[]> byteCache = new HashMap<>();
   private final Map<String, Class<?>> classes = new HashMap<>();
   private Set<String> filteredPackages = null;
   private boolean excludingFilter = true;

   /**
    * Constructs a new NestedURLClassLoader for the specified URLs using the default delegation parent ClassLoader.
    *
    * @param urls the URLs from which to load classes and resources
    */
   public NestedURLClassLoader(URL[] urls) {
      super(urls);
      setContextClassLoaderImpl();
      addURLs(urls);
   }

   /**
    * Constructs a new NestedURLClassLoader for the specified URLs using a specified parent ClassLoader.
    *
    * @param urls the URLs from which to load classes and resources
    * @param parent the parent ClassLoader
    */
   public NestedURLClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
      setContextClassLoaderImpl();
      addURLs(urls);
   }

   /**
    * Exclude or include some packages from the special class loading used by this class.
    *
    * <h1>Algorithm</h1>
    * <ul>
    * <li>If <code>excludingFilter</code> is true: packages which are in the Set will be loaded using the default behavior
    * of the <code>URLClassLoader</code>, all other packages will be loaded using the special behavior of this Class</li>
    * <li>If <code>excludingFilter</code> is false: packages which are not in the set will be loaded using the default behavior
    * of the <code>URLClassLoader</code>, all packages in the Set will be loaded using the special behavior of this Class</li>
    * </ul>
    *
    * @param excludingFilter true if the packages will be excluded if they belong to the packages Set
    * @param filteredPackages the packages
    */
   public void filterPackages(boolean excludingFilter, Set<String> filteredPackages) {
      this.filteredPackages = filteredPackages;
      this.excludingFilter = excludingFilter;
   }

   /**
    * Exclude or include some packages from the special class loading used by this class.
    *
    * @param excludingFilter true if the packages will be excluded if they belong to the packages set
    * @param filteredPackages the packages
    */
   public void filterPackages(boolean excludingFilter, String... filteredPackages) {
      if (this.filteredPackages == null) {
         this.filteredPackages = new HashSet<>();
         this.excludingFilter = excludingFilter;
      }
      this.filteredPackages.addAll(Arrays.asList(filteredPackages));
   }

   private void setContextClassLoaderImpl() {
      Thread.currentThread().setContextClassLoader(this);
   }

   @Override
   protected void addURL(URL url) {
      addURLs(url);
   }

   private void addURLs(URL... urls) {
      if (urls == null) {
         return;
      }
      try {
         for (URL url : urls) {
            addResource(url);
         }
      } catch (IOException e) {
      }
   }

   private void addResource(URL url) throws IOException {
      if (url.getProtocol().equals("jar")) {
         try (InputStream urlStream = url.openStream(); BufferedInputStream bufferedInputStream = new BufferedInputStream(urlStream); JarInputStream jarInputStream = new JarInputStream(bufferedInputStream)) {
            JarEntry jarEntry;

            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
               String spec;
               if (url.getProtocol().equals("jar")) {
                  spec = url.getPath();
               } else {
                  spec = url.getProtocol() + ":" + url.getPath();
               }
               URL theURL = new URL(null, "jar:" + spec + "!/" + jarEntry.getName(), new NestedURLStreamHandler());
               addURLToResource(jarEntry.getName(), theURL);
               addClassFromInputStream(jarInputStream, jarEntry.getName());
               if (jarEntry.getName().endsWith(".jar")) {
                  addResource(theURL);
               }
            }
         }
      } else if (url.getPath().endsWith(".class")) {
         throw new IllegalStateException("Cannot add classes directly");
      } else {
         try {
            addDirectory(new File(url.toURI()));
         } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   private void addDirectory(File directory) throws IOException {
      if (!directory.isDirectory()) {
         throw new IllegalStateException("Not a directory: " + directory);
      }
      File[] files = directory.listFiles();
      if (files == null) {
         throw new IllegalStateException("No files found in " + directory);
      }
      for (File file : files) {
         if (file.isDirectory()) {
            addDirectory(file);
         } else if (file.getName().endsWith(".jar")) {
            try {
               addResource(file.toURI().toURL());
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }
         } else {
            try {
               String relativeName = directory.toURI().relativize(file.toURI()).getPath();
               try (FileInputStream fileInputStream = new FileInputStream(file)) {
                  addClassFromInputStream(fileInputStream, relativeName);
                  addURLToResource(relativeName, file.toURI().toURL());
               }
            } catch (MalformedURLException | FileNotFoundException e) {
               throw new IllegalStateException(e);
            }
         }
      }
   }

   private void addClassFromInputStream(InputStream inputStream, String relativePath) throws IOException {
      if (relativePath.endsWith(".class")) {
         int len;
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] b = new byte[2048];

         while ((len = inputStream.read(b)) > 0) {
            out.write(b, 0, len);
         }
         out.close();
         byte[] classBytes = out.toByteArray();
         String className = resourceToClassName(relativePath);
         addToByteCache(className, classBytes);
      }
   }

   private void addToByteCache(String className, byte[] classBytes) {
      byteCache.put(className, classBytes);
   }

   private String resourceToClassName(String slashed) {
      return slashed.substring(0, slashed.lastIndexOf(".class")).replace("/", ".");
   }

   private void addURLToResource(String name, URL url) {
      Set<URL> set;
      if (resources.containsKey(name)) {
         set = resources.get(name);
      } else {
         set = new HashSet<>();
         resources.put(name, set);
      }
      set.add(url);
   }

   private Class<?> loadFromSuperClassImpl(String name, boolean resolve) throws ClassNotFoundException {
      Iterator<String> it = filteredPackages.iterator();
      while (it.hasNext()) {
         String thePackage = it.next() + ".";
         if (excludingFilter && name.startsWith(thePackage)) {
            return super.loadClass(name, resolve);
         } else if (!excludingFilter && !name.startsWith(thePackage)) {
            return super.loadClass(name, resolve);
         }
      }
      return null;
   }

   @Override
   public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      Class<?> found = null;
      if (name.startsWith("org.mdiutil.lang.loader")) {
         found = super.loadClass(name, resolve);
      } else if (name.startsWith("java.") || name.startsWith("javax.")) {
         found = getSystemClassLoader().loadClass(name);
      } else if (name.startsWith("javafx.")) {
         // javafx packages are not automatically on the classpath beginning with Java 11
         found = this.getParent().loadClass(name);
      } else if (filteredPackages != null) {
         found = loadFromSuperClassImpl(name, resolve);
      }

      if (found == null) {
         found = findLocalClassImpl(name, resolve);
      }
      if (found == null) {
         try {
            found = super.loadClass(name, resolve);
         } catch (NullPointerException e) {
         }
      }
      if (found == null) {
         found = getSystemClassLoader().loadClass(name);
      }
      if (found != null) {
         return found;
      }
      throw new ClassNotFoundException(name);
   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      return null;
   }

   private URL findLocalResource(String name) {
      Set<URL> foundResources = findLocalResources(name);
      if (foundResources.size() > 0) {
         return foundResources.iterator().next();
      }
      return null;
   }

   private Set<URL> findLocalResources(String name) {
      return resources.get(name);
   }

   @Override
   public URL findResource(String name) {
      URL foundResource = findLocalResource(name);
      return foundResource;
   }

   @Override
   public Enumeration<URL> findResources(String name) {
      Set<URL> combinedResources = null;
      Set<URL> foundResources = findLocalResources(name);
      if (foundResources != null && foundResources.size() > 0) {
         if (combinedResources == null) {
            combinedResources = new LinkedHashSet<>();
         }
         combinedResources.addAll(foundResources);
      }
      if (combinedResources != null) {
         return Collections.enumeration(combinedResources);
      }
      return Collections.emptyEnumeration();
   }

   private Class<?> findLocalClassImpl(String className, boolean resolve) throws ClassNotFoundException {
      return getLoadedClass(className, resolve);
   }

   private void definePackageForClass(String className) {
      int i = className.lastIndexOf('.');
      if (i != -1) {
         String pkgname = className.substring(0, i);
         // define the package if it is not already defined
         Package pkg = getPackage(pkgname);
         if (pkg == null) {
            definePackage(pkgname, null, null, null, null, null, null, null);
         }
      }
   }

   private Class<?> getLoadedClass(String className, boolean resolve) throws ClassNotFoundException {
      synchronized (getClassLoadingLock(className)) {

         Class<?> loadedClass = findLoadedClass(className);
         if (classes.containsKey(className)) {
            return classes.get(className);
         }
         if (byteCache.containsKey(className)) {
            definePackageForClass(className);
            byte[] classBytes = byteCache.get(className);

            if (loadedClass == null) {
               try {
                  loadedClass = defineClass(className, classBytes, 0, classBytes.length, this.getClass().getProtectionDomain());
               } catch (NoClassDefFoundError | IncompatibleClassChangeError e) {
                  throw new ClassNotFoundException(className, e);
               }
            }
            classes.put(className, loadedClass);
            if (resolve) {
               resolveClass(loadedClass);
            }
            return loadedClass;
         } else {
            return null;
         }
      }
   }
}
