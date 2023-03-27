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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * The default engine used for AutoComplete with Popup.
 *
 * @version 0.9.29
 */
public class DefaultPopupEngine implements AutoCompleteEngine {
   private AutoCompleteDictionary dictionary;
   private AutoComplete autoComplete;

   public DefaultPopupEngine() {
   }

   /**
    * Set the AutoComplete which will be used with the Engine.
    *
    * @param autoComplete the AutoComplete
    */
   @Override
   public void setAutoComplete(AutoComplete autoComplete) {
      this.autoComplete = autoComplete;
      this.dictionary = autoComplete.getDictionary();
   }

   private boolean hasDefaultCategory() {
      return dictionary.hasDefaultCategory();
   }

   /**
    * Try a hit with the typed text.
    *
    * @param typedText the typed text
    * @return true if there is a hit
    */
   @Override
   public boolean hit(String typedText) {
      boolean suggestionAdded = false;

      boolean fromStart = autoComplete.isSearchingHitsFromStart();
      boolean caseSensitive = autoComplete.isCaseSensitive();
      Set<String> categories = new HashSet<>();
      //get words in the dictionary which we added
      Iterator<AutoCompleteDictionary.Category> it = dictionary.getDictionary().iterator();
      while (it.hasNext()) {
         AutoCompleteDictionary.Category category = it.next();
         String categoryName = category.getName();
         SortedMap<String, List<AutoCompleteDictionary.Item>> items = category.getDictionnary();
         Iterator<Map.Entry<String, List<AutoCompleteDictionary.Item>>> it2 = items.entrySet().iterator();
         while (it2.hasNext()) {
            Map.Entry<String, List<AutoCompleteDictionary.Item>> entry2 = it2.next();
            String word = entry2.getKey();
            boolean matches = true;
            //each string in the word
            if (caseSensitive) {
               if (fromStart && (!word.startsWith(typedText))) {
                  matches = false;
               } else if (!fromStart && (!word.contains(typedText))) {
                  matches = false;
               }
            } else {
               if (fromStart && (!word.toLowerCase().startsWith(typedText.toLowerCase()))) {
                  matches = false;
               } else if (!fromStart && (!word.toLowerCase().contains(typedText.toLowerCase()))) {
                  matches = false;
               }
            }
            if (matches) {
               if (!hasDefaultCategory() && !categories.contains(categoryName)) {
                  categories.add(categoryName);
                  autoComplete.addCategoryToSuggestions(category);
               }
               for (AutoCompleteDictionary.Item item : entry2.getValue()) {
                  autoComplete.addItemToSuggestions(categoryName, item);
               }
               suggestionAdded = true;
            }
         }
      }
      if (autoComplete.hasAdditionalSearchItem()) {
         autoComplete.addAdditionalSearchSuggestion(typedText);
         suggestionAdded = true;
      }
      return suggestionAdded;
   }
}
