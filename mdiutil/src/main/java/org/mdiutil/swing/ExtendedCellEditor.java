/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2008, 2009, 2012, 2014, 2016, 2017, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Extends the capabilities of the <var>DefaultCellEditor</var> class, by adding new managed CellEditors.
 * <ul>
 * <li>JButton or ToggleButton</li>
 * <li>JCheckBox</li>
 * <li>JComboBox</li>
 * <li>JTextField</li>
 * <li>JSpinner</li>
 * <li>JButton</li>
 * <li>{@link JFileSelector}</li>
 * <li>{@link JMultipleFileSelector}</li>
 * <li>{@link JColorSelector}</li>
 * <li>{@link JListSelector}</li>
 * <li>{@link JMultilineTextEditor}</li>
 * <li>JSpinner</li>
 * <li>any {@link GenericEditor}</li>
 * </ul>
 * @link JColorSelector
 * @link JListSelector
 * @link JFileSelector
 * @link JMultilineTextEditor
 * @link GenericEditor
 *
 * @version 0.9.43
 */
public class ExtendedCellEditor extends DefaultCellEditor {
   private final int type;
   /**
    * The type for textfield editors.
    */
   public static int TEXTFIELD = 1;
   /**
    * The type for combobox editors.
    */
   public static int COMBOBOX = 2;
   /**
    * The type for checkbox editors.
    */
   public static int CHECKBOX = 3;
   /**
    * The type for file selector editors.
    */
   public static int FILESELECTOR = 4;
   /**
    * The type for multiple file selector editors.
    */
   public static int MULTIPLEFILESELECTOR = 5;
   /**
    * The type for button editors.
    */
   public static int BUTTON = 6;
   /**
    * The type for color selector editors.
    */
   public static int COLORSELECTOR = 7;
   /**
    * The type for list selector editors.
    */
   public static int LISTSELECTOR = 8;
   /**
    * The type for spinner editors.
    */
   public static int SPINNER = 9;
   /**
    * The type for text editors.
    */
   public static int TEXTEDITOR = 10;
   /**
    * The type for generic editors.
    */
   public static int GENERIC = 11;
   /**
    * The type for toggle button editors.
    */
   public static int TOGGLEBUTTON = 12;
   private GenericEditor editor = null;

   /**
    * Constructor, with a <code>JCheckBox</code>.
    */
   public ExtendedCellEditor() {
      super(new JCheckBox());
      type = CHECKBOX;
   }

   /**
    * Constructor, with a <code>JCheckBox</code>.
    *
    * @param b the JCheckBox
    */
   public ExtendedCellEditor(JCheckBox b) {
      super(b);
      type = CHECKBOX;
   }

   /**
    * Constructor, with a <code>JComboBox</code>.
    *
    * @param b the JComboBox
    */
   public ExtendedCellEditor(JComboBox b) {
      super(b);
      type = COMBOBOX;
   }

   /**
    * Constructor, with a <code>JTextField</code>.
    *
    * @param b the JTextField
    */
   public ExtendedCellEditor(JTextField b) {
      super(b);
      type = TEXTFIELD;
   }

   /**
    * Constructor, with a <code>GenericEditor</code>.
    *
    * @param b the GenericEditor
    */
   public ExtendedCellEditor(GenericEditor b) {
      super(new JCheckBox());
      type = GENERIC;
      editor = b;
      editorComponent = b.getEditorComponent();
      setClickCountToStart(b.getClickCountToStart());

      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JSpinner</code>.
    *
    * @param b the JSpinner
    */
   public ExtendedCellEditor(JSpinner b) {
      super(new JCheckBox());
      type = SPINNER;
      editorComponent = b;
      //This is usually 1 or 2.
      setClickCountToStart(1);

      //Must do this so that editing stops when appropriate.
      b.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            //fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JColorSelector</code>.
    *
    * @param b the JColorSelector
    */
   public ExtendedCellEditor(JColorSelector b) {
      super(new JCheckBox());
      type = COLORSELECTOR;
      editorComponent = b;
      setClickCountToStart(1); //This is usually 1 or 2.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JFileSelector</code>.
    *
    * @param b the JFileSelector
    */
   public ExtendedCellEditor(JFileSelector b) {
      super(new JCheckBox());
      type = FILESELECTOR;
      editorComponent = b;
      //This is usually 1 or 2.
      setClickCountToStart(1);

      //Must do this so that editing stops when appropriate.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JMultipleFileSelector</code>.
    *
    * @param b the JMultipleFileSelector
    */
   public ExtendedCellEditor(JMultipleFileSelector b) {
      super(new JCheckBox());
      type = MULTIPLEFILESELECTOR;
      editorComponent = b;
      //This is usually 1 or 2.
      setClickCountToStart(1);

      //Must do this so that editing stops when appropriate.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JListSelector</code>.
    *
    * @param b the JListSelector
    */
   public ExtendedCellEditor(JListSelector b) {
      super(new JCheckBox());
      type = LISTSELECTOR;
      editorComponent = b;
      //This is usually 1 or 2.
      setClickCountToStart(1);

      //Must do this so that editing stops when appropriate.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with a <code>JMultilineTextEditor</code>.
    *
    * @param editor the JMultilineTextEditor
    */
   public ExtendedCellEditor(JMultilineTextEditor editor) {
      super(new JCheckBox());
      type = TEXTEDITOR;
      editorComponent = editor;
      //This is usually 1 or 2.
      setClickCountToStart(1);

      //Must do this so that editing stops when appropriate.
      editor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with an <code>JButton</code>.
    *
    * @param b the JButton
    */
   public ExtendedCellEditor(JButton b) {
      super(new JCheckBox());
      type = BUTTON;
      editorComponent = b;
      setClickCountToStart(1); //This is usually 1 or 2.

      //Must do this so that editing stops when appropriate.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   /**
    * Constructor, with an <code>JToggleButton</code>.
    *
    * @param b the JToggleButton
    */
   public ExtendedCellEditor(JToggleButton b) {
      super(new JCheckBox());
      type = TOGGLEBUTTON;
      editorComponent = b;
      setClickCountToStart(1); //This is usually 1 or 2.

      //Must do this so that editing stops when appropriate.
      b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
         }
      });
   }

   @Override
   public void fireEditingStopped() {
      super.fireEditingStopped();
   }

   @Override
   public Object getCellEditorValue() {
      if (type == BUTTON) {
         return ((JButton) editorComponent).getText();
      } else if (type == TOGGLEBUTTON) {
         return ((JToggleButton) editorComponent).isSelected();
      } else if (type == LISTSELECTOR) {
         return ((JListSelector) editorComponent).getText();
      } else if (type == FILESELECTOR) {
         return ((JFileSelector) editorComponent).getSelectedFile();
      } else if (type == MULTIPLEFILESELECTOR) {
         return ((JMultipleFileSelector) editorComponent).getName();
      } else if (type == SPINNER) {
         return ((JSpinner) editorComponent).getValue();
      } else if (type == TEXTEDITOR) {
         return ((JMultilineTextEditor) editorComponent).getDocument();
      } else if (type == GENERIC) {
         return editor.getValue();
      } else {
         return super.getCellEditorValue();
      }
   }

   /**
    * Return the editor type.
    *
    * @return the editor type
    */
   public int getEditorType() {
      return type;
   }

   @Override
   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      String vs;
      if (value != null) {
         vs = value.toString();
      } else {
         vs = "";
      }
      if (type == BUTTON) {
         ((JButton) editorComponent).setText(vs);
      } else if (type == SPINNER) {
         // need to do this as value is NOT a numeric value in the table
         //((JSpinner)editorComponent).setValue(value);
      } else if (type == FILESELECTOR) {
         File file = new File(vs);
         if ((((JFileSelector) editorComponent).getFileSelectionMode() == JFileChooser.FILES_ONLY) && (file.isDirectory())) {
            ((JFileSelector) editorComponent).setCurrentDirectory(file);
         } else {
            ((JFileSelector) editorComponent).setSelectedFile(file);
         }
      } else if (type == MULTIPLEFILESELECTOR) {
         File file = new File(vs);
         if ((((JMultipleFileSelector) editorComponent).getFileSelectionMode() == JFileChooser.FILES_ONLY) && (file.isDirectory())) {
            ((JMultipleFileSelector) editorComponent).setCurrentDirectory(file);
         }
      }
      return editorComponent;
   }
}
