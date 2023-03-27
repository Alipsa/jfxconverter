/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout.samples;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.mdiutil.jfx.layout.AnchorPosition;
import org.mdiutil.jfx.layout.NodeAnchor;

/**
 *
 * @since 0.8
 */
public class TestAnchorShape4 extends Application {

   @Override
   public void start(Stage primaryStage) {
      AnchorPosition nodePosition = AnchorPosition.TOP;
      AnchorPosition refPosition = AnchorPosition.BOTTOM;

      Rectangle btn1 = new Rectangle();
      btn1.setFill(Color.YELLOW);

      Text btn2 = new Text();
      btn2.setText("TOTO");
      btn2.setFill(Color.RED);
      btn2.setTextOrigin(VPos.CENTER);
      btn2.setTextAlignment(TextAlignment.CENTER);
      btn2.setWrappingWidth(70);
      NodeAnchor anchor = new NodeAnchor(btn2);
      anchor.anchor(btn1, nodePosition, refPosition);

      Pane root = new Pane();
      btn1.setX(200);
      btn1.setY(100);
      btn1.setWidth(100);
      btn1.setHeight(150);
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
