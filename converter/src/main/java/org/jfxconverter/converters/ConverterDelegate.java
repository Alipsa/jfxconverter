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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Stack;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import org.jfxconverter.utils.JFXShapeUtilities;

/**
 * The ConverterDelegate class allows handle the effective conversion.
 *
 * Note that it is preferable to use the {@link org.jfxconverter.JFXConverter} class rather than this one. This class is called internally by the
 * {@link org.jfxconverter.JFXConverter} class.
 *
 * @version 0.20
 */
public class ConverterDelegate {
   private Node root = null;
   private Graphics2D g2D = null;
   private ConverterListener listener = null;
   private final Stack<TransformWrapper> transforms = new Stack<>();
   private final Stack<PaintWrapper> paints = new Stack<>();
   private final Stack<FontWrapper> fonts = new Stack<>();
   private final Stack<StrokeWrapper> strokes = new Stack<>();
   private final Stack<ClipWrapper> clips = new Stack<>();

   public ConverterDelegate() {
   }

   /**
    * Resets the ConverterDelegate.
    */
   public void reset() {
      clips.clear();
      strokes.clear();
      fonts.clear();
      paints.clear();
      transforms.clear();
   }

   /**
    * Set the ConverterListener to use for the conversion. The listener will be called at the beginning and end of each converted Node.
    *
    * @param listener the ConverterListener
    */
   public void setListener(ConverterListener listener) {
      this.listener = listener;
   }

   /**
    * Return the ConverterListener used for the conversion (may be null).
    *
    * @return the ConverterListener
    */
   public ConverterListener getListener() {
      return listener;
   }

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   public Node getRoot() {
      return root;
   }

   /**
    * Return the Graphics2D.
    *
    * @return the Graphics2D
    */
   public Graphics2D getGraphics2D() {
      return g2D;
   }

   /**
    * Set the CLip of a Node.
    *
    * @param node the Node
    */
   public void clip(Node node) {
      if (node.getClip() != null) {
         java.awt.Shape awtShape = JFXShapeUtilities.getShape(node.getClip());
         awtShape = JFXShapeUtilities.transformShape(node.getClip(), awtShape);
         clips.peek().setClip(awtShape);
         g2D.clip(awtShape);
      }
   }

   void startNode(Node node) {
      if (listener != null) {
         listener.startNode(g2D, node);
      }
      transforms.push(new TransformWrapper(g2D.getTransform()));
      paints.push(new PaintWrapper(g2D.getColor()));
      fonts.push(new FontWrapper(g2D.getFont()));
      strokes.push(new StrokeWrapper(g2D.getStroke()));
      clips.push(new ClipWrapper(g2D.getClip()));
   }

   void setStroke(Stroke stroke) {
      if (stroke != null) {
         strokes.peek().setStroke(stroke);
         g2D.setStroke(stroke);
      }
   }

   void setFont(Font font) {
      if (font != null) {
         fonts.peek().setFont(font);
         g2D.setFont(font);
      }
   }

   void setPaint(Paint paint) {
      if (paint != null) {
         paints.peek().setPaint(paint);
         g2D.setPaint(paint);
      } else {
         g2D.setPaint(null);
      }
   }

   void endNode(Node node) {
      if (listener != null) {
         listener.endNode(g2D, node);
      }
      if (!transforms.empty()) {
         TransformWrapper wrapper = transforms.pop();
         g2D.setTransform(wrapper.getTransform());
      }
      PaintWrapper wrapper = paints.pop();
      g2D.setPaint(wrapper.getOldPaint());
      FontWrapper fwrapper = fonts.pop();
      g2D.setFont(fwrapper.getOldFont());
      StrokeWrapper swrapper = strokes.pop();
      g2D.setStroke(swrapper.getOldStroke());
      ClipWrapper cwrapper = clips.pop();
      g2D.setClip(cwrapper.getOldClip());
   }

   void applyTranslation(double tX, double tY) {
      AffineTransform tr = AffineTransform.getTranslateInstance(tX, tY);
      g2D.transform(tr);
   }

   void applyScale(double scaleX, double scaleY) {
      AffineTransform tr = AffineTransform.getScaleInstance(scaleX, scaleY);
      g2D.transform(tr);
   }

   void applyRotation(double angle, double aX, double aY) {
      AffineTransform tr = AffineTransform.getRotateInstance(angle, aX, aY);
      g2D.transform(tr);
   }

   void applyTransform(AffineTransform tr) {
      g2D.transform(tr);
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param g2D the Graphics2D
    * @param root the root Node
    */
   public void convert(Graphics2D g2D, Node root) {
      this.g2D = g2D;
      this.root = root;
      AbstractConverter conv = getConverter(root);
      if (conv != null) {
         this.startNode(root);
         conv.applyTransforms();
         conv.convert();
         if (listener != null && root.getEffect() != null) {
            listener.applyEffect(g2D, root, root.getEffect());
         }
         if (root instanceof Parent) {
            Parent parent = (Parent) root;
            Iterator<Node> it = parent.getChildrenUnmodifiable().iterator();
            while (it.hasNext()) {
               Node child = it.next();
               if (child.isVisible()) {
                  convert(child);
               }
            }
         }
         if (listener != null && root.getEffect() != null) {
            listener.endEffect(g2D, root);
         }
         this.endNode(root);
      }
   }

   private AbstractConverter getConverter(Node node) {
      AbstractConverter conv = null;
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         conv = new ShapeConverter(this, shape);
      } else if (node instanceof Control) {
         Control control = (Control) node;
         conv = new ControlConverter(this, control);
      } else if (node instanceof Region) {
         Region region = (Region) node;
         conv = new RegionConverter(this, region);
      } else if (node instanceof ImageView) {
         ImageView view = (ImageView) node;
         conv = new ImageViewConverter(this, view);
      } else if (node instanceof Group) {
         Group group = (Group) node;
         conv = new GroupConverter(this, group);
      } else if (node instanceof SubScene) {
         SubScene subScene = (SubScene) node;
         conv = new SubSceneConverter(this, subScene);
      } else if (node instanceof Shape3D) {
         Shape3D shape = (Shape3D) node;
         conv = new Shape3DConverter(this, shape);
      }
      return conv;
   }

   private void convert(Node node) {
      AbstractConverter conv = getConverter(node);
      if (conv != null) {
         boolean isVisible = node.isVisible();
         if (conv.hasVisibility()) {
            if (!conv.isVisible()) {
               return;
            } else {
               isVisible = true;
            }
         }
         if (isVisible) {
            this.startNode(node);
            conv.applyTransforms();
            clip(node);
            if (listener != null) {
               listener.applyEffect(g2D, node, node.getEffect());
            }
            conv.convert();
            Node additionalNode = conv.getAdditionalNode();
            if (additionalNode != null && additionalNode.isVisible()) {
               convert(additionalNode);
            }
            Parent parent = conv.getParent();
            if (parent != null) {
               Iterator<Node> it = parent.getChildrenUnmodifiable().iterator();
               while (it.hasNext()) {
                  Node child = it.next();
                  convert(child);
               }
            }
            if (listener != null) {
               listener.endEffect(g2D, node);
            }
            this.endNode(node);
         }
      }
   }

   private static class StrokeWrapper {
      private Stroke stroke = null;
      private Stroke oldStroke = null;

      private StrokeWrapper(Stroke oldStroke) {
         this.oldStroke = oldStroke;
      }

      void setStroke(Stroke stroke) {
         this.stroke = stroke;
      }

      Stroke getOldStroke() {
         return oldStroke;
      }

      Stroke getStroke() {
         return stroke;
      }
   }

   private static class FontWrapper {
      private Font font = null;
      private Font oldFont = null;

      private FontWrapper(Font oldFont) {
         this.oldFont = oldFont;
      }

      void setFont(Font font) {
         this.font = font;
      }

      Font getOldFont() {
         return oldFont;
      }

      Font getFont() {
         return font;
      }
   }

   private static class ClipWrapper {
      private java.awt.Shape oldClip = null;
      private java.awt.Shape clip = null;

      private ClipWrapper(java.awt.Shape oldClip) {
         this.oldClip = oldClip;
      }

      java.awt.Shape getOldClip() {
         return oldClip;
      }

      void setClip(java.awt.Shape clip) {
         this.clip = clip;
      }

      java.awt.Shape getClip() {
         return clip;
      }
   }

   private static class TransformWrapper {
      private AffineTransform transform = null;

      private TransformWrapper(AffineTransform transform) {
         this.transform = transform;
      }

      AffineTransform getTransform() {
         return transform;
      }
   }

   private static class PaintWrapper {
      private Paint paint = null;
      private Paint oldPaint = null;

      private PaintWrapper(Paint oldPaint) {
         this.oldPaint = oldPaint;
      }

      void setPaint(Paint paint) {
         this.paint = paint;
      }

      Paint getOldPaint() {
         return oldPaint;
      }

      Paint getPaint() {
         return paint;
      }
   }
}
