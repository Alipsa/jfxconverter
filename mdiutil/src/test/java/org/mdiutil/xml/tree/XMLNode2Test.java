/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * test the XMLNode.
 *
 * @version 1.2.39.4
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNode2Test {

   public XMLNode2Test() {
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
   public void testNode() {
      System.out.println("XMLNode2Test : testNode");
      URL xmlURL = XMLNode.class.getResource("resources/xmlNode.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      assertNotNull("XMLNode must not be null", root);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\" name=\"name,\">").append("\n");
      buf.append("  <element name=\"first,\">").append("\n");
      buf.append("    <element name=\"second,\"/>").append("\n");
      buf.append("    <element name=\"third,\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }
}
