/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit test for the SoftHashMap class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class SoftHashMapTest {

   public SoftHashMapTest() {
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
    * Test of get method, of class SoftHashMap.
    */
   @Test
   public void testSoftReference() {
      System.out.println("SoftHashMapTest : testSoftReference");
      ArrayList<Object[]> allocations = null;

      Map<Integer, String> map = new SoftHashMap<>();
      for (int i = 0; i < 3000; i++) {
         map.put(i, "Value " + i);
      }

      // Force an OutOfMemoryError
      int size = 2;
      try {
         allocations = new ArrayList<>();
         while ((size = Math.min(Math.abs((int) Runtime.getRuntime().freeMemory()), Integer.MAX_VALUE)) > 0) {
            allocations.add(new Object[size]);
         }
      } catch (OutOfMemoryError e) {
         // the remaining of the code here if because if the JVM do not have enough memory, it will not perform the allocation of course,
         // so we can have a case where there still exist some remaining memory. We then reduce the amount of memory we will try to create
         // to fill the memory with elements in our list
         size = size / 2;
         while (true) {
            try {
               allocations.add(new Object[size]);
            } catch (OutOfMemoryError ex) {
               size = size / 2;
               if (size < 500) {
                  break;
               }
            }
         }

         size = map.size();
         assertTrue("Map must be empty", map.isEmpty());
         assertEquals("Map must be empty", 0, size);
      }
   }
}
