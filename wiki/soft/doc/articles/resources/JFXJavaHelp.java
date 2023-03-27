/*
Copyright (c) 2017, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on http://sourceforge.net/projects/docjgenerator/
 */
package org.docgene.help.examples;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.docgene.help.JavaHelpFactory;
import org.docgene.help.gui.jfx.JFXHelpContentViewer;
import org.xml.sax.SAXException;

/**
 *
 * @since 0.87
 */
public class JFXJavaHelp extends Application {
   private JavaHelpFactory factory = null;
   private JFXHelpContentViewer viewer = null;

   private void createFactory() {
      URL url = this.getClass().getResource("resources/articlesTutorial.zip");

      factory = new JavaHelpFactory(url);
      try {
         factory.create();
      } catch (IOException | SAXException ex) {
         ex.printStackTrace();
      }
   }

   @Override
   public void start(Stage primaryStage) {
      createFactory();

      BorderPane root = createLayout(primaryStage);

      Scene scene = new Scene(root, 400, 400);
      primaryStage.setTitle("DocGenerator Help Tutorial");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   private void createContent(BorderPane root) {
      TextArea area = new TextArea();
      area.setStyle("-fx-background-color: null;");
      Insets insets = new Insets(10, 10, 10, 10);
      area.setPadding(insets);
      area.setText("my area content");
      root.setCenter(area);
   }

   private void createMenu(VBox top) {
      MenuBar menuBar = new MenuBar();
      top.getChildren().add(menuBar);
      Menu menu = new Menu("Help");
      menuBar.getMenus().add(menu);
      MenuItem item = new MenuItem("Help Content");
      menu.getItems().add(item);
      item.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent e) {
            viewer.showHelpDialog(menu.getGraphic());
         }
      });
   }

   private BorderPane createLayout(Stage stage) {
      BorderPane root = new BorderPane();
      VBox top = new VBox();
      root.setTop(top);
      try {
         viewer = new JFXHelpContentViewer();
         factory.install(viewer);
         viewer.getHelpWindow(stage, "Help Content", 600, 700);

         createContent(root);
         createMenu(top);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return root;
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

}
