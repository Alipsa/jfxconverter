/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2013, 2014, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * User-interface component that presents datas in a two-dimensional
 * table format. It can be used to show and modify every types of properties.
 *
 * There is only two columns in this Table:
 * <ul>
 * <li>the left column shows the name of the property to be shown</li>
 * <li>the right column shows the value of this property to edit. All editors managed by the
 * {#link ExtendedCellEditor} class can be used</li>
 * </ul>
 *
 * It extends the functionalities of the generic <var>JTable</var> component by allowing to:
 * <ul>
 * <li>add every type of editor / renderer for each row, providing that they are
 * <var>JComponents</var> managed by the {#link ExtendedCellEditor} class</li>
 * </ul>
 *
 * The process of the creation of a PropertySelector must follow these rules :
 * <ul>
 * <li>create a new PropertySelector</li>
 * <li>add all the properties by the {@link #addProperty(JComponent, Object, String)} method</li>
 * <li>show the Table by the {@link #setVisible(boolean)} method</li>
 * </ul>
 * The component can then be used as any JPanel.
 *
 * Note: Events fired by each rows are managed by the editor added in the the row by the
 * {@link #addProperty(JComponent, Object, String)} method.
 *
 * <h1>Example</h1>
 * <pre>
 *  String[] fill_values = {"NONE", "HORIZONTAL", "VERTICAL VALUE", "BOTH"};
 *  JComboBox cb = new JComboBox(fill_values);
 *  cb.setSelectedItem("VERTICAL VALUE");
 *  JTextField tf = new JTextField("2");
 *
 *  PropertyEditor p = new PropertyEditor();
 *  p.addProperty(cb, "VERTICAL VALUE", "combo box");
 *  p.addProperty(tf, "2", "text");
 *  p.setVisible(true);
 * </pre>
 *
 * @see ExtendedCellEditor
 * @see ExtendedCellEditorAdaptor
 *
 * @version 0.9.23
 */
public class PropertyEditor extends JPanel implements TableModelListener {
   private static int DEFAULT_HEIGHT = 5;

   static {
      DEFAULT_HEIGHT = new JLabel("TEST").getPreferredSize().height * 2;
   }
   private final Vector columnNames = new Vector();
   private PropertyTable table;
   private PropertyEditorModel model;
   private Vector components = new Vector();
   private Dimension PreferredSize;
   private boolean editable = true;
   private String name;
   private JScrollPane scrollPane;
   private boolean markedToReset = true;

   /**
    * Create a new editor with default parameters.
    * <ul>
    * <li>default name :
    * <code>Property Editor</code></li>
    * <li>default column names :
    * <ul>
    * <li>properties column :
    * <code>Property</code>
    * <li>values column :
    * <code>Value</code>
    * </ul>
    * </ul>
    */
   public PropertyEditor() {
      this("Property Editor", "Property", "Value");
   }

   /**
    * Create a new editor and associate it with a name.
    *
    * @param name the name of the editor.
    */
   public PropertyEditor(String name) {
      this(name, "Property", "Value");
   }

   /**
    * Create a new editor and customize the columns names for properties and values.
    *
    * @param name the name of the editor (see {@link #toString()})
    * @param property the property column name
    * @param value the value column name
    */
   public PropertyEditor(String name, String property, String value) {
      super();
      setLayout(new BorderLayout());
      columnNames.add(property);
      columnNames.add(value);
      this.name = name;
   }

   /**
    * Add a new Property edited by the desired <var>JComponent</var> to the table.
    *
    * Example :
    * <pre>
    *   String[] fill_values = { "NONE", "HORIZONTAL", "VERTICAL VALUE","BOTH" };
    *   JComboBox cb = new JComboBox(fill_values);
    *   cb.setSelectedItem("VERTICAL VALUE");
    *   p.addProperty(cb, cb.getSelectedItem(), "property name");
    * </pre>
    * In this example, a new property is created with a JComboBox.
    *
    * Remarks:
    * <ul>
    * <li>It is necessary to provide a value for the property even if the JComponent already
    * have one, as there is no standard way of getting the current value of a JComponent used
    * for user modification. You should then be aware to put this value in the property editor,
    * else the value presented when not in editing mode will be different from the value presented
    * in editing mode</li>
    * <li>Only the components managed by the {@link ExtendedCellEditor} class are allowed</li>
    * </ul>
    *
    * @param co the JComponent used for the property
    * @param value the initial value for the property
    * @param name the name of the property
    */
   public void addProperty(JComponent co, Object value, String name) {
      components.add(new PropertyRow(co, value, name));
      markedToReset = true;
   }

   /**
    * Return the name of the Editor. The default name is ""Property Editor".
    */
   @Override
   public String toString() {
      return name;
   }

   /**
    * Do nothing by default.
    */
   @Override
   public void tableChanged(TableModelEvent ev) {
   }

   /**
    * Return the width of one colum.
    *
    * @param col the colum index
    * @return the width of one colum
    */
   public int getColumnWidth(int col) {
      TableColumn column = table.getColumnModel().getColumn(col);
      return column.getWidth();
   }

   /**
    * Return the underlying table model.
    *
    * @return the underlying table model
    */
   public AbstractTableModel getModel() {
      return (AbstractTableModel) model;
   }

   /**
    * Set the width of one colum.
    *
    * @param col the colum index
    * @param width the colum width
    */
   public void setColumnWidth(int col, int width) {
      TableColumn column = table.getColumnModel().getColumn(col);
      column.setPreferredWidth(width);
   }

   /**
    * Set the editable property of this editor.
    *
    * @param edit true if this Editor is editable
    */
   public void setEditable(boolean edit) {
      editable = edit;
   }

   /**
    * Set the name of this editor.
    *
    * @param name the Editor name
    */
   @Override
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Return true if this editor is editable.
    *
    * @return true if this editor is editable
    */
   public boolean isEditable() {
      return editable;
   }

   @Override
   public Dimension getPreferredSize() {
      return PreferredSize;
   }

   private void processResize() {
      for (int i = 0; i < table.getRowCount(); i++) {
         JComponent co = ((PropertyRow) (components.elementAt(i))).getComponent();
         co.setSize(getColumnWidth(1), co.getHeight());
      }
   }

   /**
    * Reset the content of the PropertyEditor. This can be used after the PropertyEditor has been made visible, and
    * you want to add or remove properties.
    */
   public void reset() {
      if (scrollPane != null) {
         this.remove(scrollPane);
         components.removeAllElements();
         markedToReset = true;
         this.revalidate();
      }
   }

   private void initCellSizes() {
      int i, rowheight, colwidth, height = 0, valuewidth = 10, namewidth = 10;
      JTextField tf;

      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            processResize();
         }
      });
      for (i = 0; i < table.getRowCount(); i++) {
         rowheight = (int) (((PropertyRow) (components.elementAt(i))).getComponent().getPreferredSize().getHeight());
         // we protect under the case where the rowheight is 0
         if (rowheight > 0) {
            table.setRowHeight(i, rowheight);
         } else {
            table.setRowHeight(i, DEFAULT_HEIGHT);
         }
         height = height + rowheight;
         colwidth = (int) (((PropertyRow) (components.elementAt(i))).getComponent().getPreferredSize().getWidth());
         valuewidth = Math.max(valuewidth, colwidth);
         tf = new JTextField(((PropertyRow) (components.elementAt(i))).getName());
         colwidth = (int) tf.getPreferredSize().getWidth();
         namewidth = Math.max(namewidth, colwidth);
      }
      setColumnWidth(0, namewidth);
      setColumnWidth(1, valuewidth);
      PreferredSize = new Dimension(namewidth + valuewidth, height);
   }

   /* <p>set the table visible. This method must be applied after having added all
    * the rows.
    * <p>Note : The <var>ExtendedCellEditor</var> used to edit the rows are internally managed by
    * <var>ExtendedCellEditorAdaptor</var> classes. It is then possible to add every type
    * of <var>JComponent</var> for a row, even if we don't know which component it is.
    */
   @Override
   public void setVisible(boolean bool) {
      boolean oldVisible = this.isVisible();
      super.setVisible(bool);
      if ((!oldVisible && bool) || markedToReset) {
         markedToReset = false;
         model = new PropertyEditorModel(components, columnNames);
         table = new PropertyTable(model);

         // create a RowEditorModel... this is used to hold the extra
         // information that is needed to deal with row specific editors
         RowEditorModel rm = new RowEditorModel();

         // tell the PropertyTable which RowEditorModel we are using
         table.setRowEditorModel(rm);
         model.addTableModelListener(this);

         // loop to populate each row with the proper editor
         if (editable) {
            for (int i = 0; i < components.size(); i++) {
               // create a DefaultCellEditor to use in the PropertyTable column
               JComponent po = ((PropertyRow) (components.elementAt(i))).getComponent();
               rm.addEditorForRow(i, new ExtendedCellEditorAdaptor(po));
            }
         }
         this.initCellSizes();
         table.setPreferredScrollableViewportSize(new Dimension(500, 200));
         scrollPane = new JScrollPane(table);
         add(scrollPane, BorderLayout.CENTER);
      }
   }
}

class PropertyTable extends JTable {
   protected RowEditorModel rowEdMod;

   public PropertyTable() {
      super();
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param tm the data model for the table
    */
   public PropertyTable(TableModel tm) {
      super(tm);
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param tm the data model for the table
    * @param cm the column model for the table
    */
   public PropertyTable(TableModel tm, TableColumnModel cm) {
      super(tm, cm);
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param tm the data model for the table
    * @param cm the column model for the table
    * @param sm the row selection model for the table
    */
   public PropertyTable(TableModel tm, TableColumnModel cm, ListSelectionModel sm) {
      super(tm, cm, sm);
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param rows the number of rows
    * @param cols the number of columns
    */
   public PropertyTable(int rows, int cols) {
      super(rows, cols);
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param rowData the data for the new table
    * @param colNames names of each column
    */
   public PropertyTable(final Vector rowData, final Vector colNames) {
      super(rowData, colNames);
      rowEdMod = null;
   }

   /**
    * Constructor.
    *
    * @param rowData the data for the new table
    * @param colNames names of each column
    */
   public PropertyTable(final Object[][] rowData, final Object[] colNames) {
      super(rowData, colNames);
      rowEdMod = null;
   }

   /**
    * Set the row editor model for the table.
    *
    * @param rm the row editor model for the table
    */
   void setRowEditorModel(RowEditorModel rm) {
      this.rowEdMod = rm;
   }

   /**
    * Return the row editor model for the table.
    *
    * @return the row editor model for the table
    */
   RowEditorModel getRowEditorModel() {
      return rowEdMod;
   }

   @Override
   public TableCellEditor getCellEditor(int row, int col) {
      TableCellEditor tCellEd = null;
      if (rowEdMod != null) {
         tCellEd = rowEdMod.getEditor(row);
      }
      if (tCellEd != null) {
         return tCellEd;
      }
      return super.getCellEditor(row, col);
   }

   @Override
   public TableCellRenderer getCellRenderer(int row, int col) {
      if (col == 1) {
         PropertyEditorModel p = (PropertyEditorModel) (this.getModel());
         return new CustomTableCellRenderer(p.getComponentAt(row));
      } else {
         return new DefaultTableCellRenderer();
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

class RowEditorModel {
   private final Map<Integer, TableCellEditor> data;

   public RowEditorModel() {
      data = new HashMap(10);
   }

   public void addEditorForRow(int row, TableCellEditor e) {
      data.put(row, e);
   }

   public void removeEditorForRow(int row) {
      data.remove(row);
   }

   public TableCellEditor getEditor(int row) {
      return data.get(row);
   }
}

class PropertyEditorModel extends AbstractTableModel {
   private final Vector mydata;
   private final Vector myColumnNames;

   public PropertyEditorModel(Vector data, Vector colNames) {
      mydata = data;
      myColumnNames = colNames;
   }

   @Override
   public String getColumnName(int col) {
      return (String) (myColumnNames.elementAt(col));
   }

   @Override
   public boolean isCellEditable(int row, int col) {
      if (col < 1) {
         return false;
      } else {
         return true;
      }
   }

   public Component getComponentAt(int row) {
      PropertyRow data = (PropertyRow) (mydata.elementAt(row));
      return data.getComponent();
   }

   @Override
   public Object getValueAt(int row, int col) {
      PropertyRow data = (PropertyRow) (mydata.elementAt(row));
      if (col == 0) {
         return data.getName();
      } else {
         return data.getValue();
      }
   }

   @Override
   public void setValueAt(Object value, int row, int col) {
      if (col == 1) {
         ((PropertyRow) mydata.elementAt(row)).setValue(value);
         fireTableCellUpdated(row, col);
      }
   }

   @Override
   public int getRowCount() {
      return mydata.size();
   }

   @Override
   public int getColumnCount() {
      return 2;
   }
}

class PropertyRow {
   private final String myName;
   private Object myvalue;
   private final JComponent myco;

   public PropertyRow(JComponent co, Object value, String name) {
      myName = name;
      myvalue = value;
      myco = co;
   }

   public String getName() {
      return myName;
   }

   public Object getValue() {
      return myvalue;
   }

   public void setValue(Object value) {
      myvalue = value;
   }

   public JComponent getComponent() {
      return myco;
   }
}
