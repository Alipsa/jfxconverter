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
 * Unit tests for the NodeAnchor class, when using a Rectangle as the Node and the Node reference.
 *
 * @version 0.9.25
 */
@RunWith(OrderedJavaFXRunner.class)
@Category(cat = "jfx")
public class NodeAnchorRectangleTest {
   private static final double DELTA = 0.2d;

   public NodeAnchorRectangleTest() {
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
    * Test of anchor method with TOP / TOP parameters.
    */
   @Test
   @Order(order = 1)
   public void testAnchorTopTop() {
      System.out.println("NodeAnchorTest : testAnchorTopTop");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.TOP, AnchorPosition.TOP);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with BOTTOM / BOTTOM parameters.
    */
   @Test
   @Order(order = 2)
   public void testAnchorBottomBottom() {
      System.out.println("NodeAnchorTest : testAnchorBottomBottom");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.BOTTOM, AnchorPosition.BOTTOM);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 130, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with LEFT / LEFT parameters.
    */
   @Test
   @Order(order = 3)
   public void testAnchorLeftLeft() {
      System.out.println("NodeAnchorTest : testAnchorLeftLeft");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.LEFT, AnchorPosition.LEFT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with RIGHT / RIGHT parameters.
    */
   @Test
   @Order(order = 4)
   public void testAnchorRightRight() {
      System.out.println("NodeAnchorTest : testAnchorRightRight");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.RIGHT, AnchorPosition.RIGHT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 230, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with TOP / BOTTOM parameters.
    */
   @Test
   @Order(order = 5)
   public void testAnchorTopBottom() {
      System.out.println("NodeAnchorTest : testAnchorTopBottom");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.TOP, AnchorPosition.BOTTOM);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 200, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with BOTTOM / TOP parameters.
    */
   @Test
   @Order(order = 6)
   public void testAnchorBottomTop() {
      System.out.println("NodeAnchorTest : testAnchorBottomTop");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.BOTTOM, AnchorPosition.TOP);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 30, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with LEFT / RIGHT parameters.
    */
   @Test
   @Order(order = 7)
   public void testAnchorLeftRight() {
      System.out.println("NodeAnchorTest : testAnchorLeftRight");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.LEFT, AnchorPosition.RIGHT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 300, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with RIGHT / LEFT parameters.
    */
   @Test
   @Order(order = 8)
   public void testAnchorRightLeft() {
      System.out.println("NodeAnchorTest : testAnchorRightLeft");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.RIGHT, AnchorPosition.LEFT);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 130, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with fill.
    */
   @Test
   @Order(order = 9)
   public void testAnchorFill() {
      System.out.println("NodeAnchorTest : testAnchorFill");
      Rectangle rec1 = new Rectangle();
      rec1.setFill(Color.YELLOW);

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.fill(rec1);

      Pane root = new Pane();
      rec1.setX(200);
      rec1.setY(100);
      rec1.setWidth(100);
      rec1.setHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);
      root.getChildren().add(rec1);
      root.getChildren().add(rec2);

      // reference rectangle
      assertEquals("Reference Rectangle position", 200, rec1.getX(), DELTA);
      assertEquals("Reference Rectangle position", 100, rec1.getY(), DELTA);
      assertEquals("Reference Rectangle size", 100, rec1.getWidth(), DELTA);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 200, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 100, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }
}
