/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2015, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This class is a Buffered reader which allows to filter lines read in the stream. It is
 * also possible to use either of the methods in the {@link BufferedReader} class,
 * as the result is guaranteed to behave as expected.
 * To use this class, you have to implement the {@link #filterLine(String)} method.
 *
 * For example:
 * <pre>
 *    BufferedReader reader = new FilteredBufferedReader(new FileReader(in)) {
 *       public String filterLine(String line) {
 *          return line.trim().toLowerCase();
 *       }
 *    };
 * </pre>
 *
 * In this example, each attempts to the {@link #readLine()} method will get the next
 * line in the input File, after trimming this line and transforming it to lower case.
 *
 * @version 0.9.8
 */
public abstract class FilteredBufferedReader extends BufferedReader {
   private String lastLine = null;
   private int avail = 0;
   private int pos = 0;
   private boolean finishedLine = true;
   private short finishedLinePos = 0;
   private boolean end = false;
   private String lineOnSkip = null;
   private int availOnSkip = 0;
   private int posOnSkip = 0;
   private boolean finishedLineOnSkip = true;
   private short finishedLineOnSkipPos = 0;
   private static char[] sep;

   static {
      sep = System.getProperty("line.separator").toCharArray();
   }

   /**
    * Create a buffering character-input stream that uses a default-sized input buffer.
    *
    * @param in the reader
    */
   public FilteredBufferedReader(Reader in) {
      super(in);
   }

   /**
    * Create a buffering character-input stream that uses an input buffer of the specified size.
    *
    * @param in the reader
    * @param sz the size of the buffer
    */
   public FilteredBufferedReader(Reader in, int sz) {
      super(in, sz);
   }

   /**
    * Read a line of text.
    * A line is considered to be terminated by any one of a line feed ('\n'),
    * a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
    *
    * <p>
    * Remark :As for all read methods, the lines will be filtered by the
    * {@link #filterLine(String)} method.</p>
    *
    * @return the line
    * @throws IOException
    * @see BufferedReader#readLine()
    * @see #filterLine(String)
    */
   @Override
   public String readLine() throws IOException {
      if (end) {
         return null;
      }
      String s = super.readLine();
      if (s == null) {
         end = true;
         lastLine = null;
         return null;
      } else {
         lastLine = filterLine(s);
         pos = 0;
         avail = lastLine.length();
         finishedLine = false;
         return lastLine;
      }
   }

   /**
    * This abstract method allows to filter each line which is read by this Reader.
    *
    * A straightforward implementation, which returns the input line unaffected, would be:
    * <pre>
    *  public String filterLine(String line) {
    *     return line;
    *  }
    * </pre>
    *
    * @param line the line
    * @return the filtered line
    */
   public abstract String filterLine(String line);

   /**
    * Read a single character.
    *
    * Remarks:
    * <ul>
    * <li>As for all read methods, the lines will be filtered by the {@link #filterLine(String)} method.</li>
    * <li>The read method will keep the EOL characters unaffected</li>
    * </ul>
    *
    * @return the character
    * @throws IOException
    * @see BufferedReader#read()
    * @see #filterLine(String)
    */
   @Override
   public int read() throws IOException {
      if (end) {
         return -1;
      } else if (avail > pos) {
         int c = lastLine.charAt(pos);
         pos++;
         return c;
      } else if (!finishedLine) {
         if (finishedLinePos == sep.length - 1) {
            finishedLine = true;
         }
         if (super.ready()) {
            char c = sep[finishedLinePos];
            finishedLinePos++;
            if (finishedLine) {
               finishedLinePos = 0;
            }
            return c;
         } else {
            finishedLine = true;
            return -1;
         }
      } else {
         readLine();
         if (lastLine == null) {
            return -1;
         } else {
            int c = lastLine.charAt(0);
            pos++;
            return c;
         }
      }
   }

   /**
    * Read characters into a portion of an array.
    * Remarks:
    * <ul>
    * <li>As for all read methods, the lines will be filtered by the
    * {@link #filterLine(String)} method.</li>
    * <li>The read method will keep the EOL characters unaffected</li>
    * </ul>
    *
    * @return the character
    * @throws IOException
    * @see BufferedReader#read(char[], int, int)
    * @see #filterLine(String)
    */
   @Override
   public int read(char[] cbuf, int off, int len) throws IOException {
      int _off = off;
      int toread = len;
      if (end) {
         return -1;
      } else {
         while (toread > 0) {
            if (end) {
               break;
            }
            if (avail > pos) {
               if (toread >= avail - pos) {
                  lastLine.getChars(pos, avail, cbuf, _off);
                  toread -= avail - pos;
                  _off += avail - pos;
                  pos = avail;
               } else {
                  lastLine.getChars(pos, toread, cbuf, _off);
                  _off += toread;
                  pos += toread;
                  toread = 0;
               }
            } else if (!finishedLine) {
               if (finishedLinePos == sep.length - 1) {
                  finishedLine = true;
               }
               if (super.ready()) {
                  cbuf[_off] = sep[finishedLinePos];
                  finishedLinePos++;
                  if (finishedLine) {
                     finishedLinePos = 0;
                  }
                  _off++;
                  toread--;
               } else {
                  finishedLine = true;
               }
            } else {
               readLine();
               if (lastLine == null) {
                  break;
               }
            }
         }
      }
      if (toread == len) {
         return -1;
      } else {
         return (len - toread);
      }
   }

   /**
    * Read characters into an array. This method will block until some input is available, an I/O error occurs, or the end of the stream is reached.
    * Remarks:
    * <ul>
    * <li>As for all read methods, the lines will be filtered by the
    * {@link #filterLine(String)} method.</li>
    * <li>The read method will keep the EOL characters unaffected</li>
    * </ul>
    *
    * @return the character
    * @throws IOException
    * @see BufferedReader#read(char[])
    * @see #filterLine(String)
    */
   @Override
   public int read(char[] cbuf) throws IOException {
      return read(cbuf, 0, cbuf.length);
   }

   /**
    * Tell whether this stream is ready to be read. A buffered character stream is ready if
    * the buffer is not empty, or if the underlying character stream is ready.
    *
    * @return true if this stream is ready to be read
    * @throws IOException
    * @see BufferedReader#ready()
    */
   @Override
   public boolean ready() throws IOException {
      if ((super.ready()) || (lastLine != null) || (!finishedLine)) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Skip characters. Users of this method should be aware that this implementation is not
    * efficient for wide skipping, because the stream must read the file line by line.
    *
    * @return the number of characters to skip
    * @throws IOException
    * @see BufferedReader#skip(long)
    */
   @Override
   public long skip(long n) throws IOException {
      long toread = n;
      if (end) {
         return 0;
      } else {
         while (toread > 0) {
            if (end) {
               break;
            }
            if (avail > pos) {
               if (toread >= avail - pos) {
                  toread -= avail - pos;
                  pos = avail;
               } else {
                  pos += toread;
                  toread = 0;
               }
            } else if (!finishedLine) {
               if (finishedLinePos == sep.length - 1) {
                  finishedLine = true;
               }
               if (super.ready()) {
                  finishedLinePos++;
                  if (finishedLine) {
                     finishedLinePos = 0;
                  }
                  toread--;
               } else {
                  finishedLine = true;
               }
            } else {
               readLine();
               if (lastLine == null) {
                  break;
               }
            }
         }
      }
      return n - toread;
   }

   /**
    * Mark the present position in the stream.
    * Subsequent calls to reset() will attempt to reposition the stream to this point.
    *
    * Remark: Due to the particular way this class uses to read streams, the mark will
    * effectively be defined at the end of a line if it is performed after a {@link #readLine()}
    * method.
    *
    * @throws IOException
    * @see BufferedReader#mark(int)
    */
   @Override
   public void mark(int readAheadLimit) throws IOException {
      super.mark(readAheadLimit);
      lineOnSkip = new String(lastLine);
      finishedLineOnSkip = finishedLine;
      finishedLineOnSkipPos = finishedLinePos;
      availOnSkip = avail;
      posOnSkip = pos;
   }

   /**
    * Resets the stream to the most recent mark.
    *
    * @throws IOException
    * @see #mark(int)
    * @see BufferedReader#reset()
    */
   @Override
   public void reset() throws IOException {
      super.reset();
      lastLine = new String(lineOnSkip);
      finishedLine = finishedLineOnSkip;
      finishedLinePos = finishedLineOnSkipPos;
      avail = availOnSkip;
      pos = posOnSkip;
      finishedLineOnSkip = true;
      availOnSkip = 0;
      posOnSkip = 0;
   }
}
