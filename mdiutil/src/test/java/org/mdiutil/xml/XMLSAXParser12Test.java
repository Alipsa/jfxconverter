/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class. The test handle cases where a parent file contains XInclude children.
 *
 * @since 0.9.54
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser12Test extends ResolverSAXHandler {
   public XMLSAXParser12Test() {
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
    * Check that an XML document is well formed.
    */
   @Test
   public void testParseWithoutValidation() throws IOException {
      System.out.println("XMLSAXParser12Test : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setEntityListResolver(new EntityListResolver());
      URL xsdURL = XMLSAXParser3Test.class.getResource("resources/types.xsd");
      parser.setValidating(true);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setHandler(this);
      parser.setXIncludeAware(true);
      parser.setConcatenateIncludes(true);
      parser.setSchema(xsdURL);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser12Test.class.getResource("resources/types.xml");
      parser.setDefaultBaseDirectory(FileUtilities.getParentURL(xmlURL));
      parser.parse(xmlURL.openStream());
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

}
