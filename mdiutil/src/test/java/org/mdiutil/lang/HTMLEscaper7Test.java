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
 * Unit tests for the HTMLEscaper class.
 *
 * @version 1.2.22
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLEscaper7Test {

   public HTMLEscaper7Test() {
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
    * Test of escapeToXML method, of class HTMLEscaper.
    */
   @Test
   public void testEscapeToXML() {
      System.out.println("HTMLEscaper7Test : escapeToXML");
      assertEquals("HTMLEscaper result", "The é text", HTMLEscaper.escapeToXML("The &eacute; text", true, false));
   }
}
