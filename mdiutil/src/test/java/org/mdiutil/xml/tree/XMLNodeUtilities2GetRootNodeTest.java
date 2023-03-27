/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;
import org.mdiutil.xml.XMLTestUtilities;

/**
 * Test the * @since 1.2.39.6.
 *
 * @since 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilities2GetRootNodeTest {

   public XMLNodeUtilities2GetRootNodeTest() {
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
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode() {
      System.out.println("XMLNodeUtilities2GetRootNodeTest : testGetRootNode");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(xmlURL);
      assertNotNull("root must not be null", root);
   }

   /**
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode2() throws IOException {
      System.out.println("XMLNodeUtilitiesGetRootNodeTest : testGetRootNode2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      String content = XMLTestUtilities.getText(xmlURL);
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(content);
      assertNotNull("root must not be null", root);
   }

   /**
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode3() throws IOException {
      System.out.println("XMLNodeUtilitiesGetRootNodeTest : testGetRootNode3");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      File file = new File(xmlURL.getFile());
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLRoot root = utils.getRootNode(file);
      assertNotNull("root must not be null", root);
   }

   /**
    * Test of getRootNode method.
    */
   @Test
   public void testGetRootNode4() {
      System.out.println("XMLNodeUtilities2GetRootNodeTest : testGetRootNode4");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.keepLineNumbers(true);
      XMLRoot root = utils.getRootNode(xmlURL);
      assertNotNull("root must not be null", root);
      int number = root.getLineNumber();
      assertEquals("line number must be 2", 2, number);
   }
}
