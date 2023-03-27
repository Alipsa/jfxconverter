/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Utilities for the XML Unit tests.
 *
 * @version 1.2.39
 */
public class XMLTestUtilities {
   private XMLTestUtilities() {
   }

   /**
    * Return the text content from an URL.
    *
    * @param xmlURL the URL
    * @return the text content
    * @throws IOException
    */
   public static String getText(URL xmlURL) throws IOException {
      boolean first = true;
      StringBuilder buf = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(xmlURL.openStream()))) {
         while (true) {
            String line = reader.readLine();
            if (line == null) {
               break;
            }
            if (first) {
               first = false;
            } else {
               buf.append("\n");
            }
            buf.append(line);
         }
      }
      return buf.toString();
   }
   
   /**
    * Return the text content from a File.
    *
    * @param xmlFile the File
    * @return the text content
    * @throws IOException
    */
   public static String getText(File xmlFile) throws IOException {
      boolean first = true;
      StringBuilder buf = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new FileReader(xmlFile))) {
         while (true) {
            String line = reader.readLine();
            if (line == null) {
               break;
            }
            if (first) {
               first = false;
            } else {
               buf.append("\n");
            }
            buf.append(line);
         }
      }
      return buf.toString();
   }   
}
