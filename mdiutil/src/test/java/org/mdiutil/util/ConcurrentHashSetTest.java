/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the ConcurrentHashSet class.
 *
 * @version 1.1
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class ConcurrentHashSetTest {

   public ConcurrentHashSetTest() {
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
    * Test of isEmpty method, of class ConcurrentHashSet.
    */
   @Test
   public void testIsEmpty() {
      System.out.println("ConcurrentHashSetTest : testIsEmpty");
      ConcurrentHashSet set = new ConcurrentHashSet();
      assertTrue("Must be empty", set.isEmpty());
      set.add("toto");
      set.add("tata");
      set.add("titi");
      assertFalse("Must not be empty", set.isEmpty());

      set.clear();
      assertTrue("Must be empty", set.isEmpty());
   }

   /**
    * Test of size method, of class ConcurrentHashSet.
    */
   @Test
   public void testGetSize() {
      System.out.println("ConcurrentHashSetTest : testGetSize");
      ConcurrentHashSet set = new ConcurrentHashSet();
      assertEquals("Size", 0, set.size());
      set.add("toto");
      set.add("tata");
      set.add("titi");
      assertEquals("Size", 3, set.size());

      set.clear();
      assertEquals("Size", 0, set.size());
   }

   /**
    * Test of iterator method, of class ConcurrentHashSet.
    */
   @Test
   public void testIterator() {
      System.out.println("ConcurrentHashSetTest : testIterator");
      Set setRef = new HashSet<>();
      ConcurrentHashSet set = new ConcurrentHashSet();
      set.add("toto");
      set.add("tata");
      set.add("titi");
      setRef.add("toto");
      setRef.add("tata");
      setRef.add("titi");
      Iterator<Object> it = set.iterator();
      while (it.hasNext()) {
         Object o = it.next();
         assertTrue("must have the element", setRef.contains(o));
      }
   }

   /**
    * Test of iterator method, of class ConcurrentHashSet.
    */
   @Test
   public void testIterator2() {
      System.out.println("ConcurrentHashSetTest : testIterator2");
      Set<String> setRef = new HashSet<>();
      ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
      set.add("toto");
      set.add("tata");
      set.add("titi");
      setRef.add("toto");
      setRef.add("tata");
      setRef.add("titi");
      Iterator<String> it = set.iterator();
      while (it.hasNext()) {
         String o = it.next();
         assertTrue("must have the element", setRef.contains(o));
      }
   }

   /**
    * Test of iterator method, of class ConcurrentHashSet.
    */
   @Test
   public void testRemove() {
      System.out.println("ConcurrentHashSetTest : testRemove");
      Set<String> setRef = new HashSet<>();
      ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
      set.add("toto");
      set.add("tata");
      set.add("titi");
      set.remove("toto");
      setRef.add("titi");
      setRef.add("tata");
      assertEquals("Size", 2, set.size());
      Iterator<String> it = set.iterator();
      while (it.hasNext()) {
         String o = it.next();
         assertTrue("must have the element", setRef.contains(o));
      }
   }

   /**
    * Test of contains method, of class ConcurrentHashSet.
    */
   @Test
   public void testContains() {
      System.out.println("ConcurrentHashSetTest : testContains");
      ConcurrentHashSet set = new ConcurrentHashSet();
      set.add("toto");
      set.add("tata");
      set.add("titi");
      assertTrue("Must contain toto", set.contains("toto"));
      assertTrue("Must contain tata", set.contains("tata"));
      assertTrue("Must contain titi", set.contains("titi"));
   }
}
