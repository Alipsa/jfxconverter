/*------------------------------------------------------------------------------
 * Copyright (C) 2020, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Check the isLGPL method.
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "archi")
public class MDIUtilConfigTest {

   public MDIUtilConfigTest() {
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
    * Test of isLGPL method, of class MDIUtilConfig.
    */
   @Test
   public void testIsLGPL() {
      System.out.println("MDIUtilConfigTest : testIsLGPL");
      MDIUtilConfig instance = MDIUtilConfig.getInstance();
      boolean result = instance.isLGPL();
      assertTrue("Must be LGPL", result);
   }

}
