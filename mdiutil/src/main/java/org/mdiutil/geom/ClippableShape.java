/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2011, 2014, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a shape that can be clipped, as it is possible to do with an Area.
 * <p>
 * For know, this class makes heavy use of the {@link ShapeNormalizer} class, so you have to be
 * aware that the conversion can have some imperfections, due to the conversion from a set of curves to
 * arcs.
 * However, as this class is mostly useful for converting to graphic formats that don't deal well
 * with clipping paths, and considering that most of the times, these formats don't handle curves (for
 * example, see WMF format), this is not really a problem.</p>
 *
 * @version 0.9.1
 */
public class ClippableShape implements Shape {
   /**
    * The internal clipped Shape.
    */
   protected Shape shape;
   /**
    * {@value}: Constant used for coordinates equality. Its value is 0.0001.
    */
   public static final double ASSUME_ZERO = 0.0001d;

   /**
    * Create a new ClippableShape.
    *
    * @param shape the peer shape
    */
   public ClippableShape(Shape shape) {
      this.shape = shape;
   }

   /**
    * Perform the intersection of this shape with the area. The shape will becomes the
    * intersected shape.
    * <p>
    * The algorithm is the following {@link #intersect(Area)} :</p>
    * <ul>
    * <li>if the peer Shape of this Clippable shape is an area, just use the <i>intersect</i> method of the
    * Area class, that deals well in these cases</li>
    * <li>if not, it is a plain Shape and we are on our own :
    * <ul>
    * <li>first the shape is stripped to its possible CLOSE path segments, these being transformed
    * into plain LINETO, CUBICTO, and QUADTO segments</li>
    * <li>then we use {@link ShapeNormalizer} to normalize the resulting Shape and get the associated
    * Vector of elementary shapes : Line2D, Polygon2D, Polyline2D, ArC2D</li>
    * <li>then we get the associated Area for each of the associated segments :
    * <ul>
    * <li>each line of the Shape is transformed into a Polygon, from which the line is one of
    * the vertex of this polygon (the other dimension is arbitrary taken as 1 to have a corresponding
    * closed Shape, suitable to have a corresponding Area</li>
    * <li>arcs don't have to be transformed, as they are already suitable to have an Area</li>
    * </ul>
    * </li>
    * <li>finally we use the intersect method for each of these areas and retrieve the "good"
    * segments in the corresponding path.</li>
    * </ul>
    * </ul>
    *
    * @param rhs the area to intersect
    */
   public void intersect(Area rhs) {
      if (shape instanceof Area) {
         ((Area) shape).intersect(rhs);
      } else {
         // get a shape without CLOSE segments
         Shape inShape = getUnclosedShape(shape, true);
         ShapeNormalizer norm = new ShapeNormalizer();
         List<ShapeNormalizer.ConnectedShape> shapes = norm.convertConnectedPath(inShape);
         Iterator<ShapeNormalizer.ConnectedShape> it = shapes.iterator();
         GeneralPath path = new GeneralPath();
         while (it.hasNext()) {
            Shape outShape = null;
            ShapeNormalizer.ConnectedShape cshape = it.next();
            Shape sh = cshape.getShape();
            if (sh instanceof Line2D) {
               outShape = getInternalClippedShape(sh, rhs);
            } else if (sh instanceof Polygon2D) {
               outShape = getInternalClippedShape(sh, rhs);
            } else if (sh instanceof Polyline2D) {
               outShape = getInternalClippedShape(sh, rhs);
            } else if (sh instanceof Arc2D) {
               outShape = getInternalClippedArc((Arc2D) sh, rhs);
            }
            if (outShape != null) {
               path.append(outShape, cshape.isConnected());
            }
         }

         shape = path;
      }
   }

   /**
    * Perform the intersection of the area associated to this shape with the area, after transforming
    * this shape to an Area.
    *
    * @param rhs the area to intersect
    */
   public void intersectAsArea(Area rhs) {
      if (shape instanceof Area) {
         if (!rhs.contains(shape.getBounds2D())) {
            if (rhs.intersects(shape.getBounds2D())) {
               ((Area) shape).intersect(rhs);
            } else {
               shape = null;
            }
         }
      } else {
         Area area;
         if (shape instanceof Line2D) {
            // lines should be handled separately, because their area is empty. Our algorithm is to create very small
            // polygon using the normals on the edges of the lines, to use this polygon to compute the intersection,
            // and then to co,vert back to lines
            // first we create the path by using the normals
            Line2D line = (Line2D) shape;
            GeneralPath path = new GeneralPath();
            path.append(line, false);
            Point2D p1 = line.getP1();
            Point2D p2 = line.getP2();
            Vector2D v = new Vector2D(p1, p2);
            Vector2D vnorm = v.getNormal().normalize();
            Point2D p3 = vnorm.translate(p2);
            path.append(new Line2D.Double(p2, p3), true);
            Vector2D vinverse = v.getInverse();
            Point2D p4 = vinverse.translate(p3);
            path.append(new Line2D.Double(p3, p4), true);
            path.append(new Line2D.Double(p4, p1), true);
            path.closePath();

            // then  we perform the intersection on this path (now we are sure not to have an empty area)
            area = new Area(path);
            if (!rhs.contains(area.getBounds2D())) {
               if (rhs.intersects(area.getBounds2D())) {
                  area.intersect(rhs);
                  shape = area;
                  // and we normalize the result to have only simple geometric forms
                  ShapeNormalizer n = new ShapeNormalizer();
                  List<Shape> shapes = n.convertPath(shape);
                  if (shapes.size() == 1) {
                     // if there is only one shape, we make sure we convert it to a line if it is not a line
                     shape = shapes.get(0);
                     if (shape instanceof Polygon2D) {
                        Polygon2D pol = (Polygon2D) shape;
                        // this is to avoid to deform the line if it is vertical or horizontal, since
                        // our initial polygon has a width of 1
                        double deltaX = pol.getMaxX() - pol.getMinX();
                        if (Math.abs(deltaX) <= 1) {
                           deltaX = 1;
                        }
                        double deltaY = pol.getMaxY() - pol.getMinY();
                        if (Math.abs(deltaY) <= 1) {
                           deltaY = 1;
                        }
                        shape = new Line2D.Double(pol.getMinX(), pol.getMinY(), pol.getMinX() + deltaX, pol.getMinY() + deltaY);
                     }
                  } else {
                     // else we check for polygons and lines
                     path = new GeneralPath();
                     Iterator<Shape> it = shapes.iterator();
                     while (it.hasNext()) {
                        Shape _shape = it.next();
                        if (_shape instanceof Polygon2D) {
                           Polygon2D pol = (Polygon2D) _shape;
                           line = new Line2D.Double(pol.getMinX(), pol.getMinY(), pol.getMaxX(), pol.getMaxY());
                           path.append(line, false);
                        } else if (_shape instanceof Line2D) {
                           path.append((Line2D) _shape, false);
                        }
                     }
                     shape = path;
                  }
               } else {
                  shape = null;
               }
            }
         } else {
            area = new Area(shape);
            if (!rhs.contains(area.getBounds2D())) {
               if (rhs.intersects(area.getBounds2D())) {
                  area.intersect(rhs);
                  shape = area;
               } else {
                  shape = null;
               }
            }
         }

      }
   }

   /**
    * Return the peer shape.
    *
    * @return the peer shape
    */
   public Shape getPeerShape() {
      return shape;
   }

   /**
    * Creates a "virtual" polygon corresponding to two points.
    */
   private Polygon2D getPolygon(Point2D p0, Point2D p1) {
      if (p0.distance(p1) < ASSUME_ZERO) {
         return null;
      }
      Vector2D v0 = new Vector2D(p0, p1);
      Vector2D v1 = v0.getNormal();
      Point2D p2 = v1.translate(p1);
      Point2D p3 = v1.translate(p0);
      Polygon2D pol = new Polygon2D();
      pol.addPoint(p0);
      pol.addPoint(p1);
      pol.addPoint(p2);
      pol.addPoint(p3);

      return pol;
   }

   /**
    * get the clipped Shape corresponding to an arc.
    */
   private Shape getInternalClippedArc(Arc2D arc, Area rhs) {
      if (rhs.contains(arc.getBounds2D())) {
         return arc;
      }
      if (!rhs.intersects(arc.getBounds2D())) {
         return null;
      }

      Area area = new Area(arc);
      area.intersect(rhs);
      PathIterator pat = area.getPathIterator(null);
      ShapeNormalizer norm = new ShapeNormalizer();
      List<Shape> shapes = norm.convertPath(pat);
      GeneralPath path = new GeneralPath();
      Iterator<Shape> it = shapes.iterator();
      while (it.hasNext()) {
         Shape sh = it.next();
         if (sh instanceof Arc2D) {
            Arc2D newArc = (Arc2D) sh;
            normalizeArc(arc, newArc);
            path.append(newArc, false);
         }
      }

      return path;
   }

   /**
    * get the clipped Shape corresponding to a line, polylne, or polygon.
    */
   private Shape getInternalClippedShape(Shape shape, Area rhs) {
      PathIterator pi = shape.getPathIterator(null);
      GeneralPath gp = new GeneralPath();
      double coords[] = new double[6];
      double movx = 0, movy = 0;
      double curx = 0, cury = 0;
      double newx, newy;
      boolean connect = false;
      while (!pi.isDone()) {
         switch (pi.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO: {
               curx = movx = coords[0];
               cury = movy = coords[1];
               connect = false;
               break;
            }
            case PathIterator.SEG_LINETO: {
               newx = coords[0];
               newy = coords[1];
               Line2D.Double line = new Line2D.Double(curx, cury, newx, newy);
               Polygon2D pol = getPolygon(line.getP1(), line.getP2());
               if (pol != null) {
                  Area area = new Area(pol);
                  area.intersect(rhs);
                  Point2D.Double p = new Point2D.Double(curx, cury);
                  connect = addClippedFigure(p, gp, area, line, connect);
               }
               curx = newx;
               cury = newy;
               break;
            }
            case PathIterator.SEG_QUADTO: {
               newx = coords[2];
               newy = coords[3];
               curx = newx;
               cury = newy;
               break;
            }
            case PathIterator.SEG_CUBICTO: {
               newx = coords[4];
               newy = coords[5];
               curx = newx;
               cury = newy;
               break;
            }
            case PathIterator.SEG_CLOSE: {
               curx = movx;
               cury = movy;
               connect = false;
               break;
            }
         }
         pi.next();
      }

      return gp;
   }

   /**
    * This method is used to be sure that when an arc has been clipped, the resulting clipped arc
    * has the same direction as the initial arc.
    */
   private void normalizeArc(Arc2D iniArc, Arc2D newArc) {
      double angleExtent = 0;
      double iniExtent = 0;
      angleExtent = newArc.getAngleExtent();
      iniExtent = iniArc.getAngleExtent();
      double value = angleExtent * iniExtent;
      if (value < 0) {
         double angleStart = newArc.getAngleStart();
         angleStart = angleStart + angleExtent;
         angleExtent = -angleExtent;
         newArc.setAngleStart(angleStart);
         newArc.setAngleExtent(angleExtent);
      }
   }

   /**
    * This method is used to be sure that when a line has been clipped, the resulting clipped line
    * has the same direction as the initial line.
    */
   private void normalizeVector(Vector2D v0, Vector2D v1, Point2D pcur, Point2D pnew) {
      double d = v0.getAngle() - v1.getAngle();
      if (d > Math.PI) {
         d = d - 2 * Math.PI;
      } else if (d < -Math.PI) {
         d = d + 2 * Math.PI;
      }
      if (Math.abs(d) > Math.PI / 2d) {
         double x = pnew.getX();
         double y = pnew.getY();
         pnew.setLocation(pcur.getX(), pcur.getY());
         pcur.setLocation(x, y);
      }
   }

   /**
    * Only used to add a resulted clipped line.
    */
   private boolean addClippedFigure(Point2D curp, GeneralPath gp, Area area,
      Line2D line, boolean connect) {

      PathIterator pi = area.getPathIterator(null);
      double coords[] = new double[6];
      double movx = 0, movy = 0;
      double curx = curp.getX();
      double cury = curp.getY();
      double newx, newy;
      while (!pi.isDone()) {
         switch (pi.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO: {
               curx = movx = coords[0];
               cury = movy = coords[1];
               connect = false;
               break;
            }
            case PathIterator.SEG_LINETO: {
               newx = coords[0];
               newy = coords[1];
               Point2D p0 = line.getP1();
               Point2D p1 = line.getP2();
               Vector2D v0 = new Vector2D(p0, p1);
               Point2D pcur = new Point2D.Double(curx, cury);
               Point2D pnew = new Point2D.Double(newx, newy);
               Vector2D v1 = new Vector2D(pcur, pnew);
               if (v0.isColinear(p0, v1, pcur)) {
                  normalizeVector(v0, v1, pcur, pnew);
                  Line2D.Double line2 = new Line2D.Double(curx, cury, newx, newy);
                  gp.append(line2, connect);
               }
               curx = newx;
               cury = newy;
               connect = true;
               break;
            }
            case PathIterator.SEG_QUADTO:
               break;
            case PathIterator.SEG_CUBICTO:
               break;
            case PathIterator.SEG_CLOSE:
               newx = movx;
               newy = movy;
               Point2D p0 = line.getP1();
               Point2D p1 = line.getP2();
               Vector2D v0 = new Vector2D(p0, p1);
               Point2D pcur = new Point2D.Double(curx, cury);
               Point2D pnew = new Point2D.Double(newx, newy);
               Vector2D v1 = new Vector2D(pcur, pnew);
               if (v0.isColinear(p0, v1, pcur)) {
                  normalizeVector(v0, v1, pcur, pnew);
                  Line2D.Double line2 = new Line2D.Double(curx, cury, newx, newy);
                  gp.append(line2, connect);
               }
               curx = newx;
               cury = newy;
               connect = false;
               break;
         }
         pi.next();
      }
      curp.setLocation(curx, cury);

      return connect;
   }

   /**
    * Return the associated unclosed shape.
    *
    * @param shape the shape to convert to an unclosed Shape
    * @param closeProperty true if the CLOSE segment will result in a Line, false if it will be omitted
    * @return the associated unclosed shape
    */
   public Shape getUnclosedShape(Shape shape, boolean closeProperty) {
      PathIterator pi = shape.getPathIterator(null);
      GeneralPath gp = new GeneralPath();
      double coords[] = new double[6];
      double movx = 0, movy = 0;
      double curx = 0, cury = 0;
      double newx, newy;
      boolean connect = false;
      while (!pi.isDone()) {
         switch (pi.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO: {
               curx = movx = coords[0];
               cury = movy = coords[1];
               connect = true;
               break;
            }
            case PathIterator.SEG_LINETO: {
               newx = coords[0];
               newy = coords[1];
               Line2D.Double line = new Line2D.Double(curx, cury, newx, newy);
               gp.append(line, connect);
               curx = newx;
               cury = newy;
               connect = true;
               break;
            }
            case PathIterator.SEG_QUADTO: {
               newx = coords[2];
               newy = coords[3];
               QuadCurve2D quad = new QuadCurve2D.Double(curx, cury, coords[0], coords[1], newx, newy);
               gp.append(quad, connect);
               curx = newx;
               cury = newy;
               connect = true;
               break;
            }
            case PathIterator.SEG_CUBICTO: {
               newx = coords[4];
               newy = coords[5];
               CubicCurve2D cubic = new CubicCurve2D.Double(curx, cury, coords[0], coords[1], coords[2], coords[3], newx, newy);
               gp.append(cubic, connect);
               curx = newx;
               cury = newy;
               connect = true;
               break;
            }
            case PathIterator.SEG_CLOSE: {
               if (closeProperty) {
                  Line2D.Double line = new Line2D.Double(curx, cury, movx, movy);
                  gp.append(line, connect);
                  curx = movx;
                  cury = movy;
               }
               connect = false;
               break;
            }
         }
         pi.next();
      }

      return gp;
   }

   /**
    * Defer to {@link Shape#contains(Rectangle2D)} with the resulting clipped Shape.
    *
    * @param r the Rectangle
    * @return true if the clipped Shape contains the Rectangle
    */
   @Override
   public boolean contains(Rectangle2D r) {
      return shape.contains(r);
   }

   /**
    * Defer to {@link Shape#contains(Point2D)} with the resulting clipped Shape.
    *
    * @param p the Point
    * @return true if the clipped Shape contains the Point
    */
   @Override
   public boolean contains(Point2D p) {
      return shape.contains(p);
   }

   /**
    * Defer to {@link Shape#contains(double, double)} with the resulting clipped Shape.
    *
    * @param x the x coordinate of the Point
    * @param y the y coordinate of the Point
    * @return true if the clipped Shape contains the Point
    */
   @Override
   public boolean contains(double x, double y) {
      return shape.contains(x, y);
   }

   /**
    * Defer to {@link Shape#contains(double, double, double, double)} with the resulting clipped Shape.
    *
    * @param x the x coordinate of the rectangle
    * @param y the y coordinate of the rectangle
    * @param w the width of the rectangle
    * @param h the height of the rectangle
    * @return true if the clipped Shape contains the rectangle
    */
   @Override
   public boolean contains(double x, double y, double w, double h) {
      return shape.contains(x, y, w, h);
   }

   /**
    * Return the bounds of the resulting clipped Shape.
    *
    * @return the bounds
    */
   @Override
   public Rectangle getBounds() {
      return shape.getBounds();
   }

   /**
    * Return the bounds of the resulting clipped Shape.
    *
    * @return the bounds
    */
   @Override
   public Rectangle2D getBounds2D() {
      return shape.getBounds2D();
   }

   @Override
   public PathIterator getPathIterator(AffineTransform at) {
      return shape.getPathIterator(at);
   }

   @Override
   public PathIterator getPathIterator(AffineTransform at, double flatness) {
      return shape.getPathIterator(at, flatness);
   }

   @Override
   public boolean intersects(Rectangle2D r) {
      return shape.intersects(r);
   }

   @Override
   public boolean intersects(double x, double y, double w, double h) {
      return shape.intersects(x, y, w, h);
   }
}
