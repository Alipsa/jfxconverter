/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2009, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * This class encapsulate the creation of SAX Parsers.
 * It avoids the creation of a new Parser factory or SAX parser each time XML parsing is necessary.
 *
 * @deprecated
 * @version 0.9.6
 */
public class SAXParserPool {
   private SAXParserFactory fac = null;
   private SAXParser parser;
   private static boolean isValidating = false;
   private static SAXParserPool pool = null;

   private SAXParserPool() {
      fac = SAXParserFactory.newInstance();
      fac.setValidating(isValidating);
      try {
         parser = fac.newSAXParser();
      } catch (ParserConfigurationException | SAXException e) {
         e.printStackTrace();
      }
   }

   /**
    * Return a SAX Parser pool. A new associated parser factory and parser
    * will be created only if the validating property has changed.
    *
    * @param isValidating true if the parser will be validating
    * @return the SAXParserPool
    */
   public static SAXParserPool getSAXParserPool(boolean isValidating) {
      if ((pool == null) || (SAXParserPool.isValidating != isValidating)) {
         SAXParserPool.isValidating = isValidating;
         pool = new SAXParserPool();
      }
      return pool;
   }

   /**
    * Return the parser factory.
    *
    * @return the parser factory
    */
   public SAXParserFactory getSAXParserFactory() {
      return fac;
   }

   /**
    * Return the parser.
    *
    * @return the parser
    */
   public SAXParser getSAXParser() {
      return parser;
   }
}
