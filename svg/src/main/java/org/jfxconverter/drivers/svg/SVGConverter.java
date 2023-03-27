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
package org.jfxconverter.drivers.svg;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.converters.DefaultConverterListener;
import org.jfxconverter.utils.Utilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.file.Files;

/**
 * An utility class to convert Nodes to SVG content.
 *
 * @version 0.23
 */
public class SVGConverter {
   private SVGGraphics2D g2D = null;
   private Color background = null;

   /**
    * Convert a Node in a SVG file, with a default title.
    *
    * @param node the Node
    * @param file the SVG file
    * @throws Exception
    */
   public void convert(Node node, File file) throws Exception {
      convert(node, file, null, true);
   }

   public void convert(Node node, BufferedWriter out) throws Exception {
      convert(node, out, null, true);
   }

   /**
    * Convert a Node in a SVG file
    *
    * @param node the Node
    * @param title the document
    * @param file the SVG file
    * @throws Exception
    */
   public void convert(Node node, String title, File file) throws Exception {
      convert(node, file, title, true);
   }

   /**
    * Convert a Node in a SVG file, with a default title.
    *
    * @param node the Node
    * @param file the SVG file
    * @param isExtended true for an extended conversion
    * @throws Exception
    */
   public void convert(Node node, File file, boolean isExtended) throws Exception {
      convert(node, file, null, isExtended);
   }

   /**
    * Convert a Node in a SVG file.
    *
    * @param node the Node
    * @param file the SVG file
    * @param title the document title
    * @param isExtended true for an extended conversion
    * @throws Exception
    */
   public void convert(Node node, File file, String title, boolean isExtended) throws Exception {
      // make sure that the JavaFX Platform is initialized, the Panel is not used
      new JFXPanel();
      createSVGDocument(node, file, title, isExtended);
   }

   public void convert(Node node, BufferedWriter out, String title, boolean isExtended) throws Exception {
      // make sure that the JavaFX Platform is initialized, the Panel is not used
      new JFXPanel();
      createSVGDocument(node, out, title, isExtended);
   }

   /**
    * Set the background color used for the conversion.
    *
    * @param background the background
    */
   public void setBackground(Color background) {
      this.background = background;
   }

   /**
    * Return the background color used for the conversion
    *
    * @return the background
    */
   public Color getBackground() {
      return background;
   }

   /**
    * Creates the Document corresponding to the Node, and write it to the output.
    *
    * @param node the Node
    * @param isExtended true for an extended conversion
    */
   private void createSVGDocument(Node node, File file, String title, boolean isExtended) throws IOException {
      if (file == null) {
         throw new IllegalArgumentException("File is null");
      }
      try(BufferedWriter bw = Files.newBufferedWriter(file.toPath())) {
         createSVGDocument(node, bw, title, isExtended);
      }
   }

   private void createSVGDocument(Node node, BufferedWriter out, String title, boolean isExtended) throws IOException {
      try {
         Document doc = SVGDOMImplementation.getDOMImplementation().createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

         TranscoderOutput output = new TranscoderOutput(out);
         Rectangle2D rec = Utilities.getBounds(node);

         JFXConverter converter = new JFXConverter();
         converter.setBackground(background);
         if (isExtended) {
            g2D = new ConvertorJFXSVGGraphics2D(doc);
            converter.setListener(new DefaultConverterListener());
         } else {
            g2D = new ConvertorSVGGraphics2D(doc);
         }

         converter.convert(g2D, node);

         finishTranscoding(rec, title, output);
         out.flush();
      } catch (DOMException | TranscoderException e) {
         throw new IOException("Failed to convert jfx node to svg", e);
      }
   }

   /**
    * Finish the transcoding.
    */
   private void finishTranscoding(Rectangle2D rec, String title, TranscoderOutput output) throws TranscoderException {
      // get the root element and add size
      String minX = Double.toString(rec.getMinX());
      String minY = Double.toString(rec.getMinY());
      String width = Double.toString(rec.getWidth());
      String height = Double.toString(rec.getHeight());
      String size = minX + " " + minY + " " + width + " " + height;
      if (title == null) {
         title = "The SVG Document";
      }

      Element svgRoot = g2D.getRoot();
      svgRoot.setAttributeNS(null, "viewBox", size);
      Element titleNode = g2D.getDOMFactory().createElement("title");
      Text textNode = g2D.getDOMFactory().createTextNode(title);
      titleNode.appendChild(textNode);
      org.w3c.dom.Node firstChild = svgRoot.getFirstChild();
      svgRoot.insertBefore(titleNode, firstChild);

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
   private void writeSVGToOutput(Element svgRoot, TranscoderOutput output) throws TranscoderException {
      try {
         // Writer
         Writer wr = output.getWriter();
         if (wr != null) {
            g2D.stream(svgRoot, wr);
         }
      } catch (IOException e) {
         throw new TranscoderException(e);
      }
   }
}
