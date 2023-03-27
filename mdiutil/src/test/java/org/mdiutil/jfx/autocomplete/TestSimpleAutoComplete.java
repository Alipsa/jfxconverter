/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.autocomplete;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @since 0.9.60
 */
public class TestSimpleAutoComplete extends Application {
   private JFXAutoComplete autoCompleter;

   @Override
   public void start(Stage primaryStage) {
      VBox root = new VBox();

      root.setSpacing(10);
      TextField tf = new TextField();
      root.getChildren().add(tf);

      TextArea area = new TextArea();
      root.getChildren().add(area);

      autoCompleter = new JFXAutoComplete(tf);
      autoCompleter.addAdditionalSearchItem("Containing...");
      autoCompleter.addToDictionary("bye");
      autoCompleter.addToDictionary("hello");
      autoCompleter.addToDictionary("heritage");
      autoCompleter.addToDictionary("happiness");
      autoCompleter.addToDictionary("woodbye");
      autoCompleter.addToDictionary("war");
      autoCompleter.addToDictionary("will");
      autoCompleter.addToDictionary("world");
      autoCompleter.addToDictionary("cruel world");
      autoCompleter.addToDictionary("wall");
      autoCompleter.searchPerWord(true);

      tf.addEventHandler(AutoCompleteEvent.AUTOCOMPLETE, new EventHandler<AutoCompleteEvent>() {
         @Override
         public void handle(AutoCompleteEvent event) {
            System.out.println(event.getText() + " => " + event.getStartOffset());
         }
      });

      Scene scene = new Scene(root, 300, 200);

      primaryStage.setTitle("AutoComplete");
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
