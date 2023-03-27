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
 * Unit tests for FileUtilities. Unit tests for the exist method.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities18Test {
   public FileUtilities18Test() {
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
    * Test of exist method.
    */
   @Test
   public void testExistURL() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL");
      URL url = this.getClass().getResource("resources/file1.txt");
      String path = url.getFile();
      path = path.replaceFirst("file1.txt", "filenotexist.txt");
      File file = new File(path);
      url = file.toURI().toURL();
      assertFalse("The URL must not exist", FileUtilities.exist(url));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistURL2() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL2");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/");
      assertTrue("The URL must exist", FileUtilities.exist(url));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistURL3() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL3");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.html");
      assertFalse("The URL must not exist", FileUtilities.exist(url));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistURL4() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL4");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.html");
      assertTrue("The URL must exist", FileUtilities.exist(url, FileUtilities.OPTION_FILEEXIST_HTTP_FORCETRUE));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistURL5() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL5");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.php");
      int option = FileUtilities.OPTION_FILEEXIST_HTTP_FORCETRUE + FileUtilities.OPTION_FILEEXIST_SKIP_PHP;
      assertFalse("The URL must not exist", FileUtilities.exist(url, option));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistURL6() throws IOException {
      System.out.println("FileUtilities18Test : testExistURL6");
      URL url = new URL("https://sourceforge.net/projects/mdiutilities/titi.html?name=true");
      int option = FileUtilities.OPTION_FILEEXIST_HTTP_FORCETRUE + FileUtilities.OPTION_FILEEXIST_SKIP_QUERIES;
      assertFalse("The URL must not exist", FileUtilities.exist(url, option));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistFile() throws IOException {
      System.out.println("FileUtilities18Test : testExistFile");
      URL url = this.getClass().getResource("resources/file1.txt");
      File file = new File(url.getFile());
      assertTrue("The URL must exist", FileUtilities.exist(file));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistFile2() throws IOException {
      System.out.println("FileUtilities18Test : testExistFile2");
      URL url = this.getClass().getResource("resources/file1.txt");
      File file = new File(url.getFile());
      assertTrue("The url must exist", FileUtilities.exist(file));
   }

   /**
    * Test of exist method.
    */
   @Test
   public void testExistFile3() throws IOException {
      System.out.println("FileUtilities18Test : testExistFile3");
      URL url = this.getClass().getResource("resources/file1.txt");
      String path = url.getFile();
      path = path.replaceFirst("file1.txt", "filenotexist.txt");
      File file = new File(path);
      assertFalse("The url must not exist", FileUtilities.exist(file));
   }
}
