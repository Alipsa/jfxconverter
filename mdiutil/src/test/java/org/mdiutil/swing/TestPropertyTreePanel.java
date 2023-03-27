/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Unit tests for the JMultipleFileSelector class.
 *
 * @version 0.9.25
 */
public class TestPropertyTreePanel {
   /** Test main method.
    */
   public static void main(String[] args) {
      // creates the JPropertyTreePanel
      JPropertyTreePanel panel = new JPropertyTreePanel();

      // creates first node Property View
      String[] fill_values = { "NONE", "HORIZONTAL", "VERTICAL VALUE", "BOTH" };
      JComboBox cb = new JComboBox(fill_values);
      cb.setSelectedItem("VERTICAL VALUE");
      JTextField tf = new JTextField("2");
      JButton bo = new JButton("1");
      JMultilineTextEditor textEdit = new JMultilineTextEditor(new JEditorPane());

      PropertyEditor p1 = new PropertyEditor();
      p1.addProperty(cb, "VERTICAL VALUE", "c'est un essai cette fois");
      p1.addProperty(tf, "2", "titi");
      p1.addProperty(bo, "1", "tatatatatatatat");
      p1.addProperty(textEdit, "toto", "text");
      p1.setVisible(true);

      DefaultMutableTreeNode node = panel.addEmptyNode(panel.getRootNode(), "sub-node");
      panel.addPropertyListNode(node, "node 1", p1);

      // creates second node Property View
      JFileSelector fs = new JFileSelector("tata");
      JFileSelector fs2 = new JFileSelector();
      JColorSelector cs = new JColorSelector();
      cs.setColor(Color.RED);

      PropertyEditor p2 = new PropertyEditor();
      p2.addProperty(fs, "", "bobo");
      p2.addProperty(fs2, "", "bobo");
      p2.addProperty(cs, "", "Color");
      p2.setVisible(true);

      panel.addPropertyListNode(node, "node 2", p2);
      panel.expandAll();

      JFrame f = new JFrame("test");
      f.setSize(400, 300);

      f.getContentPane().add(panel, BorderLayout.CENTER);
      f.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      f.setVisible(true);

   }
}
