/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.SAXException;

/**
 * A validator handler which wrap a ResolverSAXHandler, an add additional schema validation to the parsing.
 *
 * @version 0.9.6
 */
public class SAXValidatorHandler {
   private ResolverSAXHandler handler = null;
   private ValidatorHandler vhandler = null;

   /**
    * Constructor.
    *
    * @param handler the ResolverSAXHandler
    * @throws SAXException
    */
   public SAXValidatorHandler(ResolverSAXHandler handler) throws SAXException {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema();
      vhandler = schema.newValidatorHandler();
      vhandler.setErrorHandler(handler);
      vhandler.setContentHandler(handler);
      vhandler.setResourceResolver(handler);
      this.handler = handler;
   }

   /**
    * Return the ValidatorHandler
    *
    * @return the ValidatorHandler
    */
   public ValidatorHandler getValidatorHandler() {
      return vhandler;
   }

   /**
    * Return the wrapped ResolverSAXHandler.
    *
    * @return the wrapped ResolverSAXHandler
    */
   public ResolverSAXHandler getWrappedHandler() {
      return handler;
   }
}
