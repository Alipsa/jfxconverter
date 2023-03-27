/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
 * Unit tests for the HeaderFileHandler class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class HeaderFileHandlerTest {
   public HeaderFileHandlerTest() {
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

   private File getTmpFile() {
      File tmpDir = new File(System.getProperty("java.io.tmpdir"));
      File tmpFile = new File(tmpDir, "unitTestsMDIUtils0.log");
      if (tmpFile.exists()) {
         tmpFile.delete();
      }
      return tmpFile;
   }

   /**
    * Test of logging in a HeaderFileHandler with a header.
    */
   @Test
   public void testLoggingDefault() {
      System.out.println("HeaderFileHandlerTest : testLoggingDefault");
      Logger logger = Logger.getLogger(HeaderFileHandlerTest.class.getName());
      removeHandlers(logger);
      File tmpFile = getTmpFile();
      HeaderFileHandler handler = null;
      try {
         handler = new HeaderFileHandler("%t/unitTestsMDIUtils%g.log");
         handler.setHeader("TEST HEADER");
      } catch (IOException ex) {
         ex.printStackTrace();
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
      Formatter formatter = new ConfigurableFormatter();
      handler.setFormatter(formatter);
      logger.addHandler(handler);
      logger.log(Level.INFO, "testLog");
      try {
         BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
         String line = reader.readLine();
         assertEquals("First lilne must be header", "TEST HEADER", line);
         line = reader.readLine();
         assertEquals("Second lilne must be the log", "testLog", line);
      } catch (IOException e) {
         e.printStackTrace();
         fail("Fail to read all lines");
      }

      tmpFile.delete();
   }
}
