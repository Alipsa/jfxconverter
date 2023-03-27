/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.mdiutil.lang.ResourceLoader;

/**
 * Unit tests for FileUtilities.
 *
 * @since 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities13Test {

   /**
    * Test of isArchive method, of class FileUtilities.
    */
   @Test
   public void testIsArchive() throws IOException {
      System.out.println("FileUtilities13Test : testIsArchive");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      assertTrue(url + " must be an archive", FileUtilities.isArchive(url));
   }

   /**
    * Test of isArchive method, of class FileUtilities.
    */
   @Test
   public void testIsArchive2() throws IOException {
      System.out.println("FileUtilities13Test : testIsArchive2");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      File file = new File(url.getFile());
      assertTrue(url + " must be an archive", FileUtilities.isArchive(file));
   }

   /**
    * Test of isArchive method, of class FileUtilities.
    */
   @Test
   public void testIsArchive3() throws IOException {
      System.out.println("FileUtilities13Test : testIsArchive3");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("file1.txt");
      assertFalse("Must not be an archive", FileUtilities.isArchive(url));
   }

   /**
    * Test of isArchive method, of class FileUtilities.
    */
   @Test
   public void testIsArchive4() throws IOException {
      System.out.println("FileUtilities13Test : testIsArchive4");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("file1.txt");
      File file = new File(url.getFile());
      assertFalse("Must not be an archive", FileUtilities.isArchive(file));
   }

   /**
    * Test of isArchive method, of class FileUtilities.
    */
   @Test
   public void testIsArchive5() throws IOException {
      System.out.println("FileUtilities13Test : testIsArchive5");
      URL url = this.getClass().getResource("/testutils/TestUtils.jar");
      File testUtils = FileUtilities.getFile(url);
      assertTrue("Must be an archive", FileUtilities.isArchive(testUtils));
   }

}
