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

import java.util.ArrayList;
import java.util.List;

/**
 * An AutoComplete dummy.
 *
 * @since 0.9.33
 */
public class AutoCompleteDummy implements AutoComplete {
   private AutoCompleteDictionary dico;
   private boolean isCaseSensitive = false;
   private boolean isSearchingHitsFromStart = false;
   private List<AutoCompleteDictionary.Item> items = new ArrayList<>();

   public AutoCompleteDummy(AutoCompleteDictionary dico, boolean isCaseSensitive, boolean isSearchingHitsFromStart) {
      this.dico = dico;
      this.isCaseSensitive = isCaseSensitive;
      this.isSearchingHitsFromStart = isSearchingHitsFromStart;
   }

   public void reset() {
      items.clear();
   }

   public List<AutoCompleteDictionary.Item> getHits() {
      return items;
   }

   @Override
   public AutoCompleteDictionary getDictionary() {
      return dico;
   }

   @Override
   public void setPopupEngine(AutoCompleteEngine engine) {
   }

   @Override
   public void setOnlineEngine(AutoCompleteOnlineEngine engine) {
   }

   @Override
   public void addCategoryToSuggestions(AutoCompleteDictionary.Category category) {
   }

   @Override
   public void addItemToSuggestions(String category, AutoCompleteDictionary.Item item) {
      items.add(item);
   }

   @Override
   public boolean isCaseSensitive() {
      return isCaseSensitive;
   }

   @Override
   public boolean isSearchingHitsFromStart() {
      return isSearchingHitsFromStart;
   }

   @Override
   public void setDictionnary(AutoCompleteDictionary dictionary) {
   }

   @Override
   public void resetDictionary() {
   }

   @Override
   public boolean addCategory(AutoCompleteDictionary.Category category) {
      return false;
   }

   @Override
   public void addCategory(String category) {
   }

   @Override
   public void addToDictionary(String word) {
   }

   @Override
   public void addToDictionary(AutoCompleteDictionary.Item item) {
   }

   @Override
   public void addToDictionary(String category, String word) {
   }

   @Override
   public void addToDictionary(String category, AutoCompleteDictionary.Item item) {
   }

   @Override
   public void setMaximumPopupSize(int maxWidth, int maxHeight) {
   }

}
