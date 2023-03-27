/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 * Test for the PropertyEditor class.
 *
 * @since 0.9.3
 */
public class TestPropertyEditor {
   public static void main(String arg[]) {
      String[] fill_values = { "NONE", "HORIZONTAL", "VERTICAL VALUE", "BOTH" };
      JComboBox cb = new JComboBox(fill_values);
      cb.setSelectedItem("VERTICAL VALUE");

      JTextField tf = new JTextField("2");

      JButton bo = new JButton("1");
      JToggleButton bo2 = new JToggleButton("Toggle");

      JComboBox cb2 = new JComboBox(fill_values);
      cb2.setSelectedItem("VERTICAL VALUE");

      JListChooser.ListChooserHandler handler = new JListChooser.ListChooserHandler() {
         @Override
         public Object getCurrentValue(JComponent c) {
            JComboBox cb = (JComboBox) c;
            return cb.getSelectedItem();
         }
      };
      JListChooser lc = new JListChooser(cb2, handler);
      lc.setTitle("test");

      JListSelector ls = new JListSelector(lc);
      JFileSelector fs = new JFileSelector("tata");
      JFileSelector fs2 = new JFileSelector();
      JColorSelector cs = new JColorSelector();
      cs.setColor(Color.RED);

      cb.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            System.out.println("comboBox changed ");
         }
      });

      cs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            System.out.println("color changed ");
         }
      });

      ls.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent ae) {
            System.out.println("List changed ");
         }
      });

      PropertyEditor p = new PropertyEditor();
      p.addProperty(cb, "VERTICAL VALUE", "combo box");
      p.addProperty(tf, "2", "text");
      p.addProperty(bo, "1", "button");
      p.addProperty(bo2, true, "togglebutton");
      p.addProperty(fs, "", "file 1");
      p.addProperty(fs2, "", "file 2");
      p.addProperty(cs, "", "Color");
      p.addProperty(ls, "", "List");
      p.setVisible(true);

      JFrame f = new JFrame("test");
      f.setSize(300, 300);

      f.getContentPane().add(p, BorderLayout.CENTER);
      f.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      f.setVisible(true);
   }
}
