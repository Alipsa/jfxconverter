/*
Copyright (c) 2017, 2019 Herve Girod
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The AutoComplete Dictionnary.
 *
 * @version 0.9.52
 */
public class AutoCompleteDictionary {
   private final Map<String, Category> categories = new HashMap<>();
   private final List<Category> dictionary = new ArrayList<>();
   private boolean defaultCat = true;
   private boolean acceptDuplicates = true;

   public AutoCompleteDictionary() {
   }

   /**
    * Set if the dictionnary accept duplicates.
    *
    * @param acceptDuplicates true if the dictionnary accept duplicates
    */
   public void acceptDuplicates(boolean acceptDuplicates) {
      this.acceptDuplicates = acceptDuplicates;
   }

   /**
    * Return true if the dictionnary accept duplicates.
    *
    * @return true if the dictionnary accept duplicates
    */
   public boolean isAcceptingDuplicates() {
      return acceptDuplicates;
   }

   /**
    * Return true if the dictionnary uses only the default category.
    *
    * @return true if the dictionnary uses only the default category
    */
   public boolean hasDefaultCategory() {
      return defaultCat;
   }

   /**
    * Add a Category to the dictionary.
    *
    * @param category the category
    * @return true if the category did not previously exist
    */
   public boolean addCategory(Category category) {
      if (!categories.containsKey(category.getName())) {
         categories.put(category.getName(), category);
         dictionary.add(category);
         defaultCat = false;
         return true;
      } else {
         return false;
      }
   }

   /**
    * Add a Category to the dictionary.
    *
    * @param name the category name
    * @return the category
    */
   public Category addCategory(String name) {
      if (!categories.containsKey(name)) {
         Category category = new Category(name);
         categories.put(name, category);
         dictionary.add(category);
         defaultCat = false;
         return category;
      } else {
         return categories.get(name);
      }
   }

   /**
    * Reset the Dictionnary to use for the autoComplete.
    */
   public void reset() {
      dictionary.clear();
      categories.clear();
      defaultCat = true;
   }

   /**
    * Add a word to the dictionary to use for the autoComplete. If the dictionary use categories, the word won't be
    * added.
    *
    * @param word the word
    * @return true if the dictionary don't uses categories
    */
   public boolean addToDictionary(String word) {
      return addToDictionary(new DefaultItem(word));
   }

   /**
    * Add an item to the dictionary to use for the autoComplete. If the dictionary use categories, the item won't be
    * added.
    *
    * @param item the item
    * @return true if the dictionary don't uses categories
    */
   public boolean addToDictionary(Item item) {
      if (defaultCat) {
         Category category;
         if (dictionary.isEmpty()) {
            category = new Category(null);
            dictionary.add(category);
         } else {
            category = dictionary.get(0);
         }
         category.addItem(this, item);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Add a word to the dictionary to use for the autoComplete. If the dictionary don't use categories, the item won't be
    * added.
    *
    * @param category the item category
    * @param word the word
    * @return true if the dictionary uses categories
    */
   public boolean addToDictionary(String category, String word) {
      return addToDictionary(category, new DefaultItem(word));
   }

   /**
    * Add an item to the dictionary to use for the autoComplete. If the dictionary don't use categories, the item won't be
    * added.
    *
    * @param category the item category
    * @param item the item
    * @return true if the dictionary uses categories
    */
   public boolean addToDictionary(String category, Item item) {
      Category catItems;
      if (categories.containsKey(category)) {
         catItems = categories.get(category);
         catItems.addItem(this, item);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Return the dictionary.
    *
    * @return the dictionary
    */
   public List<Category> getDictionary() {
      return dictionary;
   }

   /**
    * Return the map of categories.
    *
    * @return the map of categories
    */
   public Map<String, Category> getCategories() {
      return categories;
   }

   /**
    * Represents a category of results in the dictionnary.
    *
    * @version 0.9.52
    */
   public static class Category {
      private final String name;
      private final SortedMap<String, List<Item>> catItems = new TreeMap<>();

      /**
       * Constructor.
       *
       * @param name the name of the category
       */
      public Category(String name) {
         this.name = name;
      }

      /**
       * Return the name of the category.
       *
       * @return the name of the category
       */
      public String getName() {
         return name;
      }

      /**
       * Return the dictionary for the category.
       *
       * @return the dictionary
       */
      public SortedMap<String, List<Item>> getDictionnary() {
         return catItems;
      }

      /**
       * Add an item in the category.
       *
       * @param dictionary the dictionary
       * @param item the item
       */
      private void addItem(AutoCompleteDictionary dictionary, Item item) {
         List<Item> items;
         if (!dictionary.isAcceptingDuplicates()) {
            if (!catItems.containsKey(item.getText())) {
               items = new ArrayList<>();
               catItems.put(item.getText(), items);
               items.add(item);
            }
         } else {
            if (catItems.containsKey(item.getText())) {
               items = catItems.get(item.getText());
               items.add(item);
            } else {
               items = new ArrayList<>();
               catItems.put(item.getText(), items);
               items.add(item);
            }
         }
      }
   }

   /**
    * A common interface for items to use for the Autocomplete.
    *
    * @version 0.9.29
    */
   public static interface Item {
      /**
       * The item text.
       *
       * @return the Item text
       */
      public String getText();
   }

   private static class DefaultItem implements Item {
      private final String name;

      private DefaultItem(String name) {
         this.name = name;
      }

      @Override
      public String getText() {
         return name;
      }

      @Override
      public String toString() {
         return name;
      }
   }
}
