/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * List chooser containing a <var>JTextField</var> and a <var>JButton</var>.
 * <ul>
 * <li>the <var>JTextField</var> shows the values of the list
 * <li>the <var>JButton</var> give access to a List Chooser
 * </ul>
 * <p>
 * the two components are connected, so when one of them is modified, the
 * other is modified accordingly.</p>
 *
 * @version 0.9.23
 */
public class JListSelector extends JComponent {
   private final List<ActionListener> mylisteners = new ArrayList<>();
   private AbstractAction openAction;
   private int BUTTONWIDTH = 30;
   private int BUTTONHEIGHT = 25;
   private final JTextField textopen = new JTextField();
   private JListChooser chooser;
   private JButton open;

   public JListSelector() {
   }

   public JListSelector(JListChooser lc) {
      chooser = lc;
      openAction = new AbstractAction("...") {
         public void actionPerformed(ActionEvent ae) {
            open();
         }
      };
      textopen.setText(lc.toString());

      open = new JButton(openAction);
      this.add(textopen);
      this.add(open);
      this.setLayout(null);
      //int preftextopenwidth = (int)(textopen.getPreferredSize().getWidth());
      int preftextopenwidth = 200;
      BUTTONWIDTH = (int) (open.getPreferredSize().getWidth());
      BUTTONHEIGHT = (int) (open.getPreferredSize().getHeight());
      textopen.setPreferredSize(new Dimension(preftextopenwidth, BUTTONHEIGHT));
      open.setPreferredSize(new Dimension(BUTTONWIDTH, BUTTONHEIGHT));
   }

   /**
    * Return the underlying JListChooser.
    *
    * @return the JListChooser
    */
   public JListChooser getListChooser() {
      return chooser;
   }

   /**
    * Set the underlying JListChooser.
    *
    * @param chooser the JListChooser
    */
   public void setListChooser(JListChooser chooser) {
      this.chooser = chooser;
   }

   public Vector<Object> getValues() {
      return chooser.getValues();
   }

   /**
    * Add an ActionListener.
    *
    * @param listener the ActionListener
    */
   public void addActionListener(ActionListener listener) {
      mylisteners.add(listener);
   }

   /**
    * Removes an ActionListener.
    *
    * @param listener the ActionListener
    */
   public void removeActionListener(ActionListener listener) {
      mylisteners.remove(listener);
   }

   @Override
   public String toString() {
      return textopen.getText();
   }

   /**
    * Resets the selector to the values of the list chooser.
    */
   public void reset() {
      setText(chooser.getValues());
      this.repaint();
   }

   @Override
   public void doLayout() {
      int height = BUTTONHEIGHT;
      if (this.getParent() != null) {
         Container parent = this.getParent();
         if (parent instanceof CellRendererPane) {
            height = this.getHeight();
         }
      }
      textopen.setBounds(0, 0, (int) (this.getSize().getWidth() - BUTTONWIDTH), height);
      open.setBounds((int) (this.getSize().getWidth() - BUTTONWIDTH), 0, BUTTONWIDTH, height);
      super.doLayout();
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension((int) (textopen.getPreferredSize().getWidth() + open.getPreferredSize().getWidth()), BUTTONHEIGHT);
   }

   @Override
   public void setBackground(Color color) {
      textopen.setBackground(color);
   }

   /**
    * Enables or disables this component.
    *
    * @param enabled true if this component should be enabled, false otherwise
    */
   @Override
   public void setEnabled(boolean enabled) {
      open.setEnabled(enabled);
   }

   /**
    * Return true if the List Selector is enabled.
    *
    * @return true if the List Selector is enabled
    */
   @Override
   public boolean isEnabled() {
      return open.isEnabled();
   }

   private void setText(Vector<Object> values) {
      StringBuffer text = new StringBuffer("");
      for (int i = 0; i < values.size(); i++) {
         if (i > 0) {
            text = text.append(" ");
         }
         text = text.append("\"").append(values.get(i).toString()).append("\"");
      }
      textopen.setText(text.toString());
   }

   public String getText() {
      return textopen.getText();
   }

   private void open() {
      int retour = chooser.showDialog(this);
      if (retour == JListChooser.APPROVE_OPTION) {
         setText(chooser.getValues());
         processEvent();
      }
   }

   private void processEvent() {
      Iterator<ActionListener> it = mylisteners.iterator();
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ChangeList");
      while (it.hasNext()) {
         it.next().actionPerformed(e);
      }
   }

   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);

      String[] fill_values = { "NONE", "HORIZONTAL", "VERTICAL VALUE", "BOTH" };
      JComboBox cb = new JComboBox(fill_values);
      cb.setSelectedItem("VERTICAL VALUE");

      JListChooser.ListChooserHandler handler = new JListChooser.ListChooserHandler() {
         @Override
         public Object getCurrentValue(JComponent c) {
            JComboBox cb = (JComboBox) c;
            return (String) (cb.getSelectedItem());
         }
      };
      JListChooser chooser = new JListChooser(cb, handler);
      chooser.setTitle("test");
      JListSelector ls = new JListSelector(chooser);

      ls.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.out.println("Action : ");
         }
      ;
      });
        f.getContentPane().add(ls);
      f.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      f.pack();
      f.setVisible(true);
   }
}
