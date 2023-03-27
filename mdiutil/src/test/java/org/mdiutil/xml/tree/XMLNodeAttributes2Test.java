/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
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
 * @version 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributes2Test {

   public XMLNodeAttributes2Test() {
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
    * Test of addAttribute method, of class XMLNode.
    */
   @Test
   public void testAddAttribute() {
      System.out.println("XMLNodeAttributes2Test : testAddAttribute");

      XMLNode node = new XMLNode("node");
      node.addAttribute("value", "toto");

      String valueStr = node.getAttributeValue("value");
      assertEquals("Value", "toto", valueStr);
   }

   /**
    * Test of addAttribute method, of class XMLNode.
    */
   @Test
   public void testAddAttribute2() {
      System.out.println("XMLNodeAttributes2Test : testAddAttribute2");

      XMLNode node = new XMLNode("node");
      node.addAttribute("value", 2);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 2, valueI);

      node.addAttribute("value", 20L);

      valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 20, valueI);

      long valueL = node.getAttributeValueAsLong("value");
      assertEquals("Value", 20L, valueL);
   }

   /**
    * Test of addAttribute method, of class XMLNode.
    */
   @Test
   public void testAddAttribute3() {
      System.out.println("XMLNodeAttributesTest : testAddAttribute3");

      XMLNode node = new XMLNode("node");
      node.addAttribute("value", 20f);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 0, valueI);

      valueI = node.getAttributeValueAsInt("value", 0, false);
      assertEquals("Value", 20, valueI);

      float valueF = node.getAttributeValueAsFloat("value");
      assertEquals("Value", 20f, valueF, 0.00001f);
   }

   /**
    * Test of addAttribute method, of class XMLNode.
    */
   @Test
   public void testAddAttribute4() {
      System.out.println("XMLNodeAttributesTest : testAddAttribute4");

      XMLNode node = new XMLNode("node");
      node.addAttribute("value", (char) 10);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 10, valueI);

      char valueC = node.getAttributeValueAsChar("value");
      assertEquals("Value", (char) 10, valueC);
   }
}
