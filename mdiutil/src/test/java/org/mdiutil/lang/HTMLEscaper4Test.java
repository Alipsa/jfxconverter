/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2020, 2021 Herve Girod
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
public class HTMLEscaper4Test {

   public HTMLEscaper4Test() {
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
   public void testEscapeText() {
      System.out.println("HTMLEscaper4Test : testEscapeText");
      assertEquals("HTMLEscaper result", "&amp;#x00E9;t&amp;#x00E9;r&amp;#x00E9;", HTMLEscaper.escapeToXML("&eacute;t&eacute;r&eacute;"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText2() {
      System.out.println("HTMLEscaper4Test : testEscapeText2");
      assertEquals("HTMLEscaper result", "&amp;the text", HTMLEscaper.escapeToXML("&amp;the text"));
   }

   /**
    * Test of escapeText method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeText3() {
      System.out.println("HTMLEscaper4Test : testEscapeText3");
      assertEquals("HTMLEscaper result", "&lt;value=\"&amp;other\"&gt;", HTMLEscaper.escapeToHTML("&lt;value=\"&amp;other\"&gt;"));
   }
}
