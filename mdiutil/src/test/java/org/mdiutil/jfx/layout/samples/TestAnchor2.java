/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout.samples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.mdiutil.jfx.layout.AnchorPosition;
import org.mdiutil.jfx.layout.NodeAnchor;

/**
 *
 * @version 0.9.29
 */
public class TestAnchor2 extends Application {

   @Override
   public void start(Stage primaryStage) {
      AnchorPosition nodePosition = AnchorPosition.BOTTOM;
      AnchorPosition refPosition = AnchorPosition.BOTTOM;

      Button btn1 = new Button();
      btn1.setText("Reference");

      Button btn2 = new Button();
      btn2.setText("Button");
      NodeAnchor anchor = new NodeAnchor(btn2);
      anchor.anchor(btn1, nodePosition, refPosition);

      Pane root = new Pane();
      btn1.setLayoutX(200);
      btn1.setLayoutY(100);
      btn1.setPrefSize(100, 100);
      btn2.setPrefSize(70, 70);
      root.getChildren().add(btn1);
      root.getChildren().add(btn2);

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
