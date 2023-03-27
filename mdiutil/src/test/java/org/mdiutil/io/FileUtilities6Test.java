/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
public class FileUtilities6Test {

   /**
    * Test of getHTTPContentAsText method, of class FileUtilities2.
    */
   @Test
   public void testGetHTTPContentAsText() throws IOException {
      System.out.println("FileUtilities6Test : testGetHTTPContentAsText");

      URL url = new URL("http://toto/tt/markdown");
      String text = FileUtilities2.getHTTPContentAsText(url);
      assertNull("http://toto/tt/markdown should not be found", text);
   }

   /**
    * Test of getHTTPContentAsText method, of class FileUtilities2.
    */
   @Test
   public void testGetHTTPContentAsText2() throws IOException {
      System.out.println("FileUtilities6Test : testGetHTTPContentAsText2");

      URL url = new URL("http://en.wikipedia.org/wiki/markdown");
      String text = FileUtilities2.getHTTPContentAsText(url);
      assertNotNull("http://en.wikipedia.org/wiki/markdown should be found", text);
      assertFalse("http://en.wikipedia.org/wiki/markdown should be found", text.isEmpty());
   }

   /**
    * Test of getHTTPContentAsText method, of class FileUtilities2.
    */
   @Test
   public void testGetHTTPContentAsText3() throws IOException {
      System.out.println("FileUtilities6Test : testGetHTTPContentAsText3");

      URL url = new URL("https://en.wikipedia.org/wiki/markdown");
      String text = FileUtilities2.getHTTPContentAsText(url);
      assertNotNull("https://en.wikipedia.org/wiki/markdown should be found", text);
      assertFalse("https://en.wikipedia.org/wiki/markdown should be found", text.isEmpty());
   }

   /**
    * Test of getLength method, of class FileUtilities2.
    */
   @Test
   public void testGetLength() throws IOException {
      System.out.println("FileUtilities6Test : testGetLength");

      URL url = this.getClass().getResource("resources/file1.txt");
      long length = FileUtilities2.getLength(url);
      assertEquals("file length", 37, length);
   }

   /**
    * Test of getLength method, of class FileUtilities2.
    */
   @Test
   public void testGetLength2() throws IOException {
      System.out.println("FileUtilities6Test : testGetLength2");

      URL url = new URL("https://en.wikipedia.org/wiki/markdown");
      long length = FileUtilities2.getLength(url);
      assertTrue("file length: " + length, length > 50000);
   }
}
