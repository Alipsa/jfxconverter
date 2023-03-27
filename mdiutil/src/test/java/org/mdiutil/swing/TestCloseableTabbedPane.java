/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mdiutil.swing.JCloseableTabbedPane.CloseListener;

/**
 * A GenericDialog example.
 *
 * @version 0.9.36
 */
public class TestCloseableTabbedPane extends JFrame {
   private final JCloseableTabbedPane closableTabbedPane;

   public TestCloseableTabbedPane() {
      super("Test");
      closableTabbedPane = new JCloseableTabbedPane();
      createContent();
   }

   private void createContent() {
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel firstPanel = new JPanel();
      firstPanel.setLayout(new BorderLayout());
      firstPanel.add(new JLabel("First Panel"), BorderLayout.CENTER);
      closableTabbedPane.addTab("First Panel", firstPanel);

      JPanel secondPanel = new JPanel();
      secondPanel.setLayout(new BorderLayout());
      secondPanel.add(new JLabel("Second Panel"), BorderLayout.CENTER);
      closableTabbedPane.addTab("Second Panel", secondPanel);

      closableTabbedPane.addCloseListener(new CloseListener() {
         @Override
         public void tabClosed(Component comp, String title) {
            System.out.println("Closed " + title);
         }
      });
      Container pane = this.getContentPane();
      pane.setLayout(new BorderLayout());
      pane.add(closableTabbedPane, BorderLayout.CENTER);
   }

   public static void main(String[] args) {
      TestCloseableTabbedPane frame = new TestCloseableTabbedPane();
      frame.setSize(400, 400);
      frame.setVisible(true);
   }
}
