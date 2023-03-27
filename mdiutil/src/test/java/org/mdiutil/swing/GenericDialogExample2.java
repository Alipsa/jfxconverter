/*------------------------------------------------------------------------------
 * Copyright (C) 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JCheckBox;

/**
 * A GenericDialog example.
 *
 * @since 0.9.36
 */
public class GenericDialogExample2 extends GenericDialog {
   private JCheckBox cb = null;

   public GenericDialogExample2() {
      super("Test");
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

   public static void main(String[] args) {
      GenericDialogExample2 dialog = new GenericDialogExample2();
      dialog.addDialogListener(new MyListener());
      dialog.showDialog();
   }

   private static class MyListener implements DialogListener {
      @Override
      public void apply(GenericDialog dialog) {
         System.out.println("applied");
      }

      @Override
      public void cancel(GenericDialog dialog) {
         System.out.println("cancelled");
      }

   }
}
