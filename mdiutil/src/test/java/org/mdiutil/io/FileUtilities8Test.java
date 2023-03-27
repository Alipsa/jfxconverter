/*------------------------------------------------------------------------------
 * Copyright (C) 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * @version 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities8Test {

   /**
    * Test of GetAbsoluteURL method, of class FileUtilities.
    */
   @Test
   public void testGetAbsoluteURL() throws IOException {
      System.out.println("FileUtilities8Test : testGetAbsoluteURL");
      URL parentURL = new URL("file:/L:/WRK//toto/test/resources");
      URL url = FileUtilities.getAbsoluteURL(parentURL, "myFile.xml");
      URL expected = new URL("file:/L:/WRK//toto/test/resources/myFile.xml");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of GetAbsoluteURL method, of class FileUtilities.
    */
   @Test
   public void testGetAbsoluteURL2() throws IOException {
      if (!SystemUtils.isWindowsPlatform()) {
         return;
      }
      System.out.println("FileUtilities8Test : testGetAbsoluteURL2");
      URL parentURL = new URL("file:/L:/WRK/toto/test/resources");
      URL url = FileUtilities.getAbsoluteURL(parentURL, "L:/WRK/toto/test/resources/myFile.xml");
      URL expected = new URL("file:/L:/WRK/toto/test/resources/myFile.xml");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of getAbsolutePath method, of class FileUtilities.
    */
   @Test
   public void testGetAbsolutePath() throws IOException {
      System.out.println("FileUtilities8Test : testGetAbsolutePath");
      URL parentURL = new URL("file:/L:/WRK//toto/test/resources");
      String path = FileUtilities.getAbsolutePath(parentURL, "myFile.xml");
      String expected = "L:\\WRK\\toto\\test\\resources\\myFile.xml".replace('\\', File.separatorChar);
      assertEquals("Expected path", expected, path);
   }

   /**
    * Test of getAbsolutePath method, of class FileUtilities.
    */
   @Test
   public void testGetAbsolutePath2() throws IOException {
      System.out.println("FileUtilities8Test : testGetAbsolutePath2");
      if (!SystemUtils.isWindowsPlatform()) {
         System.out.println("Only works on windows, skipping");
         return;
      }
      URL parentURL = new URL("file:/L:/WRK//toto/test/resources");
      String path = FileUtilities.getAbsolutePath(parentURL, "L:/WRK/toto/test/resources/myFile.xml");
      String expected = "L:\\WRK\\toto\\test\\resources\\myFile.xml";
      assertEquals("Expected path", path, expected);
   }

   /**
    * Test of getAbsolutePath method, of class FileUtilities.
    */
   @Test
   public void testGetAbsolutePath3() throws IOException {
      System.out.println("FileUtilities8Test : testGetAbsolutePath3");
      if (!SystemUtils.isWindowsPlatform()) {
         System.out.println("Only works on windows, skipping");
         return;
      }
      URL parentURL = new URL("file:/L:/WRK//my/files");
      String path = FileUtilities.getAbsolutePath(parentURL, "L:/WRK/toto/test/resources/myFile.xml");
      String expected = "L:\\WRK\\toto\\test\\resources\\myFile.xml";
      assertEquals("Expected path", path, expected);
   }

   /**
    * Test of getChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetFile1() throws IOException {
      System.out.println("FileUtilities8Test : testGetFile1");
      URL url = new URL("file:/L:/WRK/toto 1/test/");
      url = FileUtilities.getChildURL(url, "myFile.txt");
      URL expected = new URL("file:/L:/WRK/toto%201/test/myFile.txt");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of getChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetFile2() throws IOException {
      System.out.println("FileUtilities8Test : testGetFile2");
      URL url = new URL("file:/L:/WRK/toto 1/test/");
      url = FileUtilities.getChildURL(url, "myFile.txt", false, false);
      URL expected = new URL("file:/L:/WRK/toto 1/test/myFile.txt");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of getChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetFile3() throws IOException {
      System.out.println("FileUtilities8Test : testGetFile3");
      URL url = new URL("file:/L:/WRK/toto 1/test/");
      url = FileUtilities.getChildURL(url, "myFile 1.txt");
      URL expected = new URL("file:/L:/WRK/toto%201/test/myFile%201.txt");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of getChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetFile4() throws IOException {
      System.out.println("FileUtilities8Test : testGetFile4");
      URL url = new URL("file:/L:/WRK/toto 1/test/");
      url = FileUtilities.getChildURL(url, "myFile 1.txt", false, false);
      URL expected = new URL("file:/L:/WRK/toto 1/test/myFile 1.txt");
      assertEquals("Expected URL", expected, url);
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor1() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor1");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      assertTrue("must be an ancestor", FileUtilities.isAncestor(dir, file));
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor2() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor2");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("D:/WRK/Java/test/resources/MyFile.xml");
      assertFalse("must not be an ancestor", FileUtilities.isAncestor(dir, file));
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor3() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor3");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/MyFile.xml");
      assertFalse("must not be an ancestor", FileUtilities.isAncestor(dir, file));
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor4() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor4");
      File dir = new File("L:/WRK/Java/test/resources");
      URL ancestor = dir.toURI().toURL();
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL url = file.toURI().toURL();
      assertTrue("must be an ancestor", FileUtilities.isAncestor(ancestor, url));
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor5() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor5");
      File dir = new File("L:/WRK/Java/test/resources");
      URL ancestor = dir.toURI().toURL();
      File file = new File("D:/WRK/Java/test/resources/MyFile.xml");
      URL url = file.toURI().toURL();
      assertFalse("must not be an ancestor", FileUtilities.isAncestor(ancestor, url));
   }

   /**
    * Test of isAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor6() throws IOException {
      System.out.println("FileUtilities8Test : testIsAncestor6");
      File dir = new File("L:/WRK/Java/test/resources");
      URL ancestor = dir.toURI().toURL();
      File file = new File("L:/WRK/Java/test/MyFile.xml");
      URL url = file.toURI().toURL();
      assertFalse("must not be an ancestor", FileUtilities.isAncestor(ancestor, url));
   }
}
