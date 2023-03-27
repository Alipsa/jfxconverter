/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Vector2DTest.
 *
 * @author GIROD
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class Vector2DTest {
   private Point2D p0 = new Point2D.Double(0, 0);
   private Point2D p1 = new Point2D.Double(100, 0);
   private Point2D p2 = new Point2D.Double(100, 100);
   private Point2D p3 = new Point2D.Double(0, 100);
   private double delta = 0.0000001;

   public Vector2DTest() {
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
    * Test of getX and getY method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testCreator() {
      System.out.println("Vector2DTest : testCreator");

      Vector2D v1 = new Vector2D(p0, p1);
      assertEquals(100, v1.getX(), delta);
      assertEquals(0, v1.getY(), delta);
   }

   /**
    * Test of getScalarProduct method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testGetScalarProduct() {
      System.out.println("Vector2DTest : testScalarProduct");
      Vector2D v0 = new Vector2D(10, 15);
      Vector2D v1 = new Vector2D(2, -3);
      assertEquals(-25, v0.getScalarProduct(v1), delta);
   }

   /**
    * Test of minus and add method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testMinusAdd() {
      System.out.println("Vector2DTest : testMinusAdd");
      Vector2D v0 = new Vector2D(10, 15);
      Vector2D v1 = new Vector2D(2, -3);
      Vector2D v2 = v0.minus(v1);
      assertEquals(8, v2.x, delta);
      assertEquals(18, v2.y, delta);

      v2 = v0.add(v1);
      assertEquals(12, v2.x, delta);
      assertEquals(12, v2.y, delta);
   }

   /**
    * Test of rotate method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testRotate() {
      System.out.println("Vector2DTest : testRotate");
      Vector2D v0 = new Vector2D(1, 0);
      Vector2D v1 = v0.rotate(Math.PI / 4);
      double r = Math.cos(Math.PI / 4);
      assertEquals(r, v1.x, delta);
      assertEquals(r, v1.y, delta);
   }

   /**
    * Test of rotate method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testRotate2() {
      System.out.println("Vector2DTest : testRotate2");
      Vector2D v0 = new Vector2D(0, 1);
      Vector2D v1 = v0.rotate(Math.PI / 4);
      double r = Math.cos(Math.PI / 4);
      assertEquals(-r, v1.x, delta);
      assertEquals(r, v1.y, delta);
   }

   /**
    * Test of getAngle method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testGetAngle() {
      System.out.println("Vector2DTest : testGetAngle");
      Vector2D v0 = new Vector2D(p0, p1);
      double a = v0.getAngle() * 180 / Math.PI;
      assertEquals(0, a, delta);

      Vector2D v1 = new Vector2D(p1, p0);
      a = v1.getAngle() * 180 / Math.PI;
      assertEquals(180, a, delta);

      v1 = new Vector2D(p0, p2);
      a = v1.getAngle() * 180 / Math.PI;
      assertEquals(45, a, delta);
   }

   /**
    * Test of normalize method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testNormalize() {
      System.out.println("Vector2DTest : testNormalize");
      Vector2D v1 = new Vector2D(p1, p0);
      Vector2D v2 = v1.normalize();
      assertEquals(-1, v2.x, delta);
      assertEquals(0, v2.y, delta);

      v1 = new Vector2D(p0, p2);
      v2 = v1.normalize();
      double r = Math.cos(Math.PI / 4);
      assertEquals(r, v2.x, delta);
      assertEquals(r, v2.y, delta);
   }

   /**
    * Test of getNormal method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testGetNormal() {
      System.out.println("Vector2DTest : testGetNormal");
      Vector2D v1 = new Vector2D(p0, p1);
      Vector2D v2 = v1.getNormal();
      assertEquals(v2.x, 0, delta);
      assertEquals(v2.y, -1, delta);
   }

   /**
    * Test of getIntersection method, of class svglab.util.geom.Vector2D.
    */
   @Test
   public void testGetIntersection() {
      System.out.println("Vector2DTest : testGetIntersection");
      Vector2D v3 = new Vector2D(0, 1);
      Vector2D v4 = new Vector2D(1, 0);
      Point2D inter = v3.getIntersection(p3, v4, p1);
      assertEquals(0, inter.getX(), delta);
      assertEquals(0, inter.getY(), delta);
   }

   @Test
   public void testIsColinear() {
      System.out.println("Vector2DTest : testIsColinear");
      Point2D p0 = new Point2D.Double(22.69d, 57.57d);
      Point2D p1 = new Point2D.Double(30.66d, 57.57d);
      Vector2D v0 = new Vector2D(p0, p1);
      Vector2D v1 = new Vector2D(p1, p0);
      assertTrue(v0.isColinear(p0, v1, p1));
   }

   @Test
   public void testTransform() {
      System.out.println("Vector2DTest : testTransform");
      // test translation
      AffineTransform tr = new AffineTransform();
      tr.translate(50, 100);
      Vector2D v = new Vector2D(10, 30);
      Vector2D vr = v.transform(tr);
      assertEquals(v.getX(), vr.getX(), delta);
      assertEquals(v.getY(), vr.getY(), delta);
      // test scale
      tr.scale(2, 4);
      vr = v.transform(tr);
      assertEquals(2 * v.getX(), vr.getX(), delta);
      assertEquals(4 * v.getY(), vr.getY(), delta);
      // test rotation
      tr = new AffineTransform();
      tr.rotate(Math.PI / 2);
      vr = v.transform(tr);
      assertEquals(-v.getY(), vr.getX(), delta);
      assertEquals(v.getX(), vr.getY(), delta);
   }
}
