/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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

/**
 * Test ResolverSAXHandler class.
 *
 * @since 1.2.25.5
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class ResolverSAXHandler3Test {
   public ResolverSAXHandler3Test() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   @Test
   public void testSimpleKO() throws Exception {
      System.out.println("ResolverSAXHandler2 : testSimpleKO");

      SAXParserFactory fac = SAXParserFactory.newInstance();
      SAXParser parser = fac.newSAXParser();
      ResolverSAXHandler handler = new MySAXHandler();
      InputStream stream = ResolverSAXHandler3Test.class.getResourceAsStream("resources/simpleXML2.xml");
      parser.parse(stream, handler);
      if (handler.getStatus() == ResolverSAXHandler.WARNINGS) {
         fail("fail to parse: must not have Exceptions");
      }
      List<ResolverSAXHandler.ExceptionResult> list = handler.getExceptionResults();
      assertEquals("Must have 1 result", 1, list.size());
      ResolverSAXHandler.ExceptionResult result = list.get(0);
      assertTrue("Must have 1 information", result instanceof ResolverSAXHandler.InformationResult);
      ResolverSAXHandler.InformationResult info = (ResolverSAXHandler.InformationResult) result;
      assertEquals("Message", "The Info", info.getMessage());
      assertFalse("Must no have a property", info.hasAdditionalProperties());
   }

   public class MySAXHandler extends ResolverSAXHandler {
      @Override
      public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
         super.startElement(uri, localname, qname, attr);
         if (qname.equals("element")) {
            this.info("The Info");
         }
      }
   }
}
