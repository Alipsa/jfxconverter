/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.junit.Order;
import org.mdiutil.lang.ResourceLoader;

/**
 * Unit tests for FileUtilities.
 *
 * @version 0.9.45
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities2Test {
   private static List<URL> children = null;
   private static URL url = null;
   private static URL url1 = null;
   private static String startField = null;

   public FileUtilities2Test() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
      children = null;
      url = null;
      url1 = null;
      startField = null;
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 1)
   public void testGetChildren() {
      System.out.println("FileUtilities2Test : testGetChildren");
      ResourceLoader loader = new ResourceLoader("org/mdiutil/io/resources");
      url = loader.getURL("zipfile.zip");
      startField = url.getFile();
      children = FileUtilities.getChildren(url, false);
      assertNotNull("Must have two children", children);
      assertEquals("Must have two children", 2, children.size());
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 2)
   public void testGetChildren2() throws IOException {
      System.out.println("FileUtilities2Test : testGetChildren2");
      url = children.get(0);
      String path = url.getFile();
      String expected = "file:" + startField + "!/directchild.txt";
      assertEquals("First child", expected, path);
      List<String> lines = this.getLines(url);
      assertEquals("Must have 1 line", 1, lines.size());
      assertEquals("Must have 1 line", "content0", lines.get(0));
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 3)
   public void testGetChildren3() {
      System.out.println("FileUtilities2Test : testGetChildren3");

      url = children.get(1);
      String path = url.getFile();
      String expected = "file:" + startField + "!/directory/";
      assertEquals("Second child", expected, path);
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 4)
   public void testGetChildren4() {
      System.out.println("FileUtilities2Test : testGetChildren4");

      children = FileUtilities.getChildren(url, false);
      assertNotNull("Must have 3 children", children);
      assertEquals("Must have 3 children", 3, children.size());
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 5)
   public void testGetChildren5() {
      System.out.println("FileUtilities2Test : testGetChildren5");

      url1 = children.get(0);
      String path = url1.getFile();
      String expected = "file:" + startField + "!/directory/childDirectory/";
      assertEquals("First child", expected, path);
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 6)
   public void testGetChildren6() {
      System.out.println("FileUtilities2Test : testGetChildren6");

      url = children.get(1);
      String path = url.getFile();
      String expected = "file:" + startField + "!/directory/file1.txt";
      assertEquals("Second child", expected, path);
      List<String> lines = this.getLines(url);
      assertEquals("Must have 1 line", 1, lines.size());
      assertEquals("Must have 1 line", "content1", lines.get(0));
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 7)
   public void testGetChildren7() {
      System.out.println("FileUtilities2Test : testGetChildren6");

      url = children.get(2);
      String path = url.getFile();
      String expected = "file:" + startField + "!/directory/file2.txt";
      assertEquals("Third child", expected, path);
      List<String> lines = this.getLines(url);
      assertEquals("Must have 1 line", 1, lines.size());
      assertEquals("Must have 1 line", "content2", lines.get(0));
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 8)
   public void testGetChildren8() {
      System.out.println("FileUtilities2Test : testGetChildren8");

      children = FileUtilities.getChildren(url1, false);
      assertNotNull("Must have 2 children", children);
      assertEquals("Must have 2 children", 2, children.size());
   }

   /**
    * Test of getChildren method, of class FileUtilities.
    */
   @Test
   @Order(order = 9)
   public void testGetChildren9() {
      System.out.println("FileUtilities2Test : testGetChildren9");

      url = children.get(0);
      String path = url.getFile();
      String expected = "file:" + startField + "!/directory/childDirectory/file3.txt";
      assertEquals("First child", expected, path);
      List<String> lines = this.getLines(url);
      assertEquals("Must have 1 line", 1, lines.size());
      assertEquals("Must have 1 line", "content3", lines.get(0));
   }

   private List<String> getLines(URL url) {
      try {
         List<String> lines;
         try (BufferedReader reader = getReader(url)) {
            lines = new ArrayList<>();
            String line;
            while (true) {
               line = reader.readLine();
               if (line != null) {
                  lines.add(line);
               } else {
                  break;
               }
            }
         }
         return lines;
      } catch (IOException ex) {
         return new ArrayList<>();
      }
   }

   private BufferedReader getReader(URL url) throws IOException {
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      return in;
   }
}
