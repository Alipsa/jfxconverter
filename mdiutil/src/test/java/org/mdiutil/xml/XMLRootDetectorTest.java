/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016, 2017, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.*;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.xml.tree.XMLNode;

/**
 * Unit Tests for the XML root detector.
 *
 * @version 1.2.40
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLRootDetectorTest {

   public XMLRootDetectorTest() {
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
    * Check the root of the XML file.
    */
   @Test
   public void testSimpleRoot() throws Exception {
      System.out.println("XMLRootDetectorTest : testSimpleRoot");
      URL url = ResolverSAXHandlerTest.class.getResource("resources/simpleXML.xml");
      try {
         XMLRootDetector detector = new XMLRootDetector();
         XMLNode node = detector.getRoot(url);
         assertNotNull("Root must not be null", node);
         assertEquals("Root name", "root", node.getName());
         assertEquals("Number of attributes", 1, node.countAttributes());
         assertTrue("Must have an attribute for desc", node.hasAttribute("desc"));
         assertEquals("desc attribute", "example", node.getAttributeValue("desc"));
         assertTrue("Root name", detector.checkRootName(url, "root"));
      } catch (Exception e) {
         e.printStackTrace();
         fail("fail to parse");
      }
   }

   /**
    * Check the root name of the XML file.
    */
   @Test
   public void testSimpleRootName() throws Exception {
      System.out.println("XMLRootDetectorTest : testSimpleRootName");
      URL url = ResolverSAXHandlerTest.class.getResource("resources/simpleXML.xml");
      try {
         XMLRootDetector detector = new XMLRootDetector();
         String name = detector.getRootName(url);
         assertEquals("Root name", "root", name);
         assertTrue("Root name", detector.checkRootName(url, "root"));
      } catch (Exception e) {
         fail("fail to parse");
      }
   }
}
