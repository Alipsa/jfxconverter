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
package org.tools.jfxconverter.samples.svg;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.drivers.svg.ConvertorSVGGraphics2D;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.swing.ExtensionFileFilter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.io.*;

/**
 * An example class for SVG conversion.
 *
 * @version 0.9
 */
public class FirstSVGSample extends Application {
   public static final int TRANSCODER_ERROR_BASE = 0xff00;
   public static final int ERROR_NULL_INPUT = TRANSCODER_ERROR_BASE + 0;
   public static final int ERROR_INCOMPATIBLE_INPUT_TYPE = TRANSCODER_ERROR_BASE + 1;
   public static final int ERROR_INCOMPATIBLE_OUTPUT_TYPE = TRANSCODER_ERROR_BASE + 2;
   private SVGGraphics2D g2D = null;

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

      primaryStage.setTitle("Sample");
      primaryStage.setScene(scene);
      primaryStage.show();

      createDocument(node);
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
      line.setStroke(Color.RED);
      pane.getChildren().add(line);
      pane.getChildren().add(button);
      return pane;
   }

   /**
    * Creates the Document corresponding to the Node, and write it to the output.
    *
    * @param node the Node
    */
   private void createDocument(Node node) {
      try {
         File file = getFile();
         if (file != null) {
            Document doc = SVGDOMImplementation.getDOMImplementation()
               .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
               TranscoderOutput output = new TranscoderOutput(writer);
               Bounds bounds = node.getBoundsInLocal();
               Rectangle2D rec = new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

               g2D = new ConvertorSVGGraphics2D(doc);
               JFXConverter converter = new JFXConverter();
               converter.convert(g2D, node);

               finishTranscoding(rec, output);
               writer.flush();
            }
         }
      } catch (DOMException | IOException | TranscoderException e) {
         e.printStackTrace();
      }
   }

   /**
    * Finish the transcoding.
    */
   private void finishTranscoding(Rectangle2D rec, TranscoderOutput output)
      throws TranscoderException {

      // get the root element and add size
      String minX = Double.toString(rec.getMinX());
      String minY = Double.toString(rec.getMinY());
      String width = Double.toString(rec.getWidth());
      String height = Double.toString(rec.getHeight());
      String size = minX + " " + minY + " " + width + " " + height;

      Element svgRoot = g2D.getRoot();
      svgRoot.setAttributeNS(null, "viewBox", size);

      //testOutput(doc, new File(System.getProperty("user.dir"),"test.xml"));
      // Now, write the SVG content to the output
      writeSVGToOutput(svgRoot, output);
   }

   /**
    * Writes the SVG content.
    *
    * @param svgRoot the root of the SVG Document
    * @param output the output
    */
   protected void writeSVGToOutput(Element svgRoot, TranscoderOutput output) throws TranscoderException {
      try {
         // Writer
         Writer wr = output.getWriter();
         if (wr != null) {
            g2D.stream(svgRoot, wr);
            return;
         }
      } catch (IOException e) {
         throw new TranscoderException(e);
      }
   }

   protected File getFile() {
      File dir = new File(System.getProperty("user.dir"));
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(dir);
      chooser.setDialogTitle("Select SVG File to create");
      String[] ext = { "svg" };
      ExtensionFileFilter pptfilter = new ExtensionFileFilter(ext, "SVG Files");
      chooser.addChoosableFileFilter(pptfilter);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      File file = null;
      if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
         file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "svg");
      }
      return file;
   }
}
