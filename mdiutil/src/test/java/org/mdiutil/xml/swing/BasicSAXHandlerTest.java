/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2016, 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.swing;

import static org.junit.Assert.fail;
import java.io.InputStream;
import java.net.URL;
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

/**
 * Test BasicSAXHandler class.
 *
 * @version 1.0
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class BasicSAXHandlerTest {
   public BasicSAXHandlerTest() {
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
   public void testSimpleOK() throws Exception {
      System.out.println("BasicSAXHandlerTest : testSimpleOK");

      SAXParserFactory fac = SAXParserFactory.newInstance();
      fac.setValidating(true);
      SAXParser parser = fac.newSAXParser();
      BasicSAXHandler handler = new BasicSAXHandler();
      URL dtd = BasicSAXHandlerTest.class.getResource("resources/simpleDTD.dtd");
      handler.setDTD(dtd, "publicDTD");
      InputStream stream = BasicSAXHandlerTest.class.getResourceAsStream("resources/simpleXML.xml");
      try {
         parser.parse(stream, handler);
         if (handler.getStatus() != BasicSAXHandler.NO_ERRORS) {
            fail("fail to parse: has Exceptions");
         }
      } catch (Exception e) {
         fail("fail to parse");
      }
   }

   @Test
   public void testXMLWithDTDDeclaration() throws Exception {
      System.out.println("BasicSAXHandlerTest : testXMLWithDTDDeclaration");

      SAXParserFactory fac = SAXParserFactory.newInstance();
      fac.setValidating(true);
      SAXParser parser = fac.newSAXParser();
      BasicSAXHandler handler = new BasicSAXHandler();
      URL dtd = BasicSAXHandlerTest.class.getResource("resources/simpleDTD.dtd");
      handler.setDTD(dtd, "publicDTD");
      InputStream stream = BasicSAXHandlerTest.class.getResourceAsStream("resources/simpleXMLWithDTD.xml");
      try {
         parser.parse(stream, handler);
         if (handler.getStatus() != BasicSAXHandler.NO_ERRORS) {
            fail("fail to parse: has Exceptions");
         }
      } catch (Exception e) {
         fail("fail to parse");
      }
   }

   @Test
   public void testSimpleKO() throws Exception {
      System.out.println("BasicSAXHandlerTest : testSimpleKO");

      SAXParserFactory fac = SAXParserFactory.newInstance();
      fac.setValidating(true);
      SAXParser parser = fac.newSAXParser();
      BasicSAXHandler handler = new BasicSAXHandler();
      URL dtd = BasicSAXHandlerTest.class.getResource("resources/simpleDTD.dtd");
      handler.setDTD(dtd, "publicDTD");
      InputStream stream = BasicSAXHandlerTest.class.getResourceAsStream("resources/simpleXML_KO.xml");
      try {
         parser.parse(stream, handler);
         if (handler.getStatus() == BasicSAXHandler.NO_ERRORS) {
            fail("fail to parse: must have Exceptions");
         }
      } catch (Exception e) {
         fail("fail to parse");
      }
   }
}
