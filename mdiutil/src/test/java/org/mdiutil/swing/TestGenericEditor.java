/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.mdiutil.swing.TestGenericEditor.TestGenericPanel;

/**
 * An Editor which is able to edit structures in the Editor.
 *
 * @since 0.3
 */
public class TestGenericEditor extends AbstractGenericEditor<TestGenericPanel> {
   public TestGenericEditor(JTable table) {
      super(table);
      editor = new TestGenericPanel();
   }

   /**
    * Return the value modified by the user, after the dialog has closed.
    *
    * @return the value
    */
   @Override
   public Object getValue() {
      return editor.getValue();
   }

   /**
    * Reset the internal value of the Editor. Do nothing by default.
    *
    * @param value the value
    */
   @Override
   public void setValue(Object value) {
      editor.setValue((Boolean) value);
   }

   @Override
   public int getClickCountToStart() {
      return 2;
   }

   public class TestGenericPanel extends JPanel {
      private final JCheckBox cb = new JCheckBox("CheckBox");

      private TestGenericPanel() {
         this.setLayout(new BorderLayout());
         this.add(cb, BorderLayout.CENTER);
      }

      public boolean getValue() {
         return cb.isSelected();
      }

      public void setValue(boolean b) {
         cb.setSelected(b);
      }
   }

}
