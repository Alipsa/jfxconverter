/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout.samples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.mdiutil.jfx.layout.AnchorPosition;
import org.mdiutil.jfx.layout.NodeAnchor;

/**
 *
 * @since 0.8
 */
public class TestAnchorShapeParent extends Application {

   @Override
   public void start(Stage primaryStage) {
      AnchorPosition nodePosition = AnchorPosition.RIGHT;
      AnchorPosition refPosition = AnchorPosition.RIGHT;

      Pane btn1 = new Pane();
      btn1.setStyle("-fx-background-color: yellow;");

      Rectangle btn2 = new Rectangle();
      btn2.setFill(Color.RED);
      NodeAnchor anchor = new NodeAnchor(btn2);
      Pane root = new Pane();
      root.getChildren().add(btn1);
      btn1.getChildren().add(btn2);      
      anchor.anchor(btn1, nodePosition, refPosition);

      
      btn1.setLayoutX(200);
      btn1.setLayoutY(100);
      btn1.setPrefWidth(100);
      btn1.setPrefHeight(100);
      btn2.setWidth(70);
      btn2.setHeight(70);
      Scene scene = new Scene(root, 500, 400);

      primaryStage.setTitle("Hello World!");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

}
