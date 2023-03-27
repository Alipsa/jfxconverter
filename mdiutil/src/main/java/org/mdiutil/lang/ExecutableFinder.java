/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows to get a list of executables from a list of roots. Note that in all case only executables
 * which are valid for the System architecture will be added to the list:
 * <ul>
 * <li>On Windows: Portable Executables</li>
 * <li>On Unix and Linux: ELF Executables</li>
 * <li>On Mach OS X: Mach-O Executables</li>
 * </ul>
 *
 * Also on a 32 bit platform, 64 bits executables will not be added to the list.
 *
 * <h1>Usage</h1>
 * <ul>
 * <li>Add the list of roots which will be used to look for the executables</li>
 * <li>Get the list of executables, defining a directory path pattern and a file path pattern. The directory
 * pattern defines the directories which will be searched for under the roots. The file pattern defines the
 * file which will be searched for under these directories</li>
 * </ul>
 *
 * The two patterns can use wildcards and simple regular expressions. For example:
 * <ul>
 * <li><code>getExecutables("netbeans*", "bin/netbeans*.exe</code> will look for directories whose name begins
 * with "netbeans" (for example <code>netbeans 8.2</code> would be a valid name), and files whose name begins with
 * "netbeans" and with an ".exe" extension under these directories.
 * </ul>
 *
 * <h1>Examples of usage</h1>
 * <h1>Look for the Netbeans executable on the Windows Program Files directory</h1>
 * Suppose that we want to get the Netbeans executables on Windows. If we are on 64 bits, we can do:
 * <pre>
 * ExecutableFinder finder = new ExecutableFinder(false);
 * finder.addRootFromEnv("programfiles");
 * List&lt;File&gt; files = finder.getExecutables("netbeans*", "bin/*.exe");
 * </pre>
 * If we have only one Netbeans installation, the method will return two files:
 * <ul>
 * <li>The <code>Netbeans.exe</code> executable</li>
 * <li>The <code>Netbeans64.exe</code> executable</li>
 * </ul>
 *
 * Now if we are on 32 bits, we will have only one file in the list:
 * <ul>
 * <li>The <code>Netbeans.exe</code> executable</li>
 * </ul>
 *
 * And if we are on 64 bits, but we set the finder to be strict, we will also have only one file in the list:
 * <ul>
 * <li>The <code>Netbeans64.exe</code> executable</li>
 * </ul>
 *
 * <h1>Look for the Python executable</h1>
 * We will look for the python executable anywhere on a top-level directory.
 * <pre>
 * ExecutableFinder finder = new ExecutableFinder(true);
 * finder.addAllRoots();
 * List&lt;File&gt; files = finder.getExecutables("python*", "*.exe");
 * </pre>
 *
 * @version 0.9.51
 */
public class ExecutableFinder {
   private List<FileRoot> roots = new ArrayList<>();
   private boolean isStrict;
   private boolean isWindows;
   private boolean caseInsensitive = true;
   /**
    * Represents the mode where a root maybe either the parent of a directory containing the executable, or this
    * directory itself.
    */
   public static final short MODE_ALL = 0;
   /**
    * Represents the mode where a root may only be the parent of a directory containing the executable.
    */
   public static final short MODE_ROOT = 1;
   /**
    * Represents the mode where a root may only be a directory containing the executable.
    */
   public static final short MODE_PATTERN = 2;

   /**
    * Constructor.
    *
    * @param isStrict true if the finder must find only executables which are defined for the JVM architecture
    */
   public ExecutableFinder(boolean isStrict) {
      this.isStrict = isStrict;
      isWindows = SystemUtils.isWindowsPlatform();
   }

   /**
    * Add a root directory for the finder.
    *
    * @param root the root directory
    * @return true if the root directory exists and is a directory
    */
   public boolean addRoot(File root) {
      return addRoot(root, MODE_ROOT);
   }

   /**
    * Add a root directory for the finder.
    *
    * @param root the root directory
    * @param mode the mode
    * @return true if the root directory exists and is a directory
    */
   private boolean addRoot(File root, short mode) {
      if (root.exists() && root.isDirectory()) {
         roots.add(new FileRoot(root, mode));
         return true;
      } else {
         return false;
      }
   }

   /**
    * Add all the file system root directories for the finder.
    */
   public void addAllRoots() {
      FileSystem fileSystems = FileSystems.getDefault();
      Iterator<Path> it = fileSystems.getRootDirectories().iterator();
      while (it.hasNext()) {
         Path path = it.next();
         File root = path.toFile();
         addRoot(root);
      }
   }

   /**
    * Set the finder as case insensitive. Note that by default, the finder is case sensitive, except on Windows where
    * the finder will be case insensitive regardless of the value of this property.
    *
    * @param caseInsensitive true if the finder must be caseInsensitive
    */
   public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   /**
    * Return true if the finder is case insensitive.
    *
    * @return true if the finder is case insensitive
    */
   public boolean isCaseInsensitive() {
      return caseInsensitive;
   }

   /**
    * Add a root directory or several root directories from an environment variable, with the
    * {@link #MODE_ALL} mode.
    *
    * @param env the environment variable
    * @return true if the environment variable exists, and the one or several associated root directories exist
    */
   public boolean addRootFromEnv(String env) {
      return addRootFromEnv(env, MODE_ALL);
   }

   /**
    * Add a root directory or several root directories from an environment variable. The possible modes are:
    * <ul>
    * <li>{@link #MODE_ALL}: Represents the mode where a root maybe either the parent of a directory contining the executable, or this
    * directory itself</li>
    * <li>{@link #MODE_ROOT}: Represents the mode where a root may only be the parent of a directory containing the executable</li>
    * <li>{@link #MODE_PATTERN}: Represents the mode where a root may only be a directory containing the executable</li>
    * </ul>
    *
    * @param env the environment variable
    * @param mode the mode
    * @return true if the environment variable exists, and the one or several associated root directories exist
    */
   public boolean addRootFromEnv(String env, short mode) {
      String pathValue = System.getenv(env);
      if (pathValue != null) {
         char sep = isWindows ? ';' : ':';
         if (pathValue.indexOf(sep) != -1) {
            StringTokenizer tok = new StringTokenizer(pathValue, Character.toString(sep));
            boolean isOK = true;
            while (tok.hasMoreTokens()) {
               String tk = tok.nextToken().trim();
               File root = new File(tk);
               boolean result = addRoot(root, mode);
               if (!result && !root.exists()) {
                  isOK = false;
               }
            }
            return isOK;
         } else {
            File root = new File(pathValue);
            return addRoot(root, mode);
         }
      } else {
         return false;
      }
   }

   /**
    * Return a list of executables from a directory Pattern and file pattern.
    *
    * The two patterns can use wildcards and simple regular expressions. For example:
    * <ul>
    * <li><code>getExecutables("netbeans*", "bin/netbeans*.exe</code> will look for directories whose name begins
    * with "netbeans" (for example <code>netbeans 8.2</code> would be a valid name), and files whose name begins with
    * "netbeans" and with an ".exe" extension under these directories.
    * </ul>
    *
    * @param dirPattern the directory Pattern
    * @param filePattern the file pattern
    * @return the list of executables
    */
   public List<File> getExecutables(String dirPattern, String filePattern) {
      Set<File> filesSet = new HashSet<>();
      Pattern dirPat = createPattern(dirPattern);
      String subDirName = null;
      if (filePattern.lastIndexOf('/') != -1) {
         int idx = filePattern.lastIndexOf('/');
         String _filePattern = filePattern.substring(idx + 1);
         if (idx != 0) {
            subDirName = filePattern.substring(0, idx);
         }
         filePattern = _filePattern;
      } else if (filePattern.lastIndexOf('\\') != -1) {
         int idx = filePattern.lastIndexOf('\\');
         String _filePattern = filePattern.substring(idx + 1);
         if (idx != 0) {
            subDirName = filePattern.substring(0, idx);
         }
         filePattern = _filePattern;
      }
      List<File> list = new ArrayList<>();
      DirectoryFilter dirfilter = new DirectoryFilter(dirPat, false);
      Pattern filePat = createPattern(filePattern);
      FileFilter filter = new DirectoryFilter(filePat, true);
      for (int j = 0; j < roots.size(); j++) {
         FileRoot root = roots.get(j);
         if (root.mode == MODE_ALL) {
            if (dirfilter.accept(root.file)) {
               exploreRoot(root.file, null, filter, subDirName, list, filesSet);
            } else {
               exploreRoot(root.file, dirfilter, filter, subDirName, list, filesSet);
            }
         } else if (root.mode == MODE_ROOT) {
            exploreRoot(root.file, dirfilter, filter, subDirName, list, filesSet);
         } else if (root.mode == MODE_PATTERN && dirfilter.accept(root.file)) {
            exploreRoot(root.file, null, filter, subDirName, list, filesSet);
         }
      }
      return list;
   }

   private void exploreRoot(File root, DirectoryFilter dirfilter, FileFilter filter, String subDirName,
      List<File> list, Set<File> filesSet) {
      File[] dirs;
      if (dirfilter != null) {
         dirs = root.listFiles(dirfilter);
      } else {
         dirs = new File[1];
         dirs[0] = root;
      }
      if (dirs != null && dirs.length != 0) {
         for (int i = 0; i < dirs.length; i++) {
            File dir = dirs[i];
            if (subDirName != null) {
               File subDir = new File(dir, subDirName);
               if (subDir.exists() && subDir.isDirectory()) {
                  exploreDirectory(subDir, list, filesSet, filter);
               }
            } else {
               exploreDirectory(dir, list, filesSet, filter);
            }
         }
      }
   }

   private void exploreDirectory(File dir, List<File> list, Set<File> filesSet, FileFilter filter) {
      File[] files = dir.listFiles(filter);
      if (files != null && files.length != 0) {
         for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && file.exists()) {
               if (SystemUtils.isCompatible(file, isStrict)) {
                  if (!filesSet.contains(file)) {
                     list.add(file);
                     filesSet.add(file);
                  }
               }
            }
         }
      }
   }

   private Pattern createPattern(String patS) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < patS.length(); i++) {
         if (patS.charAt(i) == '*') {
            buf.append(".*");
         } else if (patS.charAt(i) == '.') {
            buf.append("\\.");
         } else if (patS.charAt(i) == '(') {
            buf.append("\\(");
         } else if (patS.charAt(i) == ')') {
            buf.append("\\)");
         } else {
            buf.append(patS.charAt(i));
         }
      }
      Pattern pat;
      if (isWindows || caseInsensitive) {
         pat = Pattern.compile(buf.toString(), Pattern.CASE_INSENSITIVE);
      } else {
         pat = Pattern.compile(buf.toString());
      }
      return pat;
   }

   private class DirectoryFilter implements FileFilter {
      private Pattern pat = null;
      private boolean isFile = false;

      private DirectoryFilter(Pattern pat, boolean isFile) {
         this.pat = pat;
         this.isFile = isFile;
      }

      @Override
      public boolean accept(File file) {
         if ((isFile && file.isDirectory()) || (!isFile && !file.isDirectory())) {
            return false;
         } else {
            Matcher m = pat.matcher(file.getName());
            return m.matches();
         }
      }
   }

   private static class FileRoot {
      private File file = null;
      private short mode = MODE_ALL;

      private FileRoot(File file) {
         this.file = file;
      }

      private FileRoot(File file, short mode) {
         this.file = file;
         this.mode = mode;
      }
   }
}
