/*
Copyright (c) 2016, 2017, 2018 Herve Girod
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

import org.apache.batik.svggen.DefaultExtensionHandler;
import org.apache.batik.svggen.SVGColor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPaintDescriptor;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import static org.apache.batik.util.SVGConstants.*;

/**
 * This SVG extension handler is able to handle color gradients.
 *
 * @version 0.21
 */
public class SVGExtensionHandler extends DefaultExtensionHandler {
   @Override
   public SVGPaintDescriptor handlePaint(Paint paint, SVGGeneratorContext genCtx) {
      // Handle LinearGradientPaint
      if (paint instanceof LinearGradientPaint) {
         return getLgpDescriptor((LinearGradientPaint) paint, genCtx);
      }

      // Handle RadialGradientPaint
      if (paint instanceof RadialGradientPaint) {
         return getRgpDescriptor((RadialGradientPaint) paint, genCtx);
      }

      return super.handlePaint(paint, genCtx);
   }

   private SVGPaintDescriptor getRgpDescriptor(RadialGradientPaint gradient, SVGGeneratorContext genCtx) {
      Element gradElem = genCtx.getDOMFactory().createElementNS(SVG_NAMESPACE_URI, SVG_RADIAL_GRADIENT_TAG);

      // Create and set unique XML id
      String id = genCtx.getIDGenerator().generateID("gradient");
      gradElem.setAttribute(SVG_ID_ATTRIBUTE, id);

      // Set x,y pairs
      Point2D centerPt = gradient.getCenterPoint();
      gradElem.setAttribute("cx", String.valueOf(centerPt.getX()));
      gradElem.setAttribute("cy", String.valueOf(centerPt.getY()));

      Point2D focusPt = gradient.getFocusPoint();
      gradElem.setAttribute("fx", String.valueOf(focusPt.getX()));
      gradElem.setAttribute("fy", String.valueOf(focusPt.getY()));

      gradElem.setAttribute("r", String.valueOf(gradient.getRadius()));

      addMgpAttributes(gradElem, genCtx, gradient);

      return new SVGPaintDescriptor("url(#" + id + ")", SVG_OPAQUE_VALUE, gradElem);
   }

   private SVGPaintDescriptor getLgpDescriptor(LinearGradientPaint gradient, SVGGeneratorContext genCtx) {
      Element gradElem = genCtx.getDOMFactory().createElementNS(SVG_NAMESPACE_URI, SVG_LINEAR_GRADIENT_TAG);

      // Create and set unique XML id
      String id = genCtx.getIDGenerator().generateID("gradient");
      gradElem.setAttribute(SVG_ID_ATTRIBUTE, id);

      // Set x,y pairs
      Point2D startPt = gradient.getStartPoint();
      gradElem.setAttribute("x1", String.valueOf(startPt.getX()));
      gradElem.setAttribute("y1", String.valueOf(startPt.getY()));

      Point2D endPt = gradient.getEndPoint();
      gradElem.setAttribute("x2", String.valueOf(endPt.getX()));
      gradElem.setAttribute("y2", String.valueOf(endPt.getY()));

      addMgpAttributes(gradElem, genCtx, gradient);

      return new SVGPaintDescriptor("url(#" + id + ")", SVG_OPAQUE_VALUE, gradElem);
   }

   private void addMgpAttributes(Element gradElem, SVGGeneratorContext genCtx, MultipleGradientPaint gradient) {
      gradElem.setAttribute(SVG_GRADIENT_UNITS_ATTRIBUTE, SVG_USER_SPACE_ON_USE_VALUE);

      // Set cycle method
      switch (gradient.getCycleMethod()) {
         case REFLECT:
            gradElem.setAttribute(SVG_SPREAD_METHOD_ATTRIBUTE, SVG_REFLECT_VALUE);
            break;
         case REPEAT:
            gradElem.setAttribute(SVG_SPREAD_METHOD_ATTRIBUTE, SVG_REPEAT_VALUE);
            break;
         case NO_CYCLE:
            gradElem.setAttribute(SVG_SPREAD_METHOD_ATTRIBUTE, SVG_PAD_VALUE);
            break;
      }

      // Set color space
      switch (gradient.getColorSpace()) {
         case LINEAR_RGB:
            gradElem.setAttribute(SVG_COLOR_INTERPOLATION_ATTRIBUTE, SVG_LINEAR_RGB_VALUE);
            break;

         case SRGB:
            gradElem.setAttribute(SVG_COLOR_INTERPOLATION_ATTRIBUTE, SVG_SRGB_VALUE);
            break;
      }

      // Set transform matrix if not identity
      AffineTransform tf = gradient.getTransform();
      if (!tf.isIdentity()) {
         String matrix = "matrix("
            + tf.getScaleX() + " " + tf.getShearX() + " " + tf.getTranslateX() + " "
            + tf.getScaleY() + " " + tf.getShearY() + " " + tf.getTranslateY() + ")";
         gradElem.setAttribute(SVG_TRANSFORM_ATTRIBUTE, matrix);
      }

      // Convert gradient stops
      Color[] colors = gradient.getColors();
      float[] fracs = gradient.getFractions();

      for (int i = 0; i < colors.length; i++) {
         Element stop = genCtx.getDOMFactory().createElementNS(SVG_NAMESPACE_URI, SVG_STOP_TAG);
         SVGPaintDescriptor pd = SVGColor.toSVG(colors[i], genCtx);

         stop.setAttribute(SVG_OFFSET_ATTRIBUTE, (int) (fracs[i] * 100.0f) + "%");
         stop.setAttribute(SVG_STOP_COLOR_ATTRIBUTE, pd.getPaintValue());

         if (colors[i].getAlpha() != 255) {
            stop.setAttribute(SVG_STOP_OPACITY_ATTRIBUTE, pd.getOpacityValue());
         }

         gradElem.appendChild(stop);
      }
   }
}
