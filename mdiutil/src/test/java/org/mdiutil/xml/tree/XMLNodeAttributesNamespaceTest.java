/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.net.URL;
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
 *
 * @since 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributesNamespaceTest {
   private static XMLNode root = null;

   public XMLNodeAttributesNamespaceTest() {
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
      URL xmlURL = XMLNode.class.getResource("resources/xmlvaluesNamespace.xml");
      root = XMLNodeUtilities.getRootNode(xmlURL);
      assertNotNull("Root must not be null", root);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   @Order(order = 2)
   public void testGetAttributeValue() {
      System.out.println("XMLNodeAttributesNamespaceTest : testGetAttributeValue");
      assertNotNull("Root must not be null", root);

      XMLNode node = root.getFirstChild();
      String valueStr = node.getAttributeValue("value");
      assertEquals("Value", "1", valueStr);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 1, valueI);

      long valueL = node.getAttributeValueAsLong("value");
      assertEquals("Value", 1L, valueL);

      short valueS = node.getAttributeValueAsShort("value");
      assertEquals("Value", (short) 1, valueS);

      byte valueB = node.getAttributeValueAsByte("value");
      assertEquals("Value", (byte) 1, valueB);

      char valueC = node.getAttributeValueAsChar("value");
      assertEquals("Value", (char) 1, valueC);

      float valueF = node.getAttributeValueAsFloat("value");
      assertEquals("Value", 1f, valueF, 0.00001f);

      double valueD = node.getAttributeValueAsDouble("value");
      assertEquals("Value", 1d, valueD, 0.00000001D);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   @Order(order = 3)
   public void testGetAttributeValue2() {
      System.out.println("XMLNodeAttributesNamespaceTest : testGetAttributeValue2");
      assertNotNull("Root must not be null", root);

      XMLNode node = root.getChildren().get(1);
      String valueStr = node.getAttributeValue("value");
      assertEquals("Value", "80000", valueStr);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 80000, valueI);

      char valueC = node.getAttributeValueAsChar("value");
      assertEquals("Value", (char) 0, valueC);

      valueC = node.getAttributeValueAsChar("value", (char) 10);
      assertEquals("Value", (char) 10, valueC);

      byte valueB = node.getAttributeValueAsByte("value");
      assertEquals("Value", (byte) 0, valueB);

      valueB = node.getAttributeValueAsByte("value", (byte) 10);
      assertEquals("Value", (byte) 10, valueB);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   @Order(order = 4)
   public void testGetAttributeValue3() {
      System.out.println("XMLNodeAttributesNamespaceTest : testGetAttributeValue3");
      assertNotNull("Root must not be null", root);

      XMLNode node = root.getChildren().get(2);

      char valueC = node.getAttributeValueAsChar("value");
      assertEquals("Value", (char) 2000, valueC);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   @Order(order = 5)
   public void testGetAttributeValue4() {
      System.out.println("XMLNodeAttributesNamespaceTest : testGetAttributeValue4");
      assertNotNull("Root must not be null", root);

      XMLNode node = root.getChildren().get(3);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 0, valueI);

      valueI = node.getAttributeValueAsInt("value", 10, true);
      assertEquals("Value", 10, valueI);

      valueI = node.getAttributeValueAsInt("value", 0, false);
      assertEquals("Value", 25, valueI);

      long valueL = node.getAttributeValueAsLong("value");
      assertEquals("Value", 0L, valueL);

      valueL = node.getAttributeValueAsLong("value", 10L, true);
      assertEquals("Value", 10L, valueL);

      valueL = node.getAttributeValueAsLong("value", 0L, false);
      assertEquals("Value", 25L, valueL);

      float valueF = node.getAttributeValueAsFloat("value");
      assertEquals("Value", 25.2f, valueF, 0.00001f);

      double valueD = node.getAttributeValueAsDouble("value");
      assertEquals("Value", 25.2d, valueD, 0.00000001D);
   }
}
