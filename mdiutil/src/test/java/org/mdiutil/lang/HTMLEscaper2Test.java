/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2021 Herve Girod
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
public class HTMLEscaper2Test {

   public HTMLEscaper2Test() {
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
      System.out.println("HTMLEscaper2Test : testEscapeText");
      assertEquals("HTMLEscaper result", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", HTMLEscaper.escapeToXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
   }

}
