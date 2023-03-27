/*
Copyright (c) 2011, 2013, 2014, 2015, 2016 Dassault Aviation.
Copyright (c) 2017 Herve Girod.
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

 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on http://j661.sourceforge.net
 */
package org.jfxconverter.drivers.ppt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.record.FontCollection;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFFontInfo;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.StrokeStyle.LineDash;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.mdiutil.geom.ClippableShape;
import org.mdiutil.geom.Polyline2D;
import org.mdiutil.geom.ShapeNormalizer;
import org.mdiutil.geom.ShapeUtilities;

/**
 * This implementation of the java.awt.Graphics2D abstract class allows users to generate PowerPoint content from Java code.
 * The class is a functional Graphics2D class and all graphic orders will paint in the PPT context.
 *
 * This implementation manage:
 * <ul>
 * <li>texts, shapes, and images</li>
 * <li>colors (including alpha-blending) and paints</li>
 * <li>images</li>
 * <li>clippings</li>
 * <li>creation of sub-contexts</li>
 * </ul>
 *
 * @@version 0.20
 */
public class PPTGraphics2D extends Graphics2D {
   public static final int ACTION_DRAW = 0;
   public static final int ACTION_FILL = 1;
   private static final short NOT_CLIPPED = 0;
   private static final short OUTSIDE_CLIP = 1;
   private static final short INTERSECT_CLIP = 2;
   private static final boolean DEBUG = false;
   protected static final float TEXT_MIN_ESCAPE = (float) (Math.PI / 180);
   protected static final double ASSUME_ZERO = 0.01f;
   private BufferedImage img;
   private Graphics2D g2D;
   protected AffineTransform trans;
   // fonts definition
   private Font font;
   private FontRenderContext fctx = null;
   private FontMetrics fontMetrics = null;
   private Shape deviceclip;
   private static final float DASH_LIMIT_DOT = 0.5f / 348f;
   private static final float DASH_LIMIT_NORMAL = 4f / 348f;
   private Paint paint;

   /* The last Color.
    */
   protected Color color;
   /**
    * The default stroke to use.
    */
   protected BasicStroke basicStroke;
   public boolean forceStroke = false;
   /**
    * True if the stroke of the shapes should be forced to a fixed value.
    */
   protected float forceStrokeValue;
   /**
    * The PPT width.
    */
   public float width;
   /**
    * The PPT height.
    */
   public float height;
   protected HSLFSlide slide = null;
   /**
    * The Font collection.
    */
   protected FontCollection coll = null;
   protected StrokeStyle.LineDash lineDashing = StrokeStyle.LineDash.SOLID;
   private boolean acceptSmallShapes = true;
   private boolean hardClipTexts = false;
   // by default the Locale is the default Locale on the Machine
   private Locale locale = Locale.getDefault();

   /**
    * Default constructor.
    */
   public PPTGraphics2D() {
      this.width = 5;
      this.height = 5;
      initContent(Color.BLACK, Color.WHITE, 1);
   }

   /**
    * Create a Graphics2D which will paint in a PPT Slide context. It will use a black background, a default white foreground,
    * and a default stroke width of 1 pixel.
    *
    * @param pptSlide the slide
    * @param imWidth the slide width
    * @param imHeight the slide height
    */
   public PPTGraphics2D(HSLFSlide pptSlide, float imWidth, float imHeight) {
      this(pptSlide, imWidth, imHeight, Color.black, Color.white, 1f);
      this.forceStroke = false;
   }

   /**
    * Create a Graphics2D which will paint in a PPT Slide context.
    *
    * @param pptSlide the slide
    * @param imWidth the slide width
    * @param imHeight the slide height
    * @param background the slide background
    * @param foreground the slide foreground
    */
   public PPTGraphics2D(HSLFSlide pptSlide, float imWidth, float imHeight, Color background, Color foreground) {
      this(pptSlide, imWidth, imHeight, background, foreground, 1f);
      this.forceStroke = false;
   }

   /**
    * Create a Graphics2D which will paint in a PPT Slide context. It will use a black background and a default white foreground.
    *
    * @param pptSlide the slide
    * @param imWidth the slide width
    * @param imHeight the slide height
    * @param strokeValue the stroke width
    */
   public PPTGraphics2D(HSLFSlide pptSlide, float imWidth, float imHeight, float strokeValue) {
      this(pptSlide, imWidth, imHeight, Color.black, Color.white, strokeValue);
      this.forceStroke = true;
   }

   /**
    * Create a Graphics2D which will paint in a PPT Slide context.
    *
    * @param pptSlide the slide
    * @param imWidth the slide width
    * @param imHeight the slide height
    * @param background the slide background
    * @param foreground the slide foreground
    * @param strokeValue the stroke width
    */
   public PPTGraphics2D(HSLFSlide pptSlide, float imWidth, float imHeight, Color background, Color foreground, float strokeValue) {
      if (background == null) {
         background = Color.black;
      }
      if (foreground == null) {
         foreground = Color.white;
      }
      this.width = imWidth;
      this.height = imHeight;
      fctx = new FontRenderContext(new AffineTransform(), true, true);
      this.slide = pptSlide;

      coll = pptSlide.getSlideShow().getDocumentRecord().getEnvironment().getFontCollection();
      initContent(background, foreground, strokeValue);
   }

   /**
    * Copy constructor.
    *
    * @param pptG2d the Graphics2D used for the PPT conversion
    */
   public PPTGraphics2D(PPTGraphics2D pptG2d) {
      trans = (AffineTransform) pptG2d.trans.clone();
      basicStroke = pptG2d.basicStroke;
      paint = pptG2d.paint;
      color = pptG2d.color;
      basicStroke = pptG2d.basicStroke;
      slide = pptG2d.slide;
      deviceclip = pptG2d.deviceclip;
      // this cast is safe (the only reason of the existence of Graphics is because Graphics predated Graphics2D
      // and Sun did not want to change the API when Swing was introduced), to the point that it is even a common pattern in Oracle tutorials on Swing
      g2D = (Graphics2D) pptG2d.g2D.create();
      img = pptG2d.img;
      font = pptG2d.font;
      fontMetrics = pptG2d.fontMetrics;
      fctx = new FontRenderContext(trans, true, true);
      coll = pptG2d.coll;
   }

   /**
    * Force the Locale to retrieve the Font names. Note that by default the default Locale on the Machine will be used.
    *
    * @param locale the Locale
    */
   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   /**
    * Force the Locale to retrieve the Font names. Note that by default the default Locale on the Machine will be used.
    *
    * @param languageTag the language tag
    */
   public void setLocale(String languageTag) {
      this.locale = Locale.forLanguageTag(languageTag);
   }

   /**
    * Return the Slide.
    *
    * @return the Slide
    */
   public HSLFSlide getSlide() {
      return slide;
   }

   public void setHardClippingTexts(boolean b) {
      this.hardClipTexts = b;
   }

   public boolean isHardClippingTexts() {
      return hardClipTexts;
   }

   public void setAcceptSmallShapes(boolean accept) {
      this.acceptSmallShapes = accept;
   }

   public boolean isAcceptingSmallShapes() {
      return acceptSmallShapes;
   }

   private void initContent(Color background, Color foreground, float strokeValue) {
      trans = new AffineTransform();
      img = new BufferedImage(1, 1, 2);
      // this cast is safe (the only reason of the existence of Graphics is because Graphics predated Graphics2D
      // and Sun did not want to change the API when Swing was introduced), to the point that it is even a common pattern in Oracle tutorials on Swing
      g2D = (Graphics2D) img.getGraphics();

      paint = background;
      color = foreground;

      basicStroke = new BasicStroke(strokeValue, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

      this.forceStroke = true;
      this.forceStrokeValue = strokeValue;

      this.setColor(background);
      this.fillRect(0, 0, (int) width, (int) height);
   }

   @Override
   public void addRenderingHints(Map<?, ?> map) {
      g2D.addRenderingHints(map);
   }

   @Override
   public void clearRect(int x, int y, int aWidth, int aHeight) {
      Paint paint1 = paint;

      setColor(getBackground());
      fillRect(x, y, aWidth, aHeight);
      setPaint(paint1);
   }

   private Rectangle2D getTextBox(String s, double x, double y, boolean rotated, float rot) {
      FontMetrics m = getFontMetrics(font);
      LineMetrics lm = m.getLineMetrics(s, this);
      Rectangle2D rec = m.getStringBounds(s, g2D).getBounds();
      TextLayout layout = new TextLayout(s, font, fctx);
      Rectangle2D rec2 = layout.getBounds();
      int stringWidth = m.stringWidth(s);
      int deltaX = 0;
      int deltaY = 0;
      // need to do this else sometimes the String is outside the bounds
      int charWidth = (s.length() / 2 + 1) * m.charWidth('X');
      int supp = 0;

      if (rotated) {
         supp = charWidth;
         deltaY = -(int) (rec2.getHeight() * Math.sin(rot));
         deltaX = (int) (rec.getWidth() * Math.cos(rot));
      }
      int aWidth = (int) (rec.getWidth() + charWidth + supp);
      int aHeight = (int) (rec2.getHeight() + supp);
      int deltaPos = (int) (rec.getHeight() - rec2.getHeight() - lm.getAscent() + lm.getDescent() - lm.getLeading());

      rec2.setRect(x + rec.getWidth() - stringWidth + deltaX, y - rec2.getHeight() + lm.getLeading() - deltaPos + deltaY, aWidth, aHeight);
      if (!acceptSmallShapes) {
         if (rec2.getWidth() < 1d || rec2.getHeight() < 1d) {
            rec2 = null;
         }
      }
      return rec2;
   }

   @Override
   public void clip(Shape shape) {
      shape = ShapeUtilities.createTransformedShape(shape, trans);
      if (deviceclip != null) {
         Area area = new Area(deviceclip);
         if (shape != null) {
            area.intersect(new Area(shape));
         }
         shape = area;
      }
      deviceclip = shape;
   }

   @Override
   public void clipRect(int x, int y, int aWidth, int aHeight) {
      clip(new Rectangle(x, y, aWidth, aHeight));
   }

   @Override
   public void copyArea(int x, int y, int aWidth, int aHeight, int dx, int dy) {
      g2D.copyArea(x, y, aWidth, aHeight, dx, dy);
   }

   @Override
   public Graphics create() {
      PPTGraphics2D pptgraphics2d = new PPTGraphics2D(this);

      return pptgraphics2d;
   }

   @Override
   public void dispose() {
      g2D.dispose();
      // img.flush();
   }

   /**
    * Return true if the Shape can be drawn. The default implementation is if both the width and height of the Shape
    * are greater than 1.
    *
    * @param shape the Shape
    * @return true if the Shape can be drawn
    */
   protected boolean acceptDrawShape(Shape shape) {
      if (acceptSmallShapes) {
         return true;
      } else {
         return shape.getBounds2D().getHeight() >= 1f || shape.getBounds2D().getWidth() >= 1f;
      }
   }

   /**
    * Return true if the Shape can be filled. The default implementation is if both the width and height of the Shape
    * are greater than 1.
    *
    * @param shape the Shape
    * @return true if the Shape can be drawn
    */
   protected boolean acceptFillShape(Shape shape) {
      if (acceptSmallShapes) {
         return true;
      } else {
         return shape.getBounds2D().getHeight() >= 1f && shape.getBounds2D().getWidth() >= 1f;
      }
   }

   @Override
   public void draw(Shape shape) {
      shape = transformShape(shape);
      if (shape != null) {
         if (acceptDrawShape(shape)) {
            doDrawing(shape);
         }
      }
   }

   private void doDrawing(Shape shape) {
      if (shape instanceof Ellipse2D) {
         doEllipse2DDrawing((Ellipse2D) shape, ACTION_DRAW);
      } else if (shape instanceof Arc2D) {
         doArc2DDrawing((Arc2D) shape, ACTION_DRAW);
      } else if (shape instanceof Line2D) {
         doLineDrawing((Line2D) shape);
      } else if (shape instanceof Rectangle2D) {
         doRectangleDrawing((Rectangle2D) shape, ACTION_DRAW);
      } else if (shape instanceof RoundRectangle2D) {
         doRoundRectangleDrawing((RoundRectangle2D) shape, ACTION_DRAW);
      } else {
         doShapeDrawing(shape, ACTION_DRAW);
      }
   }

   private Color getBrighterColor(GradientPaint paint) {
      Color col1 = paint.getColor1();
      Color col2 = paint.getColor2();
      float rgb1 = (0.299f * col1.getRed() + 0.587f * col1.getGreen() + 0.114f * col1.getBlue()) / 255f;
      float rgb2 = (0.299f * col2.getRed() + 0.587f * col2.getGreen() + 0.114f * col2.getBlue()) / 255f;
      return rgb1 > rgb2 ? col1 : col2;
   }

   private void setShapeDrawProperties(HSLFSimpleShape shape) {
      shape.setFillColor(null);
      if (color == null && paint instanceof GradientPaint) {
         GradientPaint gpaint = (GradientPaint) paint;
         shape.setLineColor(getBrighterColor(gpaint));
      } else {
         shape.setLineColor(this.getColor());
      }
      shape.setLineWidth(basicStroke.getLineWidth());
      shape.setLineDash(lineDashing);
   }

   private void setShapeFillProperties(HSLFSimpleShape shape) {
      // if the current color is not null, use it
      if (color != null) {
         shape.setFillColor(color);
         // if the alpha value of the color is not 255, it's translucent, so we need to set the transparency property for the
         // records used for the fill
         if (color.getAlpha() != 255) {
            EscherOptRecord opt = (EscherOptRecord) HSLFShape.getEscherChild(shape.getSpContainer(), EscherOptRecord.RECORD_ID);
            // int alpha =  (p.getPropertyValue() >> 8) & 0xFF;
            int alpha = color.getAlpha() << 8;
            EscherSimpleProperty p = new EscherSimpleProperty(EscherPropertyTypes.FILL__FILLOPACITY, alpha);
            opt.addEscherProperty(p);
         }
         shape.setLineColor(null);
         setShapeFillingAdditionalProperties(shape);
      } else {
         // else it's a paint, we will draw the paint in a BufferedImage, and then regularly draw this image, and we are done
         // We use the outline for the Shape, but the outline bounds for the image, so we don't have
         // only a stupid rectangle if the Shape was more complex
         Shape outline;
         if (shape instanceof HSLFFreeformShape) {
            outline = ((HSLFFreeformShape) shape).getPath();
         } else {
            outline = shape.getAnchor();
         }
         // we need to create a new outline to be positioned on 0, 0, else we will draw outside of the BufferedImage
         Rectangle rec = outline.getBounds();
         // we don't want to clip the outline to fill !!
         outline = ShapeUtilities.createTransformedShape(outline, AffineTransform.getTranslateInstance(-rec.x, -rec.y));
         if (outline != null && rec.width > 0 && rec.height > 0) {
            BufferedImage bimg = new BufferedImage(rec.width, rec.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bimg.createGraphics();
            g2d.setPaint(paint);
            g2d.fill(outline);
            try {
               ByteArrayOutputStream stream = new ByteArrayOutputStream(100);
               ImageIO.write(bimg, "png", stream);
               stream.flush();
               stream.close();

               byte[] b = stream.toByteArray();
               HSLFSlideShow show = slide.getSlideShow();
               HSLFPictureData idx = show.addPicture(b, PictureData.PictureType.PNG);

               HSLFPictureShape pict = doCreatePicture(idx);

               // and now we must position correctly the Image
               rec = new Rectangle(rec.x, rec.y, rec.width, rec.height);
               pict.setAnchor(rec);
               pict.setSheet(slide);
               shape.setLineColor(null);
               shape.setFillColor(null);
               pict.setLineColor(null);
               pict.setFillColor(null);
               setShapeFillingAdditionalProperties(pict);
               addShape(pict);
            } catch (IOException e) {
            }
         }
      }
   }

   /**
    * Creates a picture Shape.
    *
    * @param data the picture data
    * @return the picture Shape
    */
   protected HSLFPictureShape doCreatePicture(HSLFPictureData data) {
      HSLFPictureShape pict = new HSLFPictureShape(data);
      return pict;
   }

   /**
    * Creates a Line Shape.
    *
    * @return the Line Shape
    */
   protected HSLFLine doCreateLine() {
      HSLFLine linep = new HSLFLine();
      return linep;
   }

   /**
    * Creates an AutoShape.
    *
    * @param type the AutoShape type
    * @return the AutoShape
    */
   protected HSLFAutoShape doCreateAutoShape(ShapeType type) {
      HSLFAutoShape recp = new HSLFAutoShape(type);
      return recp;
   }

   /**
    * Creates a FreeForm Shape.
    *
    * @return the FreeForm Shape
    */
   protected HSLFFreeformShape doCreateFreeformShape() {
      HSLFFreeformShape recp = new HSLFFreeformShape();
      return recp;
   }

   /**
    * Creates a TextBox.
    *
    * @return the TextBox
    */
   protected HSLFTextBox doCreateTextBox() {
      HSLFTextBox text = new HSLFTextBox();
      return text;
   }

   private boolean isWellAligned(Rectangle2D boundingBox, Line2D line) {
      if (Math.abs(boundingBox.getMinX() - line.getX1()) <= ASSUME_ZERO && Math.abs(boundingBox.getMinY() - line.getY1()) <= ASSUME_ZERO) {
         return true;
      } else if (Math.abs(boundingBox.getMinX() - line.getX2()) <= ASSUME_ZERO && Math.abs(boundingBox.getMinY() - line.getY2()) <= ASSUME_ZERO) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Add the Shape to the Slide.
    *
    * @param shape the Shape
    */
   protected void addShape(HSLFShape shape) {
      shape.setSheet(slide);
      slide.addShape(shape);
   }

   private void doLineDrawing(Line2D line) {
      HSLFLine linep = doCreateLine();

      linep.setSheet(slide);

      Rectangle2D rec = line.getBounds2D();

      // we absolutely need to anchor with the same rectangle, else the line will not be correctly aligned (for example
      // horizontal lines will appear a little not horizontal)
      linep.setAnchor(rec);
      // we don't need to do that, but in the wild lines appear without any insets, so it will not hurt do do the same here
      linep.setTopInset(0f);
      linep.setBottomInset(0f);
      linep.setLeftInset(0f);
      linep.setRightInset(0f);
      if (!isWellAligned(rec, line)) {
         linep.setRotation(90);
      }
      addShape(linep);
      setShapeDrawProperties(linep);
   }

   private void doRectangleDrawing(Rectangle2D rec, int type) {
      HSLFAutoShape recp = doCreateAutoShape(ShapeType.RECT);
      recp.setSheet(slide);
      recp.setAnchor(rec.getBounds());
      addShape(recp);
      if (type == ACTION_DRAW) {
         setShapeDrawProperties(recp);
      } else {
         this.setShapeFillProperties(recp);
      }
   }

   private void doRoundRectangleDrawing(RoundRectangle2D rec, int type) {
      HSLFFreeformShape recp = doCreateFreeformShape();
      recp.setPath(new Path2D.Double(rec));

      addShape(recp);
      //recp.setAnchor(rec.getBounds());
      if (type == ACTION_DRAW) {
         setShapeDrawProperties(recp);
      } else {
         this.setShapeFillProperties(recp);
      }
   }

   private void doEllipse2DDrawing(Ellipse2D e, int type) {
      HSLFAutoShape ellipse = doCreateAutoShape(ShapeType.ELLIPSE);

      ellipse.setSheet(slide);
      ellipse.setAnchor(e.getBounds());
      addShape(ellipse);
      if (type == ACTION_DRAW) {
         setShapeDrawProperties(ellipse);
      } else {
         this.setShapeFillProperties(ellipse);
      }
   }

   private void doArc2DDrawing(Arc2D arc, int type) {
      if (Math.abs(arc.getAngleExtent()) >= 360) {
         Ellipse2D e = new Ellipse2D.Double(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight());

         doEllipse2DDrawing(e, type);
      } else {
         double startAngle = arc.getAngleStart();
         double extent = arc.getAngleExtent();
         if (extent < 0) {
            // don't know why I need to do this...
            extent = 360 + extent;
            arc = new Arc2D.Double(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(), startAngle, extent, arc.getArcType());
         }
         HSLFFreeformShape free = new HSLFFreeformShape();

         free.setSheet(slide);
         Path2D.Double path = new Path2D.Double(arc);
         free.setPath(path);
         addShape(free);
         if (type == ACTION_DRAW) {
            setShapeDrawProperties(free);
         } else {
            this.setShapeFillProperties(free);
         }
      }
   }

   /**
    * This method will be called after the Shape properties has been set by this class, to add
    * other user properties to the Shape. Do nothing by default.
    *
    * @param shape the Shape
    */
   protected void setShapeFillingAdditionalProperties(HSLFSimpleShape shape) {
   }

   private void doShapeDrawing(Shape shape, int type) {
      HSLFFreeformShape free = doCreateFreeformShape();

      free.setSheet(slide);
      Rectangle rec = shape.getBounds();
      if (rec.getWidth() < 3 || rec.getHeight() < 3) {
         double recWidth = Math.max(3., rec.getWidth());
         double recHeight = Math.max(3., rec.getHeight());
         rec.setSize((int) recWidth, (int) recHeight);
      }
      free.setAnchor(rec);

      AffineTransform affinetransform = new AffineTransform();
      PathIterator pathiterator = shape.getPathIterator(affinetransform);
      ShapeNormalizer shapeNorm = new ShapeNormalizer(true);
      List<Shape> shapes = shapeNorm.convertPath(pathiterator);

      if (shapes.size() == 1) {
         Shape _shape = shapes.get(0);
         if (_shape instanceof Polyline2D) {
            Polyline2D pol = (Polyline2D) _shape;
            if (pol.npoints == 2) {
               Line2D.Float line = new Line2D.Float(pol.xpoints[0], pol.ypoints[0], pol.xpoints[1], pol.ypoints[1]);
               doAddShape(free, line, type);
            } else {
               doAddShape(free, _shape, type);
            }
         } else {
            doAddShape(free, _shape, type);
         }
      } else {
         doAddShape(free, shape, type);
      }
   }

   private void doAddShape(HSLFFreeformShape free, Shape shape, int type) {
      Path2D.Double path = new Path2D.Double(shape);
      free.setPath(path);
      if (type == ACTION_DRAW) {
         setShapeDrawProperties(free);
      } else {
         setShapeFillProperties(free);
      }
      addShape(free);
   }

   @Override
   public void drawArc(int x, int y, int aWidth, int aHeight, int startAngle, int arcAngle) {
      draw(new Arc2D.Float(x, y, aWidth, aHeight, startAngle, arcAngle, 0));
   }

   @Override
   public void drawGlyphVector(GlyphVector vec, float x, float y) {
      fill(vec.getOutline(x, y));
   }

   /**
    * Create a PNG image as a byte array from a rendered Image. PNG is chosen because JPEG
    * seems not adapted to batik or POI (inverts colors in the resulted image).
    */
   private byte[] getImageData(Image image, int iWidth, int iHeight, ImageObserver observer) throws IOException {
      BufferedImage bufImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = bufImage.createGraphics();
      g2d.drawImage(image, new AffineTransform(), observer);

      ByteArrayOutputStream stream = new ByteArrayOutputStream(100);

      ImageIO.write(bufImage, "png", stream);
      stream.flush();
      stream.close();

      return stream.toByteArray();
   }

   private void addImage(Image image, Rectangle rec, ImageObserver observer) {
      HSLFSlideShow show = slide.getSlideShow();
      try {
         byte[] b = getImageData(image, rec.width, rec.height, observer);
         HSLFPictureData idx = show.addPicture(b, PictureData.PictureType.PNG);

         HSLFPictureShape pict = doCreatePicture(idx);
         rec = new Rectangle(rec.x, rec.y, rec.width, rec.height);
         pict.setAnchor(rec);

         // filling with a null color is important else a default color will mask the image
         pict.setFillColor(null);
         pict.setLineColor(null);
         addShape(pict);
      } catch (IOException e) {
      }
   }

   private boolean isEmpty(Rectangle rec) {
      return rec.isEmpty() || rec.width <= 0 || rec.height <= 0;
   }

   @Override
   public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color color1, ImageObserver observer) {
      Rectangle recdst = new Rectangle(dx1, dy1, dx2 - dx1, dy2 - dy1);
      Rectangle rec = getTransformedForImage(recdst);
      if (rec != null) {
         addImage(image, rec, observer);

      }
      return true;
   }

   private Rectangle getTransformedForImage(Rectangle recdst) {
      if (!isEmpty(recdst)) {
         AffineTransform rotation = ShapeUtilities.getRotationTransform(trans);
         if (rotation.isIdentity()) {
            Shape shape = transformShape(recdst);
            if (shape != null) {
               Rectangle rec = shape.getBounds();
               return rec;
            } else {
               return null;
            }
         } else {
            AffineTransform tr = AffineTransform.getTranslateInstance(-trans.getTranslateX(), -trans.getTranslateY());
            Shape shape = tr.createTransformedShape(recdst);
            if (shape != null) {
               Rectangle rec = shape.getBounds();
               return rec;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
      return drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
   }

   @Override
   public boolean drawImage(Image image, int x, int y, int iWidth, int iHeight, Color theColor, ImageObserver observer) {
      Rectangle rectangle = new Rectangle(x, y, iWidth, iHeight);
      Rectangle rec = getTransformedForImage(rectangle);
      if (rec != null) {
         addImage(image, rec, observer);
      }
      addImage(image, rec, observer);

      return true;
   }

   @Override
   public boolean drawImage(Image image, int x, int y, int aWidth, int aHeight, ImageObserver observer) {
      return drawImage(image, x, y, aWidth, aHeight, null, observer);
   }

   @Override
   public boolean drawImage(Image image, int x, int y, Color aColor, ImageObserver observer) {
      return drawImage(image, x, y, image.getWidth(observer), image.getHeight(observer), aColor, observer);
   }

   @Override
   public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
      return drawImage(image, x, y, image.getWidth(observer), image.getHeight(observer), observer);
   }

   @Override
   public boolean drawImage(Image image, AffineTransform tr, ImageObserver imageobserver) {
      AffineTransform affine1 = (AffineTransform) trans.clone();

      trans.concatenate(tr);
      drawImage(image, 0, 0, imageobserver);
      trans = affine1;
      return true;
   }

   @Override
   public void drawImage(BufferedImage image, BufferedImageOp imageOp, int i, int j) {
      BufferedImage bufImage1 = imageOp.filter(image, null);
      drawImage(bufImage1, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, i, j), null);
   }

   @Override
   public void drawLine(int x1, int y1, int x2, int y2) {
      draw(new Line2D.Float(x1, y1, x2, y2));
   }

   @Override
   public void drawOval(int x, int y, int aWidth, int aHeight) {
      draw(new Ellipse2D.Float(x, y, aWidth, aHeight));
   }

   @Override
   public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
      draw(new Polygon(xPoints, yPoints, nPoints));
   }

   @Override
   public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {
      if (nPoints > 0) {
         GeneralPath generalpath = new GeneralPath();

         generalpath.moveTo(xPoints[0], yPoints[0]);
         for (int j = 1; j < nPoints; j++) {
            generalpath.lineTo(xPoints[j], yPoints[j]);
         }

         draw(generalpath);
      }
   }

   @Override
   public void drawRect(int x, int y, int aWidth, int aHeight) {
      Rectangle rectangle = new Rectangle(x, y, aWidth, aHeight);

      draw(rectangle);
   }

   @Override
   public void drawRenderableImage(RenderableImage image, AffineTransform transform) {
      drawRenderedImage(image.createDefaultRendering(), transform);
   }

   @Override
   public void drawRenderedImage(RenderedImage image, AffineTransform transform) {
      BufferedImage bufferedimage = new BufferedImage(image.getColorModel(), image.getData().createCompatibleWritableRaster(), false, null);

      bufferedimage.setData(image.getData());
      drawImage(bufferedimage, transform, null);
   }

   @Override
   public void drawRoundRect(int x, int y, int aWidth, int aHeight, int arcWidth, int arcHeight) {
      draw(new RoundRectangle2D.Float(x, y, aWidth, aHeight, arcWidth, arcHeight));
   }

   @Override
   public void drawString(String s, float x, float y) {
      doDrawString(s, x, y);
   }

   /**
    * Return the clipping state of the text.
    */
   private short getClippedState(Rectangle2D rec) {
      if (deviceclip == null) {
         return NOT_CLIPPED;
      } else if (deviceclip.contains(rec)) {
         return NOT_CLIPPED;
      } else if (deviceclip.intersects(rec)) {
         return INTERSECT_CLIP;
      } else {
         return OUTSIDE_CLIP;
      }
   }

   private void doDrawString(String s, float x, float y) {
      // set escapement (rotation of the text)
      float rot = -(float) ShapeUtilities.getRotationAngle(trans);
      boolean rotated = rot != 0;

      // all this is to be sure to have the most precise position possible
      TextLayout layout = new TextLayout(s, font, fctx);
      // we need to check the clipping against the real position of the text of course
      Rectangle2D layoutRec = layout.getBounds();
      layoutRec.setRect(layoutRec.getX() + x, layoutRec.getY() + y, layoutRec.getWidth(), layoutRec.getHeight());

      Shape shape = this.transformShape(layoutRec);
      if (shape != null) {
         layoutRec = shape.getBounds2D();
         Rectangle2D rec = getTextBox(s, layoutRec.getX(), layoutRec.getY() + fontMetrics.getDescent(), rotated, rot);

         if (rec == null) {
            return;
         }
         short clipState = getClippedState(rec);
         if (clipState == INTERSECT_CLIP) {
            if (hardClipTexts) {
               Rectangle clipRec = deviceclip.getBounds();
               BufferedImage buf = new BufferedImage((int) clipRec.getWidth(), (int) clipRec.getHeight(), BufferedImage.TYPE_INT_ARGB);
               Graphics2D clipG2D = buf.createGraphics();
               clipG2D.setColor(color);
               AffineTransform tr = AffineTransform.getTranslateInstance(-clipRec.getX(), -clipRec.getY());
               Shape _clip = tr.createTransformedShape(deviceclip);
               clipG2D.clip(_clip);
               layout.draw(clipG2D, 0, 0);
               Rectangle rec2 = new Rectangle(clipRec.x, clipRec.y, (int) clipRec.getWidth(), (int) clipRec.getHeight());
               addImage(buf, rec2, null);
            } else {
               drawUnclippedText(s, rec, rot);
            }
         } else if (clipState == NOT_CLIPPED) {
            drawUnclippedText(s, rec, rot);
         }
      }
   }

   private void drawUnclippedText(String s, Rectangle2D rec, float rot) {
      HSLFTextBox txt = doCreateTextBox();

      txt.setWordWrap(false);
      txt.setText(s);
      txt.setSheet(slide);

      if (Math.abs(rot) > TEXT_MIN_ESCAPE) {
         txt.setRotation((int) (-rot * 180d / Math.PI));
      }

      if (DEBUG) {
         Color col = this.getColor();
         this.setColor(Color.YELLOW);
         fontMetrics = getFontMetrics(font);
         this.draw(fontMetrics.getStringBounds(s, g2D).getBounds());
         TextLayout _layout = new TextLayout(s, font, fctx);
         this.setColor(Color.MAGENTA);
         this.draw(_layout.getBounds());
         this.setColor(col);
      }

      txt.setAnchor(rec.getBounds());
      txt.setTopInset(0f);
      txt.setBottomInset(0f);
      txt.setLeftInset(0f);
      txt.setRightInset(0f);
      // need to use AnchorTop and not AlignCenter
      txt.setVerticalAlignment(VerticalAlignment.TOP);
      // use RichTextRun to work with the text format
      HSLFTextRun rt = txt.getTextParagraphs().get(0).getTextRuns().get(0);

      rt.setFontSize((double) font.getSize2D());
      rt.setText(s);
      // the coll.getFontIndex(fontName) method does not exist anymore in POI 3.17
      // thanks Mark Schmieder for the fix
      String fontName = font.getFontName(locale);
      HSLFFontInfo fInfo = coll.getFontInfo(fontName);
      int fIndex = (fInfo == null) ? -1 : fInfo.getIndex();
      if (fIndex == -1) {
         rt.setFontFamily(font.getFamily());
      } else {
         rt.setFontIndex(fIndex);
      }
      rt.setBold(font.isBold());
      rt.setItalic(font.isItalic());
      rt.setFontColor(this.getColor());
      // need to use AlignLeft and not AlignCenter
      txt.getTextParagraphs().get(0).setTextAlign(TextParagraph.TextAlign.LEFT);
      addShape(txt);
   }

   @Override
   public void drawString(String s, int x, int y) {
      drawString(s, (float) x, (float) y);
   }

   @Override
   public void drawString(AttributedCharacterIterator ati, float x, float y) {
      // now iterate through all the runs
      ati.first();
      while (ati.current() != CharacterIterator.DONE) {
         // retrieve the current span of text for the run
         StringBuilder buf = new StringBuilder();

         while (true) {
            buf.append(ati.current());
            if (ati.getIndex() == ati.getRunLimit() - 1) {
               ati.next();
               break;
            } else {
               ati.next();
            }
         }
         String s = buf.toString();
         HSLFTextBox txt = new HSLFTextBox();

         txt.setSheet(slide);
         txt.setFillColor(null);
         txt.setLineColor(null);
         txt.setText(s);

         // set escapement (rotation of the text)
         float rot = (float) Math.atan2(trans.getShearX(), trans.getScaleX());
         boolean rotated = false;

         if (Math.abs(rot) > TEXT_MIN_ESCAPE) {
            txt.setRotation((int) (-rot * 180d / Math.PI));
            rotated = true;
         }

         double _x = x;
         double _y = y;

         Point2D.Double pt = new Point2D.Double(_x + getFont().getTransform().getTranslateX(), _y + getFont().getTransform().getTranslateY());

         txt.setAnchor(getTextBox(s, pt.getX(), pt.getY(), rotated, rot).getBounds());
         txt.setTopInset(0f);
         txt.setBottomInset(0f);
         txt.setLeftInset(0f);
         txt.setRightInset(0f);
         // need to use AnchorTop and not AlignCenter
         txt.setVerticalAlignment(VerticalAlignment.TOP);
         // use RichTextRun to work with the text format
         HSLFTextRun rt = txt.getTextParagraphs().get(0).getTextRuns().get(0);
         rt.setFontSize((double) font.getSize2D());

         // the coll.getFontIndex(fontName) method does not exist anymore in POI 3.17
         // thanks Mark Schmieder for the fix
         String fontName = font.getFontName(locale);

         HSLFFontInfo fInfo = coll.getFontInfo(fontName);
         int fIndex = (fInfo == null) ? -1 : fInfo.getIndex();
         if (fIndex == -1) {
            rt.setFontFamily(font.getFamily());
         } else {
            rt.setFontIndex(fIndex);
         }
         rt.setBold(font.isBold());
         rt.setItalic(font.isItalic());
         rt.setFontColor(this.getColor());
         // need to use AlignLeft and not AlignCenter
         txt.getTextParagraphs().get(0).setTextAlign(TextParagraph.TextAlign.LEFT);
         addShape(txt);
      }
   }

   @Override
   public void drawString(AttributedCharacterIterator ati, int x, int y) {
      float fx = x;
      float fy = y;
      drawString(ati, fx, fy);
   }

   protected Shape transformShape(Shape shape) {
      shape = ShapeUtilities.createTransformedShape(shape, trans);
      shape = getClippedShape(shape);
      return shape;
   }

   @Override
   public void fill(Shape shape) {
      shape = transformShape(shape);
      if (shape != null) {
         if (acceptFillShape(shape)) {
            doFilling(shape);
         }
      }
   }

   private boolean doFilling(Shape shape) {
      // protected against a null slide. THis method will be called with the default constructor
      // for example
      if (slide != null) {
         if (shape instanceof Ellipse2D) {
            doEllipse2DDrawing((Ellipse2D) shape, ACTION_FILL);
         } else if (shape instanceof Arc2D) {
            doArc2DDrawing((Arc2D) shape, ACTION_FILL);
         } else if (shape instanceof Rectangle2D) {
            doRectangleDrawing((Rectangle2D) shape, ACTION_FILL);
         } else if (shape instanceof RoundRectangle2D) {
            doRoundRectangleDrawing((RoundRectangle2D) shape, ACTION_FILL);
         } else {
            doShapeDrawing(shape, ACTION_FILL);
         }
         return true;
      } else {
         return false;
      }
   }

   private Shape getClippedArea(Shape shape) {
      Shape outputShape;
      if (deviceclip != null) {
         Area clip = new Area(deviceclip);

         /*
          * need to use getBounds and not getBounds2D, because there getBounds2D for a
          * vertical or horizontal line gets a Rectangle2D with no width or height,
          * and in this case there is no intersection
          */
         if (clip.contains(shape.getBounds())) {
            outputShape = shape;
         } else if (clip.intersects(shape.getBounds())) {
            ClippableShape csh = new ClippableShape(shape);

            csh.intersectAsArea(clip);
            outputShape = csh.getPeerShape();
         } else {
            outputShape = null;
         }
      } else {
         outputShape = shape;
      }

      return outputShape;
   }

   private Shape getClippedShape(Shape shape) {
      Shape outputShape;
      if (deviceclip != null) {
         Area clip = new Area(deviceclip);
         Rectangle2D bounds = shape.getBounds2D();
         if (bounds.getWidth() < 0.1 || bounds.getHeight() < 0.1) {
            double boundsWidth = Math.max(0.1, bounds.getWidth());
            double boundsHeight = Math.max(0.1, bounds.getHeight());
            bounds.setRect(bounds.getX(), bounds.getY(), boundsWidth, boundsHeight);
         }
         /*
          * need to use getBounds and not getBounds2D, because there getBounds2D for a
          * vertical or horizontal line gets a Rectangle2D with no width or height,
          * and in this case there is no intersection
          */
         if (clip.contains(bounds)) {
            outputShape = shape;
         } else if (clip.intersects(bounds)) {
            ClippableShape csh = new ClippableShape(shape);
            csh.intersect(clip);
            outputShape = csh.getPeerShape();
         } else {
            outputShape = null;
         }
      } else {
         outputShape = shape;
      }
      return outputShape;
   }

   @Override
   public void fillArc(int x, int y, int aWidth, int aHeight, int startAngle, int arcAngle) {
      fill(new java.awt.geom.Arc2D.Float(x, y, aWidth, aHeight, startAngle, arcAngle, 2));
   }

   @Override
   public void fillOval(int x, int y, int aWidth, int aHeight) {
      fill(new java.awt.geom.Ellipse2D.Float(x, y, aWidth, aHeight));
   }

   @Override
   public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
      fill(new Polygon(xPoints, yPoints, nPoints));
   }

   @Override
   public void fillRect(int x, int y, int rWidth, int rHeight) {
      fill(new Rectangle(x, y, rWidth, rHeight));
   }

   @Override
   public void fillRoundRect(int x, int y, int rWidth, int rHeight, int arcWidth, int arcHeight) {
      fill(new java.awt.geom.RoundRectangle2D.Float(x, y, rWidth, rHeight, arcWidth, arcHeight));
   }

   @Override
   public Color getBackground() {
      return Color.WHITE;
   }

   @Override
   public Shape getClip() {
      try {
         return ShapeUtilities.createTransformedShape(deviceclip, trans.createInverse());
      } catch (Exception _ex) {
         return null;
      }
   }

   @Override
   public Rectangle getClipBounds() {
      if (deviceclip != null) {
         return getClip().getBounds();
      } else {
         return null;
      }
   }

   @Override
   public Color getColor() {
      return color;
   }

   @Override
   public Composite getComposite() {
      return g2D.getComposite();
   }

   @Override
   public GraphicsConfiguration getDeviceConfiguration() {
      return g2D.getDeviceConfiguration();
   }

   @Override
   public Font getFont() {
      return g2D.getFont();
   }

   @Override
   public FontMetrics getFontMetrics(Font font) {
      return g2D.getFontMetrics(font);
   }

   @Override
   public FontRenderContext getFontRenderContext() {
      g2D.setTransform(trans);
      return g2D.getFontRenderContext();
   }

   @Override
   public Paint getPaint() {
      return paint;
   }

   @Override
   public Object getRenderingHint(java.awt.RenderingHints.Key key) {
      return g2D.getRenderingHint(key);
   }

   @Override
   public RenderingHints getRenderingHints() {
      return g2D.getRenderingHints();
   }

   @Override
   public Stroke getStroke() {
      return basicStroke;
   }

   @Override
   public AffineTransform getTransform() {
      return (AffineTransform) trans.clone();
   }

   @Override
   public boolean hit(Rectangle rectangle, Shape shape, boolean flag) {
      g2D.setTransform(trans);
      g2D.setStroke(getStroke());
      g2D.setClip(getClip());
      return g2D.hit(rectangle, shape, flag);
   }

   @Override
   public void rotate(double theta) {
      trans.rotate(theta);
   }

   @Override
   public void rotate(double theta, double x, double y) {
      trans.rotate(theta, x, y);
   }

   @Override
   public void scale(double sx, double sy) {
      trans.scale(sx, sy);
   }

   @Override
   public void setBackground(Color aColor) {
   }

   @Override
   public void setClip(int x, int y, int cWidth, int cHeight) {
      setClip(new Rectangle(x, y, cWidth, cHeight));
   }

   /**
    * Set the clipping for the next graphic orders.
    *
    * @param shape the Clipping Shape
    */
   @Override
   public void setClip(Shape shape) {
      if (shape != null) {
         deviceclip = ShapeUtilities.createTransformedShape(shape, trans);
      } else {
         deviceclip = null;
      }
   }

   @Override
   public void setColor(Color col) {
      setPaint(col);
   }

   @Override
   public void setComposite(Composite composite) {
      g2D.setComposite(composite);
   }

   @Override
   public void setFont(Font font) {
      g2D.setFont(font);
      fontMetrics = getFontMetrics(font);
      this.font = font;
   }

   @Override
   public void setPaint(Paint paint) {
      if (paint != null) {
         if (paint instanceof Color) {
            color = (Color) paint;
            this.paint = null;
         } else {
            color = null;
            this.paint = paint;
         }
      }
   }

   @Override
   public void setPaintMode() {
   }

   @Override
   public void setRenderingHint(java.awt.RenderingHints.Key key, Object obj) {
      g2D.setRenderingHint(key, obj);
   }

   @Override
   public void setRenderingHints(Map<?, ?> map) {
      g2D.setRenderingHints(map);
   }

   @Override
   public void setStroke(Stroke theStroke) {
      if (theStroke instanceof BasicStroke) {
         basicStroke = (BasicStroke) theStroke;
         setPenStyle(basicStroke);
      } else {
         basicStroke = new BasicStroke();
         setPenStyle(basicStroke);
      }
      if (forceStroke) {
         basicStroke = new BasicStroke(forceStrokeValue, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(),
            basicStroke.getDashArray(), basicStroke.getDashPhase());
         setPenStyle(basicStroke);
      }
   }

   private void setPenStyle(BasicStroke stroke) {
      lineDashing = StrokeStyle.LineDash.SOLID;
      float[] dash = stroke.getDashArray();

      // dash value
      if (dash == null || dash.length == 1) {
         lineDashing = StrokeStyle.LineDash.SOLID;
      } else if (dash.length == 2) {
         if (dash[0] / width < DASH_LIMIT_DOT) {
            lineDashing = StrokeStyle.LineDash.DASH_DOT;
         } else if (dash[0] / width < DASH_LIMIT_NORMAL) {
            lineDashing = StrokeStyle.LineDash.DOT;
         } else {
            lineDashing = StrokeStyle.LineDash.DASH;
         }
      } else if (dash.length == 3) {
         lineDashing = LineDash.DASH_DOT;
      } else if (dash.length < 6) {
         lineDashing = StrokeStyle.LineDash.LG_DASH_DOT;
      } else {
         lineDashing = StrokeStyle.LineDash.LG_DASH_DOT_DOT;
      }
   }

   @Override
   public void setTransform(AffineTransform tr) {
      // trans = new AffineTransform();
      trans = (AffineTransform) tr.clone();
   }

   @Override
   public void setXORMode(Color aColor) {
   }

   @Override
   public void shear(double shx, double shy) {
      trans.shear(shx, shy);
   }

   @Override
   public void transform(AffineTransform tr) {
      trans.concatenate(tr);
   }

   Image transformImage(Image image, Rectangle rec, Rectangle rec1, ImageObserver observer, Color col) {
      if (trans.isIdentity() && getClip() == null && col == null) {
         return image;
      }
      if (deviceclip != null) {
         Area area = new Area(deviceclip);

         area.intersect(new Area(trans.createTransformedShape(rec)));
         rec1.setBounds(area.getBounds());
      }
      BufferedImage bufferedimage = new BufferedImage(rec1.width, rec1.height, 2);
      // this cast is safe (the only reason of the existence of Graphics is because Graphics predated Graphics2D
      // and Sun did not want to change the API when Swing was introduced), to the point that it is even a common pattern in Oracle tutorials on Swing
      Graphics2D graphics2d = (Graphics2D) bufferedimage.getGraphics();

      graphics2d.addRenderingHints(getRenderingHints());
      graphics2d.translate(-rec1.x, -rec1.y);
      graphics2d.transform((AffineTransform) getTransform().clone());
      graphics2d.setClip(getClip());
      if (col != null) {
         graphics2d.drawImage(image, rec.x, rec.y, rec.width, rec.height, col, observer);
      } else {
         graphics2d.drawImage(image, rec.x, rec.y, rec.width, rec.height, observer);
      }
      graphics2d.dispose();
      return bufferedimage;
   }

   Image transformImage(Image image, int ai[], Rectangle rec, ImageObserver observer, Color col) {
      BufferedImage buf = new BufferedImage(rec.width, rec.height, 2);
      // this cast is safe (the only reason of the existence of Graphics is because Graphics predated Graphics2D
      // and Sun did not want to change the API when Swing was introduced), to the point that it is even a common pattern in Oracle tutorials on Swing
      Graphics2D graphics2d = (Graphics2D) buf.getGraphics();

      graphics2d.addRenderingHints(getRenderingHints());
      graphics2d.translate(-rec.x, -rec.y);
      graphics2d.transform((AffineTransform) getTransform().clone());
      graphics2d.setClip(getClip());
      if (col != null) {
         graphics2d.drawImage(image, ai[0], ai[1], ai[2], ai[3], ai[4], ai[5], ai[6], ai[7], col, observer);
      } else {
         graphics2d.drawImage(image, ai[0], ai[1], ai[2], ai[3], ai[4], ai[5], ai[6], ai[7], observer);
      }
      return buf;
   }

   @Override
   public void translate(double x, double y) {
      trans.translate(x, y);
   }

   @Override
   public void translate(int x, int y) {
      trans.translate(x, y);
   }
}
