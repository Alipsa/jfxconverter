/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang.swing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Color;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @since 1.2.35
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLSwingColorsTest {

   public HTMLSwingColorsTest() {
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
    * Test of decodeColor method.
    */
   @Test
   public void testDecodeColor() {
      System.out.println("HTMLSwingColorsTest : testDecodeColor");
      Color color = HTMLSwingColors.decodeColor("000000");
      assertEquals("Color", new Color(0, 0, 0), color);
      color = HTMLSwingColors.decodeColor("#000000");
      assertEquals("Color", new Color(0, 0, 0), color);
      color = HTMLSwingColors.decodeColor("FFFFFF");
      assertEquals("Color", new Color(255, 255, 255), color);
      color = HTMLSwingColors.decodeColor("White");
      assertEquals("Color", new Color(255, 255, 255), color);
      color = HTMLSwingColors.decodeColor("white");
      assertEquals("Color", new Color(255, 255, 255), color);
      color = HTMLSwingColors.decodeColor("toto");
      assertNull("Color", color);
      color = HTMLSwingColors.decodeColor("toto", Color.MAGENTA);
      assertEquals("Color", Color.MAGENTA, color);
   }

   /**
    * Test of decodeColor method.
    */
   @Test
   public void testDecodeColor2() {
      System.out.println("HTMLSwingColorsTest : testDecodeColor2");
      Color color = HTMLSwingColors.decodeColor("blue");
      assertEquals("Color", Color.BLUE, color);
      color = HTMLSwingColors.decodeColor("cornflowerblue");
      assertEquals("Color", new Color(100, 149, 237), color);
      color = HTMLSwingColors.decodeColor("cornflowerBlue");
      assertEquals("Color", new Color(100, 149, 237), color);
   }
}
