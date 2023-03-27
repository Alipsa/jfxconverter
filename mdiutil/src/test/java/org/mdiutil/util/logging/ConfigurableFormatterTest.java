/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import static org.junit.Assert.*;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the ConfigurableFormatter class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class ConfigurableFormatterTest {
   public ConfigurableFormatterTest() {
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
    * Test of logging in the default configuration.
    */
   @Test
   public void testLoggingDefault() {
      System.out.println("ConfigurableFormatterTest : testLoggingDefault");
      Logger logger = Logger.getLogger(ConfigurableFormatterTest.class.getName());
      removeHandlers(logger);
      SimpleConsoleHandler handler = new SimpleConsoleHandler();
      Formatter formatter = new ConfigurableFormatter();;
      handler.setFormatter(formatter);
      logger.addHandler(handler);
      DebugStream stream = new DebugStream(System.out);
      System.setOut(stream);
      logger.log(Level.INFO, "testLog");
      String result = stream.getString();
      assertTrue("Must contain one new line", result.indexOf('\n') != -1);
      String firstLine = result.substring(0, result.indexOf('\n')).trim();
      assertEquals("Must be testLog", "testLog", firstLine);
      String secondLine = result.substring(result.indexOf('\n'), result.length()).trim();
      assertEquals("Must have a 0 length", 0, secondLine.length());
   }

   /**
    * Test of logging in the default configuration for a SEVERE record.
    */
   @Test
   public void testLoggingSevere() {
      System.out.println("ConfigurableFormatterTest : testLoggingSevere");
      Logger logger = Logger.getLogger(ConfigurableFormatterTest.class.getName());
      removeHandlers(logger);
      SimpleConsoleHandler handler = new SimpleConsoleHandler();
      Formatter formatter = new ConfigurableFormatter();
      handler.setFormatter(formatter);
      logger.addHandler(handler);
      DebugStream stream = new DebugStream(System.err);
      System.setErr(stream);
      logger.log(Level.SEVERE, "testLog");
      String result = stream.getString();
      assertTrue("Must contain one new line", result.indexOf('\n') != -1);
      String firstLine = result.substring(0, result.indexOf('\n')).trim();
      String expected = "org.mdiutil.util.logging.ConfigurableFormatterTest testLoggingSevere : SEVERE: testLog";
      assertEquals("Must present the class and method", expected, firstLine);
      String secondLine = result.substring(result.indexOf('\n'), result.length()).trim();
      assertEquals("Must have a 0 length", 0, secondLine.length());
   }
}
