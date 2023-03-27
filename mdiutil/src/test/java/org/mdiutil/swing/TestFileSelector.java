/*------------------------------------------------------------------------------
 * Copyright (C) 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Unit tests for the JFileSelector class.
 *
 * @since 0.9.43
 */
public class TestFileSelector {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      JFileSelector fs = new JFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));
      URL url = TestFileSelector.class.getResource("testFile.txt");
      File file = new File(url.getFile());
      fs.setSelectedFile(file);
      fs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JFileChooser fs = (JFileChooser) e.getSource();
            System.out.println(fs.getSelectedFile());
         }
      });
      f.getContentPane().add(fs);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
