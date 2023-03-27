/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for FileUtilities. Unit tests for getFileNameBody.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities16Test {
   public FileUtilities16Test() {
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
    * Test of getFileNameBody method, of class FileUtilities.
    */
   @Test
   public void testGetFileNameWithExtension() throws IOException {
      System.out.println("FileUtilities15Test : testGetFileNameBody");
      URL url = new URL("file:/D:/Java/MDIUtilities/code/resources/file1.txt");
      String filename = FileUtilities.getFileNameWithExtension(url);
      assertEquals("filename", "file1.txt", filename);
   }

   /**
    * Test of getFileNameBody method, of class FileUtilities.
    */
   @Test
   public void testGetFileNameWithExtension2() throws IOException {
      System.out.println("FileUtilities15Test : testGetFileNameBody");
      URL url = new URL("https://en.wikipedia.org/wiki/Pablo_Picasso");
      String filename = FileUtilities.getFileNameWithExtension(url);
      assertEquals("filename", "Pablo_Picasso", filename);
   }
}
