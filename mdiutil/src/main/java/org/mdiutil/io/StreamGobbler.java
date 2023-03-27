/*------------------------------------------------------------------------------
* Copyright (C) 2018 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A class which constantly consumes the input from an InputStream, and distribute the input lines to a listener.
 *
 * @since 0.9.45
 */
public class StreamGobbler {
   private final InputStream in;
   private final GogglerRunnable runnable;
   private boolean isClosed = false;
   private Thread t = null;
   private Listener listener = null;

   /**
    * Constructor.
    *
    * @param in the InputStream
    */
   public StreamGobbler(InputStream in) {
      this.in = in;
      this.runnable = new GogglerRunnable();
   }

   /**
    * Set the InputStream listener.
    *
    * @param listener the listener
    */
   public void setListener(Listener listener) {
      this.listener = listener;
   }

   /**
    * Return the InputStream listener.
    *
    * @return the listener
    */
   public Listener getListener() {
      return listener;
   }

   /**
    * Return the underlying InputStream.
    *
    * @return the InputStream
    */
   public InputStream getInputStream() {
      return in;
   }

   /**
    * Start the Gobbler.
    */
   public void start() {
      if (t == null) {
         t = new Thread(runnable);
         t.setDaemon(true);
         t.start();
      }
   }

   /**
    * Close the Gobbler. Note that the listener won't get any new line, but the underlying InputStream won't be
    * closed.
    */
   public void close() {
      isClosed = true;
      if (listener != null) {
         listener.close();
      }
   }

   /**
    * A listener which listens to a {@link StreamGobbler} InputStream.
    *
    * @since 0.9.45
    */
   public interface Listener {
      /**
       * Fired for each line read in the InputStream.
       *
       * @param line the line
       */
      public void readLine(String line);

      /**
       * Fired when the InputStream is closed.
       */
      public void close();
   }

   private class GogglerRunnable implements Runnable {
      private BufferedReader reader = null;

      private GogglerRunnable() {
         reader = new BufferedReader(new InputStreamReader(in));
      }

      @Override
      public void run() {
         while (!isClosed) {
            try {
               String readLine = reader.readLine();
               if (readLine == null) {
                  close();
                  break;
               } else if (listener != null) {
                  listener.readLine(readLine);
               }
            } catch (IOException ex) {
               close();
               break;
            }
         }
      }

   }

}
