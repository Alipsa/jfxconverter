/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.util.List;

/**
 * This class tests the ExecutableFinder class. Note that it is difficult to create real JUnit tests here.
 *
 * @since 0.9.17
 */
public class TestExecutableFinder {
   public static void main(String[] args) {
      ExecutableFinder finder = new ExecutableFinder(true);
      finder.addRootFromEnv("programfiles");
      List<File> files = finder.getExecutables("netbeans*8*", "bin/*.exe");
      System.out.println(files.size());

      files = finder.getExecutables("netbeans*", "bin/*.exe");
      System.out.println(files.size());

      finder = new ExecutableFinder(true);
      finder.addRoot(new File("C:"));
      files = finder.getExecutables("python*", "*.exe");
      System.out.println(files.size());

      finder = new ExecutableFinder(true);
      finder.addAllRoots();
      files = finder.getExecutables("python*", "pythonw.exe");
      System.out.println(files.size());

      finder = new ExecutableFinder(true);
      finder.addAllRoots();
      finder.addRoot(new File("C:"));
      files = finder.getExecutables("python*", "*.exe");
      System.out.println(files.size());
   }
}
