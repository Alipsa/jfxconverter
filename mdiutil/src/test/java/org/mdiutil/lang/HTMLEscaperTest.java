/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the HTMLEscaper class.
 *
 * @version 1.2.22
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLEscaperTest {

   public HTMLEscaperTest() {
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
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText2() {
      System.out.println("HTMLEscaperTest : testEscapeText2");
      assertEquals("HTMLEscaper result", "The &amp;#x00E9; text", HTMLEscaper.escapeToXML("The &eacute; text"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText3() {
      System.out.println("HTMLEscaperTest : testEscapeText3");
      assertEquals("HTMLEscaper result", "The &amp;#x00E9;", HTMLEscaper.escapeToXML("The &eacute;"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText4() {
      System.out.println("HTMLEscaperTest : testEscapeText4");
      assertEquals("HTMLEscaper result", "The &amp;#x00E9; or &amp;#x00E8;", HTMLEscaper.escapeToXML("The &eacute; or &egrave;"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText5() {
      System.out.println("HTMLEscaperTest : testEscapeText5");
      assertEquals("HTMLEscaper result", "The &amp;#x00E9; or &amp;#x00E8; text", HTMLEscaper.escapeToXML("The &eacute; or &egrave; text"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText6() {
      System.out.println("HTMLEscaperTest : testEscapeText6");
      assertEquals("HTMLEscaper result", "The &amp;#x2022; text", HTMLEscaper.escapeToXML("The &bull; text"));
   }

}
