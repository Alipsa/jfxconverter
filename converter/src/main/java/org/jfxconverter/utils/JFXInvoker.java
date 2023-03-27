/*
Copyright (c) 2017, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.jfxconverter.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 * The JFXInvoker class allows some code to be executed synchronously on the FX event thread.
 *
 * @since 0.20
 */
public class JFXInvoker {
   private static JFXInvoker invoker = null;

   private JFXInvoker() {
      // make sure that the JavaFX Platform is initialized, the Panel is not used
      JFXPanel jfxPanel = new JFXPanel();
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static final JFXInvoker getInstance() {
      if (invoker == null) {
         invoker = new JFXInvoker();
      }
      return invoker;
   }

   /**
    * Allows some code to be executed synchronously on the FX thread. Note that this code can also be safely
    * executed in the FX thread.
    *
    * @param runnable the Runnable
    * @throws Exception if the runnable thrown an exception
    */
   public void invokeBlocking(Runnable runnable) throws Exception {
      if (Platform.isFxApplicationThread()) {
         runnable.run();
      } else {
         FutureTask<?> future = new FutureTask<>(runnable, null);
         Platform.runLater(future);
         try {
            future.get();
         } catch (InterruptedException ex) {
            // we don't show any exception here, because the user is just canceling the action
         } catch (ExecutionException ex) {
            // here we try to get the ExecutionException cause, which should be the exception which originates the problem
            Exception th = ex;
            if (ex.getCause() != null) {
               th = (Exception) ex.getCause();
            }
            throw th;
         }
      }
   }
}
