/*------------------------------------------------------------------------------
* Copyright (C) 2012, 2016, 2017 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * UTF8ReaderTest.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class UTF8ReaderTest {

   public UTF8ReaderTest() {
   }

   /**
    * Test of hexToString method, of class girod.util.xml.UTF8Reader.
    */
   @Test
   public void testStringToHex() {
      System.out.println("UTF8ReaderTest : testStringToHex");

      UTF8Reader reader = UTF8Reader.getReader();
      assertEquals(reader.stringToHex("a\u00E9\u00E9"), "a&#x00e9;&#x00e9;");
   }

   /**
    * Test of hexToString method, of class girod.util.xml.UTF8Reader.
    */
   @Test
   public void testHexToString() {
      System.out.println("UTF8ReaderTest : testHexToString");

      UTF8Reader reader = UTF8Reader.getReader();
      String s = "a&#x00e9;&#x00e9;";
      assertEquals(reader.hexToString(s), "a\u00E9\u00E9");
   }
}
