/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2011, 2012, 2016, 2017, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
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
 * ShapeNormalizerTest.
 *
 * @version 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class ShapeNormalizerTest {
   private double delta = 0.001;

   public ShapeNormalizerTest() {
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
    * Test of convertPath method : angle of 30 degrees. test Conversion of [0, 30] arc.
    */
   @Test
   public void testConvertArc1() {
      System.out.println("ShapeNormalizerTest : testConvertArc1");
      ShapeNormalizer norm = new ShapeNormalizer();

      Arc2D.Float arc = new Arc2D.Float(100f, 100f, 20f, 20f, 0, 30f, Arc2D.OPEN);

      GeneralPath path = new GeneralPath(arc); // the path is a set of lines and curves (no arcs anymore)
      List<Shape> vec = norm.convertPath(path); // convert the path, verify if its the same arc
      // we verify if the converted path is a single arc identical to the first
      assertEquals(1, vec.size());
      Object o = vec.get(0);
      if (o instanceof Arc2D) {
         Arc2D arc2 = (Arc2D) o;
         assertEquals(arc.getCenterX(), arc2.getCenterX(), delta);
         assertEquals(arc.getCenterY(), arc2.getCenterY(), delta);
         assertEquals(arc.getArcType(), arc2.getArcType());
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      } else {
         fail();
      }
   }

   /**
    * Test of convertPath method : angle of 180 degrees. test Conversion of [120, 180] arc.
    */
   @Test
   public void testConvertArc2() {
      System.out.println("ShapeNormalizerTest : testConvertArc2");
      ShapeNormalizer norm = new ShapeNormalizer();

      Arc2D.Float arc = new Arc2D.Float(100f, 100f, 20f, 20f, 120f, 180f, Arc2D.OPEN);
      GeneralPath path = new GeneralPath(arc); // the path is a set of lines and curves (no arcs anymore)
      List<Shape> vec = norm.convertPath(path); // convert the path, verify if its the same arc
      // we verify if the converted path is a single arc identical to the first
      assertEquals(1, vec.size());
      Object o = vec.get(0);
      if (o instanceof Arc2D) {
         Arc2D arc2 = (Arc2D) o;
         assertEquals(arc.getCenterX(), arc2.getCenterX(), delta);
         assertEquals(arc.getCenterY(), arc2.getCenterY(), delta);
         assertEquals(arc.getArcType(), arc2.getArcType());
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      } else {
         fail();
      }
   }

   /**
    * Test of convertPath method : angle of 180 degrees. test Conversion of [-120, 270] arc.
    */
   @Test
   public void testConvertArc3() {
      System.out.println("ShapeNormalizerTest : testConvertArc3");
      ShapeNormalizer norm = new ShapeNormalizer();

      Arc2D.Float arc = new Arc2D.Float(100f, 100f, 40f, 40f, -120f, 270f, Arc2D.OPEN);
      GeneralPath path = new GeneralPath(arc); // the path is a set of lines and curves (no arcs anymore)
      List<Shape> vec = norm.convertPath(path); // convert the path, verify if its the same arc
      // we verify if the converted path is a single arc identical to the first
      assertEquals(1, vec.size());
      Object o = vec.get(0);
      if (o instanceof Arc2D) {
         Arc2D arc2 = (Arc2D) o;
         assertEquals(arc.getCenterX(), arc2.getCenterX(), delta);
         assertEquals(arc.getCenterY(), arc2.getCenterY(), delta);
         assertEquals(arc.getArcType(), arc2.getArcType());
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         //assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         anglesEquals(arc.getAngleStart(), arc2.getAngleStart());
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      } else {
         fail();
      }
   }

   /**
    * Test of convertPath method : angle of 360 degrees. test Conversion of [0, 360] arc.
    */
   @Test
   public void testConvertArc4() {
      System.out.println("ShapeNormalizerTest : testConvertArc4");
      ShapeNormalizer norm = new ShapeNormalizer();

      Arc2D.Float arc = new Arc2D.Float(100f, 100f, 40f, 40f, 0f, 360f, Arc2D.OPEN);
      // the path is a set of lines and curves (no arcs anymore)
      GeneralPath path = new GeneralPath(arc);
      // convert the path, verify if its the same arc
      List<Shape> vec = norm.convertPath(path);
      // we verify if the converted path is a single arc identical to the first
      assertEquals(1, vec.size());
      Object o = vec.get(0);
      if (o instanceof Arc2D) {
         Arc2D arc2 = (Arc2D) o;
         assertEquals(arc.getCenterX(), arc2.getCenterX(), delta);
         assertEquals(arc.getCenterY(), arc2.getCenterY(), delta);
         assertEquals(arc.getArcType(), arc2.getArcType());
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         //assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         anglesEquals(arc.getAngleStart(), arc2.getAngleStart());
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      } else {
         fail();
      }
   }

   /**
    * Test of convertPath method : angle of 360 degrees. test Arc 360.
    */
   @Test
   public void testConvertArc360() {
      System.out.println("ShapeNormalizerTest : testConvertArc360");
      ShapeNormalizer norm = new ShapeNormalizer();
      norm.allowCircles(true);

      GeneralPath path = new GeneralPath();
      path.append(new Arc2D.Float(0f, 0f, 10f, 10f, 0f, 180f, Arc2D.OPEN), false);
      path.append(new Line2D.Float(0f, 5f, 0f, 5f), true);
      path.append(new Arc2D.Float(0f, 0f, 10f, 10f, 180f, 180f, Arc2D.OPEN), true);
      // convert the path, verify if its the same arc
      List<Shape> vec = norm.convertPath(path);
      // we verify if the converted path is a single arc identical to the first
      assertEquals(1, vec.size());
      Object o = vec.get(0);
      assertTrue("Must be an Ellipse2D", o instanceof Ellipse2D);
      Ellipse2D el = (Ellipse2D) o;
      assertEquals(10f, el.getHeight(), delta);
      assertEquals(10f, el.getWidth(), delta);
   }

   /**
    * Test of convertPath method : roundRectangle.
    */
   @Test
   public void testConvertRoundRectangle() {
      System.out.println("ShapeNormalizerTest : testConvertRoundRectangle");
      ShapeNormalizer norm = new ShapeNormalizer();
      norm.allowCircles(true);
      RoundRectangle2D rec = new RoundRectangle2D.Float(0, 0, 200, 100, 10, 10);
      List<Shape> shapes = norm.convertPath(rec);
      // we verify if the converted path is a single RoundRectangle2D identical to the first
      assertEquals(1, shapes.size());
      Shape shape = shapes.get(0);
      assertTrue("Must be a RoundRectangle2D", shape instanceof RoundRectangle2D);
      RoundRectangle2D rec2 = (RoundRectangle2D) shape;
      assertEquals(rec.getX(), rec2.getX(), delta);
      assertEquals(rec.getY(), rec2.getY(), delta);
      assertEquals(rec.getWidth(), rec2.getWidth(), delta);
      assertEquals(rec.getHeight(), rec2.getHeight(), delta);
      assertEquals(rec.getArcWidth(), rec2.getArcWidth(), delta);
      assertEquals(rec.getArcHeight(), rec2.getArcHeight(), delta);
   }

   /**
    * Test of convertPath method : Rectangle.
    */
   @Test
   public void testConvertRectangle() {
      System.out.println("ShapeNormalizerTest : testConvertRectangle");
      ShapeNormalizer norm = new ShapeNormalizer();
      norm.allowCircles(true);
      Rectangle2D rec = new Rectangle2D.Float(0, 0, 200, 100);
      List<Shape> shapes = norm.convertPath(rec);
      // we verify if the converted path is a single RoundRectangle2D identical to the first
      assertEquals(1, shapes.size());
      Shape shape = shapes.get(0);
      assertTrue("Must be a Rectangle2D", shape instanceof Rectangle2D);
      Rectangle2D rec2 = (Rectangle2D) shape;
      assertEquals(rec.getX(), rec2.getX(), delta);
      assertEquals(rec.getY(), rec2.getY(), delta);
      assertEquals(rec.getWidth(), rec2.getWidth(), delta);
      assertEquals(rec.getHeight(), rec2.getHeight(), delta);
   }

   private void anglesEquals(double angle0, double angle1) {
      if (angle0 < 0) {
         angle0 = 360 + angle0;
      }
      if (angle1 < 0) {
         angle1 = 360 + angle1;
      }
      assertEquals(angle0, angle1, delta);
   }
}
