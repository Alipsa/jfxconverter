/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2018, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A JTable which is able to use custom TableCellEditors.
 *
 * @version 0.9.59
 */
public class ExtendedEditorTable extends JTable {
   /**
    * The Map of listeners which listens to opening of editors.
    */
   protected final Map<Integer, Map<Integer, ActionListener>> openEditorListeners = new HashMap<>();
   /**
    * The Map of editors.
    */
   protected final Map<Integer, Map<Integer, TableCellEditor>> editors = new HashMap<>();
   /**
    * The Map of renderers.
    */
   protected final Map<Integer, Map<Integer, TableCellRenderer>> renderers = new HashMap<>();

   public ExtendedEditorTable() {
      super();
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> that is initialized with <code>dm</code> as the data model, a default column model,
    * and a default selection model.
    *
    * @param dm the data model for the table
    */
   public ExtendedEditorTable(TableModel dm) {
      super(dm);
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> that is initialized with
    * <code>dm</code> as the data model, <code>cm</code>
    * as the column model, and a default selection model.
    *
    * @param dm the data model for the table
    * @param cm the column model for the table
    */
   public ExtendedEditorTable(TableModel dm, TableColumnModel cm) {
      super(dm, cm);
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> that is initialized with
    * <code>dm</code> as the data model, <code>cm</code> as the
    * column model, and <code>sm</code> as the selection model.
    *
    * @param dm the data model for the table
    * @param cm the column model for the table
    * @param sm the row selection model for the table
    */
   public ExtendedEditorTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
      super(dm, cm, sm);
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> with <code>numRows</code> and <code>numColumns</code> of empty cells using <code>DefaultTableModel</code>.
    *
    * @param numRows the number of rows
    * @param numColumns the number of columns
    */
   public ExtendedEditorTable(int numRows, int numColumns) {
      super(numRows, numColumns);
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> to display the values in the <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>,
    * with column names, <code>columnNames</code>.
    *
    * @param rowData the data for the new table
    * @param columnNames names of each column
    */
   public ExtendedEditorTable(Vector rowData, Vector columnNames) {
      super(rowData, columnNames);
      setUp();
   }

   /**
    * Constructs a <code>JTable</code> to display the values in the two dimensional array,
    * <code>rowData</code>, with column names, <code>columnNames</code>.
    *
    * @param rowData the data for the new table
    * @param columnNames names of each column
    */
   public ExtendedEditorTable(final Object[][] rowData, final Object[] columnNames) {
      super(rowData, columnNames);
      setUp();
   }

   private void listenToMousePressed(MouseEvent e) {
      Point p = e.getPoint();
      int row = rowAtPoint(p);
      int column = columnAtPoint(p);
      TableCellEditor tcellEditor = getCellEditor(row, column);
      if (tcellEditor != null
         && (!(tcellEditor instanceof DefaultCellEditor) || e.getClickCount() == ((DefaultCellEditor) tcellEditor).getClickCountToStart())) {
         // check if the row has an associated specific Editor, then open it
         if (openEditorListeners.containsKey(row)) {
            Map<Integer, ActionListener> map = openEditorListeners.get(row);
            if (map.containsKey(column)) {
               fireOpenEditor(row, column, e.getWhen(), e.getModifiers());
            }
         }
      } else if (tcellEditor != null && (tcellEditor instanceof ExtendedCellEditor)) {
         unfocusCell();
      }
   }

   private void setUp() {
      // process double click on table
      this.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            listenToMousePressed(e);
         }
      });
   }

   private void unfocusCell() {
      // this is necessary to correctly repaint the table, it will remove the focus on the current cell
      this.editCellAt(-1, -1);
   }

   /**
    * Set the editor to use with a particular row and column.
    *
    * @param row the row index
    * @param column the column index
    * @param editor the Editor
    */
   public void addEditor(int row, int column, TableCellEditor editor) {
      Map<Integer, TableCellEditor> map;
      if (editors.containsKey(row)) {
         map = editors.get(row);
      } else {
         map = new HashMap<>();
         editors.put(row, map);
      }
      if (editor instanceof ExtendedCellEditor) {
         ExtendedCellEditor extEditor = (ExtendedCellEditor) editor;
         if (extEditor.getEditorType() != ExtendedCellEditor.GENERIC) {
            Component comp = extEditor.getComponent();
            CustomTableCellRenderer customRDR = new CustomTableCellRenderer(comp);
            addRenderer(row, column, customRDR);
         }
      }
      map.put(column, editor);
   }

   /**
    * Remove the editor used with a particular row and column.
    *
    * @param row the row index
    * @param column the column index
    * @return the previous editor for the row and column, or null if there was no editor for this row and column
    */
   public TableCellEditor removeEditor(int row, int column) {
      if (editors.containsKey(row)) {
         Map<Integer, TableCellEditor> map = editors.get(row);
         return map.remove(column);
      } else {
         return null;
      }
   }

   /**
    * Return the specific TableCellEditor for a row and column. Note that contrary to the {@link #getCellEditor(int, int)}, this method will return
    * null if there is no specific TableCellEditor defined for the row and column.
    *
    * @param row the row index
    * @param column the column index
    * @return editor the TableCellEditor
    */
   public TableCellEditor getEditor(int row, int column) {
      if (editors.containsKey(row)) {
         Map<Integer, TableCellEditor> map = editors.get(row);
         if (map.containsKey(column)) {
            return map.get(column);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Returns an appropriate editor for the cell specified by <code>row</code> and <code>column</code>.Note that contrary to
    * the {@link #getEditor(int, int)}, this method will defer to the parent class if there is no specific TableCellEditor defined for the row and column.
    *
    * @param row the row of the cell to edit
    * @param column the column of the cell to edit
    * @return the TableCellEditor
    */
   @Override
   public TableCellEditor getCellEditor(int row, int column) {
      if (!editors.containsKey(row)) {
         return super.getCellEditor(row, column);
      } else {
         Map<Integer, TableCellEditor> map = editors.get(row);
         if (!map.containsKey(column)) {
            return super.getCellEditor(row, column);
         } else {
            return map.get(column);
         }
      }
   }

   /**
    * Remove the cell renderer used with a particular row and column.
    *
    * @param row the row index
    * @param column the column index
    * @return the previous editor for the row and column, or null if there was no editor for this row and column
    */
   public TableCellRenderer removeRenderer(int row, int column) {
      if (renderers.containsKey(row)) {
         Map<Integer, TableCellRenderer> map = renderers.get(row);
         return map.remove(column);
      } else {
         return null;
      }
   }

   /**
    * Set the cell renderer to use with a particular row and column.
    *
    * @param row the row index
    * @param column the column index
    * @param renderer the cell renderer
    */
   public void addRenderer(int row, int column, TableCellRenderer renderer) {
      Map<Integer, TableCellRenderer> map;
      if (renderers.containsKey(row)) {
         map = renderers.get(row);
      } else {
         map = new HashMap<>();
         renderers.put(row, map);
      }
      map.put(column, renderer);
   }

   /**
    * Returns the renderer for the cell specified by this row and column. If there is no specific renderrer for the row and column, the method will defer
    * to the parent class.
    *
    * @param row the row of the cell to render, where 0 is the first row
    * @param column the column of the cell to render, where 0 is the first column
    * @return the assigned renderer; if <code>null</code> returns the default renderer for this type of object
    */
   @Override
   public TableCellRenderer getCellRenderer(int row, int column) {
      if (renderers.containsKey(row)) {
         Map<Integer, TableCellRenderer> map = renderers.get(row);
         if (map.containsKey(column)) {
            // we needed to remove it, else in some cases the editotrs did not fire
            //unfocusCell();
            return map.get(column);
         } else {
            return super.getCellRenderer(row, column);
         }
      } else {
         return super.getCellRenderer(row, column);
      }
   }

   /**
    * Add a listener which listen to opening of editors.
    *
    * @param row the row where to add the listener
    * @param column the column where to add the listener
    * @param listener the listener
    */
   public void addOpenEditorListener(int row, int column, ActionListener listener) {
      Map<Integer, ActionListener> map;
      if (openEditorListeners.containsKey(row)) {
         map = openEditorListeners.get(row);
      } else {
         map = new HashMap<>();
         openEditorListeners.put(row, map);
      }
      map.put(column, listener);
   }

   /**
    * Remove a listener which listens to opening of editors.
    *
    * @param row the row where to remove the listener
    * @param column the column where to remove the listener
    */
   public void removeOpenEditorListener(int row, int column) {
      if (openEditorListeners.containsKey(row)) {
         Map<Integer, ActionListener> map = openEditorListeners.get(row);
         if (map.containsKey(column)) {
            map.remove(column);
            if (map.isEmpty()) {
               openEditorListeners.remove(row);
            }
         }
      }

   }

   /*
    * Open the specific Editor on a specified row.
    *
    * @param row the row
    * @param column the column
    */
   protected void fireOpenEditor(final int row, final int column, long when, int modifiers) {
      if (openEditorListeners.containsKey(row)) {
         Map<Integer, ActionListener> map = openEditorListeners.get(row);
         if (map.containsKey(column)) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "doubleClick", when, modifiers);
            ActionListener listener = map.get(column);
            listener.actionPerformed(event);
            if (editors.containsKey(row)) {
               Map<Integer, TableCellEditor> map1 = editors.get(row);
               if (map1.containsKey(column)) {
                  TableCellEditor editor = map1.get(row);
                  if (editor instanceof ExtendedCellEditor) {
                     ExtendedCellEditor extEditor = (ExtendedCellEditor) editor;
                     final Object value = extEditor.getCellEditorValue();
                     SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           getModel().setValueAt(value, row, column);
                        }
                     });
                  }
               }
            }
            // this is necessary to correctly repaint the table, it will remove the focus on the current cell
            unfocusCell();
         }
      }
   }

   class CustomTableCellRenderer implements TableCellRenderer {
      private final Component myComp;

      public CustomTableCellRenderer(Component comp) {
         myComp = comp;
      }

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasfocus, int row, int col) {
         return myComp;
      }
   }
}
