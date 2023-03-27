/*------------------------------------------------------------------------------
* Copyright (C) 2018, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
 * test the XMLNode.
 *
 * @version 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeTest {

   public XMLNodeTest() {
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
    * Test of copy method, of class XMLNode.
    */
   @Test
   public void testCopy() {
      System.out.println("XMLNodeTest : testCopy");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      XMLNode copy = root.copy();

      String xml = XMLNodeUtilities.print(copy, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      assertFalse("Copy must not be == to the copied node", copy == root);
      assertTrue("Copy must be equals to the copied node", copy.equals(root));
      assertFalse("Copy must not be equal to the copied node", copy.getFirstChild() == root.getFirstChild());
   }

   /**
    * Test of copy method, of class XMLNode.
    */
   @Test
   public void testCopy2() {
      System.out.println("XMLNodeTest : testCopy2");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint.xml");
      XMLRoot root = XMLNodeUtilities.getRootNode(xmlURL);
      XMLNode node = root.getChildren().get(0);

      XMLNode copy = node.copy();

      String xml = XMLNodeUtilities.print(copy, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<element name=\"first\">").append("\n");
      buf.append("  <element name=\"second\"/>").append("\n");
      buf.append("  <element name=\"third\"/>").append("\n");
      buf.append("</element>");
      assertEquals("XML content", buf.toString(), xml);

      assertFalse("Copy must not be == to the copied node", copy == node);
      assertTrue("Copy must be equals to the copied node", copy.equals(node));
      assertFalse("Copy must not be equal to the copied node", copy.getFirstChild() == root.getFirstChild());
   }

   /**
    * Test of copy method, of class XMLNode.
    */
   @Test
   public void testCopy3() {
      System.out.println("XMLNodeTest : testCopy3");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint7.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      XMLNode copy = root.copy();

      String xml = XMLNodeUtilities.print(copy, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">the first element<element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\">the third element</element>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      assertFalse("Copy must not be == to the copied node", copy == root);
      assertTrue("Copy must be equals to the copied node", copy.equals(root));
      assertFalse("Copy must not be equal to the copied node", copy.getFirstChild() == root.getFirstChild());
   }

   /**
    * Test of equals method, of class XMLNode.
    */
   @Test
   public void testEquals() {
      System.out.println("XMLNodeTest : testEquals");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint7.xml");
      XMLNode nodeWithSpace = XMLNodeUtilities.getRootNode(xmlURL);
      XMLNode nodeWithoutSpace = XMLNodeUtilities.getRootNode(xmlURL);

      assertTrue("Nodes must be equal", nodeWithSpace.equals(nodeWithoutSpace));
   }

   /**
    * Test of equals method, of class XMLNode.
    */
   @Test
   public void testEquals2() {
      System.out.println("XMLNodeTest : testEquals2");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint7.xml");
      XMLNode nodeWithSpace = XMLNodeUtilities.getRootNode(xmlURL, false, false, true, false);
      XMLNode nodeWithoutSpace = XMLNodeUtilities.getRootNode(xmlURL, false, false, true, false);

      assertTrue("Nodes must be equal", nodeWithSpace.equals(nodeWithoutSpace));
   }

   /**
    * Test of equals method, of class XMLNode.
    */
   @Test
   public void testEquals3() {
      System.out.println("XMLNodeTest : testEquals3");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint7.xml");
      XMLNode nodeWithSpace = XMLNodeUtilities.getRootNode(xmlURL, false, false, true, false);
      XMLNode nodeWithoutSpace = XMLNodeUtilities.getRootNode(xmlURL);

      assertFalse("Nodes must not be equal", nodeWithSpace.equals(nodeWithoutSpace));
      assertFalse("Nodes must not be equal", nodeWithoutSpace.equals(nodeWithSpace));
   }

   /**
    * Test of equals method, of class XMLNode.
    */
   @Test
   public void testEquals4() {
      System.out.println("XMLNodeTest : testEquals4");
      URL xmlURL = XMLNode.class.getResource("resources/xmlPrint7.xml");
      int preserve = XMLNodeUtilities.PRESERVE_SPACE;
      XMLNode nodeWithSpace = XMLNodeUtilities.getRootNode(xmlURL, preserve);
      XMLNode nodeWithoutSpace = XMLNodeUtilities.getRootNode(xmlURL);

      assertFalse("Nodes must not be equal", nodeWithSpace.equals(nodeWithoutSpace));
      assertFalse("Nodes must not be equal", nodeWithoutSpace.equals(nodeWithSpace));
   }

   /**
    * Test of getAllChildren method, of class XMLNode.
    */
   @Test
   public void testGetAllChildren() {
      System.out.println("XMLNodeTest : testGetAllChildren");
      URL xmlURL = XMLNode.class.getResource("resources/allChildren.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      List<XMLNode> list = root.getAllChildren("element");
      assertFalse("Must not be empty", list.isEmpty());
      assertEquals("Must contain 2 elements", 2, list.size());
   }
}
