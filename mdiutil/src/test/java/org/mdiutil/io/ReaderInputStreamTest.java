/*------------------------------------------------------------------------------
* Copyright (C) 2019, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class ReaderInputStreamTest {

   public ReaderInputStreamTest() {
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
    * Test of read method, of class ReaderInputStream.
    */
   @Test
   public void testRead() throws Exception {
      System.out.println("ReaderInputStreamTest : testRead");
      String text = "The quick brown fox jumped over the lazy dog";
      Reader r = new StringReader(text);
      StringBuilder buf = new StringBuilder();
      InputStream in = new ReaderInputStream(r);
      Reader r2 = new InputStreamReader(in);
      for (int c; (c = r2.read()) != -1;) {
         buf.append((char) c);
      }
      assertEquals("The Sentence should be", "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    The quick brown fox jumped over the lazy dog                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ", buf.toString());
   }

}
