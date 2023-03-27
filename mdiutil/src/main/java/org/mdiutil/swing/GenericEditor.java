/*------------------------------------------------------------------------------
* Copyright (C) 2008, 2016, 2017 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.event.ActionListener;
import javax.swing.JComponent;

/**
 * A Generic editor to be used with {@link PropertyEditor}s or JTables.
 *
 * @version 0.9.23
 */
public interface GenericEditor {
   /**
    * Return the current value of the editor.
    *
    * @return the current value of the editor
    */
   public Object getValue();

   /**
    * Sets the current value of the editor.
    *
    * @param value the current value of the editor
    */
   public void setValue(Object value);

   /**
    * Sets the listener which will be fired in case the editing has stopped.
    *
    * @param listener the listener
    */
   public void addActionListener(ActionListener listener);

   /**
    * Return the component to use for the {@link PropertyEditor}s or JTable.
    *
    * @return the component
    */
   public JComponent getEditorComponent();

   /**
    * Return the number of clicks needed to start editing.
    *
    * @return the number of clicks needed to start editing
    */
   public int getClickCountToStart();
}
