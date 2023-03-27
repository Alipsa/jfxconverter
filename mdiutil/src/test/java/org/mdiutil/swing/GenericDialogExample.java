/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

/**
 * A GenericDialog example.
 *
 * @version 0.9.36
 */
public class GenericDialogExample extends GenericDialog {
   private JCheckBox cb = null;

   public GenericDialogExample() {
      super("Test", null, true);
      createDialog(null);
   }

   @Override
   protected void createPanel() {
      Container pane = dialog.getContentPane();

      pane.setLayout(new BorderLayout());
      cb = new JCheckBox("My CheckBox");
      pane.add(cb, BorderLayout.CENTER);
      pane.add(createYesNoPanel(), BorderLayout.SOUTH);
   }

   private boolean isSelected() {
      return cb.isSelected();
   }

   public static void main(String[] args) {
      GenericDialogExample dialog = new GenericDialogExample();
      int ret = dialog.showDialog();
      if (ret == JFileChooser.APPROVE_OPTION) {
         System.out.println("Selected: " + dialog.isSelected());
      }
      System.exit(0);
   }
}
