/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the LauncherUtils class.
 *
 * @version 1.2.31
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class LauncherUtils2Test {

   public LauncherUtils2Test() {
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
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties() {
      System.out.println("LauncherUtils2Test : getLaunchProperties");
      String[] args = new String[2];
      args[0] = "-key=value";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 1, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
   }
}
