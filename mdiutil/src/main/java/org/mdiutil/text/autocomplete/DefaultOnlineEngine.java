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

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

/**
 * The default engine used for AutoComplete without Popup.
 *
 * @version 0.9.29
 */
public class DefaultOnlineEngine implements AutoCompleteOnlineEngine {
   private AutoCompleteDictionary dictionary;
   private AutoComplete autoComplete;
   private String matchedText = null;
   private AutoCompleteDictionary.Item item = null;

   public DefaultOnlineEngine() {
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

   /**
    * Return the text matched from the last hit.
    *
    * @return the text
    */
   @Override
   public String getMatchedText() {
      return matchedText;
   }

   /**
    * Return the item matched from the last hit.
    *
    * @return the item
    */
   @Override
   public AutoCompleteDictionary.Item getItem() {
      return item;
   }

   /**
    * Try a hit with the typed text.
    *
    * @param typedText the typed text
    * @return true if there is a hit
    */
   @Override
   public boolean hit(String typedText) {
      matchedText = null;
      boolean isUniqueHit = true;
      item = null;
      //get words in the dictionnary which we added
      Iterator<AutoCompleteDictionary.Category> it = dictionary.getDictionary().iterator();
      while (it.hasNext()) {
         AutoCompleteDictionary.Category category = it.next();
         SortedMap<String, List<AutoCompleteDictionary.Item>> items = category.getDictionnary();
         Iterator<String> it2 = items.keySet().iterator();
         while (it2.hasNext()) {
            String word = it2.next();
            item = items.get(word).get(0);
            //each string in the word
            if (autoComplete.isCaseSensitive()) {
               if (word.startsWith(typedText)) {
                  if (matchedText != null) {
                     isUniqueHit = false;
                  }
                  matchedText = word;
               }
            } else {
               if (word.toLowerCase().startsWith(typedText.toLowerCase())) {
                  if (matchedText != null) {
                     isUniqueHit = false;
                  }
                  matchedText = word;
               }
            }
         }
      }
      return isUniqueHit;
   }
}
