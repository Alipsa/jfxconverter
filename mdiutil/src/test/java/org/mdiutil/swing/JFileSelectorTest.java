/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import javax.swing.JFileChooser;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @since 1.2.17
 */
@RunWith(CategoryRunner.class)
@Category(cat = "swing")
public class JFileSelectorTest {

   public JFileSelectorTest() {
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
    * Test of getFileChooser method, of class JFileSelector.
    */
   @Test
   public void testGetFileChooser() {
      System.out.println("JFileSelectorTest : testGetFileChooser");
      JFileChooser chooser = JFileSelector.getFileChooser();
   }

   /**
    * Test of various options.
    */
   @Test
   public void testSetOptions() {
      System.out.println("JFileSelectorTest : testSetOptions");
      JFileSelector selector = new JFileSelector();

      File dir = new File(System.getProperty("user.dir"));
      selector.setCurrentDirectory(dir);
      selector.setDialogType(JFileChooser.SAVE_DIALOG);
      selector.setFileSelectionMode(JFileChooser.FILES_ONLY);

      File dir2 = selector.getCurrentDirectory();
      assertEquals("Current directory", dir, dir2);
      int selMode = selector.getFileSelectionMode();
      assertEquals("FileSelectionMode", JFileChooser.FILES_ONLY, selMode);
      int type = selector.getDialogType();
      assertEquals("DialogType", JFileChooser.SAVE_DIALOG, type);
   }
}
