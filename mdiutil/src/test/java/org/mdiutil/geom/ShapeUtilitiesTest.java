/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016, 2017, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * ShapeUtilitiesTest.
 *
 * @version 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class ShapeUtilitiesTest {
   private double delta = 0.00001;

   public ShapeUtilitiesTest() {
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
    * Test of createTransformedShape method, for an Ellipse2D and a translation
    */
   @Test
   public void testShapeEllipse1() {
      System.out.println("ShapeUtilitiesTest : testShapeEllipse1");
      AffineTransform tr = new AffineTransform();
      tr.translate(50, 100);
      Ellipse2D el = new Ellipse2D.Double(10, 40, 30, 150);
      Shape s = ShapeUtilities.createTransformedShape(el, tr);
      if (!(s instanceof Ellipse2D)) {
         fail("not an ellipse");
      } else {
         Ellipse2D el2 = (Ellipse2D) s;
         assertEquals(60, el2.getX(), delta);
         assertEquals(140, el2.getY(), delta);
         assertEquals(30, el2.getWidth(), delta);
         assertEquals(150, el2.getHeight(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Ellipse2D and a rotation
    */
   @Test
   public void testShapeEllipse2() {
      System.out.println("ShapeUtilitiesTest : testShapeEllipse2");
      AffineTransform tr = new AffineTransform();
      tr.rotate(Math.PI / 2);
      Ellipse2D el = new Ellipse2D.Double(10, 40, 30, 150);
      Shape s = ShapeUtilities.createTransformedShape(el, tr);
      if (!(s instanceof Ellipse2D)) {
         fail("not an ellipse");
      } else {
         Ellipse2D el2 = (Ellipse2D) s;
         assertEquals(-190, el2.getX(), delta);
         assertEquals(10, el2.getY(), delta);
         assertEquals(150, el2.getWidth(), delta);
         assertEquals(30, el2.getHeight(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Ellipse2D and a scale
    */
   @Test
   public void testShapeEllipse3() {
      System.out.println("ShapeUtilitiesTest : testShapeEllipse3");
      AffineTransform tr = new AffineTransform();
      tr.scale(3, 3);
      Ellipse2D el = new Ellipse2D.Double(10, 40, 30, 150);
      Shape s = ShapeUtilities.createTransformedShape(el, tr);
      if (!(s instanceof Ellipse2D)) {
         fail("not an ellipse");
      } else {
         Ellipse2D el2 = (Ellipse2D) s;
         assertEquals(30, el2.getX(), delta);
         assertEquals(120, el2.getY(), delta);
         assertEquals(90, el2.getWidth(), delta);
         assertEquals(450, el2.getHeight(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Ellipse2D : SVGLab bug 82
    */
   @Test
   public void testSVGLabBug82() {
      System.out.println("ShapeUtilitiesTest : testSVGLabBug82");
      AffineTransform tr = new AffineTransform(1f, 0f, 0f, -1f, 176f, 114.9f);
      Ellipse2D el = new Ellipse2D.Float(-96f, -96f, 192f, 192f);
      Shape s = ShapeUtilities.createTransformedShape(el, tr);
      if (!(s instanceof Ellipse2D)) {
         fail("not an ellipse");
      } else {
         Ellipse2D el2 = (Ellipse2D) s;
         assertEquals(80, el2.getX(), delta);
         assertEquals(18.9, el2.getY(), delta);
         assertEquals(192f, el2.getWidth(), delta);
         assertEquals(192f, el2.getHeight(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Arc2D and a translation
    */
   @Test
   public void testShapeArc1() {
      System.out.println("ShapeUtilitiesTest : testShapeArc1");
      AffineTransform tr = new AffineTransform();
      tr.translate(50, 100);
      Arc2D arc = new Arc2D.Double(10, 40, 100, 100, 20, 50, Arc2D.OPEN);
      Shape s = ShapeUtilities.createTransformedShape(arc, tr);
      if (!(s instanceof Arc2D)) {
         fail("not an arc");
      } else {
         Arc2D arc2 = (Arc2D) s;
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
         assertEquals(60, arc2.getX(), delta);
         assertEquals(140, arc2.getY(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Arc2D and a rotation
    */
   @Test
   public void testShapeArc2() {
      System.out.println("ShapeUtilitiesTest : testShapeArc2");
      AffineTransform tr = new AffineTransform();
      tr.rotate(35 * Math.PI / 180);
      Arc2D arc = new Arc2D.Double(10, 40, 100, 100, 20, 50, Arc2D.OPEN);
      Shape s = ShapeUtilities.createTransformedShape(arc, tr);
      if (!(s instanceof Arc2D)) {
         fail("not an arc");
      } else {
         Arc2D arc2 = (Arc2D) s;
         assertEquals(-15, arc2.getAngleStart(), delta);
         assertEquals(50, arc2.getAngleExtent(), delta);
      }
   }

   /**
    * Test of createTransformedShape method, for an Arc2D and a scale
    */
   @Test
   public void testShapeArc3() {
      System.out.println("ShapeUtilitiesTest : testShapeArc3");
      AffineTransform tr = new AffineTransform();
      tr.scale(3, 3);
      Arc2D arc = new Arc2D.Double(10, 40, 100, 100, 20, 50, Arc2D.OPEN);
      Shape s = ShapeUtilities.createTransformedShape(arc, tr);
      if (!(s instanceof Arc2D)) {
         fail("not an arc");
      } else {
         Arc2D arc2 = (Arc2D) s;
         assertEquals(30, arc2.getX(), delta);
         assertEquals(120, arc2.getY(), delta);
         assertEquals(300, arc2.getWidth(), delta);
         assertEquals(300, arc2.getHeight(), delta);
         assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      }
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle");
      double angle = Math.toRadians(20);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle2() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle2");
      double angle = Math.toRadians(-20);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle3() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle3");
      double angle = Math.toRadians(75);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle4() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle4");
      double angle = Math.toRadians(-75);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle5() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle5");
      double angle = Math.toRadians(130);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle6() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle6");
      double angle = Math.toRadians(360 - 190);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      //System.out.println(Math.toDegrees(angle2));
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle7() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle7");
      double angle = Math.toRadians(190);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      //System.out.println(Math.toDegrees(angle2));
      assertEquals(angle, Math.PI * 2 + angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle8() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle8");
      double angle = Math.toRadians(180);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle9() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle9");
      double angle = Math.toRadians(-180);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(Math.PI, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle10() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle10");
      double angle = Math.toRadians(90);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationAngle method.
    */
   @Test
   public void testShapeGetRotationAngle11() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationAngle11");
      double angle = Math.toRadians(-90);
      AffineTransform tr = AffineTransform.getRotateInstance(angle);
      double angle2 = ShapeUtilities.getRotationAngle(tr);
      assertEquals(angle, angle2, delta);
   }

   /**
    * Test of getRotationTransform method.
    */
   @Test
   public void testShapeGetRotationTransform() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationTransform");
      double angle = Math.toRadians(20);
      AffineTransform tr = AffineTransform.getRotateInstance(angle, 10, 10);
      AffineTransform tr2 = ShapeUtilities.getRotationTransform(tr);
      AffineTransformComparator.compare(tr2, tr2, delta);
   }

   /**
    * Test of getRotationTransform method.
    */
   @Test
   public void testShapeGetRotationTransform2() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationTransform2");
      double angle = Math.toRadians(70);
      AffineTransform tr = AffineTransform.getRotateInstance(angle, 10, 30);
      AffineTransform tr2 = ShapeUtilities.getRotationTransform(tr);
      AffineTransformComparator.compare(tr2, tr2, delta);
   }

   /**
    * Test of getRotationTransform method.
    */
   @Test
   public void testShapeGetRotationTransform3() {
      System.out.println("ShapeUtilitiesTest : testShapeGetRotationTransform3");
      double angle = Math.toRadians(180);
      AffineTransform tr = AffineTransform.getRotateInstance(angle, 10, 90);
      AffineTransform tr2 = ShapeUtilities.getRotationTransform(tr);
      AffineTransformComparator.compare(tr2, tr2, delta);
   }
}
