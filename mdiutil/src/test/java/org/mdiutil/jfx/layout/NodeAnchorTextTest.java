/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout;

import static org.junit.Assert.*;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedJavaFXRunner;

/**
 * Unit tests for the NodeAnchor class, when using a Text as the Node and a Rectangle as the Node reference.
 *
 * @version 0.9.25
 */
@RunWith(OrderedJavaFXRunner.class)
@Category(cat = "jfx")
public class NodeAnchorTextTest {
   private static final double DELTA = 0.2d;

   public NodeAnchorTextTest() {
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
    * Test of anchor method with BOTTOM / TOP parameters.
    */
   @Test
   @Order(order = 1)
   public void testAnchorRightLeft() {
      System.out.println("NodeAnchor2Test : testAnchorRightLeft");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Text rec2 = new Text();
      rec2.setFill(Color.RED);
      rec2.setText("TOTO");
      rec2.setTextOrigin(VPos.CENTER);
      rec2.setTextAlignment(TextAlignment.CENTER);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.BOTTOM, AnchorPosition.TOP);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWrappingWidth(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 84, (int)rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWrappingWidth(), DELTA);
   }
}
