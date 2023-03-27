/*
Copyright (c) 2017, 2018, 2020 Herve Girod
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
package org.jfxconverter.app.driversutils;

import java.awt.Color;
import java.io.File;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jfxconverter.drivers.svg.SVGConverter;
import org.jfxconverter.utils.JFXInvoker;

/**
 * An utility class to convert Nodes to SVG content.
 *
 * @version 0.23
 */
public class SVGDriverAppUtils extends Application {
   private Node node = null;
   private File file = null;
   private StackPane root = null;
   private Color background = null;

   protected Dimension2D getDimension() {
      return new Dimension2D(300, 250);
   }

   /**
    * Set the background color used for the conversion.
    *
    * @param background the background
    */
   public void setBackground(Color background) {
      this.background = background;
   }

   public void convert(Node node, String title, File file) throws Exception {
      convert(node, file, title, false);
   }

   public void convert(Node node, File file) throws Exception {
      convert(node, file, null, false);
   }

   public void convert(Node node, File file, boolean isExtended) throws Exception {
      convert(node, file, null, isExtended);
   }

   public void convert(Node node, File file, String title, boolean isExtended) throws Exception {
      this.node = node;
      this.file = file;

      JFXInvoker invoker = JFXInvoker.getInstance();
      invoker.invokeBlocking(new Runnable() {
         @Override
         public void run() {
            showStage();
         }
      });
      SVGConverter svgUtils = new SVGConverter();
      svgUtils.setBackground(background);
      svgUtils.convert(node, file, title, isExtended);
   }

   private void showStage() {
      root = new StackPane();
      Dimension2D dim = getDimension();
      Scene scene = new Scene(root, dim.getWidth(), dim.getHeight());
      Stage stage = new Stage();
      stage.setTitle("JavaFX Content");
      stage.setScene(scene);
      start(stage);
   }

   @Override
   public void start(Stage primaryStage) {
      root.getChildren().add(node);
      primaryStage.show();
   }
}
