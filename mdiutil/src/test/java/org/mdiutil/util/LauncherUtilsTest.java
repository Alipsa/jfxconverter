/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2022 Herve Girod
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
public class LauncherUtilsTest {

   public LauncherUtilsTest() {
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
      System.out.println("LauncherUtilsTest : getLaunchProperties");
      String[] args = new String[1];
      args[0] = "-key=value";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 1, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties2() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties2");
      String[] args = new String[2];
      args[0] = "-key=value";
      args[1] = "-key2=value2";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 2, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties3() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties3");
      String[] args = new String[3];
      args[0] = "-key=value";
      args[1] = "-key2=value2";
      args[2] = "-key3=";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 3, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
      assertTrue("Properties", props.containsKey("key3"));
      assertEquals("Properties", "", props.get("key3"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties4() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties4");
      String[] args = new String[4];
      args[0] = "-key=value";
      args[1] = "-key2=value2";
      args[2] = "-key3=";
      args[3] = "-key4";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 4, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
      assertTrue("Properties", props.containsKey("key3"));
      assertEquals("Properties", "", props.get("key3"));
      assertTrue("Properties", props.containsKey("key4"));
      assertEquals("Properties", "", props.get("key4"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties5() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties5");
      String[] args = new String[4];
      args[0] = "-key=value";
      args[1] = "-key4";
      args[2] = "-key2=value2";
      args[3] = "-key3=";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 4, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key4"));
      assertEquals("Properties", "", props.get("key4"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
      assertTrue("Properties", props.containsKey("key3"));
      assertEquals("Properties", "", props.get("key3"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties6() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties6");
      String[] args = new String[3];
      args[0] = "key";
      args[1] = "value";
      args[2] = "-key2=value2";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 2, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties7() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties7");
      String[] args = new String[2];
      args[0] = "key=value";
      args[1] = "key2=value2";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 2, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties8() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties8");
      String[] args = new String[7];
      args[0] = "key";
      args[1] = "value";
      args[2] = "-key2=";
      args[3] = "-key3";
      args[4] = "-key4=value2";
      args[5] = "-key5=value3";
      args[6] = "key6=value4";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 6, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "", props.get("key2"));
      assertTrue("Properties", props.containsKey("key3"));
      assertEquals("Properties", "", props.get("key3"));
      assertTrue("Properties", props.containsKey("key4"));
      assertEquals("Properties", "value2", props.get("key4"));
      assertTrue("Properties", props.containsKey("key5"));
      assertEquals("Properties", "value3", props.get("key5"));
      assertTrue("Properties", props.containsKey("key6"));
      assertEquals("Properties", "value4", props.get("key6"));
   }

   /**
    * Test of getLaunchProperties method, of class LauncherUtils.
    */
   @Test
   public void testGetLaunchProperties9() {
      System.out.println("LauncherUtilsTest : testGetLaunchProperties9");
      String[] args = new String[3];
      args[0] = "key=value";
      args[1] = "key2=value2";
      args[2] = "-key3=value3";
      Map<String, String> props = LauncherUtils.getLaunchProperties(args);

      assertNotNull("Properties should not be null", props);
      assertEquals("Properties size", 3, props.size());
      assertTrue("Properties", props.containsKey("key"));
      assertEquals("Properties", "value", props.get("key"));
      assertTrue("Properties", props.containsKey("key2"));
      assertEquals("Properties", "value2", props.get("key2"));
      assertTrue("Properties", props.containsKey("key3"));
      assertEquals("Properties", "value3", props.get("key3"));
   }
}
