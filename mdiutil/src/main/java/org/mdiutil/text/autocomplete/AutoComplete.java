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

import org.mdiutil.text.autocomplete.AutoCompleteDictionary.Category;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary.Item;

/**
 * An Autocomplete interface.
 *
 * @version 0.9.52
 */
public interface AutoComplete {
   /**
    * Return the dictionnary used for the Autocompletion.
    *
    * @return the dictionnary
    */
   public AutoCompleteDictionary getDictionary();

   /**
    * Set the dictionnary used for the Autocompletion.
    *
    * @param dictionary the dictionnary
    */
   public void setDictionnary(AutoCompleteDictionary dictionary);

   /**
    * Reset the Dictionnary to use for the autoComplete.
    */
   public void resetDictionary();

   /**
    * Add a category to the dictionary.
    *
    * @param category the category
    * @return true if the category did not previously exist
    */
   public boolean addCategory(Category category);

   /**
    * Add a category to the dictionary.
    *
    * @param category the category name
    */
   public void addCategory(String category);

   /**
    * Add a word to the dictionary to use for the autoComplete.
    *
    * @param word the word
    */
   public void addToDictionary(String word);

   /**
    * Add an item to the dictionary to use for the autoComplete.
    *
    * @param item the item
    */
   public void addToDictionary(Item item);

   /**
    * Add a word to the dictionary to use for the autoComplete.
    *
    * @param category the item category
    * @param word the word
    */
   public void addToDictionary(String category, String word);

   /**
    * Add an item to the dictionary to use for the autoComplete.
    *
    * @param category the item category
    * @param item the item
    */
   public void addToDictionary(String category, Item item);

   /**
    * Set the maximum width and height of the Popup.
    *
    * @param maxWidth the maximum width of the Popup
    * @param maxHeight the maximum height of the Popup
    */
   public void setMaximumPopupSize(int maxWidth, int maxHeight);

   /**
    * Set the search engine used for the Autocomplete with Popup.
    *
    * @param engine the search engine
    */
   public void setPopupEngine(AutoCompleteEngine engine);

   /**
    * Set the search engine used for the Autocomplete without Popup.
    *
    * @param engine the search engine
    */
   public void setOnlineEngine(AutoCompleteOnlineEngine engine);

   /**
    * Add a category to the list of suggestions in the Popup Window. This method maybe used in
    * an {@link AutoCompleteEngine} used for the search with a Popup Window.
    *
    * @param category the category
    * @see #setPopupEngine(AutoCompleteEngine)
    */
   public void addCategoryToSuggestions(Category category);

   /**
    * Add a category to the list of suggestions in the Popup Window. This method maybe used in
    * an {@link AutoCompleteEngine} used for the search with a Popup Window.
    *
    * @param category the category name
    * @param item the Item
    * @see #setPopupEngine(AutoCompleteEngine)
    */
   public void addItemToSuggestions(String category, Item item);

   /**
    * Add the additional search text suggestion in the popup Window. Do nothing by default.
    *
    * @param typedText the typed text
    */
   public default void addAdditionalSearchSuggestion(String typedText) {
   }

   /**
    * Return true if the search is case sensitive.
    *
    * @return true if the search is case sensitive
    */
   public boolean isCaseSensitive();

   /**
    * Return true if the search begins from the start of the typed text.
    *
    * @return true if the search begins from the start of the typed text
    */
   public boolean isSearchingHitsFromStart();

   /**
    * Add the additional search item in the popup. It can be used to add an additional item in the list of suggestions, for example to add a
    * full-text search option in the list of suggestions.
    *
    * Note that the template uses the syntax of the {@link org.mdiutil.lang.MessageConstructor} utility class.
    * For example If additionalSearchText = "Containing: $1 cents", the result for the typed text "toto" will be "Containing: toto".
    *
    * @param template the template for the additional search item
    */
   public default void addAdditionalSearchItem(String template) {
   }

   /**
    * Return true if there is an additional search item in the popup.
    *
    * @return true if there is an additional search item in the popup
    */
   public default boolean hasAdditionalSearchItem() {
      return false;
   }
}
