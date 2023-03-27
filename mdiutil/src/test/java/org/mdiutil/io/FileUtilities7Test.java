/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2018, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.SystemUtils;

/**
 * Unit tests for FileUtilities.
 *
 * @version 1.2.8
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities7Test {

   /**
    * Test of getDrive method, of class FileUtilities.
    */
   @Test
   public void testGetDrive() throws IOException {
      System.out.println("FileUtilities7Test : testGetDrive");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core>.html#getValue--");
      File file = FileUtilities.getFile(url);
      String drive = FileUtilities.getDrive(file);
      assertNull("Drive must be null", drive);
   }

   /**
    * Test of getDrive method, of class FileUtilities.
    */
   @Test
   public void testGetDrive2() throws IOException {
      System.out.println("FileUtilities7Test : testGetDrive2");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core/myFile.html");
      File file = FileUtilities.getFile(url);
      String drive = FileUtilities.getDrive(file);
      if (SystemUtils.isWindowsPlatform())
         assertEquals("Drive must be L", "L:\\", drive);
   }

   /**
    * Test of isAbsolute method, of class FileUtilities.
    */
   @Test
   public void testIsAbsolute() throws IOException {
      System.out.println("FileUtilities7Test : testIsAbsolute");
      String path = "model/core/myFile.html";
      boolean isAbsolute = FileUtilities.isAbsolute(path);
      assertFalse("path must be relative", isAbsolute);
   }

   /**
    * Test of isAbsolute method, of class FileUtilities.
    */
   @Test
   public void testIsAbsolute2() throws IOException {
      System.out.println("FileUtilities7Test : testIsAbsolute2");
      String path = "L:/WRK/Java/toto/model/core/myFile.html";
      boolean isAbsolute = FileUtilities.isAbsolute(path);
      if (SystemUtils.isWindowsPlatform())
         assertTrue("path must be absolute", isAbsolute);
   }

   /**
    * Test of getRelativePath method, of class FileUtilities.
    */
   @Test
   public void testGetRelativePath() throws IOException {
      System.out.println("FileUtilities7Test : testGetRelativePath");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core/myFile.html");
      URL baseURL = new URL("file:/L:/WRK/Java/toto");
      String relativePath = FileUtilities.getRelativePath(baseURL, url);
      assertEquals("RelativePath must be model/core/myFile.html", "model/core/myFile.html", relativePath);
   }

   /**
    * Test of getRelativePath method, of class FileUtilities.
    */
   @Test
   public void testGetRelativePath2() throws IOException {
      System.out.println("FileUtilities7Test : testGetRelativePath2");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core>.html#getValue--");
      URL baseURL = new URL("file:/L:/WRK/Java/toto");
      String relativePath = FileUtilities.getRelativePath(baseURL, url);
      assertEquals("RelativePath must be /L:/WRK/Java/toto/model/core>.html", "/L:/WRK/Java/toto/model/core>.html", relativePath);
   }

   /**
    * Test of getRelativePath method, of class FileUtilities.
    */
   @Test
   public void testGetRelativePath3() throws IOException {
      System.out.println("FileUtilities7Test : testGetRelativePath3");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core/myFile.html");
      URL baseURL = new URL("file:/L:/WRK/Java/toto/");
      String relativePath = FileUtilities.getRelativePath(baseURL, url);
      assertEquals("RelativePath must be model/core/myFile.html", "model/core/myFile.html", relativePath);
   }

   /**
    * Test of normalize method, of class FileUtilities.
    */
   @Test
   public void testNormalize() throws IOException {
      System.out.println("FileUtilities7Test : testNormalize");
      URL url = new URL("file:/L:/WRK/Java/toto/model/core/myFile.html");
      URL newURL = FileUtilities.normalize(url);
      assertEquals("Normalize test", "file:/L:/WRK/Java/toto/model/core/myFile.html", newURL.toString());
   }

   /**
    * Test of normalize method, of class FileUtilities.
    */
   @Test
   public void testNormalize2() throws IOException {
      System.out.println("FileUtilities7Test : testNormalize2");
      URL url = new URL("file:/L:/WRK/Java/toto/model/../core/myFile.html");
      URL newURL = FileUtilities.normalize(url);
      assertEquals("Normalize test", "file:/L:/WRK/Java/toto/core/myFile.html", newURL.toString());
   }

   /**
    * Test of normalize method, of class FileUtilities.
    */
   @Test
   public void testNormalize3() throws IOException {
      System.out.println("FileUtilities7Test : testNormalize3");
      URL url = new URL("file:/L:/WRK/Java/toto/model/./core/myFile.html");
      URL newURL = FileUtilities.normalize(url);
      assertEquals("Normalize test", "file:/L:/WRK/Java/toto/model/core/myFile.html", newURL.toString());
   }

   /**
    * Test of normalize method, of class FileUtilities.
    */
   @Test
   public void testNormalize4() throws IOException {
      System.out.println("FileUtilities7Test : testNormalize4");
      URL url = new URL("file:/L:/WRK/Java/toto/model/./core/myFile.html#anchor");
      URL newURL = FileUtilities.normalize(url);
      assertEquals("Normalize test", "file:/L:/WRK/Java/toto/model/core/myFile.html#anchor", newURL.toString());
   }
}
