/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test the XMLNodeUtilities print method with comments.
 *
 * @version 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesPrintCommentsTest {

   public XMLNodeUtilitiesPrintCommentsTest() {
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
    * Test of print method.
    */
   @Test
   public void testPrint() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint");
      XMLRoot root = new XMLCommentedRoot("root", "the root comment");
      root.addAttribute("desc", "example");
      XMLNode node = new XMLCommentedNode("element", "the node comment");
      node.addAttribute("name", "first");
      root.addChild(node);
      node = new XMLNode("element");
      node.addAttribute("name", "second");
      root.addChild(node);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<!-- the root comment -->").append("\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <!-- the node comment -->").append("\n");
      buf.append("  <element name=\"first\"/>").append("\n");
      buf.append("  <element name=\"second\"/>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }
}
