/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

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
 * Unit tests for FileUtilities. Check the fix for Received fatal alert: handshake_failure through SSLHandshakeException exception.
 *
 * @since 0.9.58
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities10Test {
   public FileUtilities10Test() {
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
    * Test of isURLFound method, of class FileUtilities. Check https protocols.
    */
   @Test
   public void testIsURLFound() throws IOException {
      System.out.println("FileUtilities10Test : testIsURLFound");

      URL url = new URL("https://openlayers.org");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("https://openlayers.org should be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities. Check https protocols.
    */
   @Test
   public void testIsURLFound2() throws IOException {
      System.out.println("FileUtilities10Test : testIsURLFound2");

      URL url = new URL("https://jsoup.org/");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("https://jsoup.org/ should be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities. Check https protocols.
    */
   @Test
   public void testIsURLFound3() throws IOException {
      System.out.println("FileUtilities10Test : testIsURLFound3");

      URL url = new URL("https://mottie.github.io/tablesorter/docs/");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("https://mottie.github.io/tablesorter/docs/ should be found", found);
   }
}
