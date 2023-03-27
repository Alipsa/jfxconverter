/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2011 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Map;

/**
 * This Graphics2D wraps another Graphics2D, filtering all graphics orders.
 * <p>
 * The <i>AffineTransform</i> set on this wrapper at initialization will wrap all the
 * transformations set on the underlying Graphics2D. For example, if you want to provide an
 * additional scale on all graphic orders, you can do the following :
 * <pre>
 *    AffineTransform tr = AffineTransform.getScaleInstance(scale, scale);
 *    Graphics2DWrapper wrapper = new Graphics2DWrapper(g2d, tr);
 * </pre>
 * or:
 * <pre>
 *    Graphics2DWrapper wrapper = new Graphics2DWrapper(g2d, scale);
 * </pre>
 * or even:
 * <pre>
 *    Graphics2DWrapper wrapper = new Graphics2DWrapper(g2d);
 *    wrapper.setScale(scale);
 * </pre>
 * <p>
 * The following properties can be set on this wrapper:</p>
 * <ul>
 * <li>{@link #filterCharacterIterators(boolean)}: if set to true, the
 * <i>AttributedCharacterIterator</i>s passed as parameters to the
 * {@link #drawString(AttributedCharacterIterator, float x, float y)} or
 * {@link #drawString(AttributedCharacterIterator, int x, int y)} methods will be transformed
 * as simple Strings before passing the <i>drawString</i> order to the underlying Graphics2D.
 * This is useful for Graphics2D that can't draw <i>AttributedCharacterIterator</i>s.>/li>
 * <li>{@link #inverseStringYPos(boolean)}: if set to true, each positioned String on the
 * underlying Graphics2D will be positioned at y + the ascent position of the current font.</li>
 * </ul>
 *
 * @version 0.8.6
 */
public class Graphics2DWrapper extends Graphics2D {
   /** The shapes are not clipped in the transcoding result.
    */
   public static final int NO_CLIPPING = 0;

   /**
    * The shapes are clipped in the transcoding result. The transcoded clipped shape is
    * a new shape that is cropped using the clipping shape. In other words, there are
    * no clipping shapes in the result.
    */
   public static final int SOFT_CLIPPING = 1;

   /**
    * The shapes are clipped in the transcoding result. The transcoded clipped shape is a clipped shape.
    */
   public static final int HARD_CLIPPING = 2;
   private Graphics2D g2d;
   private FontRenderContext ctx = new FontRenderContext(new AffineTransform(), false, false);
   private LineMetrics lm;
   private int clipProp = HARD_CLIPPING; // add HGIROD
   private double flatness;
   private boolean flatnessProp = false;
   private Shape deviceclip;
   private Float forceStroke = null;
   private float scale = 1.0f;
   private AffineTransform transform = null;
   private boolean filterCharIter = false;
   private boolean inverseStringYPos = false;

   /**
    * Create a new wrapper with no additional transformation.
    *
    * @param g2d the wrapped Graphics2D
    */
   public Graphics2DWrapper(Graphics2D g2d) {
      super();
      this.g2d = g2d;
      transform = new AffineTransform();
      g2d.setTransform(transform);
   }

   /**
    * Create a new wrapper with a scale transformation.
    *
    * @param g2d the wrapped Graphics2D
    * @param scale the scale value
    */
   public Graphics2DWrapper(Graphics2D g2d, float scale) {
      super();
      this.g2d = g2d;
      transform = AffineTransform.getScaleInstance((double) scale, (double) scale);
      g2d.setTransform(transform);
   }

   /**
    * Create a new wrapper with a transformation.
    *
    * @param g2d the wrapped Graphics2D
    * @param transform the transformation
    */
   public Graphics2DWrapper(Graphics2D g2d, AffineTransform transform) {
      super();
      this.g2d = g2d;
      this.transform = (AffineTransform) transform.clone();
      g2d.setTransform(transform);
   }

   /**
    * Return the wrapped Graphics2D.
    *
    * @return the wrapped Graphics2D
    */
   public Graphics2D getWrappedGraphics2D() {
      return g2d;
   }

   /**
    * If set to true, filter all {@link #drawString(AttributedCharacterIterator, float, float)}
    * and {@link #drawString(AttributedCharacterIterator, int, int)} methods in order to pass
    * only Strings to the underlying Graphics2D.
    */
   public void filterCharacterIterators(boolean filter) {
      this.filterCharIter = filter;
   }

   /** @return true if AttributedCharacterIterators are filtered.
    * @see #filterCharacterIterators(boolean)
    */
   public boolean isFilteringCharacterIterators() {
      return filterCharIter;
   }

   /**
    * If set to true, add to each Y String position the ascent value of the current font.
    *
    * @param inverse true if each Y String position the ascent value of the current font
    */
   public void inverseStringYPos(boolean inverse) {
      this.inverseStringYPos = inverse;
   }

   /**
    * Return true if Y String positions are inversed
    *
    * @return true if Y String positions are inversed.
    * @see #inverseStringYPos(boolean)
    */
   public boolean isInversingStringYPos() {
      return inverseStringYPos;
   }

   /**
    * Return the wrapped AffineTransform used for the filtering process
    *
    * @return the wrapped AffineTransform used for the filtering process.
    */
   public AffineTransform getWrappedTransform() {
      return g2d.getTransform();
   }

   public void setForceStroke(Float stroke) {
      this.forceStroke = stroke;
      if (stroke != null) {
         g2d.setStroke(new BasicStroke(stroke.floatValue()));
      }
   }

   /**
    * Set the current scale of the transformation. This order concatenates the current
    * wrapping transform with a scale transform.
    */
   public void setScale(float scale) {
      this.scale = scale;
      transform.concatenate(AffineTransform.getScaleInstance((double) scale, (double) scale));
   }

   public float getScale() {
      return scale;
   }

   @Override
   public void addRenderingHints(Map hints) {
      g2d.addRenderingHints(hints);
   }

   @Override
   public void clearRect(int x, int y, int width, int height) {
      g2d.clearRect(x, y, width, height);
   }

   @Override
   public void clip(Shape shape) {
      if (clipProp == HARD_CLIPPING) {
         g2d.clip(shape);
      } else if (clipProp == SOFT_CLIPPING) {
         if (deviceclip != null) {
            Area area = new Area(getClip());
            if (shape != null) {
               area.intersect(new Area(shape));
            }
            shape = area;
         }
         setClip(shape);
      }
   }

   @Override
   public void clipRect(int x, int y, int width, int height) {
      clip(new Rectangle(x, y, width, height));
   }

   @Override
   public void copyArea(int x, int y, int width, int height, int dx, int dy) {
      g2d.copyArea(x, y, width, height, dx, dy);
   }

   @Override
   public Graphics create() {
      return g2d.create();
   }

   @Override
   public void dispose() {
      g2d.dispose();
   }

   @Override
   public void draw(Shape shape) {
      if (clipProp != SOFT_CLIPPING) {
         g2d.draw(shape);
      } else {
         shape = getClippedShape(shape);
         if (shape != null) {
            g2d.draw(shape);
         }
      }
   }

   private Shape getClippedShape(Shape shape) {
      Shape outputShape = null;
      if ((clipProp != NO_CLIPPING) && (deviceclip != null)) {
         Area clip = new Area(deviceclip);
         Rectangle2D bounds = shape.getBounds2D();
         if ((bounds.getWidth() == 0) || (bounds.getHeight() == 0)) {
            double width = bounds.getWidth() == 0 ? 0.1d : bounds.getWidth();
            double height = bounds.getHeight() == 0 ? 0.1d : bounds.getHeight();
            bounds.setRect(bounds.getX(), bounds.getY(), width, height);
         }
         /* need to use getBounds and not getBounds2D, because there getBounds2D for a
             * vertical or horizontal line gets a Rectangle2D with no width or height,
             * and in this case there is no intersection
          */
         if (clip.contains(bounds)) {
            outputShape = shape;
         } else if (clip.intersects(bounds)) {
            if (clipProp == HARD_CLIPPING) {
               g2d.setClip(deviceclip);
               outputShape = shape;
            } else {
               ClippableShape csh = new ClippableShape(shape);
               csh.intersect(clip);
               outputShape = csh.getPeerShape();
            }
         } else {
            outputShape = null;
         }
      } else {
         outputShape = shape;
      }

      return outputShape;
   }

   private Shape getClippedArea(Shape shape) {
      Shape outputShape = null;
      if ((clipProp != NO_CLIPPING) && (deviceclip != null)) {
         Area clip = new Area(deviceclip);
         /* need to use getBounds and not getBounds2D, because there getBounds2D for a
             * vertical or horizontal line gets a Rectangle2D with no width or height,
             * and in this case there is no intersection
          */
         if (clip.contains(shape.getBounds())) {
            outputShape = shape;
         } else if (clip.intersects(shape.getBounds())) {
            if (clipProp == HARD_CLIPPING) {
               g2d.setClip(deviceclip);
               outputShape = shape;
            } else {
               ClippableShape csh = new ClippableShape(shape);
               csh.intersectAsArea(clip);
               outputShape = csh.getPeerShape();
            }
         } else {
            outputShape = null;
         }
      } else {
         outputShape = shape;
      }

      return outputShape;
   }

   @Override
   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      g2d.drawArc(x, y, width, height, startAngle, arcAngle);
   }

   @Override
   public void drawGlyphVector(GlyphVector g, float x, float y) {
      g2d.drawGlyphVector(g, x, y);
   }

   @Override
   public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
      return g2d.drawImage(img, xform, obs);
   }

   @Override
   public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
      g2d.drawImage(img, op, x, y);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
      return g2d.drawImage(img, x, y, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
      return g2d.drawImage(img, x, y, bgcolor, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
      return g2d.drawImage(img, x, y, width, height, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int width, int height,
      Color bgcolor, ImageObserver observer) {
      return g2d.drawImage(img, x, y, width, height, bgcolor, observer);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
      int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
      return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
      int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
      return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
   }

   @Override
   public void drawLine(int x1, int y1, int x2, int y2) {
      g2d.drawLine(x1, y1, x2, y2);
   }

   @Override
   public void drawOval(int x, int y, int width, int height) {
      g2d.drawOval(x, y, width, height);
   }

   @Override
   public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      g2d.drawPolygon(xPoints, yPoints, nPoints);
   }

   @Override
   public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
      g2d.drawPolyline(xPoints, yPoints, nPoints);
   }

   @Override
   public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
      g2d.drawRenderableImage(img, xform);
   }

   @Override
   public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
      g2d.drawRenderedImage(img, xform);
   }

   @Override
   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
   }

   @Override
   public void drawString(String s, float x, float y) {
      g2d.drawString(s, x, getFilteredY(y));
   }

   @Override
   public void drawString(String str, int x, int y) {
      g2d.drawString(str, x, getFilteredY(y));
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, int x, int y) {
      if (filterCharIter) {
         g2d.drawString(getString(iterator), x, getFilteredY(y));
      } else {
         g2d.drawString(iterator, x, getFilteredY(y));
      }
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, float x, float y) {
      if (filterCharIter) {
         g2d.drawString(getString(iterator), x, getFilteredY(y));
      } else {
         g2d.drawString(iterator, x, getFilteredY(y));
      }
   }

   private float getFilteredY(float y) {
      if (inverseStringYPos) {
         y = lm.getAscent() + y;
         return y;
      } else {
         return y;
      }
   }

   private int getFilteredY(int y) {
      return (int) getFilteredY((float) y);
   }

   private String getString(AttributedCharacterIterator it) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;
      while (true) {
         char c;
         if (first) {
            c = it.first();
            first = false;
         } else {
            c = it.next();
         }
         if (c == CharacterIterator.DONE) {
            break;
         }
         buf.append(c);
      }
      return buf.toString();
   }

   @Override
   public void fill(Shape shape) {
      if (clipProp != SOFT_CLIPPING) {
         g2d.fill(shape);
      } else {
         shape = getClippedArea(shape);
         if (shape != null) {
            g2d.fill(shape);
         }
      }
   }

   @Override
   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      g2d.fillArc(x, y, width, height, startAngle, arcAngle);
   }

   @Override
   public void fillOval(int x, int y, int width, int height) {
      g2d.fillOval(x, y, width, height);
   }

   @Override
   public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      g2d.fillPolygon(xPoints, yPoints, nPoints);
   }

   @Override
   public void fillRect(int x, int y, int width, int height) {
      g2d.fillRect(x, y, width, height);
   }

   @Override
   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
   }

   @Override
   public Color getBackground() {
      return g2d.getBackground();
   }

   @Override
   public Shape getClip() {
      return g2d.getClip();
   }

   @Override
   public Rectangle getClipBounds() {
      return g2d.getClipBounds();
   }

   @Override
   public Color getColor() {
      return g2d.getColor();
   }

   @Override
   public Composite getComposite() {
      return g2d.getComposite();
   }

   @Override
   public GraphicsConfiguration getDeviceConfiguration() {
      return g2d.getDeviceConfiguration();
   }

   @Override
   public Font getFont() {
      return g2d.getFont();
   }

   @Override
   public FontMetrics getFontMetrics(Font f) {
      return g2d.getFontMetrics(f);
   }

   @Override
   public FontRenderContext getFontRenderContext() {
      return g2d.getFontRenderContext();
   }

   @Override
   public Paint getPaint() {
      return g2d.getPaint();
   }

   @Override
   public Object getRenderingHint(RenderingHints.Key hintKey) {
      return g2d.getRenderingHint(hintKey);
   }

   @Override
   public RenderingHints getRenderingHints() {
      return g2d.getRenderingHints();
   }

   @Override
   public Stroke getStroke() {
      return g2d.getStroke();
   }

   @Override
   public AffineTransform getTransform() {
      return g2d.getTransform();
   }

   @Override
   public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
      return g2d.hit(rect, s, onStroke);
   }

   @Override
   public void rotate(double theta) {
      g2d.rotate(theta);
   }

   @Override
   public void rotate(double theta, double x, double y) {
      g2d.rotate(theta, x, y);
   }

   @Override
   public void scale(double sx, double sy) {
      g2d.scale(sx, sy);
   }

   @Override
   public void setBackground(Color color) {
      g2d.setBackground(color);
   }

   @Override
   public void setClip(Shape shape) {
      if (clipProp == HARD_CLIPPING) {
         g2d.setClip(shape);
      } else if (clipProp == SOFT_CLIPPING) {
         deviceclip = shape;
      } else {
         deviceclip = null;
      }
   }

   @Override
   public void setClip(int x, int y, int width, int height) {
      setClip(((Shape) (new Rectangle(x, y, width, height))));
   }

   @Override
   public void setColor(Color c) {
      g2d.setColor(c);
   }

   @Override
   public void setComposite(Composite comp) {
      g2d.setComposite(comp);
   }

   @Override
   public void setFont(Font font) {
      lm = font.getLineMetrics("X", 0, 0, ctx);
      g2d.setFont(font);
   }

   @Override
   public void setPaint(Paint paint) {
      g2d.setPaint(paint);
   }

   @Override
   public void setPaintMode() {
      g2d.setPaintMode();
   }

   @Override
   public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
      g2d.setRenderingHint(hintKey, hintValue);
   }

   @Override
   public void setRenderingHints(Map hints) {
      g2d.setRenderingHints(hints);
   }

   @Override
   public void setStroke(Stroke s) {
      if (forceStroke == null) {
         g2d.setStroke(s);
      }
   }

   public void setClippingProperty(int clipProperty) {
      this.clipProp = clipProperty;
   }

   /**
    * Transformations are filtered by this class before being sent to the wrapped Graphics2D.
    */
   @Override
   public void setTransform(AffineTransform Tx) {
      AffineTransform _tr = (AffineTransform) transform.clone();
      _tr.concatenate(Tx);
      g2d.setTransform(_tr);
   }

   @Override
   public void setXORMode(Color c1) {
      g2d.setXORMode(c1);
   }

   @Override
   public void shear(double shx, double shy) {
      g2d.shear(shx, shy);
   }

   @Override
   public void transform(AffineTransform Tx) {
      g2d.transform(Tx);
   }

   @Override
   public void translate(double tx, double ty) {
      g2d.translate(tx, ty);
   }

   @Override
   public void translate(int x, int y) {
      g2d.translate(x, y);
   }

}
