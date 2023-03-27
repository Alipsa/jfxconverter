/*
Copyright (c) 2019, Herve Girod
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * An simple example of AutoComplete.
 *
 * @version 0.9.60
 */
public class TestAutoCompleteSearchText extends JFrame {
   private JAutoComplete autoCompleter;

   public TestAutoCompleteSearchText() {
      super();
      createGUI();
   }

   private void createGUI() {
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTextField tf = new JTextField(10);
      autoCompleter = new JAutoComplete(tf);
      autoCompleter.setOpacity(0.9f);
      autoCompleter.addAdditionalSearchItem("Containing...");

      autoCompleter.addAutoCompleteListener(new AutoCompleteListener() {
         @Override
         public void autoCompletePerformed(AutoCompleteEvent e) {
            System.out.println(e.getText() + " => " + e.getStartOffset());
         }
      });

      autoCompleter.addSearchTextListener(new AdditionalSearchListener() {
         @Override
         public void searchTextPerformed(AdditionalSearchEvent e) {
            System.out.println("Listen for containing text: " + e.getText());
         }
      });

      autoCompleter.addToDictionary("bye");
      autoCompleter.addToDictionary("hello");
      autoCompleter.addToDictionary("heritage");
      autoCompleter.addToDictionary("happiness");
      autoCompleter.addToDictionary("woodbye");
      autoCompleter.addToDictionary("war");
      autoCompleter.addToDictionary("will");
      autoCompleter.addToDictionary("world");
      autoCompleter.addToDictionary("cruel world");
      autoCompleter.addToDictionary("wall");

      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

      JPanel optionsPanel = new JPanel();
      final JCheckBox cb = new JCheckBox("Search per word");
      cb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            autoCompleter.searchPerWord(cb.isSelected());
         }
      });
      optionsPanel.add(cb);

      final JCheckBox cb2 = new JCheckBox("Hits from start");
      cb2.setSelected(true);
      cb2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            autoCompleter.searchHitsFromStart(cb2.isSelected());
         }
      });
      optionsPanel.add(cb2);

      final JCheckBox cb3 = new JCheckBox("Show Popup");
      cb3.setSelected(true);
      cb3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            autoCompleter.showPopup(cb3.isSelected());
         }
      });
      optionsPanel.add(cb3);

      final JCheckBox cb4 = new JCheckBox("Start search immediately");
      cb4.setSelected(true);
      cb4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!cb4.isSelected()) {
               autoCompleter.startSearchOnKeyCode(KeyEvent.VK_SPACE);
            } else {
               autoCompleter.startSearchOnKeyCode(-1);
            }
         }
      });
      optionsPanel.add(cb4);

      p.add(optionsPanel);
      p.add(Box.createVerticalStrut(5));
      JPanel fieldPanel = new JPanel();
      fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
      fieldPanel.add(new JLabel("Field"));
      fieldPanel.add(Box.createHorizontalStrut(5));
      fieldPanel.add(tf);
      fieldPanel.add(Box.createHorizontalStrut(5));
      fieldPanel.add(Box.createHorizontalGlue());

      p.add(fieldPanel);
      p.add(Box.createVerticalStrut(5));

      add(p);
      pack();
   }

   public static void main(String[] args) {
      TestAutoCompleteSearchText test = new TestAutoCompleteSearchText();
      test.setVisible(true);
   }
}
