/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 201 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.UnsupportedEncodingException;

/**
 * Provides several String utilities.
 *
 * @version 0.9.15
 */
public class StringUtils {
   private static final char ONE_BYTE = 0;
   private static final char TWO_BYTES = 1;
   private static final char THREE_BYTES = 2;

   private StringUtils() {
   }

   /**
    * Escape a String converting non ASCII characters to their associated XML escaped representation.
    * Defer to {@link #escapeString(String, String)} with the <code>charset</code> argument set to null.
    *
    * @param s the String
    * @return the escaped String
    */
   public static String escapeString(String s) {
      return escapeString(s, null);
   }

   /**
    * Escape a String converting non ASCII characters to their associated XML escaped representation. Note that for the
    * moment this method only handles sequences of 2 or 3 bytes (but not 4 bytes sequences).
    * See <a href="http://www.fileformat.info/info/unicode/utf8.htm" >www.fileformat.info/info/unicode/utf8.htm</a> for
    * more information.
    *
    * <h1>Example</h1>
    * Suppose that we have the small letter E with acute. The Unicode value of this letter is U+00E9. It will be converted
    * in UTF-8 as the following characters:
    * <ul>
    * <li>0xC3</li>
    * <li>0xA9</li>
    * </ul>
    * The method will return:
    * <ul>
    * <li>{@code &#x00E9;}</li>
    * </ul>
    *
    * <h1>Limitations</h1>
    * For the moment the method only work for characters on:
    * <ul>
    * <li>One byte</li>
    * <li>Two bytes</li>
    * <li>Three bytes</li>
    * </ul>
    * Characters on four bytes sequences are not handled for the moment.
    *
    * @param s the String
    * @param charset the character set or null for the UTF-8 charset
    * @return the escaped String
    * TODO: dos not work
    */
   public static String escapeString(String s, String charset) {
      if (charset != null && !charset.equals("UTF-8")) {
         try {
            byte[] array = s.getBytes(charset);
            StringBuilder buf = new StringBuilder();
            for (byte b : array) {
               char c = (char) b;
               if (c <= 0x7F) {
                  buf.append((char) c);
               } else {
                  c = (char) (c & 0xFF);
                  String hexS = Integer.toHexString(c).toUpperCase();
                  if (hexS.length() < 4) {
                     hexS = "000".substring(0, 4 - hexS.length()) + hexS;
                  }
                  hexS = "&#x" + hexS + ";";
                  buf.append(hexS);
               }
            }
            return buf.toString();
         } catch (UnsupportedEncodingException ex) {

         }
      }
      return encodeXML(s);
   }

   private String oldEncodeXMLFromUTF8(String s) {
      StringBuilder buf = new StringBuilder();
      char first = 0;
      short type = ONE_BYTE;
      boolean isSecond = false;
      char secondChar = 0;
      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (first != 0) {
            c = (char) (c & 0x3F);
            if (type == TWO_BYTES) {
               int charI = first << 6 | c;
               String hexS = Integer.toHexString(charI).toUpperCase();
               if (hexS.length() < 4) {
                  hexS = "000".substring(0, 4 - hexS.length()) + hexS;
               }
               hexS = "&#x" + hexS + ";";
               buf.append(hexS);
               first = 0;
            } else if (type == THREE_BYTES) {
               if (isSecond) {
                  int charI = first << 12 | secondChar << 6 | c;
                  String hexS = Integer.toHexString(charI).toUpperCase();
                  if (hexS.length() < 4) {
                     hexS = "000".substring(0, 4 - hexS.length()) + hexS;
                  }
                  hexS = "&#x" + hexS + ";";
                  buf.append(hexS);
                  first = 0;
                  secondChar = 0;
                  isSecond = false;
               } else {
                  secondChar = c;
                  isSecond = true;
               }
            }
         } else if (c <= 0x7F) {
            buf.append(c);
            first = 0;
            isSecond = false;
            secondChar = 0;
            type = ONE_BYTE;
         } else if (c >= 0xC2 && c <= 0xDF) {
            first = (char) (c & 0x1F);
            isSecond = false;
            secondChar = 0;
            type = TWO_BYTES;
         } else if (c >= 0xE0 && c <= 0xEF) {
            first = (char) (c & 0x0F);
            isSecond = false;
            secondChar = 0;
            type = THREE_BYTES;
         } else if (c >= 0x7F) {
            isSecond = false;
            secondChar = 0;
            type = ONE_BYTE;
            String hexS = Integer.toHexString(c).toUpperCase();
            if (hexS.length() < 4) {
               hexS = "000".substring(0, 4 - hexS.length()) + hexS;
            }
            hexS = "&#x" + hexS + ";";
            buf.append(hexS);
         }
      }
      return buf.toString();
   }
   public static String encodeXML(CharSequence s) {
      StringBuilder sb = new StringBuilder();
      int len = s.length();
      for (int i=0;i<len;i++) {
         int c = s.charAt(i);
         if (c >= 0xd800 && c <= 0xdbff && i + 1 < len) {
            c = ((c-0xd7c0)<<10) | (s.charAt(++i)&0x3ff);    // UTF16 decode
         }
         if (c < 0x80) {      // ASCII range: test most common case first
            if (c < 0x20 && (c != '\t' && c != '\r' && c != '\n')) {
               // Illegal XML character, even encoded. Skip or substitute
               sb.append("&#xfffd;");   // Unicode replacement character
            } else {
               switch(c) {
                  case '&':  sb.append("&amp;"); break;
                  case '>':  sb.append("&gt;"); break;
                  case '<':  sb.append("&lt;"); break;
                  default:   sb.append((char)c);
               }
            }
         } else if ((c >= 0xd800 && c <= 0xdfff) || c == 0xfffe || c == 0xffff) {
            // Illegal XML character, even encoded. Skip or substitute
            sb.append("&#xfffd;");   // Unicode replacement character
         } else {
            sb.append("&#x");
            String hexS = Integer.toHexString(c).toUpperCase();
            if (hexS.length() < 4) {
               hexS = "000".substring(0, 4 - hexS.length()) + hexS;
            }
            sb.append(hexS);
            sb.append(';');
         }
      }
      return sb.toString();
   }

   public static String escapeHtml(String str) {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
            sb.append("&#")
                .append((int)c)
                .append(';');
         } else {
            sb.append(c);
         }
      }
      return sb.toString();
   }
}
