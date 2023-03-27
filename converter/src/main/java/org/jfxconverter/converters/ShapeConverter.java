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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import javafx.collections.ObservableList;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.jfxconverter.utils.CSSProperties;
import org.jfxconverter.utils.JFXShapeUtilities;
import org.jfxconverter.utils.Utilities;

/**
 * A converter which convert Shapes.
 *
 * @version 0.12
 */
public class ShapeConverter extends AbstractConverter {
   private Shape shape = null;

   public ShapeConverter(ConverterDelegate converter, Shape shape) {
      super(converter, shape);
      this.shape = shape;
   }

   private boolean hasStrokeProperty() {
      return properties.containsKey(STROKE_WIDTH) || properties.containsKey(STROKE_LINECAP) || properties.containsKey(STROKE_LINEJOIN)
         || properties.containsKey(STROKE_MITERLIMIT) || properties.containsKey(STROKE_DASHARRAY) || properties.containsKey(STROKE_DASHOFFSET);
   }

   private boolean hasFontProperty() {
      return properties.containsKey(FONT_FAMILY) || properties.containsKey(FONT_SIZE) || properties.containsKey(FONT_STYLE)
         || properties.containsKey(FONT_WEIGHT);
   }

   /**
    * Return the Arc Width of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Width
    * @see org.jfxconverter.utils.CSSProperties#ARC_WIDTH
    */
   private double getArcWidth(Rectangle rec) {
      if (properties.containsKey(ARC_WIDTH)) {
         double arcWidth = (Double) properties.get(ARC_WIDTH);
         return arcWidth;
      } else {
         return rec.getArcWidth();
      }
   }

   /**
    * Return the Arc Height of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Height
    * @see org.jfxconverter.utils.CSSProperties#ARC_HEIGHT
    */
   private double getArcHeight(Rectangle rec) {
      if (properties.containsKey(ARC_HEIGHT)) {
         double arcWidth = (Double) properties.get(ARC_HEIGHT);
         return arcWidth;
      } else {
         return rec.getArcWidth();
      }
   }

   private javafx.scene.text.Font getFont(Text text) {
      if (properties.containsKey(FONT)) {
         javafx.scene.text.Font font = (javafx.scene.text.Font) properties.get(FONT);
         if (font != javafx.scene.text.Font.getDefault()) {
            return font;
         }
      }
      if (hasFontProperty()) {
         javafx.scene.text.Font font = text.getFont();
         double size = font.getSize();
         String family = font.getFamily();
         FontWeight weight = Utilities.getWeight(font);
         FontPosture posture = Utilities.getPosture(font);
         font = javafx.scene.text.Font.font(family, weight, posture, size);
         return font;
      } else {
         return text.getFont();
      }
   }

   private int getLineCap(StrokeLineCap lineCap) {
      int cap = BasicStroke.CAP_ROUND;
      if (lineCap == StrokeLineCap.SQUARE) {
         cap = BasicStroke.CAP_SQUARE;
      } else if (lineCap == StrokeLineCap.BUTT) {
         cap = BasicStroke.CAP_BUTT;
      }
      return cap;
   }

   private int getLineJoin(StrokeLineJoin lineJoin) {
      int join = BasicStroke.JOIN_BEVEL;
      if (lineJoin == StrokeLineJoin.MITER) {
         join = BasicStroke.JOIN_MITER;
      } else if (lineJoin == StrokeLineJoin.ROUND) {
         join = BasicStroke.JOIN_ROUND;
      }
      return join;
   }

   private Stroke getStroke() {
      double width = shape.getStrokeWidth();
      ObservableList<Double> dashArray = shape.getStrokeDashArray();
      double offset = shape.getStrokeDashOffset();
      StrokeLineCap lineCap = shape.getStrokeLineCap();
      int cap = getLineCap(lineCap);
      StrokeLineJoin lineJoin = shape.getStrokeLineJoin();
      int join = getLineJoin(lineJoin);
      double miterLimit = shape.getStrokeMiterLimit();
      if (hasStrokeProperty()) {
         if (properties.containsKey(STROKE_WIDTH)) {
            width = (double) properties.get(STROKE_WIDTH);
         }
         if (properties.containsKey(STROKE_LINECAP)) {
            lineCap = (StrokeLineCap) properties.get(STROKE_LINECAP);
            cap = getLineCap(lineCap);
         }
         if (properties.containsKey(STROKE_LINEJOIN)) {
            lineJoin = (StrokeLineJoin) properties.get(STROKE_LINEJOIN);
            cap = getLineJoin(lineJoin);
         }
         if (properties.containsKey(STROKE_MITERLIMIT)) {
            miterLimit = (double) properties.get(STROKE_MITERLIMIT);
         }
         if (properties.containsKey(STROKE_DASHOFFSET)) {
            offset = (double) properties.get(STROKE_DASHOFFSET);
         }
         if (properties.containsKey(STROKE_DASHARRAY)) {
            Object array = properties.get(STROKE_DASHARRAY);
            if (array instanceof ObservableList) {
               dashArray = (ObservableList<Double>) array;
            }
         }
         if (dashArray.isEmpty()) {
            BasicStroke stroke = new BasicStroke((float) width, cap, join, (float) miterLimit);
            return stroke;
         } else {
            float[] dash = new float[dashArray.size()];
            for (int i = 0; i < dashArray.size(); i++) {
               dash[i] = dashArray.get(i).floatValue();
            }
            BasicStroke stroke = new BasicStroke((float) width, cap, join, (float) miterLimit, dash, (float) offset);
            return stroke;
         }
      } else if (dashArray.isEmpty()) {
         BasicStroke stroke = new BasicStroke((float) width, cap, join, (float) miterLimit);
         return stroke;
      } else {
         float[] dash = new float[dashArray.size()];
         for (int i = 0; i < dashArray.size(); i++) {
            dash[i] = dashArray.get(i).floatValue();
         }
         BasicStroke stroke = new BasicStroke((float) width, cap, join, (float) miterLimit, dash, (float) offset);
         return stroke;
      }
   }

   /**
    * Return the Awt Paint corresponing to the JavaFX Stroke.
    *
    * @return the Awt Paint
    * @see CSSProperties#STROKE_PAINT
    */
   private Paint getStrokePaint() {
      double opacity = this.getOpacityStroke();
      if (opacity <= 0) {
         return null;
      }
      if (properties.containsKey(STROKE_PAINT)) {
         javafx.scene.paint.Paint paint = (javafx.scene.paint.Paint) properties.get(STROKE_PAINT);
         Paint awtPaint = Utilities.getAWTPaint(shape, paint, opacity);
         return awtPaint;
      } else {
         // we must use the strokeProperty() and to the getFill() method, because getStroke() will return a Color.BLACK for a null stroke
         Paint paint = Utilities.getAWTPaint(shape, shape.strokeProperty().get(), opacity);
         return paint;
      }
   }

   /**
    * Return the Awt Paint corresponing to the JavaFX Text fill.
    *
    * @return the Awt Paint
    * @see CSSProperties#TEXT_FILL
    */
   private Paint getTextFill() {
      double opacity = this.getOpacity();
      if (properties.containsKey(TEXT_FILL)) {
         javafx.scene.paint.Paint paint = (javafx.scene.paint.Paint) properties.get(TEXT_FILL);
         Paint awtPaint = Utilities.getAWTPaint(shape, paint, opacity);
         return awtPaint;
      } else {
         // we must use the fillProperty() and to the getFill() method, because getFill() will return a Color.BLACK for a null fill
         Paint paint = Utilities.getAWTPaint(shape, shape.fillProperty().get(), opacity);
         return paint;
      }
   }

   /**
    * Return the opacity of the Node Fill. The way it work for a Shape is slightly different from a Region, because
    * contrary to a Region, a Shape can set its own Stroke and Fill. Hence foe example if the Fill of the Shape is
    * transparent or null, the opacity will be considered 0 even if the opacity value in the CSS is not 0.
    *
    * @return the opacity
    * @see org.jfxconverter.utils.CSSProperties#OPACITY
    */
   protected double getOpacityFill() {
      if (properties.containsKey(OPACITY)) {
         Number opacity = (Number) properties.get(OPACITY);
         return opacity.doubleValue();
      } else {
         double opacity = shape.getOpacity();
         javafx.scene.paint.Paint paint = shape.getFill();
         if (paint == null) {
            opacity = 0;
         } else if (paint instanceof javafx.scene.paint.Color) {
            javafx.scene.paint.Color color = (javafx.scene.paint.Color) paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }
         return opacity;
      }
   }

   /**
    * Return the opacity of the Node Stroke.
    *
    * @return the opacity
    */
   protected double getOpacityStroke() {
      if (properties.containsKey(OPACITY)) {
         Number opacity = (Number) properties.get(OPACITY);
         return opacity.doubleValue();
      } else {
         double opacity = shape.getOpacity();
         javafx.scene.paint.Paint paint = shape.getStroke();
         if (paint == null) {
            opacity = 0;
         } else if (paint instanceof javafx.scene.paint.Color) {
            javafx.scene.paint.Color color = (javafx.scene.paint.Color) paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }
         return opacity;
      }
   }

   /**
    * Return the Awt Paint corresponing to the JavaFX fill.
    *
    * @return the Awt Paint
    */
   private Paint getFillPaint() {
      double opacity = this.getOpacityFill();
      if (opacity <= 0) {
         return null;
      }
      if (properties.containsKey(FILL_PAINT)) {
         javafx.scene.paint.Paint paint = (javafx.scene.paint.Paint) properties.get(FILL_PAINT);
         Paint awtPaint = Utilities.getAWTPaint(shape, paint, opacity);
         return awtPaint;
      } else {
         // we must use the fillProperty() and to the getFill() method, because getFill() will return a Color.BLACK for a null fill
         Paint paint = Utilities.getAWTPaint(shape, shape.fillProperty().get(), opacity);
         return paint;
      }
   }

   /**
    * Convert the Shape. The currently suppported Shape classes are:
    * <ul>
    * <li>Line</li>
    * <li>Circle</li>
    * <li>Ellipse</li>
    * <li>Arc</li>
    * <li>Rectangle</li>
    * <li>Path</li>
    * <li>Polygon</li>
    * <li>Polyline</li>
    * <li>Text</li>
    * <li>QuadCurve</li>
    * <li>CubicCurve</li>
    * </ul>
    */
   @Override
   public void convert() {
      Graphics2D g2D = converter.getGraphics2D();
      if (shape instanceof Line) {
         Line line = (Line) shape;
         Paint paint = getStrokePaint();
         converter.setPaint(paint);
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         if (paint != null) {
            g2D.drawLine((int) line.getStartX(), (int) line.getStartY(), (int) line.getEndX(), (int) line.getEndY());
         }
      } else if (shape instanceof Text) {
         Text text = (Text) shape;
         Paint paint = getTextFill();
         converter.setPaint(paint);
         Font font = Utilities.getAWTFont(this.getFont(text));
         converter.setFont(font);
         String str = text.getText();
         if (!str.isEmpty() && paint != null) {
            double x = text.getX();
            double y = text.getY();
            g2D.drawString(str, (int) x, (int) y);
         }
      } else if (shape instanceof Polygon) {
         Polygon polygon = (Polygon) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(polygon);
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fill(awtShape);
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof Polyline) {
         Polyline polyline = (Polyline) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(polyline);
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fill(awtShape);
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof SVGPath) {
         SVGPath path = (SVGPath) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(path);
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fill(awtShape);
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof Path) {
         Path path = (Path) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(path);
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fill(awtShape);
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof Circle) {
         Circle circle = (Circle) shape;
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fillOval((int) (circle.getCenterX() - circle.getRadius()), (int) (circle.getCenterY() - circle.getRadius()),
               (int) (circle.getRadius() * 2), (int) (circle.getRadius() * 2));
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.drawOval((int) (circle.getCenterX() - circle.getRadius()), (int) (circle.getCenterY() - circle.getRadius()),
               (int) (circle.getRadius() * 2), (int) (circle.getRadius() * 2));
         }
      } else if (shape instanceof Ellipse) {
         Ellipse ellipse = (Ellipse) shape;
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fillOval((int) (ellipse.getCenterX() - ellipse.getRadiusX()), (int) (ellipse.getCenterY() - ellipse.getRadiusY()),
               (int) (ellipse.getRadiusX() * 2), (int) (ellipse.getRadiusY() * 2));
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.drawOval((int) (ellipse.getCenterX() - ellipse.getRadiusX()), (int) (ellipse.getCenterY() - ellipse.getRadiusY()),
               (int) (ellipse.getRadiusX() * 2), (int) (ellipse.getRadiusY() * 2));
         }
      } else if (shape instanceof Arc) {
         Arc arc = (Arc) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(arc);
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.fill(awtShape);
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof Rectangle) {
         Rectangle rec = (Rectangle) shape;
         double arcWidth = getArcWidth(rec);
         double arcHeight = getArcHeight(rec);
         boolean isRoundRect = arcWidth != 0 || arcHeight != 0;
         Paint paint = getFillPaint();
         converter.setPaint(paint);
         if (paint != null) {
            if (isRoundRect) {
               g2D.fillRoundRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight(), (int) arcWidth,
                  (int) arcHeight);
            } else {
               g2D.fillRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight());
            }
         }
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            if (isRoundRect) {
               g2D.drawRoundRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight(), (int) arcWidth,
                  (int) arcHeight);
            } else {
               g2D.drawRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight());
            }
         }
      } else if (shape instanceof QuadCurve) {
         QuadCurve curve = (QuadCurve) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(curve);
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         Paint paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      } else if (shape instanceof CubicCurve) {
         CubicCurve curve = (CubicCurve) shape;
         java.awt.Shape awtShape = JFXShapeUtilities.getAWTPath(curve);
         Stroke stroke = getStroke();
         converter.setStroke(stroke);
         Paint paint = getStrokePaint();
         converter.setPaint(paint);
         if (paint != null) {
            g2D.draw(awtShape);
         }
      }
   }
}
