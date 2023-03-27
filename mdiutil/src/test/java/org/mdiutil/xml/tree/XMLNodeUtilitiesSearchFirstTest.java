/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
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
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test the XMLNodeUtilities searchFirst method.
 *
 * @version 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesSearchFirstTest {

   public XMLNodeUtilitiesSearchFirstTest() {
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
    * Test of searchFirst method.
    */
   @Test
   public void testSearchFirstDeep() {
      System.out.println("XMLNodeUtilitiesSearchFirstTest : testSearchFirstDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      XMLNode node = XMLNodeUtilities.searchFirst(root, "element2", true);
      assertNotNull("Node must not be null", node);
      
      assertEquals("Node name", "element2", node.getName());
      String attrValue = node.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "second", attrValue);      
   }
   
   /**
    * Test of searchFirst method.
    */
   @Test
   public void testSearchFirstNotDeep() {
      System.out.println("XMLNodeUtilitiesSearchFirstTest : testSearchFirstNotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      XMLNode node = XMLNodeUtilities.searchFirst(root, "element2", false);
      assertNull("Node must be null", node);  
   }   
   
   /**
    * Test of searchFirst method.
    */
   @Test
   public void testSearchFirst2NotDeep() {
      System.out.println("XMLNodeUtilitiesSearchFirstTest : testSearchFirst2NotDeep");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearch4.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild();
      XMLNode node = XMLNodeUtilities.searchFirst(root, "element1", false);
      assertNotNull("Node must not be null", node);  
      
      assertEquals("Node name", "element1", node.getName());
      String attrValue = node.getAttributeValue("name");
      assertNotNull("Attribute exists", attrValue);
      assertEquals("Attribute value", "first", attrValue);      
   }     
}
