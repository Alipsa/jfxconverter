/*
Copyright (c) 2017, 2018 Herve Girod
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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.converters.DefaultConverterListener;

/**
 * An utility class to convert Nodes to PPT content.
 *
 * @version 0.21
 */
public class PPTConverter {
   private File file = null;

   /**
    * Convert a Node in a PPT file.
    *
    * @param node the Node
    * @param file the PPT file
    * @throws Exception
    */
   public void convert(Node node, File file) throws Exception {
      convert(node, file, false);
   }

   /**
    * Convert a Node in a PPT file.
    *
    * @param node the Node
    * @param file the PPT file
    * @param isExtended true for an extended conversion
    * @throws Exception
    */
   public void convert(Node node, File file, boolean isExtended) throws Exception {
      // make sure that the JavaFX Platform is initialized, the Panel is not used
      JFXPanel jfxPanel = new JFXPanel();
      this.file = file;

      createSlides(node, isExtended);
   }

   /**
    * Creates the Slide corresponding to the Node.
    *
    * @param node the Node
    * @param isExtended true for an extended conversion
    */
   private void createSlides(Node node, boolean isExtended) throws IOException {
      if (file != null) {
         Bounds bounds = node.getBoundsInLocal();
         HSLFSlideShow pptSlides = new HSLFSlideShow();
         HSLFSlide slide = pptSlides.createSlide();

         JFXConverter converter = new JFXConverter();
         PPTGraphics2D g2d;
         if (isExtended) {
            g2d = new PPTJFXGraphics2D(slide, (float) bounds.getWidth(), (float) bounds.getHeight(), Color.WHITE, Color.BLACK);
            converter.setListener(new DefaultConverterListener());
         } else {
            g2d = new PPTGraphics2D(slide, (float) bounds.getWidth(), (float) bounds.getHeight(), Color.WHITE, Color.BLACK);
         }
         converter.convert(g2d, node);

         try (FileOutputStream stream = new FileOutputStream(file)) {
            pptSlides.write(stream);
            stream.flush();
         }
      }
   }
}
