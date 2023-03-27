/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
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
public class MessageConstructor2Test {

   public MessageConstructor2Test() {
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
      System.out.println("MessageConstructor2Test : testGetText");
      String template = "my ($1) cents";
      String result = MessageConstructor.getText(template, 2);
      assertEquals("Result", "my (2) cents", result);
   }

   /**
    * Test of getText method, of class MessageConstructor.
    */
   @Test
   public void testGetText2() {
      System.out.println("MessageConstructor2Test : testGetText2");
      String template = "new value ($1) is different from the old one ($2)";
      String result = MessageConstructor.getText(template, 2, 4);
      assertEquals("Result", "new value (2) is different from the old one (4)", result);
   }

}
