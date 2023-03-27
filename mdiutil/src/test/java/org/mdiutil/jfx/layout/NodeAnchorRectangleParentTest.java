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
 * Unit tests for the NodeAnchor class, when using a Rectangle as the Node and the Parent of the
 * Rectangle as the Node reference.
 *
 * @version 0.9.25
 */
@RunWith(OrderedJavaFXRunner.class)
@Category(cat = "jfx")
public class NodeAnchorRectangleParentTest {
   private static final double DELTA = 0.2d;

   public NodeAnchorRectangleParentTest() {
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
    * Test of anchor method with TOP / BOTTOM parameters.
    */
   @Test
   @Order(order = 1)
   public void testAnchorTopBottom() {
      System.out.println("NodeAnchorTest : testAnchorTopBottom");
      Pane rec1 = new Pane();
      rec1.setStyle("-fx-background-color: yellow;");

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      Pane root = new Pane();
      root.getChildren().add(rec1);
      rec1.getChildren().add(rec2);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.TOP, AnchorPosition.BOTTOM);

      rec1.setLayoutX(200);
      rec1.setLayoutY(100);
      rec1.setPrefWidth(100);
      rec1.setPrefHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 0, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", -1, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with BOTTOM / TOP parameters.
    */
   @Test
   @Order(order = 2)
   public void testAnchorBottomTop() {
      System.out.println("NodeAnchorTest : testAnchorBottomTop");
      Pane rec1 = new Pane();
      rec1.setStyle("-fx-background-color: yellow;");

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      Pane root = new Pane();
      root.getChildren().add(rec1);
      rec1.getChildren().add(rec2);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.BOTTOM, AnchorPosition.TOP);

      rec1.setLayoutX(200);
      rec1.setLayoutY(100);
      rec1.setPrefWidth(100);
      rec1.setPrefHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", 0, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", -70, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 100, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 70, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with LEFT / RIGHT parameters.
    */
   @Test
   @Order(order = 3)
   public void testAnchorLeftRight() {
      System.out.println("NodeAnchorTest : testAnchorLeftRight");
      Pane rec1 = new Pane();
      rec1.setStyle("-fx-background-color: yellow;");

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      Pane root = new Pane();
      root.getChildren().add(rec1);
      rec1.getChildren().add(rec2);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.LEFT, AnchorPosition.RIGHT);

      rec1.setLayoutX(200);
      rec1.setLayoutY(100);
      rec1.setPrefWidth(100);
      rec1.setPrefHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", -1, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 0, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }

   /**
    * Test of anchor method with RIGHT / LEFT parameters.
    */
   @Test
   @Order(order = 4)
   public void testAnchorRightLeft() {
      System.out.println("NodeAnchorTest : testAnchorRightLeft");
      Pane rec1 = new Pane();
      rec1.setStyle("-fx-background-color: yellow;");

      Rectangle rec2 = new Rectangle();
      rec2.setFill(Color.RED);
      Pane root = new Pane();
      root.getChildren().add(rec1);
      rec1.getChildren().add(rec2);
      NodeAnchor anchor = new NodeAnchor(rec2);
      anchor.anchor(rec1, AnchorPosition.RIGHT, AnchorPosition.LEFT);

      rec1.setLayoutX(200);
      rec1.setLayoutY(100);
      rec1.setPrefWidth(100);
      rec1.setPrefHeight(100);
      rec2.setWidth(70);
      rec2.setHeight(70);

      // the position of the second Rectangle is forced to the position of the first
      assertEquals("Relative Rectangle position", -70, rec2.getX(), DELTA);
      assertEquals("Relative Rectangle position", 0, rec2.getY(), DELTA);
      // the widget of the second Rectangle is forced to the width of the first
      assertEquals("Relative Rectangle size", 70, rec2.getWidth(), DELTA);
      assertEquals("Relative Rectangle size", 100, rec2.getHeight(), DELTA);
   }
}
