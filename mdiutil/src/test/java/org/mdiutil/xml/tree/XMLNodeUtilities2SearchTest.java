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
 * Test the XMLNodeUtilities2 search method.
 *
 * @since 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilities2SearchTest {

   public XMLNodeUtilities2SearchTest() {
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearchDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL);
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch2Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      assertEquals("encoding", "UTF-8", root.getEncoding());
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch3Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch3NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      List<XMLNode> list = utils.search(root, "element2", false);
      assertEquals("list size", 0, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch3NotDeep2() {
      System.out.println("XMLNodeUtilities2SearchTest : testSearch3NotDeep2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode node = utils.getRootNode(xmlURL).getFirstChild();
      List<XMLNode> list = utils.search(node, "element2", false);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch4Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch5Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL);
      root = root.getFirstChild().getFirstChild();
      root = root.getLastChild();
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch6Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL);
      root = root.getFirstChild().getFirstChild();
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch7Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch3.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      assertNull("encoding", root.getEncoding());
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch7NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch3.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      assertNull("encoding", root.getEncoding());
      List<XMLNode> list = utils.search(root, "element2", false);
      assertEquals("list size", 0, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch8Deep() {
      System.out.println("XMLNodeUtilities2SearchTest : testSearch8Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch8NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = utils.search(root, "element2", false);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch9Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = utils.search(root, "element2", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch9NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch2.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL).getFirstChild().getFirstChild();
      List<XMLNode> list = utils.search(root, "element2", false);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch10Deep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch4.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL);
      List<XMLNode> list = utils.search(root, "element1", true);
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
      System.out.println("XMLNodeUtilities2SearchTest : testSearch10NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch4.xml");

      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL).getFirstChild();
      List<XMLNode> list = utils.search(root, "element1", false);
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
