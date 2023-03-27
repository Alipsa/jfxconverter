/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2014, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 * Unit Tests for the NetworkPreferencesFactory class.
 *
 * @version 0.9.25
 */
@RunWith(OrderedRunner.class)
@Category(cat = "prefs")
public class NetworkPreferencesFactoryTest {
   private static final String appli = "_NetworkFactoryTest_";
   private static final String SYSTEM_DIR_NAME = "prefs";
   private static final String PREF_FILE = "pref.xml";

   private void deleteCreatedUserDirectory() {
      File home = new File(System.getProperty("user.dir"));
      File dir = new File(home, "." + appli);
      File f = new File(dir, PREF_FILE);
      if (f.exists()) {
         f.delete();
      }
      if (dir.exists()) {
         dir.delete();
      }
   }

   private void deleteCreatedSystemDirectory() {
      File home = new File(System.getProperty("user.dir"));
      File dir = new File(home, "." + SYSTEM_DIR_NAME);
      File f = new File(dir, PREF_FILE);
      if (f.exists()) {
         f.delete();
      }
      if (dir.exists()) {
         dir.delete();
      }
   }

   /**
    * Test of the creation of simple user Preferences, and creation of new Nodes.
    */
   @Test
   @Order(order = 1)
   public void testSimpleUserPreferences() {
      System.out.println("NetworkPreferencesFactoryTest : testSimpleUserPreferences");

      // creation of preferences
      try {
         File userDir = new File(System.getProperty("user.home"));
         File systemDir = new File(System.getProperty("user.dir"));
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(userDir, systemDir, null, appli);
         Preferences user = fac.userRoot();
         user.put("testRoot", "valueRoot");
         Preferences userNode = user.node("node");
         userNode.put("testNode", "valueNode");
         userNode.flush();
      } catch (BackingStoreException e) {
         TestCase.fail(e.toString());
      }

      // use of preferences
      NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
      Preferences user2 = fac2.userRoot();
      String testRoot = user2.get("testRoot", "");
      Preferences userNode2 = user2.node("node");
      String testNode = userNode2.get("testNode", "");
      assertEquals("valueRoot", testRoot);
      assertEquals("valueNode", testNode);
      // test bug 101 SVGLab
      try {
         assertEquals(user2.nodeExists("node"), true);
         userNode2.removeNode();
         assertEquals(user2.nodeExists("node"), false);
      } catch (BackingStoreException e) {
         TestCase.fail();
      }

      deleteCreatedSystemDirectory();
      deleteCreatedUserDirectory();
   }

   /**
    * Test of the creation of user packages Nodes.
    */
   @Test
   @Order(order = 2)
   public void testPackagedUserPreferences() {
      System.out.println("NetworkPreferencesFactoryTest : testPackagedUserPreferences");

      // creation of preferences
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(null, null, null, this.getClass().getSimpleName());
         Preferences user = fac.userRoot();
         Preferences userPackage = user.userNodeForPackage(NetworkPreferencesFactory.class);
         userPackage.put("testRoot", "valueRoot");
         Preferences userNode = userPackage.node("node");
         userNode.put("testNode", "valueNode");
         userNode.flush();
      } catch (BackingStoreException e) {
         e.printStackTrace();
         TestCase.fail(e.toString());
      }

      // use of preferences
      NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
      Preferences user2 = fac2.userRoot();
      Preferences userPackage2 = user2.userNodeForPackage(NetworkPreferencesFactory.class);
      String testRoot = userPackage2.get("testRoot", "");
      Preferences userNode2 = userPackage2.node("node");
      String testNode = userNode2.get("testNode", "");
      assertEquals("valueRoot", testRoot);
      assertEquals("valueNode", testNode);

      deleteCreatedSystemDirectory();
      deleteCreatedUserDirectory();
   }

   /**
    * Test of the creation of simple system Preferences, and creation of new Nodes.
    */
   @Test
   @Order(order = 3)
   public void testSimpleSystemPreferences() {
      System.out.println("NetworkPreferencesFactoryTest : testSimpleSystemPreferences");

      // creation of preferences
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(null, null, null, this.getClass().getSimpleName());
         Preferences system = fac.systemRoot();
         system.put("testRoot", "valueRoot");
         Preferences systemNode = system.node("node");
         systemNode.put("testNode", "valueNode");
         systemNode.flush();
      } catch (BackingStoreException e) {
         TestCase.fail(e.toString());
      }

      // use of preferences
      NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
      Preferences system2 = fac2.systemRoot();
      String testRoot = system2.get("testRoot", "");
      Preferences systemNode2 = system2.node("node");
      String testNode = systemNode2.get("testNode", "");
      assertEquals("valueRoot", testRoot);
      assertEquals("valueNode", testNode);

      deleteCreatedSystemDirectory();
      deleteCreatedUserDirectory();
   }

   /**
    * Test of the creation of system packages Nodes.
    */
   @Test
   @Order(order = 4)
   public void testPackagedSystemPreferences() {
      System.out.println("NetworkPreferencesFactoryTest : testPackagedSystemPreferences");

      // creation of preferences
      try {
         Preferences systemPackage = NetworkPreferencesFactory.systemNodeForPackage(NetworkPreferencesFactory.class);
         systemPackage.put("testRoot", "valueRoot");
         Preferences systemNode = systemPackage.node("node");
         systemNode.put("testNode", "valueNode");
         systemNode.flush();
      } catch (BackingStoreException e) {
         TestCase.fail(e.toString());
      }

      // use of preferences
      Preferences systemPackage2 = NetworkPreferencesFactory.systemNodeForPackage(NetworkPreferencesFactory.class);
      String testRoot = systemPackage2.get("testRoot", "");
      Preferences systemNode2 = systemPackage2.node("node");
      String testNode = systemNode2.get("testNode", "");
      assertEquals("valueRoot", testRoot);
      assertEquals("valueNode", testNode);

      deleteCreatedSystemDirectory();
      deleteCreatedUserDirectory();
   }
}
