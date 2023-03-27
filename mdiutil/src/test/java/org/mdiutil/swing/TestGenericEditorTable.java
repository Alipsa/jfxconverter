/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Vector;
import javafx.scene.paint.Color;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

/**
 * An Editor which is able to edit structures in the Editor.
 *
 * @since 0.3
 */
public class TestGenericEditorTable extends JFrame {
   public TestGenericEditorTable() {
      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Value");
      Vector<Object> v = new Vector<>();
      v.add(true);
      model.addRow(v);
      v = new Vector<>();
      v.add(1);
      model.addRow(v);
      v = new Vector<>();
      v.add(Color.RED);
      model.addRow(v);

      ExtendedEditorTable table = new ExtendedEditorTable(model);
      TestGenericEditor editor = new TestGenericEditor(table);
      table.addEditor(0, 0, new ExtendedCellEditor(editor));
      table.addOpenEditorListener(0, 0, editor.getAppproveListener());
      table.addEditor(2, 0, new ExtendedCellEditor(new JColorSelector()));

      Container pane = this.getContentPane();
      pane.setLayout(new BorderLayout());
      pane.add(new JScrollPane(table));
      this.pack();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   public static void main(String[] args) {
      TestGenericEditorTable gen = new TestGenericEditorTable();
      gen.setVisible(true);
   }
}
