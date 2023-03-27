/*------------------------------------------------------------------------------
* Copyright (C) 2021, 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test the XMLNodeUtilities.
 *
 * @version 1.2.34
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesNamespace3Test {

   public XMLNodeUtilitiesNamespace3Test() {
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
    * Test of search method.
    */
   @Test
   public void testSearch() {
      System.out.println("XMLNodeUtilitiesNamespace3Test : testSearch");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearchNamespace2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "h:element2", true);
      assertEquals("list size", 2, list.size());

      list = XMLNodeUtilities.search(root, "p:element2", true);
      assertEquals("list size", 1, list.size());

      list = XMLNodeUtilities.search(root, "element2", true);
      assertEquals("list size", 1, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch2() {
      System.out.println("XMLNodeUtilitiesNamespace3Test : testSearch2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearchNamespace2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);
      List<XMLNode> list = XMLNodeUtilities.search(root, "h:element2", false);
      assertEquals("list size", 0, list.size());

      list = XMLNodeUtilities.search(root, "p:element2", false);
      assertEquals("list size", 0, list.size());

      list = XMLNodeUtilities.search(root, "element2", false);
      assertEquals("list size", 0, list.size());
   }

   /**
    * Test of search method.
    */
   @Test
   public void testSearch3() {
      System.out.println("XMLNodeUtilitiesNamespace3Test : testSearch3");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlSearchNamespace2.xml");

      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL).getFirstChild();
      List<XMLNode> list = XMLNodeUtilities.search(root, "h:element2", false);
      assertEquals("list size", 2, list.size());

      list = XMLNodeUtilities.search(root, "p:element2", false);
      assertEquals("list size", 1, list.size());

      list = XMLNodeUtilities.search(root, "element2", false);
      assertEquals("list size", 1, list.size());
   }
}
