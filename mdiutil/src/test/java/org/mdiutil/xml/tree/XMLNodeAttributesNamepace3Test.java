/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import javax.xml.namespace.QName;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 * test the XMLNode.
 *
 * @since 1.2.39.5
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributesNamepace3Test {
   private static XMLNode root = null;

   public XMLNodeAttributesNamepace3Test() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
      root = null;
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of parsing an URL.
    */
   @Test
   @Order(order = 1)
   public void testParseURL() {
      System.out.println("XMLNodeAttributesNamespaceTest : testParseURL");
      URL xmlURL = XMLNode.class.getResource("resources/dataSpaceProfile.xml");
      root = XMLNodeUtilities.getRootNode(xmlURL, XMLNodeUtilities.NAMESPACE_AWARE);
      assertNotNull("Root must not be null", root);
   }

   /**
    * Test of getting attributes for an XMLNode.
    */
   @Test
   @Order(order = 2)
   public void testAttributes() {
      System.out.println("XMLNodeAttributesNamepace3Test : testAttributes");
      assertNotNull("Root must not be null", root);

      QName qname = new QName("", "schemaLocation", "xsi");

      assertTrue("Attribute QName", root.hasAttribute(qname));
      String value = root.getAttributeValue(qname);
      assertEquals("Attribute value", "http://www.aviation-ia.com/aeec/SupportFiles/661/dataSpaceProfileSeed.xsd/1 dataSpaceProfileSeed.xsd", value);
   }
}
