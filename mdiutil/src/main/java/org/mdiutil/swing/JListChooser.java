/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * This component allows to define a list of data, which can be added / removed / changed.
 *
 * @version 0.9.21
 */
public class JListChooser extends JComponent {
   protected JDialog dialog;
   private ListChooserHandler handler;
   private Vector<Object> extValues = new Vector<>(1);
   private final Vector<Object> values = new Vector<>(1);
   private final JList<Object> list = new JList<>(values);
   private JComponent editor;
   private JPanel yesnopanel;
   private JPanel listpanel;
   private JPanel centralpanel = null;
   private JPanel optionspanel;
   private String title = "";
   private int returnValue;
   public static final int APPROVE_OPTION = 0;
   public static final int CANCEL_OPTION = 1;
   private AbstractAction yesaction;
   private AbstractAction cancelaction;
   private AbstractAction removeaction;
   private AbstractAction addaction;
   private AbstractAction changeaction;
   private AbstractAction upaction;
   private AbstractAction downaction;
   private JButton add;
   private JButton remove;
   private JButton change;
   private JButton up;
   private JButton down;

   /**
    * Constructor.
    */
   public JListChooser() {
      super();
   }

   /**
    * Constructor.
    *
    * @param editor the component used to edit the values
    * @param handler the handler which will listen to the editor component edits
    */
   public JListChooser(JComponent editor, ListChooserHandler handler) {
      this();
      this.editor = editor;
      this.handler = handler;
   }

   /**
    * Set the component used to edit the values.
    *
    * @param editor the component
    */
   public void setEditor(JComponent editor) {
      this.editor = editor;
   }

   /**
    * Set the handler which will listen to the editor component edits.
    *
    * @param handler the handler
    */
   @Deprecated
   public void setListChooserHandler(ListChooserHandler handler) {
      this.handler = handler;
   }

   /**
    * Return the associated list.
    *
    * @return the list
    */
   public JList<Object> getList() {
      return list;
   }

   @Override
   public String toString() {
      if ((extValues == null) || (extValues.isEmpty())) {
         return new String();
      } else {
         StringBuffer text = new StringBuffer("");
         for (int i = 0; i < extValues.size(); i++) {
            if (i > 0) {
               text = text.append(" ");
            }
            text = text.append("\"").append(extValues.get(i).toString()).append("\"");
         }
         return text.toString();
      }
   }

   /**
    * Return the Vector of values.
    *
    * @return the Vector
    */
   public Vector<Object> getValues() {
      return values;
   }

   /**
    * Return the list central panel.
    *
    * @return the list central panel
    */
   public JPanel getPanel() {
      if (centralpanel == null) {
         setUpListPanel();
      }
      return centralpanel;
   }

   private void setUpListPanel() {
      // listpanel
      listpanel = new JPanel();
      //listpanel.setLayout(new BoxLayout(listpanel, BoxLayout.Y_AXIS));
      listpanel.setLayout(new BorderLayout());

      // action buttons creation
      removeaction = new AbstractAction("Remove") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doRemove();
         }
      };

      addaction = new AbstractAction("Add") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doAdd();
         }
      };

      changeaction = new AbstractAction("Change") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doChange();
         }
      };

      upaction = new AbstractAction("Up") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doUp();
         }
      };

      downaction = new AbstractAction("Down") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doDown();
         }
      };

      // edit panel
      JPanel editpanel = new JPanel();
      editpanel.setLayout(new FlowLayout());
      editpanel.add(editor);

      list.setBorder(new LineBorder(Color.GRAY));
      listpanel.add(editpanel, BorderLayout.NORTH);
      listpanel.add(list, BorderLayout.CENTER);

      // principal panel layout
      centralpanel = new JPanel();
      centralpanel.setLayout(new BoxLayout(centralpanel, BoxLayout.X_AXIS));
      centralpanel.add(Box.createHorizontalStrut(10));
      centralpanel.add(listpanel);
      centralpanel.add(Box.createHorizontalStrut(10));
      centralpanel.add(Box.createHorizontalGlue());
      centralpanel.add(getOptionsPanel());
   }

   /**
    * Set up the panel contents.
    */
   private void setUp() {
      if (centralpanel == null) {
         setUpListPanel();
         // yes/no panel
         yesnopanel = new JPanel();
         yesnopanel.setLayout(new FlowLayout());

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

         yesnopanel.add(new JButton(yesaction), BorderLayout.WEST);
         yesnopanel.add(new JButton(cancelaction), BorderLayout.EAST);
         yesnopanel.setMaximumSize(yesnopanel.getMinimumSize());

         // layout of all panels
         this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
         this.add(centralpanel);
         this.add(Box.createVerticalGlue());
         this.add(yesnopanel);
      }

   }

   /**
    * Set the title.
    *
    * @param title the title
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * Set the handler responsible for handling the text appearance of the list selector.
    *
    * @param handler the handler
    */
   public void setHandler(ListChooserHandler handler) {
      this.handler = handler;
   }

   /**
    * Set the values of the list.
    *
    * @param extValues the Vector of values
    */
   public void setValues(Vector<Object> extValues) {
      this.extValues = extValues;
      copyToInternalValues();
      list.setListData(values);
      list.setSelectedIndex(values.size() - 1);
      this.repaint();
   }

   /**
    * Called when the user clicks on the Yes button.
    */
   private void doYes() {
      returnValue = JListChooser.APPROVE_OPTION;

      if (dialog != null) {
         copyToExternalValues();
         dialog.setVisible(false);
      }
   }

   /**
    * Called when the user clicks on the Cancel button.
    */
   private void doCancel() {
      returnValue = JListChooser.CANCEL_OPTION;
      if (dialog != null) {
         copyToInternalValues();
         dialog.setVisible(false);
      }
   }

   /**
    * Set up the internal values corresponding to the list.
    * It is performed at the definition of the list or when the user
    * cancels the selection.
    */
   private void copyToInternalValues() {
      Iterator it = extValues.iterator();
      values.removeAllElements();
      while (it.hasNext()) {
         values.add(it.next());
      }
      list.setListData(values);
   }

   /**
    * Set up the external values after a Yes selection.
    */
   private void copyToExternalValues() {
      Iterator it = values.iterator();
      extValues.removeAllElements();
      while (it.hasNext()) {
         extValues.add(it.next());
      }
   }

   /**
    * Add the current selection.
    */
   protected void doAdd() {
      Object value = handler.getCurrentValue(editor);
      if (!list.isSelectionEmpty()) {
         int[] indexes = list.getSelectedIndices();
         values.add(indexes[indexes.length - 1] + 1, value);
         list.setListData(values);
         list.setSelectedIndex(indexes[indexes.length - 1] + 1);
      } else {
         values.add(value);
         list.setListData(values);
         list.setSelectedIndex(values.size() - 1);
      }
      list.repaint();
   }

   /**
    * Remove the current selection.
    */
   protected void doRemove() {
      if (!list.isSelectionEmpty()) {
         int[] indexes = list.getSelectedIndices();
         for (int i = 0; i < indexes.length; i++) {
            values.removeElementAt(indexes[i]);
         }
         list.setListData(values);
         if (!values.isEmpty()) {
            if (indexes[0] > 0) {
               list.setSelectedIndex(indexes[0] - 1);
            } else {
               list.setSelectedIndex(indexes[0]);
            }
         }
         list.repaint();
      }
   }

   /**
    * Change the current selection.
    */
   protected void doChange() {
      if (!list.isSelectionEmpty()) {
         int index = list.getSelectedIndex();
         values.setElementAt(handler.getCurrentValue(editor), index);
         list.setListData(values);
         list.setSelectedIndex(index);
         list.repaint();
      }
   }

   /**
    * Move the current selection up.
    */
   protected void doUp() {
      if (!list.isSelectionEmpty()) {
         int index = list.getSelectedIndex();
         if (index > 0) {
            Object _up = values.get(index - 1);
            values.setElementAt(values.get(index), index - 1);
            values.setElementAt(_up, index);
            list.setListData(values);
            list.setSelectedIndex(index - 1);
            list.repaint();
         }
      }
   }

   /**
    * Move the current selection down.
    */
   protected void doDown() {
      if (!list.isSelectionEmpty()) {
         int index = list.getSelectedIndex();
         if (index < values.size() - 1) {
            Object _down = (String) values.get(index + 1);
            values.setElementAt(values.get(index), index + 1);
            values.setElementAt(_down, index);
            list.setListData(values);
            list.setSelectedIndex(index + 1);
            list.repaint();
         }
      }

   }

   /**
    * Show the dialog.
    *
    * @param parent the component parent
    * @return the return state of the file chooser on popdown:
    * <ul>
    * <li>JFileChooser.CANCEL_OPTION</li>
    * <li>JFileChooser.APPROVE_OPTION</li>
    * </ul>
    * @throws HeadlessException
    */
   public int showDialog(Component parent) throws HeadlessException {
      dialog = this.createDialog(parent);

      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = CANCEL_OPTION;
         }
      });

      dialog.setVisible(true);
      dialog.dispose();
      dialog = null;

      return returnValue;
   }

   protected JPanel getOptionsPanel() {
      add = new JButton(addaction);
      remove = new JButton(removeaction);
      change = new JButton(changeaction);
      up = new JButton(upaction);
      down = new JButton(downaction);

      // options buttons panel
      optionspanel = new JPanel();
      optionspanel.setLayout(new BoxLayout(optionspanel, BoxLayout.Y_AXIS));
      optionspanel.add(add);
      optionspanel.add(Box.createVerticalStrut(4));
      optionspanel.add(remove);
      optionspanel.add(Box.createVerticalStrut(4));
      optionspanel.add(change);
      optionspanel.add(Box.createVerticalStrut(4));
      optionspanel.add(up);
      optionspanel.add(Box.createVerticalStrut(4));
      optionspanel.add(down);
      optionspanel.setMaximumSize(optionspanel.getMinimumSize());

      return optionspanel;
   }

   protected JDialog createDialog(Component parent) throws HeadlessException {
      setUp();
      Frame frame = parent instanceof Frame ? (Frame) parent
              : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

      dialog = new JDialog(frame, title, true);

      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = JFileChooser.CANCEL_OPTION;
         }
      });

      Container contentPane = dialog.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(this, BorderLayout.CENTER);

      dialog.pack();

      return dialog;
   }

   public static void main(String[] args) {
      String[] fillValues = { "NONE", "HORIZONTAL", "VERTICAL VALUE", "BOTH" };
      JComboBox cb = new JComboBox(fillValues);
      cb.setSelectedItem("VERTICAL VALUE");

      ListChooserHandler handler = new ListChooserHandler() {
         @Override
         public Object getCurrentValue(JComponent c) {
            JComboBox cb = (JComboBox) c;
            return cb.getSelectedItem();
         }
      };
      JListChooser chooser = new JListChooser(cb, handler);

      chooser.setHandler(handler);
      chooser.setTitle("test");
      int retour = chooser.showDialog(null);
      if (retour == JListChooser.APPROVE_OPTION) {
         System.out.println("APPROVE: " + chooser.getValues().size());
      } else {
         System.out.println("CANCEL");
      }
      System.exit(0);
   }

   /**
    * This interface listen to changes in the editor component. THe associated values will be
    * used to fill the content of the list. For example, if component used to edit the list is a <code>JComboBox</code>:
    * <pre>
    * ListChooserHandler handler = new ListChooserHandler() {
    *    public Object getCurrentValue(JComponent c) {
    *       JComboBox cb = (JComboBox) c;
    *       return cb.getSelectedItem();
    *    }
    * };
    * </pre>
    *
    * @version 0.9.16
    */
   public interface ListChooserHandler {
      /**
       * Fired when the current value of the editor component has changed.
       *
       * @param c the component
       * @return the value for this component
       */
      public Object getCurrentValue(JComponent c);
   }
}
