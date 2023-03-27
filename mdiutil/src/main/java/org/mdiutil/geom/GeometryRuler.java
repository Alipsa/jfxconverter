/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2011, 2014, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

/**
 * This class computes areas and perimeters of any Shape.
 *
 * @version 0.8.6
 */
public class GeometryRuler {
   private static GeometryRuler ruler = null;
   private double flatness = 0.1f;

   private GeometryRuler() {
   }

   /**
    * Return the unique GeometryRuler instance.
    *
    * @return the unique GeometryRuler instance
    */
   public static GeometryRuler getRuler() {
      if (ruler == null) {
         ruler = new GeometryRuler();
      }
      return ruler;
   }

   /**
    * Set the flatness value (which will be used to compute CubicCurves areas and perimeters.
    *
    * @param flatness the flatness value
    */
   public void setFlatness(double flatness) {
      this.flatness = flatness;
   }

   /**
    * Return the flatness value.
    *
    * @return the flatness value
    */
   public double getFlatness() {
      return flatness;
   }

   /**
    * Return the area of a Shape. This is a convenience method, equivalent to setting the flatness and
    * calling the {@link #getShapeArea(Shape)}.
    *
    * @param shape the Shape
    * @param flatness the flatness value (see {@link #setFlatness(double)})
    * @return the shape area
    * @see #getShapeArea(Shape)
    */
   public float getShapeArea(Shape shape, double flatness) {
      this.flatness = flatness;
      return getShapeArea(shape);
   }

   /**
    * This method compute the area of a shape. The Shape can be:
    * <ul>
    * <li>a complex Shape : the Shape will be transformed to a set of lines and Polylines,
    * by using the {@link ShapeNormalizer} class. The area of the Shape is the
    * sum of the areas of all the sub-Shapes computed by this class</li>
    * <li>a Polygon2D : equivalent to {@link #getPolygon2DArea(Polygon2D)}</li>
    * <li>an Arc2D : equivalent to {@link #getArc2DArea(Arc2D)}</li>
    * <li>an Ellipse2D : equivalent to {@link #getEllipse2DArea(Ellipse2D)}</li>
    * </ul>
    *
    * @param shape the shape
    * @return the shape area
    */
   public float getShapeArea(Shape shape) {
      float _surf = 0;
      Area area = new Area(shape);

      if (shape instanceof Ellipse2D) {
         _surf = getEllipse2DArea((Ellipse2D) shape);
      } else if (shape instanceof Arc2D) {
         _surf = getArc2DArea((Arc2D) shape);
      } else if (shape instanceof Polygon2D) {
         _surf = getPolygon2DArea((Polygon2D) shape);
      } else if (shape instanceof Rectangle2D) {
         Rectangle2D rec = (Rectangle2D) shape;
         _surf = (float) (rec.getHeight() * rec.getWidth());
      } else {
         AffineTransform affinetransform = new AffineTransform();
         PathIterator pathiterator = area.getPathIterator(affinetransform, flatness);
         ShapeNormalizer shapeNorm = new ShapeNormalizer();
         List<Shape> shapes = shapeNorm.convertPath(pathiterator);

         for (Iterator iterator = shapes.iterator(); iterator.hasNext();) {
            Shape sh = (Shape) iterator.next();
            if (sh instanceof Polygon2D) {
               _surf += getPolygon2DArea((Polygon2D) sh);
            } else if (sh instanceof Polyline2D) {
               Polyline2D pol = (Polyline2D) sh;
               Polygon2D pol1 = new Polygon2D();
               for (int i = 0; i < pol.npoints; i++) {
                  pol1.addPoint(pol.xpoints[i], pol.ypoints[i]);
               }
               _surf += getPolygon2DArea((Polygon2D) pol1);
            } else if (sh instanceof Arc2D) {
               _surf += getArc2DArea((Arc2D) sh);
            }
         }
      }
      return _surf;
   }

   /**
    * Return an Arc2D area.
    *
    * @param arc the Arc2D
    * @return the Arc2D area
    */
   public float getArc2DArea(Arc2D arc) {
      // calculate surface
      // FIXED : used width and height, but it should be width / 2 and height / 2
      float radius = (float) (arc.getWidth() + arc.getHeight()) / 4f;
      float angle = (float) (Math.abs(arc.getAngleExtent()));
      float area = (float) (Math.PI * (angle / 360f) * Math.pow(radius, 2f));

      return area;
   }

   /**
    * Return an Ellipse2D area.
    *
    * @param el the Ellipse2D
    * @return the Ellipse2D area
    */
   public float getEllipse2DArea(Ellipse2D el) {
      // calculate surface
      // FIXED : used width and height, but it should be width / 2 and height / 2
      double a = el.getWidth() / 2;
      double b = el.getHeight() / 2;
      float area = (float) (Math.PI * a * b);

      return area;
   }

   /**
    * Return a Polygon area using the following formula:
    * <pre>
    * area = 1/2 * (x1*y2 - x2*y1 + x2*y3 - x3*y2 ...
    *        + xn-1*yn - xn*yn-1 + xn*y1 - x1*yn)
    * </pre>
    *
    * @param pol the Polygon2D
    * @return the Polygon2D area
    */
   public float getPolygon2DArea(Polygon2D pol) {
      float area = 0;
      for (int i = 0; i < pol.npoints; i++) {
         float x1, y1, x2, y2;

         if (i < pol.npoints - 1) {
            x1 = pol.xpoints[i];
            x2 = pol.xpoints[i + 1];
            y1 = pol.ypoints[i];
            y2 = pol.ypoints[i + 1];
            area += x1 * y2 - y1 * x2;
         } else {
            x1 = pol.xpoints[i];
            y1 = pol.ypoints[i];
            x2 = pol.xpoints[0];
            y2 = pol.ypoints[0];
            area += x1 * y2 - y1 * x2;
         }
      }

      area = Math.abs(area / 2);

      return area;
   }

   /**
    * Return the length of a Shape. This is a convenience method, equivalent to setting the flatness and
    * calling the {@link #getShapeLength(Shape)}.
    *
    * @param shape the Shape
    * @param flatness the flatness value (see {@link #setFlatness(double)})
    * @return the Shape length
    * @see #getShapeLength(Shape)
    */
   public float getShapeLength(Shape shape, double flatness) {
      this.flatness = flatness;
      return getShapeLength(shape);
   }

   /**
    * This method compute the length (perimeter) of a shape.
    * The Shape can be:
    * <ul>
    * <li>a complex Shape: the Shape will be transformed to a set of lines and Polylines,
    * by using the {@link ShapeNormalizer} class. The legnth of the Shape is the
    * sum of the lengts of all the sub-Shapes computed by this class</li>
    * <li>a Polygon2D: equivalent to {@link #getPolygon2DLength(Polygon2D)}</li>
    * <li>a Polyline2D: equivalent to {@link #getPolyline2DLength(Polyline2D)}</li>
    * <li>a Line2D: equivalent to {@link #getPolyline2DLength(Polyline2D)}, the Polyline2D
    * corresponding to the Line2D </li>
    * <li>an Arc2D: equivalent to {@link #getArc2DLength(Arc2D)}</li>
    * <li>a Ellipse2D: equivalent to {@link #getEllipse2DLength(Ellipse2D)}</li>
    * </ul>
    *
    * @param shape the Shape
    * @return the Shape perimeter
    */
   public float getShapeLength(Shape shape) {
      float _length = 0;

      if (shape instanceof Ellipse2D) {
         _length = getEllipse2DLength((Ellipse2D) shape);
      } else if (shape instanceof Arc2D) {
         _length = getArc2DLength((Arc2D) shape);
      } else if (shape instanceof Polygon2D) {
         _length = getPolygon2DLength((Polygon2D) shape);
      } else if (shape instanceof Polyline2D) {
         _length = getPolyline2DLength((Polyline2D) shape);
      } else if (shape instanceof Line2D) {
         _length = getPolyline2DLength(new Polyline2D((Line2D) shape));
      } else {
         AffineTransform affinetransform = new AffineTransform();
         PathIterator pathiterator = shape.getPathIterator(affinetransform);
         ShapeNormalizer shapeNorm = new ShapeNormalizer();
         List<Shape> shapes = shapeNorm.convertPath(pathiterator);

         // calculate distance of all the Shapes sides
         for (Iterator iterator = shapes.iterator(); iterator.hasNext();) {
            Shape sh = (Shape) iterator.next();
            if (sh instanceof Polygon2D) {
               Polygon2D pol = (Polygon2D) sh;
               _length += getPolygon2DLength(pol);
            } else if (sh instanceof Polyline2D) {
               Polyline2D pol = (Polyline2D) sh;
               _length += getPolyline2DLength(pol);
            } else if (sh instanceof Arc2D) {
               Arc2D arc = (Arc2D) sh;
               _length += getArc2DLength(arc);
            }
         }
      }

      return _length;
   }

   /**
    * Return an Arc2D length.
    *
    * @param arc the Arc2D
    * @return the Arc2D length
    */
   public float getArc2DLength(Arc2D arc) {
      float radius = (float) (arc.getWidth() + arc.getHeight()) / 4f;
      float angle = Math.abs((float) (arc.getAngleExtent()));
      float length = (float) (2 * Math.PI * (angle / 360f) * radius);

      return length;
   }

   /**
    * Return an Ellipse2D length (perimeter).
    *
    * The approximative formula used for the calculation is :
    * <pre>PI * sqrt(2*(a*a + b*b) - (a - b)*(a-b)/2)</pre>
    * where a and be are the major radius and minor radius of he ellipsis.
    *
    * @param el the Ellipse2D
    * @return the Ellipse2D length
    */
   public float getEllipse2DLength(Ellipse2D el) {
      // FIXED : used width and height, but it should be width / 2 and height / 2
      double a = el.getWidth() / 2;
      double b = el.getHeight() / 2;
      float length = (float) (Math.PI * Math.sqrt(2 * (a * a + b * b) - (a - b) * (a - b) / 2));
      return length;
   }

   /**
    * Return a Polygon2D length;
    *
    * @param pol the Polygon2D
    * @return the Polygon2D length
    */
   public float getPolygon2DLength(Polygon2D pol) {
      float dx = 0;
      float dy = 0;
      float length = 0;

      // compute all segments distance
      for (int j = 0; j < pol.npoints - 1; j++) {
         dx = pol.xpoints[j + 1] - pol.xpoints[j];
         dy = pol.ypoints[j + 1] - pol.ypoints[j];
         length += (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
      }
      // now add the last segment (going back to first point)
      dx = pol.xpoints[pol.npoints - 1] - pol.xpoints[0];
      dy = pol.ypoints[pol.npoints - 1] - pol.ypoints[0];
      length += (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

      return length;
   }

   /**
    * Return a Polyline2D length;
    *
    * @param pol the Polyline2D
    * @return the Polyline2D length
    */
   public float getPolyline2DLength(Polyline2D pol) {
      float dx;
      float dy;
      float length = 0;

      for (int j = 0; j < pol.npoints - 1; j++) {
         dx = pol.xpoints[j + 1] - pol.xpoints[j];
         dy = pol.ypoints[j + 1] - pol.ypoints[j];
         length += (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
      }

      return length;
   }
}
