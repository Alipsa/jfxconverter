/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2013, 2016, 2017, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the Message provider class.
 *
 * @version 1.2.39.2
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class MessageProviderTest {
   private MessageBundle bundle = null;
   private MessageBundle errorBundle = null;

   public MessageProviderTest() {
   }

   @BeforeClass
   public static void setUpClass() {
      MessageProvider provider = MessageProvider.getInstance();
      provider.createBundle("testKey", "org/mdiutil/lang/resources", "messages.properties");
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
      bundle = MessageProvider.getBundle("testKey");
      errorBundle = MessageProvider.getBundle("error");
   }

   @After
   public void tearDown() {
   }

   /**
    * Tests of the existence of a bundle even if no bundle with the specified name was created.
    */
   @Test
   public void testErrorBundle() throws Exception {
      System.out.println("MessageProvider : testErrorBundle");
      assertNotNull("Error Bundle", errorBundle);
   }

   /**
    * Tests of the getMessage method when the bundle does not exist.
    */
   @Test
   public void testErrorBundle2() throws Exception {
      System.out.println("MessageProvider : testErrorBundle2");
      String message = errorBundle.getMessage("noParams");
      assertEquals("Message String", "Error, no bundle with the specified name", message);
   }

   /**
    * Tests of the getMessage method without variables.
    */
   @Test
   public void testGetMessageWithoutVars() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars");
      String message = bundle.getMessage("noParams");
      assertEquals("Message String", "No Parameter", message);
   }
   
   /**
    * Tests of the getMessage method without variables, but with a variable declaration in the arguments.
    */
   @Test
   public void testGetMessageWithoutVars2() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithoutVars2");
      String message = bundle.getMessage("noParams", "test1");
      assertEquals("Message String", "No Parameter", message);
   }   
   
   /**
    * Tests of the getMessage method without variables, but with a variable declaration in the arguments.
    */
   @Test
   public void testGetMessageWithoutVars3() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithoutVars3");
      String message = bundle.getMessage("noParams", new Object[0]);
      assertEquals("Message String", "No Parameter", message);
   }    

   /**
    * Tests of the getMessage method with variables, in the case where there is no variables Pattern defined.
    */
   @Test
   public void testGetMessageWithVars() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars");
      String message = bundle.getMessage("noParams");
      assertEquals("Message String", "No Parameter", message);
   }

   /**
    * Tests of the getMessage method with variables.
    */
   @Test
   public void testGetMessageWithVars2() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars2");
      String message = bundle.getMessage("oneParam", "test1");
      assertEquals("Message String", "One Parameter test1 is defined", message);
   }

   /**
    * Tests of the getMessage method with variables.
    */
   @Test
   public void testGetMessageWithVars3() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars3");
      String message = bundle.getMessage("oneParam", "test1", "test2");
      assertEquals("Message String", "One Parameter test1 is defined", message);
   }

   /**
    * Tests of the getMessage method with variables.
    */
   @Test
   public void testGetMessageWithVars4() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars4");
      String message = bundle.getMessage("twoParams", "test1", "test2");
      assertEquals("Message String", "Two Parameters test1 and test2", message);
   }

   /**
    * Tests of the getMessage method with variables.
    */
   @Test
   public void testGetMessageWithVars5() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars5");
      String message = bundle.getMessage("oneParam", 4);
      assertEquals("Message String", "One Parameter 4 is defined", message);
   }

   /**
    * Tests of the getMessage method with variables, where there are too many variables.
    */
   @Test
   public void testGetMessageWithVars6() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars6");
      String message = bundle.getMessage("twoParams", "test1", "test2", 4);
      assertEquals("Message String", "Two Parameters test1 and test2", message);
   }

   /**
    * Tests of the getMessage method with variables, where there are missing variables compared to tags.
    */
   @Test
   public void testGetMessageWithVars7() throws Exception {
      System.out.println("MessageProvider : testGetMessageWithVars7");
      String message = bundle.getMessage("twoParams", "test1");
      assertEquals("Message String", "Two Parameters test1 and", message);
   }
}
