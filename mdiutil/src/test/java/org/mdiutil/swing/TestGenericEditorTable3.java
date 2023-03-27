/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

/**
 * An Editor which is able to edit structures in the Editor.
 *
 * @since 0.9.59
 */
public class TestGenericEditorTable3 extends JFrame {
   public TestGenericEditorTable3() {
      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Value");
      Vector<Object> v = new Vector<>();
      v.add(true);
      model.addRow(v);
      v = new Vector<>();
      v.add(true);
      model.addRow(v);
      v = new Vector<>();
      v.add(true);
      model.addRow(v);

      ExtendedEditorTable table = new ExtendedEditorTable(model);
      DefaultCellEditor editor = new DefaultCellEditor(createBooleanCombo());
      table.addEditor(0, 0, editor);

      editor = new DefaultCellEditor(createBooleanCombo());
      table.addEditor(1, 0, editor);

      JMultilineTextEditor textEditor = new JMultilineTextEditor();
      textEditor.setAllowImport(true);
      editor = new ExtendedCellEditor(textEditor);
      table.addEditor(2, 0, editor);

      editor = new DefaultCellEditor(createBooleanCombo());
      table.addEditor(3, 0, editor);

      Container pane = this.getContentPane();
      pane.setLayout(new BorderLayout());
      pane.add(new JScrollPane(table));
      this.pack();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   private JComboBox createBooleanCombo() {
      JComboBox cb = new JComboBox();
      cb.addItem("false");
      cb.addItem("true");
      return cb;
   }

   public static void main(String[] args) {
      TestGenericEditorTable3 gen = new TestGenericEditorTable3();
      gen.setVisible(true);
   }
}
