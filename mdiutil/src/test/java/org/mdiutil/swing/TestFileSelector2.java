/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Unit tests for the JFileSelector class.
 *
 * @since 1.2.32
 */
public class TestFileSelector2 {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(500, 300);
      
      Container pane = f.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
      pane.add(new JLabel("File"));
      pane.add(Box.createHorizontalStrut(5));      
      
      JFileSelector fs = new JFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));
      URL url = TestFileSelector2.class.getResource("testFile.txt");
      File file = new File(url.getFile());
      fs.setSelectedFile(file);
      fs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JFileChooser fs = (JFileChooser) e.getSource();
            System.out.println(fs.getSelectedFile());
         }
      });
      pane.add(fs);
      pane.add(Box.createHorizontalGlue()); 
      
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setVisible(true);
   }
}
