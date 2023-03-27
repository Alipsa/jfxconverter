/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2020, 2021 Herve Girod
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
 * Unit Tests for the MessageConstructor class.
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class MessageConstructorTest {

   public MessageConstructorTest() {
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
    * Test of getText method, of class MessageConstructor.
    */
   @Test
   public void testGetText() {
      System.out.println("MessageConstructorTest : testGetText");
      String template = "my $1 cents";
      String result = MessageConstructor.getText(template, 2);
      assertEquals("Result", "my 2 cents", result);
   }

   /**
    * Test of getText method, of class MessageConstructor.
    */
   @Test
   public void testGetText2() {
      System.out.println("MessageConstructorTest : testGetText2");
      String template = "$1 and $2 makes $3";
      String result = MessageConstructor.getText(template, 2, 2, 4);
      assertEquals("Result", "2 and 2 makes 4", result);
   }

   /**
    * Test of getText method, of class MessageConstructor.
    */
   @Test
   public void testGetText3() {
      System.out.println("MessageConstructorTest : testGetText3");
      String template = "$1 and $1 makes $2";
      String result = MessageConstructor.getText(template, 2, 4);
      assertEquals("Result", "2 and 2 makes 4", result);
   }

   /**
    * Test of getText method, of class MessageConstructor.
    */
   @Test
   public void testGetText4() {
      System.out.println("MessageConstructorTest : testGetText4");
      String template = "$0 should work";
      String result = MessageConstructor.getText(template, 2);
      assertEquals("Result", "2 should work", result);
   }

}
