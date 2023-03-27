/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;

/**
 * Unit tests for the JMultipleFileSelector class.
 *
 * @version 1.2.37
 */
public class TestMultipleFileSelector3 {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      JMultipleFileSelector fs = new JMultipleFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));
      f.getContentPane().add(fs);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      fs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JMultipleFileSelector selector = (JMultipleFileSelector) e.getSource();
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < selector.getSelectedFiles().length; i++) {
               if (first) {
                  first = false;
               } else {
                  buf.append(" ");
               }
               buf.append(selector.getSelectedFiles()[i].getAbsolutePath());
            }
            System.out.println(buf.toString());
         }
      });
      f.pack();
      f.setVisible(true);
   }
}
