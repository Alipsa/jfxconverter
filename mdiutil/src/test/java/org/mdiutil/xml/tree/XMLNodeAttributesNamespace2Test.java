/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import javax.xml.namespace.QName;
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
 * @since 1.2.20
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeAttributesNamespace2Test {

   public XMLNodeAttributesNamespace2Test() {
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
      System.out.println("XMLNodeAttributesNamespace2Test : testAddAttribute");

      QName qname = new QName("http://www.test.org/", "node", "h");
      XMLNode node = new XMLNode(qname);
      node.addAttribute("value", "toto");

      String valueStr = node.getAttributeValue("value");
      assertEquals("Value", "toto", valueStr);
   }

   /**
    * Test of addAttribute method, of class XMLNode.
    */
   @Test
   public void testAddAttribute2() {
      System.out.println("XMLNodeAttributesNamespace2Test : testAddAttribute2");

      QName qname = new QName("http://www.test.org/", "node", "h");
      XMLNode node = new XMLNode(qname);
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
      System.out.println("XMLNodeAttributesNamespace2Test : testAddAttribute3");

      QName qname = new QName("http://www.test.org/", "node", "h");
      XMLNode node = new XMLNode(qname);
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
      System.out.println("XMLNodeAttributesNamespace2Test : testAddAttribute4");

      QName qname = new QName("http://www.test.org/", "node", "h");
      XMLNode node = new XMLNode(qname);
      node.addAttribute("value", (char) 10);

      int valueI = node.getAttributeValueAsInt("value");
      assertEquals("Value", 10, valueI);

      char valueC = node.getAttributeValueAsChar("value");
      assertEquals("Value", (char) 10, valueC);
   }
}
