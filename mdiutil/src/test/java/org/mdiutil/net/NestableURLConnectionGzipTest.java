/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
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
 * Unit test for the NestableURLConnection class. Using gzip protocol.
 *
 * @since 1.2.26
 */
@RunWith(CategoryRunner.class)
@Category(cat = "net")
public class NestableURLConnectionGzipTest {

   /**
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream() throws IOException {
      System.out.println("NestableURLConnectionGzipTest : testGetInputStream");
      URL zipUrl = this.getClass().getResource("/org/mdiutil/io/resources/zipfile4.zip");
      File zipFile = FileUtilities.getFile(zipUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/directory/childDirectory/file3.txt";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
      conn.setAcceptAlternateProtocols(true);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content3", line);
      }
   }

   /**
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream2() throws IOException {
      System.out.println("NestableURLConnectionGzipTest : testGetInputStream2");
      URL zipUrl = this.getClass().getResource("/org/mdiutil/io/resources/zipfile4.zip");
      File zipFile = FileUtilities.getFile(zipUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/gzipfile.gz";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
      conn.setAcceptAlternateProtocols(true);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content for gzip file", line);
      }
   }

   /**
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream3() throws IOException {
      System.out.println("NestableURLConnectionGzipTest : testGetInputStream3");
      URL zipUrl = this.getClass().getResource("/org/mdiutil/io/resources/zipfile4.zip");
      File zipFile = FileUtilities.getFile(zipUrl);
      assertTrue("zipFile must exist", zipFile.exists());

      NestableURLConstructor constructor = new NestableURLConstructor(zipFile, false);
      constructor.addNestedEntry("gzipfile.gz");
      String urlSpec = constructor.getURLSpec();
      URL url = new URL(urlSpec);

      NestableURLConnection conn = new NestableURLConnection(url);
      conn.setAcceptAlternateProtocols(true);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content for gzip file", line);
      }
   }
}
