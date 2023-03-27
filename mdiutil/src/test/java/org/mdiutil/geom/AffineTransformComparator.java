/*------------------------------------------------------------------------------
* Copyright (C) 2016, 2017 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import static org.junit.Assert.assertEquals;
import java.awt.geom.AffineTransform;

/**
 * Compare two AffineTransform.
 *
 * @author GIROD
 * @version 0.9.25
 */
public class AffineTransformComparator {

   private AffineTransformComparator() {
   }

   /**
    * Compare two AffineTransform.
    *
    * @param tr1 the first AffineTransform
    * @param tr2 the second AffineTransform
    * @param delta the comparison delta
    */
   public static void compare(AffineTransform tr1, AffineTransform tr2, double delta) {
      double m00_1 = tr1.getScaleX();
      double m01_1 = tr1.getShearX();
      double m10_1 = tr1.getShearY();
      double m11_1 = tr1.getScaleY();
      double m02_1 = tr1.getTranslateX();
      double m12_1 = tr1.getTranslateY();

      double m00_2 = tr2.getScaleX();
      double m01_2 = tr2.getShearX();
      double m10_2 = tr2.getShearY();
      double m11_2 = tr2.getScaleY();
      double m02_2 = tr2.getTranslateX();
      double m12_2 = tr2.getTranslateY();

      assertEquals(m00_1, m00_2, delta);
      assertEquals(m01_1, m01_2, delta);
      assertEquals(m10_1, m10_2, delta);
      assertEquals(m11_1, m11_2, delta);
      assertEquals(m02_1, m02_2, delta);
      assertEquals(m12_1, m12_2, delta);
   }
}
