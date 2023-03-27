/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * A blocking TimerTask. Contrary to a TimerTask, the method which will run after the timer has elapsed
 * is the {@link #runImpl()} rather than {@link #run()}.
 *
 * The {@link #await()} method will block in the calling Thread until the timer is run (so after the timer has
 * elapsed). However if you don't call the {@link #await()} method, the BlockingTimerTask will work just like a
 * <code>TimerTask</code>.
 *
 * You must be aware that if you have a periodic timer, the {@link #await()} method will block for each iteration
 * of the timer loop, which might not be what you want.
 *
 * @since 1.2.28
 */
public abstract class BlockingTimerTask extends TimerTask {
   private final CountDownLatch countDownLatch = new CountDownLatch(1);

   @Override
   public boolean cancel() {
      boolean result = super.cancel();
      if (countDownLatch.getCount() != 0) {
         countDownLatch.countDown();
      }
      return result;
   }

   /**
    * The code to be run after the timer has elapsed.
    */
   public abstract void runImpl();

   /**
    * The action to be performed by this timer task.
    */
   @Override
   public final void run() {
      runImpl();
      if (countDownLatch.getCount() != 0) {
         countDownLatch.countDown();
      }
   }

   /**
    * Await for the timer to elaapse.
    *
    * @throws InterruptedException
    */
   public void await() throws InterruptedException {
      countDownLatch.await();
   }
}
