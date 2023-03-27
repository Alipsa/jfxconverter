/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

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
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class.
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParserKOTest {

   public XMLSAXParserKOTest() {
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
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation() {
      System.out.println("XMLSAXParserKOTest : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      URL schemaURL = XMLSAXParserKOTest.class.getResource("resources/test2.xsd");
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(false);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserKOTest.class.getResource("resources/testKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation2() {
      System.out.println("XMLSAXParserKOTest : testParseWithValidation2");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      URL schemaURL = XMLSAXParserKOTest.class.getResource("resources/test2.xsd");
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(false);
      parser.showWarnings(true);
      parser.setExceptionListener(listener);
      URL xmlURL = XMLSAXParserKOTest.class.getResource("resources/testKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptionResults().isEmpty());
   }

   /**
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation2Config() {
      System.out.println("XMLSAXParserKOTest : testParseWithValidation2Config");

      XMLSAXParser parser = new XMLSAXParser();
      URL schemaURL = XMLSAXParserKOTest.class.getResource("resources/test2.xsd");
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      XMLParserConfiguration config = new XMLParserConfiguration();
      config.isValidating = true;
      config.schemaURL = schemaURL;
      parser.setHandler(handler);
      config.isShowingExceptions = false;
      config.isShowingWarnings = true;
      config.exceptionListener = listener;
      parser.setParserConfiguration(config);
      URL xmlURL = XMLSAXParserKOTest.class.getResource("resources/testKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptionResults().isEmpty());
   }

   /**
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation3() {
      System.out.println("XMLSAXParserKOTest : testParseWithValidation3");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      URL schemaURL = XMLSAXParserKOTest.class.getResource("resources/test2.xsd");
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.setExceptionListener(listener);
      URL xmlURL = XMLSAXParserKOTest.class.getResource("resources/testKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no Exception", listener.getExceptions().isEmpty());
      assertEquals("Must have one SAXException", 1, listener.getExceptionResults().size());
   }

   /**
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation3Config() {
      System.out.println("XMLSAXParserKOTest : testParseWithValidation3Config");

      XMLSAXParser parser = new XMLSAXParser();
      URL schemaURL = XMLSAXParserKOTest.class.getResource("resources/test2.xsd");
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      XMLParserConfiguration config = new XMLParserConfiguration();
      config.isValidating = true;
      config.schemaURL = schemaURL;
      config.isShowingExceptions = true;
      config.isShowingWarnings = true;
      config.exceptionListener = listener;
      parser.setParserConfiguration(config);
      URL xmlURL = XMLSAXParserKOTest.class.getResource("resources/testKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no Exception", listener.getExceptions().isEmpty());
      assertEquals("Must have one SAXException", 1, listener.getExceptionResults().size());
   }
}
