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
package org.jfxconverter.drivers.eps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import net.sf.epsgraphics.ColorMode;
import org.jfxconverter.JFXConverter;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 * An utility class to convert Nodes to EPS content.
 *
 * @since 0.20
 */
public class EPSConverter {
   private File file = null;
   private ColorMode colorMode = null;
   private String title = null;
   private boolean setup = false;
   private Method finishMethod = null;

   public void convert(Node node, File file) throws Exception {
      convert(node, file, "The EPS file", ColorMode.COLOR_RGB);
   }
   public void convert(Node node, File file, String title, ColorMode colorMode) throws Exception {
      // make sure that the JavaFX Platform is initialized, the Panel is not used
      JFXPanel jfxPanel = new JFXPanel();
      this.title = title;
      this.file = file;
      this.colorMode = colorMode;

      if (!setup) {
         setUp();
      }

      createEPS(node);
   }

   private void setUp() {
      try {
         finishMethod = EpsGraphics2D.class.getDeclaredMethod("finish");
      } catch (NoSuchMethodException | SecurityException ex) {
      }
      setup = true;
   }

   /**
    * Creates the Slide corresponding to the Node.
    *
    * @param node the Node
    */
   private void createEPS(Node node) throws IOException {
      if (file != null) {
         Bounds bounds = node.getBoundsInLocal();
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            EpsGraphics2D g2d = new EpsGraphics2D(writer, title, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), colorMode);
            JFXConverter converter = new JFXConverter();
            converter.convert(g2d, node);
            finish(g2d, writer);
         }
      }
   }

   private void finish(EpsGraphics2D g2d, Writer writer) {
      if (finishMethod != null) {
         try {
            finishMethod.invoke(g2d, (Object[]) null);
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
         }
      } else {
         try {
            writer.flush();
         } catch (IOException ex) {
         }
      }
   }
}
