/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertTrue;
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
import org.mdiutil.lang.ResourceLoader;

/**
 * Unit tests for FileUtilities. Unit tests in the context of zip entries.
 *
 * @version 1.2.24
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities14Test {
   public FileUtilities14Test() {
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
    * Test of getJarEntryURL method, of class FileUtilities.
    */
   @Test
   public void testGetJarEntryURL() throws IOException {
      System.out.println("FileUtilities14Test : testGetJarEntryURL");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      URL childURL = FileUtilities.getJarEntryURL(url, "directchild.txt");
      String protocol = childURL.getProtocol();
      String file = childURL.getFile();
      assertEquals("Protocol", "jar", protocol);
      assertTrue("File", file.startsWith("file:"));
      assertTrue("File", file.endsWith("zipfile.zip!/directchild.txt"));
   }

   /**
    * Test of getJarEntryURL method, of class FileUtilities.
    */
   @Test
   public void testGetJarEntryURL2() throws IOException {
      System.out.println("FileUtilities14Test : testGetJarEntryURL2");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      URL childURL = FileUtilities.getJarEntryURL(url, "directory/file1.txt");
      String protocol = childURL.getProtocol();
      String file = childURL.getFile();
      assertEquals("Protocol", "jar", protocol);
      assertTrue("File", file.startsWith("file:"));
      assertTrue("File", file.endsWith("zipfile.zip!/directory/file1.txt"));
   }

   /**
    * Test of getParentURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetParentURL() throws IOException {
      System.out.println("FileUtilitiesTest : testGetParentURL");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      URL childURL = FileUtilities.getJarEntryURL(url, "directory/file1.txt");
      URL parentURL = FileUtilities.getParentURL(childURL);
      assertNotNull("parentURL must not be null", parentURL);
      String protocol = parentURL.getProtocol();
      String file = parentURL.getFile();
      assertEquals("Protocol", "jar", protocol);
      assertTrue("File", file.startsWith("file:"));
      assertTrue("File", file.endsWith("zipfile.zip!/directory"));
   }

   /**
    * Test of getParentURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetParentURL2() throws IOException {
      System.out.println("FileUtilitiesTest : testGetParentURL2");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      URL url = loader.getURL("zipfile.zip");
      URL childURL = FileUtilities.getJarEntryURL(url, "directchild.txt");
      URL parentURL = FileUtilities.getParentURL(childURL);
      assertNotNull("parentURL must not be null", parentURL);
      String protocol = parentURL.getProtocol();
      String file = parentURL.getFile();
      assertEquals("Protocol", "jar", protocol);
      assertTrue("File", file.startsWith("file:"));
      assertTrue("File", file.endsWith("zipfile.zip!"));
   }
}
