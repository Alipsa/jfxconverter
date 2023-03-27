/*------------------------------------------------------------------------------
 * Copyright (C) 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import org.xml.sax.SAXException;

/**
 * Used to stop SAX parsing in the least ugly way. It is impossible to stop SAX parsing without throwing an Exception,
 * so we create a special one which only means "end of parsing".
 *
 * This is used to be able to stop the parsing programmatically by the handler beforethe end of the XML Stream. However,
 * you should think about using Pull-Parsing if you rely on this kind of behavior.
 *
 * Note that this is only useful for SAX parsing, you don't need this kind of class if you use Stax parsing.
 *
 * @version 0.9.6
 */
public class StopParsingException extends SAXException {
   private Object additionalContent = null;

   /**
    * Create an exception.
    */
   public StopParsingException() {
      super("Normal Premature End of SAX Parsing");
   }

   /**
    * Create an exception, with an additional content.
    *
    * @param additionalContent the additional content
    */
   public StopParsingException(Object additionalContent) {
      super("Normal Premature End of SAX Parsing");
      this.additionalContent = additionalContent;
   }

   /**
    * Return the exception additional content.
    *
    * @return the exception additional content
    */
   public Object getAdditionalContent() {
      return additionalContent;
   }
}
