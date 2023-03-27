/*------------------------------------------------------------------------------
 * Copyright (C) 2011, 2013, 2017 Herve Girod
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
 * Unit tests for the StringUtils class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class StringUtils2Test {

   /**
    * Test of escapeString method, of class StringUtils.
    */
   @Test
   public void testEscapeString8() {
      System.out.println("StringUtilsTest : testEscapeString8");
      String result = StringUtils.encodeXML("\u00E9");
      assertEquals("Escaped String", "&#x00E9;", result);
   }

   @Test
   public void testHtmlEscapeString() {
      System.out.println("StringUtilsTest : testHtmlEscapeString");
      String result = StringUtils.escapeHtml("\u00E5\u00E4\u00F6");
      assertEquals("Escaped String", "&#229;&#228;&#246;", result);
   }
}
