/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
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
import org.mdiutil.lang.ResourceLoader;

/**
 * Unit tests for FileUtilities. Unit tests for getAbsoluteURL.
 *
 * @version 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities15Test {
   public FileUtilities15Test() {
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
    * Test of getAbsoluteURL method, of class FileUtilities.
    */
   @Test
   public void testGetAbsoluteURL() throws IOException {
      System.out.println("FileUtilities15Test : testGetAbsoluteURL");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io");
      URL baseURL = loader.getURL("resources");
      URL url = FileUtilities.getAbsoluteURL(baseURL, "file1.txt");
      loader = new ResourceLoader("org/mdiutil/io/resources");
      URL expectedURL = loader.getURL("file1.txt");
      assertEquals("URL", expectedURL, url);
   }

   /**
    * Test of getAbsoluteURL method, of class FileUtilities.
    */
   @Test
   public void testGetAbsoluteURL2() throws IOException {
      System.out.println("FileUtilities15Test : testGetAbsoluteURL2");

      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL baseURL = loader.getURL("file1.txt");
      URL url2 = loader.getURL("file2.txt");
      URL url = FileUtilities.getAbsoluteURL(baseURL, url2.getPath());
      loader = new ResourceLoader("org/mdiutil/io/resources");
      URL expectedURL = loader.getURL("file2.txt");
      assertEquals("URL", expectedURL, url);
   }
}
