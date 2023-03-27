/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Test the XMLNodeUtilitiess.
 *
 * @since 1.2.41
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilities2ParseValidateTest implements ErrorHandler {
   private List<SAXParseException> exceptions = new ArrayList<>();

   public XMLNodeUtilities2ParseValidateTest() {
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
    * Test of getNode method.
    */
   @Test
   public void testGetNode() {
      System.out.println("XMLNodeUtilities2Parse2Test : testGetNode");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/nodes/xmlNode.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.setErrorHandler(this);
      utils.setValidating(true);
      XMLNode node = utils.getNode(xmlURL);
      assertTrue("Must have no exceptions", exceptions.isEmpty());
      assertNotNull("node must not be null", node);

      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
   }

   /**
    * Test of getNode method.
    */
   @Test
   public void testGetNode2() {
      System.out.println("XMLNodeUtilities2Parse2Test : testGetNode2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/nodes/xmlNode2.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.setErrorHandler(this);
      utils.setValidating(true);
      XMLNode node = utils.getNode(xmlURL);
      assertTrue("Must have no exceptions", exceptions.isEmpty());
      assertNotNull("node must not be null", node);

      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
   }

   @Override
   public void warning(SAXParseException exception) throws SAXException {
      System.err.println(exception.getMessage());
      exceptions.add(exception);
   }

   @Override
   public void error(SAXParseException exception) throws SAXException {
      System.err.println(exception.getMessage());
      exceptions.add(exception);
   }

   @Override
   public void fatalError(SAXParseException exception) throws SAXException {
      System.err.println(exception.getMessage());
      exceptions.add(exception);
   }
}
