/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

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
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.xml.tree.XMLNodeUtilities;
import org.mdiutil.xml.tree.XMLRoot;

/**
 *
 * @version 1.2.39.6
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLIncluderTest {

   public XMLIncluderTest() {
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
      System.out.println("XMLIncluderTest : testInclude");
      URL xmlFile = XMLIncluderTest.class.getResource("resources/testIncludeParent.xml");
      File outputFile = File.createTempFile("includer", ".xml");
      XMLIncluder includer = new XMLIncluder(xmlFile);
      includer.write(outputFile);

      String text = XMLTestUtilities.getText(outputFile);
      XMLRoot root = XMLNodeUtilities.getRootNode(text, XMLNodeUtilities.SHOW_EXCEPTIONS | XMLNodeUtilities.SHOW_WARNINGS);
      assertNotNull("XMLRoot must not be null", root);
      assertEquals("XMLRoot name", "root", root.getName());
      assertEquals("XMLRoot children", 1, root.countChildren());
   }

   /**
    * Check of including a file.
    */
   @Test
   public void testInclude2() throws IOException {
      System.out.println("XMLIncluderTest : testInclude2");
      URL xmlFile = XMLIncluderTest.class.getResource("resources/testIncludeParent4.xml");
      File outputFile = File.createTempFile("includer", "xml");
      XMLIncluder includer = new XMLIncluder(xmlFile);
      includer.write(outputFile);

      String text = XMLTestUtilities.getText(outputFile);
      XMLRoot root = XMLNodeUtilities.getRootNode(text, 0);
      assertNotNull("XMLRoot must not be null", root);
      assertEquals("XMLRoot name", "root", root.getName());
      assertEquals("XMLRoot children", 1, root.countChildren());
   }
}
