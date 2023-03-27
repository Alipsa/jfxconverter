/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.io.File;
import java.net.URL;
import javax.swing.JFrame;

/**
 * Unit tests for the JMultipleFileSelector class.
 *
 * @version 0.9.25
 */
public class TestMultipleFileSelector {
   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      f.setSize(200, 300);
      JMultipleFileSelector fs = new JMultipleFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));
      URL url = TestMultipleFileSelector.class.getResource("testFile.txt");
      File file = new File(url.getFile());
      File[] files = new File[1];
      files[0] = file;
      fs.setSelectedFiles(files);
      f.getContentPane().add(fs);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
