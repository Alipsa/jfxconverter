/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 *
 * @since 1.2.39.4
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class SortableQNameTest {

   public SortableQNameTest() {
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
    * Test of the instanciation.
    */
   @Test
   public void createQNameTest() {
      System.out.println("SortableQNameTest : createQNameTest");
      SortableQName qname = new SortableQName("name");
      assertNotNull("Prefix must not be null", qname.getPrefix());
      assertTrue("Prefix must be empty", qname.getPrefix().isEmpty());
      assertNotNull("LocalPart must not be null", qname.getLocalPart());
      assertEquals("LocalPart must be name", "name", qname.getLocalPart());
      assertNotNull("NamespaceURI must not be null", qname.getNamespaceURI());
      assertTrue("NamespaceURI must be empty", qname.getNamespaceURI().isEmpty());
   }

   /**
    * Test of the instanciation.
    */
   @Test
   public void createQName2Test() {
      System.out.println("SortableQNameTest : createQName2Test");
      SortableQName qname = new SortableQName("http://the.uri", "name");
      assertNotNull("Prefix must not be null", qname.getPrefix());
      assertTrue("Prefix must be empty", qname.getPrefix().isEmpty());
      assertNotNull("LocalPart must not be null", qname.getLocalPart());
      assertEquals("LocalPart must be name", "name", qname.getLocalPart());
      assertNotNull("NamespaceURI must not be null", qname.getNamespaceURI());
      assertEquals("NamespaceURI", "http://the.uri", qname.getNamespaceURI());
   }

   /**
    * Test of the instanciation.
    */
   @Test
   public void createQName3Test() {
      System.out.println("SortableQNameTest : createQName3Test");
      SortableQName qname = new SortableQName("pre:name");
      assertNotNull("Prefix", qname.getPrefix());
      assertEquals("Prefix", "pre", qname.getPrefix());
      assertNotNull("LocalPart must not be null", qname.getLocalPart());
      assertEquals("LocalPart must be name", "name", qname.getLocalPart());
      assertNotNull("NamespaceURI must not be null", qname.getNamespaceURI());
      assertTrue("NamespaceURI must be empty", qname.getNamespaceURI().isEmpty());
   }

   /**
    * Test of the sorting.
    */
   @Test
   public void sortableQNamesTest() {
      System.out.println("SortableQNameTest : sortableQNamesTest");
      SortableQName qname = new SortableQName("pre:name");
      SortableQName qname2 = new SortableQName("pre:name2");
      assertEquals("Sort qnames", 0, qname.compareTo(qname));
      assertEquals("Sort qnames", 1, qname2.compareTo(qname));
      assertEquals("Sort qnames", -1, qname.compareTo(qname2));
   }

   /**
    * Test of the has code.
    */
   @Test
   public void qNameHashcodeTest() {
      System.out.println("SortableQNameTest : qNameHashcodeTest");
      SortableQName qname = new SortableQName("pre:name");
      SortableQName qname2 = new SortableQName("pre:name2");
      Map<SortableQName, String> map = new TreeMap<>();
      map.put(qname, "theName");
      map.put(qname2, "theName2");

      assertEquals("Map size", 2, map.size());
      String value = map.get(qname);
      assertEquals("Map value", "theName", value);

      value = map.get(qname2);
      assertEquals("Map value", "theName2", value);
   }

}
