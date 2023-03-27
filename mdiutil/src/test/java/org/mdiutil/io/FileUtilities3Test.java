/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.ResourceLoader;
import org.mdiutil.lang.SystemUtils;

/**
 * Unit tests for FileUtilities.
 *
 * @version 1.2.4
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities3Test {

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   public void testGetChildren2() {
      System.out.println("FileUtilities3Test : testGetChildren2");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      List<URL> children = FileUtilities.getChildren(url, true);
      assertNotNull("Must have no child", children);
      assertTrue("Must have no child", children.isEmpty());
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath");
      File dir = new File("L:/WRK/Java/test/");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url);
      assertEquals("Relative path", "resources/MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath2() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath2");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url);
      assertEquals("Relative path", "MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath3() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath3");
      if (!SystemUtils.isWindowsPlatform()) {
         System.out.println("Only works on windows, skipping");
         return;
      }
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("D:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url);
      assertEquals("Relative path", "/D:/WRK/Java/test/resources/MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath4() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath4");
      if (SystemUtils.isWindowsPlatform()) {
         File dir = new File("L:/WRK/Java/test1/resources");
         File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
         URL baseDir = dir.toURI().toURL();
         URL url = file.toURI().toURL();

         String path = FileUtilities.getRelativePath(baseDir, url);
         assertEquals("Relative path", "L:/WRK/Java/test/resources/MyFile.xml", path);
      }
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath5() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath5");
      if (SystemUtils.isWindowsPlatform()) {
         File dir = new File("L:/WRK/Java/test/resources");
         File file = new File("D:/WRK/Java/test/resources/MyFile.xml");
         URL baseDir = dir.toURI().toURL();
         URL url = file.toURI().toURL();

         String path = FileUtilities.getRelativePath(baseDir, url, false);
         assertEquals("Relative path", "/D:/WRK/Java/test/resources/MyFile.xml", path);
      }
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath6() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath6");
      File dir = new File("L:/WRK/Java/test1/resources");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url, false);
      assertEquals("Relative path", "../../test/resources/MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath7() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath7");
      File dir = new File("L:/WRK/Java/test/");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url, true);
      assertEquals("Relative path", "resources/MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath8() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath8");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url, true);
      assertEquals("Relative path", "MyFile.xml", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath9() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath9");
      if (SystemUtils.isWindowsPlatform()) {
         File dir = new File("L:/WRK/Java/test1/resources");
         File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
         URL baseDir = dir.toURI().toURL();
         URL url = file.toURI().toURL();

         String path = FileUtilities.getRelativePath(baseDir, url, true);
         assertEquals("Relative path", "L:/WRK/Java/test/resources/MyFile.xml", path);
      }
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath10() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath10");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/resources/MyFile.xml#toto");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url, false);
      assertEquals("Relative path", "MyFile.xml#toto", path);
   }

   /**
    * Test of getRelativePath method.
    */
   @Test
   public void testGetRelativePath11() throws IOException {
      System.out.println("FileUtilitiesTest : testGetRelativePath11");
      File dir = new File("L:/WRK/Java/test/resources");
      File file = new File("L:/WRK/Java/test/MyFile.xml#toto");
      URL baseDir = dir.toURI().toURL();
      URL url = file.toURI().toURL();

      String path = FileUtilities.getRelativePath(baseDir, url, false);
      assertEquals("Relative path", "../MyFile.xml#toto", path);
   }
}
