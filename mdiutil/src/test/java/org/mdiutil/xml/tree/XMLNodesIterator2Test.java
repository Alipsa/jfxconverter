/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;
import org.mdiutil.xml.DefaultParserExceptionListener;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.SAXParseException;

/**
 * Test the node iterator.
 *
 * @version 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodesIterator2Test {
   private static XMLNode root = null;
   private static Iterator<XMLNode> iterator = null;

   public XMLNodesIterator2Test() {
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
    * Parse the nodes tree.
    */
   @Test
   @Order(order = 1)
   public void testParse() {
      System.out.println("XMLNodesIterator2Test : testParse");
      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      XMLTreeHandler handler = new XMLTreeHandler();
      handler.setKeepLineNumbers(true);
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLTreeHandler.class.getResource("resources/testXMLIterator.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());

      root = handler.getRoot();
   }

   /**
    * Create the Iterator.
    */
   @Test
   @Order(order = 1)
   public void testCreateIterator() {
      System.out.println("XMLNodesIterator2Test : testCreateIterator");
      assertNotNull("Root node", root);
      iterator = root.iterator();
      assertNotNull("iterator", iterator);
   }

   /**
    * Iterate through the Iterator.
    */
   @Test
   @Order(order = 1)
   public void testIterate() {
      System.out.println("XMLNodesIterator2Test : testIterate");
      assertNotNull("iterator", iterator);
      assertTrue("hasNext", iterator.hasNext());
      XMLNode node = iterator.next();
      assertTrue("must be the root", root == node);

      assertTrue("hasNext", iterator.hasNext());
      node = iterator.next();
      assertNotNull("node", node);
      assertTrue("must have a name attribute", node.hasAttribute("name"));
      String name = node.getAttributeValue("name");
      assertEquals("Node name", "Title", name);
      XMLNode parent = node.getParent();
      assertTrue("parent must be the root", root == parent);

      assertTrue("hasNext", iterator.hasNext());
      node = iterator.next();
      assertNotNull("node", node);
      assertTrue("must have a name attribute", node.hasAttribute("name"));
      name = node.getAttributeValue("name");
      assertEquals("Node name", "Content", name);

      assertTrue("hasNext", iterator.hasNext());
      node = iterator.next();
      assertNotNull("node", node);
      assertTrue("must have a name attribute", node.hasAttribute("name"));
      name = node.getAttributeValue("name");
      assertEquals("Node name", "Content2", name);

      assertTrue("hasNext", iterator.hasNext());
      node = iterator.next();
      assertNotNull("node", node);
      assertTrue("must have a name attribute", node.hasAttribute("name"));
      name = node.getAttributeValue("name");
      assertEquals("Node name", "Children", name);

      assertTrue("hasNext", iterator.hasNext());
      node = iterator.next();
      assertNotNull("node", node);
      assertTrue("must have a name attribute", node.hasAttribute("name"));
      name = node.getAttributeValue("name");
      assertEquals("Node name", "Content3", name);

      assertFalse("hasNext", iterator.hasNext());
   }
}
