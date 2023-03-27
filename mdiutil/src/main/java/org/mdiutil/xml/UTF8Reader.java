/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

/**
 * This class is able to convert escaped hexadecimal UTF8 String constructions to Java Unicode
 * Strings and reverse.
 * Using this class make the process of conversion from any Java Unicode Strings to XML "acceptable"
 * Strings and reverse easy.
 * <p>
 * Remark : XML UTF8 files does not accept pure Unicode Strings if they are not encoded using UTF8
 * constructs. therefore, it is not allowed, for example, to use in a XML file the following String : &#x00e9;,
 * one should use this construct instead : "&#x00e9;", which is the escaped Unicode sequence
 * for this String.</p>
 * <ul>
 * <li>Conversion from Java Strings to hexadecimals convert Java String to escaped Unicode sequences</li>
 * <li>Conversion from hexadecimals to Java Strings to convert escaped Unicode sequences to Java Strings</li>
 * </ul>
 *
 * @version 0.9.6
 */
public class UTF8Reader {
   private static UTF8Reader reader = null;

   private UTF8Reader() {
   }

   /**
    * Return the singleton reader.
    *
    * @return the reader
    */
   public static UTF8Reader getReader() {
      if (reader == null) {
         reader = new UTF8Reader();
      }
      return reader;
   }

   /**
    * Convert a Java String to its equivalent escaped Unicode sequence.
    * <ul>
    * <li>characters in the range [0, 0x007F] will not be modified</li>
    * <li>characters outside the range [0, 0x007F] will be converted to the quivalent escaped Unicode
    * sequence</li>
    * </ul>
    *
    * <h1>Example</h1>
    * Example: a&#x00e9;a will be converted to "a&#x00e9;a"
    *
    * @param s the String
    * @return the String equivalent escaped Unicode sequence
    * @see #hexToString(String)
    */
   public String stringToHex(String s) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (c > 0x007F) {
            String hex = "0000" + Integer.toHexString(c);
            buf.append("&#x").append(hex.substring(hex.length() - 4)).append(";");
         } else {
            buf.append(c);
         }
      }
      return buf.toString();
   }

   /**
    * Convert an escaped Unicode sequence to its equivalent Java String.
    *
    * <h1>Example</h1>
    * Example: a&#x00e9;a will be converted to "a&#x00e9;a"
    *
    * @param s the escaped Unicode sequence
    * @return the Java String
    * @see #stringToHex(String)
    */
   public String hexToString(String s) {
      int i = 0;
      StringBuilder buf = new StringBuilder();

      if (s == null) {
         return s; // if null we return null (to avoid any errors)
      }
      while (i < s.length()) {
         char c = s.charAt(i);
         /* if the current character is the ampersand, it can be the beginning of a
             * Unicode sequence, we must look for a further "#x" construt
          */
         if (c == '&') {
            String _s = s.substring(i + 1, i + 3);
            if (!_s.equals("#x")) {
               // we don't find a "#x" construct, it is note an escaped Unicode sequence then
               buf.append(c);
               i++;
            } else {
               /* if we find a further "#x" construct, we must then look for the ";" marking
                * the end of the sequence.
                */
               int idx = s.indexOf(';', i + 3);
               if (idx == 0) {
                  // there is not trailing ";" character, it is not a Unicode sequence at last
                  buf.append(c);
                  i++;
               } else {
                  /* there is a trailing ";" character, wemust convert the entire escaped sequence to
                   * its equivalent character
                   */
                  int charval = 0;
                  for (int j = i + 3; j < idx; j++) {
                     int _c = s.charAt(j);
                     if (_c >= '0' && _c <= '9') {
                        charval <<= 4;
                        charval += (_c - '0');
                     } else if (_c >= 'a' && _c <= 'f') {
                        charval <<= 4;
                        charval += 10 + (_c - 'a');
                     } else if (_c >= 'A' && _c <= 'F') {
                        charval <<= 4;
                        charval += 10 + (_c - 'A');
                        continue;
                     }
                  }
                  buf.append((char) charval);
                  i = idx + 1;
               }
            }
         } else {
            // not an ampersand, so add the current character
            buf.append(c);
            i++;
         }
      }

      return buf.toString();
   }
}
