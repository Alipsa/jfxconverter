/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2016, 2017, 2018, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.testutils.MyTestUtilClass;

/**
 * Unit Tests for the SystemUtils class.
 *
 * @version 1.2.18
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class SystemUtilsTest {
   public SystemUtilsTest() {
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
   public void testIsAtLeastVersion1() {
      System.out.println("SystemUtilsTest : isAtLeastVersion1");
      assertTrue("Must be at least 1.7", SystemUtils.isAtLeastVersion("1.7.0_014", "1.7"));
      assertFalse("Must not be at least 1.8", SystemUtils.isAtLeastVersion("1.7.0_014", "1.8"));
      assertTrue("Must be at least 1.7", SystemUtils.isAtLeastVersion("1.7.0_014-ea", "1.7"));
      assertFalse("Must not be at least 1.8", SystemUtils.isAtLeastVersion("1.7.0_014-ea", "1.8"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion2() {
      System.out.println("SystemUtilsTest : isAtLeastVersion2");
      assertTrue("Must be at least 1.7.0", SystemUtils.isAtLeastVersion("1.7.0_014", "1.7.0"));
      assertFalse("Must not be at least 1.8.0", SystemUtils.isAtLeastVersion("1.7.0_014", "1.8.0"));
      assertTrue("Must be at least 1.7.0", SystemUtils.isAtLeastVersion("1.7.0_014-ea", "1.7.0"));
      assertFalse("Must not be at least 1.8.0", SystemUtils.isAtLeastVersion("1.7.0_014-ea", "1.8.0"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion3() {
      System.out.println("SystemUtilsTest : isAtLeastVersion3");
      assertTrue("Must be at least 1.7.0", SystemUtils.isAtLeastVersion("1.7.0", "1.7.0"));
      assertFalse("Must not be at least 1.8.0", SystemUtils.isAtLeastVersion("1.7.0", "1.8.0"));
      assertTrue("Must be at least 1.7.0", SystemUtils.isAtLeastVersion("1.7.0-ea", "1.7.0"));
      assertFalse("Must not be at least 1.8.0", SystemUtils.isAtLeastVersion("1.7.0-ea", "1.8.0"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion4() {
      System.out.println("SystemUtilsTest : isAtLeastVersion4");
      assertFalse("Must not be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.0", "1.7.0_014"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion5() {
      System.out.println("SystemUtilsTest : isAtLeastVersion5");
      assertTrue("Must be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.0_014", "1.7.0_014"));
      assertFalse("Must not be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.0_013", "1.7.0_014"));
      assertFalse("Must not be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.0_013-ea", "1.7.0_014"));
      assertTrue("Must be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.0_014-ea", "1.7.0_014"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion6() {
      System.out.println("SystemUtilsTest : isAtLeastVersion6");
      assertTrue("Must be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.8.0_01", "1.7.0_014"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion7() {
      System.out.println("SystemUtilsTest : isAtLeastVersion7");
      assertTrue("Must be at least 1.7.1", SystemUtils.isAtLeastVersion("1.7.2", "1.7.1"));
      assertTrue("Must be at least 1.7.1", SystemUtils.isAtLeastVersion("1.8.0_01", "1.7.1"));
      assertTrue("Must be at least 1.7.1", SystemUtils.isAtLeastVersion("1.7.1", "1.7.1"));
      assertTrue("Must be at least 1.7.1", SystemUtils.isAtLeastVersion("1.7.1_014", "1.7.1"));
      assertFalse("Must not be at least 1.7.0_014", SystemUtils.isAtLeastVersion("1.7.1", "1.7.1_014"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion8() {
      System.out.println("SystemUtilsTest : testIsAtLeastVersion8");
      assertTrue("Must be at least 12.0", SystemUtils.isAtLeastVersion("12.1", "12.0"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion9() {
      System.out.println("SystemUtilsTest : testIsAtLeastVersion9");
      assertTrue("Must be at least 10", SystemUtils.isAtLeastVersion("10.1", "10"));
      assertFalse("Must not be at least 10", SystemUtils.isAtLeastVersion("8.1", "10"));
      assertFalse("Must not be at least 15", SystemUtils.isAtLeastVersion("8.1", "15"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion10() {
      System.out.println("SystemUtilsTest : testIsAtLeastVersion10");
      assertTrue("Must be at least 15", SystemUtils.isAtLeastVersion("15", "15"));
      assertFalse("Must not be at least 15.1", SystemUtils.isAtLeastVersion("15", "15.1"));
   }

   /**
    * Test of isAtLeastVersion method, of class SystemUtils.
    */
   @Test
   public void testIsAtLeastVersion11() {
      System.out.println("SystemUtilsTest : testIsAtLeastVersion11");
      assertTrue("Must be at least 10", SystemUtils.isAtLeastVersion("12.1", "10"));
      assertFalse("Must not be at least 15", SystemUtils.isAtLeastVersion("12.1", "15"));
   }

   /**
    * Test of isBetweenVersions method, of class SystemUtils.
    */
   @Test
   public void testIsBetweenVersions1() {
      System.out.println("SystemUtilsTest : testIsBetweenVersions1");
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014", "1.7", "1.8"));
      assertFalse("Must not between 1.7.0_15 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014", "1.7.0_015", "1.8"));
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014-ea", "1.7", "1.8"));
   }

   /**
    * Test of isBetweenVersions method, of class SystemUtils.
    */
   @Test
   public void testIsBetweenVersions2() {
      System.out.println("SystemUtilsTest : testIsBetweenVersions2");
      assertTrue("Must be between 1.7 and 1.7", SystemUtils.isBetweenVersions("1.7.0_014", "1.7", "1.7"));
      assertFalse("Must not be between 1.8 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014", "1.8", "1.8"));
      assertTrue("Must be between 1.7 and 1.7", SystemUtils.isBetweenVersions("1.7.0_014-ea", "1.7", "1.7"));
      assertFalse("Must not be between 1.8 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014-ea", "1.8", "1.8"));
   }

   /**
    * Test of isBetweenVersions method, of class SystemUtils.
    */
   @Test
   public void testIsBetweenVersions3() {
      System.out.println("SystemUtilsTest : testIsBetweenVersions3");
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.7", "1.7", "1.8"));
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014", "1.7", "1.8", true));
      assertFalse("Must not between 1.7.0_15 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014", "1.7.0_015", "1.8", true));
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.7.0_014-ea", "1.7", "1.8", true));
      assertFalse("Must not be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.8", "1.7", "1.8", true));
      assertTrue("Must be between 1.7 and 1.8", SystemUtils.isBetweenVersions("1.8", "1.7", "1.8"));
   }

   /**
    * Test of getLocation method, of class SystemUtils.
    */
   @Test
   public void testGetLocation() throws IOException {
      System.out.println("SystemUtilsTest : testGetLocation");
      // Location for String
      URL url = SystemUtils.getLocation(String.class);
      assertNull("Null Location for String.class", url);
      // Location for SystemUtils
      url = SystemUtils.getLocation(SystemUtils.class);
      File userdir = new File(System.getProperty("user.dir"));
      File expected = new File(userdir, "target/classes");
      URL expectedURL = expected.toURI().toURL();
      assertEquals("Location for SystemUtils.class", expectedURL, url);
   }

   /**
    * Test of getLocation method, of class SystemUtils.
    */
   @Test
   public void testGetLocation2() throws IOException {
      System.out.println("SystemUtilsTest : testGetLocation2");

      // Location for MyTestUtilClass
      URL url = SystemUtils.getLocation(MyTestUtilClass.class);
      File userdir = new File(System.getProperty("user.dir"));
      File expected = new File(userdir, "target/classes");
      URL expectedURL = expected.toURI().toURL();
      assertEquals("Location for MyTestUtilClass.class", expectedURL, url);
   }
}
