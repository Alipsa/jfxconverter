/*------------------------------------------------------------------------------
* Copyright (C) 2016, 2017, 2020, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import org.mdiutil.xml.DefaultParserExceptionListener;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.SAXParseException;

/**
 * Test the XMLTreeHandler class.
 *
 * @version 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLTreeHandlerTest {

   public XMLTreeHandlerTest() {
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
    * Check the nodes tree.
    */
   @Test
   public void testParseWithoutValidation() {
      System.out.println("XMLTreeHandlerTest : testParseWithoutValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      XMLTreeHandler handler = new XMLTreeHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLTreeHandler.class.getResource("resources/simpleXMLWithoutDTD.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());

      XMLNode root = handler.getRoot();
      assertNotNull("Root must not be null", root);
      assertEquals("Number of attributes", 1, root.countAttributes());
      assertTrue("Must have an attribute for desc", root.hasAttribute("desc"));
      assertEquals("desc attribute", "example", root.getAttributeValue("desc"));
      assertEquals("Line number", -1, root.getLineNumber());
      assertEquals("Number of children", 1, root.countChildren());

      XMLNode child = root.getChildren().get(0);
      assertEquals("Number of attributes", 1, child.countAttributes());
      assertTrue("Must have an attribute for name", child.hasAttribute("name"));
      assertEquals("desc attribute", "first", child.getAttributeValue("name"));
      assertEquals("Line number", -1, child.getLineNumber());
      assertEquals("Number of children", 2, child.countChildren());

      XMLNode child1 = child.getChildren().get(0);
      assertEquals("Number of attributes", 1, child1.countAttributes());
      assertTrue("Must have an attribute for name", child1.hasAttribute("name"));
      assertEquals("desc attribute", "second", child1.getAttributeValue("name"));
      assertEquals("Number of children", 0, child1.countChildren());

      XMLNode child2 = child.getChildren().get(1);
      assertEquals("Number of attributes", 1, child2.countAttributes());
      assertTrue("Must have an attribute for name", child2.hasAttribute("name"));
      assertEquals("desc attribute", "third", child2.getAttributeValue("name"));
      assertEquals("Number of children", 0, child2.countChildren());
   }

   /**
    * Check the nodes tree with CDATA content.
    */
   @Test
   public void testParseWithoutValidation2() {
      System.out.println("XMLTreeHandlerTest : testParseWithoutValidation2");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      XMLTreeHandler handler = new XMLTreeHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint7.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());

      XMLNode root = handler.getRoot();
      assertNotNull("Root must not be null", root);
      assertNull("No CDATA", root.getCDATA());
      assertEquals("Number of attributes", 1, root.countAttributes());
      assertTrue("Must have an attribute for desc", root.hasAttribute("desc"));
      assertEquals("desc attribute", "example", root.getAttributeValue("desc"));
      assertEquals("Number of children", 1, root.countChildren());

      XMLNode child = root.getChildren().get(0);
      assertNotNull("CDATA", child.getCDATA());
      assertEquals("CDATA", "the first element", child.getCDATA());
      assertEquals("Number of attributes", 1, child.countAttributes());
      assertTrue("Must have an attribute for name", child.hasAttribute("name"));
      assertEquals("desc attribute", "first", child.getAttributeValue("name"));
      assertEquals("Number of children", 2, child.countChildren());

      XMLNode child1 = child.getChildren().get(0);
      assertNull("No CDATA", child1.getCDATA());
      assertEquals("Number of attributes", 1, child1.countAttributes());
      assertTrue("Must have an attribute for name", child1.hasAttribute("name"));
      assertEquals("desc attribute", "second", child1.getAttributeValue("name"));
      assertEquals("Number of children", 0, child1.countChildren());

      XMLNode child2 = child.getChildren().get(1);
      assertNotNull("CDATA", child.getCDATA());
      assertEquals("CDATA", "the third element", child2.getCDATA());
      assertEquals("Number of attributes", 1, child2.countAttributes());
      assertTrue("Must have an attribute for name", child2.hasAttribute("name"));
      assertEquals("desc attribute", "third", child2.getAttributeValue("name"));
      assertEquals("Number of children", 0, child2.countChildren());
   }

   /**
    * Check the nodes tree with CDATA content and the preserveSpace property.
    */
   @Test
   public void testParseWithoutValidation3() {
      System.out.println("XMLTreeHandlerTest : testParseWithoutValidation3");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      XMLTreeHandler handler = new XMLTreeHandler();
      handler.preserveSpace(true);
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint7.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());

      XMLNode root = handler.getRoot();
      assertNotNull("Root must not be null", root);
      assertNotNull("No CDATA", root.getCDATA());
      assertEquals("CDATA", "\n  ", root.getCDATA());
      assertEquals("Number of attributes", 1, root.countAttributes());
      assertTrue("Must have an attribute for desc", root.hasAttribute("desc"));
      assertEquals("desc attribute", "example", root.getAttributeValue("desc"));
      assertEquals("Number of children", 1, root.countChildren());

      XMLNode child = root.getChildren().get(0);
      assertNotNull("CDATA", child.getCDATA());
      assertEquals("CDATA", "the first element\n    ", child.getCDATA());
      assertEquals("Number of attributes", 1, child.countAttributes());
      assertTrue("Must have an attribute for name", child.hasAttribute("name"));
      assertEquals("desc attribute", "first", child.getAttributeValue("name"));
      assertEquals("Number of children", 2, child.countChildren());

      XMLNode child1 = child.getChildren().get(0);
      assertNull("No CDATA", child1.getCDATA());
      assertEquals("Number of attributes", 1, child1.countAttributes());
      assertTrue("Must have an attribute for name", child1.hasAttribute("name"));
      assertEquals("desc attribute", "second", child1.getAttributeValue("name"));
      assertEquals("Number of children", 0, child1.countChildren());

      XMLNode child2 = child.getChildren().get(1);
      assertNotNull("CDATA", child.getCDATA());
      assertEquals("CDATA", "the third element", child2.getCDATA());
      assertEquals("Number of attributes", 1, child2.countAttributes());
      assertTrue("Must have an attribute for name", child2.hasAttribute("name"));
      assertEquals("desc attribute", "third", child2.getAttributeValue("name"));
      assertEquals("Number of children", 0, child2.countChildren());
   }
}
