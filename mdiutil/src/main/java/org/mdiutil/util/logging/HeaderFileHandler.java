/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;

/** 
 * This class allows to add a header to a File logged by a FileHandler.
 *
 * @version 0.8
 */
public class HeaderFileHandler extends FileHandler {
   private String header = null;
   private volatile boolean isStarted = false;

   /**
    * Construct a default <tt>HeaderFileHandler</tt>.
    *
    * @exception IOException if there are IO problems opening the files.
    * @exception SecurityException if a security manager exists and if
    * the caller does not have <tt>LoggingPermission("control"))</tt>.
    * @exception NullPointerException if pattern property is an empty String.
    */
   public HeaderFileHandler() throws IOException, SecurityException {
      super();
   }

   /**
    * Initialize a <tt>HeaderFileHandler</tt> to write to the given filename pattern.
    *
    * @param pattern the pattern
    * @exception IOException if there are IO problems opening the files.
    * @exception SecurityException if a security manager exists and if the caller does not
    * have <tt>LoggingPermission("control")</tt>
    */
   public HeaderFileHandler(String pattern) throws IOException, SecurityException {
      super(pattern);
   }

   /**
    * Initialize a <tt>FileHandler</tt> to write to the given filename, with optional append.
    *
    * @param pattern the pattern
    * @exception IOException if there are IO problems opening the files.
    * @exception SecurityException if a security manager exists and if the caller does not
    * have <tt>LoggingPermission("control")</tt>
    */
   public HeaderFileHandler(String pattern, boolean append) throws IOException, SecurityException {
      super(pattern, append);
   }

    /**
     * Initialize a <tt>FileHandler</tt> to write to a set of files.  When (approximately) the given limit has been
     * written to one file, another file will be opened.  The output will cycle through a set of count files.
     * The count must be at least 1.
     *
     * @param pattern  the pattern for naming the output file
     * @param limit  the maximum number of bytes to write to any one file
     * @param count  the number of files to use
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if the caller does not have
     * <tt>LoggingPermission("control")</tt>
     * @exception IllegalArgumentException if limit < 0, or count < 1.
     */
   public HeaderFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
      super(pattern, limit, count);
   }

   /** Set the optional header for the handler.
    *
    * @param header the header
    */
   public void setHeader(String header) {
      this.header = header;
   }

   /**
    * Format and publish a <tt>LogRecord</tt>.
    *
    * @param record description of the log event
    */
   @Override
   public synchronized void publish(LogRecord record) {
      if (!isStarted && (header != null)) {
         LogRecord headerRecord = new LogRecord(record.getLevel(), header);
         super.publish(headerRecord);
         isStarted = true;
      }
      super.publish(record);
   }
}
