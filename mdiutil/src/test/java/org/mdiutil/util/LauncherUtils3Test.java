/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
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
 * @since 1.2.31
 */
@RunWith(CategoryRunner.class)
@Category(cat = "util")
public class LauncherUtils3Test {

   public LauncherUtils3Test() {
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
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties() {
      System.out.println("LauncherUtils3Test : getLaunchProperties");
      String[] args = new String[] { "-arg1", "23", "-arg2=5" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 2 arguments", 2, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "5", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties2() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties2");
      String[] args = new String[] { "-arg1", "23", "-arg2" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 2 arguments", 2, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties3() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties3");
      String[] args = new String[] { "-arg1", "-arg2=5" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 2 arguments", 2, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "5", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties4() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties4");
      String[] args = new String[] { "-arg1", "23", "-arg2", "-arg3=5" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 3 arguments", 3, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "", value);
      value = result.get("arg3");
      assertNotNull("arg3 value must not be null", value);
      assertEquals("arg3 value", "5", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties5() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties5");
      String[] args = new String[] { "-arg1", "23", "-arg2", "-arg3" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 3 arguments", 3, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "", value);
      value = result.get("arg3");
      assertNotNull("arg3 value must not be null", value);
      assertEquals("arg3 value", "", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties6() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties6");
      String[] args = new String[] { "-arg1=23", "-arg2", "-arg3" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 3 arguments", 3, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "", value);
      value = result.get("arg3");
      assertNotNull("arg3 value must not be null", value);
      assertEquals("arg3 value", "", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties7() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties7");
      String[] args = new String[] { "-arg1=23", "56", "-arg2", "-arg3" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 3 arguments", 3, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "", value);
      value = result.get("arg3");
      assertNotNull("arg3 value must not be null", value);
      assertEquals("arg3 value", "", value);
   }

   /**
    * Test of getLaunchProperties method.
    */
   @Test
   public void testGetLaunchProperties8() {
      System.out.println("LauncherUtils3Test : testGetLaunchProperties8");
      String[] args = new String[] { "-arg1", "23", "-arg2=false", "-arg3=1.2", "-arg4=toto", "-arg5=L:/my/file", "-arg6" };
      Map<String, String> result = LauncherUtils.getLaunchProperties(args);
      assertNotNull("Result must not be null", result);
      assertEquals("Result must have 6 arguments", 6, result.size());
      assertTrue("Result must have arg1", result.containsKey("arg1"));
      Object value = result.get("arg1");
      assertNotNull("arg1 value must not be null", value);
      assertEquals("arg1 value", "23", value);
      assertTrue("Result must have arg2", result.containsKey("arg2"));
      value = result.get("arg2");
      assertNotNull("arg2 value must not be null", value);
      assertEquals("arg2 value", "false", value);
      value = result.get("arg3");
      assertNotNull("arg3 value must not be null", value);
      assertEquals("arg3 value", "1.2", value);
      value = result.get("arg4");
      assertNotNull("arg4 value must not be null", value);
      assertEquals("arg4 value", "toto", value);
      value = result.get("arg5");
      assertNotNull("arg5 value must not be null", value);
      assertEquals("arg5 value", "L:/my/file", value);
      value = result.get("arg6");
      assertNotNull("arg6 value must not be null", value);
      assertEquals("arg6 value", "", value);
   }
}
