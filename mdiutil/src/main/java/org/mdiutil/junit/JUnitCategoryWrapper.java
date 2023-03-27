/*
 MODIFIED BSD LICENSE
 ***** BEGIN LICENSE BLOCK *****

 Copyright (c) 2014 Dassault Aviation
 Copyright (c) 2014, 2016, 2018, 2019 Hervé Girod
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the <organization>.
 4. Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 ***** END LICENSE BLOCK *****
 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on http://j661.sourceforge.net
 */
package org.mdiutil.junit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import org.junit.runners.model.FrameworkMethod;

/**
 * A wrapper which wrap around Unit Test categories. This class will parse the JUnit tests resource file which define the categories which will be
 * allowed to run, and will return the Set of test categories allowed to run.
 *
 * The file used to condigure the categories is the <i>categories.properties</i> file at the root of the Unit tests.
 *
 * <h1>File structure</h1>
 * The <i>categories.properties</i> file is a properties file with (key, value) pairs:
 * <ul>
 * <li>The key is the category name</li>
 * <li>The value is true or false: true indicates that the tests of the specified category should run, false that they should not run</li>
 * </ul>
 * Note that if no values are defined, all Unit tests will be run.
 *
 * <h2>strictMode</h2>
 * If the "strictMode" value is true, only tests which belong to all true categories will be executed. For example:
 * <pre>
 * strictMode=true
 * editor=true
 * server=true
 * client=
 * </pre>
 * Only Unit Tests annotated by both "editor" and "server" will run.
 *
 * <h2>showSkippedClasses</h2>
 * If the "showSkippedClasses" value is true, tests which are not executed will be shown as ignored. For example:
 * <pre>
 * showSkippedClasses=true
 * editor=true
 * server=
 * client=
 * </pre>
 *
 * <h1>Examples</h1>
 * <pre>
 * editor=
 * server=
 * client=
 * </pre>
 * All Unit Tests will run.
 *
 * <pre>
 * editor=true
 * server=false
 * client=false
 * </pre>
 * Only "editor" Unit Tests will run.
 *
 * <pre>
 * editor=true
 * server=true
 * client=
 * </pre>
 * Only "editor" or "server" Unit Tests will run.
 *
 * <pre>
 * editor=
 * server=
 * client=false
 * </pre>
 * All Unit Tests except "client" Unit tests will run.
 *
 * <pre>
 * strictMode=true
 * editor=true
 * server=true
 * client=
 * </pre>
 * Only Unit Tests annotated by both "editor" and "server" will run.
 *
 * <h1>Discovering the categories file</h1>
 * The properties file which contains the categories must be in the following package:
 * <code>org.mdiutils.junit.resources</code> and its name must be <code>categories.properties</code>.
 *
 * @version 1.2.2
 */
public class JUnitCategoryWrapper {
   private static final String STRICT_MODE_KEY = "strictMode";
   private static final String SHOW_SKIPPED_CLASSES_KEY = "showSkippedClasses";
   private static final String DEFAULT_STATE_KEY = "defaultState";
   /**
    * The name of the categories environment variable.
    */
   public static final String ENV_CATEGORIES = "MDIUTILITIES_JUNIT_ENV";
   private static final String ENV_CATEGORIES_ERROR = "JUNIT_CATEGORIES_ERROR";
   private static final String PACKAGE = "org/mdiutil/junit/resources/";
   private static JUnitCategoryWrapper wrapper = null;
   private final Set<String> categories = new HashSet<>();
   private final Set<String> excludedCategories = new HashSet<>();
   private boolean isStrict = false;
   private boolean showSkippedClasses = true;
   private final ClassLoader loader = Thread.currentThread().getContextClassLoader();
   public static final List<FrameworkMethod> EMPTY_LIST = new ArrayList<>();

   private JUnitCategoryWrapper() {
   }

   public boolean isShowingSkippedClasses() {
      return showSkippedClasses;
   }

   public boolean isStrict() {
      return isStrict;
   }

   public Set<String> getCategories() {
      return categories;
   }

   public Set<String> getExcludedCategories() {
      return excludedCategories;
   }

   /**
    * Return the JUnit tests categories which will be allowed to run.
    *
    * @return the JUnit tests categories which will be allowed to run
    */
   public static JUnitCategoryWrapper getInstance() {
      if (wrapper == null) {
         wrapper = new JUnitCategoryWrapper();
         String envValue = System.getProperty(ENV_CATEGORIES);
         wrapper.configureCategories(envValue);
      }
      return wrapper;
   }

   /**
    * Parse the JUnit tests resource file which define the categories which will be allowed to run.
    *
    * @return true if the configuration properties file could be found
    */
   private boolean configureCategories(String envValue) {
      if (envValue == null) {
         return defineCategories();
      } else {
         return retrieveCategories(envValue);
      }
   }

   /**
    * Parse the JUnit tests resource file which define the categories which will be allowed to run.
    *
    * @return true if the configuration properties file could be found
    */
   private boolean retrieveCategories(String envValue) {
      categories.clear();
      excludedCategories.clear();

      if (envValue.equals(ENV_CATEGORIES_ERROR)) {
         return false;
      } else {
         getEnv(envValue);
         return true;
      }
   }

   private void getEnv(String envValue) {
      StringTokenizer tok = new StringTokenizer(envValue, ";");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         int index = tk.indexOf('=');
         if (index > 0 && index < tk.length() - 1) {
            String cat = tk.substring(0, index);
            String value = tk.substring(index + 1);
            if (value.equals("true")) {
               categories.add(cat);
            } else {
               excludedCategories.add(cat);
            }
         }
      }
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * @param url the URL
    * @return true if the file or the resource denoted by this URL exist
    */
   private boolean exist(URL url) {
      if (url == null) {
         return false;
      } else {
         try {
            InputStream stream = url.openStream();
            stream.close();
            return true;
         } catch (IOException e) {
            return false;
         }
      }
   }

   private URL getURL(String pack, String path) {
      return loader.getResource(pack + "/" + path);
   }

   /**
    * Return the PropertyResourceBundle looking for the <code>bundle</code> in the resource package or URL.
    * This method will use the default ClassLoader.
    *
    * @param bundle the bundle
    * @return the PropertyResourceBundle
    */
   private PropertyResourceBundle getPropertyResourceBundle(String bundle) {
      PropertyResourceBundle prb = null;
      try {
         prb = new PropertyResourceBundle(getURL(PACKAGE, bundle).openStream());
      } catch (MalformedURLException e) {
         System.out.println(e);
      } catch (IOException e) {
         System.out.println(e);
      }
      return prb;
   }

   /**
    * Parse the JUnit tests resource file which define the categories which will be allowed to run.
    *
    * @return true if the configuration properties file could be found
    */
   private boolean defineCategories() {
      categories.clear();
      excludedCategories.clear();

      URL url = getURL(PACKAGE, "categories.properties");
      // we accept that the URL "categories.properties" does not exist, in this case all tests will run
      if (exist(url)) {
         PropertyResourceBundle prb = getPropertyResourceBundle("categories.properties");
         Enumeration<String> keys = prb.getKeys();
         while (keys.hasMoreElements()) {
            String cat = keys.nextElement();
            String s = prb.getString(cat);
            if (s != null) {
               s = s.trim();
               if (cat.equalsIgnoreCase(SHOW_SKIPPED_CLASSES_KEY)) {
                  if (s.equals("false")) {
                     showSkippedClasses = false;
                  }
               } else if (s.equals("true")) {
                  if (cat.equalsIgnoreCase(STRICT_MODE_KEY)) {
                     isStrict = true;
                  } else {
                     categories.add(cat);
                  }
               } else if (s.equals("false") && !cat.equalsIgnoreCase(STRICT_MODE_KEY)) {
                  excludedCategories.add(cat);
               }
            }
         }
         setEnv();
         return true;
      } else {
         System.err.println("Could not find org/mdiutil/junit/resources/categories.properties, all tests will run");
         System.setProperty(ENV_CATEGORIES, ENV_CATEGORIES_ERROR);
         return false;
      }
   }

   private void setEnv() {
      StringBuilder buf = new StringBuilder();
      Iterator<String> it = categories.iterator();
      while (it.hasNext()) {
         String cat = it.next();
         buf.append(cat).append("=true;");
      }
      it = excludedCategories.iterator();
      while (it.hasNext()) {
         String cat = it.next();
         buf.append(cat).append("=false;");
      }
   }
}
