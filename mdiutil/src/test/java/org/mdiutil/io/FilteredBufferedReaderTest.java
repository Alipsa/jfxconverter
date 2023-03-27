/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2011, 2016, 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * Unit Tests for the FilteredBufferedReader, which allows to filter lines read in the stream.
 *
 * @version 1.2.4
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FilteredBufferedReaderTest {
   private static String sep = System.lineSeparator();

   @AfterClass
   public static void tearDownClass() {
      sep = null;
   }


   /**
    * Test of readLine method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testReadLine() {
      System.out.println("FilteredBufferedReaderTest : testReadLine");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         String line;
         int l = 0;
         while ((line = reader.readLine()) != null) {
            if (l == 0) {
               assertEquals(line, "herve girod");
            } else if (l == 1) {
               assertEquals(line, "essai 1");
            } else if (l == 2) {
               assertEquals(line, "essai 2");
            }
            l++;
         }
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }

   /**
    * Test of read method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testRead() {
      System.out.println("FilteredBufferedReaderTest : testRead");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         StringBuilder buf = new StringBuilder();
         while (true) {
            int i = reader.read();
            if (i == -1) {
               break;
            }
            buf.append((char) i);
         }
         assertEquals(buf.toString(), "herve girod" + sep + "essai 1" + sep + "essai 2");
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }

   /**
    * Test of readBuffer method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testReadBuffer() {
      System.out.println("FilteredBufferedReaderTest : testReadBuffer");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         char[] cbuf = new char[20];
         StringBuilder buf = new StringBuilder();
         while (true) {
            int i = reader.read(cbuf);
            if (i == -1) {
               break;
            }
            buf.append(cbuf, 0, i);
         }
         assertEquals(buf.toString(), "herve girod" + sep + "essai 1" + sep + "essai 2");
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }

   /**
    * Test of mark and reset method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testMark1() {
      System.out.println("FilteredBufferedReaderTest : testMark1");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         boolean first = true;
         String line;
         int l = 0;
         while ((line = reader.readLine()) != null) {
            if (l == 0) {
               assertEquals(line, "herve girod");
            } else if (l == 1) {
               assertEquals(line, "essai 1");
            } else if (l == 2) {
               assertEquals(line, "essai 2");
            } else if (l == 3) {
               assertEquals(line, "essai 1");
            } else if (l == 4) {
               assertEquals(line, "essai 2");
            }
            if (first && (line.equals("herve girod"))) {
               reader.mark(100);
            } else if (first && (line.equals("essai 2"))) {
               reader.reset();
               first = false;
            }
            l++;
         }
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }

   /**
    * Test of mark and reset method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testMark2() {
      System.out.println("FilteredBufferedReaderTest : testMark2");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader2.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         boolean first = true;
         StringBuilder buf = new StringBuilder();
         while (true) {
            int i = reader.read();
            if (i == -1) {
               break;
            }
            char c = (char) i;
            buf.append(c);
            if (first && (c == '1')) {
               reader.mark(100);
            } else if (first && (c == '2')) {
               reader.reset();
               first = false;
            }
         }
         String expected = "herve girod" + sep + "line a 1 essai" + sep + "line 2 essai" + sep + "line 2 essai" + sep + "line 2 b";
         assertEquals(buf.toString(), expected);
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }

   /**
    * Test of skip method, of class girod.util.io.FilteredBufferedReader.
    */
   @Test
   public void testSkip() {
      System.out.println("FilteredBufferedReaderTest : testSkip");
      try {
         InputStream stream = this.getClass().getResourceAsStream("resources/filterreader.txt");
         // readLine
         BufferedReader reader = new FilteredBufferedReader(new InputStreamReader(stream)) {
            public String filterLine(String line) {
               return line.trim().toLowerCase();
            }
         };
         StringBuilder buf = new StringBuilder();
         long actualSkipped = reader.skip(14);
         assertEquals(14, actualSkipped);
         while (true) {
            int i = reader.read();
            if (i == -1) {
               break;
            }
            buf.append((char) i);
         }
         // skip will be different in windows compared to linux due to CRLF vs LF
         if (SystemUtils.isWindowsPlatform())
            assertEquals(buf.toString(), "ssai 1" + sep + "essai 2");
         else
            assertEquals(buf.toString(), "sai 1" + sep + "essai 2");
         reader.close();
      } catch (IOException e) {
         fail("IOException");
      }
   }
}
