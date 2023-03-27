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
 * @version 1.2.27
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HexaDecoderTest {

   public HexaDecoderTest() {
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
    * Test of byteToHex method, of class HexaDecoder.
    */
   @Test
   public void testByteToHex() {
      System.out.println("HexaDecoderTest : testByteToHex");
      String hex = HexaDecoder.byteToHex((byte) 'A');
      assertEquals("'A' as hex", "41", hex);

      hex = HexaDecoder.byteToHex((byte) 127);
      assertEquals("127 as hex", "7F", hex);
   }

   /**
    * Test of encode method, of class HexaDecoder.
    */
   @Test
   public void testEncode() {
      System.out.println("HexaDecoderTest : testEncode");
      byte[] b = new byte[2];
      b[0] = (byte) 'A';
      b[1] = (byte) 127;
      String hex = HexaDecoder.encode(b);
      assertEquals("hex", "417F", hex);
   }

   /**
    * Test of decode method, of class HexaDecoder.
    */
   @Test
   public void testDecode() {
      System.out.println("HexaDecoderTest : testDecode");
      byte[] b = HexaDecoder.decode("417F");
      assertNotNull("must not be null", b);
      assertEquals("length", 2, b.length);
      assertEquals("41", (byte) 'A', b[0]);
      assertEquals("7F", (byte) 127, b[1]);
   }

   /**
    * Test of decode method, of class HexaDecoder.
    */
   @Test
   public void testDecodeEmpty() {
      System.out.println("HexaDecoderTest : testDecodeEmpty");
      byte[] b = HexaDecoder.decode("");
      assertNotNull("must not be null", b);
   } 
}
