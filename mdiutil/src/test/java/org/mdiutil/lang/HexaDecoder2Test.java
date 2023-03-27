/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @since 1.2.27
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HexaDecoder2Test {

   public HexaDecoder2Test() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of parseInt method, of class HexaDecoder.
    */
   @Test
   public void testParseInt() {
      System.out.println("HexaDecoder2Test : testParseInt");
      int i = HexaDecoder.parseInt("123");
      assertEquals("incorrect value", 123, i);
      i = HexaDecoder.parseInt("0x7D");
      assertEquals("incorrect value", 125, i);
   }

   /**
    * Test of parseInt method, of class HexaDecoder.
    */
   @Test
   public void testParseIntKO() {
      System.out.println("HexaDecoder2Test : testParseIntKO");
      try {
         int i = HexaDecoder.parseInt("");
         fail("Must have an exception");
      } catch (NumberFormatException e) {
      }
   }

   /**
    * Test of parseShort method, of class HexaDecoder.
    */
   @Test
   public void testParseShort() {
      System.out.println("HexaDecoder2Test : testParseInt");
      short s = HexaDecoder.parseShort("123");
      assertEquals("incorrect value", 123, s);
      s = HexaDecoder.parseShort("0x7D");
      assertEquals("incorrect value", 125, s);
   }

   /**
    * Test of parseLong method, of class HexaDecoder.
    */
   @Test
   public void testParseLong() {
      System.out.println("HexaDecoder2Test : testParseLong");
      long l = HexaDecoder.parseLong("123456");
      assertEquals("incorrect value", 123456, l);
      l = HexaDecoder.parseLong("0x7D7D7D");
      assertEquals("incorrect value", 8224125, l);
   }

   /**
    * Test of parseLong method, of class HexaDecoder.
    */
   @Test
   public void testParseByte() {
      System.out.println("HexaDecoder2Test : testParseByte");
      byte b = HexaDecoder.parseByte("12");
      assertEquals("incorrect value", (byte) 12, b);
      b = HexaDecoder.parseByte("0xD");
      assertEquals("incorrect value", (byte) 13, b);
   }

   /**
    * Test of parseLong method, of class HexaDecoder.
    */
   @Test
   public void testParseChar() {
      System.out.println("HexaDecoder2Test : testParseByte");
      char c = HexaDecoder.parseChar("12");
      assertEquals("incorrect value", (char) 12, c);
      c = HexaDecoder.parseChar("0xD");
      assertEquals("incorrect value", (char) 13, c);
   }
}
