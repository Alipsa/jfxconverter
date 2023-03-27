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

import org.junit.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class. The test handle cases where a parent file contains XInclude children
 * which are incorrect.
 *
 * @since 0.9.54
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser7KOTest extends ResolverSAXHandler {
   private boolean foundChild = false;

   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      super.startElement(uri, localname, qname, attr);
      if (qname.equals("child")) {
         foundChild = true;
      }

   }

   /**
    * Check that an XML document is not well formed.
    * TODO: xinclude does not work
    */
   @Ignore
   @Test
   public void testParseWithoutValidation() {
      System.out.println("XMLSAXParser7KOTest : testParseWithoutValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setEntityListResolver(new EntityListResolver());
      parser.setValidating(true);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setHandler(this);
      parser.setXIncludeAware(true);
      parser.setConcatenateIncludes(true);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser7KOTest.class.getResource("resources/testIncludeParentKO.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertEquals("Must have one SAXParserException", 1, exceptions.size());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      assertFalse("Must not have found child", foundChild);
   }

}
