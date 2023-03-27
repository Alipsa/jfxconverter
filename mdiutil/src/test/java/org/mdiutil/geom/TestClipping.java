/*------------------------------------------------------------------------------
* Copyright (C) 2017 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.geom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Girod
 * @version 0.9.16
 */
public class TestClipping extends JFrame {
   public TestClipping() {
      super();
      this.setSize(500, 500);
      this.setLayout(new BorderLayout());
      this.add(new Drawing(), BorderLayout.CENTER);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   public static void main(String[] arg) {
      TestClipping test = new TestClipping();
      test.setVisible(true);
   }

   public class Drawing extends JComponent {
      public Shape outputShape = null;
      public Shape inputShape = null;
      public Shape unclosedShape = null;

      public Drawing() {
         super();
         inputShape = new RoundRectangle2D.Double(245.669, 321.260, 132d, 57d, 5d, 5d);
         Rectangle2D clip = new Rectangle2D.Double(0, 0, 377.95d, 377.95d);
         ClippableShape csh = new ClippableShape(inputShape);
         unclosedShape = csh.getUnclosedShape(inputShape, true);
         csh.intersect(new Area(clip));
         outputShape = csh.getPeerShape();
      }

      @Override
      public void paint(Graphics g) {
         Graphics2D g2D = (Graphics2D) g;
         g2D.setColor(Color.GREEN);
         g2D.draw(inputShape);

         g2D.setColor(Color.YELLOW);
         g2D.draw(unclosedShape);

         g2D.setColor(Color.RED);
         g2D.draw(outputShape);
      }
   }
}
