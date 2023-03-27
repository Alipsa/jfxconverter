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
 * @since 1.2.40
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributesBoundPrefixTest {

   public XMLNodeAttributesBoundPrefixTest() {
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
    * Test of addAttribute method, of class XMLNode. Check case where we bind a prefix.
    */
   @Test
   public void test() {
      System.out.println("XMLNodeAttributesBoundPrefixTest : test>AddAttribute");
      XMLNode node = new XMLNode("myNode");
      String includeURI = "http://www.w3.org/2001/XInclude";
      node.addAttribute("xmnls:xi", includeURI);
      Map<SortableQName, String> attributes = node.getAttributes();
      assertTrue("Attributes map is empty", attributes.isEmpty());
      Map<String, String> prefixes = node.getBoundPrefixes();
      assertNotNull("BoundPrefixes should not be null", prefixes);
      assertEquals("BoundPrefixes", 1, prefixes.size());
      assertTrue("BoundPrefixes should contain http://www.w3.org/2001/XInclude", prefixes.containsKey(includeURI));
      String prefix = prefixes.get(includeURI);
      assertEquals("BoundPrefixe for http://www.w3.org/2001/XInclude", "xi", prefix);
   }
}
