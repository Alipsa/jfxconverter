/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Tests JRE version detection.
 *
 * @version 1.2.39.2
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class JREVersionDetectorTest {
   private static File LOCATION = null;

   public JREVersionDetectorTest() {
   }

   @BeforeClass
   public static void setUpClass() {
      String location = System.getProperty("java.home");
      LOCATION = new File(location);
      System.out.println("LOCATION is " + LOCATION);
      JREVersionDetector.setDefaultTimeOut(500);
   }

   @AfterClass
   public static void tearDownClass() {
      LOCATION = null;
   }

   /**
    * Test of isAtLeastVersion method, of class JREVersionDetector.
    */
   @Test
   public void testGetVersion() {
      System.out.println("JREVersionDetectorTest : testgetVersion");
      JREVersionDetector detector = new JREVersionDetector();
      String version = System.getProperty("java.version");
      assertEquals("Must be " + version, version, detector.getVersion(LOCATION));
   }

   /**
    * Test of isAtLeastVersion method, of class JREVersionDetector.
    */
   @Test
   public void testIsAtLeastVersion() {
      System.out.println("JREVersionDetectorTest : testIsAtLeastVersion");
      JREVersionDetector detector = new JREVersionDetector();
      assertTrue("Must be at least 1.7", detector.isAtLeastVersion(LOCATION, "1.7"));
   }

   /**
    * Test of isAtLeastVersion method, of class JREVersionDetector.
    */
   @Test
   public void testIsAtLeastVersion2() {
      System.out.println("JREVersionDetectorTest : testIsAtLeastVersion2");
      JREVersionDetector detector = new JREVersionDetector();
      assertTrue("Must be at least 1.7", detector.isAtLeastVersion(LOCATION, "1.7", JREVersionDetector.ARCH_64BIT));
      assertFalse("Must not be 32 bit", detector.isAtLeastVersion(LOCATION, "1.7", JREVersionDetector.ARCH_32BIT));
   }

   /**
    * Test of isBetweenVersions method, of class JREVersionDetector.
    */
   @Test
   public void testIsBetweenVersions() {
      System.out.println("JREVersionDetectorTest : testIsBetweenVersions");
      JREVersionDetector detector = new JREVersionDetector();
      assertTrue("Must be between 1.7 and 1.8", detector.isBetweenVersions(LOCATION, "1.7", "1.8"));
      assertFalse("Must not be between 1.7 and 1.8", detector.isBetweenVersions(LOCATION, "1.7", "1.8", true));
   }

   /**
    * Test of isBetweenVersions method, of class JREVersionDetector.
    */
   @Test
   public void testIsBetweenVersions2() {
      System.out.println("JREVersionDetectorTest : testIsBetweenVersions2");
      JREVersionDetector detector = new JREVersionDetector();
      assertTrue("Must be between 1.7 and 1.8", detector.isBetweenVersions(LOCATION, "1.7", "1.8", JREVersionDetector.ARCH_64BIT));
      assertFalse("Must not be 32 bit", detector.isBetweenVersions(LOCATION, "1.7", "1.8", JREVersionDetector.ARCH_32BIT));
   }
}
