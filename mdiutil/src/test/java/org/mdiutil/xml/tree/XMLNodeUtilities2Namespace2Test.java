/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test the XMLNodeUtilities2.
 *
 * @since 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilities2Namespace2Test {

   public XMLNodeUtilities2Namespace2Test() {
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
      System.out.println("XMLNodeUtilities2Namespace2Test : testPrint");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrintNamespace1.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode root = utils.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\" xmlns:h=\"http://www.test.org/\">").append("\n");
      buf.append("  <h:element name=\"first\">").append("\n");
      buf.append("    <h:element name=\"second\"/>").append("\n");
      buf.append("    <h:element name=\"third\"/>").append("\n");
      buf.append("  </h:element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }
}
