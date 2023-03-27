/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
 * @since 1.2.39.6
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLIncluder2Test {
   private static File outputFile = null;

   public XMLIncluder2Test() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
      if (outputFile != null && outputFile.exists()) {
         outputFile.delete();
      }
      outputFile = null;
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
      System.out.println("XMLIncluder2Test : testInclude");
      URL xmlFile = XMLIncluder2Test.class.getResource("resources/include/widgetLibrary.xml");
      outputFile = File.createTempFile("includer", ".xml");
      XMLIncluder includer = new XMLIncluder(xmlFile);
      includer.write(outputFile);

      XMLRoot root = XMLNodeUtilities.getRootNode(outputFile, XMLNodeUtilities.SHOW_EXCEPTIONS | XMLNodeUtilities.SHOW_WARNINGS);
      assertNotNull("XMLRoot must not be null", root);
      assertEquals("XMLRoot name", "widgetLibrary", root.getName());
      assertEquals("XMLRoot children", 1, root.countChildren());
   }
}
