/*------------------------------------------------------------------------------
 * Copyright (C) 2013 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A SimpleFormatter which can optionnaly not present the date, the class and the method of the log record.
 *
 * @version 0.7.10
 */
public class ConfigurableFormatter extends Formatter {
   private boolean showDate = false;
   private boolean showOnTwoLines = false;
   private int levelForClass = Level.SEVERE.intValue();
   private int levelForShowLevel = Level.SEVERE.intValue();
   private final Date dat = new Date();
   private final Object args[] = new Object[1];
   private final static String format = "{0,date} {0,time}";
   private MessageFormat formatter;
   private final String lineSeparator = System.getProperty("line.separator");

   /**
    * Return true if the date must be presented.
    *
    * @return true if the date must be showing on the log
    */
   public boolean isShowingDate() {
      return showDate;
   }

   /**
    * Set if the date must be presented.
    *
    * @param showDate true if the date ust be presented
    */
   public void setShowingDate(boolean showDate) {
      this.showDate = showDate;
   }

   /**
    * Return true if the results must be presented on two lines.
    *
    * @return true if the record must be presented on two lines
    */
   public boolean isShowingOnTwoLines() {
      return showOnTwoLines;
   }

   /**
    * Set if the results must be presented on two lines.
    *
    * @param showOnTwoLines true if the results must be presented on two lines
    */
   public void setShowingOnTwoLines(boolean showOnTwoLines) {
      this.showOnTwoLines = showOnTwoLines;
   }

   /**
    * Return the record level at which the formatter must show the class and the method from which the record was emitted.
    *
    * @return the level at which the formatter must show the class and the method
    */
   public int getLevelForClass() {
      return levelForClass;
   }

   /**
    * Set the record level at which the formatter must show the class and the method from which the record was emitted.
    *
    * @param levelForClass the level at which the formatter must show the class and the method
    */
   public void setLevelForClass(int levelForClass) {
      this.levelForClass = levelForClass;
   }

   /**
    * Return the record level at which the formatter must show the level of the record.
    *
    * @return the record level at which the formatter must show the level of the record
    */
   public int getLevelForShowLevel() {
      return levelForShowLevel;
   }

   /**
    * Set the record level at which the formatter must show the level of the record.
    *
    * @param levelForShowLevel the record level at which the formatter must show the level of the record
    */
   public void setLevelForShowLevel(int levelForShowLevel) {
      this.levelForShowLevel = levelForShowLevel;
   }

   public ConfigurableFormatter() {
      super();
   }

   /**
    * Format the given LogRecord.
    *
    * @param record the log record to be formatted.
    * @return a formatted log record
    */
   @Override
   public synchronized String format(LogRecord record) {
      StringBuilder sb = new StringBuilder();
      // only show the date if we allow it
      if (showDate) {
         dat.setTime(record.getMillis());
         args[0] = dat;
         StringBuffer text = new StringBuffer();
         if (formatter == null) {
            formatter = new MessageFormat(format);
         }
         formatter.format(args, text, null);
         sb.append(text);
         sb.append(" ");
      }
      if (record.getLevel().intValue() >= levelForClass) {
         if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
            if (record.getSourceMethodName() != null) {
               sb.append(" ");
               sb.append(record.getSourceMethodName());
            }
            if (isShowingOnTwoLines()) {
               sb.append(lineSeparator);
            } else {
               sb.append(" ");
            }
            sb.append(": ");
         }
      }
      String message = formatMessage(record);
      if (record.getLevel().intValue() >= levelForShowLevel) {
         sb.append(record.getLevel().getName());
         sb.append(": ");
      }
      sb.append(message);
      sb.append(lineSeparator);
      // show the StackTrace if we have to
      if (record.getThrown() != null) {
         try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.toString());
         } catch (Exception ex) {
         }
      }
      return sb.toString();
   }
}
