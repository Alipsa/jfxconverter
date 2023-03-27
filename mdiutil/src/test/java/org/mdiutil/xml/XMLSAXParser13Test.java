/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class. The test handle the usage of a default resource resolver.
 *
 * @since 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser13Test {
   public XMLSAXParser13Test() {
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
    * Check that the default resolver is correctly set.
    */
   @Test
   @Ignore
   public void testParseWithValidation() {
      System.out.println("XMLSAXParser13Test : testParseWithValidation");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(true);
      parser.setDefaultLSResourceResolver(true);
      URL schemaURL = XMLSAXParser13Test.class.getResource("resources/mySchemaxmllang.xsd");
      parser.setSchema(schemaURL);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser13Test.class.getResource("resources/myXMLMxmllang.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      List<ResolverSAXHandler.ExceptionResult> results = handler.getExceptionResults();
      assertTrue("Must have no SAXException", results.isEmpty());
   }

}
