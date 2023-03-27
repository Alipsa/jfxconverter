/*
Copyright (c) 2017, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on http://sourceforge.net/projects/docjgenerator/
 */
package org.docgene.help.examples;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import org.docgene.help.JavaHelpFactory;
import org.docgene.help.gui.swing.SwingHelpContentViewer;
import org.xml.sax.SAXException;

/**
 *
 * @since 0.87
 */
public class SwingJavaHelp extends JFrame {
   private JavaHelpFactory factory = null;
   private SwingHelpContentViewer viewer = null;

   public SwingJavaHelp() {
      super();
      this.setTitle("DocGenerator Help Tutorial");
      createFactory();
      createLayout();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   private void createFactory() {
      URL url = this.getClass().getResource("resources/articlesTutorial.zip");

      factory = new JavaHelpFactory(url);
      try {
         factory.create();
      } catch (IOException | SAXException ex) {
         ex.printStackTrace();
      }
   }

   private void createContent(Container cont) {
      JPanel areaPanel = new JPanel();
      areaPanel.setLayout(new BorderLayout());
      areaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      JTextArea area = new JTextArea();
      area.setText("my area content");
      areaPanel.add(area, BorderLayout.CENTER);
      cont.add(areaPanel, BorderLayout.CENTER);
   }

   private void createMenu() {
      JMenuBar menuBar = new JMenuBar();
      this.setJMenuBar(menuBar);
      JMenu menu = new JMenu("Help");
      menuBar.add(menu);
      AbstractAction helpAction = new AbstractAction("Help Content") {
         @Override
         public void actionPerformed(ActionEvent e) {
            viewer.showHelpDialog(menu);
         }
      };
      menu.add(helpAction);
   }

   private void createLayout() {
      Container cont = this.getContentPane();
      cont.setLayout(new BorderLayout());

      try {
         viewer = new SwingHelpContentViewer();
         factory.install(viewer);
         viewer.getHelpWindow(this, "Help Content", 600, 700);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      createContent(cont);
      createMenu();
   }

   public static void main(String[] args) {
      SwingJavaHelp helper = new SwingJavaHelp();
      helper.setSize(400, 400);
      helper.setVisible(true);
   }
}
