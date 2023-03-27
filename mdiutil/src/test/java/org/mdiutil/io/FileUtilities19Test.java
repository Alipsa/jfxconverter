/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
 * Unit tests for FileUtilities. Unit tests for the getChildURL method.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities19Test {
   public FileUtilities19Test() {
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
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL");
      URL url = this.getClass().getResource("resources/file1.txt");
      URL parentURL = FileUtilities.getParentURL(url);
      URL childURL = FileUtilities.getChildURL(parentURL, "file1.txt");
      assertNotNull(childURL);
      assertEquals(url, childURL);
   }

   /**
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL2() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL2");
      URL url = this.getClass().getResource("resources/file1.txt");
      URL parentURL = FileUtilities.getParentURL(url);
      URL childURL = FileUtilities.getChildURL(parentURL, "filenotexist.txt", true);
      assertNull(childURL);
   }

   /**
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL3() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL3");
      URL url = this.getClass().getResource("resources/file1.txt");
      URL parentURL = FileUtilities.getParentURL(url);
      int options = FileUtilities.OPTION_CHILDURL_FILTEREXIST_FILE;
      URL childURL = FileUtilities.getChildURL(parentURL, "filenotexist.txt", options);
      assertNull(childURL);
   }

   /**
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL4() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL4");
      URL url = this.getClass().getResource("resources/file1.txt");
      URL parentURL = FileUtilities.getParentURL(url);
      int options = FileUtilities.OPTION_CHILDURL_FILTEREXIST_HTTP;
      URL childURL = FileUtilities.getChildURL(parentURL, "filenotexist.txt", options);
      assertNull(childURL);
   }

   /**
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL5() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL5");
      URL parentURL = new URL("https://sourceforge.net/projects/mdiutilities/");
      URL childURL = FileUtilities.getChildURL(parentURL, "titi.html");
      assertNotNull(childURL);
   }

   /**
    * Test of getChildURL method.
    */
   @Test
   public void testGetChildURL6() throws IOException {
      System.out.println("FileUtilities19Test : testGetChildURL6");
      URL parentURL = new URL("https://sourceforge.net/projects/mdiutilities/");
      int options = FileUtilities.OPTION_CHILDURL_FILTEREXIST_HTTP;
      URL childURL = FileUtilities.getChildURL(parentURL, "titi.html", options);
      assertNull("Child URL must be null", childURL);
   }
}
