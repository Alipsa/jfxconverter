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

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary;

/**
 * An example of JAutoComplete with categories.
 *
 * @version 0.9.52
 */
public class TestAutoCompleteCategories {

   public TestAutoCompleteCategories() {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JAutoComplete autoCompleter = new MyAutoComplete(new JTextField(10));
      autoCompleter.setOpacity(0.9f);
      autoCompleter.searchPerWord(true);

      autoCompleter.addAutoCompleteListener(new AutoCompleteListener() {
         @Override
         public void autoCompletePerformed(AutoCompleteEvent e) {
            JAutoComplete field = e.getSource();
            System.out.println(field.getText());
            System.out.println("Item: " + e.getItem());
         }
      });

      autoCompleter.addCategory("category 1");
      autoCompleter.addCategory("category 2");

      autoCompleter.addToDictionary("category 1", "hello");
      autoCompleter.addToDictionary("category 1", "heritage");
      autoCompleter.addToDictionary("category 1", "happiness");
      autoCompleter.addToDictionary("category 1", "woodbye");
      autoCompleter.addToDictionary("category 2", "cruel world");
      autoCompleter.addToDictionary("category 2", "war");
      autoCompleter.addToDictionary("category 2", "will");
      autoCompleter.addToDictionary("category 2", "world");
      autoCompleter.addToDictionary("category 2", "wall");

      JPanel p = new JPanel();

      p.add(autoCompleter.getTextComponent());

      frame.add(p);

      frame.pack();
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new TestAutoCompleteCategories();
         }
      });
   }

   public static class MyAutoComplete extends JAutoComplete {
      public MyAutoComplete(JTextComponent comp) {
         super(comp);
      }

      @Override
      public void addCategoryToSuggestions(AutoCompleteDictionary.Category category) {
         JLabel catLabel = new JLabel("<html><i><u>" + category.getName() + "</u></i></html>");
         catLabel.setFont(textComponent.getFont());
         Dimension prefSize = catLabel.getPreferredSize();
         updatePopupSize(prefSize.width, prefSize.height);
         suggestedHitsPanel.add(catLabel);
      }
   }
}
