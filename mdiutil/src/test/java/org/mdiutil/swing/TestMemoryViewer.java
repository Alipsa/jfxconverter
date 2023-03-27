/*------------------------------------------------------------------------------
 * Copyright (C) 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Container;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Unit tests for the JMemoryViewer class.
 *
 * @since 1.2.44
 */
public class TestMemoryViewer {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      JMemoryViewer mv = new JMemoryViewer();
      mv.setUseColorLimits(true);
      //mv.setRemainingTypeValue(JMemoryViewer.TYPE_SHOW_MB);
      mv.start();
      Container vpane = f.getContentPane();
      vpane.setLayout(new BoxLayout(vpane, BoxLayout.Y_AXIS));
      vpane.add(Box.createVerticalStrut(5));
      JPanel hpane = new JPanel();
      hpane.setLayout(new BoxLayout(hpane, BoxLayout.X_AXIS));
      hpane.add(Box.createHorizontalStrut(5));
      hpane.add(mv);
      hpane.add(Box.createHorizontalStrut(5));
      hpane.add(Box.createHorizontalGlue());

      vpane.add(hpane);
      vpane.add(Box.createVerticalStrut(5));
      vpane.add(Box.createVerticalGlue());

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
