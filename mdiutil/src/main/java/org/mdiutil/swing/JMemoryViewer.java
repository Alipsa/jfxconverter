/*------------------------------------------------------------------------------
 * Copyright (C) 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

/**
 * A memory viewer.
 *
 * @since 1.2.44
 */
public class JMemoryViewer extends JPanel implements ActionListener {
   private Dimension d;
   /**
    * The default width of the component.
    */
   public static final int DEFAULT_WIDTH = 150;
   /**
    * The default height of the component.
    */
   public static final int DEFAULT_HEIGHT = 25;
   /**
    * The status indicating that no value is shown on the component.
    */
   public static final short TYPE_SHOW_NONE = 0;
   /**
    * The status indicating that the remaining memory percentage is shown on the component.
    */
   public static final short TYPE_SHOW_PERCENT = 1;
   /**
    * The status indicating that the remaining megabytes is shown on the component.
    */
   public static final short TYPE_SHOW_MB = 2;
   private static final long MEGABYTE = 1024L * 1024L;
   private int prefWidth = DEFAULT_WIDTH;
   private short remainingTypeValue = TYPE_SHOW_PERCENT;
   private boolean useColorLimits = false;
   private float orangeColorLimit = 0.5f;
   private float redColorLimit = 0.8f;
   private final Timer timer;
   private long totalMemory;
   private long freeMemory;
   private float percent = 0f;
   private String shownValue = "";
   private final Font font;

   public JMemoryViewer() {
      super();
      d = new Dimension(prefWidth, DEFAULT_HEIGHT);
      this.setLayout(new BorderLayout());
      timer = new Timer(1000, this);
      font = new Font("Arial", Font.BOLD, 15);
      this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
   }

   /**
    * Start the memory viewer.
    */
   public void start() {
      timer.start();
   }

   /**
    * Stop the memory viewer.
    */
   public void stop() {
      timer.stop();
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setFont(font);
      g2d.setColor(Color.LIGHT_GRAY);
      g2d.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
      if (!useColorLimits) {
         g2d.setColor(Color.RED);
      } else {
         Color col = getColor(percent);
         g2d.setColor(col);
      }
      g2d.fillRect(0, 0, (int) (DEFAULT_WIDTH * percent), DEFAULT_HEIGHT);
      if (remainingTypeValue != TYPE_SHOW_NONE) {
         g.setColor(Color.WHITE);
         if (remainingTypeValue == TYPE_SHOW_PERCENT) {
            g.drawString(shownValue, DEFAULT_WIDTH - 40, DEFAULT_HEIGHT - 8);
         } else {
            g.drawString(shownValue, DEFAULT_WIDTH - 60, DEFAULT_HEIGHT - 8);
         }
      }
   }

   private Color getColor(float percent) {
      if (percent > redColorLimit) {
         return Color.RED;
      } else if (percent > orangeColorLimit) {
         return Color.ORANGE;
      } else {
         return Color.GREEN;
      }
   }

   /**
    * Return the size for the panel.
    *
    * @return the size
    */
   @Override
   public Dimension getSize() {
      return d;
   }

   /**
    * Return the preferred size for the panel.
    *
    * @return the size
    * @see #getSize()
    */
   @Override
   public Dimension getPreferredSize() {
      return getSize();
   }

   /**
    * Return the maximum size for the panel.
    *
    * @return the size
    * @see #getSize()
    */
   @Override
   public Dimension getMaximumSize() {
      return getSize();
   }

   /**
    * Set if the memory viewer use different colors to show the remaining memory depending on the free memory.
    *
    * @param useColorLimits true if the memory viewer use different colors to show the remaining memory depending on the free memory
    */
   public void setUseColorLimits(boolean useColorLimits) {
      this.useColorLimits = useColorLimits;
   }

   /**
    * Return true if the memory viewer use different colors to show the remaining memory depending on the free memory.
    *
    * @return true if the memory viewer use different colors to show the remaining memory depending on the free memory
    */
   public boolean isUsingColorLimits() {
      return useColorLimits;
   }

   /**
    * Set the color limits to use if {@link #isUsingColorLimits()} is true.
    *
    * @param orangeColorLimit the orange color limit
    * @param redColorLimit the red color limit
    */
   public void setColorLimits(float orangeColorLimit, float redColorLimit) {
      this.orangeColorLimit = orangeColorLimit;
      this.redColorLimit = redColorLimit;
   }

   /**
    * Return the orange color limit.
    *
    * @return the orange color limit
    */
   public float getOrangeColorLimit() {
      return orangeColorLimit;
   }

   /**
    * Return the red color limit.
    *
    * @return the red color limit
    */
   public float getRedColorLimit() {
      return redColorLimit;
   }

   /**
    * Set the remaining type value shown on the component. The allowed values are:
    * <ul>
    * <li>{@link #TYPE_SHOW_NONE}: The status indicating that no value is shown on the component</li>
    * <li>{@link #TYPE_SHOW_PERCENT}: The status indicating that the remaining memory percentage is shown on the component</li>
    * <li>{@link #TYPE_SHOW_MB}: The status indicating that the remaining memory percentage is shown on the component</li>
    * </ul>*
    *
    * @param remainingTypeValue the remaining type value
    */
   public void setRemainingTypeValue(short remainingTypeValue) {
      this.remainingTypeValue = remainingTypeValue;
   }

   /**
    * Return the remaining type value shown on the component. The allowed values are:
    * <ul>
    * <li>{@link #TYPE_SHOW_NONE}: The status indicating that no value is shown on the component</li>
    * <li>{@link #TYPE_SHOW_PERCENT}: The status indicating that the remaining memory percentage is shown on the component</li>
    * <li>{@link #TYPE_SHOW_MB}: The status indicating that the remaining memory percentage is shown on the component</li>
    * </ul>*
    *
    * @return the remaining type value
    */
   public short getRemainingTypeValue() {
      return remainingTypeValue;
   }

   /**
    * Set the preferred width of the component.
    *
    * @param width the width
    */
   public void setPreferredWidth(int width) {
      this.prefWidth = width;
      d = new Dimension(prefWidth, DEFAULT_HEIGHT);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      totalMemory = Runtime.getRuntime().totalMemory();
      freeMemory = Runtime.getRuntime().freeMemory();
      percent = 1f - (float) ((double) freeMemory / (double) totalMemory);
      if (remainingTypeValue == TYPE_SHOW_PERCENT) {
         shownValue = Integer.toString((int) (percent * 100)) + "%";
      } else if (remainingTypeValue == TYPE_SHOW_MB) {
         shownValue = Integer.toString((int) (freeMemory / MEGABYTE)) + " MB";
      }
      this.repaint();
   }

}
