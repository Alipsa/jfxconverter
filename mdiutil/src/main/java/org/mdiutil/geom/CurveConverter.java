/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2011, 2014, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class convert a set of Curves to Arcs.
 * <p>
 * To use it, you must:</p>
 * <ul>
 * <li>call {@link #endArc()} to force the end of the current Arc</li>
 * <li>call {@link #addCubicCurve(CubicCurve2D)} for each new CubicCurve</li>
 * <li>call {@link #addQuadCurve(QuadCurve2D)} for each new QuadCurve</li>
 * </ul>
 * <p>
 * After each order, retrieve the state of the converter and then the corresponding Arc
 * if the state is equal to END_ARC.</p>
 * <p>
 * Remark: if a curve is flat, the algorithm will create a Line and then end the current curve.</p>
 *
 * @version 0.8.8
 */
public class CurveConverter {
   private List<CurvePeer> curves;
   private List<Shape> shapes;
   private boolean allowCircles = false;
   public static final int NO_ARC = 0;
   public static final int CURRENT_ARC = 1;
   public static final int END_ARC = 2;
   protected int state;
   private static final double PERCENT = 0.9d;
   private static final double ASSUME_ZERO = 0.001d;
   private static final double MINIMAL_ANGLE = 10d;

   /**
    * Constructor of the converter. The initialization state is NO_ARC.
    */
   public CurveConverter() {
      curves = new ArrayList<>();
      shapes = new ArrayList<>();
      state = NO_ARC;
   }

   /**
    * Constructor of the converter. The initialization state is NO_ARC.
    *
    * @param allowCircles tyue if circles are allowed as results of the conversion
    */
   public CurveConverter(boolean allowCircles) {
      curves = new ArrayList<>();
      shapes = new ArrayList<>();
      state = NO_ARC;
      this.allowCircles = allowCircles;
   }

   /**
    * Clear this converter to allow the creation of new Curves.
    */
   public void clear() {
      curves = new ArrayList<>();
      shapes = new ArrayList<>();
      state = NO_ARC;
   }

   /**
    * Return the current state.
    *
    * @return the current state
    */
   public int getCurrentState() {
      return state;
   }

   /**
    * Return the conversion policy.
    *
    * @return true if circles are allowaed in the conversion
    */
   public boolean isAllowingCircles() {
      return allowCircles;
   }

   /**
    * Set the conversion policy.
    *
    * @param allowCircles true if circles are allowaed in the conversion
    */
   public void allowCircles(boolean allowCircles) {
      this.allowCircles = allowCircles;
   }

   /**
    * Return the last Shape issued from the conversion process. It can either be a Line2D or an Arc2D.
    *
    * @return the last Shape issued from the conversion process
    */
   public Shape getLastShape() {
      return shapes.get(shapes.size() - 1);
   }

   /**
    * Return the Shapes issued from the conversion process.
    * The associated Vector is composed of Line2D and Arc2D elements.
    *
    * @return the Shapes issued from the conversion process
    */
   public List<Shape> getShapes() {
      return shapes;
   }

   /**
    * Return true if this converter contain some converted Shapes.
    *
    * @return true if this converter contain some converted Shapes
    */
   public boolean hasShapes() {
      if (shapes == null) {
         return false;
      } else {
         return !shapes.isEmpty();
      }
   }

   /**
    * Force begin of Arc.
    *
    * @return the resulting state
    */
   private int beginArc() {
      if (state == CURRENT_ARC) {
         shapes.addAll(toArc2D());
      }
      state = CURRENT_ARC;
      curves.clear();

      return state;
   }

   /**
    * Force end of Arc.
    *
    * @return the resulting state
    */
   public int endArc() {
      if (state == CURRENT_ARC) {
         shapes.addAll(toArc2D());
         state = END_ARC;
      } else {
         state = NO_ARC;
      }

      curves.clear();

      return state;
   }

   /**
    * Add a CubicCurve to the curve Vector.
    *
    * @param cubic the CubicCurve
    * @return the resulting state
    */
   public int addCubicCurve(CubicCurve2D cubic) {
      // manage the state
      if ((state == NO_ARC) || (state == END_ARC)) {
         state = beginArc();
      } else {
         state = CURRENT_ARC;
      }

      Rectangle2D rec = cubic.getBounds2D();

      /* look if the CubicCurve is flat, in this case it is reduced to a Line,
       * and this is the end of the current arc.
       */
      if (cubic.getFlatness() < Math.max(rec.getWidth(), rec.getHeight()) / 100) {
         Point2D p0 = cubic.getP1();
         Point2D p1 = cubic.getP2();
         Shape s = new Line2D.Double(p0.getX(), p0.getY(), p1.getX(), p1.getY());
         if (state == CURRENT_ARC) {
            shapes.addAll(toArc2D());
         } else {
            shapes.add(s);
         }
         state = END_ARC;
      } else {
         // else assume it is a Circular Arc
         Point2D p0 = cubic.getP1();
         Point2D p1 = cubic.getCtrlP1();
         Point2D p2 = cubic.getCtrlP2();
         Point2D p3 = cubic.getP2();
         Vector2D v0 = new Vector2D(p0, p1).getNormal();
         Vector2D v2 = new Vector2D(p2, p3).getNormal();

         Point2D center = v0.getIntersection(p0, v2, p3);
         Arc2D arc;
         if (center != null) {
            double r = center.distance(p0);
            // TODO : need to understand why need to do - new Vector2D instead of new Vector2D
            double start = -new Vector2D(center, p0).getAngle() * 180 / Math.PI;
            double end = -new Vector2D(center, p3).getAngle() * 180 / Math.PI;
            CurvePeer peer = new CurvePeer(center, p0, p3, start, end, r);
            curves.add(peer);
         } else {
            state = END_ARC;
            shapes.addAll(toArc2D());
            curves.clear();
         }
      }

      return state;
   }

   /**
    * Add a QuadCurve to the Curve Vector.
    *
    * @param quad the QuadCurve
    * @return the resulting state
    */
   public int addQuadCurve(QuadCurve2D quad) {
      // manage the state
      if ((state == NO_ARC) || (state == END_ARC)) {
         state = beginArc();
      } else {
         state = CURRENT_ARC;
      }

      Rectangle2D rec = quad.getBounds2D();

      /* look if the QuadCurve is flat, in this case it is reduced to a Line,
       * and this is the end of the current arc.
       */
      if (quad.getFlatness() < Math.max(rec.getWidth(), rec.getHeight()) / 100) {
         Point2D p0 = quad.getP1();
         Point2D p1 = quad.getP2();
         Shape s = new Line2D.Double(p0.getX(), p0.getY(), p1.getX(), p1.getY());
         if (state == CURRENT_ARC) {
            shapes.addAll(toArc2D());
         } else {
            shapes.add(s);
         }
         state = END_ARC;
      } else {
         // else assume it is a Circular Arc
         Point2D p0 = quad.getP1();
         Point2D p1 = quad.getCtrlPt();
         Point2D p2 = quad.getP2();
         Vector2D v0 = new Vector2D(p0, p1).getNormal();
         Vector2D v2 = new Vector2D(p1, p2).getNormal();

         Point2D center = v0.getIntersection(p0, v2, p2);
         if (center != null) {
            double r = center.distance(p0);
            // TODO : need to understand why need to do - new Vector2D instead of new Vector2D
            double start = -new Vector2D(center, p0).getAngle() * 180 / Math.PI;
            double end = -new Vector2D(center, p2).getAngle() * 180 / Math.PI;
            CurvePeer peer = new CurvePeer(center, p0, p2, start, end, r);
            curves.add(peer);
         } else {
            state = END_ARC;
            shapes.addAll(toArc2D());
            curves.clear();
         }
      }

      return state;
   }

   /**
    * Convert the current curve Vector to a Vector of Arc2D.
    *
    * @return the converted Shapes
    */
   protected List<Shape> toArc2D() {
      List<Shape> arcs = new ArrayList<>(1);

      // positions of the first and last points of the curve
      Point2D firstPoint = new Point2D.Double();
      Point2D lastPoint = new Point2D.Double();

      AngleSegment angle = new AngleSegment(0, 0, AngleSegment.ANGLE_END);
      double ray = 0;
      Point2D startPoint = new Point2D.Double();
      Point2D endPoint = new Point2D.Double();
      Point2D center = new Point2D.Double();
      boolean first = true;

      Iterator<CurvePeer> it = curves.iterator();
      while (it.hasNext()) {
         CurvePeer peer = it.next();
         double startAnglePeer = Math.IEEEremainder(5 * Math.rint(peer.startAngle / 5), 360d);
         double endAnglePeer = Math.IEEEremainder(5 * Math.rint(peer.endAngle / 5), 360d);
         double rayPeer = peer.ray;
         Point2D centerPeer = peer.center;
         Point2D startPointPeer = peer.startPoint;
         Point2D endPointPeer = peer.endPoint;
         AngleSegment anglePeer = new AngleSegment(startAnglePeer, endAnglePeer, AngleSegment.ANGLE_END);

         if (first) {
            first = false;
            angle = anglePeer;
            ray = rayPeer;
            startPoint = startPointPeer;
            // first point of the arc
            firstPoint = startPoint;
            endPoint = endPointPeer;
            lastPoint = endPointPeer;
            center = centerPeer;
         } else if (canConcatenateArcs(angle, ray, center, anglePeer, rayPeer, centerPeer)) {
            if (ray < rayPeer) {
               ray = rayPeer;
               center = centerPeer;
            }
            endPoint = endPointPeer;
            lastPoint = endPoint;
            angle = angle.concatenate(anglePeer);
         } else {
            double startAngle = angle.angleStart;
            double extent = angle.angleExtent;

            if ((allowCircles) && (extent >= 359.9f)) {
               arcs.add(new Ellipse2D.Double(center.getX() - ray, center.getY() - ray,
                  2 * ray, 2 * ray));
            } else {
               arcs.add(getArc(new Point2D.Double(center.getX(), center.getY()),
                  ray, startAngle, extent, Arc2D.OPEN, firstPoint, lastPoint));
            }

            angle = anglePeer;
            ray = rayPeer;
            startPoint = startPointPeer;
            // first point of the arc
            firstPoint = startPoint;
            endPoint = endPointPeer;
            // last point of the arc
            lastPoint = endPointPeer;
            center = centerPeer;

            first = false;
         }
      }
      double startAngle = angle.angleStart;
      double extent = angle.angleExtent;

      if ((allowCircles) && (extent >= 359.9f)) {
         arcs.add(new Ellipse2D.Double(center.getX() - ray, center.getY() - ray, 2 * ray, 2 * ray));
      } else {
         arcs.add(getArc(new Point2D.Double(center.getX(), center.getY()), ray, startAngle, extent, Arc2D.OPEN, firstPoint, lastPoint));
      }

      return arcs;
   }

   private Arc2D getArc(Point2D center, double ray, double startAngle, double extent, int arcType,
      Point2D firstPoint, Point2D lastPoint) {
      /* ensures that the end and begin points of the new arc fit
       * with the first and end point of this arc.
       * First put the center on the normal line between the two extremities.
       */
      double ox = firstPoint.getX() + (lastPoint.getX() - firstPoint.getX()) / 2;
      double oy = firstPoint.getY() + (lastPoint.getY() - firstPoint.getY()) / 2;
      Point2D o = new Point2D.Double(ox, oy);
      Vector2D v = new Vector2D(firstPoint, lastPoint).getNormal();
      Point2D center2 = v.getNormalProjection(o, center);
      double ray1 = center2.distance(firstPoint);
      /* Then tweak the angles in orde to begin and end exactly on
       * the begin and end points
       */
      double startAngle1 = new Vector2D(center2, firstPoint).getAngle();
      double extent1 = new Vector2D(center2, lastPoint).getAngle() - startAngle1;
      startAngle1 = -startAngle1 * 180f / Math.PI;
      // need to be sure that we keep the sign of the extent
      if (Math.abs(extent) >= 360f) {
         extent1 = extent;
      } else {
         extent1 = -extent1 * 180f / Math.PI;
         if (extent1 > 360f) {
            extent1 -= 360f;
         } else if (extent1 < -360f) {
            extent1 += 360f;
         }
         if ((extent < 0) && (extent1 > 0)) {
            extent1 -= 360f;
         } else if ((extent > 0) && (extent1 < 0)) {
            extent1 += 360f;
         }
      }

      Arc2D.Double arc = new Arc2D.Double(center2.getX() - ray1, center2.getY() - ray1, 2 * ray1, 2 * ray1,
         startAngle1, extent1, arcType);
      return arc;
   }

   /**
    * Set to true if it is the two concecutive arcs can form one unique arc.
    */
   private boolean canConcatenateArcs(AngleSegment angle, double ray, Point2D center,
      AngleSegment angle1, double ray1, Point2D center1) {

      if ((ray < PERCENT * ray1) || (ray1 < PERCENT * ray)) {
         return false;
      }

      if ((Math.abs(center.getX()) > ASSUME_ZERO) && (Math.abs(center1.getX()) > ASSUME_ZERO)) {
         if ((Math.abs(center.getX()) < PERCENT * Math.abs(center1.getX()))
            || (Math.abs(center1.getX()) < PERCENT * Math.abs(center.getX()))) {
            return false;
         }
      }
      if ((Math.abs(center.getY()) > ASSUME_ZERO) && (Math.abs(center1.getY()) > ASSUME_ZERO)) {
         if ((Math.abs(center.getY()) < PERCENT * Math.abs(center1.getY()))
            || (Math.abs(center1.getY()) < PERCENT * Math.abs(center.getY()))) {
            return false;
         }
      }

      double a = Math.abs(angle.angleEnd - angle1.angleStart);
      double a1 = Math.abs(angle.angleStart - angle1.angleEnd);
      double a2 = Math.abs(angle.angleStart - angle1.angleStart);
      double a3 = Math.abs(angle.angleEnd - angle1.angleEnd);
      if (a > 360d) {
         a = 360d;
      } else if (a < -360d) {
         a = -360d;
      }
      if (a1 > 360d) {
         a1 = 360d;
      } else if (a1 < -360d) {
         a1 = -360d;
      }
      if (a2 > 360d) {
         a2 = 360d;
      } else if (a2 < -360d) {
         a2 = -360d;
      }
      if (a3 > 360d) {
         a3 = 360d;
      } else if (a3 < -360d) {
         a3 = -360d;
      }
      if ((a > MINIMAL_ANGLE) && (a1 > MINIMAL_ANGLE)
         && (a2 > MINIMAL_ANGLE) && (a3 > MINIMAL_ANGLE)) {
         return false;
      }

      return true;
   }

   private class CurvePeer {
      Point2D center;
      Point2D startPoint;
      Point2D endPoint;
      double ray;
      double startAngle;
      double endAngle;

      CurvePeer(Point2D center, Point2D startPoint, Point2D endPoint,
         double startAngle, double endAngle, double ray) {
         this.center = center;
         this.startPoint = startPoint;
         this.endPoint = endPoint;
         this.startAngle = startAngle;
         this.endAngle = endAngle;
         this.ray = ray;
      }
   }

   /**
    * Represents an Angle Segment.
    *
    * @version 0.7.23
    */
   public class AngleSegment {
      public double angleStart;
      public double angleExtent;
      public double angleEnd;
      public static final int ANGLE_END = 0;
      public static final int ANGLE_EXTENT = 1;

      public AngleSegment(double angleStart, double angleValue, int type) {
         if (type == ANGLE_EXTENT) {
            this.angleStart = angleStart;
            this.angleExtent = angleValue;
            this.angleEnd = angleStart + angleValue;
            if (angleEnd > 180d) {
               angleEnd = angleEnd - 360d;
            } else if (angleEnd < -180d) {
               angleEnd = angleEnd + 360d;
            }
         } else {
            this.angleStart = angleStart;
            this.angleExtent = angleValue - angleStart;
            this.angleEnd = angleValue;
            if (angleExtent > 180d) {
               angleExtent = angleExtent - 360d;
            } else if (angleExtent < -180d) {
               angleExtent = angleExtent + 360d;
            }
         }
         this.normalize();
      }

      /**
       * Concatenates an AngleSegment to this one.
       *
       * @param seg the AngleSegment
       * @return the resulting concatenation
       */
      public AngleSegment concatenate(AngleSegment seg) {
         this.normalize();
         seg.normalize();

         double startRes = this.angleStart;
         double extentRes = this.angleExtent + seg.angleExtent;
         return new AngleSegment(startRes, extentRes, ANGLE_EXTENT);
      }

      /**
       * Normalize an angle.
       * The resulting angle start is in the ]0; 360] segment.
       * The resulting angle angle is in the ]0; 360] segment.
       * The resulting angle extent is not greater than 360 or
       * less than -360 degrees.
       */
      public final void normalize() {
         if (angleStart > 360d) {
            angleStart = angleStart - 360d;
         } else if (angleStart < 0) {
            angleStart = angleStart + 360d;
         }
         if (angleExtent > 360d) {
            angleExtent = 360d;
         } else if (angleExtent < -360d) {
            angleExtent = -360d;
         }
         if (angleEnd > 360d) {
            //angleEnd = 360d;
            angleEnd = angleEnd - 360d;
         } else if (angleEnd < 0) {
            angleEnd = angleEnd + 360d;
         }
      }
   }
}
