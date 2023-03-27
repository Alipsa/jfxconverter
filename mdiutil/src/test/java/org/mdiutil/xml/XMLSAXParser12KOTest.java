/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class.
 *
 * @since 0.9.54
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser12KOTest extends ResolverSAXHandler {
   private boolean foundChild = false;

   public XMLSAXParser12KOTest() {
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
      if (qname.equals("child")) {
         foundChild = true;
      }
   }

   /**
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithValidation() {
      System.out.println("XMLSAXParser9KOTest : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      URL xsdURL = XMLSAXParser12KOTest.class.getResource("resources/testIncludeXSD2.xsd");
      parser.setValidating(true);
      parser.setXIncludeAware(true);
      parser.setConcatenateIncludes(true);
      parser.setSchema(xsdURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setHandler(this);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser12KOTest.class.getResource("resources/testKO2.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertEquals("Must have one SAXParserException", 1, exceptions.size());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      assertFalse("Must not have found child", foundChild);
   }
}
