/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 * A Color chooser containing a <var>JTextField</var> and a <var>JButton</var>.
 * <ul>
 * <li>the <var>Icon</var> presenting the chosen color</li>
 * <li>the <var>JButton</var> give access to a File Chooser</li>
 * </ul>
 *
 * @version 0.9.23
 */
public class JColorSelector extends JComponent {
   private JColorChooser chooser;
   private final List<ActionListener> mylisteners = new ArrayList<>();
   private AbstractAction openAction;
   private JButton open;
   private ColorIcon icon;

   /**
    * Constructor.
    *
    * @param chooser the color chooser
    */
   public JColorSelector(JColorChooser chooser) {
      super();
      this.chooser = chooser;
      icon = new ColorIcon(chooser.getColor());
      openAction = new AbstractAction("", icon) {
         @Override
         public void actionPerformed(ActionEvent ae) {
            open();
         }
      };
      open = new JButton(openAction);
      this.add(open);
      this.setLayout(null);
      int minwidth = (int) (open.getMinimumSize().getWidth());
      int minheight = (int) (open.getMinimumSize().getHeight());
      open.setPreferredSize(new Dimension(ColorIcon.BUTTONWIDTH, ColorIcon.BUTTONHEIGHT));
      open.setMinimumSize(new Dimension(minwidth, minheight));
   }

   /**
    * Constructor with a default color chooser.
    */
   public JColorSelector() {
      this(new JColorChooser());
   }

   /**
    * Constructor with a default color chooser and an initial Color.
    *
    * @param color the initial color
    */
   public JColorSelector(Color color) {
      this(new JColorChooser(color));
   }

   /**
    * Constructor with a default color chooser and a ColorSelectionModel.
    *
    * @param model the ColorSelectionModel
    */
   public JColorSelector(ColorSelectionModel model) {
      this(new JColorChooser(model));
   }

   /**
    * Return the backed color chooser
    *
    * @return the color chooser
    */
   public JColorChooser getColorChooser() {
      return chooser;
   }

   /**
    * Set the selected color.
    *
    * @param color the color
    */
   public void setColor(Color color) {
      chooser.setColor(color);
      icon.setColor(color);
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
    * Return true if the Color Selector is enabled.
    *
    * @return true if the Color Selector is enabled
    */
   @Override
   public boolean isEnabled() {
      return open.isEnabled();
   }

   private void open() {
      chooser.setColor(JColorChooser.showDialog(this, "choose Color", this.getColorChooser().getColor()));
      processEvent();
      icon.setColor(chooser.getColor());
   }

   @Override
   public void doLayout() {
      int hgap = (int) ((this.getSize().getWidth() - ColorIcon.BUTTONWIDTH) / 2);
      int vgap = (int) ((this.getSize().getHeight() - ColorIcon.BUTTONHEIGHT) / 2);
      open.setBounds(hgap, vgap, ColorIcon.BUTTONWIDTH, ColorIcon.BUTTONHEIGHT);
      super.doLayout();
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension((int) (open.getPreferredSize().getWidth()), (int) (open.getPreferredSize().getHeight()));
   }

   private void processEvent() {
      Iterator<ActionListener> it = mylisteners.iterator();
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SelectColor");
      while (it.hasNext()) {
         it.next().actionPerformed(e);
      }
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

   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      JColorSelector cs = new JColorSelector(new DefaultColorSelectionModel());
      cs.setColor(Color.lightGray);

      cs.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.out.println("Action : ");
         }
      });

      f.getContentPane().setLayout(new FlowLayout());
      f.getContentPane().add(cs);
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

class ColorIcon implements Icon {
   private Color color = Color.lightGray;
   public final static int BUTTONWIDTH = 30;
   public final static int BUTTONHEIGHT = 20;

   public ColorIcon(Color color) {
      this.color = color;
   }

   @Override
   public int getIconWidth() {
      return BUTTONWIDTH;
   }

   @Override
   public int getIconHeight() {
      return BUTTONHEIGHT;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   @Override
   public void paintIcon(Component comp, Graphics g, int x, int y) {
      g.setColor(color);
      g.fillRect(x, y, this.getIconWidth(), this.getIconHeight());
   }
}
