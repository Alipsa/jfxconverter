/*------------------------------------------------------------------------------
* Copyright (C) 2021, 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;
import org.mdiutil.xml.XMLTestUtilities;

/**
 * Test the XMLNodeUtilities.
 *
 * @version 1.2.34
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesNamespace1Test {

   public XMLNodeUtilitiesNamespace1Test() {
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
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode() {
      System.out.println("XMLNodeUtilitiesNamespace1Test : testGetRootNode");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrintNamespace1.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      assertNotNull("root must not be null", root);
   }

   /**
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode2() throws IOException {
      System.out.println("XMLNodeUtilitiesNamespace1Test : testGetRootNode2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrintNamespace1.xml");
      String content = XMLTestUtilities.getText(xmlURL);
      XMLNode root = XMLNodeUtilities.getRootNode(content);
      assertNotNull("root must not be null", root);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch() {
      System.out.println("XMLNodeUtilitiesNamespace1Test : testSearch");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearchNamespace.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "h:element2", true);
      assertEquals("list size", 2, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element2", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "second", attrValue);

      XMLNode node2 = list.get(1);
      assertEquals("Node name", "element2", node2.getName());
      attrValue = node2.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "third", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch2() {
      System.out.println("XMLNodeUtilitiesNamespace1Test : testSearch2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearchNamespace.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "h:element2", false);
      assertEquals("list size", 0, list.size());
   }
}
