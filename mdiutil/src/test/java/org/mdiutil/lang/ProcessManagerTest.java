/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
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
public class ProcessManagerTest {

   /**
    * Test of isHandlingPlatform method, of class ProcessManager.
    */
   @Test
   public void testIsHandlingPlatform() {
      System.out.println("ProcessManagerTest : testIsHandlingPlatform");
      ProcessManager manager = new ProcessManager();
      assertTrue("Must be handling the platform", manager.isHandlingPlatform());
   }

   /**
    * Test of isHandlingPlatform method, of class ProcessManager.
    */
   @Test
   public void testGetProcesses() {
      System.out.println("ProcessManagerTest : testGetProcesses");
      ProcessManager manager = new ProcessManager();
      List<ProcessManager.ExternalProcess> list = manager.getProcesses();
      assertFalse("The list must not be empty", list.isEmpty());
   }

   /**
    * Test of isHandlingPlatform method, of class ProcessManager.
    */
   @Test
   public void testGetProcessMap() {
      System.out.println("ProcessManagerTest : testGetProcessMap");
      ProcessManager manager = new ProcessManager();
      Map<String, List<ProcessManager.ExternalProcess>> map = manager.getProcessMap();
      assertFalse("The map must not be empty", map.isEmpty());
   }

   /**
    * Test of getProcessByCreationDate method, of class ProcessManager.
    */
   @Test
   public void testGetProcessByCreationDate() {
      System.out.println("ProcessManagerTest : testGetProcessByCreationDate");
      ProcessManager manager = new ProcessManager();
      SortedMap<Long, List<ProcessManager.ExternalProcess>> map = manager.getProcessByCreationDate();
      assertFalse("The map must not be empty", map.isEmpty());
   }
}
