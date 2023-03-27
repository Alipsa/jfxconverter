/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2019, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.SystemUtils;

/**
 * Unit tests for FileUtilities.
 *
 * @version 1.2.33
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities4Test {
   public FileUtilities4Test() {
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
    * Test of getDrive method, of class FileUtilities.
    */
   @Test
   public void testGetDrive() {
      if (SystemUtils.isWindowsPlatform()) {
         System.out.println("FileUtilities4Test : testGetDrive");
         File file = new File("L:/WRK/Java/test/resources/MyFile.xml");
         String drive = FileUtilities.getDrive(file);
         assertEquals("Drive name", "L:\\", drive);
      }
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound() {
      System.out.println("FileUtilities4Test : testIsURLFound");

      URL url = this.getClass().getResource("resources/filterreader.txt");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("URL must be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound2() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound2");

      File file = new File("P:/titi/toto/test/resources/MyFile.xml");
      URL url = file.toURI().toURL();
      boolean found = FileUtilities.isURLFound(url);
      assertFalse("URL must not be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound3() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound3");

      URL url = new URL("http://docs.oracle.com/javase/8/docs/api/java/awt/Button.html");
      boolean found = FileUtilities.isURLFound(url);
      assertTrue("URL must be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound4() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound4");

      URL url = new URL("https://docs.oracle1.com/javase/8/docs11/api/java/awt/Button1111.html");
      boolean found = FileUtilities.isURLFound(url);
      assertFalse("URL must not be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound5() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound5");
      URL url = new URL("http://docs.oracle1.com/javase/8/docs11/api/java/awt/Button1111.html");

      boolean found = FileUtilities.isURLFound(url);
      assertTrue("URL must be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound6() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound6");

      URL url = new URL("http://docs.oracle1.com/javase/8/docs11/api/java/awt/Button1111.html");
      boolean found = FileUtilities.isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE | FileUtilities.OPTION_FOLLOWREDIRECT);
      assertFalse("URL must not be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound7() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound7");

      URL url = new URL("http://docs.oracle1.com/javase/8/docs11/api/java/awt/Button1111.html");
      boolean found = FileUtilities.isURLFound(url, 0);
      assertFalse("URL must not be found", found);
   }

   /**
    * Test of isURLFound method, of class FileUtilities.
    */
   @Test
   public void testIsURLFound8() throws MalformedURLException {
      System.out.println("FileUtilities4Test : testIsURLFound8");

      URL url = new URL("http://docs.oracle.com/javase/8/docs/api/java/awt/Button1111.html");
      boolean found = FileUtilities.isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE);
      assertTrue("URL must be found", found);
   }

   /**
    * Test of testGetResponseCode method, of class FileUtilities.
    */
   @Test
   public void testGetResponseCode() throws IOException {
      System.out.println("FileUtilities4Test : testGetResponseCode");

      URL url = this.getClass().getResource("resources/filterreader.txt");
      int code = FileUtilities.getResponseCode(url);
      assertEquals("URL response code", -1, code);
   }

   /**
    * Test of testGetResponseCode method, of class FileUtilities.
    */
   @Test
   public void testGetResponseCode2() throws IOException {
      System.out.println("FileUtilities4Test : testGetResponseCode2");

      URL url = new URL("https://docs.oracle.com/javase/8/docs/api/java/awt/Button.html");
      int code = FileUtilities.getResponseCode(url);
      assertEquals("URL response code", 200, code);
   }

   /**
    * Test of testGetResponseCode method, of class FileUtilities.
    */
   @Test
   public void testGetResponseCode3() throws IOException {
      System.out.println("FileUtilities4Test : testGetResponseCode3");

      URL url = new URL("https://docs.oracle1.com/javase/8/docs/api/java/awt/Button1111.html");
      int code = FileUtilities.getResponseCode(url);
      assertEquals("URL response code", 404, code);
   }

   /**
    * Test of testIsAncestor method, of class FileUtilities.
    */
   @Test
   public void testIsAncestor() throws IOException {
      System.out.println("FileUtilities4Test : testIsAncestor");

      URL url = new URL("http://docs.oracle.com/javase/8/docs/api/java/awt/Button.html");
      URL ancestor = new URL("http://docs.oracle.com/javase/8/docs/api/");
      boolean isAncestor = FileUtilities.isAncestor(ancestor, url);
      assertTrue("Must be an ancestor", isAncestor);
   }
}
