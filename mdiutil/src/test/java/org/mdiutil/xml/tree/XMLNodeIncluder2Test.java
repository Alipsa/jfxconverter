/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import org.junit.*;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.xml.XMLTestUtilities;

/**
 *
 * @since 1.2.39.6
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
// TODO: Xinclude does not work
@Ignore
public class XMLNodeIncluder2Test {

   public XMLNodeIncluder2Test() {
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
      System.out.println("XMLNodeIncluder2Test : testInclude");
      URL xmlFile = XMLNodeIncluder2Test.class.getResource("resources/include/widgetLibrary.xml");
      File outputFile = File.createTempFile("includer", ".xml");
      XMLNodeIncluder includer = new XMLNodeIncluder(xmlFile);
      includer.write(outputFile);

      String text = XMLTestUtilities.getText(outputFile);
      XMLRoot root = XMLNodeUtilities.getRootNode(text, XMLNodeUtilities.SHOW_EXCEPTIONS | XMLNodeUtilities.SHOW_WARNINGS);
      assertNotNull("XMLRoot must not be null", root);
      assertEquals("XMLRoot name", "widgetLibrary", root.getName());
      assertEquals("XMLRoot children", 1, root.countChildren());
   }
}
