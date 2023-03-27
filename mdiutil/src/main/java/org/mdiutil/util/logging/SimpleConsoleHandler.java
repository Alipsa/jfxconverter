/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A ConsoleHandler which does print its records on the System.out Stream if they are lower than a specified level.
 * The default level for printing on the System.err Stream is WARNING.
 *
 * @version 0.7.23
 */
public class SimpleConsoleHandler extends ConsoleHandler {
   private static final int offValue = Level.OFF.intValue();
   private int errLevel = Level.WARNING.intValue();

   public SimpleConsoleHandler() {
      super();
   }

   /**
    * Set the level at which the record will be printed on the System.err. The default is the WARNING level.
    *
    * @param level the level
    */
   public void setErrLevel(int level) {
      this.errLevel = level;
   }

   /**
    * Return the level at which the record will be printed on the System.err. The default is the WARNING level.
    *
    * @return the level at which the record will be printed on the System.err
    */
   public int getErrLevel() {
      return errLevel;
   }

   /**
    * Do nothing.
    */
   @Override
   public void close() {
   }

   /**
    * Do nothing.
    */
   @Override
   public void flush() {
   }

   /**
    * Do nothing.
    */
   @Override
   protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
   }

   /**
    * Check if this <tt>Handler</tt> would actually log a given <tt>LogRecord</tt>. This method override the
    * parent method because the output writer is null.
    *
    * @return true if this <tt>Handler</tt> would actually log a given <tt>LogRecord</tt>
    */
   @Override
   public boolean isLoggable(LogRecord record) {
      if (record == null) {
         return false;
      }
      int levelValue = getLevel().intValue();
      if (record.getLevel().intValue() < levelValue || levelValue == offValue) {
         return false;
      }
      Filter filter = getFilter();
      if (filter == null) {
         return true;
      }
      return filter.isLoggable(record);
   }

   /**
    * Format and publish a <tt>LogRecord</tt> on the System.out Stream, or System.err, depending on the level of the record.
    *
    * @param record description of the log event. A null record is silently ignored and is not published
    */
   @Override
   public synchronized void publish(LogRecord record) {
      if (!isLoggable(record)) {
         return;
      }
      String msg;
      try {
         msg = getFormatter().format(record);
      } catch (Exception ex) {
         reportError(null, ex, ErrorManager.FORMAT_FAILURE);
         return;
      }
      // only emit on System.err if the level is at least equal to the errLevel
      if (record.getLevel().intValue() >= errLevel) {
         System.err.print(msg);
      } else {
         System.out.print(msg);
      }
   }
}
