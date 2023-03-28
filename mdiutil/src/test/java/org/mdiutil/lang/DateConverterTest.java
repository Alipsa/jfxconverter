/*------------------------------------------------------------------------------
 * Copyright (C) 2020, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.StringTokenizer;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class DateConverterTest {

   /**
    * Test of convertWMIC method, of class DateConverter.
    */
   @Test
   public void testConvertWMIC() {
      System.out.println("DateConverter : testConvertWMIC");
      String wmic = "20200211094245.978966+060";
      long ms = DateConverter.convertWMIC(wmic);
      assertEquals("Long time value", 1581410566028L, ms);
   }

   /**
    * Test of convertWMIC method, of class DateConverter.
    * TODO: does not work properly (2 days off)
    */
   @Test
   @Ignore
   public void testConvertWMIC2() throws Exception {
      if (SystemUtils.isWindowsPlatform()) {
         System.out.println("DateConverter : testConvertWMIC2");
         String WMICTASK = "wmic process get processid,name,creationdate";
         Process p = Runtime.getRuntime().exec(WMICTASK);
         long time = System.currentTimeMillis();
         String wmic = null;
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               StringTokenizer tok = new StringTokenizer(line, " ");
               if (line.isEmpty() || tok.countTokens() < 3) {
                  continue;
               }
               String wmicDate = tok.nextToken();
               String name = tok.nextToken();
               if (name.equalsIgnoreCase("wmic.exe")) {
                  wmic = wmicDate;
                  break;
               }
            }
         }
         long ms = DateConverter.convertWMIC(wmic);
         Date expectedDate = new Date(time);
         Date actualDate = new Date(ms);
         assertEquals(expectedDate, actualDate);
         assertEquals("Long time values must be approximately equal, diff was " , time, ms, 500);
      }
   }

   /**
    * Test of convertFromlStart method, of class DateConverter.
    */
   @Test
   public void testConvertFromlstart() {
      System.out.println("DateConverter : testConvertFromlstart");
      long ms = DateConverter.convertFromlStart("Jun", "7", "01:29:38", "2016");
      assertEquals("Long time value", 1465342178050L, ms);
   }
}
