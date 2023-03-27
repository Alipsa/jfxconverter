/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2016, 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the SingletonList class.
 *
 * @version 1.1
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class SingletonListTest {

   public SingletonListTest() {
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
    * Test of getUniqueElement method, of class SingletonList.
    */
   @Test
   public void testSetGetUniqueElement() {
      System.out.println("SingletonListTest : testSetGetUniqueElement");
      SingletonList list = new SingletonList("toto");
      assertEquals("Get unique element", "toto", list.getUniqueElement());
      list.setUniqueElement("titi");
      assertEquals("Get unique element", "titi", list.getUniqueElement());
   }

   /**
    * Test of size method, of class SingletonList.
    */
   @Test
   public void testSizeIsEmpty() {
      System.out.println("SingletonListTest : testSizeIsEmpty");
      SingletonList<String> list = new SingletonList<>();
      assertEquals("List size", 0, list.size());
      assertTrue("List is Empty", list.isEmpty());
      list.setUniqueElement("titi");
      assertEquals("List size", 1, list.size());
      assertFalse("List is not Empty", list.isEmpty());
   }

   /**
    * Test of contains method, of class SingletonList.
    */
   @Test
   public void testContains() {
      System.out.println("SingletonListTest : testContains");
      SingletonList list = new SingletonList();
      assertEquals("List size", 0, list.size());
      assertFalse("List does not contain titi", list.contains("titi"));
      list.setUniqueElement("titi");
      assertTrue("List does contain titi", list.contains("titi"));
   }

   /**
    * Test of iterator method, of class SingletonList.
    */
   @Test
   public void testIterator() {
      System.out.println("SingletonListTest : testIterator");
      SingletonList list = new SingletonList("toto");
      assertEquals("Get unique element", "toto", list.getUniqueElement());
      Iterator<String> it = list.iterator();
      assertTrue("Iterator hasNext", it.hasNext());
      try {
         it.next();
      } catch (Exception e) {
         fail("First next must complete");
      }
      assertFalse("Iterator hasNext", it.hasNext());
      boolean catchEx = false;
      try {
         it.next();
      } catch (NoSuchElementException e) {
         catchEx = true;
      }
      assertTrue("Second hasNext should trigger an exception", catchEx);
   }

   /**
    * Test of iterator method, of class SingletonList.
    */
   @Test
   public void testIterator2() {
      System.out.println("SingletonListTest : testIterator");
      SingletonList list = new SingletonList();
      Iterator<String> it = list.iterator();
      assertFalse("Iterator hasNext", it.hasNext());
   }

   /**
    * Test of add method, of class SingletonList.
    */
   @Test
   public void testAdd() {
      System.out.println("SingletonListTest : testAdd");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      assertEquals("List size", 1, list.size());
      assertEquals("Get unique element", "titi", list.getUniqueElement());
      boolean catchEx = false;
      try {
         list.add("titi");
      } catch (IndexOutOfBoundsException e) {
         catchEx = true;
      }
      assertTrue("Second add must fail", catchEx);
   }

   /**
    * Test of remove method, of class SingletonList.
    */
   @Test
   public void testRemove() {
      System.out.println("SingletonListTest : testRemove");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      assertEquals("List size", 1, list.size());
      assertEquals("Get unique element", "titi", list.getUniqueElement());
      list.remove("titi");
      try {
         list.remove("toto");
      } catch (Exception e) {
         fail("Remove must not fail");
      }
   }

   /**
    * Test of clear method, of class SingletonList.
    */
   @Test
   public void testClear() {
      System.out.println("SingletonListTest : testClear");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      assertEquals("List size", 1, list.size());
      assertEquals("Get unique element", "titi", list.getUniqueElement());
      list.clear();
      assertEquals("List size", 0, list.size());
      try {
         list.clear();
      } catch (Exception e) {
         fail("Clear must not fail");
      }
   }

   /**
    * Test of get method, of class SingletonList.
    */
   @Test
   public void testGet() {
      System.out.println("SingletonListTest : testGet");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      assertEquals("List size", 1, list.size());
      assertEquals("Get unique element", "titi", list.get(0));
      boolean catchEx = false;
      try {
         list.get(1);
      } catch (IndexOutOfBoundsException e) {
         catchEx = true;
      }
      assertTrue("Second get must fail", catchEx);
   }

   /**
    * Test of removeAll method, of class SingletonList.
    */
   @Test
   public void testRemoveAll() {
      System.out.println("SingletonListTest : testRemoveAll");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      list2.add("toto");
      boolean removed = list.removeAll(list2);
      assertTrue("List removed", removed);
      assertEquals("List size", 0, list.size());
      assertTrue("List size", list.isEmpty());
   }

   /**
    * Test of removeAll method, of class SingletonList.
    */
   @Test
   public void testRemoveAll2() {
      System.out.println("SingletonListTest : testRemoveAll2");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      List<String> list2 = new ArrayList<>();
      list2.add("tata");
      list2.add("toto");
      boolean removed = list.removeAll(list2);
      assertFalse("List not removed", removed);
      assertEquals("List size", 1, list.size());
      assertFalse("List size", list.isEmpty());
   }

   /**
    * Test of containsAll method, of class SingletonList.
    */
   @Test
   public void testContainsAll() {
      System.out.println("SingletonListTest : testContainsAll");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      boolean contains = list.containsAll(list2);
      assertTrue("List contains list2", contains);
      assertEquals("List size", 1, list.size());
   }

   /**
    * Test of containsAll method, of class SingletonList.
    */
   @Test
   public void testContainsAll2() {
      System.out.println("SingletonListTest : testContainsAll2");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      list2.add("tata");
      boolean contains = list.containsAll(list2);
      assertFalse("List does not contains list2", contains);
      assertEquals("List size", 1, list.size());
   }

   /**
    * Test of addAll method, of class SingletonList.
    */
   @Test
   public void testAddAll() {
      System.out.println("SingletonListTest : testAddAll");
      SingletonList<String> list = new SingletonList();
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      boolean added = list.addAll(list2);
      assertTrue("List added", added);
      assertEquals("List size", 1, list.size());
      assertFalse("List size", list.isEmpty());
   }

   /**
    * Test of addAll method, of class SingletonList.
    */
   @Test
   public void testAddAll2() {
      System.out.println("SingletonListTest : testAddAll2");
      SingletonList<String> list = new SingletonList();
      list.add("titi");
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      boolean added = list.addAll(list2);
      assertFalse("List not added", added);
      assertEquals("List size", 1, list.size());
      assertFalse("List size", list.isEmpty());
   }

   /**
    * Test of addAll method, of class SingletonList.
    */
   @Test
   public void testAddAll3() {
      System.out.println("SingletonListTest : testAddAll3");
      SingletonList<String> list = new SingletonList();
      List<String> list2 = new ArrayList<>();
      list2.add("titi");
      list2.add("tata");
      boolean added = list.addAll(list2);
      assertFalse("List not added", added);
      assertEquals("List size", 0, list.size());
      assertTrue("List size", list.isEmpty());
   }
}
