/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 *
 * @since 1.2.33
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributes3Test {
   private static XMLNode root = null;

   public XMLNodeAttributes3Test() {
   }

   @BeforeClass
   public static void setUpClass() {
      URL xmlURL = XMLNode.class.getResource("resources/xmlvalues2.xml");
      root = XMLNodeUtilities.getRootNode(xmlURL);
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
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   public void testGetAttributeValue() {
      System.out.println("XMLNodeAttributes2Test : testGetAttributeValue");

      XMLNode node = root.getFirstChild();
      String valueStr = node.getAttributeValue("valueInt");
      assertEquals("Value", "1", valueStr);

      int valueI = node.getAttributeValueAsInt("valueInt");
      assertEquals("Value", 1, valueI);
      boolean b = node.attributeValueIsInt("valueInt");
      assertTrue("value must be an int", b);

      long valueL = node.getAttributeValueAsLong("valueInt");
      assertEquals("Value", 1L, valueL);
      b = node.attributeValueIsLong("valueInt");
      assertTrue("value must be a long", b);

      char valueC = node.getAttributeValueAsChar("valueInt");
      assertEquals("Value", (char) 1, valueC);
      b = node.attributeValueIsChar("valueInt");
      assertTrue("value must be a char", b);

      float valueF = node.getAttributeValueAsFloat("valueInt");
      assertEquals("Value", 1f, valueF, 0.00001f);
      b = node.attributeValueIsFloat("valueInt");
      assertTrue("value must be a float", b);

      double valueD = node.getAttributeValueAsDouble("valueInt");
      assertEquals("Value", 1d, valueD, 0.00000001D);
      b = node.attributeValueIsDouble("valueInt");
      assertTrue("value must be a double", b);

      boolean valueB = node.getAttributeValueAsBoolean("valueInt");
      assertEquals("Value", false, valueB);
      b = node.attributeValueIsBoolean("valueInt");
      assertFalse("value must not be a boolean", b);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   public void testGetAttributeValue2() {
      System.out.println("XMLNodeAttributes2Test : testGetAttributeValue2");

      XMLNode node = root.getChild(1);
      String valueStr = node.getAttributeValue("valueLong");
      assertEquals("Value", "8000000", valueStr);

      int valueI = node.getAttributeValueAsInt("valueLong");
      assertEquals("Value", 8000000, valueI);
      boolean b = node.attributeValueIsInt("valueLong");
      assertTrue("value must be an int", b);
      
      short valueS = node.getAttributeValueAsShort("valueLong");
      assertEquals("Value", 0, valueS);
      b = node.attributeValueIsShort("valueLong");
      assertFalse("value must not be a short", b);      

      long valueL = node.getAttributeValueAsLong("valueLong");
      assertEquals("Value", 8000000L, valueL);
      b = node.attributeValueIsLong("valueLong");
      assertTrue("value must be a long", b);

      char valueC = node.getAttributeValueAsChar("valueLong");
      assertEquals("Value", (char) 0, valueC);
      b = node.attributeValueIsChar("valueLong");
      assertFalse("value must not be a char", b);

      float valueF = node.getAttributeValueAsFloat("valueLong");
      assertEquals("Value", 8000000f, valueF, 0.00001f);
      b = node.attributeValueIsFloat("valueLong");
      assertTrue("value must be a float", b);

      double valueD = node.getAttributeValueAsDouble("valueLong");
      assertEquals("Value", 8000000d, valueD, 0.00000001D);
      b = node.attributeValueIsDouble("valueLong");
      assertTrue("value must be a double", b);

      boolean valueB = node.getAttributeValueAsBoolean("valueLong");
      assertEquals("Value", false, valueB);
      b = node.attributeValueIsBoolean("valueLong");
      assertFalse("value must not be a boolean", b);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   public void testGetAttributeValue3() {
      System.out.println("XMLNodeAttributes2Test : testGetAttributeValue3");

      XMLNode node = root.getChild(2);
      String valueStr = node.getAttributeValue("valueFloat");
      assertEquals("Value", "25.2", valueStr);

      int valueI = node.getAttributeValueAsInt("valueFloat", 0, false);
      assertEquals("Value", 25, valueI);
      boolean b = node.attributeValueIsInt("valueFloat", false);
      assertTrue("value must be an int", b);

      valueI = node.getAttributeValueAsInt("valueFloat");
      assertEquals("Value", 0, valueI);
      b = node.attributeValueIsInt("valueFloat");
      assertFalse("value must not be an int", b);

      long valueL = node.getAttributeValueAsLong("valueFloat", 0L, false);
      assertEquals("Value", 25L, valueL);
      b = node.attributeValueIsLong("valueFloat", false);
      assertTrue("value must be a long", b);

      valueL = node.getAttributeValueAsLong("valueFloat", 0L);
      assertEquals("Value", 0L, valueL);
      b = node.attributeValueIsLong("valueFloat");
      assertFalse("value must not be a long", b);

      char valueC = node.getAttributeValueAsChar("valueFloat");
      assertEquals("Value", (char) 0, valueC);
      b = node.attributeValueIsChar("valueLong");
      assertFalse("value must not be a char", b);

      float valueF = node.getAttributeValueAsFloat("valueFloat");
      assertEquals("Value", 25.2f, valueF, 0.00001f);
      b = node.attributeValueIsFloat("valueFloat");
      assertTrue("value must be a float", b);

      double valueD = node.getAttributeValueAsDouble("valueFloat");
      assertEquals("Value", 25.2D, valueD, 0.00000001D);
      b = node.attributeValueIsDouble("valueFloat");
      assertTrue("value must be a double", b);
      
      short valueS = node.getAttributeValueAsShort("valueFloat");
      assertEquals("Value", 0, valueS);
      b = node.attributeValueIsShort("valueFloat");
      assertFalse("value must not be a short", b);             

      boolean valueB = node.getAttributeValueAsBoolean("valueFloat");
      assertEquals("Value", false, valueB);
      b = node.attributeValueIsBoolean("valueFloat");
      assertFalse("value must not be a boolean", b);
   }

   /**
    * Test of getAttributeValue method, of class XMLNode.
    */
   @Test
   public void testGetAttributeValue4() {
      System.out.println("XMLNodeAttributes2Test : testGetAttributeValue4");

      XMLNode node = root.getChild(3);
      String valueStr = node.getAttributeValue("valueBool");
      assertEquals("Value", "true", valueStr);

      int valueI = node.getAttributeValueAsInt("valueBool");
      assertEquals("Value", 0, valueI);
      boolean b = node.attributeValueIsInt("valueBool");
      assertFalse("value must not be an int", b);

      long valueL = node.getAttributeValueAsLong("valueBool");
      assertEquals("Value", 0L, valueL);
      b = node.attributeValueIsLong("valueBool");
      assertFalse("value must not be a long", b);

      char valueC = node.getAttributeValueAsChar("valueBool");
      assertEquals("Value", (char) 0, valueC);
      b = node.attributeValueIsChar("valueBool");
      assertFalse("value must not be a char", b);

      float valueF = node.getAttributeValueAsFloat("valueBool");
      assertEquals("Value", 0f, valueF, 0.00001f);
      b = node.attributeValueIsFloat("valueBool");
      assertFalse("value must not be a float", b);

      double valueD = node.getAttributeValueAsDouble("valueBool");
      assertEquals("Value", 0d, valueD, 0.00000001D);
      b = node.attributeValueIsDouble("valueBool");
      assertFalse("value must not be a double", b);

      boolean valueB = node.getAttributeValueAsBoolean("valueBool");
      assertEquals("Value", true, valueB);
      b = node.attributeValueIsBoolean("valueBool");
      assertTrue("value must be a boolean", b);
   }
}
