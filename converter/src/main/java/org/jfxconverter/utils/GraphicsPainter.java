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
package org.jfxconverter.utils;

import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * A class which allows to paint in the Swing EDT thread.
 *
 * @version 0.1
 */
public class GraphicsPainter {
   private final Graphics2D g2d;
   private final JComponent comp2Paint;

   /**
    * Create a new Painter.
    *
    * @param comp the component
    * @param g the Graphics2D where to paint
    */
   public GraphicsPainter(JComponent comp, Graphics2D g) {
      this.g2d = g;
      this.comp2Paint = comp;
   }

   /**
    * Internal method to perform the Paint.
    */
   private void doPaint() {
      // double buffering should be disabled, else we can have artifacts when painting the Layer
      RepaintManager.currentManager(comp2Paint).setDoubleBufferingEnabled(false);
      comp2Paint.paint(g2d);
      // ensure that the state for double buffering and painting overlays is set back to the default
      RepaintManager.currentManager(comp2Paint).setDoubleBufferingEnabled(true);
   }

   /**
    * Paint the Component in the Graphics2D.
    *
    * @throws java.lang.Exception if an Exception occured during the Painting
    */
   public void paint() throws Exception {
      // If we are in the EDT, we must no call an invokeAndWait or we would have an Exception
      if (SwingUtilities.isEventDispatchThread()) {
         doPaint();
      } else {
         SwingUtilities.invokeAndWait(() -> {
            doPaint();
         });
      }
   }
}
