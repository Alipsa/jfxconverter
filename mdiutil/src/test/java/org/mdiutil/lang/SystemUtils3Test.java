/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertFalse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit Tests for the SystemUtils class.
 *
 * @since 1.2.18
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class SystemUtils3Test {
   public SystemUtils3Test() {
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
    * Test of isBetweenVersions method, of class SystemUtils.
    */
   @Test
   public void testIsBetweenVersions() {
      System.out.println("SystemUtils3Test : testIsBetweenVersions");
      assertFalse("Must not between 1.7.0_15 and 1.8 (strict)", SystemUtils.isBetweenVersions("1.8.0_271", "1.7", "1.8", true));
   }
}
