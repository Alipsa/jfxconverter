/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertEquals;
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
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the EntityResolver class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class EntityListResolverTest {

   public EntityListResolverTest() {
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
    * Validate an XML Document relative to a Schema with a redefinition from one Schema to another one. The reference of the
    * first Schema in the second one is invalid (impossible to find the first Schema in the SchemaLocation attribute), and we
    * should have a SAXException.
    */
   @Test
   public void testValidation() {
      System.out.println("EntityListResolverTest : testValidation");
      URL schemaURL = EntityListResolverTest.class.getResource("resources/testRedefine.xsd");

      XMLSAXParser parser = new XMLSAXParser();
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setExceptionListener(listener);
      parser.setSchema(schemaURL);
      parser.setValidating(true);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = EntityListResolverTest.class.getResource("resources/test.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertEquals("Must have no SAXParserException", 0, exceptions.size());
      assertEquals("Must have no SAXException", 1, listener.getExceptions().size());
   }

   /**
    * Validate an XML Document relative to a Schema with a redefinition from one Schema to another one. The reference of the
    * first Schema in the second one should be found because the EntityListResolver specifies the URL to access with the SchemaLocation
    * attribute, so we should not have a SAXException.
    */
   @Test
   public void testValidation2() {
      System.out.println("EntityListResolverTest : testValidation2");
      URL schemaURL = EntityListResolverTest.class.getResource("resources/testRedefine.xsd");
      URL totoReference = EntityListResolverTest.class.getResource("resources/test.xsd");

      EntityListResolver resolver = new EntityListResolver();
      resolver.addResolvedSystemID("toto", totoReference);

      XMLSAXParser parser = new XMLSAXParser();
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setExceptionListener(listener);
      parser.setSchema(schemaURL);
      parser.setEntityListResolver(resolver);
      parser.setValidating(true);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = EntityListResolverTest.class.getResource("resources/test.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Validate an XML Document relative to a Schema with a redefinition from one Schema to another one. The reference of the
    * first Schema in the second one should be found because the EntityListResolver specifies the URL to access with the SchemaLocation
    * attribute, so we should not have a SAXException. However the content of the document is invalid relative to the redefined Schema, so we
    * should have a SAXParseException.
    */
   @Test
   public void testValidation3() {
      System.out.println("EntityListResolverTest : testValidation3");
      URL schemaURL = EntityListResolverTest.class.getResource("resources/testRedefine.xsd");
      URL totoReference = EntityListResolverTest.class.getResource("resources/test.xsd");

      EntityListResolver resolver = new EntityListResolver();
      resolver.addResolvedSystemID("toto", totoReference);

      XMLSAXParser parser = new XMLSAXParser();
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setExceptionListener(listener);
      parser.setSchema(schemaURL);
      parser.setEntityListResolver(resolver);
      parser.setValidating(true);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = EntityListResolverTest.class.getResource("resources/test2.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertEquals("Must have one SAXParserException", 1, exceptions.size());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Validate an XML Document relative to a Schema with a redefinition from one Schema to another one. The reference of the
    * first Schema in the second one should be found because the EntityListResolver specifies the URL to access with the SchemaLocation
    * attribute, so we should not have a SAXException. In this case we reference an http resource.
    */
   @Test
   public void testValidation4() {
      System.out.println("EntityListResolverTest : testValidation4");
      URL schemaURL = EntityListResolverTest.class.getResource("resources/testRedefine2.xsd");
      URL totoReference = EntityListResolverTest.class.getResource("resources/test.xsd");

      EntityListResolver resolver = new EntityListResolver();
      resolver.addResolvedSystemID("http://sourceforge.net/project/j661/test.xsd", totoReference);

      XMLSAXParser parser = new XMLSAXParser();
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setExceptionListener(listener);
      parser.setSchema(schemaURL);
      parser.setEntityListResolver(resolver);
      parser.setValidating(true);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = EntityListResolverTest.class.getResource("resources/test2.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }

   /**
    * Validate an XML Document with an EntityResolver which set the location of a Schema in a specific namespace. This Schema
    * is itself referencing a child Schema. The EntityResolver must be able to find the parent and the child Schema.
    */
   @Test
   public void testValidation5() {
      System.out.println("EntityListResolverTest : testValidation5");
      URL totoReference = EntityListResolverTest.class.getResource("resources/testResolver.xsd");

      EntityListResolver resolver = new EntityListResolver();
      resolver.addResolvedSystemID("http://toto.com/sample/testResolver.xsd", totoReference);

      XMLSAXParser parser = new XMLSAXParser();
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      parser.setExceptionListener(listener);
      parser.setEntityListResolver(resolver);
      parser.setValidating(true);
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = EntityListResolverTest.class.getResource("resources/test3.xml");
      parser.parse(xmlURL);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());
   }
}
