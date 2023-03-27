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
package org.mdiutil.jfx.autocomplete;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Path;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.mdiutil.lang.MessageConstructor;
import org.mdiutil.text.autocomplete.AutoComplete;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary.Category;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary.Item;
import org.mdiutil.text.autocomplete.AutoCompleteEngine;
import org.mdiutil.text.autocomplete.AutoCompleteOnlineEngine;
import org.mdiutil.text.autocomplete.DefaultOnlineEngine;
import org.mdiutil.text.autocomplete.DefaultPopupEngine;

/**
 * An Autocomplete class, usable with a TextInputControl, which allows to add Autocomplete behavior to the text component.
 *
 * This class can be used in two main ways:
 * <ul>
 * <li>Autocompleting the text "inline" in the text component</li>
 * <li>Showing a Popup window with the list of possible choices and allow to choose between these choices</li>
 * </ul>
 *
 * <h1>Customization</h1>
 * The class can also be customized through several properties:
 * <ul>
 * <li>{@link #setCaseSensitive(boolean)}: true if case is sensitive for the search</li>
 * <li>{@link #searchHitsFromStart(boolean)}: true if the Search for hits is performed from the start of the typed text only.
 * For example the dictionnary word "conf" would be a hit for the text "unconfirmed" if this property was set to false,
 * and false if it was set to true. There would be a hit in the two cases for the text "confirmed"</li>
 * <li>{@link #showPopup(boolean)}: true if a Popup Window presenting the possible hits should be presented. if false, the first hit
 * will be automatically used for the completion</li>
 * <li>{@link #acceptDuplicates(boolean)}: true if the dictionnary accept duplicates</li>
 * <li>{@link #startSearchOnKeyCode(KeyCode)}: the keyCode to use to start the search. By default the search is started
 * immediately during the typing. If the keyCode is strictly greater than 0, the search will be started by typing
 * this keyCode with the Control key down</li>
 * <li>{@link #searchPerWord(boolean)}: true if the Search is performed per each word</li>
 * </ul>
 *
 * Moreover, it is possible to customize the way the list of suggestions is presented, and to change the default
 * search engine used to check for hits.
 *
 * <h1>CSS styling</h1>
 * The JFXAutoComplete uses the following components and styleClasses:
 * <ul>
 * <li>A ListView for the list, with the "autoComplete-list" styleClass</li>
 * <li>A Label for the categories (if they are used), with the "autoComplete-category" styleClass</li>
 * <li>A {@link SuggestedHit} (also a Label) for the suggestions, with the "autoComplete-hit" styleClass if categories are not used,
 * and "autoComplete-hit-category" if categories are used</li>
 * </ul>
 *
 * <h1>Example</h1>
 * <pre>
 * TextField tf = new TextField();
 * JFXAutoComplete autoCompleter = new JFXAutoComplete(tf);
 *
 * //add the dictionary
 * autoCompleter.addToDictionary("hello");
 * autoCompleter.addToDictionary("highlight");
 * autoCompleter.addToDictionary("cruel world");
 * autoCompleter.addToDictionary("war");
 * autoCompleter.addToDictionary("wording");
 * autoCompleter.addToDictionary("world");
 * </pre>
 *
 * @version 0.9.60
 */
public class JFXAutoComplete implements AutoComplete {
   /**
    * The text component.
    */
   protected TextInputControl textComponent;
   /**
    * The dictionnary.
    */
   private AutoCompleteDictionary dictionary = new AutoCompleteDictionary();
   private int maxWidth = 250;
   private int maxHeight = 150;
   private int typedOffset = 0;
   /**
    * True if the Search is case sentitive.
    */
   private boolean caseSensitive = false;
   /**
    * True if the search begins from the start of the typed text.
    */
   private boolean fromStart = true;
   private boolean showPopup = true;
   private boolean startSearchImmediately = true;
   private KeyCode startSearchOnKeyCode = null;
   private boolean searchPerWord = false;
   private String additionalSearchText = null;
   private AutoCompleteOnlineEngine onlineEngine = null;
   private AutoCompleteEngine popupEngine = null;
   private Popup autoCompletePopUp;
   private ListView<Node> listview = null;
   private final EventHandler<KeyEvent> keyHandlerImpl = new KeyHandlerImpl();

   /**
    * Constructor. The default parameters are:
    * <ul>
    * <li>The Popup Window is presented with the suggested words during typing</li>
    * <li>The Popup Window has an opacity of 1</li>
    * <li>Search is only performed from the start of the typed text</li>
    * <li>Search is not case-sensitive</li>
    * <li>Duplicates in the dictionnary are allowed</li>
    * <li>The search is active immediately during the typing</li>
    * <li>The search is performed for the whole text</li>
    * </ul>
    *
    * @param textComponent the text Component
    */
   public JFXAutoComplete(TextInputControl textComponent) {
      this(textComponent, true);
   }

   /**
    * Constructor. The default parameters are:
    * <ul>
    * <li>The Popup Window, if presented, has an opacity of 1</li>
    * <li>Search is only performed from the start of the typed text</li>
    * <li>Search is not case-sensitive</li>
    * <li>Duplicates in the dictionnary are allowed</li>
    * <li>The search is active immediately during the typing</li>
    * <li>The search is performed for the whole text</li>
    * </ul>
    *
    * @param textComponent the text Component
    * @param showPopup true if the Popup Window is presented with the suggested words during typing
    */
   public JFXAutoComplete(TextInputControl textComponent, boolean showPopup) {
      this.textComponent = textComponent;
      this.showPopup = showPopup;
      setupListeners();
   }

   /**
    * Set the maximum width and height of the Popup.
    *
    * @param maxWidth the maximum width of the Popup
    * @param maxHeight the maximum height of the Popup
    */
   @Override
   public void setMaximumPopupSize(int maxWidth, int maxHeight) {
      this.maxWidth = maxWidth;
      this.maxHeight = maxHeight;
   }

   /**
    * Add the additional search item in the popup. It can be used to add an additional item in the list of suggestions, for example to add a
    * full-text search option in the list of suggestions.
    *
    * Note that the template uses the syntax of the {@link MessageConstructor} utility class.
    * For example If additionalSearchText = "Containing: $1", the result for the typed text "toto" will be "Containing: toto".
    *
    * @param template the template for the additional search item
    */
   @Override
   public void addAdditionalSearchItem(String template) {
      this.additionalSearchText = template;
   }

   /**
    * Return true if there is an additional search item in the popup.
    *
    * @return true if there is an additional search item in the popup
    */
   @Override
   public boolean hasAdditionalSearchItem() {
      return additionalSearchText != null;
   }

   private void setupListeners() {
      textComponent.textProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue observable, String oldValue, String newValue) {
            if (startSearchImmediately) {
               checkPopupSuggestions(newValue.length() < oldValue.length());
            }
         }
      });

      //Hide always by focus-in (optional) and out
      textComponent.focusedProperty().addListener(new ChangeListener() {
         @Override
         public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            getWindow();
            autoCompletePopUp.hide();
         }
      });
   }

   private void onWindowMove(boolean isX, double oldValue, double newValue) {
      if (isX) {
         autoCompletePopUp.setAnchorX(autoCompletePopUp.getAnchorX() + newValue - oldValue);
      } else {
         autoCompletePopUp.setAnchorY(autoCompletePopUp.getAnchorY() + newValue - oldValue);
      }
   }

   private void getWindow() {
      if (autoCompletePopUp == null) {
         autoCompletePopUp = new Popup();

         String css = JFXAutoComplete.class.getResource("autoComplete.css").toExternalForm();
         textComponent.getScene().getStylesheets().add(css);

         Window window = textComponent.getScene().getWindow();
         window.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
               if (autoCompletePopUp != null && autoCompletePopUp.isShowing()) {
                  onWindowMove(true, oldValue.doubleValue(), newValue.doubleValue());
               }
            }
         });
         window.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
               if (autoCompletePopUp != null && autoCompletePopUp.isShowing()) {
                  onWindowMove(false, oldValue.doubleValue(), newValue.doubleValue());
               }
            }
         });
      }
   }

   /**
    * Return true if the dictionnary uses only the default category.
    *
    * @return true if the dictionnary uses only the default category
    */
   public boolean hasDefaultCategory() {
      return dictionary.hasDefaultCategory();
   }

   /**
    * Set if the dictionnary accept duplicates.
    *
    * @param acceptDuplicates true if the dictionnary accept duplicates
    */
   public void acceptDuplicates(boolean acceptDuplicates) {
      dictionary.acceptDuplicates(acceptDuplicates);
   }

   /**
    * Return true if the dictionnary accept duplicates.
    *
    * @return true if the dictionnary accept duplicates
    */
   public boolean isAcceptingDuplicates() {
      return dictionary.isAcceptingDuplicates();
   }

   /**
    * Set the KeyCode to use to start the search. By default the search is started immediately during the typing.
    * If the KeyCode is not null, the search will be started by typing this keyCode with the Control key down.
    *
    * @param keyCode the KeyCode to use to start the search
    */
   public void startSearchOnKeyCode(KeyCode keyCode) {
      this.startSearchImmediately = keyCode == null;
      this.startSearchOnKeyCode = keyCode;
      if (!startSearchImmediately) {
         textComponent.addEventHandler(KeyEvent.KEY_PRESSED, keyHandlerImpl);
      } else {
         textComponent.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandlerImpl);
      }
   }

   /**
    * Return the KeyCode to use to start the search By default the search is started immediately during the typing.
    * If the keyCode is not null, the search will be started by typing this keyCode with the Control key down.
    *
    * @return the KeyCode to use to start the search
    */
   public KeyCode getSearchOnKeyCode() {
      return startSearchOnKeyCode;
   }

   /**
    * Return true if the search is started immediately during the typing.
    *
    * @return true if the search is started immediately during the typing
    */
   public boolean isStartingSearchImmediately() {
      return startSearchImmediately;
   }

   /**
    * Set if the search is performed per each typed word.
    *
    * @param searchPerWord true if the search is performed per each typed word
    */
   public void searchPerWord(boolean searchPerWord) {
      this.searchPerWord = searchPerWord;
   }

   /**
    * Return true if the search is performed per each typed word.
    *
    * @return true iif the search is performed per each typed word
    */
   public boolean isSearchingPerWord() {
      return searchPerWord;
   }

   /**
    * Set if the Popup Window with the words suggestions must be presented during typing.
    *
    * @param showPopup if the Popup Window with the words suggestions must be presented
    */
   public void showPopup(boolean showPopup) {
      this.showPopup = showPopup;
   }

   /**
    * Return true if the Popup Window with the words suggestions must be presented during typing.
    *
    * @return true if the Popup Window with the words suggestions must be presented
    */
   public boolean isShowingPopup() {
      return showPopup;
   }

   /**
    * Set if the search is case sensitive.
    *
    * @param caseSensitive if the search is case sensitive
    */
   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   /**
    * Return true if the search is case sensitive.
    *
    * @return true if the search is case sensitive
    */
   @Override
   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   /**
    * Set if the search begins from the start of the typed text. Note that if {@link #isShowingPopup()} is true, then the
    * search will always being from start regardless of the value if this property.
    *
    * @param fromStart if the search begins from the start of the typed text
    */
   public void searchHitsFromStart(boolean fromStart) {
      this.fromStart = fromStart;
   }

   /**
    * Return true if the search begins from the start of the typed text.
    *
    * @return true if the search begins from the start of the typed text
    */
   @Override
   public boolean isSearchingHitsFromStart() {
      return fromStart;
   }

   /**
    * Set the search engine used for the Autocomplete with Popup.
    *
    * @param engine the search engine
    */
   @Override
   public void setPopupEngine(AutoCompleteEngine engine) {
      this.popupEngine = engine;
      engine.setAutoComplete(this);
   }

   /**
    * Set the search engine used for the Autocomplete without Popup.
    *
    * @param engine the search engine
    */
   @Override
   public void setOnlineEngine(AutoCompleteOnlineEngine engine) {
      this.onlineEngine = engine;
      engine.setAutoComplete(this);
   }

   /**
    * Return the text component text.
    *
    * @return the text component text
    */
   public final String getText() {
      return textComponent.getText();
   }

   /**
    * Fire an action if the search has reached a hit.
    *
    * @param startOffset the text start offset
    * @param item the item used for the AutoComplete
    */
   private void fireAction(int startOffset, Item item) {
      AutoCompleteEvent evt = new AutoCompleteEvent(startOffset - 1, item);
      textComponent.fireEvent(evt);
   }

   /**
    * Fire an action for the full text search event.
    *
    * @param text the text used for the full text search
    */
   private void fireAction(String text) {
      AdditionalSearchEvent evt = new AdditionalSearchEvent(text);
      textComponent.fireEvent(evt);
   }

   private void setFocusToTextField() {
      textComponent.requestFocus();
   }

   /**
    * Check the words dictionnary against the typed text,for the online AutoComplete2.
    *
    * @param offset the offset of the typed text
    * @param typedText the typed text
    * @return true if there is a hit
    */
   private boolean checkOnlineSuggestions(int offset, String typedText) {
      if (typedText.isEmpty()) {
         return false;
      }
      if (onlineEngine == null) {
         onlineEngine = new DefaultOnlineEngine();
         onlineEngine.setAutoComplete(this);
      }

      boolean isUniqueHit = onlineEngine.hit(typedText);
      String matchedText = onlineEngine.getMatchedText();
      if (matchedText != null && isUniqueHit) {
      }
      return matchedText != null && isUniqueHit;
   }

   /**
    * Check the words dictionnary against the typed text,for the WIndow Popup AutoComplete2.
    *
    * @param offset the offset of the typed text
    * @param typedText the typed text
    * @return true if there is a hit
    */
   private boolean checkPopupSuggestions(String typedText) {
      if (typedText.isEmpty()) {
         return false;
      }
      if (popupEngine == null) {
         popupEngine = new DefaultPopupEngine();
         popupEngine.setAutoComplete(this);
      }

      boolean suggestionAdded = popupEngine.hit(typedText);
      return suggestionAdded;
   }

   private String getCurrentlyTypedText(int offset) {
      if (searchPerWord) {
         String text;
         if (offset > textComponent.getText().length() - 1) {
            return null;
         }
         text = textComponent.getText(offset, offset + 1);
         char lastChar = text.charAt(text.length() - 1);
         if (lastChar == ' ' || lastChar == '\n') {
            return null;
         } else {
            int indexSpace = textComponent.getText(0, offset).lastIndexOf(' ');
            int indexNewLine = textComponent.getText(0, offset).lastIndexOf('\n');
            int index = indexSpace > indexNewLine ? indexSpace : indexNewLine;
            String lastWord = textComponent.getText(index + 1, index + 1 + offset - index);
            return lastWord;
         }
      } else {
         return textComponent.getText();
      }
   }

   private void checkPopupSuggestions(boolean removed) {
      int offset = textComponent.getCaretPosition();
      if (!startSearchImmediately) {
         offset--;
      } else if (removed) {
         offset -= 2;
      }
      if (offset < 0) {
         return;
      }

      String typedWord = this.getCurrentlyTypedText(offset);
      if (typedWord == null) {
         return;
      }
      getWindow();

      // remove all previous suggested hits
      autoCompletePopUp.getContent().clear();
      listview = new ListView();
      listview.getStyleClass().add("autoComplete-list");
      listview.setMaxHeight(maxHeight);
      listview.setMaxWidth(maxWidth);
      // add event handles (click on ENTER and click on item)
      listview.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
               Node node = listview.getSelectionModel().getSelectedItem();
               if (node instanceof SuggestedHit) {
                  SuggestedHit hit = (SuggestedHit) node;
                  if (hit.item != null) {
                     hit.replaceWithSuggestedText();
                     fireSuggestionAction(hit.item);
                  } else {
                     fireFullTextSearchAction(hit.searchedText);
                  }
                  autoCompletePopUp.hide();
               }
            }
         }
      });
      listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            Node node = listview.getSelectionModel().getSelectedItem();
            if (node instanceof SuggestedHit) {
               SuggestedHit hit = (SuggestedHit) node;
               if (hit.item != null) {
                  hit.replaceWithSuggestedText();
                  fireSuggestionAction(hit.item);
               } else {
                  fireFullTextSearchAction(hit.searchedText);
               }
               autoCompletePopUp.hide();
            }
         }
      });

      typedOffset = offset;
      boolean added = checkPopupSuggestions(typedWord);

      if (!added) {
         if (autoCompletePopUp.isShowing()) {
            autoCompletePopUp.hide();
         }
      } else {
         showPopUpWindow();
         setFocusToTextField();
      }
   }

   private void showPopUpWindow() {
      Path caret = findCaret(textComponent);
      Point2D screenLoc = findScreenLocation(caret);
      if (fromStart) {
         screenLoc = new Point2D(textComponent.localToScreen(new Point2D(0d, 0d)).getX(), screenLoc.getY());
      }

      autoCompletePopUp.getContent().add(listview);
      autoCompletePopUp.show(textComponent, screenLoc.getX(), screenLoc.getY() + 20);
   }

   private Path findCaret(Parent parent) {
      for (Node n : parent.getChildrenUnmodifiable()) {
         if (n instanceof Path) {
            return (Path) n;
         } else if (n instanceof Parent) {
            Path p = findCaret((Parent) n);
            if (p != null) {
               return p;
            }
         }
      }
      return null;
   }

   private Point2D findScreenLocation(Node node) {
      double x = 0;
      double y = 0;
      for (Node n = node; n != null; n = n.getParent()) {
         Bounds parentBounds = n.getBoundsInParent();
         x += parentBounds.getMinX();
         y += parentBounds.getMinY();
      }
      Scene scene = node.getScene();
      x += scene.getX();
      y += scene.getY();
      Window window = scene.getWindow();
      x += window.getX();
      y += window.getY();
      Point2D screenLoc = new Point2D(x, y);
      return screenLoc;
   }

   private void fireFullTextSearchAction(String text) {
      fireAction(text);
   }

   private void fireSuggestionAction(Item item) {
      fireAction(typedOffset, item);
   }

   /**
    * Add a category to the list of suggestions in the Popup Window. This method maybe used in
    * an {@link AutoCompleteEngine} used for the search with a Popup Window.
    *
    * @param category the category
    * @see #setPopupEngine(AutoCompleteEngine)
    */
   @Override
   public void addCategoryToSuggestions(Category category) {
      addCategoryLabelToSuggestions(category.getName());
   }

   private void addCategoryLabelToSuggestions(String label) {
      Label catLabel = new Label(label);
      catLabel.getStyleClass().add("autoComplete-category");
      listview.getItems().add(catLabel);
   }

   /**
    * Add a category to the list of suggestions in the Popup Window. This method maybe used in
    * an {@link AutoCompleteEngine} used for the search with a Popup Window.
    *
    * @param category the category name
    * @param item the Item
    * @see #setPopupEngine(AutoCompleteEngine)
    */
   @Override
   public void addItemToSuggestions(String category, Item item) {
      final SuggestedHit suggestionLabel = new SuggestedHit(item);
      if (category == null) {
         suggestionLabel.getStyleClass().add("autoComplete-hit");
      } else {
         suggestionLabel.getStyleClass().add("autoComplete-hit-category");
      }
      listview.getItems().add(suggestionLabel);
   }

   /**
    * Return the full-text search label to show for the corresponding item.
    *
    * @param typedText the typed text
    * @return the full-text search label
    */
   protected String getAdditionalSearchLabel(String typedText) {
      String text = MessageConstructor.getText(additionalSearchText, typedText);
      return text;
   }

   /**
    * Add the additional search text suggestion in the popup Window.
    *
    * @param typedText the typed text
    */
   @Override
   public void addAdditionalSearchSuggestion(String typedText) {
      if (hasAdditionalSearchItem()) {
         addCategoryLabelToSuggestions(additionalSearchText);
         final SuggestedHit suggestionLabel = new SuggestedHit(typedText, typedText);
         suggestionLabel.getStyleClass().add("autoComplete-hit");
         listview.getItems().add(suggestionLabel);
      }
   }

   /**
    * Reset the Dictionnary to use for the autoComplete.
    */
   @Override
   public void resetDictionary() {
      dictionary.reset();
   }

   /**
    * Return the dictionnary used for the Autocompletion.
    *
    * @return the dictionnary
    */
   @Override
   public AutoCompleteDictionary getDictionary() {
      return dictionary;
   }

   /**
    * Set the dictionnary used for the Autocompletion.
    *
    * @param dictionary the dictionnary
    */
   @Override
   public void setDictionnary(AutoCompleteDictionary dictionary) {
      this.dictionary = dictionary;
   }

   /**
    * Add a category to the dictionary.
    *
    * @param category the category
    * @return true if the category did not previously exist
    */
   @Override
   public boolean addCategory(Category category) {
      return dictionary.addCategory(category);
   }

   /**
    * Add a category to the dictionary.
    *
    * @param category the category name
    */
   @Override
   public void addCategory(String category) {
      dictionary.addCategory(category);
   }

   /**
    * Add a word to the dictionary to use for the autoComplete.
    *
    * @param word the word
    */
   @Override
   public void addToDictionary(String word) {
      dictionary.addToDictionary(word);
   }

   /**
    * Add an item to the dictionary to use for the autoComplete.
    *
    * @param item the item
    */
   @Override
   public void addToDictionary(Item item) {
      dictionary.addToDictionary(item);
   }

   /**
    * Add a word to the dictionary to use for the autoComplete.
    *
    * @param category the item category
    * @param word the word
    */
   @Override
   public void addToDictionary(String category, String word) {
      dictionary.addToDictionary(category, word);
   }

   /**
    * Add an item to the dictionary to use for the autoComplete.
    *
    * @param category the item category
    * @param item the item
    */
   @Override
   public void addToDictionary(String category, Item item) {
      dictionary.addToDictionary(category, item);
   }

   /**
    * Return the text component.
    *
    * @return the text component
    */
   public TextInputControl getTextComponent() {
      return textComponent;

   }

   /**
    * The Label used for the Suggested Hits.
    *
    * @version 0.9.33
    */
   public class SuggestedHit extends Label {
      private final Item item;
      private final String searchedText;

      /**
       * Constructor.
       *
       * @param item the item
       */
      protected SuggestedHit(Item item) {
         super(item.toString());
         this.item = item;
         this.searchedText = null;
      }

      /**
       * Constructor.
       *
       * @param label the label to present
       * @param text the text
       */
      protected SuggestedHit(String label, String text) {
         super(label);
         this.item = null;
         this.searchedText = text;
      }

      private void replaceWithSuggestedText() {
         String suggestedWord = item.getText();
         String text = getCurrentlyTypedText(typedOffset);
         if (text == null) {
            return;
         }
         int _offset = typedOffset - text.length() + 1;
         String replacedText = text.replace(text, suggestedWord);
         String currentText = textComponent.getText();
         String newText = currentText.substring(0, _offset) + replacedText
            + currentText.substring(currentText.length() - _offset);
         textComponent.setText(newText);
         textComponent.positionCaret(_offset + replacedText.length());
      }
   }

   private class KeyHandlerImpl implements EventHandler<KeyEvent> {
      @Override
      public void handle(KeyEvent event) {
         if (event.isControlDown() && event.getCode().equals(startSearchOnKeyCode)) {
            checkPopupSuggestions(false);
         }
      }
   }
}
