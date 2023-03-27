/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

/**
 * An hexadecimal Encoder/Decoder. This class is used to encode and decode data in hexadecimal format.
 *
 * @version 1.2.27
 */
public class HexaDecoder {
   private static final char DIG[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   private HexaDecoder() {
   }

   /**
    * Parse a float expressed as a float value, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param floatS the float value as a String
    * @return the float value
    */
   public static float parseFloat(String floatS) throws NumberFormatException {
      if (!floatS.trim().isEmpty()) {
         float floatValue;
         if (isHexadecimal(floatS)) {
            Long l = Long.parseLong(floatS, 16);
            floatValue = Float.intBitsToFloat(l.intValue());
         } else {
            floatValue = Float.parseFloat(floatS);
         }
         return floatValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }
   
   /**
    * Parse a double expressed as a double value, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param doubleS the double value as a String
    * @return the double value
    */
   public static double parseDouble(String doubleS) throws NumberFormatException {
      if (!doubleS.trim().isEmpty()) {
         double doubleValue;
         if (isHexadecimal(doubleS)) {
            Long l = Long.parseLong(doubleS, 16);
            doubleValue = Double.longBitsToDouble(l);
         } else {
            doubleValue = Double.parseDouble(doubleS);
         }
         return doubleValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }   

   /**
    * Parse an int expressed as an int value, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param intS the int value as a String
    * @return the int value
    */
   public static int parseInt(String intS) throws NumberFormatException {
      if (!intS.trim().isEmpty()) {
         int intValue;
         if (isHexadecimal(intS)) {
            intValue = Integer.parseInt(intS.substring(2), 16);
         } else {
            intValue = Integer.parseInt(intS);
         }
         return intValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }

   /**
    * Parse a char expressed as a char value, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param charS the char value as a char
    * @return the int value
    */
   public static char parseChar(String charS) throws NumberFormatException {
      if (!charS.trim().isEmpty()) {
         char charValue;
         if (isHexadecimal(charS)) {
            charValue = (char) Integer.parseInt(charS.substring(2), 16);
         } else {
            charValue = (char) Integer.parseInt(charS);
         }
         return charValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }

   /**
    * Parse a long expressed as a long, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param longS the long value as a String
    * @return the long value
    */
   public static long parseLong(String longS) throws NumberFormatException {
      if (!longS.trim().isEmpty()) {
         long longValue;
         if (isHexadecimal(longS)) {
            longValue = Long.parseLong(longS.substring(2), 16);
         } else {
            longValue = Long.parseLong(longS);
         }
         return longValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }

   /**
    * Parse a short expressed as a short, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param shortS the short value as a String
    * @return the short value
    */
   public static short parseShort(String shortS) throws NumberFormatException {
      if (!shortS.trim().isEmpty()) {
         short shortValue;
         if (isHexadecimal(shortS)) {
            shortValue = Short.parseShort(shortS.substring(2), 16);
         } else {
            shortValue = Short.parseShort(shortS);
         }
         return shortValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }

   /**
    * Parse a byte expressed as a byte, or as an hexadecimal value, in the format 0x&lt;hexadecimal digits&gt;.
    *
    * @param byteS the byte value as a String
    * @return the byte value
    */
   public static byte parseByte(String byteS) throws NumberFormatException {
      if (!byteS.trim().isEmpty()) {
         byte byteValue;
         if (isHexadecimal(byteS)) {
            byteValue = Byte.parseByte(byteS.substring(2), 16);
         } else {
            byteValue = Byte.parseByte(byteS);
         }
         return byteValue;
      } else {
         throw new NumberFormatException("Unparsable empty String");
      }
   }

   /**
    * Return true if the input String represent an hexadecimal value in a hexadecimal representation. An hexadecimal representation begins with "0x"
    * or "OX".
    *
    * @param str the String
    * @return true if the String represent an hexadecimal value
    */
   private static boolean isHexadecimal(String str) {
      return str.startsWith("0x") || str.startsWith("0X");
   }

   /**
    * Encodes a byte array into an hexadecimal String format. No blanks or line breaks are inserted.
    *
    * @param in a String containing the data bytes to be encoded
    * @return a character array with the an hexadecimal String format encoded data
    */
   public static String encode(byte[] in) {
      return encode(in, in.length);
   }

   /**
    * Return the size in bits of an hexadecimal value. Note that if it preferable to not use this method at runtime, as it currently transform the
    * value to a String representation before computing the size.
    *
    * @param value the hexadecimal value
    * @return the size in bits of an hexadecimal value
    */
   public static int getSizeInBits(int value) {
      String str = Integer.toHexString(value).toUpperCase();
      return str.length() * 4;
   }

   /**
    * Return the hexadecimal String corresponding to an int value, in the format 0x&lt;Hexa string&gt;
    *
    * @param value the value as an int
    * @return the hexadecimal String
    */
   public static String toHexString(int value) {
      String str = Integer.toHexString(value).toUpperCase();
      if (str.length() % 2 != 0) {
         return "0x0" + str;
      } else {
         return "0x" + str;
      }
   }

   /**
    * Return the hexadecimal String corresponding to a long value, in the format 0x&lt;Hexa string&gt;
    *
    * @param value the value as a long
    * @return the hexadecimal String
    */
   public static String toHexString(long value) {
      String str = Long.toHexString(value).toUpperCase();
      if (str.length() % 2 != 0) {
         return "0x0" + str;
      } else {
         return "0x" + str;
      }
   }

   /**
    * Encodes a byte array into an hexadecimal String format. No blanks or line breaks are inserted.
    *
    * @param in an array containing the data bytes to be encoded
    * @param length number of bytes to process in <code>in</code>
    * @return a String with the hexadecimal String format encoded data
    */
   public static String encode(byte[] in, int length) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < length; ++i) {
         String str = Integer.toHexString(in[i]).toUpperCase();
         int strLength = str.length();
         if (strLength > 2) {
            String subStr = str.substring(strLength - 2, strLength);
            buf.append(subStr);
         } else if (strLength == 1) {
            buf.append("0").append(str);
         } else {
            buf.append(str);
         }
      }
      return buf.toString();
   }

   /**
    * Decodes a byte array from an hexadecimal String format.
    *
    * @param s an hexadecimal String to be decoded.
    * @return an array containing the decoded data bytes
    * @throws IllegalArgumentException if the input is not valid hexadecimal String format encoded data
    */
   public static byte[] decode(String s) {
      byte[] byteArray = new byte[s.length() / 2];
      int i = 0;
      int j = 0;

      // the < which was there before was wrong, the last byte was to read
      while (i <= s.length() - 2) {
         char c = (char) Integer.parseInt(s.substring(i, i + 2), 16);

         byteArray[j] = (byte) c;
         i += 2;
         j++;
      }
      return byteArray;
   }

   /**
    * A custom decoding function usable for Debugging.
    *
    * @param b the byte
    * @return the decoded String
    */
   public static String byteToHex(byte b) {
      if (b > -1 && b < 16) {
         return "0" + DIG[b];
      } else {
         int dec = b;
         if (dec < 0) {
            dec += 256;
         }
         String hex = decToHex(dec);
         return hex;
      }
   }

   private static String decToHex(int dec) {
      int input;
      String hex = "";
      while (dec > 0) {
         input = dec % 16;
         hex = DIG[input] + hex;
         dec = dec / 16;
      }
      return hex;
   }
}
