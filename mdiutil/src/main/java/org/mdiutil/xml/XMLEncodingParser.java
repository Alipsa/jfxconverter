/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2014, 2015, 2016, 2019, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class get the encoding of an XML file.
 *
 * @version 1.2.39
 */
public class XMLEncodingParser {
   private static XMLInputFactory factory = null;

   private XMLEncodingParser() {
   }

   /**
    * Return the declared encoding of a file.
    *
    * @param file the file
    * @return the encoding or null if no encoding could be found
    */
   public static String getEncoding(File file) {
      try {
         URL url = file.toURI().toURL();
         return getEncoding(url);
      } catch (MalformedURLException ex) {
         return null;
      }
   }

   /**
    * Return the declared encoding of an URL.
    *
    * @param url the URL
    * @return the encoding or null if no encoding could be found
    */
   public static String getEncoding(URL url) {
      if (factory == null) {
         factory = XMLInputFactory.newFactory();
      }
      try {
         String encoding;
         try (InputStream stream = url.openStream()) {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            encoding = reader.getEncoding();
         }
         return encoding;
      } catch (IOException | XMLStreamException ex) {
         return null;
      }
   }

   /**
    * Return the declared encoding of an input stream.
    *
    * @param stream the input stream
    * @return the encoding or null if no encoding could be found
    */
   public static String getEncoding(InputStream stream) {
      if (factory == null) {
         factory = XMLInputFactory.newFactory();
      }
      try {
         XMLStreamReader reader = factory.createXMLStreamReader(stream);
         String encoding = reader.getEncoding();
         return encoding;
      } catch (XMLStreamException ex) {
         return null;
      }
   }
}
