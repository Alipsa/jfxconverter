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
package org.jfxconverter.converters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;

/**
 * A converter which convert a Shape3D.
 *
 * @since 0.17
 */
public class Shape3DConverter extends AbstractConverter {
   private Shape3D shape = null;

   public Shape3DConverter(ConverterDelegate converter, Shape3D shape) {
      super(converter, shape);
      this.shape = shape;
   }

   /**
    * Return the Shape3D.
    *
    * @return the Shape3D
    */
   public Shape3D getShape() {
      return shape;
   }

   /**
    * Convert a Shape3D by serializing the Shape as an Image.
    */
   @Override
   public void convert() {
      SnapshotParameters params = new SnapshotParameters();
      Color transparent = new Color(0, 0, 0, 0);
      params.setFill(transparent);
      params.setDepthBuffer(true);
      params.setCamera(shape.getScene().getCamera());
      WritableImage wimage = shape.snapshot(params, null);
      BufferedImage image = new BufferedImage((int) wimage.getWidth(), (int) wimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
      image = SwingFXUtils.fromFXImage(wimage, image);

      Graphics2D g2D = converter.getGraphics2D();
      g2D.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
   }
}
