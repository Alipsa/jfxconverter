/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
public class SystemUtils2Test {
   public SystemUtils2Test() {
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
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion");
      assertTrue("Must be at least 1.8", SystemUtils.isAtLeastVersion("1.8", "1.8"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion2() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion2");
      assertTrue("Must be at least 1.8", SystemUtils.isAtLeastVersion("1.8", "1.8"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion3() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion3");
      assertTrue("Must be at least 1.8", SystemUtils.isAtLeastVersion("10", "1.8"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion4() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion4");
      assertTrue("Must be at least 1.8", SystemUtils.isAtLeastVersion("9.0.1", "1.8"));
      assertTrue("Must be at least 9", SystemUtils.isAtLeastVersion("9.0.1", "9"));
      assertFalse("Must not be at least 9.1", SystemUtils.isAtLeastVersion("9.0.1", "9.1"));
      assertFalse("Must not be at least 10", SystemUtils.isAtLeastVersion("9.0.1", "10"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion5() {
      System.out.println("SystemUtilsTest : testIsAtLeastVersion5");
      assertTrue("Must be at least 8", SystemUtils.isAtLeastVersion("1.8.0_1", "1.8"));
      assertFalse("Must not be at least 10", SystemUtils.isAtLeastVersion("1.8.0_1", "10"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion6() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion6");
      assertTrue("Must be at least 1.8", SystemUtils.isAtLeastVersion("1.8.0_1-beta", "1.8"));
      assertFalse("Must not be at least 10", SystemUtils.isAtLeastVersion("1.8.0_1-beta", "10"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion7() {
      System.out.println("SystemUtils2Test : testIsAtLeastVersion7");
      assertTrue("Must be at least 10", SystemUtils.isAtLeastVersion("10.1", "10"));
      assertFalse("Must not be at least 10.1", SystemUtils.isAtLeastVersion("10", "10.1"));
   }
}
