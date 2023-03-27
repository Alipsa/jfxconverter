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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 * Unit tests for the AutoCompleteDictionary.
 *
 * @since 0.9.31
 */
@RunWith(OrderedRunner.class)
@Category(cat = "swing")
public class AutoCompleteDictionaryTest {

   public AutoCompleteDictionaryTest() {
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
   public void testDictionary() {
      System.out.println("AutoCompleteDictionaryTest : testDictionary");
      AutoCompleteDictionary dico = new AutoCompleteDictionary();
      dico.addToDictionary("one");
      dico.addToDictionary("two");
      dico.addToDictionary("three");

      List<AutoCompleteDictionary.Category> categories = dico.getDictionary();
      assertEquals("Must have one Category list", 1, categories.size());
      AutoCompleteDictionary.Category cat = categories.get(0);
      SortedMap<String, List<AutoCompleteDictionary.Item>> dico2 = cat.getDictionnary();
      assertEquals("Must have 3 items", 3, dico2.size());
      List<AutoCompleteDictionary.Item> items = dico2.get("one");
      assertNotNull("Must have 3 items", items);
      assertEquals("Must have 3 items", 1, items.size());
      assertEquals("Must have 3 items", "one", items.get(0).getText());
      items = dico2.get("two");
      assertNotNull("Must have 3 items", items);
      assertEquals("Must have 3 items", 1, items.size());
      assertEquals("Must have 3 items", "two", items.get(0).getText());
      items = dico2.get("three");
      assertNotNull("Must have 3 items", items);
      assertEquals("Must have 3 items", 1, items.size());
      assertEquals("Must have 3 items", "three", items.get(0).getText());

      Map<String, AutoCompleteDictionary.Category> categoriesMap = dico.getCategories();
      assertTrue("Must have no Category", categoriesMap.isEmpty());
   }

   @Test
   @Order(order = 2)
   public void testDictionary2() {
      System.out.println("AutoCompleteDictionaryTest : testDictionary2");
      AutoCompleteDictionary dico = new AutoCompleteDictionary();
      dico.addCategory("cat1");
      dico.addCategory("cat2");
      dico.addToDictionary("cat1", "one");
      dico.addToDictionary("cat1", "two");
      dico.addToDictionary("cat2", "three");

      List<AutoCompleteDictionary.Category> categories = dico.getDictionary();
      assertEquals("Must have 2 Categories", 2, categories.size());

      Map<String, AutoCompleteDictionary.Category> categoriesMap = dico.getCategories();
      assertEquals("Must have 2 Categories", 2, categoriesMap.size());
      AutoCompleteDictionary.Category cat = categoriesMap.get("cat1");
      assertNotNull("Must have 2 Categories", cat);
      SortedMap<String, List<AutoCompleteDictionary.Item>> dico2 = cat.getDictionnary();
      assertEquals("Must have 2 items", 2, dico2.size());
      List<AutoCompleteDictionary.Item> items = dico2.get("one");
      assertNotNull("Must have 2 items", items);
      assertEquals("Must have 2 items", 1, items.size());
      assertEquals("Must have 2 items", "one", items.get(0).getText());
      items = dico2.get("two");
      assertNotNull("Must have 2 items", items);
      assertEquals("Must have 2 items", 1, items.size());
      assertEquals("Must have 2 items", "two", items.get(0).getText());

      cat = categoriesMap.get("cat2");
      assertNotNull("Must have 2 Categories", cat);
      dico2 = cat.getDictionnary();
      assertEquals("Must have 1 items", 1, dico2.size());
      items = dico2.get("three");
      assertNotNull("Must have 1 items", items);
      assertEquals("Must have 1 items", 1, items.size());
      assertEquals("Must have 1 items", "three", items.get(0).getText());
   }
}
