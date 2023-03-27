/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

/**
 * Unit tests for FileUtilities.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities5Test {
   public FileUtilities5Test() {
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
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 301 response code, but
    * the returned URL does not match with the initial URL.
    */
   @Test
   public void testIsURLFound() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound");

      URL url = new URL("http://en.wikpedia.org/wiki/markdown");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://en.wikpedia.org/wiki/markdown should be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 200 response code.
    */
   @Test
   public void testIsURLFound2() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound2");

      URL url = new URL("http://en.wikipedia.org/wiki/markdown");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://en.wikipedia.org/wiki/markdown should be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 404 response code.
    */
   @Test
   public void testIsURLFound3() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound3");

      URL url = new URL("http://toto/wiki/markdown");
      boolean found = FileUtilities.isURLFound(url);
      assertFalse("http://toto/wiki/markdown should not be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 200 response code.
    */
   @Test
   public void testIsURLFound4() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound4");

      URL url = new URL("http://doc.qt.io/qt-5/");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://doc.qt.io/qt-5/ should be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 301 response code, and
    * the response URL is identical to the input URL.
    */
   @Test
   public void testIsURLFound5() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound5");

      URL url = new URL("http://www.flickr.com");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://www.flickr.com should be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 301 response code, and
    * the response URL is identical to the input URL.
    */
   @Test
   public void testIsURLFound6() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound6");

      URL url = new URL("http://www.flickr.com//explore");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://www.flickr.com//explore should be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 301 response code, but
    * the returned URL does not match with the initial URL.
    */
   @Test
   public void testIsURLFound7() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound7");

      URL url = new URL("http://en.tikpedia.org/wiki/markdown");
      boolean found = FileUtilities.isURLFound(url);
      assertFalse("http://en.tikpedia.org/wiki/markdown should not be found", found);
   }

   /**
    * Test of testIsURLFound method, of class FileUtilities. In this case the server returns a 404 response code.
    */
   @Test
   public void testIsURLFound8() throws IOException {
      System.out.println("FileUtilities5Test : testIsURLFound8");

      URL url = new URL("http://en.wikipedia.org/wiki/ttaati");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("http://en.wikipedia.org/wiki/ttaati should be found", found);
   }

}
