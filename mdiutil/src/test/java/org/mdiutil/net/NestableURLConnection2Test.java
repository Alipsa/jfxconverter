/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
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
import java.net.URLConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit test for the NestableURLConnection class.
 *
 * @since 1.2.4
 */
@RunWith(CategoryRunner.class)
@Category(cat = "net")
public class NestableURLConnection2Test {

   /**
    * Test of class NestableURLConnection.
    */
   @Test
   public void testGetInputStream() throws IOException {
      System.out.println("NestableURLConnection2Test : testGetInputStream");
      File zipFile = FileUtilities.getFile(this.getClass().getResource("/org/mdiutil/io/resources/zipfile.zip"));
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/directory/childDirectory/file3.txt";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
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
      System.out.println("NestableURLConnection2Test : testGetInputStream2");
      URL zipfile3Url = this.getClass().getResource("/org/mdiutil/io/resources/zipfile3.zip");
      assertNotNull("zipfile3.zip was not found", zipfile3Url);
      File zipFile = FileUtilities.getFile(zipfile3Url);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/directory/childDirectory/file3.txt";
      URL url = new URL(urlSpec);
      NestableURLConnection conn = new NestableURLConnection(url);
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content3", line);
      }
   }

   /**
    * Test of class URLConnection.
    */
   @Test
   public void testGetInputStreamFromURLConnection() throws IOException {
      System.out.println("NestableURLConnection2Test : testGetInputStreamFromURLConnection");
      URL zipfileUrl = this.getClass().getResource("/org/mdiutil/io/resources/zipfile.zip");
      File zipFile = FileUtilities.getFile(zipfileUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/directory/childDirectory/file3.txt";
      URL url = new URL(urlSpec);
      URLConnection conn = url.openConnection();
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content3", line);
      }
   }

   /**
    * Test of class URLConnection.
    */
   @Test
   public void testGetInputStreamFromURLConnection2() throws IOException {
      System.out.println("NestableURLConnection2Test : testGetInputStreamFromURLConnection2");
      // File dir = new File(System.getProperty("user.dir"));
      //File zipFile = new File(dir, "test/org/mdiutil/io/resources/zipfile3.zip");
      URL zipfileUrl = this.getClass().getResource("/org/mdiutil/io/resources/zipfile3.zip");
      File zipFile = FileUtilities.getFile(zipfileUrl);
      assertTrue("zipFile must exist", zipFile.exists());
      String zipfileURL = zipFile.toURI().toURL().toString();
      String urlSpec = "jar:" + zipfileURL + "!/directory/childDirectory/file3.txt";
      //urlSpec = urlSpec.substring(0, urlSpec.indexOf('/')) + urlSpec.substring(urlSpec.indexOf('/') + 1);
      URL url = new URL(urlSpec);
      URLConnection conn = url.openConnection();
      InputStream stream = conn.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         String line = reader.readLine();
         assertEquals("First line", "content3", line);
      }
   }
}
