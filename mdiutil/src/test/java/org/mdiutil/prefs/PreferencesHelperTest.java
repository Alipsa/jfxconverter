/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2014, 2016, 2017, 2019, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import static org.junit.Assert.assertEquals;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
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
import org.mdiutil.prefs.swing.PreferencesUIHelper;

/**
 * Unit Tests for the PreferencesHelper class.
 *
 * @version 1.2.9
 */
@RunWith(OrderedRunner.class)
@Category(cat = "prefs")
public class PreferencesHelperTest {
   private static final String appli = "_NetworkFactoryTest_";
   private static final String PREF_FILE = "pref.xml";

   private void deleteCreatedUserDirectory() {
      File home = new File(System.getProperty("user.home"));
      File dir = new File(home, "." + appli);
      File f = new File(dir, PREF_FILE);

      if (f.exists()) {
         f.delete();
      }
      if (dir.exists()) {
         dir.delete();
      }
   }

   /**
    * Test of putColor and getColor methods, of class girod.util.prefs.PreferencesHelper.
    */
   @Test
   @Order(order = 1)
   public void testPutGetColor() {
      System.out.println("PreferencesHelperTest : testPutGetColor");

      // creation of Color key
      try {
         File userDir = new File(System.getProperty("user.home"));
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.newFactory(userDir, null, null, appli);
         Preferences user = fac.userRoot();
         Color color = new Color(10, 20, 30, 40);
         PreferencesUIHelper.putColor(user, "colorRoot", color);
         user.flush();
      } catch (BackingStoreException e) {
         TestCase.fail();
      }
   }

   /**
    * Test of putFile and getFile method, of class girod.util.prefs.PreferencesHelper.
    */
   @Test
   @Order(order = 2)
   public void testPutGetFile() {
      System.out.println("PreferencesHelperTest : testPutGetFile");
      // put and get file
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(null, null, null, this.getClass().getSimpleName());
         Preferences user = fac.userRoot();

         File file = File.createTempFile("prefHelper", "test");
         PreferencesHelper.putFile(user, "fileRoot", file);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         File fileRoot = PreferencesHelper.getFile(user, "fileRoot");
         assertEquals(fileRoot, file);

         file.delete();
      } catch (IOException e) {
         TestCase.fail("unable to create temp file");
      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }

   @Test
   @Order(order = 3)
   public void testPutGetFile2() {
      System.out.println("PreferencesHelperTest : testPutGetFile2");
      // put and get file
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(null, null, null, this.getClass().getSimpleName());
         Preferences user = fac.userRoot();
         File file = File.createTempFile("prefH�lp�r", "test");
         PreferencesHelper.putFile(user, "fileRoot", file);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         File fileRoot = PreferencesHelper.getFile(user, "fileRoot");

         assertEquals(fileRoot, file);

         file.delete();
      } catch (IOException e) {
         TestCase.fail("unable to create temp file");
      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }

   /**
    * Test of putFile and getFile method.
    */
   @Test
   @Order(order = 4)
   public void testPutGetFiles() {
      System.out.println("PreferencesHelperTest : testPutGetFiles");
      // put and get files
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         Preferences user = fac.userRoot();
         File file0 = File.createTempFile("prefHelper", "test");
         File file1 = File.createTempFile("prefHelper", "test");
         File file2 = File.createTempFile("prefHelper", "test");
         File[] files = new File[3];
         files[0] = file0;
         files[1] = file1;
         files[2] = file2;
         PreferencesHelper.putFiles(user, "fileNode", files);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         File[] files2 = PreferencesHelper.getFiles(user, "fileNode", null);

         assertEquals(files2.length, files.length);
         for (int i = 0; i < files.length; i++) {
            assertEquals(files[i], files2[i]);
         }

         file0.delete();
         file1.delete();
         file2.delete();
      } catch (IOException e) {
         TestCase.fail("unable to create temp files");
      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }

   @Test
   @Order(order = 5)
   public void testPutGetStringArray() {
      System.out.println("PreferencesHelperTest : testPutGetStringArray");
      // put and get files
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         Preferences user = fac.userRoot();

         String[] st = { "S1", "S2", "S3" };
         PreferencesHelper.putStringArray(user, "stringNode", st);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         String[] st2 = PreferencesHelper.getStringArray(user, "stringNode", null);

         assertEquals(st2.length, st.length);
         for (int i = 0; i < st.length; i++) {
            assertEquals(st[i], st2[i]);
         }

         // test bug 101 SVGLab
         String[] st3 = { "S1" };
         PreferencesHelper.putStringArray(user, "stringNode", st3);
         user.flush();

         st2 = PreferencesHelper.getStringArray(user, "stringNode", null);

         assertEquals(st2.length, st3.length);
         for (int i = 0; i < st2.length; i++) {
            assertEquals(st3[i], st2[i]);
         }

      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }

   @Test
   @Order(order = 6)
   public void testPutGetBooleanArray() {
      System.out.println("PreferencesHelperTest : testPutGetBooleanArray");
      // put and get files
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         if (fac == null)
            fac = NetworkPreferencesFactory.newFactory(null, null, null, this.getClass().getSimpleName());
         Preferences user = fac.userRoot();

         boolean[] _bool = { true, true, false };
         PreferencesHelper.putBooleanArray(user, "booleanNode", _bool);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         boolean[] _bool2 = PreferencesHelper.getBooleanArray(user, "booleanNode", false);

         assertEquals(_bool2.length, _bool.length);
         for (int i = 0; i < _bool.length; i++) {
            assertEquals(_bool[i], _bool2[i]);
         }

         boolean[] _bool3 = { true };
         PreferencesHelper.putBooleanArray(user, "booleanNode", _bool3);
         user.flush();

         _bool2 = PreferencesHelper.getBooleanArray(user, "booleanNode", false);

         assertEquals(_bool2.length, _bool3.length);
         for (int i = 0; i < _bool2.length; i++) {
            assertEquals(_bool3[i], _bool2[i]);
         }

      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }

   @Test
   @Order(order = 7)
   public void testPutGetMap() {
      System.out.println("PreferencesHelperTest : testPutGetMap");
      // put and get Map
      try {
         NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
         Preferences user = fac.userRoot();

         Map<String, Object> map = new HashMap<>();
         map.put("value3", false);
         map.put("value4", true);
         map.put("value10", false);
         PreferencesHelper.putMap(user, "mapNode", map);
         user.flush();

         NetworkPreferencesFactory fac2 = NetworkPreferencesFactory.getFactory();
         Preferences user2 = fac2.userRoot();
         Map map2 = PreferencesHelper.getMap(user, "mapNode", Boolean.class, null);

         assertEquals("not same size", map2.size(), map.size());
         Iterator<String> it = map.keySet().iterator();
         while (it.hasNext()) {
            String mapKey = it.next();
            Object o = map.get(mapKey);
            if (!map2.containsKey(mapKey)) {
               TestCase.fail("Element dont exists for " + mapKey);
            }
            Object o2 = map2.get(mapKey);
            if ((!(o instanceof Boolean)) || (!(o2 instanceof Boolean))) {
               TestCase.fail("not a Boolean element");
            }
            assertEquals("not Equal booleans", ((Boolean) o), ((Boolean) o2));
         }

      } catch (BackingStoreException e) {
         TestCase.fail(e.getMessage());
      } finally {
         deleteCreatedUserDirectory();
      }
   }
}
