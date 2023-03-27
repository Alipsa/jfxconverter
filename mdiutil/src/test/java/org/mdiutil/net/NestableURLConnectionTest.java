/*------------------------------------------------------------------------------
 * Copyright (C) 2015, 2016, 2017, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.net;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit test for the NestableURLConnection class.
 *
 * @version 1.2.10
 */
@RunWith(CategoryRunner.class)
@Category(cat = "net")
public class NestableURLConnectionTest {

   public NestableURLConnectionTest() {
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
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream() throws IOException {
      System.out.println("NestableURLConnectionTest : testGetInputStream");
      ZipStreamHandlerFactory.installFactory();

      URL zipUrl = this.getClass().getResource("/org/mdiutil/io/resources/readerParent.zip");
      File zipFile = FileUtilities.getFile(zipUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "zip:zip:" + zipfileURL + "!/reader.zip!/reader.txt";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "HERVE GIROD", line);
      }
   }

   /**
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream2() throws IOException {
      System.out.println("NestableURLConnectionTest : testGetInputStream2");
      ZipStreamHandlerFactory.installFactory();

      URL zipUrl = this.getClass().getResource("/org/mdiutil/io/resources/zippedFile2.zip");
      File zipFile = FileUtilities.getFile(zipUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "zip:" + zipfileURL + "!/reader.txt";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "HERVE GIROD", line);
      }
   }
}
