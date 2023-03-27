/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
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

/**
 * Unit tests for FileUtilities.
 *
 * @version 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities12Test {
   public FileUtilities12Test() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of fixURLPath method, of class FileUtilities.
    */
   @Test
   public void testReplaceEscapedSequences() throws IOException {
      System.out.println("FileUtilities12Test : testReplaceEscapedSequences");
      String s = "jar:file:/my/File with space/module.jar!/org/my/file/file.xml";
      URL url = new URL(s);
      url = FileUtilities.fixURLPath(url);
      URL parent = FileUtilities.getParentURL(url);
      String s2 = parent.toString();
      assertEquals("jar:file:/my/File%20with%20space/module.jar!/org/my/file", s2);
   }

}
