/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 * Tests JRE version detection timeOut settings.
 *
 * @version 1.2.39.2
 */
@RunWith(OrderedRunner.class)
@Category(cat = "lang")
public class JREVersionDetector2Test {

   /**
    * Test of default timeOut, without settings it.
    */
   @Test
   @Order(order = 1)
   public void tesGetDefaultTimeOut() {
      System.out.println("JREVersionDetector2Test : tesGetDefaultTimeOut");
      JREVersionDetector.resetToOriginalDefaultTimeout();
      JREVersionDetector detector = new JREVersionDetector();
      assertEquals("Must be 200", 200L, detector.getTimeOut());
   }

   /**
    * Test of settings the timeOut.
    */
   @Test
   @Order(order = 2)
   public void testSetTimeOut() {
      System.out.println("JREVersionDetector2Test : testSetTimeOut");
      JREVersionDetector detector = new JREVersionDetector();
      detector.setTimeOut(400L);
      assertEquals("Must be 400", 400L, detector.getTimeOut());
   }

   /**
    * Test of settings the default timeOut.
    */
   @Test
   @Order(order = 3)
   public void testSetDefaultTimeOut() {
      System.out.println("JREVersionDetector2Test : testSetTimeOut");
      JREVersionDetector.setDefaultTimeOut(500L);
      JREVersionDetector detector = new JREVersionDetector();
      assertEquals("Must be 500", 500L, detector.getTimeOut());
   }

   /**
    * Test of settings the timeOut, overrding the default.
    */
   @Test
   @Order(order = 4)
   public void testSetTimeOut2() {
      System.out.println("JREVersionDetector2Test : testSetTimeOut2");
      JREVersionDetector detector = new JREVersionDetector();
      detector.setTimeOut(600L);
      assertEquals("Must be 600", 600L, detector.getTimeOut());
      assertEquals("Must be 500", 500L, JREVersionDetector.getDefaultTimeOut());
   }
}
