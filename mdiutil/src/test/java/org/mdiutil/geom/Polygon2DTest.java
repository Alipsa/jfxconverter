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
import java.awt.Polygon;
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
 * Polygon2DTest.
 *
 * @author GIROD
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class Polygon2DTest {
   private static final float ASSUME_ZERO = 0.0001f;

   public Polygon2DTest() {
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

   private Polygon2D getPolygon1() {
      Polygon2D pol = new Polygon2D();
      pol.addPoint(0, 0);
      pol.addPoint(0f, 10.7f);
      pol.addPoint(10.2f, 10.7f);
      pol.addPoint(10.2f, 0f);
      return pol;
   }

   /**
    * Test of getPolyline2D method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testGetPolyline2D() {
      System.out.println("Polygon2DTest : testGetPolyline2D");
      Polygon2D polg = getPolygon1();
      Polyline2D poly = polg.getPolyline2D();
      assertEquals("wrong number of points", polg.npoints + 1, poly.npoints);
      for (int i = 0; i < polg.npoints; i++) {
         assertEquals("bad X coordinate " + i, polg.xpoints[i], poly.xpoints[i], ASSUME_ZERO);
         assertEquals("bad Y coordinate " + i, polg.ypoints[i], poly.ypoints[i], ASSUME_ZERO);
      }
      assertEquals("bad X coordinate " + poly.npoints, polg.xpoints[0],
         poly.xpoints[poly.npoints - 1], ASSUME_ZERO);
      assertEquals("bad Y coordinate " + poly.npoints, polg.ypoints[0],
         poly.ypoints[poly.npoints - 1], ASSUME_ZERO);
   }

   /**
    * Test of getPolygon method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testGetPolygon() {
      System.out.println("Polygon2DTest : testGetPolygon");
      Polygon2D polg = getPolygon1();
      Polygon pol = polg.getPolygon();
      assertEquals("wrong number of points", polg.npoints, pol.npoints);
      for (int i = 0; i < pol.npoints; i++) {
         assertEquals("bad X coordinate " + i, (int) polg.xpoints[i], pol.xpoints[i]);
         assertEquals("bad Y coordinate " + i, (int) polg.ypoints[i], pol.ypoints[i]);
      }
   }

   /**
    * Test of addPoint method.
    */
   @Test
   public void testAddPoint() {
      System.out.println("Polygon2DTest : testAddPoint");
      Polygon2D polg = getPolygon1();
      Polygon2D polg1 = (Polygon2D) polg.clone();
      polg.addPoint(12f, -10f);
      assertEquals("wrong number of points", polg1.npoints + 1, polg.npoints);
      Rectangle2D rec = polg.getBounds2D();
      float xmax = (float) rec.getMaxX();
      float ymax = (float) rec.getMaxY();
      float xmin = (float) rec.getMinX();
      float ymin = (float) rec.getMinY();
      rec = polg1.getBounds2D();
      float xmin1 = (float) rec.getMinX();
      float ymin1 = (float) rec.getMinY();
      float xmax1 = (float) rec.getMaxX();
      float ymax1 = (float) rec.getMaxY();
      assertEquals("wrong xmin for bounds", xmin1, xmin, ASSUME_ZERO);
      assertEquals("wrong ymin for bounds", -10f, ymin, ASSUME_ZERO);
      assertEquals("wrong xmax for bounds", 12f, xmax, ASSUME_ZERO);
      assertEquals("wrong ymax for bounds", ymax1, ymax, ASSUME_ZERO);
      rec = new Rectangle2D.Float(0f, -10f, 5f, 13f);
      assertTrue("must intersect (5f, -5f, 5f, 10f)", polg.intersects(rec));
      assertTrue("must contain (5, -1)", polg.contains(5f, -1f));
   }

   /**
    * Test of addPoint method.
    */
   @Test
   public void testAddPoint2() {
      System.out.println("Polygon2DTest : testAddPoint2");
      Polygon2D polg = new Polygon2D();
      polg.addPoint(12f, -10f);
      polg.addPoint(20f, -20f);
      assertEquals("wrong number of points", 2, polg.npoints);
      assertEquals("wrong number of points", 2, polg.xpoints.length);
      assertEquals("wrong number of points", 2, polg.ypoints.length);
   }

   /**
    * Test of contains method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testContains() {
      System.out.println("Polygon2DTest : testContains");
      Polygon2D polg = getPolygon1();
      assertTrue("must contain (5, 5)", polg.contains(5f, 5f));
      assertFalse("must not contain (5, 15)", polg.contains(5f, 15f));
   }

   /**
    * Test of getBounds2D method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testGetBounds2D() {
      System.out.println("Polygon2DTest : testGetBounds2D");
      Polygon2D polg = getPolygon1();
      Rectangle2D rec = polg.getBounds2D();
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
    * Test of intersects method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testIntersects() {
      System.out.println("Polygon2DTest : testIntersects");
      Polygon2D polg = getPolygon1();
      Rectangle2D rec = new Rectangle2D.Float(15f, 15f, 5f, 5f);
      assertFalse("must not intersect (15f, 15f, 5f, 5f)", polg.intersects(rec));
      rec = new Rectangle2D.Float(5f, 5f, 3f, 3f);
      assertTrue("must intersect (5f, 5f, 3f, 3f)", polg.intersects(rec));
      rec = new Rectangle2D.Float(-2f, 5f, 13f, 5f);
      assertTrue("must intersect (-2f, 5f, 13f, 5f)", polg.intersects(rec));
   }

   /**
    * Test of getPathIterator method, of class girod.util.geom.Polygon2D.
    */
   @Test
   public void testGetPathIterator() {
      System.out.println("Polygon2DTest : testGetPathIterator");
      Polygon2D polg = getPolygon1();

      PathIterator it = polg.getPathIterator(new AffineTransform());
      int count = 0;
      while (!it.isDone()) {
         count++;
         it.next();
      }
      assertEquals("wrong number of segments in iterator", 4, polg.npoints);
      it = polg.getPathIterator(new AffineTransform());
      float[] coords = new float[6];
      int type = 0;
      int i = 0;
      while (!it.isDone()) {
         type = it.currentSegment(coords);
         if (i == 0) {
            assertTrue("must be a MOVETO", type == PathIterator.SEG_MOVETO);
         } else if (i == count - 1) {
            assertTrue("must be a CLOSE", type == PathIterator.SEG_CLOSE);
         } else {
            assertTrue("must be a LINETO", type == PathIterator.SEG_LINETO);
         }
         i++;
         it.next();
      }
   }
}
