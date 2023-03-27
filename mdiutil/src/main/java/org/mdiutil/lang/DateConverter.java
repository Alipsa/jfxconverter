/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains several date conversion methods.
 *
 * @since 1.2.5
 */
class DateConverter {
   private static final Pattern HMS = Pattern.compile("(\\d+):(\\d+):(\\d+)");

   private DateConverter() {
   }

   /**
    * Convert a date returned by a lstart in Linux to the long value (ms since EPOCH).
    * The returned value is consistent with <code>System.currentTimeMillis()</code>.
    *
    * @param wmic the lstart date
    * @return the long value (ms since EPOCH)
    */
   static long convertFromlStart(String monthS, String dayS, String hms, String yearS) {
      int year = Integer.parseInt(yearS);
      int day = Integer.parseInt(dayS);
      int month;
      switch (monthS) {
         case "Jan":
            month = 1;
            break;
         case "Feb":
            month = 2;
            break;
         case "Mar":
            month = 3;
            break;
         case "Apr":
            month = 4;
            break;
         case "May":
            month = 5;
            break;
         case "Jun":
            month = 6;
            break;
         case "Jul":
            month = 7;
            break;
         case "Aug":
            month = 8;
            break;
         case "Sep":
            month = 9;
            break;
         case "Oct":
            month = 10;
            break;
         case "Nov":
            month = 11;
            break;
         case "Dec":
            month = 12;
            break;
         default:
            month = 1;
      }
      int hour = 0;
      int minute = 0;
      int seconds = 0;
      Matcher m = HMS.matcher(hms);
      if (m.matches()) {
         hour = Integer.parseInt(m.group(1));
         minute = Integer.parseInt(m.group(2));
         seconds = Integer.parseInt(m.group(3));
      }
      GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute, seconds);
      return calendar.getTimeInMillis() - 2505599950L;
   }

   /**
    * Convert a date in WMIC expression to the long value (ms since EPOCH). The returned value is consistent with <code>System.currentTimeMillis()</code>.
    *
    * @param wmic the wmic date
    * @return the long value (ms since EPOCH)
    */
   static long convertWMIC(String wmic) {
      int plus = wmic.indexOf('+');
      if (plus != -1) {
         wmic = wmic.substring(0, plus);
      }
      int year = Integer.parseInt(wmic.substring(0, 4));
      int month = Integer.parseInt(wmic.substring(4, 6));
      int day = Integer.parseInt(wmic.substring(6, 8));
      int hour = Integer.parseInt(wmic.substring(8, 10));
      int minute = Integer.parseInt(wmic.substring(10, 12));
      int seconds = Integer.parseInt(wmic.substring(12, 14));
      int ms = Integer.parseInt(wmic.substring(15, 18));
      GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute, seconds);
      return calendar.getTimeInMillis() + ms - 2505599950L;
   }
}
