/*------------------------------------------------------------------------------
* Copyright (C) 2018, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;
import org.mdiutil.xml.tree.XMLTreeReplacer.NodePath;

/**
 * Test the XMLTreeHandler class.
 *
 * @version 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLTreeReplacerTest {

   public XMLTreeReplacerTest() {
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
    * Test of replace method.
    */
   @Test
   public void testReplace() throws IOException {
      System.out.println("XMLTreeReplacerTest : testReplace");
      URL xmlURL = XMLTreeReplacerTest.class.getResource("resources/xmlPrint.xml");
      XMLTreeReplacer replacer = new XMLTreeReplacer();
      XMLTreeReplacer.Node node = new XMLTreeReplacer.Node("root");
      XMLTreeReplacer.Node node1 = new XMLTreeReplacer.Node("element");
      node1.setAttribute("name", "changed");
      XMLTreeReplacer.Node node2 = new XMLTreeReplacer.Node("element", 1);
      node2.setAttribute("name", "changed2");
      List<XMLTreeReplacer.Node> tree = new ArrayList<>();
      tree.add(node);
      tree.add(node1);
      tree.add(node2);
      NodePath path = new NodePath(tree);
      File file = File.createTempFile("testMDIUtilities", ".xml");
      replacer.replace(xmlURL, file.toURI().toURL(), path);

      XMLNode root = XMLNodeUtilities.getRootNode(file.toURI().toURL());

      assertNotNull("Root node", root);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"changed\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"changed2\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of replace method.
    */
   @Test
   public void testReplace2() throws IOException {
      System.out.println("XMLTreeReplacerTest : testReplace2");
      URL xmlURL = XMLTreeReplacerTest.class.getResource("resources/xmlPrint2.xml");
      XMLTreeReplacer replacer = new XMLTreeReplacer();
      XMLTreeReplacer.Node node = new XMLTreeReplacer.Node("root");
      XMLTreeReplacer.Node node1 = new XMLTreeReplacer.Node("element");
      node1.setAttribute("name", "changed");
      XMLTreeReplacer.Node node2 = new XMLTreeReplacer.Node("element", "id", "theID2");
      node2.setAttribute("name", "changed2");
      List<XMLTreeReplacer.Node> tree = new ArrayList<>();
      tree.add(node);
      tree.add(node1);
      tree.add(node2);
      NodePath path = new NodePath(tree);
      File file = File.createTempFile("testMDIUtilities", ".xml");
      replacer.replace(xmlURL, file.toURI().toURL(), path);

      XMLNode root = XMLNodeUtilities.getRootNode(file.toURI().toURL());

      assertNotNull("Root node", root);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element id=\"theID\" name=\"changed\">").append("\n");
      buf.append("    <element id=\"theID2\" name=\"changed2\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of replace method.
    */
   @Test
   public void testReplace3() throws IOException {
      System.out.println("XMLTreeReplacerTest : testReplace3");
      URL xmlURL = XMLTreeReplacerTest.class.getResource("resources/xmlPrint.xml");
      XMLTreeReplacer replacer = new XMLTreeReplacer();

      XMLTreeReplacer.Node node = new XMLTreeReplacer.Node("root");
      XMLTreeReplacer.Node node1 = new XMLTreeReplacer.Node("element");
      node1.setAttribute("name", "changed");
      List<XMLTreeReplacer.Node> tree = new ArrayList<>();
      tree.add(node);
      tree.add(node1);
      NodePath path = new NodePath(tree);

      node = new XMLTreeReplacer.Node("root");
      node1 = new XMLTreeReplacer.Node("element");
      XMLTreeReplacer.Node node2 = new XMLTreeReplacer.Node("element", 1);
      node2.setAttribute("name", "changed2");
      tree = new ArrayList<>();
      tree.add(node);
      tree.add(node1);
      tree.add(node2);
      NodePath path2 = new NodePath(tree);
      File file = File.createTempFile("testMDIUtilities", ".xml");

      replacer.replace(xmlURL, file.toURI().toURL(), path, path2);

      XMLNode root = XMLNodeUtilities.getRootNode(file.toURI().toURL());

      assertNotNull("Root node", root);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"changed\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"changed2\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of replace method.
    */
   @Test
   public void testReplace4() throws IOException {
      System.out.println("XMLTreeReplacerTest : testReplace4");
      URL xmlURL = XMLTreeReplacerTest.class.getResource("resources/xmlPrint.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      XMLTreeReplacer replacer = new XMLTreeReplacer();
      XMLTreeReplacer.Node node = new XMLTreeReplacer.Node("root");
      XMLTreeReplacer.Node node1 = new XMLTreeReplacer.Node("element");
      node1.setAttribute("name", "changed");
      XMLTreeReplacer.Node node2 = new XMLTreeReplacer.Node("element", 1);
      node2.setAttribute("name", "changed2");
      List<XMLTreeReplacer.Node> tree = new ArrayList<>();
      tree.add(node);
      tree.add(node1);
      tree.add(node2);
      NodePath path = new NodePath(tree);
      XMLNode rootResult = replacer.replace(root, path);
      assertFalse("XML content", rootResult == root);
      assertNotNull("Root node", rootResult);

      String xml = XMLNodeUtilities.print(rootResult, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"changed\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"changed2\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of replace method.
    */
   @Test
   public void testReplace5() throws IOException {
      System.out.println("XMLTreeReplacerTest : testReplace5");
      URL xmlURL = XMLTreeReplacerTest.class.getResource("resources/xmlPrint3.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      XMLTreeReplacer replacer = new XMLTreeReplacer();
      XMLTreeReplacer.Node node = new XMLTreeReplacer.Node("root");
      XMLTreeReplacer.Node node1 = new XMLTreeReplacer.Node("element", "name", "second");
      XMLTreeReplacer.Node node2 = new XMLTreeReplacer.Node("element2", 0);
      node2.setAttribute("name", "changed");
      NodePath path = new NodePath(node, node1, node2);
      XMLNode rootResult = replacer.replace(root, path);
      assertFalse("XML content", rootResult == root);
      assertNotNull("Root node", rootResult);

      String xml = XMLNodeUtilities.print(rootResult, 2);
      StringBuilder buf = new StringBuilder();

      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element id=\"theID\" name=\"first\"/>").append("\n");
      buf.append("  <element name=\"second\">").append("\n");
      buf.append("    <element2 name=\"changed\"/>").append("\n");
      buf.append("    <element2 name=\"thisName2\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("  <element name=\"third\">").append("\n");
      buf.append("    <element2 name=\"thisName\"/>").append("\n");
      buf.append("    <element2 name=\"thisName2\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }
}
