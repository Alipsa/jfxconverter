/*------------------------------------------------------------------------------
 * Copyright (C) 2011, 2013, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the StringUtils class.
 *
 * @version 0.9.25
 * TODO: does not work on windows
 */
@Ignore
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class StringUtilsTest {

   /**
    * Test of escapeString method, of class StringUtils.
    */
   @Test
   public void testEscapeString() {
      System.out.println("StringUtilsTest : testEscapeString");
      String s = "this is a text";
      String result = StringUtils.escapeString(s);
      assertEquals("Escaped String", s, result);
   }

   /**
    * Test of escapeString method, of class StringUtils. There is one accented French character.
    */
   @Test
   public void testEscapeString2() throws IOException {
      System.out.println("StringUtilsTest : testEscapeString2");
      URL url = StringUtilsTest.class.getResource("resources/accentedtext.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String s = reader.readLine();
      reader.close();
      String expected = "this is a t&#x00E9;xt";

      String result = StringUtils.escapeString(s);
      assertEquals("Escaped String", expected, result);
   }

   /**
    * Test of escapeString method, of class StringUtils. There are two accented French characters.
    */
   @Test
   public void testEscapeString3() throws IOException {
      System.out.println("StringUtilsTest : testEscapeString3");
      URL url = StringUtilsTest.class.getResource("resources/accentedtext2.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String s = reader.readLine();
      reader.close();
      String expected = "this is a t&#x00E9;&#x00E0;xt";

      String result = StringUtils.escapeString(s);
      assertEquals("Escaped String", expected, result);
   }

   /**
    * Test of escapeString method, of class StringUtils. There is a Chinese Character with Unicode value U+8C61
    * (Han character for Elephant).
    */
   @Test
   public void testEscapeString4() throws IOException {
      System.out.println("StringUtilsTest : testEscapeString4");
      URL url = StringUtilsTest.class.getResource("resources/accentedtext3.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String s = reader.readLine();
      reader.close();
      String expected = "this is a t&#x8C61;xt";

      String result = StringUtils.escapeString(s);
      assertEquals("Escaped String", expected, result);
   }

   /**
    * Test of escapeString method, of class StringUtils. There are two accented French characters.
    */
   @Test
   public void testEscapeString5() throws IOException {
      System.out.println("StringUtilsTest : testEscapeString5");
      URL url = StringUtilsTest.class.getResource("resources/accentedtext4.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1));
      String s = reader.readLine();
      reader.close();
      String expected = "this is a t&#x00E9;xt&#x00E0;";

      String result = StringUtils.escapeString(s, "ISO-8859-1");
      assertEquals("Escaped String", expected, result);
   }

   /**
    * Test of escapeString method, of class StringUtils.
    */
   @Test
   public void testEscapeString6() {
      System.out.println("StringUtilsTest : testEscapeString6");
      String s = "\u2212";
      String result = StringUtils.escapeString(s);
      assertEquals("Escaped String", "&#x2212;", result);
   }

   /**
    * Test of escapeString method, of class StringUtils.
    */
   @Test
   public void testEscapeString7() {
      System.out.println("StringUtilsTest : testEscapeString7");
      String s = "Escaped é";
      String result = StringUtils.escapeString(s, "ISO-8859-1");
      assertEquals("Escaped String", "Escaped &#x00E9;", result);
   }

   /**
    * Test of escapeString method, of class StringUtils.
    */
   @Test
   public void testEscapeString8() {
      System.out.println("StringUtilsTest : testEscapeString8");
      String s = "Escaped é";
      String result = StringUtils.escapeString(s, "UTF-8");
      assertEquals("Escaped String", "Escaped &#x00E9;", result);
   }
}
