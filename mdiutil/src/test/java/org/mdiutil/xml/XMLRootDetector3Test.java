/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.*;
import java.net.URL;
import javax.xml.namespace.QName;
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
 * @since 1.2.40
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLRootDetector3Test {

   public XMLRootDetector3Test() {
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
   public void testRoot() throws Exception {
      System.out.println("XMLRootDetector3Test : testRoot");
      URL url = ResolverSAXHandlerTest.class.getResource("resources/dataTypeSet.xml");
      try {
         XMLRootDetector detector = new XMLRootDetector();
         XMLNode node = detector.getRoot(url);
         assertNotNull("Root must not be null", node);
         assertEquals("Root name", "dataTypeSet", node.getName());
         assertTrue("Root name", detector.checkRootName(url, "dataTypeSet"));
      } catch (Exception e) {
         e.printStackTrace();
         fail("fail to parse");
      }
   }
}