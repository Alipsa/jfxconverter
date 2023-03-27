/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.*;
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
public class XMLSAXParser8KOTest extends ResolverSAXHandler {
   private boolean foundChild = false;

   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      super.startElement(uri, localname, qname, attr);
      if (qname.equals("child")) {
         foundChild = true;
      }
   }

   /**
    * Check that an XML document is well formed.
    * TODO: test does not work on linux (Must have one SAXParserException expected:<1> but was:<3>)
    */
   @Ignore
   @Test
   public void testParseWithValidation() throws IOException {
      System.out.println("XMLSAXParser8KOTest : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      URL xsdURL = XMLSAXParser8KOTest.class.getResource("resources/testIncludeXSD.xsd");
      parser.setValidating(true);
      parser.setXIncludeAware(true);
      parser.setConcatenateIncludes(true);
      parser.setSchema(xsdURL);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setHandler(this);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser8KOTest.class.getResource("resources/testIncludeParentXSDKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertEquals("Must have one SAXParserException", 1, exceptions.size());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }
}
