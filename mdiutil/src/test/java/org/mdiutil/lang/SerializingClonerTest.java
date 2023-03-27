/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.io.NotSerializableException;
import javax.swing.JToggleButton;
import javax.swing.text.PlainDocument;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests fot the SerializingCloner class.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class SerializingClonerTest {

   public SerializingClonerTest() {
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
    * Test of clone method, of class SerializingCloner.
    */
   @Test
   public void testClone() throws Exception {
      System.out.println("SerializingClonerTest : testClone");
      JToggleButton button = new JToggleButton("toto");
      button.setSelected(true);
      SerializingCloner<JToggleButton> cloner = new SerializingCloner<>();
      JToggleButton button2 = cloner.deepClone(button);
      assertNotNull("result should be a JToggleButton");
      assertFalse("result should not be the input JToggleButton", button == button2);
      assertEquals("result fields", "toto", button2.getText());
      assertTrue("result fields", button2.isSelected());
   }

   /**
    * Test of clone method, of class SerializingCloner.
    */
   @Test
   public void testClone2() throws Exception {
      System.out.println("SerializingClonerTest : testClone2");
      PlainDocument doc = new PlainDocument();
      doc.insertString(0, "the text", null);
      SerializingCloner<PlainDocument> cloner = new SerializingCloner<>();
      PlainDocument doc2 = cloner.deepClone(doc);
      assertNotNull("result should be a PlainDocument");
      assertFalse("result should not be the input PlainDocument", doc == doc2);
      assertEquals("result text", "the text", doc.getText(0, doc.getLength()));
   }

   /**
    * Test of clone method, of class SerializingCloner.
    */
   @Test
   public void testClone3() throws Exception {
      System.out.println("SerializingClonerTest : testClone3");
      TestSerializingCloner test = new TestSerializingCloner(3, "toto");
      SerializingCloner<TestSerializingCloner> cloner = new SerializingCloner<>();
      try {
         TestSerializingCloner test2 = cloner.deepClone(test);
         fail("should not be able to deepClone the instance");
      } catch (NotSerializableException e) {
         // we expect to catch this exception
      } catch (IOException | ClassNotFoundException e) {
         fail("should not emit exceptions other than NotSerializableException");
      }
   }

}
