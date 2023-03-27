/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
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
 * test the XMLNode.
 *
 * @since 1.2.40
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNode3Test {

   public XMLNode3Test() {
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
    * Test of copy method, of class XMLNode.
    */
   @Test
   public void testNode() {
      System.out.println("XMLNode3Test : testNode");
      XMLNode root = new XMLRoot("root");
      root.addAttribute("name", "example");
      root.addAttribute("desc", "name");
      root.setCDATA("< the escaped content >");

      String xml = XMLNodeUtilities.print(root, 2);
      assertEquals("XML content", "<root desc=\"name\" name=\"example\">&lt; the escaped content &gt;</root>", xml);
   }
}
