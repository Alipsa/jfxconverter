/*
 * Copyright 1998-2007 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright 2010, 2011, 2012, 2016 Herve Girod
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package org.mdiutil.net;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.BitSet;

/**
 * A class that contains useful routines common for encoding and decoding file names to/from UTF8.
 *
 * @version 0.9.5
 */
public class ParseUtil {
   private static final BitSet ENCODED_IN_PATH;

   static {
      ENCODED_IN_PATH = new BitSet(256);

      // Set the bits corresponding to characters that are encoded in the
      // path component of a URI.
      // These characters are reserved in the path segment as described in
      // RFC2396 section 3.3.
      ENCODED_IN_PATH.set('=');
      ENCODED_IN_PATH.set(';');
      ENCODED_IN_PATH.set('?');
      ENCODED_IN_PATH.set('/');

      // These characters are defined as excluded in RFC2396 section 2.4.3
      // and must be escaped if they occur in the data part of a URI.
      ENCODED_IN_PATH.set('#');
      ENCODED_IN_PATH.set(' ');
      ENCODED_IN_PATH.set('<');
      ENCODED_IN_PATH.set('>');
      ENCODED_IN_PATH.set('%');
      ENCODED_IN_PATH.set('"');
      ENCODED_IN_PATH.set('{');
      ENCODED_IN_PATH.set('}');
      ENCODED_IN_PATH.set('|');
      ENCODED_IN_PATH.set('\\');
      ENCODED_IN_PATH.set('^');
      ENCODED_IN_PATH.set('[');
      ENCODED_IN_PATH.set(']');
      ENCODED_IN_PATH.set('`');

      // US ASCII control characters 00-1F and 7F.
      for (int i = 0; i < 32; i++) {
         ENCODED_IN_PATH.set(i);
      }
      ENCODED_IN_PATH.set(127);
   }

   /**
    * Constructs an encoded version of the specified path string suitable for use in the construction of a URL.
    * A platform-dependent File.separatorChar will be used.
    *
    * @param path the path to escape
    * @return the path encoded version
    *
    * @see #decodePath(String)
    */
   public static String encodePath(String path) {
      return encodePath(path, true);
   }

   /**
    * Constructs an encoded version of the specified path string suitable for use in the construction of a URL.
    *
    * A path separator is replaced by a forward slash. The string is UTF8
    * encoded. The % escape sequence is used for characters that are above
    * 0x7F or those defined in RFC2396 as reserved or excluded in the path
    * component of a URL.
    *
    * @param path the path to escape
    * @param flag indicates whether path uses platform dependent File.separatorChar or not. True indicates path uses platform
    * dependent File.separatorChar
    * @return the path encoded version
    *
    * @see #decodePath(String)
    */
   public static String encodePath(String path, boolean flag) {
      char[] retCC = new char[path.length() * 2 + 16];
      int retLen = 0;
      char[] pathCC = path.toCharArray();

      int n = path.length();
      for (int i = 0; i < n; i++) {
         char c = pathCC[i];
         if ((!flag && c == '/') || (flag && c == File.separatorChar)) {
            retCC[retLen++] = '/';
         } else {
            if (c <= 0x007F) {
               if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                  retCC[retLen++] = c;
               } else if (ENCODED_IN_PATH.get(c)) {
                  retLen = escape(retCC, c, retLen);
               } else {
                  retCC[retLen++] = c;
               }
            } else if (c > 0x07FF) {
               retLen = escape(retCC, (char) (0xE0 | ((c >> 12) & 0x0F)), retLen);
               retLen = escape(retCC, (char) (0x80 | ((c >> 6) & 0x3F)), retLen);
               retLen = escape(retCC, (char) (0x80 | ((c >> 0) & 0x3F)), retLen);
            } else {
               retLen = escape(retCC, (char) (0xC0 | ((c >> 6) & 0x1F)), retLen);
               retLen = escape(retCC, (char) (0x80 | ((c >> 0) & 0x3F)), retLen);
            }
         }
         //worst case scenario for character [0x7ff-] every single
         //character will be encoded into 9 characters.
         if (retLen + 9 > retCC.length) {
            int newLen = retCC.length * 2 + 16;
            if (newLen < 0) {
               newLen = Integer.MAX_VALUE;
            }
            char[] buf = new char[newLen];
            System.arraycopy(retCC, 0, buf, 0, retLen);
            retCC = buf;
         }
      }
      return new String(retCC, 0, retLen);
   }

   /**
    * Appends the URL escape sequence for the specified char to the specified StringBuffer.
    */
   private static int escape(char[] cc, char c, int index) {
      cc[index++] = '%';
      cc[index++] = Character.forDigit((c >> 4) & 0xF, 16);
      cc[index++] = Character.forDigit(c & 0xF, 16);
      return index;
   }

   /**
    * Un-escape and return the character at position i in string s.
    */
   private static byte unescape(String s, int i) {
      return (byte) Integer.parseInt(s.substring(i + 1, i + 3), 16);
   }

   /**
    * Returns a new String constructed from the specified String by replacing the URL escape sequences and UTF8 encoding with the
    * characters they represent. The resulting String will be a valid fileName.
    *
    * @param path the path to get from its escaped value
    * @return the resulting String
    *
    * @see #encodePath(String)
    */
   public static String decodePath(String path) {
      int n = path.length();
      if ((n == 0) || (path.indexOf('%') < 0)) {
         return path;
      }

      StringBuilder sb = new StringBuilder(n);
      ByteBuffer bb = ByteBuffer.allocate(n);
      CharBuffer cb = CharBuffer.allocate(n);
      CharsetDecoder dec = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);

      char c = path.charAt(0);
      for (int i = 0; i < n;) {
         assert c == path.charAt(i);
         if (c != '%') {
            sb.append(c);
            if (++i >= n) {
               break;
            }
            c = path.charAt(i);
            continue;
         }
         bb.clear();
         int ui = i;
         for (;;) {
            assert (n - i >= 2);
            try {
               bb.put(unescape(path, i));
            } catch (NumberFormatException e) {
               throw new IllegalArgumentException();
            }
            i += 3;
            if (i >= n) {
               break;
            }
            c = path.charAt(i);
            if (c != '%') {
               break;
            }
         }
         bb.flip();
         cb.clear();
         dec.reset();
         CoderResult cr = dec.decode(bb, cb, true);
         if (cr.isError()) {
            throw new IllegalArgumentException("Error decoding percent encoded characters");
         }
         cr = dec.flush(cb);
         if (cr.isError()) {
            throw new IllegalArgumentException("Error decoding percent encoded characters");
         }
         sb.append(cb.flip().toString());
      }

      return sb.toString();
   }

   /**
    * Returns a canonical version of the specified string for a path name.
    * <ul>
    * <li>Remove embedded /../</li>
    * <li>Remove embedded /./</li>
    * <li>Remove trailing ..</li>
    * <li>Remove trailing .</li>
    * </ul>
    *
    * @param file the file
    * @return the canonical version of the file path
    */
   public static String canonizeString(String file) {
      int i;
      int lim;

      // Remove embedded /../
      while ((i = file.indexOf("/../")) >= 0) {
         if ((lim = file.lastIndexOf('/', i - 1)) >= 0) {
            file = file.substring(0, lim) + file.substring(i + 3);
         } else {
            file = file.substring(i + 3);
         }
      }
      // Remove embedded /./
      while ((i = file.indexOf("/./")) >= 0) {
         file = file.substring(0, i) + file.substring(i + 2);
      }
      // Remove trailing ..
      while (file.endsWith("/..")) {
         i = file.indexOf("/..");
         if ((lim = file.lastIndexOf('/', i - 1)) >= 0) {
            file = file.substring(0, lim + 1);
         } else {
            file = file.substring(0, i);
         }
      }
      // Remove trailing .
      if (file.endsWith("/.")) {
         file = file.substring(0, file.length() - 1);
      }

      return file;
   }

   /**
    * Return an escaped UTF8 URL from a file definition.
    *
    * @param file the File
    * @return the URL
    * @throws MalformedURLException
    */
   public static URL fileToEncodedURL(File file) throws MalformedURLException {
      String path = file.getAbsolutePath();
      path = ParseUtil.encodePath(path);
      if (!path.startsWith("/")) {
         path = "/" + path;
      }
      if (!path.endsWith("/") && file.isDirectory()) {
         path = path + "/";
      }
      return new URL("file", "", path);
   }
}
