/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for FileUtilities. Unit tests for the isAbsoluteURI method.
 *
 * @since 1.2.29.3
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities23Test {
   public FileUtilities23Test() {
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
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsoluteURI() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsoluteURI");
      File file = new File("L:/WRK/Java/toto/model/core/myFile.html");
      String uri = file.toURI().toString();
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertTrue("The uri must be absolute", isAbsolute);
   }

   /**
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsoluteURI2() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsoluteURI2");
      String uri = "./myFile.html";
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertFalse("The uri must not be absolute", isAbsolute);
   }

   /**
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsoluteURI3() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsoluteURI3");
      String uri = "/myFile.html";
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertFalse("The uri must not be absolute", isAbsolute);
   }

   /**
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsoluteURI4() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsoluteURI4");
      String uri = "myFile.html";
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertFalse("The uri must not be absolute", isAbsolute);
   }

   /**
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsoluteURI5() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsoluteURI5");
      String uri = "../myFile.html";
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertFalse("The uri must not be absolute", isAbsolute);
   }

   /**
    * Test of isAbsoluteURI method.
    */
   @Test
   public void testIsAbsolute6() throws IOException {
      System.out.println("FileUtilities23Test : testIsAbsolute6");
      File file = new File("http://model/core/myFile.html");
      String uri = file.toURI().toString();
      boolean isAbsolute = FileUtilities.isAbsoluteURI(uri);
      assertTrue("The path must be absolute", isAbsolute);
   }
}
