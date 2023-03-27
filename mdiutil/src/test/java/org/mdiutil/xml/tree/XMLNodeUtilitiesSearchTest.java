/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

/**
 * Test the XMLNodeUtilities search method.
 *
 * @version 1.2.34
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesSearchTest {

   public XMLNodeUtilitiesSearchTest() {
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
    * Test of search method.
    */
   @Test
   public void testSearchDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearchDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
   public void testSearch2Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch2Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      assertEquals("encoding", "UTF-8", root.getEncoding());
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
   public void testSearch3Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch3Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch3NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch3NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", false);
      assertEquals("list size", 0, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch3NotDeep2() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch3NotDeep2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNode node = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(node, "element2", false);
      assertEquals("list size", 1, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element2", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "second", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch4Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch4Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch5Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch5Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      root = root.getFirstChild().getFirstChild();
      root = root.getLastChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
      assertEquals("list size", 1, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element2", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch6Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch6Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      root = root.getFirstChild().getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch7Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch7Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch3.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      assertNull("encoding", root.getEncoding());
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
   public void testSearch7NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch7NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch3.xml");

      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      assertNull("encoding", root.getEncoding());
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", false);
      assertEquals("list size", 0, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch8Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch8Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
      assertEquals("list size", 1, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element2", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "second", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch8NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch8NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", false);
      assertEquals("list size", 1, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element2", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "second", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch9Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch9Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", true);
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
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch9NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch9NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element2", false);
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
      assertEquals("Attribute value", "fith", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch10Deep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch10Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch4.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "element1", true);
      assertEquals("list size", 2, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element1", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "first", attrValue);

      XMLNode node2 = list.get(1);
      assertEquals("Node name", "element1", node2.getName());
      attrValue = node2.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "third", attrValue);
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch10NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchTest : testSearch10NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch4.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "element1", false);
      assertEquals("list size", 2, list.size());

      XMLNode node1 = list.get(0);
      assertEquals("Node name", "element1", node1.getName());
      String attrValue = node1.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "first", attrValue);

      XMLNode node2 = list.get(1);
      assertEquals("Node name", "element1", node2.getName());
      attrValue = node2.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "third", attrValue);
   }

}
