/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 * Unit tests for the JMultilineTextEditor class.
 *
 * @version 0.9.59
 */
public class TestMultilineTextEditor2 {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      final JMultilineTextEditor editor = new JMultilineTextEditor(new JEditorPane());
      editor.setAllowImport(true);
      editor.setDialogDimension(new Dimension(200, 200));

      editor.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.out.println(editor.getText());
         }
      });
      f.getContentPane().add(editor);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
