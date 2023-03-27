/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
 * @since 1.2.4
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities11Test {
   public FileUtilities11Test() {
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
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL");

      URL url = new URL("https://www.flickr.com");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNotNull("url should be found", newURL);
      assertEquals("URl", newURL, new URL("https://www.flickr.com"));
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL2() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL2");

      URL url = new URL("http://www.flickr.com");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNotNull("url should be found", newURL);
      assertEquals("URl", newURL, new URL("https://www.flickr.com"));
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL3() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL3");

      URL url = new URL("http://doc.qt.io/qt-5/");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNotNull("url should be found", newURL);
      assertEquals("URL", newURL, new URL("https://doc.qt.io/qt-5/"));
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL4() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL4");

      URL url = new URL("http://toto/wiki/markdown");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNull("url should not be found", newURL);
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL5() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL5");

      URL url = new URL("http://en.wikipedia.org/wiki/markdown");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNotNull("url should be found", newURL);
      assertEquals("URL", newURL, new URL("https://en.wikipedia.org/wiki/markdown"));
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL6() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL6");

      URL url = new URL("http://en.wikipedia.org/wiki/ttaati");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNull("url should not be found", newURL);
   }

   /**
    * Test of getRedirectURL method, of class FileUtilities.
    */
   @Test
   public void testGetRedirectURL7() throws IOException {
      System.out.println("FileUtilities11Test : testGetRedirectURL7");

      URL url = new URL("https://en.wikipedia.org/wiki/ttaati");
      URL newURL = FileUtilities.getRedirectURL(url);
      assertNull("url should not be found", newURL);
   }

}
