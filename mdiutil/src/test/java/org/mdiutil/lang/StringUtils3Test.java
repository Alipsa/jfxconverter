/*------------------------------------------------------------------------------
 * Copyright (C) 2011, 2013, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the StringUtils class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class StringUtils3Test {

   public StringUtils3Test() {
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
    * Test of escapeString method, of class StringUtils. There is a Chinese Character with Unicode value U+8C61
    * (Han character for Elephant).
    */
   @Test
   public void testEscapeString4() throws IOException {
      System.out.println("StringUtilsTest : testEscapeString4");

      String result = StringUtils.escapeString("\u8C61");
      assertEquals("Escaped String", "&#x8C61;", result);
   }
}
