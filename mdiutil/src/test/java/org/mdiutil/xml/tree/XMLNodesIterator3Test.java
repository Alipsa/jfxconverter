/*------------------------------------------------------------------------------
* Copyright (C) 2023 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;
import org.xml.sax.SAXException;

/**
 *
 * @since 1.2.44
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodesIterator3Test {
   private static XMLRoot root = null;
   private static Map<String, NodeFilter> filters = new HashMap<>();
   private static final String AUTOBOUND_FILTER = "ProxyAutoBoundsNode";
   private static XMLNode theNode = null;
   private static XMLNodesIterator walker = null;

   public XMLNodesIterator3Test() {
   }

   @BeforeClass
   public static void setUpClass() {
      filters.put(AUTOBOUND_FILTER, new NodeFilter(AUTOBOUND_FILTER, "y:ProxyAutoBoundsNode"));
   }

   @AfterClass
   public static void tearDownClass() {
      root = null;
      filters = null;
      theNode = null;
      walker = null;
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of parsing.
    */
   @Test
   @Order(order = 1)
   public void testParse() throws Exception {
      System.out.println("XMLNodesIterator3Test : testParse");
      URL url = this.getClass().getResource("resources/groups.graphml");
      SAXParserFactory factory = SAXParserFactory.newInstance();
      try {
         SAXParser parser = factory.newSAXParser();
         XMLTreeHandler handler = new XMLTreeHandler();
         parser.parse(url.openStream(), handler);
         root = handler.getRoot();
         assertNotNull("Root must not be null", root);
      } catch (ParserConfigurationException | SAXException ex) {
         ex.printStackTrace();
         fail(ex.getMessage());
      }
   }

   /**
    * Test of walking.
    */
   @Test
   @Order(order = 2)
   public void testWalk() throws Exception {
      System.out.println("XMLNodesIterator3Test : testWalk");
      walker = new XMLNodesIterator(root);
      walker.setParentFilters(filters);
      XMLNode node;
      for (int i = 0; i < 11; i++) {
         node = walker.nextNode();
         assertNotNull("node must not be null", node);
         assertEquals("Node name", "key", node.getName());
         assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
         assertNull("Must not be in any filter", walker.getCurrentFilterName());
         assertEquals("Node level", 1, walker.getLevel());
      }

      node = walker.nextNode();
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "graph", node.getName());
      assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be in any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 1, walker.getLevel());

      node = walker.nextNode();
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "data", node.getName());
      assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be in any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 2, walker.getLevel());

      node = walker.nextNode();
      theNode = node;
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "node", node.getName());
      assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be in any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 2, walker.getLevel());

      for (int i = 0; i < 3; i++) {
         node = walker.nextNode();
         assertNotNull("node must not be null", node);
         assertEquals("Node name", "data", node.getName());
         assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
         assertNull("Must not be in any filter", walker.getCurrentFilterName());
         assertEquals("Node level", 3, walker.getLevel());
      }

      node = walker.nextNode();
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "y:ProxyAutoBoundsNode", node.getPrefixedName());
      assertTrue("Must be in ProxyAutoBoundsNode filter", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNotNull("Must define ProxyAutoBoundsNode filter", walker.getCurrentFilterName());
      assertEquals("Must define ProxyAutoBoundsNode filter", AUTOBOUND_FILTER, walker.getCurrentFilterName());
      assertEquals("Node level", 4, walker.getLevel());

      node = walker.nextNode();
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "y:Realizers", node.getPrefixedName());
      assertTrue("Must be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be in any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 5, walker.getLevel());

      for (int i = 0; i < 18; i++) {
         node = walker.nextNode();
         assertNotNull("node must not be null", node);
         assertTrue("Must be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
         assertNull("Must not be in any filter", walker.getCurrentFilterName());
      }

      node = walker.nextNode();
      assertNotNull("node must not be null", node);
      assertEquals("Node name", "graph", node.getName());
      assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be in any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 3, walker.getLevel());
   }

   /**
    * Test of setting the current node.
    */
   @Test
   @Order(order = 3)
   public void testSetCurrentNode() {
      System.out.println("XMLTreeWalkerTest : testSetCurrentNode");
      assertNotNull("Walker must not be null", walker);
      assertNotNull("Node must not be null", theNode);
      walker.setCurrentNode(theNode);
      assertFalse("Must not be in parent tag", walker.inParentFilter(AUTOBOUND_FILTER));
      assertNull("Must not be define any filter", walker.getCurrentFilterName());
      assertEquals("Node level", 2, walker.getLevel());
   }

}
