/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class has the same behavior than {@link Polygon2D}, except that the figure is not closed.
 *
 * @version 0.8.6
 */
public class Polyline2D extends Polygon2D {
   protected static final float ASSUME_ZERO = 0.001f;

   /**
    * Constructor.
    */
   public Polyline2D() {
      super();
   }

   /**
    * Constructor.
    *
    * @param xpoints an array of <i>x</i> coordinates
    * @param ypoints an array of <i>y</i> coordinates
    * @param npoints the total number of points in the <code>Polyline2D</code>
    * @exception NegativeArraySizeException if the value of
    * <code>npoints</code> is negative.
    * @exception IndexOutOfBoundsException if <code>npoints</code> is
    * greater than the length of <code>xpoints</code>
    * or the length of <code>ypoints</code>.
    * @exception NullPointerException if <code>xpoints</code> or
    * <code>ypoints</code> is <code>null</code>.
    */
   public Polyline2D(float[] xpoints, float[] ypoints, int npoints) {
      super(xpoints, ypoints, npoints);
   }

   /**
    * Constructor.
    *
    * @param xpoints an array of <i>x</i> coordinates
    * @param ypoints an array of <i>y</i> coordinates
    * @param npoints the total number of points in the <code>Polyline2D</code>
    * @exception NegativeArraySizeException if the value of <code>npoints</code> is negative.
    * @exception IndexOutOfBoundsException if <code>npoints</code> is greater than the length of <code>xpoints</code>
    * or the length of <code>ypoints</code>.
    * @exception NullPointerException if <code>xpoints</code> or <code>ypoints</code> is <code>null</code>.
    */
   public Polyline2D(int xpoints[], int ypoints[], int npoints) {
      super(xpoints, ypoints, npoints);
   }

   /**
    * Constructor.
    *
    * @param line the line
    */
   public Polyline2D(Line2D line) {
      super();
      this.npoints = 2;
      this.xpoints = new float[2];
      this.ypoints = new float[2];
      xpoints[0] = (float) line.getX1();
      xpoints[1] = (float) line.getX2();
      ypoints[0] = (float) line.getY1();
      ypoints[1] = (float) line.getY2();
      calculatePath();
   }

   @Override
   public Object clone() {
      Polyline2D pol = new Polyline2D();
      for (int i = 0; i < npoints; i++) {
         pol.addPoint(xpoints[i], ypoints[i]);
      }
      return pol;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Point p) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(double x, double y) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(int x, int y) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Point2D p) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(double x, double y, double w, double h) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Rectangle2D r) {
      return false;
   }

   /**
    * Returns an iterator object that iterates along the boundary of this
    * <code>Polygon</code> and provides access to the geometry
    * of the outline of this <code>Polygon</code>. An optional
    * {@link AffineTransform} can be specified so that the coordinates
    * returned in the iteration are transformed accordingly.
    *
    * @param at an optional <code>AffineTransform</code> to be applied to the
    * coordinates as they are returned in the iteration, or
    * <code>null</code> if untransformed coordinates are desired
    * @return a {@link PathIterator} object that provides access to the
    * geometry of this <code>Polygon</code>.
    */
   @Override
   public PathIterator getPathIterator(AffineTransform at) {
      if (path == null) {
         return null;
      } else {
         return path.getPathIterator(at);
      }
   }

   /* get the associated {@link Polygon2D}.
     * This method take care that may be the last point can
     * be equal to the first. In that case it must not be included in the Polygon,
     * as polygons declare their first point only once.
    */
   public Polygon2D getPolygon2D() {
      Polygon2D pol = new Polygon2D();
      for (int i = 0; i < npoints - 1; i++) {
         pol.addPoint((float) xpoints[i], (float) ypoints[i]);
      }
      Point2D.Double p0
         = new Point2D.Double(xpoints[0], ypoints[0]);
      Point2D.Double p1
         = new Point2D.Double(xpoints[npoints - 1], ypoints[npoints - 1]);

      if (p0.distance(p1) > ASSUME_ZERO) {
         pol.addPoint((float) xpoints[npoints - 1], (float) ypoints[npoints - 1]);
      }

      return pol;
   }
}
