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
package org.mdiutil.swing.autocomplete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
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
 * An Autocomplete class, usable with a JTextComponent, which allows to add Autocomplete behavior to the text component.
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
 * <li>{@link #setOpacity(float)}: the opacity of the Popup Window</li>
 * <li>{@link #setCaseSensitive(boolean)}: true if case is sensitive for the search</li>
 * <li>{@link #searchHitsFromStart(boolean)}: true if the Search for hits is performed from the start of the typed text only.
 * For example the dictionnary word "conf" would be a hit for the text "unconfirmed" if this property was set to false,
 * and false if it was set to true. There would be a hit in the two cases for the text "confirmed"</li>
 * <li>{@link #showPopup(boolean)}: true if a Popup Window presenting the possible hits should be presented. if false, the first hit
 * will be automatically used for the completion</li>
 * <li>{@link #acceptDuplicates(boolean)}: true if the dictionnary accept duplicates</li>
 * <li>{@link #startSearchOnKeyCode(int)}: the keyCode to use to start the search. By default the search is started
 * immediately during the typing. If the keyCode is strictly greater than 0, the search will be started by typing
 * this keyCode with the Control key down</li>
 * <li>{@link #searchPerWord(boolean)}: true if the Search is performed per each word</li>
 * </ul>
 *
 * Moreover, it is possible to customize the way the list of suggestions is presented, and to change the default
 * search engine used to check for hits.
 *
 * <h1>Example</h1>
 * <pre>
 * JTextField tf = new JTextField(10);
 * JAutoComplete autoCompleter = new JAutoComplete(tf);
 * autoCompleter.setOpacity(0.9f);
 * autoCompleter.searchHitsFromStart(false);
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
public class JAutoComplete implements AutoComplete {
   /**
    * The text component.
    */
   protected JTextComponent textComponent;
   private Window container;
   /**
    * The suggestion panel.
    */
   protected JPanel suggestedHitsPanel;
   private JWindow autoCompletePopUp;
   /**
    * The dictionnary.
    */
   private AutoCompleteDictionary dictionary = new AutoCompleteDictionary();
   private int tW;
   private int tH;
   private int maxWidth = 250;
   private int maxHeight = 150;
   private static final Color TEXT_COLOR = Color.BLACK;
   private static final Color FOCUSED_BACKGROUND = new Color(0, 127, 255);
   private static final Color POPUP_BORDER_COLOR = Color.GRAY;
   private static final Color POPUP_BACKGROUND = Color.WHITE;
   private float opacity = 1f;
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
   private int startSearchOnKeyCode = -1;
   private boolean searchPerWord = false;
   private String additionalSearchText = null;
   private Point wp = null;
   private AutoCompleteOnlineEngine onlineEngine = null;
   private AutoCompleteEngine popupEngine = null;
   private final static FontRenderContext FONT_CTX = new FontRenderContext(new AffineTransform(), false, false);
   private final List<AutoCompleteListener> listeners = new ArrayList<>();
   private final AutoCompleteListenerImpl documentListener = new AutoCompleteListenerImpl();
   private final TimerListenerImpl timerListener = new TimerListenerImpl();
   private final Timer timer = new Timer(150, timerListener);
   private final List<AdditionalSearchListener> clisteners = new ArrayList<>();

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
   public JAutoComplete(JTextComponent textComponent) {
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
   public JAutoComplete(JTextComponent textComponent, boolean showPopup) {
      this.textComponent = textComponent;
      this.showPopup = showPopup;
      setupListeners();
      tW = 0;
      tH = 0;
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
    * @param text the additional search text
    */
   @Override
   public void addAdditionalSearchItem(String text) {
      this.additionalSearchText = text;
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
      textComponent.getDocument().addDocumentListener(documentListener);
   }

   /**
    * Set the Popup Window opacity.
    *
    * @param opacity the opacity
    */
   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }

   /**
    * Return the Popup Window opacity.
    *
    * @return the opacity
    */
   public float getOpacity() {
      return opacity;
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
    * Set the keyCode to use to start the search. By default the search is started immediately during the typing.
    * If the keyCode is strictly greater than 0, the search will be started by typing this keyCode with the Control
    * key down.
    *
    * @param keyCode the keycode to use to start the search
    */
   public void startSearchOnKeyCode(int keyCode) {
      this.startSearchImmediately = keyCode <= 0;
      this.startSearchOnKeyCode = keyCode;
      if (!startSearchImmediately) {
         textComponent.addKeyListener(documentListener);
      } else {
         textComponent.removeKeyListener(documentListener);
      }
   }

   /**
    * Return the keyCode to use to start the search By default the search is started immediately during the typing.
    * If the keyCode is strictly greater than 0, the search will be started by typing this keyCode with the Control
    * key down.
    *
    * @return the keycode to use to start the search
    */
   public int getSearchOnKeyCode() {
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
    * Set if the search is perfoemd per each typed word.
    *
    * @param searchPerWord true if the search is perfoemd per each typed word
    */
   public void searchPerWord(boolean searchPerWord) {
      this.searchPerWord = searchPerWord;
   }

   /**
    * Return true if the search is perfoemd per each typed word.
    *
    * @return true iif the search is perfoemd per each typed word
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
    * Return the color of the suggested hits text.
    *
    * @return the color
    */
   protected Color getSuggestedHitTextColor() {
      return TEXT_COLOR;
   }

   /**
    * Return the background color of the focused suggested hit.
    *
    * @return the color
    */
   protected Color getSuggestedHitFocusedColor() {
      return FOCUSED_BACKGROUND;
   }

   /**
    * Return the background color of the suggested hits panel.
    *
    * @return the color
    */
   protected Color getPopUpBackgroundColor() {
      return POPUP_BACKGROUND;
   }

   /**
    * Return the borded color of the suggested hits panel.
    *
    * @return the color
    */
   protected Color getPopUpBorderColor() {
      return POPUP_BORDER_COLOR;
   }

   /**
    * Add an JAutoComplete listener.
    *
    * @param l the listener
    */
   public void addAutoCompleteListener(AutoCompleteListener l) {
      listeners.add(l);
   }

   /**
    * Removes an JAutoComplete listener.
    *
    * @param l the listener
    */
   public void removeAutoCompleteListener(AutoCompleteListener l) {
      listeners.remove(l);
   }

   /**
    * Return the list of JAutoComplete listeners.
    *
    * @return the list of JAutoComplete listeners
    */
   public List<AutoCompleteListener> getAutoCompleteListeners() {
      return listeners;
   }

   /**
    * Add an containing text listener.
    *
    * @param l the listener
    */
   public void addSearchTextListener(AdditionalSearchListener l) {
      clisteners.add(l);
   }

   /**
    * Removes a containing text listener.
    *
    * @param l the listener
    */
   public void removeContainingTextListener(AdditionalSearchListener l) {
      clisteners.remove(l);
   }

   /**
    * Return the list of containing text listeners.
    *
    * @return the list of containing text listeners
    */
   public List<AdditionalSearchListener> getContainingTextListeners() {
      return clisteners;
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
    * @param item the item used for the AutoComplete2
    */
   private void fireAction(int startOffset, Item item) {
      AutoCompleteEvent evt = new AutoCompleteEvent(this, startOffset, item);
      Iterator<AutoCompleteListener> it = listeners.iterator();
      while (it.hasNext()) {
         AutoCompleteListener listener = it.next();
         listener.autoCompletePerformed(evt);
      }
   }

   /**
    * Fire an action if the search has reached a hit.
    *
    * @param startOffset the text start offset
    * @param searchText the text
    */
   private void fireAction(String searchText) {
      AdditionalSearchEvent evt = new AdditionalSearchEvent(this, searchText);
      Iterator<AdditionalSearchListener> it = clisteners.iterator();
      while (it.hasNext()) {
         AdditionalSearchListener listener = it.next();
         listener.searchTextPerformed(evt);
      }
   }

   private void addPopupKeyBinding() {
      textComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UpReleased");
      textComponent.getActionMap().put("UpReleased", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            //focuses on the last label
            SuggestionHitsPanel _panel = (SuggestionHitsPanel) suggestedHitsPanel;
            SuggestedHit hit = _panel.getLastSuggestion();
            hit.setFocused(true);
            _panel.focusedHit = _panel.suggestions.size() - 1;
            autoCompletePopUp.toFront();
            autoCompletePopUp.requestFocusInWindow();
            suggestedHitsPanel.requestFocusInWindow();
            hit.requestFocusInWindow();
         }
      });

      suggestedHitsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UpReleased");
      suggestedHitsPanel.getActionMap().put("UpReleased", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            applyScrollUp();
         }
      });

      textComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "DownReleased");
      textComponent.getActionMap().put("DownReleased", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            //focuses on the first label
            SuggestionHitsPanel _panel = (SuggestionHitsPanel) suggestedHitsPanel;
            SuggestedHit hit = _panel.getFirstSuggestion();
            hit.setFocused(true);
            _panel.focusedHit = 0;
            autoCompletePopUp.toFront();
            autoCompletePopUp.requestFocusInWindow();
            suggestedHitsPanel.requestFocusInWindow();
            hit.requestFocusInWindow();
         }
      });
      suggestedHitsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "DownReleased");
      suggestedHitsPanel.getActionMap().put("DownReleased", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            applyScrollDown();
         }
      });
   }

   private void applyScrollUp() {
      SuggestionHitsPanel _panel = (SuggestionHitsPanel) suggestedHitsPanel;
      List<SuggestedHit> suggestions = _panel.suggestions;

      if (suggestions.size() > 1) {
         int index = _panel.focusedHit;
         if (index == -1) {
            index = suggestions.size() - 1;
         }
         if (index > 0) {
            SuggestedHit hit = suggestions.get(index);
            hit.setFocused(false);
            SuggestedHit newhit = suggestions.get(index - 1);
            newhit.setFocused(true);
            _panel.focusedHit = index - 1;
            autoCompletePopUp.toFront();
            autoCompletePopUp.requestFocusInWindow();
            _panel.requestFocusInWindow();
            newhit.requestFocusInWindow();
         }
      } else {
         //only a single suggestion was given
         autoCompletePopUp.setVisible(false);
         setFocusToTextField();
         checkPopupSuggestions(false);
      }
   }

   private void applyScrollDown() {
      // allows scrolling of labels
      SuggestionHitsPanel _panel = (SuggestionHitsPanel) suggestedHitsPanel;
      List<SuggestedHit> suggestions = _panel.suggestions;
      if (suggestions.size() > 1) {
         int index = _panel.focusedHit;
         if (index == -1) {
            index = 0;
         }
         if (index < suggestions.size() - 1) {
            SuggestedHit hit = suggestions.get(index);
            hit.setFocused(false);
            SuggestedHit newhit = suggestions.get(index + 1);
            newhit.setFocused(true);
            _panel.focusedHit = index + 1;
            autoCompletePopUp.toFront();
            autoCompletePopUp.requestFocusInWindow();
            _panel.requestFocusInWindow();
            newhit.requestFocusInWindow();
         }
      } else {
         // only a single suggestion was given
         autoCompletePopUp.setVisible(false);
         setFocusToTextField();
         // fire method as if document listener change occurred
         checkPopupSuggestions(false);
      }
   }

   private void getWindow() {
      if (container == null) {
         container = SwingUtilities.getWindowAncestor(textComponent);
         autoCompletePopUp = new JWindow(container);
         autoCompletePopUp.setOpacity(opacity);

         container.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent evt) {
               if (wp == null) {
                  wp = new Point(container.getX(), container.getY());
               } else if (autoCompletePopUp != null && autoCompletePopUp.isShowing()) {
                  onWindowMove(wp, new Point(container.getX(), container.getY()));
               }
            }
         });
         suggestedHitsPanel = new SuggestionHitsPanel();
         suggestedHitsPanel.setLayout(new GridLayout(0, 1));
         suggestedHitsPanel.setBackground(getPopUpBackgroundColor());
         suggestedHitsPanel.setBorder(BorderFactory.createLineBorder(getPopUpBorderColor()));
         addPopupKeyBinding();
      }
   }

   private void onWindowMove(Point oldValue, Point newValue) {
      Point p = new Point((int) (autoCompletePopUp.getX() + newValue.getX() - oldValue.getX()),
         (int) (autoCompletePopUp.getY() + newValue.getY() - oldValue.getY()));
      autoCompletePopUp.setLocation(p);
      wp = newValue;
   }

   private void setFocusToTextField() {
      getWindow();
      container.toFront();
      container.requestFocusInWindow();
      textComponent.requestFocusInWindow();
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
         Document doc = textComponent.getDocument();
         try {
            int _offset = 0;
            if (searchPerWord) {
               _offset = offset;
            }
            doc.remove(_offset, doc.getLength() - _offset);
            doc.insertString(_offset, matchedText, null);
            fireAction(_offset, onlineEngine.getItem());
         } catch (BadLocationException ex) {
         }
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

   private void checkPopupSuggestions(boolean removing) {
      int offset = textComponent.getCaretPosition();
      if (!startSearchImmediately) {
         offset--;
      }

      String typedWord;
      if (removing) {
         typedWord = this.getCurrentlyTypedText(offset - 2);
      } else {
         typedWord = this.getCurrentlyTypedText(offset);
      }
      if (typedWord == null) {
         return;
      }
      getWindow();

      // remove all previous suggested hits
      suggestedHitsPanel.removeAll();

      //used to calculate size of the window as new labels are added
      tW = 0;
      tH = 0;

      typedOffset = offset;
      boolean added = checkPopupSuggestions(typedWord);

      if (!added) {
         if (autoCompletePopUp.isVisible()) {
            autoCompletePopUp.setVisible(false);
         }
      } else {
         showPopUpWindow();
         setFocusToTextField();
      }
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
      JLabel catLabel = new JLabel("<html><i>" + label + "</i></html>");
      catLabel.setFont(textComponent.getFont());
      Dimension prefSize = catLabel.getPreferredSize();
      updatePopupSize(prefSize.width, prefSize.height);
      suggestedHitsPanel.add(catLabel);
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
      SuggestedHit suggestionLabel = new SuggestedHit(item);
      if (category != null) {
         suggestionLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
      }
      Dimension prefSize = suggestionLabel.getPreferredSize();
      updatePopupSize(prefSize.width, prefSize.height);
      suggestedHitsPanel.add(suggestionLabel);
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
         SuggestedHit suggestionLabel = new SuggestedHit(typedText, typedText);
         Dimension prefSize = suggestionLabel.getPreferredSize();
         updatePopupSize(prefSize.width, prefSize.height);
         suggestedHitsPanel.add(suggestionLabel);
      }
   }

   /**
    * Update the PopUp Size.
    *
    * @param width the width of the element
    * @param height the height of the element
    */
   protected final void updatePopupSize(int width, int height) {
      if (tW < width) {
         tW = width;
      }
      tH += height;
   }

   private void computePopupPosition() {
      // first position the popup relative to the parent container regardless of the position of the caret
      Point tp = textComponent.getLocationOnScreen();
      int windowX = tp.x + 5;
      int windowY = tp.y + textComponent.getHeight();

      // then adjust the position with the current caret position
      Point p = null;
      if (fromStart) {
         try {
            Rectangle2D charRec = textComponent.getFont().getStringBounds("A", FONT_CTX);
            int charHeight = (int) (charRec.getHeight() + 5);
            Point caretPosition = textComponent.modelToView(0).getLocation();
            p = new Point(caretPosition.x, -caretPosition.y - textComponent.getHeight() + charHeight);
         } catch (BadLocationException ex) {
         }
      } else {
         int caret = textComponent.getCaretPosition();
         try {
            Rectangle2D charRec = textComponent.getFont().getStringBounds("A", FONT_CTX);
            int charWidth = (int) (charRec.getWidth() + 5);
            int charHeight = (int) (charRec.getHeight() + 5);
            Point caretPosition = textComponent.modelToView(caret).getLocation();
            p = new Point(caretPosition.x + charWidth, -caretPosition.y - textComponent.getHeight() + charHeight);
         } catch (BadLocationException ex) {
         }
      }
      if (p != null) {
         windowY += p.y;
         windowX += p.x;
      }

      autoCompletePopUp.setLocation(windowX, windowY);
   }

   private void showPopUpWindow() {
      getWindow();
      autoCompletePopUp.getContentPane().removeAll();
      if (tH < maxHeight && tW < maxWidth) {
         autoCompletePopUp.getContentPane().add(suggestedHitsPanel);
         autoCompletePopUp.setSize(tW + 5, tH);
         autoCompletePopUp.setPreferredSize(new Dimension(tW + 5, tH));
         autoCompletePopUp.setMinimumSize(new Dimension(tW + 5, tH));
      } else {
         int _width = tW < maxWidth ? tW + 5 : maxWidth;
         int _height = tH < maxHeight ? tH + 5 : maxHeight;
         JScrollPane scroll = new JScrollPane(suggestedHitsPanel);
         JScrollBar bar = scroll.getHorizontalScrollBar();
         bar.setUI(new ScrollBarCustomUI());
         scroll.setHorizontalScrollBar(bar);
         bar = scroll.getVerticalScrollBar();
         bar.setUI(new ScrollBarCustomUI());
         scroll.setVerticalScrollBar(bar);
         if (opacity != 1f) {
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
         }
         autoCompletePopUp.getContentPane().add(scroll);
         autoCompletePopUp.setSize(tW + 5, tH);
         autoCompletePopUp.setPreferredSize(new Dimension(_width, _height));
         autoCompletePopUp.setMinimumSize(new Dimension(_width, _height));
      }

      autoCompletePopUp.pack();
      autoCompletePopUp.setVisible(true);

      computePopupPosition();
      autoCompletePopUp.revalidate();
      autoCompletePopUp.repaint();
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
    * Return the suggestion Popup Window. Will return null if {@link #isShowingPopup()} is false.
    *
    * @return the suggestion Popup Window
    */
   private JWindow getAutoSuggestionPopUpWindow() {
      return autoCompletePopUp;
   }

   /**
    * Return the text component.
    *
    * @return the text component
    */
   public JTextComponent getTextComponent() {
      return textComponent;
   }

   /**
    * The Label used for the Suggested Hits.
    *
    * @version 0.9.52
    */
   public class SuggestedHit extends JLabel {
      private final Item item;
      private final String searchedText;

      /**
       * Constructor.
       *
       * @param item the item
       */
      protected SuggestedHit(Item item) {
         super(item.getText());
         this.item = item;
         this.searchedText = null;
         initComponent();
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
         initComponent();
      }

      private void fireSuggestionAction() {
         timer.stop();
         timerListener.isActive = false;
         fireAction(typedOffset, item);
      }

      private void fireSearchTextAction() {
         timer.stop();
         timerListener.isActive = false;
         fireAction(searchedText);
      }

      private void initComponent() {
         this.setFont(textComponent.getFont());
         setFocusable(true);
         setForeground(getSuggestedHitTextColor());

         addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
               super.mouseClicked(me);
               if (item != null) {
                  replaceWithSuggestedText();
                  fireSuggestionAction();
               } else {
                  fireSearchTextAction();
               }
               getAutoSuggestionPopUpWindow().setVisible(false);
            }
         });
         addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                  if (!timerListener.isActive) {
                     timerListener.isActive = true;
                     timerListener.isUp = false;
                     timer.restart();
                  }
               } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                  if (!timerListener.isActive) {
                     timerListener.isActive = true;
                     timerListener.isUp = true;
                     timer.restart();
                  }
               }
            }

            @Override
            public void keyReleased(KeyEvent e) {
               timerListener.isActive = false;
               timer.stop();
            }
         });

         getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "EnterReleased");
         getActionMap().put("EnterReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               if (item != null) {
                  replaceWithSuggestedText();
                  fireSuggestionAction();
               } else {
                  fireSearchTextAction();
               }

               getAutoSuggestionPopUpWindow().setVisible(false);
            }
         });
      }

      /**
       * Set the Suggestion Label as focused.
       *
       * @param focused true if the Suggestion Label is focused
       */
      private void setFocused(boolean focused) {
         if (focused) {
            setBackground(getSuggestedHitFocusedColor());
            setForeground(Color.WHITE);
            setOpaque(true);
         } else {
            setBackground(null);
            setForeground(getSuggestedHitTextColor());
            setOpaque(false);
         }
         repaint();
         this.grabFocus();
      }

      private void replaceWithSuggestedText() {
         String suggestedWord = item.getText();
         String text = getCurrentlyTypedText(typedOffset);
         int _offset = typedOffset - text.length() + 1;
         String replacedText = text.replace(text, suggestedWord);
         Document doc = textComponent.getDocument();
         try {
            doc.remove(_offset, doc.getLength() - _offset);
            doc.insertString(_offset, replacedText, null);
         } catch (BadLocationException ex) {
         }
      }
   }

   private String getCurrentlyTypedText(int offset) {
      if (searchPerWord) {
         String text;
         try {
            text = textComponent.getText(offset, 1);
            char lastChar = text.charAt(text.length() - 1);
            if (lastChar == ' ' || lastChar == '\n') {
               return null;
            } else {
               int indexSpace = textComponent.getText(0, offset).lastIndexOf(' ');
               int indexNewLine = textComponent.getText(0, offset).lastIndexOf('\n');
               int index = indexSpace > indexNewLine ? indexSpace : indexNewLine;
               String lastWord = textComponent.getText(index + 1, offset - index);
               return lastWord;
            }
         } catch (BadLocationException ex) {
         }
         return null;
      } else {
         return textComponent.getText();
      }
   }

   private class SuggestionHitsPanel extends JPanel {
      private List<SuggestedHit> suggestions = new ArrayList<>();
      private int focusedHit = -1;

      private SuggestionHitsPanel() {
         super();
      }

      @Override
      public void removeAll() {
         super.removeAll();
         suggestions.clear();
         focusedHit = -1;
      }

      private SuggestedHit getFirstSuggestion() {
         return suggestions.get(0);
      }

      private SuggestedHit getLastSuggestion() {
         return suggestions.get(suggestions.size() - 1);
      }

      public void setFocusedHit(int index) {
         focusedHit = index;
      }

      @Override
      public Component add(Component comp) {
         comp = super.add(comp);
         if (comp instanceof SuggestedHit) {
            suggestions.add((SuggestedHit) comp);
         }
         return comp;
      }
   }

   private class TimerListenerImpl implements ActionListener {
      private boolean isUp = true;
      private boolean isActive = false;

      @Override
      public void actionPerformed(ActionEvent e) {
         if (isActive) {
            if (isUp) {
               applyScrollUp();
            } else {
               applyScrollDown();
            }
         }
      }

   }

   /**
    * Listens to the text component document evernts and also the key events if the search is performed on a
    * specific key code.
    */
   private class AutoCompleteListenerImpl implements DocumentListener, KeyListener {
      private volatile boolean mutating = false;
      private volatile boolean completed = false;

      private void removeAll() {
         // we use invokeLater because it is not possible to mutate the Document through the listener code
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  Document doc = textComponent.getDocument();
                  doc.remove(0, doc.getLength());
               } catch (BadLocationException ex) {
               }
            }
         });
      }

      private void checkSuggestions(final int offset, boolean removing) {
         if (showPopup) {
            getWindow();
            checkPopupSuggestions(removing);
         } else if (!mutating) {
            if (completed) {
               if (removing) {
                  // we use invokeLater because it is not possible to mutate the Document through the listener code
                  SwingUtilities.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                        mutating = true;
                        removeAll();
                        mutating = false;
                     }
                  });
               }
               completed = false;
            }
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  mutating = true;
                  String text = getCurrentlyTypedText(offset);
                  if (text != null) {
                     int _offset = offset - text.length() + 1;
                     typedOffset = _offset;
                     completed = checkOnlineSuggestions(_offset, text);
                  }
                  mutating = false;
               }
            });
         }
      }

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (startSearchImmediately) {
            int offset = de.getOffset() + de.getLength() - 1;
            checkSuggestions(offset, false);
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (startSearchImmediately) {
            int offset = de.getOffset() + de.getLength() - 1;
            checkSuggestions(offset, true);
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         if (startSearchImmediately) {
            int offset = de.getOffset() + de.getLength() - 1;
            checkSuggestions(offset, false);
         }
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
         if (!startSearchImmediately && e.isControlDown() && e.getKeyCode() == startSearchOnKeyCode) {
            checkSuggestions(textComponent.getCaretPosition(), false);
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
   }

   private class ScrollBarCustomUI extends MetalScrollBarUI {
      @Override
      protected void paintThumb(Graphics g, JComponent comp, Rectangle thumbBounds) {
         Color c = g.getColor();
         g.setColor(Color.LIGHT_GRAY);
         g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
         g.setColor(c);
      }
   }
}
