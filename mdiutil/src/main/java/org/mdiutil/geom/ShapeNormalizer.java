/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2011, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class converts a Shape in an Array of Elementary Shapes.
 * The transformation algorithm follows these rules:
 * <ul>
 * <li>CubicCurves and QuadCurves are transformed into Arcs or Circles (depending
 * if this normalizer allows to convert to circles)</li>
 * <li>Segments are transformed into Polygon2D, Polyline2D, or Lines</li>
 * </ul>
 *
 * The algorithm try to concatenate as much Arcs or lines as possible.
 *
 * Remark: this class uses internally a {@link CurveConverter}.
 *
 * @version 0.9.1
 */
public class ShapeNormalizer {
   private final CurveConverter curveconv;
   private int state;
   private boolean isSurfaceMode = false;
   private boolean allowCircles = false;
   public final static double ASSUME_ZERO = 0.01d;

   /**
    * Creates a new normalizer. The default surface mode is false {@link #setSurfaceMode}.
    */
   public ShapeNormalizer() {
      curveconv = new CurveConverter();
   }

   /**
    * Creates a new normalizer. The default surface mode is false {@link #setSurfaceMode}.
    *
    * @param allowCircles true if circles are allowed in the result
    */
   public ShapeNormalizer(boolean allowCircles) {
      curveconv = new CurveConverter(allowCircles);
      this.allowCircles = allowCircles;
   }

   /**
    * Set the surface filling mode of this normalizer.
    * <ul>
    * <li>If mode is true, the normalizer will add the associated enclosed {@link Polygon2D}
    * to the shapes Vector for each encountered Path that has at least one <code>CubicCurve2D</code> or
    * <code>QuadCurve2D</code></li>
    * <li>If not, it will add no added shape to the surface</li>
    * </ul>
    * <p>
    * This ensures that the interior of any Area will be filled, even if this area is constituted
    * of curves.</p>
    *
    * @param mode true if the surface filling mode is active
    */
   public void setSurfaceMode(boolean mode) {
      this.isSurfaceMode = mode;
   }

   /**
    * Return true if the surface filling mode of this normalizer is active.
    *
    * @return true if the surface filling mode of this normalizer is active
    * @see #setSurfaceMode
    */
   public boolean getSurfaceMode() {
      return isSurfaceMode;
   }

   /**
    * Return true if circles are allowed in the result.
    *
    * @return true if circles are allowed in the result
    */
   public boolean isAllowingCircles() {
      return allowCircles;
   }

   /**
    * Set if circles are allowed in the result
    *
    * @param allowCircles true if circles are allowed in the result
    */
   public void allowCircles(boolean allowCircles) {
      this.allowCircles = allowCircles;
      curveconv.allowCircles(allowCircles);
   }

   /**
    * Convert any Shape in a Vector of elementary Arcs, Polygons, or Lines.
    *
    * @param shape the input Shape
    * @return the resulting connected Shapes list
    */
   public List<ConnectedShape> convertConnectedPath(Shape shape) {
      return convertConnectedPath(shape, new AffineTransform());
   }

   /**
    * Convert any Shape in a Vector of elementary Arcs, Polygons, Polylines, or Lines.
    *
    * @param shape the input Shape
    * @param affine the AffineTransform
    * @return the resulting connected Shapes list
    */
   public List<ConnectedShape> convertConnectedPath(Shape shape, AffineTransform affine) {
      if (ShapeUtilities.simpleShapeAsTransform(shape, affine)) {
         Shape s = ShapeUtilities.createTransformedShape(shape, affine);
         List<ConnectedShape> v = new ArrayList<>(1);
         v.add(new ConnectedShape(s, false));
         return v;

      } else {
         PathIterator it = getPathIterator(shape, affine, false, 0);
         return convertConnectedPath(it);
      }
   }

   /**
    * Convert any Shape in a Vector of elementary Arcs, Polygons, , Polylines, or Lines.
    *
    * Note that the possible connection of segments in the input Shape is lost in the conversion. To keep the connection between
    * the Shapes, you should use the {@link #convertConnectedPath(Shape)} method.
    *
    * @param shape the input Shape
    * @return the resulting Shape list
    */
   public List<Shape> convertPath(Shape shape) {
      return convertPath(shape, new AffineTransform());
   }

   /**
    * Convert any Shape in a Vector of elementary Arcs, Polygons, Polylines, or Lines.
    *
    * Note that the possible connection of segments in the input Shape is lost in the conversion. To keep the connection between
    * the Shapes, you should use the {@link #convertConnectedPath(Shape, AffineTransform)} method.
    *
    * @param shape the input Shape
    * @param affine the AffineTransform
    * @return the resulting Shape list
    */
   public List<Shape> convertPath(Shape shape, AffineTransform affine) {
      return convertPath(shape, affine, false, 0);
   }

   /**
    * Convert any Shape in a Vector of elementary Arcs, Ellipse, Polygons, Polylines, or Lines.
    * <p>
    * The Shape is converted to its associated PathIterator :</p>
    * <ul>
    * <li>with the flatness value if the flatnessProp property is true</li>
    * <li>else without flatness value</li>
    * </ul>
    *
    * If the Shape is already an Arc, Ellipse, Polygon, Polyline, or Line, and if it is
    * possible, we keep a simple Shape and we don't use a PathIterator to transform the Shape.
    *
    * @param shape the input Shape
    * @param affine the AffineTransform
    * @param flatnessProp true if Shapes will be flattened
    * @param flatness the flatness value
    * @return the resulting Shape list
    */
   public List<Shape> convertPath(Shape shape, AffineTransform affine, boolean flatnessProp, double flatness) {
      if (ShapeUtilities.simpleShapeAsTransform(shape, affine)) {
         Shape s = ShapeUtilities.createTransformedShape(shape, affine);
         List<Shape> v = new ArrayList<>(1);
         v.add(s);
         return v;
      } else {
         PathIterator it = getPathIterator(shape, affine, flatnessProp, flatness);
         return convertPath(it);
      }
   }

   /**
    * Return a Polygon corresponding to a list of Points.
    *
    * @param v the list of Points
    * @return the Polygon
    */
   private Polygon2D toPolygon(List<Point2D> v) {
      if (v.size() == 1) {
         return null;
      }
      Polygon2D pol = new Polygon2D();
      for (int i = 0; i < v.size(); i++) {
         Point2D p = v.get(i);
         pol.addPoint((float) p.getX(), (float) p.getY());
      }
      return pol;
   }

   /**
    * Return a Line or Polyline corresponding to a list of Points.
    *
    * @param v the list of Points
    * @return the line or Polyline
    */
   private Shape toPolyline(List<Point2D> v) {
      if (v.size() == 1) {
         return null;
      } else if (v.size() == 2) {
         Line2D line = new Line2D.Float(v.get(0), v.get(1));
         return line;
      } else {
         Polyline2D pol = new Polyline2D();
         for (int i = 0; i < v.size(); i++) {
            Point2D p = v.get(i);
            pol.addPoint((float) p.getX(), (float) p.getY());
         }
         return pol;
      }
   }

   /**
    * Convert any PathIterator in a Vector of elementary Arcs, Polygons, Polylines, or Lines.
    *
    * Note that the possible connection of segments in the input PathIterator is lost in the conversion. To keep the connection between
    * the Shapes, you should use the {@link #convertConnectedPath(PathIterator)} method.
    *
    * @param pathiterator the PathIterator
    * @return the resulting Shape list
    */
   public List<Shape> convertPath(PathIterator pathiterator) {
      List<ConnectedShape> wshapes = convertConnectedPath(pathiterator);
      List<Shape> shapes = new ArrayList<>(wshapes.size());
      Iterator<ConnectedShape> it = wshapes.iterator();
      while (it.hasNext()) {
         ConnectedShape wshape = it.next();
         shapes.add(wshape.shape);
      }
      return shapes;
   }

   /**
    * Convert any PathIterator in a Vector of elementary Arcs, Polygons, Polylines, or Lines.
    *
    * @param pathiterator the PathIterator
    * @return the resulting connected Shapes list
    */
   public List<ConnectedShape> convertConnectedPath(PathIterator pathiterator) {
      Point2D p = null;
      boolean firstPt = false;
      boolean previousIsCurve = false;
      curveconv.clear();
      List<ConnectedShape> shapes = new ArrayList<>(1);

      float af[] = new float[6];
      // the Vector containing all the shapes
      List<ConnectedShape> vector = new ArrayList<>();
      // the Vector representing the last Polygon2D or Polyline2D
      List<Point2D> poly = new ArrayList<>(1);
      // a figure begins with a MOVE order and ends with another MOVE order or a CLOSE order

      for (; !pathiterator.isDone(); pathiterator.next()) {
         int j = pathiterator.currentSegment(af);
         switch (j) {
            default:
               break;

            case PathIterator.SEG_MOVETO:
               previousIsCurve = false;
               // MOVE to a new Point
               if (!poly.isEmpty()) {
                  addPoly(firstPt, p, vector, poly);
               }

               state = curveconv.getCurrentState();

               if (state == CurveConverter.END_ARC) {
                  if (curveconv.hasShapes()) {
                     vector.addAll(ConnectedShape.createWrappedShapes(curveconv.getShapes(), false));
                  }
                  curveconv.clear();
               }

               p = new Point2D.Double(af[0], af[1]);
               firstPt = true;
               break;

            case PathIterator.SEG_CUBICTO:
               // creates a CubicCurve
               CubicCurve2D cubic = new CubicCurve2D.Float((float) p.getX(), (float) p.getY(), af[0], af[1], af[2], af[3], af[4], af[5]);
               curveconv.addCubicCurve(cubic);
               previousIsCurve = true;

               state = curveconv.getCurrentState();
               if (state == CurveConverter.END_ARC) {
                  vector.add(new ConnectedShape(curveconv.getLastShape(), true));
                  curveconv.clear();
               }

               if ((firstPt) && (isSurfaceMode)) {
                  poly.add(p);
               }
               p = new Point2D.Double(af[4], af[5]);
               if (isSurfaceMode) {
                  poly.add(p);
               } else if (!poly.isEmpty()) {
                  addPoly(firstPt, p, vector, poly);
               }
               firstPt = false;
               break;

            case PathIterator.SEG_QUADTO:
               // creates a QuadCurve
               QuadCurve2D quad = new QuadCurve2D.Float((float) p.getX(), (float) p.getY(), af[0], af[1], af[2], af[3]);
               curveconv.addQuadCurve(quad);
               previousIsCurve = true;

               state = curveconv.getCurrentState();
               if (state == CurveConverter.END_ARC) {
                  vector.add(new ConnectedShape(curveconv.getLastShape(), true));
               }
               if ((firstPt) && (isSurfaceMode)) {
                  poly.add(p);
               }
               p = new Point2D.Double(af[2], af[3]);
               if (isSurfaceMode) {
                  poly.add(p);
               } else if (!poly.isEmpty()) {
                  addPoly(firstPt, p, vector, poly);
               }
               // firstPt = true
               firstPt = false;
               break;

            case PathIterator.SEG_LINETO:
               // creates a Line
               Point2D p2 = new Point2D.Float(af[0], af[1]);
               state = curveconv.getCurrentState();
               // FIXED: conversion of Arcs sometimes incorrect
               if ((state == CurveConverter.CURRENT_ARC) && (distance(p, p2) > ASSUME_ZERO)) {
                  curveconv.endArc();
                  if (curveconv.getLastShape() != null) {
                     vector.add(new ConnectedShape(curveconv.getLastShape(), true));
                     curveconv.clear();
                  }
               }
               if (previousIsCurve) {
                  previousIsCurve = false;
                  poly.add(p);
               }

               if (distance(p, p2) > ASSUME_ZERO) {
                  if (firstPt) {
                     poly.add(p);
                  }
                  p = new Point2D.Double(af[0], af[1]);
                  poly.add(p);
                  firstPt = false;
               }
               break;

            case PathIterator.SEG_CLOSE:
               // Close the current figure
               previousIsCurve = false;
               state = curveconv.getCurrentState();
               if (state == CurveConverter.CURRENT_ARC) {
                  curveconv.endArc();
                  if (curveconv.getLastShape() != null) {
                     vector.addAll(ConnectedShape.createWrappedShapes(curveconv.getShapes(), true));
                  }
               }
               if (!poly.isEmpty()) {
                  addPoly(firstPt, p, vector, poly, true);
               }
               firstPt = false;
               break;
         }
      }

      if (!poly.isEmpty()) {
         addPoly(firstPt, p, vector, poly);
      }

      if (curveconv.getCurrentState() == CurveConverter.CURRENT_ARC) {
         curveconv.endArc();
         if (curveconv.getLastShape() != null) {
            vector.addAll(ConnectedShape.createWrappedShapes(curveconv.getShapes(), true));
         }
      }

      // iterates through the Shapes
      Iterator<ConnectedShape> it = vector.iterator();
      while (it.hasNext()) {
         ConnectedShape sh = it.next();
         shapes.add(sh);
      }
      return shapes;
   }

   /**
    * Add a new Polygon or Polyline to the Shapes Vector.
    *
    * @param shapes the connected Shapes Vector
    * @param poly the Polygon points vector
    */
   private void addPoly(boolean firstPoint, Point2D p, List<ConnectedShape> shapes, List<Point2D> poly) {
      if (isSurfaceMode) {
         addPoly(firstPoint, p, shapes, poly, true);
      } else {
         addPoly(firstPoint, p, shapes, poly, false);
      }
      poly.clear();
   }

   /**
    * Add a new Polygon or Polyline to the Shapes Vector.
    *
    * @param shapes the connected Shapes Vector
    * @param poly the Polygon points vector
    * @param close true if the figure must be closed
    */
   private void addPoly(boolean firstPoint, Point2D p, List<ConnectedShape> shapes, List<Point2D> poly, boolean close) {
      if (close) {
         Polygon2D pol = toPolygon(poly);
         if (pol != null) {
            shapes.add(new ConnectedShape(pol, true));
         }
      } else {
         Shape shape = toPolyline(poly);
         if (shape != null) {
            shapes.add(new ConnectedShape(shape, true));
         }
      }
      poly.clear();
   }

   private double distance(double x, double y) {
      return Math.sqrt(x * x + y * y);
   }

   private double distance(Point2D p0, Point2D p1) {
      return distance(p0.getX() - p1.getX(), p0.getY() - p1.getY());
   }

   private PathIterator getPathIterator(Shape shape, AffineTransform affine, boolean flatnessProp,
      double flatness) {
      if (flatnessProp) {
         return shape.getPathIterator(affine, flatness);
      } else {
         return shape.getPathIterator(affine);
      }
   }

   /**
    * This class wraps a Shape with a boolean indicating if the Shape is connected to the previous Shape.
    *
    * @since 0.9.1
    */
   public static class ConnectedShape {
      private Shape shape = null;
      private boolean connected = false;

      private ConnectedShape(Shape shape, boolean connected) {
         this.shape = shape;
         this.connected = connected;
      }

      /**
       * Return the Shape.
       *
       * @return the Shape
       */
      public Shape getShape() {
         return shape;
      }

      /**
       * Return true is connected to the previous Shape.
       *
       * @return true is connected to the previous Shape
       */
      public boolean isConnected() {
         return connected;
      }

      /**
       * Utility method to create a list of ConnectedShapes.
       *
       * @param shapes the Shapes
       * @param connected true if the first Shape of the list must be connected
       * @return the list of ConnectedShapes
       */
      private static List<ConnectedShape> createWrappedShapes(List<Shape> shapes, boolean connected) {
         List<ConnectedShape> wrappedShapes = new ArrayList<>(shapes.size());
         for (int i = 0; i < shapes.size(); i++) {
            if (i == 0) {
               wrappedShapes.add(new ConnectedShape(shapes.get(i), connected));
            } else {
               wrappedShapes.add(new ConnectedShape(shapes.get(i), true));
            }
         }
         return wrappedShapes;
      }
   }
}
