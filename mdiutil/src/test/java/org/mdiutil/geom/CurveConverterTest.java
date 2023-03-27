/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2016, 2017, 2021 Herve Girod
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
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * CurveConverterTest.
 *
 * @version 1.2.15
 */
@RunWith(CategoryRunner.class)
@Category(cat = "geom")
public class CurveConverterTest {
   private static double delta = 0.001;

   public CurveConverterTest() {
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

   private Shape getConvertedShape(Arc2D arc) {
      CurveConverter conv = new CurveConverter();
      // the path is a set of lines and curves (no arcs anymore)
      GeneralPath path = new GeneralPath(arc);

      float af[] = new float[6];
      Point2D p = new Point2D.Double(0, 0);
      PathIterator it = path.getPathIterator(new AffineTransform());
      for (; !it.isDone(); it.next()) {
         int j = it.currentSegment(af);
         switch (j) {
            default:
               break;
            case PathIterator.SEG_MOVETO: // '\0'
               p = new Point2D.Double(af[0], af[1]);
               break;
            case PathIterator.SEG_CUBICTO: // '\003'
               CubicCurve2D cubic = new CubicCurve2D.Float(
                  (float) p.getX(), (float) p.getY(), af[0], af[1], af[2], af[3], af[4], af[5]);
               conv.addCubicCurve(cubic);
               p = new Point2D.Double(af[4], af[5]);
               break;
            case PathIterator.SEG_QUADTO: // '\002'
               QuadCurve2D quad = new QuadCurve2D.Float(
                  (float) p.getX(), (float) p.getY(), af[0], af[1], af[2], af[3]);
               conv.addQuadCurve(quad);
               p = new Point2D.Double(af[2], af[3]);
               break;
            case PathIterator.SEG_LINETO: // '\001'
               if (conv.getCurrentState() == CurveConverter.CURRENT_ARC) {
                  conv.endArc();
               }
               p = new Point2D.Double(af[0], af[1]);
               break;
            case PathIterator.SEG_CLOSE: // '\004'
               if (conv.getCurrentState() == CurveConverter.CURRENT_ARC) {
                  conv.endArc();
               }
               break;
         }
      }
      if (conv.getCurrentState() != CurveConverter.END_ARC) {
         conv.endArc();
      }

      return conv.getLastShape();
   }

   /**
    * Test of toArc2D method, of class svglab.util.geom.CurveConverter.
    */
   @Test
   public void testCubicCurveToArc2D() {
      System.out.println("CurveConverterTest : testCubicCurveToArc2D");

      Arc2D.Float arc = new Arc2D.Float(100f, 100f, 20f, 20f, 0, 30f, Arc2D.OPEN);
      Shape shape = getConvertedShape(arc);
      try {
         Arc2D arc2 = (Arc2D) shape;
         assertEquals(arc.getCenterX(), arc2.getCenterX(), delta);
         assertEquals(arc.getCenterY(), arc2.getCenterY(), delta);
         assertEquals(arc.getArcType(), arc2.getArcType());
         assertEquals(arc.getHeight(), arc2.getHeight(), delta);
         assertEquals(arc.getWidth(), arc2.getWidth(), delta);
         assertEquals(arc.getAngleStart(), arc2.getAngleStart(), delta);
         assertEquals(arc.getAngleExtent(), arc2.getAngleExtent(), delta);
      } catch (Exception e) {
         fail();
      }
   }
}
