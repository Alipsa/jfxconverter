/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.xml.XMLIncluderTest;
import org.mdiutil.xml.XMLTestUtilities;

/**
 *
 * @since 1.2.39.6
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeIncluderTest {

   public XMLNodeIncluderTest() {
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
    * Check of including a file.
    */
   @Test
   public void testInclude() throws IOException {
      System.out.println("XMLNodeIncluderTest : testInclude");
      URL xmlFile = XMLIncluderTest.class.getResource("resources/testIncludeParent.xml");
      File outputFile = File.createTempFile("includer", ".xml");
      XMLNodeIncluder includer = new XMLNodeIncluder(xmlFile);
      includer.write(outputFile);

      String text = XMLTestUtilities.getText(outputFile);
      XMLRoot root = XMLNodeUtilities.getRootNode(text, XMLNodeUtilities.SHOW_EXCEPTIONS | XMLNodeUtilities.SHOW_WARNINGS);
      assertNotNull("XMLRoot must not be null", root);
      assertEquals("XMLRoot name", "root", root.getName());
      assertEquals("XMLRoot children", 1, root.countChildren());
      XMLNode child = root.getFirstChild();
      assertFalse("Node must not be an XMLRoot", child instanceof XMLRoot);
      assertTrue("Node must have name attribute", child.hasAttribute("name"));
   }
}
