/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io.charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the CharsetDetector class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class CharsetDetectorTest {

   /**
    * Test of getCharsetMatch method, of class CharsetDetector.
    */
   @Test
   public void testGetCharsetMatch() throws Exception {
      System.out.println("CharsetDetectorTest : getCharsetMatch");
      URL url = this.getClass().getResource("resources/file1.txt");
      CharsetMatch match = CharsetDetector.detectCharsetMatch(url);
      assertNotNull("CharsetMatch must be found", match);
      assertEquals("Charset name", "ISO-8859-1", match.getName());
   }

   /**
    * Test of getCharsetMatch method, of class CharsetDetector.
    */
   @Test
   public void testGetCharsetMatch2() throws Exception {
      System.out.println("CharsetDetectorTest : testGetCharsetMatch2");
      URL url = this.getClass().getResource("resources/file2.txt");
      CharsetMatch match = CharsetDetector.detectCharsetMatch(url);
      assertNotNull("CharsetMatch must be found", match);
      assertEquals("Charset name", "UTF-8", match.getName());
   }

   /**
    * Test of getCharsetMatch method, of class CharsetDetector.
    */
   @Test
   public void testGetCharsetMatch3() throws Exception {
      System.out.println("CharsetDetectorTest : testGetCharsetMatch3");
      CharsetDetector detector = new CharsetDetector();
      URL url = this.getClass().getResource("resources/file1.txt");
      CharsetMatch match = detector.getCharsetMatch(url);
      assertNotNull("CharsetMatch must be found", match);
      assertEquals("Charset name", "ISO-8859-1", match.getName());

      url = this.getClass().getResource("resources/file2.txt");
      match = detector.getCharsetMatch(url);
      assertNotNull("CharsetMatch must be found", match);
      assertEquals("Charset name", "UTF-8", match.getName());
   }
}
