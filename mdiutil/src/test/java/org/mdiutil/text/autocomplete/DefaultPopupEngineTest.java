/*
Copyright (c) 2017, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.text.autocomplete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
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
 * Unit tests for the DefaultPopupEngine.
 *
 * @since 0.9.31
 */
@RunWith(OrderedRunner.class)
@Category(cat = "swing")
public class DefaultPopupEngineTest {

   public DefaultPopupEngineTest() {
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

   @Test
   @Order(order = 1)
   public void testHit() {
      System.out.println("DefaultPopupEngineTest : testHit");

      AutoCompleteDictionary dico = new AutoCompleteDictionary();
      dico.addToDictionary("bye");
      dico.addToDictionary("hello");
      dico.addToDictionary("heritage");
      dico.addToDictionary("happiness");
      dico.addToDictionary("woodbye");
      dico.addToDictionary("war");
      dico.addToDictionary("will");
      dico.addToDictionary("world");
      dico.addToDictionary("cruel world");
      dico.addToDictionary("wall");

      AutoCompleteDummy autoCompleter = new AutoCompleteDummy(dico, true, true);
      DefaultPopupEngine popupEngine = new DefaultPopupEngine();
      popupEngine.setAutoComplete(autoCompleter);

      boolean hit = popupEngine.hit("by");
      assertTrue("Hit is true", hit);
      List<AutoCompleteDictionary.Item> hits = autoCompleter.getHits();
      assertEquals("found one hit", 1, hits.size());
      assertEquals("found one hit", "bye", hits.get(0).getText());
      autoCompleter.reset();

      hit = popupEngine.hit("world");
      assertTrue("Hit is true", hit);
      hits = autoCompleter.getHits();
      assertEquals("found 2 hits", 1, hits.size());
      autoCompleter.reset();

      hit = popupEngine.hit("w");
      assertTrue("Hit is true", hit);
      hits = autoCompleter.getHits();
      assertEquals("found 5 hits", 5, hits.size());
      autoCompleter.reset();

      hit = popupEngine.hit("z");
      assertFalse("Hit is false", hit);
   }

   @Test
   @Order(order = 2)
   public void testHit2() {
      System.out.println("DefaultPopupEngineTest : testHit2");

      AutoCompleteDictionary dico = new AutoCompleteDictionary();
      dico.addToDictionary("bye");
      dico.addToDictionary("hello");
      dico.addToDictionary("heritage");
      dico.addToDictionary("happiness");
      dico.addToDictionary("woodbye");
      dico.addToDictionary("war");
      dico.addToDictionary("will");
      dico.addToDictionary("world");
      dico.addToDictionary("cruel world");
      dico.addToDictionary("wall");

      AutoCompleteDummy autoCompleter = new AutoCompleteDummy(dico, true, false);
      DefaultPopupEngine popupEngine = new DefaultPopupEngine();
      popupEngine.setAutoComplete(autoCompleter);

      boolean hit = popupEngine.hit("world");
      assertTrue("Hit is true", hit);
      List<AutoCompleteDictionary.Item> hits = autoCompleter.getHits();
      assertEquals("found 2 hits", 2, hits.size());
      autoCompleter.reset();

      hit = popupEngine.hit("World");
      assertFalse("Hit is false", hit);
   }

   @Test
   @Order(order = 2)
   public void testHit3() {
      System.out.println("DefaultPopupEngineTest : testHit3");

      AutoCompleteDictionary dico = new AutoCompleteDictionary();
      dico.addToDictionary("bye");
      dico.addToDictionary("hello");
      dico.addToDictionary("heritage");
      dico.addToDictionary("happiness");
      dico.addToDictionary("woodbye");
      dico.addToDictionary("war");
      dico.addToDictionary("will");
      dico.addToDictionary("world");
      dico.addToDictionary("cruel world");
      dico.addToDictionary("wall");

      AutoCompleteDummy autoCompleter = new AutoCompleteDummy(dico, false, false);
      DefaultPopupEngine popupEngine = new DefaultPopupEngine();
      popupEngine.setAutoComplete(autoCompleter);

      boolean hit = popupEngine.hit("world");
      assertTrue("Hit is true", hit);
      List<AutoCompleteDictionary.Item> hits = autoCompleter.getHits();
      assertEquals("found 2 hits", 2, hits.size());
      autoCompleter.reset();

      hit = popupEngine.hit("World");
      assertTrue("Hit is true", hit);
   }

}
