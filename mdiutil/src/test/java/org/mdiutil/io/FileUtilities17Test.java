/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.IOException;
import java.net.URL;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import java.io.File;

import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.SystemUtils;

/**
 * Unit tests for FileUtilities. Unit tests for methods which use URLs and convert them to files, i the case where the path
 * contrains esxaped characters.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities17Test {
   public FileUtilities17Test() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of getFile method.
    */
   @Test
   public void testGetFile() throws IOException {
      System.out.println("FileUtilities17Test : testGetFile");
      if (SystemUtils.isWindowsPlatform()) {
         URL url = new URL("file:/D:/Java/docGenerator/testWiki9/input/doc-files/Pablo%20Picasso%20-%20Wikipedia.html");
         File file = FileUtilities.getFile(url);
         String path = file.getPath();
         assertEquals("path", "D:\\Java\\docGenerator\\testWiki9\\input\\doc-files\\Pablo Picasso - Wikipedia.html", path);
      }
   }

   /**
    * Test of getFileNameWithExtension method.
    */
   @Test
   public void testGetFileNameWithExtension() throws IOException {
      System.out.println("FileUtilities17Test : testGetFileNameWithExtension");
      URL url = new URL("file:/D:/Java/docGenerator/testWiki9/input/doc-files/Pablo%20Picasso%20-%20Wikipedia.html");
      String filename = FileUtilities.getFileNameWithExtension(url);
      assertEquals("Pablo Picasso - Wikipedia.html", filename);
   }

   /**
    * Test of getFileExtension method.
    */
   @Test
   public void testGetFileExtension() throws IOException {
      System.out.println("FileUtilities17Test : testGetFileExtension");
      URL url = new URL("file:/D:/Java/docGenerator/testWiki9/input/doc-files/Pablo%20Picasso%20-%20Wikipedia.html");
      String ext = FileUtilities.getFileExtension(url);
      assertEquals("html", ext);
   }

   /**
    * Test of getFileNameBody method.
    */
   @Test
   public void testGetFileNameBody() throws IOException {
      System.out.println("FileUtilities17Test : testGetFileNameBody");
      URL url = new URL("file:/D:/Java/docGenerator/testWiki9/input/doc-files/Pablo%20Picasso%20-%20Wikipedia.html");
      String body = FileUtilities.getFileNameBody(url);
      assertEquals("D:/Java/docGenerator/testWiki9/input/doc-files/Pablo Picasso - Wikipedia", body);
   }
}
