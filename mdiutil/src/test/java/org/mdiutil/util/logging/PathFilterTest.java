/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the PathFilter class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class PathFilterTest {
   public PathFilterTest() {
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
    * Test of isLoggable method, for a default WARNING LogRecord without source.
    */
   @Test
   public void isLoggableWarning() {
      System.out.println("PathFilterTest : isLoggableWarning");
      PathFilter filter = new PathFilter();
      LogRecord record = new LogRecord(Level.WARNING, "testMessage");
      assertTrue("Must be loggable", filter.isLoggable(record));
   }

   /**
    * Test of isLoggable method, for a default INFO LogRecord without source.
    */
   @Test
   public void isLoggableInfo() {
      System.out.println("PathFilterTest : isLoggableInfo");
      PathFilter filter = new PathFilter();
      LogRecord record = new LogRecord(Level.INFO, "testMessage");
      assertTrue("Must be loggable", filter.isLoggable(record));
   }

   /**
    * Test of isLoggable method, for a default WARNING LogRecord with source.
    */
   @Test
   public void isLoggableWarningWithSource() {
      System.out.println("PathFilterTest : isLoggableWarningWithSource");
      PathFilter filter = new PathFilter();
      Set<String> paths = new HashSet<>();
      paths.add("my.package");
      paths.add("my.package2.myClass");
      filter.setPaths(paths);
      LogRecord record = new LogRecord(Level.WARNING, "testMessage");
      record.setSourceClassName("my.package.myClass");
      assertTrue("Must be loggable", filter.isLoggable(record));

      record = new LogRecord(Level.WARNING, "testMessage");
      record.setSourceClassName("my.package2.myClass");
      assertTrue("Must be loggable", filter.isLoggable(record));

      record = new LogRecord(Level.WARNING, "testMessage");
      record.setSourceClassName("my.package2.anotherClass");
      assertTrue("Must be loggable", filter.isLoggable(record));
   }

   /**
    * Test of isLoggable method, for a default INFO LogRecord with source.
    */
   @Test
   public void isLoggableInfoWithSource() {
      System.out.println("PathFilterTest : isLoggableInfoWithSource");
      PathFilter filter = new PathFilter();
      Set<String> paths = new HashSet<>();
      paths.add("my.package");
      paths.add("my.package2.myClass");
      filter.setPaths(paths);
      LogRecord record = new LogRecord(Level.INFO, "testMessage");
      record.setSourceClassName("my.package.myClass");
      assertTrue("Must be loggable", filter.isLoggable(record));

      record = new LogRecord(Level.INFO, "testMessage");
      record.setSourceClassName("my.package2.myClass");
      assertTrue("Must be loggable", filter.isLoggable(record));

      record = new LogRecord(Level.INFO, "testMessage");
      record.setSourceClassName("my.package2.anotherClass");
      assertFalse("Must not be loggable", filter.isLoggable(record));
   }

   /**
    * Test of isLoggable method, for a default INFO LogRecord with source.
    */
   @Test
   public void isLoggableInfoWithSource2() {
      System.out.println("PathFilterTest : isLoggableInfoWithSource2");
      PathFilter filter = new PathFilter();
      Set<String> paths = new HashSet<>();
      paths.add("my.package");
      paths.add("my.package2.myClass");
      filter.setPaths(paths);

      LogRecord record = new LogRecord(Level.INFO, "testMessage");
      record.setSourceClassName("my.package2.myClass2");
      assertFalse("Must not be loggable", filter.isLoggable(record));
   }
}
