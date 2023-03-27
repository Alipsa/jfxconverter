/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A default ParserExceptionListener, which stores exceptions and messages.
 *
 * @since 0.7.14
 */
public class DefaultParserExceptionListener implements ParserExceptionListener {
   private final List<ResolverSAXHandler.ExceptionResult> parserExceptions = new ArrayList();
   private final List<Throwable> exceptions = new ArrayList();
   private final List<String> messages = new ArrayList();

   public DefaultParserExceptionListener() {
   }

   /**
    * Clear the content of the ParserExceptionListener.
    */
   public void clear() {
      parserExceptions.clear();
      exceptions.clear();
      messages.clear();
   }

   /**
    * Return the list of Exception Results.
    *
    * @return the list of Exception Results
    */
   public List<ResolverSAXHandler.ExceptionResult> getExceptionResults() {
      return parserExceptions;
   }

   /**
    * Return the list of Throwables.
    *
    * @return the list of Throwables
    */
   public List<Throwable> getExceptions() {
      return exceptions;
   }

   /**
    * Return the list of messages.
    *
    * @return the list of messages
    */
   public List<String> getMessages() {
      return messages;
   }

   /**
    * Handle SAX Exceptions encountered after an XML parsing.
    *
    * @param title the title of the Exception
    * @param exceptions the exception results
    */
   @Override
   public void handleExceptionList(String title, List<ResolverSAXHandler.ExceptionResult> exceptions) {
      parserExceptions.addAll(exceptions);
   }

   /**
    * Handle a Throwable Stack Trace. The simplest way to do it is <code>e.printStackTrace()</code>.
    *
    * @param title the title of the Exception
    * @param e the Throwable
    */
   @Override
   public void handleException(String title, Throwable e) {
      exceptions.add(e);
   }

   /**
    * Handle an Exception message.
    *
    * @param message the message
    */
   @Override
   public void handleException(String message) {
      messages.add(message);
   }

}
