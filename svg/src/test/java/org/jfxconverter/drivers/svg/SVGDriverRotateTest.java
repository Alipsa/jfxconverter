/*
Copyright (c) 2018, Herve Girod
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

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests for the SVGDriver class.
 *
 * @since 0.20
 */
public class SVGDriverRotateTest {

   /**
    * Test of converting a Line.
    */
   @Test
   public void testConvertRectangleAndLine() throws Exception {
      System.out.println("SVGDriverRotateTest : testConvertRectangleAndLine");
      Pane pane = new Pane();
      Rectangle rect = new Rectangle(0, 0, 200, 200);
      rect.setFill(Color.YELLOW);
      rect.setStyle("-fx-fill: green; -fx-rotate: 45;");
      Line line = new Line(0, 0, 200, 200);
      line.setStroke(Color.RED);
      pane.getChildren().add(rect);
      pane.getChildren().add(line);

      new JFXPanel();
      SVGConverter utils = new SVGConverter();
      File file = File.createTempFile("jfxconverter", ".svg");
      utils.convert(pane, file);

      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
      Document doc = factory.createDocument(file.toURI().toString());
      assertNotNull(doc, "Document must not be null");
      Element elt = doc.getDocumentElement();
      Node child = elt.getLastChild();
      Node g = child.getLastChild();
      String fill = g.getAttributes().getNamedItem("fill").getNodeValue();
      assertEquals("Fill", "red", fill);

      g = child.getFirstChild();
      fill = g.getAttributes().getNamedItem("fill").getNodeValue();
      assertEquals("Fill", "green", fill);
      fill = g.getAttributes().getNamedItem("stroke").getNodeValue();
      assertEquals("Stroke", "green", fill);
      String matrix = g.getAttributes().getNamedItem("transform").getNodeValue();
      assertEquals("Rectangle node transform", "matrix(0.7071,0.7071,-0.7071,0.7071,100,-41.4214)", matrix);

      Node rectNode = g.getFirstChild();
      String x = rectNode.getAttributes().getNamedItem("x").getNodeValue();
      assertEquals("Rectangle node x", "0", x);
      String y = rectNode.getAttributes().getNamedItem("y").getNodeValue();
      assertEquals("Rectangle node y", "0", y);
      String width = rectNode.getAttributes().getNamedItem("width").getNodeValue();
      assertEquals("Rectangle node width", "200", width);
      String height = rectNode.getAttributes().getNamedItem("height").getNodeValue();
      assertEquals("Rectangle node height", "200", height);

      if(!file.delete()) file.deleteOnExit();
   }
}
