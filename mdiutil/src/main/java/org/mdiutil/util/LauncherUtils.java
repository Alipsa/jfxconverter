/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.mdiutil.lang.SystemUtils;

/**
 * This class has only one static method which allows to return the launch arguments of a
 * main static method into a Map of &lt;key,value&gt;.
 *
 * @version 1.2.36
 */
public class LauncherUtils {
   private LauncherUtils() {
   }

   /**
    * Return the launch arguments of a main static method into a Map of &lt;key,value&gt;.
    * The arguments can have one of the following patterns:
    * <pre>
    * key1 prop1 key2 prop2 ...
    * </pre>
    * or
    * <pre>
    * key1=prop1 key2=prop2 ...
    * </pre>
    * it is even possible to mix the two patterns such as:
    * <pre>
    * key1 prop1 key2=prop2 ...
    * </pre>
    *
    * if the value for the key is not set, then a (key, value) will be added with an empty String as value
    * <pre>
    * key2= -key3 -key4=value4
    * </pre>
    *
    * @param args the launch arguments
    * @return the key, value pairs corresponding to the arguments
    */
   public static Map<String, String> getLaunchProperties(String[] args) {
      Map<String, String> props = new HashMap();
      if ((args != null) && (args.length != 0)) {
         int i = 0;
         boolean toStore = false;
         String propKey = null;
         while (i < args.length) {
            String arg = args[i];
            if (arg == null) {
               i++;
               continue;
            }
            if (toStore) {
               if (arg.charAt(0) != '-') {
                  toStore = false;
                  String propValue = arg;
                  props.put(propKey, propValue);
                  propKey = null;
               } else {
                  toStore = false;
                  props.put(propKey, "");
                  propKey = null;
                  if (arg.indexOf('=') != -1) {
                     String intKey = arg.substring(0, arg.indexOf('='));
                     if (intKey.startsWith("-")) {
                        intKey = intKey.substring(1);
                     }
                     String propValue = arg.substring(arg.indexOf('=') + 1);
                     props.put(intKey, propValue);
                  } else if (arg.charAt(0) == '-') {
                     propKey = arg.substring(1);
                     toStore = true;
                  }
               }
            } else if (arg.indexOf('=') != -1) {
               if (toStore) {
                  toStore = false;
                  props.put(propKey, "");
                  propKey = null;
               }
               String intKey = arg.substring(0, arg.indexOf('='));
               if (intKey.startsWith("-")) {
                  intKey = intKey.substring(1);
               }
               String propValue = arg.substring(arg.indexOf('=') + 1);
               props.put(intKey, propValue);
            } else {
               if (arg.charAt(0) == '-') {
                  propKey = arg.substring(1);
                  toStore = true;
               } else {
                  char c = arg.charAt(0);
                  if (c < '0' || c > '9') {
                     propKey = arg;
                     toStore = true;
                  }
               }
            }
            i++;
         }
         if (propKey != null) {
            props.put(propKey, "");
         }
      }
      return props;
   }

   /**
    * Return the parent directory of the code which contains a class. For example:
    * <ul>
    * <li>If the class is not in a jar file, it is equivalent to getting the File with the
    * <code>System.getProperty("user.dir")</code> path</li>
    * <li>If the class is in a jar file, it will return the directory in which is the jar file</li>
    * <li>If the class is in a remote file accessed by a http or https protocl, it will return the remote parent directory
    * of the class</li>
    * </ul>
    *
    * @param clazz the class
    * @return the parent directory
    */
   public static File getUserDir(Class clazz) {
      boolean isUnix = SystemUtils.isUnixPlatform();
      URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
      String urlAsText = replaceEscapedSequences(url.getFile());
      File userDirFile;
      if (!urlAsText.endsWith(".jar")) {
         userDirFile = new File(System.getProperty("user.dir"));
      } else if (urlAsText.startsWith("file:")) {
         userDirFile = new File(getUrlPath(urlAsText, 5)).getParentFile();
      } else if (urlAsText.startsWith("http:")) {
         userDirFile = new File(urlAsText.substring(5)).getParentFile();
      } else if (urlAsText.startsWith("https:")) {
         userDirFile = new File(urlAsText.substring(5)).getParentFile();
      } else if (urlAsText.startsWith("/") && !isUnix) {
         userDirFile = new File(urlAsText.substring(1)).getParentFile();
      } else {
         userDirFile = new File(getUrlPath(urlAsText, 0)).getParentFile();
      }
      if (isUnix) {
         String userDirPath = userDirFile.getAbsolutePath();
         if (!userDirPath.startsWith("/")) {
            userDirPath = "/" + userDirPath;
            userDirFile = new File(userDirPath);
         }
      }
      return userDirFile;
   }

   private static String getUrlPath(String urlAsText, int offset) {
      urlAsText = urlAsText.substring(offset);
      if (SystemUtils.isUnixPlatform()) {
         if (!urlAsText.startsWith("/")) {
            return "/" + urlAsText;
         } else {
            return urlAsText;
         }
      } else {
         return urlAsText;
      }
   }

   /**
    * Replace the percent-encoding escape sequences in a URL path by their equivalent characters.
    */
   private static String replaceEscapedSequences(String path) {
      if (!path.contains("%")) {
         return path;
      } else {
         StringBuilder buf = new StringBuilder();
         int offset = 0;
         while (true) {
            int index = path.indexOf('%', offset);
            if (index != -1) {
               buf.append(path.substring(offset, index));
               char c = (char) (int) Integer.decode("0x" + path.substring(index + 1, index + 3));
               buf.append(c);
               offset = index + 3;
            } else {
               buf.append(path.substring(offset));
               break;
            }
         }
         return buf.toString();
      }
   }
}
