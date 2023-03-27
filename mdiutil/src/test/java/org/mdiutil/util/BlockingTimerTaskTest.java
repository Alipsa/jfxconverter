/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @since 1.2.28
 */
public class BlockingTimerTaskTest {

   public BlockingTimerTaskTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of run method, of class BlockingTimerTask.
    */
   @Test
   public void testRun() {
      System.out.println("BlockingTimerTaskTest : testRun");
      BlockingTimerTask instance = new BlockingTimerTask() {
         @Override
         public void runImpl() {
         }
      };
      long time = System.currentTimeMillis();
      Timer timer = new Timer();
      timer.schedule(instance, 1000);
      try {
         instance.await();
      } catch (InterruptedException ex) {
         fail("Must not be interrupted");
      }
      time = System.currentTimeMillis() - time;
      assertTrue("Time must be less approx 1000 ms", time < 1050);
      assertTrue("Time must be less approx 1000 ms", time > 950);
   }

   /**
    * Test of run method, of class BlockingTimerTask.
    */
   @Test
   public void testRun2() {
      System.out.println("BlockingTimerTaskTest : testRun2");
      BlockingTimerTask instance = new BlockingTimerTask() {
         @Override
         public void runImpl() {
         }
      };
      long time = System.currentTimeMillis();
      Timer timer = new Timer();
      timer.schedule(instance, 1000);
      time = System.currentTimeMillis() - time;
      assertTrue("Time must be less less than 50 ms", time < 50);
   }

   /**
    * Test of cancel method, of class BlockingTimerTask.
    */
   @Test
   public void testCancel() {
      System.out.println("BlockingTimerTaskTest : testCancel");
      BlockingTimerTask instance = new BlockingTimerTask() {
         @Override
         public void runImpl() {
         }
      };
      TimerTask instance2 = new TimerTask() {
         @Override
         public void run() {
            instance.cancel();
         }
      };
      long time = System.currentTimeMillis();
      Timer timer = new Timer();
      timer.schedule(instance, 1000);
      timer.schedule(instance2, 100);
      try {
         instance.await();
      } catch (InterruptedException ex) {
         fail("Must not be interrupted");
      }
      time = System.currentTimeMillis() - time;
      assertTrue("Time must be less less than 150 ms", time < 150);
   }

}
