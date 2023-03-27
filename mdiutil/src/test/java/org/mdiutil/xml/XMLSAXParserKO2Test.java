/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.mdiutil.io.ReaderProvider;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class.
 *
 * @version 0.9.55
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParserKO2Test {

   /**
    * Check that an XML document is valid.
    */
   @Test
   public void testParseWithValidation() throws IOException {
      System.out.println("XMLSAXParserKO2Test : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      URL schemaURL = XMLSAXParserKO2Test.class.getResource("resources/test2.xsd");
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(false);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParserKO2Test.class.getResource("resources/testKO.xml");
      try (InputStreamReader reader = new InputStreamReader(xmlURL.openStream())) {
         parser.parse(reader);
         List<SAXParseException> exceptions = parser.getHandler().getExceptions();
         assertTrue("Must have no SAXParserException", exceptions.isEmpty());
         assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      }
   }

   /**
    * Check that an XML document is valid.
    */
   @Test
   public void testParseWithValidation2() throws IOException {
      System.out.println("XMLSAXParserKO2Test : testParseWithValidation2");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      URL schemaURL = XMLSAXParserKO2Test.class.getResource("resources/test2.xsd");
      parser.setSchema(schemaURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(false);
      parser.showWarnings(true);
      final URL xmlURL = XMLSAXParserKO2Test.class.getResource("resources/testKO.xml");
      ReaderProvider provider = new ReaderProvider() {
         @Override
         public Reader newReader() {
            InputStreamReader reader = null;
            try {
               reader = new InputStreamReader(xmlURL.openStream());
               return reader;
            } catch (IOException ex) {
               ex.printStackTrace();
            }
            return reader;
         }
      };
      parser.parse(provider);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertFalse("Must have one SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }
}
