/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout.samples;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.mdiutil.jfx.layout.NodeAnchor;

/**
 *
 * @since 0.8
 */
public class TestAnchor3 extends Application {

   @Override
   public void start(Stage primaryStage) {
      Button btn1 = new Button();
      btn1.setText("Reference");

      Label lbl = new Label("My Label");
      lbl.setTextFill(Color.RED);
      lbl.setTextAlignment(TextAlignment.CENTER);
      lbl.setAlignment(Pos.CENTER);
      lbl.setStyle("-fx-background-color: yellow");
      NodeAnchor anchor = new NodeAnchor(lbl);
      anchor.fill(btn1);

      Pane root = new Pane();
      btn1.setLayoutX(200);
      btn1.setLayoutY(100);
      btn1.setPrefSize(100, 100);
      root.getChildren().add(btn1);
      root.getChildren().add(lbl);

      Scene scene = new Scene(root, 500, 400);

      primaryStage.setTitle("Hello World!");
      primaryStage.setScene(scene);
      primaryStage.show();
      double width1 = btn1.getPrefWidth();
      double width2 = lbl.getPrefWidth();
      System.out.println("btn1 : " + width1 + "lbl: " + width2);
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

}
