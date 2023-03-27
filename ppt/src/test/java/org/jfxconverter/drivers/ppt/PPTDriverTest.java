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
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.jfxconverter.drivers.ppt;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.junit.jupiter.api.*;

/**
 * Tests for the PPTGraphics2D class.
 *
 * @version 0.20
 */
public class PPTDriverTest {

   /**
    * Test of converting a Line.
    */
   @Test
   public void testConvertNode() throws Exception {
      System.out.println("PPTDriverTest : testConvertNode");
      Line line = new Line(0, 0, 200, 200);
      line.setStroke(Color.RED);

      PPTConverter utils = new PPTConverter();
      File file = File.createTempFile("jfxconverter", ".ppt");
      utils.convert(line, file);
      HSLFSlide slide = PPTParserUtils.getSlide(file);
      List<HSLFShape> shapes = slide.getShapes();
      assertEquals(2, shapes.size(), "Shape count");

      HSLFShape shape = shapes.get(0);
      assertTrue(shape instanceof HSLFAutoShape, "Shape must be an AutoShape");
      HSLFAutoShape pauto = (HSLFAutoShape) shape;

      shape = shapes.get(1);
      assertTrue(shape instanceof HSLFLine, "Shape must be a Line");
      HSLFLine pline = (HSLFLine) shape;
      java.awt.Color pcol = pline.getLineColor();
      assertEquals(java.awt.Color.RED, pcol, "Line Color");
      Rectangle2D rec = pline.getAnchor();
      assertEquals(new Rectangle2D.Float(49.75f, 24.75f, 200f, 200f), rec, "Line anchor");

      if (!file.delete()) file.deleteOnExit();
   }
}
