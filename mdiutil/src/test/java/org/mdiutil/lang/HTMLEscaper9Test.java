/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the HTMLEscaper class.
 *
 * @since 1.2.40
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLEscaper9Test {

   public HTMLEscaper9Test() {
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
    * Test of escapeXMLCDATA method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeXMLCDATA() {
      System.out.println("HTMLEscaper8Test : testEscapeXMLCDATA");
      String input = "< the value >";
      String output = "&lt; the value &gt;";
      assertEquals("HTMLEscaper result", output, HTMLEscaper.escapeXMLCDATA(input));
   }   
   
   /**
    * Test of escapeXMLCDATA method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeXMLCDATA2() {
      System.out.println("HTMLEscaper8Test : testEscapeXMLCDATA2");
      String input = "< the value '\"&>";
      String output = "&lt; the value '\"&amp;&gt;";     
      assertEquals("HTMLEscaper result", output, HTMLEscaper.escapeXMLCDATA(input));
   }   
   
   /**
    * Test of escapeXMLCDATA method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeXMLAttribute() {
      System.out.println("HTMLEscaper8Test : testEscapeXMLAttribute");
      String input = "< the value \">";
      String output = "&lt; the value &#34;&gt;";
      assertEquals("HTMLEscaper result", output, HTMLEscaper.escapeXMLAttribute(input));
   }      
}
