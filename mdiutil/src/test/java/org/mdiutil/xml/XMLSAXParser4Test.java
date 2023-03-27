/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017 Herve Girod
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
import org.mdiutil.io.BufferedReaderFactory;
import org.mdiutil.io.FilteredBufferedReader;
import org.mdiutil.io.ReaderProvider;
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
public class XMLSAXParser4Test {

   public XMLSAXParser4Test() {
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
    * Check validating parsing with a Schema.
    */
   @Test
   public void testParseWithValidation() {
      System.out.println("XMLSAXParser4Test : testParseWithValidation");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2.xml");
      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a Reader.
    */
   @Test
   public void testParseWithValidationReader() throws FileNotFoundException {
      System.out.println("XMLSAXParser4Test : testParseWithValidationReader");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2.xml");
      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      final File file = new File(xmlURL.getFile());
      ReaderProvider provider = new ReaderProvider() {
         @Override
         public Reader newReader() {
            Reader reader = null;
            try {
               reader = new FileReader(file);
            } catch (FileNotFoundException ex) {
               ex.printStackTrace();
            }
            return reader;
         }
      };
      parser.parse(provider);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a Reader.
    */
   @Test
   public void testParseWithValidationReader2() throws IOException {
      System.out.println("XMLSAXParser4Test : testParseWithValidationReader2");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      final URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2filter.xml");

      ReaderProvider provider = new ReaderProvider() {
         @Override
         public Reader newReader() {
            BufferedReader reader = null;
            try {
               reader = new FilteredBufferedReader(new InputStreamReader(xmlURL.openStream())) {
                  public String filterLine(String line) {
                     line = line.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
                     return line;
                  }
               };
            } catch (IOException ex) {
               ex.printStackTrace();
            }
            return reader;
         }
      };

      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.parse(provider);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a File.
    */
   @Test
   public void testParseWithFile() {
      System.out.println("XMLSAXParser4Test : testParseWithValidationFile");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2.xml");
      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      File file = new File(xmlURL.getFile());
      parser.parse(file);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with a File.
    */
   @Test
   public void testParseWithFile2() throws IOException {
      System.out.println("XMLSAXParser4Test : testParseWithFile2");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2filter.xml");
      File file = new File(xmlURL.getFile());

      BufferedReaderFactory bufFac = new BufferedReaderFactory() {
         @Override
         public BufferedReader createReader(Reader in, int sz) {
            FilteredBufferedReader reader = new FilteredBufferedReader(in, sz) {
               @Override
               public String filterLine(String line) {
                  line = line.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
                  return line;
               }
            };
            return reader;
         }
      };

      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.parse(file, bufFac);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Check parsing with an URL.
    */
   @Test
   public void testParseWithURL() throws IOException {
      System.out.println("XMLSAXParser4Test : testParseWithURL");

      URL schemaURL = XMLSAXParser4Test.class.getResource("resources/test4.xsd");
      URL xmlURL = XMLSAXParser4Test.class.getResource("resources/simpleXML2filter.xml");

      BufferedReaderFactory bufFac = new BufferedReaderFactory() {
         @Override
         public BufferedReader createReader(Reader in, int sz) {
            FilteredBufferedReader reader = new FilteredBufferedReader(in, sz) {
               @Override
               public String filterLine(String line) {
                  line = line.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
                  return line;
               }
            };
            return reader;
         }
      };

      XMLSAXParser parser = new XMLSAXParser();
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      parser.parse(xmlURL, bufFac);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }
}
