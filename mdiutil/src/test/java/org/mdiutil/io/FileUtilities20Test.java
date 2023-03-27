/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertFalse;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for FileUtilities. Unit tests for the isURLFound method.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities20Test {
   public FileUtilities20Test() {
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
    * Test of isURLFound method.
    */
   @Test
   public void testIsURLFound() throws IOException {
      System.out.println("FileUtilities18Test : testIsURLFound");
      URL url = this.getClass().getResource("resources/file1.txt");
      String path = url.getFile();
      path = path.replaceFirst("file1.txt", "filenotexist.txt");
      File file = new File(path);
      url = file.toURI().toURL();
      assertFalse("The URL must not exist", FileUtilities.isURLFound(url));
   }

   /**
    * Test of isURLFound method.
    */
   @Test
   public void testIsURLFound2() throws IOException {
      System.out.println("FileUtilities18Test : testIsURLFound2");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/");
      assertTrue("The URL must exist", FileUtilities.isURLFound(url));
   }

   /**
    * Test of isURLFound method.
    */
   @Test
   public void testIsURLFound3() throws IOException {
      System.out.println("FileUtilities18Test : testIsURLFound3");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.html");
      assertFalse("The URL must not exist", FileUtilities.isURLFound(url));
   }

   /**
    * Test of isURLFound method.
    */
   @Test
   public void testIsURLFound5() throws IOException {
      System.out.println("FileUtilities18Test : testIsURLFound5");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.php");
      int option = FileUtilities.OPTION_FILEEXIST_HTTP_FORCETRUE + FileUtilities.OPTION_FILEEXIST_SKIP_PHP;
      assertFalse("The URL must not exist", FileUtilities.isURLFound(url, option));
   }

   /**
    * Test of isURLFound method.
    */
   @Test
   public void testIsURLFound6() throws IOException {
      System.out.println("FileUtilities18Test : testIsURLFound6");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.html?name=true");
      int option = FileUtilities.OPTION_FILEEXIST_HTTP_FORCETRUE + FileUtilities.OPTION_FILEEXIST_SKIP_QUERIES;
      assertFalse("The URL must not exist", FileUtilities.isURLFound(url, option));
   }
}
