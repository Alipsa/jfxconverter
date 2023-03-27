/*
Copyright (c) 2020, Herve Girod
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMRectElement;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests for the SVGGraphics2D class.
 *
 * @since 0.22
 */
public class SVGDriver3Test {

   /**
    * Test of converting a Line.
    */
   @Test
   public void testConvertNode() throws Exception {
      System.out.println("SVGDriver3Test : testConvertNode");
      Line line = new Line(0, 0, 200, 200);
      line.setStroke(Color.RED);

      new JFXPanel();
      SVGConverter utils = new SVGConverter();
      utils.setBackground(java.awt.Color.BLACK);
      File file = File.createTempFile("jfxconverter", ".svg");
      utils.convert(line, file);
      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
      Document doc = factory.createDocument(file.toURI().toString());
      assertNotNull(doc, "Document must not be null");
      Element elt = doc.getDocumentElement();
      Node child = elt.getLastChild();
      child = child.getFirstChild();
      Node rect = child.getFirstChild();
      assertTrue(rect instanceof SVGOMRectElement, "Node must be a SVGOMRectElement");

      if(!file.delete()) file.deleteOnExit();
   }
}
