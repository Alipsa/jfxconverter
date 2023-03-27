/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

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
 * Unit tests for the XMLSAXParser class. The test handle cases where a parent file contains XInclude children.
 *
 * @since 0.9.54
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser11Test extends ResolverSAXHandler {
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
    * TODO: xinclude does not work
    */
   @Ignore
   @Test
   public void testParseWithoutValidation() {
      System.out.println("XMLSAXParser11Test : testParseWithoutValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setEntityListResolver(new EntityListResolver());
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setHandler(this);
      parser.setXIncludeAware(true);
      parser.setConcatenateIncludes(true);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser11Test.class.getResource("resources/testIncludeParent3.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
      assertTrue("Must have found child", foundChild);
   }

}
