/*------------------------------------------------------------------------------
 * Copyright (C) 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * A TabbedPane with a close button.
 *
 * @since 0.9.46
 */
public class JCloseableTabbedPane extends JTabbedPane {
   private static final URL DEFAULT_IMAGE = JCloseableTabbedPane.class.getResource("closeTab.png");
   private static final Color DEFAULT_CLOSE_COLOR = new Color(230, 15, 24);
   private final List<CloseListener> closeListeners = new ArrayList<>();
   private ImageIcon closeIcon = null;
   private ImageIcon focusedCloseIcon = null;
   private Color closeColor = DEFAULT_CLOSE_COLOR;
   private boolean invertOnFocused = true;

   public JCloseableTabbedPane() {
      super();
      setIcons(DEFAULT_IMAGE);
   }

   private void setIcons(URL image) {
      try {
         BufferedImage img = ImageIO.read(image);
         closeIcon = new ImageIcon(img);
         if (invertOnFocused) {
            BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < img.getWidth(); x++) {
               for (int y = 0; y < img.getHeight(); y++) {
                  int rgba = img.getRGB(x, y);
                  Color col = new Color(rgba, true);
                  col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue(), col.getAlpha());
                  newImage.setRGB(x, y, col.getRGB());
               }
            }
            focusedCloseIcon = new ImageIcon(newImage);
         } else {
            focusedCloseIcon = closeIcon;
         }
      } catch (IOException ex) {
      }

   }

   /**
    * Set the close button background color when the close button is focused.
    *
    * @param color the background color
    */
   public void setCloseBackground(Color color) {
      this.closeColor = color;
   }

   /**
    * Use the default icon for the close button.
    */
   public void setDefaultCloseIcon() {
      setIcons(DEFAULT_IMAGE);
   }

   /**
    * Set if the close icon will have inverted colors if focused.
    *
    * @param invertOnFocused true if the close icon will have inverted colors if focused
    */
   public void setInvertBackgroundOnFocus(boolean invertOnFocused) {
      this.invertOnFocused = invertOnFocused;
   }

   /**
    * Return true if the close icon will have inverted colors if focused.
    *
    * @return true if the close icon will have inverted colors if focused
    */
   public boolean isInvertingBackgroundOnFocus() {
      return invertOnFocused;
   }

   /**
    * Set the close Icon. Note that by default, a default close icon will be used.
    *
    * @param image the close image
    */
   public void setCloseIcon(URL image) {
      setIcons(image);
   }

   /**
    * Return the close Icon. If {@link #setCloseIcon(URL)} was not set, the default close icon will be returned.
    *
    * @return the close Icon
    */
   public ImageIcon getCloseIcon() {
      return closeIcon;
   }

   /**
    * Return the close Icon when focused. If {@link #setCloseIcon(URL)} was not set, the default close icon will be returned.
    *
    * @return the close Icon when focused
    */
   public ImageIcon getFocusedCloseIcon() {
      return focusedCloseIcon;
   }

   /**
    * Add a CloseListener which will be fired when a tab is closed.
    *
    * @param listener the CloseListener
    */
   public void addCloseListener(CloseListener listener) {
      if (!closeListeners.contains(listener)) {
         closeListeners.add(listener);
      }
   }

   /**
    * Return the list of CloseListeners.
    *
    * @return the list of CloseListeners
    */
   public List<CloseListener> getCloseListeners() {
      return closeListeners;
   }

   /**
    * Remove a CloseListener.
    *
    * @param listener the CloseListener
    */
   public void removeCloseListener(CloseListener listener) {
      closeListeners.remove(listener);
   }

   /**
    * Inserts a new tab for the given component, at the given index.
    *
    * @param title the title to be displayed on the tab
    * @param icon the icon to be displayed on the tab
    * @param component the component to be displayed when this tab is clicked.
    * @param tip the tooltip to be displayed for this tab
    * @param index the position to insert this new tab
    */
   @Override
   public void insertTab(String title, Icon icon, Component component, String tip, int index) {
      super.insertTab(title, icon, component, tip, index);
      this.setTabComponentAt(index, new TabComponent(component, title));
   }

   private void fireTabClosed(Component comp, String title) {
      Iterator<CloseListener> it = closeListeners.iterator();
      while (it.hasNext()) {
         CloseListener listener = it.next();
         listener.tabClosed(comp, title);
      }
   }

   /**
    * This listener listens to the closing of a tab in the JClosableTabbedPane.
    *
    * @since 0.9.46
    */
   public interface CloseListener {
      /**
       * Fired when a tab is closed.
       *
       * @param comp the tab associated component
       * @param title the tab associated title
       */
      public void tabClosed(Component comp, String title);
   }

   private void closeTab(Component comp) {
      int index = this.indexOfComponent(comp);
      String title = this.getTitleAt(index);
      remove(index);
      fireTabClosed(comp, title);
   }

   private class TabComponent extends JPanel {
      private JLabel label = null;
      private TabButton closeButton = null;

      private TabComponent(final Component comp, String title) {
         this.label = new JLabel(title);

         closeButton = new TabButton(closeIcon);
         label.setOpaque(false);
         closeButton.setMaximumSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));
         closeButton.setMinimumSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));
         closeButton.setPreferredSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));

         this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
         this.add(label);
         this.add(Box.createHorizontalStrut(5));
         this.add(closeButton);

         closeButton.addActionListener(
            new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               closeTab(comp);
            }
         });

         this.setMaximumSize(null);
      }

      @Override
      public void paintComponent(Graphics g) {
      }
   }

   private class TabButton extends JButton {
      private boolean mouseEntered = false;

      private TabButton(Icon icon) {
         super(icon);
         this.setBackground(null);
         this.setBorderPainted(false);
         this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
               mouseEntered = true;
               if (closeColor != null) {
                  setBackground(closeColor);
               }
               if (invertOnFocused) {
                  setIcon(focusedCloseIcon);
               }
            }

            @Override
            public void mouseExited(MouseEvent e) {
               mouseEntered = false;
               setBackground(null);
               setIcon(closeIcon);
            }
         });
      }

      @Override
      protected void paintBorder(Graphics g) {
         if (mouseEntered) {
            super.paintBorder(g);
         }
      }
   }
}
