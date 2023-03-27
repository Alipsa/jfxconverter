/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2021 Herve Girod
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
 * Unit tests for the HTMLEscaper class. Tests the inverse conversion from XML to HTML entities.
 *
 * @version 1.2.22
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLEscaper6Test {

   public HTMLEscaper6Test() {
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
    * Test of escapeToHTML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToHTML() {
      System.out.println("HTMLEscaper6Test : testEscapeToHTML");
      assertEquals("HTMLEscaper result", "The &eacute; text", HTMLEscaper.escapeToHTML("The \u00E9 text"));
   }

   /**
    * Test of escapeToHTML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToHTML2() {
      System.out.println("HTMLEscaper6Test : testEscapeToHTML2");
      assertEquals("HTMLEscaper result", "The &isin; text", HTMLEscaper.escapeToHTML("The \u8712 text"));
   }

   /**
    * Test of escapeToHTML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToHTML3() {
      System.out.println("HTMLEscaper6Test : testEscapeToHTML3");
      assertEquals("HTMLEscaper result", "The &lt; text", HTMLEscaper.escapeToHTML("The < text"));
   }

   /**
    * Test of escapeToHTML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToHTML4() {
      System.out.println("HTMLEscaper6Test : testEscapeToHTML4");
      assertEquals("HTMLEscaper result", "The &minus; text", HTMLEscaper.escapeToHTML("The \u2013 text"));
      assertEquals("HTMLEscaper result", "The &minus; text", HTMLEscaper.escapeToHTML("The \u2014 text"));
   }

   /**
    * Test of escapeToHTML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToHTML5() {
      System.out.println("HTMLEscaper6Test : testEscapeToHTML5");
      assertEquals("HTMLEscaper result", "The - text", HTMLEscaper.escapeToHTML("The - text"));
   }
}
