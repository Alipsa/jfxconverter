/*
Copyright (c) 2016, 2020 Herve Girod
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
package org.jfxconverter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javafx.scene.Node;
import org.jfxconverter.converters.ConverterDelegate;
import org.jfxconverter.converters.ConverterListener;
import org.jfxconverter.utils.Utilities;

/**
 * The JFXConverter class allows to convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
 *
 * <h1>Usage</h1>
 * <pre>
 * JFXConverter converter = new JFXConverter();
 * converter.convert(node, graphics2d);
 * </pre>
 *
 * <h1>Configuration</h1>
 * The configuration of the conversion is controlled by the {@link org.jfxconverter.conf.ConverterConfig} class.
 *
 * @version 0.22
 */
public class JFXConverter {
   private ConverterDelegate delegate = null;
   private Color background = null;

   public JFXConverter() {
      delegate = new ConverterDelegate();
   }

   /**
    * Set the background color used for the conversion.
    *
    * @param background the background color
    */
   public void setBackground(Color background) {
      this.background = background;
   }

   /**
    * Set the JavaFX background color used for the conversion.
    *
    * @param background the JavaFX background Color
    */
   public void setBackground(javafx.scene.paint.Color background) {
      this.background = Utilities.getAWTColor(background);
   }

   /**
    * Set the JavaFX background color used for the conversion.
    *
    * @param background the JavaFX background Color
    * @param opacity the opacity
    */
   public void setBackground(javafx.scene.paint.Color background, double opacity) {
      this.background = Utilities.getAWTColor(background, opacity);
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
    * Set the ConverterListener to use for the conversion. The listener will be called at the beginning and end of each converted Node.
    *
    * @param listener the ConverterListener
    */
   public void setListener(ConverterListener listener) {
      delegate.setListener(listener);
   }

   /**
    * Return the ConverterListener used for the conversion (may be null).
    *
    * @return the ConverterListener
    */
   public ConverterListener getListener() {
      return delegate.getListener();
   }

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   public Node getRoot() {
      return delegate.getRoot();
   }

   /**
    * Return the Graphics2D.
    *
    * @return the Graphics2D
    */
   public Graphics2D getGraphics2D() {
      return delegate.getGraphics2D();
   }

   /**
    * Return the ConverterDelegate.
    *
    * @return the ConverterDelegate
    */
   public ConverterDelegate getConverterDelegate() {
      return delegate;
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param g2D the Graphics2D
    * @param root the root Node
    */
   public void convert(Graphics2D g2D, Node root) {
      delegate.reset();

      if (background != null) {
         Rectangle2D rec = Utilities.getBounds(root);
         g2D.setBackground(background);
         g2D.fillRect(0, 0, (int) rec.getWidth(), (int) rec.getHeight());
      }
      delegate.convert(g2D, root);
   }
}
