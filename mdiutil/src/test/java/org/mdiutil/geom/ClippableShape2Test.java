/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * ClippableShapeTest.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class ClippableShape2Test {
   private static final double DELTA = 0.5f;

   public ClippableShape2Test() {
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

   /**
    * Test of intersect method : case when the Shape is entirely inside the clip.
    */
   @Test
   public void testNoIntersection() {
      System.out.println("ClippableShapeTest : testNoIntersection");
      Arc2D.Float arc = new Arc2D.Float(0, 0, 10f, 10f, 90f, 360f, Arc2D.OPEN);
      Rectangle2D.Float clip = new Rectangle2D.Float(-200f, -200f, 400f, 400f);
      ClippableShape in = new ClippableShape(arc);
      in.intersect(new Area(clip));
      Shape out = in.getPeerShape();
      ShapeNormalizer norm = new ShapeNormalizer();
      List<ShapeNormalizer.ConnectedShape> v = norm.convertConnectedPath(out);
      assertEquals("Should have one Shape", 1, v.size());
      Shape shape = v.get(0).getShape();
      assertTrue("Shape must be an Arc2D", shape instanceof Arc2D);

      Arc2D arc1 = (Arc2D) shape;
      assertEquals(arc1.getAngleStart(), 90d, DELTA);
      assertEquals(arc1.getAngleExtent(), 360d, DELTA);
   }

   /**
    * Test of intersect method. The Shape is a circle, and it is cut in half by the clip.
    * The result leads to two arcs.
    */
   @Test
   public void testIntersectionHalfCircle() {
      System.out.println("ClippableShapeTest : testIntersectionHalfCircle");
      Arc2D.Float arc = new Arc2D.Float(-50, -50, 100f, 100f, 90f, 360f, Arc2D.OPEN);
      Rectangle2D.Float clip = new Rectangle2D.Float(-100f, -100f, 200f, 100f);
      ClippableShape in = new ClippableShape(arc);
      in.intersect(new Area(clip));
      Shape out = in.getPeerShape();
      ShapeNormalizer norm = new ShapeNormalizer();
      List<ShapeNormalizer.ConnectedShape> v = norm.convertConnectedPath(out);
      assertEquals("Should have two Shapes", 2, v.size());
      Shape shape = v.get(0).getShape();
      assertTrue("Shape must be an Arc2D", shape instanceof Arc2D);

      Arc2D arc1 = (Arc2D) shape;
      assertEquals(arc1.getAngleStart(), 90d, DELTA);
      assertEquals(arc1.getAngleExtent(), 90d, DELTA);

      shape = v.get(1).getShape();
      assertTrue("Shape must be connected", v.get(1).isConnected());
      arc1 = (Arc2D) shape;
      assertEquals(arc1.getAngleStart(), 0d, DELTA);
      assertEquals(arc1.getAngleExtent(), 90d, DELTA);
   }

   /**
    * Test of intersect method. The Shape is a RoundRectangle.
    */
   @Test
   public void testIntersectionRoundRectangle() {
      System.out.println("ClippableShapeTest : testIntersectionRoundRectangle");
      RoundRectangle2D rec = new RoundRectangle2D.Double(245.669, 321.260, 132d, 57d, 5d, 5d);
      Rectangle2D clip = new Rectangle2D.Double(0, 0, 377.95d, 377.95d);
      ClippableShape in = new ClippableShape(rec);
      in.intersect(new Area(clip));
      Shape out = in.getPeerShape();
      ShapeNormalizer norm = new ShapeNormalizer();
      List<ShapeNormalizer.ConnectedShape> v = norm.convertConnectedPath(out);

      assertEquals("Should have 8 Shapes", 8, v.size());
      ShapeNormalizer.ConnectedShape cshape = v.get(0);
      Shape shape = cshape.getShape();
      assertTrue("Shape 0 must be a Line2D", shape instanceof Line2D);
      cshape = v.get(1);
      shape = cshape.getShape();
      assertTrue("Shape 1 must be an Arc2D", shape instanceof Arc2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(2);
      shape = cshape.getShape();
      assertTrue("Shape 2 must be a Line2D", shape instanceof Line2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(3);
      shape = cshape.getShape();
      assertTrue("Shape 3 must be an Arc2D", shape instanceof Arc2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(4);
      shape = cshape.getShape();
      assertTrue("Shape 4 must be a Line2D", shape instanceof Line2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(5);
      shape = cshape.getShape();
      assertTrue("Shape 5 must be an Arc2D", shape instanceof Arc2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(6);
      shape = cshape.getShape();
      assertTrue("Shape 6 must be a Line2D", shape instanceof Line2D);
      assertTrue("Shape must be connected", cshape.isConnected());
      cshape = v.get(7);
      shape = cshape.getShape();
      assertTrue("Shape 7 must be an Arc2D", shape instanceof Arc2D);
      assertTrue("Shape must be connected", cshape.isConnected());
   }
}
