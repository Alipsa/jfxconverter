/*
Copyright (c) 2016, Herve Girod
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
package org.tools.jfxconverter.samples.ppt;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.drivers.ppt.PPTGraphics2D;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.swing.ExtensionFileFilter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * An example class for PPT conversion.
 *
 * @version 0.7
 */
public class FirstPPTSample extends Application {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      StackPane root = new StackPane();
      Node node = getNode();
      root.getChildren().add(node);

      Scene scene = new Scene(root, 300, 250);

      primaryStage.setTitle("FirstPPTSample");
      primaryStage.setScene(scene);
      primaryStage.show();

      createSlides(node);
   }

   /**
    * Creates the Node hierarchy to convert.
    *
    * @return the root of the Node hierarchy
    */
   protected Node getNode() {
      Pane pane = new Pane();
      pane.setPrefWidth(400);
      pane.setPrefHeight(400);
      Button button = new Button("PUSH");
      button.setPrefWidth(100);
      button.setPrefHeight(150);
      button.setLayoutX(50);
      button.setLayoutY(50);
      Line line = new Line(0, 0, 200, 200);
      line.setStroke(javafx.scene.paint.Color.RED);
      pane.getChildren().add(line);
      pane.getChildren().add(button);
      return pane;
   }

   /**
    * Creates the Slide corresponding to the Node.
    *
    * @param node the Node
    */
   private void createSlides(Node node) {
      try {
         File file = getFile();
         if (file != null) {
            Bounds dim = node.getBoundsInLocal();
            HSLFSlideShow pptSlides = new HSLFSlideShow();
            HSLFSlide slide = pptSlides.createSlide();

            PPTGraphics2D g2d = new PPTGraphics2D(slide, (float) dim.getWidth(), (float) dim.getHeight(), Color.WHITE, Color.BLACK);
            JFXConverter converter = new JFXConverter();
            converter.convert(g2d, node);

            FileOutputStream stream = new FileOutputStream(file);
            pptSlides.write(stream);
            stream.flush();
            stream.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Return a user-selected File.
    *
    * @return the File
    */
   protected File getFile() {
      File dir = new File(System.getProperty("user.dir"));
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(dir);
      chooser.setDialogTitle("Select PPT File to create");
      String[] ext = { "ppt" };
      ExtensionFileFilter pptfilter = new ExtensionFileFilter(ext, "PPT Files");
      chooser.addChoosableFileFilter(pptfilter);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      File file = null;
      if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
         file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "ppt");
      }
      return file;
   }
}
