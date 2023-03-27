/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.util.List;

/**
 * A handler which is able to customize how to react to Exceptions or SAX Exceptinos encountedred during the SAX parsing.
 *
 * @since 0.7.14
 */
public interface ParserExceptionListener {
   /**
    * Handle SAX Exceptions encountered after an XML parsing.
    *
    * @param title the title of the Exception
    * @param exceptions the exception results
    */
   public void handleExceptionList(String title, List<ResolverSAXHandler.ExceptionResult> exceptions);

   /**
    * Handle a Throwable Stack Trace. The simplest way to do it is <code>e.printStackTrace()</code>.
    *
    * @param title the title of the Exception
    * @param e the Throwable
    */
   public void handleException(String title, Throwable e);

   /**
    * Handle an Exception message.
    *
    * @param message the message
    */
   public void handleException(String message);
}
