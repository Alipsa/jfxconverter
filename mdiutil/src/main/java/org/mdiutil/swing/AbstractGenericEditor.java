/*------------------------------------------------------------------------------
* Copyright (C) 2017, 2019, 2020 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * An abstract implementation of the Generic editor.
 *
 * <h1>Usage example</h1>
 * Suppose that we want to add a specific editor for a boolean value which will pop-up for a cell in the table, rather than the default editor.
 *
 * We would specify the editor as:
 * <pre>
 * public class MyGenericEditor extends AbstractGenericEditor&lt;MyGenericPanel&gt; {
 *   public MyGenericEditor(JTable table) {
 *   super(table);
 *   editor = new TestGenericPanel();
 * }
 *
 * public Object getValue() {
 *   return editor.getValue();
 * }
 *
 * public void setValue(Object value) {
 *   editor.setValue((Boolean) value);
 * }
 *
 * public class MyGenericPanel extends JPanel {
 *   private final JCheckBox cb = new JCheckBox("CheckBox");
 *
 *   private TestGenericPanel() {
 *     this.setLayout(new BorderLayout());
 *     this.add(cb, BorderLayout.CENTER);
 *   }
 *
 *   public boolean getValue() {
 *     return cb.isSelected();
 *   }
 *
 *   public int getClickCountToStart() {
 *     return 2;
 *   }
 *
 *   public void setValue(boolean b) {
 *     cb.setSelected(b);
 *   }
 * }
 * </pre>
 *
 * And we would set table as:
 * <pre>
 *   DefaultTableModel model = new DefaultTableModel();
 *   model.addColumn("Value");
 *   Vector&lt;Object&gt; v = new Vector&lt;&gt;();
 *   v.add(true);
 *   model.addRow(v);
 *   v = new Vector&lt;&gt;();
 *   v.add(1);
 *   model.addRow(v);
 *
 *   ExtendedEditorTable table = new ExtendedEditorTable(model);
 *   TestGenericEditor editor = new TestGenericEditor(table);
 *   table.addEditor(0, 0, new ExtendedCellEditor(editor));
 *   table.addOpenEditorListener(0, 0, editor.getAppproveListener());
 * </pre>
 *
 * @param <C> the editor component class
 * @version 1.2.6
 */
public abstract class AbstractGenericEditor<C extends JComponent> implements GenericEditor {
   /**
    * The component to use in the table for the TableCell. By default it will be a JTextField.
    */
   protected JComponent cellComp;
   /**
    * The Dialog window used by the Editor.
    */
   protected JDialog editorDialog;
   /**
    * The Yes / Cancel Panel.
    */
   protected JPanel yesnopanel;
   /**
    * The Yes action.
    */
   protected AbstractAction yesaction;
   /**
    * The Cancel action.
    */
   protected AbstractAction cancelaction;
   /**
    * The return value;
    */
   protected int returnValue;
   /**
    * The parent frame.
    */
   protected JFrame frame;
   /**
    * The parent component.
    */
   protected JComponent parent;
   /**
    * The editor component.
    */
   protected C editor = null;
   /**
    * The listener which will be fired when the changes on the editor have been approved.
    */
   private ActionListener approveListener = null;
   private String title = "";

   /**
    * Constructor.
    *
    * @param parent the parent
    */
   public AbstractGenericEditor(JComponent parent) {
      super();
      this.parent = parent;
      approveListener = new ApproveListener(this);
      cellComp = new JTextField();
      ((JTextField) cellComp).setEditable(false);
   }

   /**
    * Set the listener which will be fired when approving the editor changes.
    *
    * @param listener the listener.
    */
   public void setAppproveListener(ActionListener listener) {
      this.approveListener = listener;
   }

   /**
    * Return the listener which will be fired when approving the editor changes.
    *
    * @return the listener.
    */
   public ActionListener getAppproveListener() {
      return approveListener;
   }

   /**
    * Set the Editor component.
    *
    * @param editor the component
    */
   public void setEditor(C editor) {
      this.editor = editor;
   }

   /**
    * Return the Editor component.
    *
    * @return the component
    */
   public C getEditor() {
      return editor;
   }

   /**
    * Return the parent frame.
    *
    * @return the parent frame
    */
   public JFrame getParentFrame() {
      return frame;
   }

   /**
    * Return the parent component.
    *
    * @return the parent component
    */
   public JComponent getParent() {
      return parent;
   }

   /**
    * Set the dialog title.
    *
    * @param title the title
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * Return the dialog title.
    *
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * Do nothing by default.
    *
    * @param listener the listener
    */
   @Override
   public void addActionListener(ActionListener listener) {
   }

   /**
    * Create the Yes / No bottom Panel.
    *
    * Note that it is possible to define a completely different panel can be defined by overriding this method. It is
    * even possible to define a panel with a completely different content, even without Yes /No buttons.
    *
    * @return the Yes/No Panel
    */
   protected JPanel createYesNoPanel() {
      // yes/no panel
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new FlowLayout());

      yesaction = new AbstractAction("Yes") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doYes();
         }
      };

      cancelaction = new AbstractAction("Cancel") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doCancel();
         }
      };

      thePanel.add(new JButton(yesaction), BorderLayout.WEST);
      thePanel.add(new JButton(cancelaction), BorderLayout.EAST);
      thePanel.setMaximumSize(thePanel.getMinimumSize());

      return thePanel;
   }

   /**
    * Set up the panel contents.
    */
   protected void setUp() {
      editorDialog = new JDialog(frame, title, true);
      editorDialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = JFileChooser.CANCEL_OPTION;
         }
      });

      // yes/no panel
      yesnopanel = createYesNoPanel();

      // layout of all panels
      editorDialog.setLayout(new BorderLayout());
      editorDialog.add(editor, BorderLayout.CENTER);
      editorDialog.add(yesnopanel, BorderLayout.SOUTH);
   }

   /**
    * Called when user click on Yes button.
    */
   protected void doYes() {
      returnValue = JFileChooser.APPROVE_OPTION;
      if (editorDialog != null) {
         editorDialog.setVisible(false);
      }
   }

   /**
    * Called when user click on Cancel button.
    */
   protected void doCancel() {
      returnValue = JFileChooser.CANCEL_OPTION;
      if (editorDialog != null) {
         editorDialog.setVisible(false);
      }
   }

   private JDialog createDialog(Component parent) throws HeadlessException {
      setUp();
      frame = parent instanceof JFrame ? (JFrame) parent : (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
      editorDialog.pack();

      return editorDialog;
   }

   /**
    * Open the dialog, and return the result of the opening.
    *
    * @return the result of the opening. The returned value will be the same as the value that can be returned by
    * the {@link JFileChooser#showOpenDialog(Component)}
    */
   public int showDialog() throws HeadlessException {
      JDialog dialog = createDialog(parent);
      // Cannot be replaced by lambda. this a SonarQube mistake
      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = JFileChooser.CANCEL_OPTION;
         }
      });
      dialog.setVisible(true);
      dialog.dispose();

      return returnValue;
   }

   /**
    * Called when the editor is closed after {@link JFileChooser#APPROVE_OPTION}. Do nothing by default.
    */
   public void performApproveAction() {
   }

   /**
    * Return the component to use for the {@link PropertyEditor}s or JTable. By default it will be a JTextField.
    *
    * @return the component
    */
   @Override
   public JComponent getEditorComponent() {
      return cellComp;
   }

   /**
    * The default listener which will be fired when approving the editor changes.
    */
   public static class ApproveListener implements ActionListener {
      private final AbstractGenericEditor editor;

      /**
       * Constructor.
       *
       * @param editor the editor
       */
      public ApproveListener(AbstractGenericEditor editor) {
         this.editor = editor;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         int ret = editor.showDialog();
         if (ret == JFileChooser.APPROVE_OPTION) {
            editor.performApproveAction();
         }
      }
   }
}
