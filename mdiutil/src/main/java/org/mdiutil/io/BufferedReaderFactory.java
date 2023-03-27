/*------------------------------------------------------------------------------
* Copyright (C) 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.BufferedReader;
import java.io.Reader;

/**
 * A factory which can creates <code>BufferedReader</code>s with input readers.
 *
 * <h1>Example</h1>
 * Suppose that we want to get a reader which would read files with XML syntax and escape "&lt;" and "&gt;" characters. We could perform:
 * <pre>
 * BufferedReaderFactory bufFac = new FilteredReaderFactory() {
 *   public BufferedReader createReader(Reader in, int sz) {
 *      BufferedReader reader = new FilteredBufferedReader(in, sz) {
 *        public String filterLine(String line) {
 *          line = line.replaceAll("&lt;", "&#x0026;lt;").replaceAll("&gt;", "&#x0026;gt;");
 *          return line;
 *        }
 *      };
 *      return reader;
 *   }
 * };
 * </pre>
 *
 *
 * @since 0.9.8
 */
public abstract class BufferedReaderFactory {
   /**
    * The default character buffer size, it was chosen to be equal to the one used in the BufferedReader class.
    */
   private static final int defaultCharBufferSize = 8192;

   /**
    * Create a BufferedReader. Defer to {@link #createReader(Reader, int)} with 8192 as the character buffer size.
    *
    * @param in the input reader
    * @return the BufferedReader
    */
   public BufferedReader createReader(Reader in) {
      return createReader(in, defaultCharBufferSize);
   }

   /**
    * Create a BufferedReader.
    *
    * @param in the input reader
    * @param sz the size of the buffer
    * @return the BufferedReader
    */
   public abstract BufferedReader createReader(Reader in, int sz);
}
