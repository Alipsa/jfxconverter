/*------------------------------------------------------------------------------
 * Copyright (C) 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.*;
import java.util.List;
import java.util.SortedMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @version 1.2.5
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class ProcessManager2Test {

   public ProcessManager2Test() {
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
    * Test of getProcessByCreationDate method, of class ProcessManager.
    */
   @Test
   public void testGetProcessByCreationDate() {
      System.out.println("ProcessManager2Test : testGetProcessByCreationDate");
      ProcessManager manager = new ProcessManager();
      SortedMap<Long, List<ProcessManager.ExternalProcess>> map = manager.getProcessByCreationDate();
      assertFalse("The map must not be empty", map.isEmpty());
   }
}
