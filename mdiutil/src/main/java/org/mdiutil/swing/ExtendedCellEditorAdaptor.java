/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * Adapts the <var>ExtendedCellEditor</var> class. It is then possible to create a
 * new cell editor with a <var>JComponent</var>, even if we don't know of wich class it is
 * an instance (providing it can be managed by the <var>ExtendedCellEditor</var>).
 *
 * @see ExtendedCellEditor
 *
 * @version 0.9.6
 */
public class ExtendedCellEditorAdaptor implements TableCellEditor {
   private ExtendedCellEditor cellEd = null;

   public ExtendedCellEditorAdaptor(JComponent jcomp) {
      // create a DefaultCellEditor to use in the PropertyTable column
      Class[] c1 = new Class[1];
      Object[] obj = new Object[1];
      c1[0] = jcomp.getClass();
      obj[0] = jcomp;  // create an array with the JComponent

      // need to do this in case this is a generic Editor
      if (jcomp instanceof GenericEditor) {
         obj[0] = (GenericEditor) jcomp;
         c1[0] = GenericEditor.class;
      }

      /* try to add Editor for row of index i : new CustomCellEditor
       * created with the proper constructor (with base class as argument) */
      try {
         Constructor construct = ExtendedCellEditor.class.getConstructor(c1); // get the proper constructor
         try {
            cellEd = (ExtendedCellEditor) (construct.newInstance(obj));
         } catch (InstantiationException e) {
            System.out.println("InstantiationException");
         } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException");
         } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException");
         }
      } catch (NoSuchMethodException e) {
         System.out.println("NoSuchMethodException");
      }
   }

   public ExtendedCellEditor getCellEditor() {
      return cellEd;
   }

   @Override
   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      return cellEd.getTableCellEditorComponent(table, value, isSelected, row, column);
   }

   @Override
   public Object getCellEditorValue() {
      return cellEd.getCellEditorValue();
   }

   @Override
   public void cancelCellEditing() {
      cellEd.cancelCellEditing();
   }

   @Override
   public void addCellEditorListener(CellEditorListener l) {
      cellEd.addCellEditorListener(l);
   }

   @Override
   public void removeCellEditorListener(CellEditorListener l) {
      cellEd.removeCellEditorListener(l);
   }

   @Override
   public boolean isCellEditable(EventObject anEvent) {
      return cellEd.isCellEditable(anEvent);
   }

   @Override
   public boolean shouldSelectCell(EventObject anEvent) {
      return cellEd.shouldSelectCell(anEvent);
   }

   @Override
   public boolean stopCellEditing() {
      return cellEd.stopCellEditing();
   }
}
