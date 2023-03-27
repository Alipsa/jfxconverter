/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout;

import static org.junit.Assert.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
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
 * Unit tests for the NodeAnchor class, when using an Ellipse as the Node and a Rectangle as the Node reference.
 *
 * @version 0.9.25
 */
@RunWith(OrderedJavaFXRunner.class)
@Category(cat = "jfx")
public class NodeAnchorEllipseTest {
   private static final double DELTA = 0.2d;

   public NodeAnchorEllipseTest() {
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
    * Test of anchor method with RIGHT / LEFT parameters.
    */
   @Test
   @Order(order = 1)
   public void testAnchorRightLeft() {
      System.out.println("NodeAnchor3Test : testAnchorRightLeft");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Ellipse rec2 = new Ellipse();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.RIGHT, AnchorPosition.LEFT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(150);
      rec2.setRadiusX(70);
      rec2.setRadiusY(50);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 130, rec2.getCenterX(), DELTA);
      assertEquals("Relative Rectangle position", 175, rec2.getCenterY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getRadiusX(), DELTA);
      assertEquals("Relative Rectangle size", 75, rec2.getRadiusY(), DELTA);
   }

   /**
    * Test of anchor method with LEFT / RIGHT parameters.
    */
   @Test
   @Order(order = 2)
   public void testAnchorLeftRight() {
      System.out.println("NodeAnchor3Test : testAnchorLeftRight");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Ellipse rec2 = new Ellipse();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.LEFT, AnchorPosition.RIGHT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(150);
      rec2.setRadiusX(70);
      rec2.setRadiusY(50);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 370, rec2.getCenterX(), DELTA);
      assertEquals("Relative Rectangle position", 175, rec2.getCenterY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getRadiusX(), DELTA);
      assertEquals("Relative Rectangle size", 75, rec2.getRadiusY(), DELTA);
   }

   /**
    * Test of anchor method with TOP / BOTTOM parameters.
    */
   @Test
   @Order(order = 2)
   public void testAnchorTopBottom() {
      System.out.println("NodeAnchor3Test : testAnchorTopBottom");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Ellipse rec2 = new Ellipse();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.TOP, AnchorPosition.BOTTOM);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(150);
      rec2.setRadiusX(70);
      rec2.setRadiusY(30);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 250, rec2.getCenterX(), DELTA);
      assertEquals("Relative Rectangle position", 280, rec2.getCenterY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 50, rec2.getRadiusX(), DELTA);
      assertEquals("Relative Rectangle size", 30, rec2.getRadiusY(), DELTA);
   }
}
