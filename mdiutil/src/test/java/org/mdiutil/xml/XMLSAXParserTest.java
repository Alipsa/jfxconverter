/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.io.FilteredBufferedReader;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParserTest {

   public XMLSAXParserTest() {
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
   public void testParseWithoutValidation() {
      System.out.println("XMLSAXParserTest : testParseWithoutValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserTest.class.getResource("resources/simpleXMLWithoutDTD.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a Reader.
    */
   @Test
   public void testParseWithReader() throws FileNotFoundException {
      System.out.println("XMLSAXParserTest : testParseWithReader");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserTest.class.getResource("resources/simpleXMLWithoutDTD.xml");
      File file = new File(xmlURL.getFile());
      Reader reader = new FileReader(file);
      parser.parse(reader);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a Reader.
    */
   @Test
   public void testParseWithReader2() throws IOException {
      System.out.println("XMLSAXParserTest : testParseWithReader2");

      URL xmlURL = XMLSAXParserTest.class.getResource("resources/simpleXMLWithoutDTD2.xml");
      BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(xmlURL.openStream())) {
         public String filterLine(String line) {
            line = line.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
            return line;
         }
      };

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.parse(reader);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a File.
    */
   @Test
   public void testParseWithFile() {
      System.out.println("XMLSAXParserTest : testParseWithFile");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserTest.class.getResource("resources/simpleXMLWithoutDTD.xml");
      File file = new File(xmlURL.getFile());
      parser.parse(file);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }
}
