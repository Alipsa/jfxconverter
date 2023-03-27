/*------------------------------------------------------------------------------
 * Copyright (C) 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
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
 * @since 0.9.44
 */
@RunWith(CategoryRunner.class)
@Category(cat = "swing")
public class StylableSizableAreaTest {

   public StylableSizableAreaTest() {
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
    * Test of setMaximumLines method, of class StylableSizableArea.
    */
   @Test
   public void testSetMaximumLines() {
      System.out.println("StylableSizableAreaTest : testSetMaximumLines");
      StylableSizableArea area = new StylableSizableArea(20);
      area.setMaximumLines(25);
      assertTrue("StylableSizableArea hasMaximum", area.hasMaximumLines());
      assertEquals("StylableSizableArea hasMaximum", 25, area.getMaximumLines());
   }

   /**
    * Test of setMaximumLines method, of class StylableSizableArea.
    */
   @Test
   public void testSetMaximumLines2() {
      System.out.println("StylableSizableAreaTest : testSetMaximumLines2");
      StylableSizableArea area = new StylableSizableArea(20);
      area.setMaximumLines(25);
      area.setMaximumLinesBehavior(false);
      assertFalse("StylableSizableArea hasMaximum", area.hasMaximumLines());
   }

   /**
    * Test of setMaximumLines method, of class StylableSizableArea.
    */
   @Test
   public void testSetMaximumLines3() {
      System.out.println("StylableSizableAreaTest : testSetMaximumLines3");
      StylableSizableArea area = new StylableSizableArea(20);
      area.setMaximumLinesBehavior(false);
      area.appendText("one");
      area.appendText("two");
      area.appendText("three");
      area.appendText("four");
      area.appendText("five");
      area.appendText("six");
      Document doc = area.getDocument();
      try {
         String text = doc.getText(0, doc.getLength());
         assertEquals("Document", "\none\ntwo\nthree\nfour\nfive\nsix\n", text);
      } catch (BadLocationException ex) {
         fail("Document incorrect");
      }
   }

   /**
    * Test of setMaximumLines method, of class StylableSizableArea.
    */
   @Test
   public void testSetMaximumLines4() {
      System.out.println("StylableSizableAreaTest : testSetMaximumLines4");
      StylableSizableArea area = new StylableSizableArea(20);
      area.setMaximumLines(3);
      area.appendText("one");
      area.appendText("two");
      area.appendText("three");
      area.appendText("four");
      area.appendText("five");
      area.appendText("six");
      Document doc = area.getDocument();
      try {
         String text = doc.getText(0, doc.getLength());
         assertEquals("Document", "four\nfive\nsix\n", text);
      } catch (BadLocationException ex) {
         fail("Document incorrect");
      }
   }
}
