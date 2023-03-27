/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.net;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.SystemUtils;

/**
 *
 * @since 1.2.10
 */
@RunWith(CategoryRunner.class)
@Category(cat = "net")
public class NestableURLConstructor2Test {

   public NestableURLConstructor2Test() {
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
   public void testGetURLSPec() throws MalformedURLException {
      System.out.println("NestableURLConstructor2Test : testGetURLSPec");
      ZipStreamHandlerFactory.installFactory();

      File file = new File("D://samples/File.zip");
      URL url = file.toURI().toURL();
      NestableURLConstructor constructor = new NestableURLConstructor(url);
      constructor.addNestedEntry("71812_file");
      constructor.addNestedEntry("myFile.properties");
      URL theURL = constructor.getURL();
      String spec = theURL.toString();
      if(SystemUtils.isWindowsPlatform()) {
         assertEquals("URL spec", "zip:zip:file:/D:/samples/File.zip!/71812_file!/myFile.properties", spec);
      } else {
         assertTrue(spec + " does not start correctly", spec.startsWith("zip:zip:file:/"));
         assertTrue(spec + " does not end correctly", spec.endsWith("File.zip!/71812_file!/myFile.properties"));
      }
   }

}
