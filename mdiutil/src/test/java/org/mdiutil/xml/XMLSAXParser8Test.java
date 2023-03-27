/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class. The test handle cases where a parent file contains XInclude children.
 *
 * @since 1.2.7.5
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser8Test extends ResolverSAXHandler {
   public XMLSAXParser8Test() {
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

   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      super.startElement(uri, localname, qname, attr);
   }

   /**
    * Check parsing with a StringReader.
    */
   @Test
   public void testParseWithStringReader() throws IOException {
      System.out.println("XMLSAXParser8Test : testParseWithStringReader");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserTest.class.getResource("resources/simpleXMLWithoutDTD.xml");
      String content = XMLTestUtilities.getText(xmlURL);
      Reader reader = new StringReader(content);
      parser.parse(reader);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check validating parsing with a StringReader and a Schema.
    */
   @Test
   public void testParseWithValidation() throws IOException {
      System.out.println("XMLSAXParser8Test : testParseWithValidation");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2.xml");
      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      String content = XMLTestUtilities.getText(xmlURL);
      Reader reader = new StringReader(content);

      parser.parse(reader);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

}
