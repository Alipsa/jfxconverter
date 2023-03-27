/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import static org.junit.Assert.assertTrue;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit Tests for the SimpleConsoleHandler class.
 *
 * @since 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class SimpleConsoleHandlerTest {
   SimpleFormatter formatter;

   public SimpleConsoleHandlerTest() {
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

   private void removeHandlers(Logger logger) {
      Handler[] handlers = logger.getHandlers();
      if ((handlers != null) && (handlers.length != 0)) {
         for (int i = 0; i < handlers.length; i++) {
            logger.removeHandler(handlers[i]);
         }
      }

      logger.setUseParentHandlers(false);
   }

   /**
    * Test of publish method, for a Warning. it should be published on System.err.
    */
   @Test
   public void testLoggingWarning() {
      System.out.println("SimpleConsoleHandlerTest : testLoggingWarning");
      Locale.setDefault(Locale.US);
      Logger logger = Logger.getLogger(SimpleConsoleHandlerTest.class.getName());
      removeHandlers(logger);
      SimpleConsoleHandler handler = new SimpleConsoleHandler();
      logger.addHandler(handler);
      DebugStream stream = new DebugStream(System.err);
      System.setErr(stream);
      logger.log(Level.WARNING, "WARNING");
      String result = stream.getString();
      assertTrue("Must contain one new line", result.indexOf('\n') != -1);
      String firstLine = result.substring(0, result.indexOf('\n')).trim();
      assertTrue("Must finish by testLoggingWarning", firstLine.endsWith("testLoggingWarning"));
      String secondLine = result.substring(result.indexOf('\n'), result.length()).trim();
      assertTrue("Must finish by ATTENTION: WARNING", secondLine.endsWith("WARNING: WARNING"));
   }

   /**
    * Test of publish method, for an info. it should be published on System.out.
    */
   @Test
   public void testLoggingInfo() {
      System.out.println("SimpleConsoleHandlerTest : testLoggingInfo");
      Logger logger = Logger.getLogger(SimpleConsoleHandlerTest.class.getName());
      removeHandlers(logger);
      SimpleConsoleHandler handler = new SimpleConsoleHandler();
      logger.addHandler(handler);
      DebugStream stream = new DebugStream(System.out);
      System.setOut(stream);
      logger.log(Level.INFO, "INFO");
      String result = stream.getString();
      assertTrue("Must contain one new line", result.indexOf('\n') != -1);
      String firstLine = result.substring(0, result.indexOf('\n')).trim();
      assertTrue("Must finish by testLoggingInfo", firstLine.endsWith("testLoggingInfo"));
      String secondLine = result.substring(result.indexOf('\n'), result.length()).trim();
      assertTrue("Must finish by INFO: INFO", secondLine.endsWith("INFO: INFO"));
   }
}
