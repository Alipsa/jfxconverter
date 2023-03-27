/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * Test the XMLNodeUtilitiess.
 *
 * @since 1.2.39.6
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilities2GetNodeTest {

   public XMLNodeUtilities2GetNodeTest() {
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
    * Test of getNode method.
    */
   @Test
   public void testGetNode() {
      System.out.println("XMLNodeUtilities2GetNodeTest : testGetNode");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode node = utils.getNode(xmlURL);
      assertNotNull("node must not be null", node);
      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
   }

   /**
    * Test of getNode method.
    */
   @Test
   public void testGetNode2() throws IOException {
      System.out.println("XMLNodeUtilities2GetNodeTest : testGetNode2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      String content = XMLTestUtilities.getText(xmlURL);
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode node = utils.getNode(content);
      assertNotNull("node must not be null", node);
      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
   }

   /**
    * Test of getNode method.
    */
   @Test
   public void testGetNode3() throws IOException {
      System.out.println("XMLNodeUtilities2GetNodeTest : testGetNode3");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      File file = new File(xmlURL.getFile());
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      XMLNode node = utils.getNode(file);
      assertNotNull("node must not be null", node);
      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
   }

   /**
    * Test of getNode method.
    */
   @Test
   public void testGetNode4() {
      System.out.println("XMLNodeUtilities2GetRootNodeTest : testGetNode4");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint8.xml");
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.keepLineNumbers(true);
      XMLNode node = utils.getNode(xmlURL);
      assertNotNull("node must not be null", node);
      assertFalse("node must not be an XMLRoot", node instanceof XMLRoot);
      int number = node.getLineNumber();
      assertEquals("line number must be 2", 2, number);
   }
}
