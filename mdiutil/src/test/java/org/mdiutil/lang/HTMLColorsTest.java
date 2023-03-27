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
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLColorsTest {

   public HTMLColorsTest() {
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
    * Test of decodeColor method, of class HTMLColors.
    */
   @Test
   public void testDecodeColor() {
      System.out.println("HTMLColorsTest : testDecodeColor");
      String color = HTMLColors.decodeColor("000000");
      assertEquals("Color", "#000000", color);
      color = HTMLColors.decodeColor("#000000");
      assertEquals("Color", "#000000", color);
      color = HTMLColors.decodeColor("FFFFFF");
      assertEquals("Color", "#FFFFFF", color);
      color = HTMLColors.decodeColor("White");
      assertEquals("Color", "#FFFFFF", color);
      color = HTMLColors.decodeColor("white");
      assertEquals("Color", "#FFFFFF", color);
      color = HTMLColors.decodeColor("toto");
      assertNull("Color", color);
   }

   /**
    * Test of decodeColor method, of class HTMLColors.
    */
   @Test
   public void testDecodeColor2() {
      System.out.println("HTMLColorsTest : testDecodeColor2");
      String color = HTMLColors.decodeColor("blue");
      assertEquals("Color", "#0000FF", color);
      color = HTMLColors.decodeColor("cornflowerblue");
      assertEquals("Color", "#6495ED", color);
      color = HTMLColors.decodeColor("cornflowerBlue");
      assertEquals("Color", "#6495ED", color);
   }

}
