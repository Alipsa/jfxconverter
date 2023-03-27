/*------------------------------------------------------------------------------
 * Copyright (C) 2013 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/** A PrintStream which print on System.out or System.err, but also keep the recods in a StringBuilder.
 *
 * @since 0.7.4
 */
public class DebugStream extends PrintStream {
   private StringBuilder buf = new StringBuilder();

   public DebugStream(OutputStream out) {
      super(out);
   }

   /** Return the result.
    */
   public String getString() {
      return buf.toString();
   }

   @Override
   public void write(byte[] b) throws IOException {
      super.write(b);
      String str = new String(b);
      buf.append(str);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      super.write(b, off, len);
      String str = new String(b, off, len);
      buf.append(str);
   }

   @Override
   public void write(int b) {
      super.write(b);
      byte[] bytes = new byte[1];
      bytes[0] = (byte)b;
      String str = new String(bytes);
      buf.append(str);
   }
}
