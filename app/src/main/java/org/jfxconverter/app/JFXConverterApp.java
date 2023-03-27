/*
Copyright (c) 2018, 2020 Herve Girod
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
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.jfxconverter.app;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javafx.scene.Node;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.jfxconverter.app.driversutils.EPSDriverAppUtils;
import org.jfxconverter.app.driversutils.PPTDriverAppUtils;
import org.jfxconverter.app.driversutils.SVGDriverAppUtils;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.swing.ExtensionFileFilter;

/**
 * A simple application which allows to convert using a Script file.
 *
 * @version 0.23
 */
public class JFXConverterApp extends JFrame {
   private JCheckBox extCB = null;
   private JTextField titleTf = null;
   private JButton convertButton = null;
   private JButton selectContentButton = null;
   private ScriptWrapper wrapper = null;

   public JFXConverterApp() {
      super("Converter Application");
      createContent();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   private void createContent() {
      this.setSize(400, 100);
      Container pane = this.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

      // options panel
      extCB = new JCheckBox("Extended Conversion");
      JPanel optionsPanel = new JPanel();
      optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
      optionsPanel.add(extCB);

      optionsPanel.add(Box.createHorizontalStrut(5));
      JLabel label = new JLabel("Title");
      optionsPanel.add(label);
      optionsPanel.add(Box.createHorizontalStrut(10));
      titleTf = new JTextField(10);
      titleTf.setText("Default Title");
      optionsPanel.add(titleTf);
      optionsPanel.add(Box.createHorizontalStrut(5));
      optionsPanel.add(Box.createHorizontalGlue());

      pane.add(optionsPanel);
      pane.add(Box.createVerticalStrut(5));

      // conversion panel
      JPanel conversionPanel = new JPanel();
      conversionPanel.setLayout(new BoxLayout(conversionPanel, BoxLayout.X_AXIS));
      selectContentButton = new JButton("Select Script");
      selectContentButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            selectContent();
         }
      });
      conversionPanel.add(Box.createHorizontalStrut(5));
      conversionPanel.add(selectContentButton);
      conversionPanel.add(Box.createHorizontalStrut(40));

      convertButton = new JButton("Convert");
      convertButton.setEnabled(false);
      convertButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            convert();
         }
      });
      conversionPanel.add(convertButton);
      conversionPanel.add(Box.createHorizontalStrut(5));
      conversionPanel.add(Box.createHorizontalGlue());
      pane.add(conversionPanel);
      pane.add(Box.createVerticalStrut(5));
      pane.add(Box.createVerticalGlue());
   }

   private void selectContent() {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Select Groovy Script");
      chooser.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            String name = f.getName();
            return f.isDirectory() || name.endsWith(".groovy");
         }

         @Override
         public String getDescription() {
            return "Groovy Scripts";
         }
      });
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "groovy");
         try {
            getScript(file);
            String text = FileUtilities.getFileNameBody(file);
            selectContentButton.setText(text);
            convertButton.setEnabled(true);
         } catch (Exception ex) {
            ex.printStackTrace();
            wrapper = null;
         }
      }
   }

   private void convert() {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Select output file");
      ExtensionFileFilter epsFilter = new ExtensionFileFilter("EPS Files");
      epsFilter.add(".eps");
      ExtensionFileFilter pptFilter = new ExtensionFileFilter("PPT Files");
      pptFilter.add(".ppt");
      ExtensionFileFilter svgFilter = new ExtensionFileFilter("SVG Files");
      svgFilter.add(".svg");
      chooser.addChoosableFileFilter(epsFilter);
      chooser.addChoosableFileFilter(pptFilter);
      chooser.addChoosableFileFilter(svgFilter);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();

         if (file != null) {
            String ext = "svg";
            FileFilter filter = chooser.getFileFilter();
            if (filter != null && filter instanceof ExtensionFileFilter) {
               ExtensionFileFilter extFilter = (ExtensionFileFilter) filter;
               ext = extFilter.getUniqueExtension().substring(1);
            }
            file = FileUtilities.getCompatibleFile(file, ext);
            boolean isExtended = extCB.isSelected();
            convert(file, ext, titleTf.getText(), isExtended);
         }
      }
   }

   private ScriptWrapper getScript(File file) throws Exception {
      wrapper = new ScriptWrapper(file);
      wrapper.createScript(this.getClass().getClassLoader());
      return wrapper;
   }

   private void convert(File file, String ext, String title, boolean isExtended) {
      try {
         if (wrapper != null) {
            Node node = wrapper.executeScript();
            if (node != null) {
               if (ext.equals("svg")) {
                  convertToSVG(node, file, title, isExtended);
               } else if (ext.equals("eps")) {
                  convertToEPS(node, title, file);
               } else if (ext.equals("ppt")) {
                  convertToPPT(node, file, isExtended);
               }
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void convertToSVG(Node node, File file, String title, boolean isExtended) throws Exception {
      SVGDriverAppUtils utils = new SVGDriverAppUtils();
      utils.convert(node, file, title, isExtended);
   }

   private void convertToEPS(Node node, String title, File file) throws Exception {
      EPSDriverAppUtils utils = new EPSDriverAppUtils();
      utils.convert(node, title, file);
   }

   private void convertToPPT(Node node, File file, boolean isExtended) throws Exception {
      PPTDriverAppUtils utils = new PPTDriverAppUtils();
      utils.convert(node, file, isExtended);
   }

   public static void main(String[] args) {
      JFXConverterApp app = new JFXConverterApp();
      app.setVisible(true);
   }
}
