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
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.drivers.svg.ConvertorSVGGraphics2D;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.swing.ExtensionFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.io.*;

/**
 * The abstract class for SVG conversion samples.
 *
 * @since 0.1
 */
public abstract class AbstractSVGSample extends Application {
   public static final int TRANSCODER_ERROR_BASE = 0xff00;
   public static final int ERROR_NULL_INPUT = TRANSCODER_ERROR_BASE + 0;
   public static final int ERROR_INCOMPATIBLE_INPUT_TYPE = TRANSCODER_ERROR_BASE + 1;
   public static final int ERROR_INCOMPATIBLE_OUTPUT_TYPE = TRANSCODER_ERROR_BASE + 2;
   private SVGGraphics2D g2D = null;

   @Override
   public void start(Stage primaryStage) {
      StackPane root = new StackPane();
      Node node = getNode();
      root.getChildren().add(node);

      Dimension2D dim = getDimension();
      Scene scene = new Scene(root, dim.getWidth(), dim.getHeight());

      primaryStage.setTitle("Sample");
      primaryStage.setScene(scene);
      primaryStage.show();

      createDocument(node);
   }

   protected Dimension2D getDimension() {
      return new Dimension2D(300, 250);
   }

   protected abstract Node getNode();

   private void createDocument(Node node) {
      try {
         File file = getFile();
         if (file != null) {
            Document doc = SVGDOMImplementation.getDOMImplementation()
               .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
            Writer writer = new BufferedWriter(new FileWriter(file));
            TranscoderOutput output = new TranscoderOutput(writer);
            Bounds bounds = node.getBoundsInLocal();
            Rectangle2D rec = new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

            g2D = new ConvertorSVGGraphics2D(doc);
            JFXConverter converter = new JFXConverter();
            converter.convert(g2D, node);

            finishTranscoding(rec, output);
            writer.flush();
            writer.close();
         }
      } catch (Exception e) {
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
