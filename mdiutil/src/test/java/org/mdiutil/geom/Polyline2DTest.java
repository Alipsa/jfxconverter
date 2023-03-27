/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Polyline2DTest.
 *
 * @author GIROD
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class Polyline2DTest {
   private static final float ASSUME_ZERO = 0.0001f;

   public Polyline2DTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   private Polyline2D getPolyline1() {
      Polyline2D pol = new Polyline2D();
      pol.addPoint(0, 0);
      pol.addPoint(0f, 10.7f);
      pol.addPoint(10.2f, 10.7f);
      pol.addPoint(10.2f, 0f);
      return pol;
   }

   private Polyline2D getPolyline2() {
      Polyline2D pol = new Polyline2D();
      pol.addPoint(0, 0);
      pol.addPoint(0f, 10.7f);
      pol.addPoint(10.2f, 10.7f);
      pol.addPoint(10.2f, 0f);
      pol.addPoint(0, 0);
      return pol;
   }

   private Polyline2D getPolyline3() {
      Polyline2D pol = new Polyline2D();
      pol.addPoint(0, 0);
      pol.addPoint(0f, 10.7f);
      return pol;
   }

   /**
    * Test of getPolyline2D method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testGetPolygon2D() {
      System.out.println("Polyline2DTest : testGetPolyline2D");
      Polyline2D poly = getPolyline1();
      Polygon2D polg = poly.getPolygon2D();
      assertEquals("wrong number of points", poly.npoints, polg.npoints);
      for (int i = 0; i < poly.npoints; i++) {
         assertEquals("bad X coordinate " + i, poly.xpoints[i], polg.xpoints[i], ASSUME_ZERO);
         assertEquals("bad Y coordinate " + i, poly.ypoints[i], polg.ypoints[i], ASSUME_ZERO);
      }

      poly = getPolyline2();
      polg = poly.getPolygon2D();
      assertEquals("wrong number of points", poly.npoints - 1, polg.npoints);
      for (int i = 0; i < poly.npoints - 1; i++) {
         assertEquals("bad X coordinate " + i, poly.xpoints[i], polg.xpoints[i], ASSUME_ZERO);
         assertEquals("bad Y coordinate " + i, poly.ypoints[i], polg.ypoints[i], ASSUME_ZERO);
      }
   }

   /**
    * Test of addPoint method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testAddPoint() {
      System.out.println("Polyline2DTest : testAddPoint");
      Polyline2D poly = getPolyline1();
      Polyline2D poly1 = (Polyline2D) poly.clone();
      poly.addPoint(12f, -10f);
      assertEquals("wrong number of points", poly1.npoints + 1, poly.npoints);
      assertEquals("wrong number of points", poly1.npoints + 1, poly.xpoints.length);
      assertEquals("wrong number of points", poly1.npoints + 1, poly.ypoints.length);
      Rectangle2D rec = poly.getBounds2D();
      float xmax = (float) rec.getMaxX();
      float ymax = (float) rec.getMaxY();
      float xmin = (float) rec.getMinX();
      float ymin = (float) rec.getMinY();
      rec = poly1.getBounds2D();
      float xmin1 = (float) rec.getMinX();
      float ymin1 = (float) rec.getMinY();
      float xmax1 = (float) rec.getMaxX();
      float ymax1 = (float) rec.getMaxY();
      assertEquals("wrong xmin for bounds", xmin1, xmin, ASSUME_ZERO);
      assertEquals("wrong ymin for bounds", -10f, ymin, ASSUME_ZERO);
      assertEquals("wrong xmax for bounds", 12f, xmax, ASSUME_ZERO);
      assertEquals("wrong ymax for bounds", ymax1, ymax, ASSUME_ZERO);
      rec = new Rectangle2D.Float(0f, -10f, 5f, 13f);
      assertTrue("must intersect (5f, -5f, 5f, 10f)", poly.intersects(rec));
   }

   /**
    * Test of addPoint method.
    */
   @Test
   public void testAddPoint2() {
      System.out.println("Polyline2DTest : testAddPoint2");
      Polyline2D polg = new Polyline2D();
      polg.addPoint(12f, -10f);
      polg.addPoint(20f, -20f);
      assertEquals("wrong number of points", 2, polg.npoints);
      assertEquals("wrong number of points", 2, polg.xpoints.length);
      assertEquals("wrong number of points", 2, polg.ypoints.length);
   }

   /**
    * Test of contains method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testContains() {
      System.out.println("Polyline2DTest : testContains");
      Polyline2D poly = getPolyline1();
      assertFalse("must not contain (5, 15)", poly.contains(5f, 15f));
   }

   /**
    * Test of getBounds2D method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testGetBounds2D() {
      System.out.println("Polyline2DTest : testGetBounds2D");
      Polyline2D poly = getPolyline1();
      Rectangle2D rec = poly.getBounds2D();
      float xmax = (float) rec.getMaxX();
      float ymax = (float) rec.getMaxY();
      float xmin = (float) rec.getMinX();
      float ymin = (float) rec.getMinY();
      assertEquals("wrong xmin for bounds", 0f, xmin, ASSUME_ZERO);
      assertEquals("wrong ymin for bounds", 0f, ymin, ASSUME_ZERO);
      assertEquals("wrong xmax for bounds", 10.2f, xmax, ASSUME_ZERO);
      assertEquals("wrong ymax for bounds", 10.7f, ymax, ASSUME_ZERO);
   }

   /**
    * Test of intersects method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testIntersects() {
      System.out.println("Polyline2DTest : testIntersects");
      Polyline2D poly = getPolyline1();
      Rectangle2D rec = new Rectangle2D.Float(15f, 15f, 5f, 5f);
      assertFalse("must not intersect (15f, 15f, 5f, 5f)", poly.intersects(rec));
      rec = new Rectangle2D.Float(5f, 5f, 3f, 3f);
      assertTrue("must intersect (5f, 5f, 3f, 3f)", poly.intersects(rec));
      rec = new Rectangle2D.Float(-2f, 5f, 13f, 5f);
      assertTrue("must intersect (-2f, 5f, 13f, 5f)", poly.intersects(rec));
   }

   /**
    * Test of getPathIterator method, of class girod.util.geom.Polyline2D.
    */
   @Test
   public void testGetPathIterator() {
      System.out.println("Polyline2DTest : testGetPathIterator");
      Polyline2D poly = getPolyline1();

      PathIterator it = poly.getPathIterator(new AffineTransform());
      int count = 0;
      while (!it.isDone()) {
         count++;
         it.next();
      }
      assertEquals("wrong number of segments in iterator", 4, poly.npoints);
      it = poly.getPathIterator(new AffineTransform());
      float[] coords = new float[6];
      int type = 0;
      int i = 0;
      while (!it.isDone()) {
         type = it.currentSegment(coords);
         if (i == 0) {
            assertTrue("must be a MOVETO", type == PathIterator.SEG_MOVETO);
         } else {
            assertTrue("must be a LINETO", type == PathIterator.SEG_LINETO);
         }
         i++;
         it.next();
      }
   }

   /**
    * Test of the number of points in the array.
    */
   @Test
   public void testPointsArray() {
      System.out.println("Polyline2DTest : testPointsArray");
      Polyline2D poly = getPolyline3();
      assertEquals("Number of Points", 2, poly.npoints);
      assertEquals("Number of Points", 2, poly.xpoints.length);
      assertEquals("Number of Points", 2, poly.ypoints.length);
   }

   /**
    * Test of the number of points in the array.
    */
   @Test
   public void testPointsArray2() {
      System.out.println("Polyline2DTest : testPointsArray2");
      Polyline2D poly = getPolyline1();
      assertEquals("Number of Points", 4, poly.npoints);
      assertEquals("Number of Points", 4, poly.xpoints.length);
      assertEquals("Number of Points", 4, poly.ypoints.length);
   }
}
