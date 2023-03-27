/*
Copyright (c) 2016, 2018 Herve Girod
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import org.jfxconverter.utils.Utilities;
import org.mdiutil.geom.ShapeUtilities;

/**
 * A converter which convert ImageViews.
 *
 * @version 0.20
 */
public class ImageViewConverter extends AbstractConverter {
   private ImageView view = null;

   /**
    * Constructor.
    *
    * @param converter the ConverterDelegate
    * @param view the ImageView
    */
   public ImageViewConverter(ConverterDelegate converter, ImageView view) {
      super(converter, view);
      this.view = view;
   }

   /**
    * Convert the ImageView.
    */
   @Override
   public void convert() {
      Image image = view.getImage();
      if (allProperties.containsKey(IMAGE)) {
         URL url = (URL) allProperties.get(IMAGE);
         if (url == null) {
            image = null;
         } else {
            image = new Image(url.toString());
         }
      }
      if (image != null) {
         Graphics2D g2D = converter.getGraphics2D();
         int x = (int) view.getX();
         int y = (int) view.getY();
         double width = image.getWidth();
         double height = image.getHeight();
         double fitWidth = view.getFitWidth();
         // this is just to avoid any exception
         if (fitWidth <= 0) {
            fitWidth = width;
         }
         double fitHeight = view.getFitHeight();
         // this is just to avoid any exception
         if (fitHeight <= 0) {
            fitHeight = height;
         }
         double dstWidth = fitWidth;
         double dstHeight = fitHeight;
         if (view.isPreserveRatio()) {
            if (fitHeight < fitWidth) {
               dstWidth = width / height * fitHeight;
            } else {
               dstHeight = height / width * fitWidth;
            }
         }
         BufferedImage awtImage = new BufferedImage((int) dstWidth, (int) dstHeight, BufferedImage.TYPE_INT_ARGB);
         java.awt.Image awtimage2 = SwingFXUtils.fromFXImage(image, awtImage);
         if (view.isDisabled()) {
            awtimage2 = Utilities.createDisabledImage(awtimage2);
         }
         awtimage2 = awtimage2.getScaledInstance((int) dstWidth, (int) dstHeight, java.awt.Image.SCALE_SMOOTH);
         Transform fromAncestorTransform = this.getTransformFromAncestor(view);
         AffineTransform awtTransform = this.getTransform(fromAncestorTransform);
         double angle = ShapeUtilities.getRotationAngle(awtTransform);
         if (angle != 0) {
            BufferedImage awtImage3 = new BufferedImage((int) dstWidth, (int) dstHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = awtImage3.createGraphics();

            AffineTransform at = new AffineTransform();
            at.translate(dstWidth / 2, dstHeight / 2);
            at.rotate(angle);
            at.scale(0.5, 0.5);
            at.translate(-dstWidth / 2, -dstHeight / 2);
            g2d.drawImage(awtimage2, at, null);
            awtimage2 = awtImage3;

         }
         g2D.drawImage(awtimage2, x, y, x + (int) dstWidth, y + (int) dstHeight, 0, 0, (int) dstWidth, (int) dstHeight, null);
      }
   }
}
