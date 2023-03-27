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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An exemple of JFXAutoComplete with additional item.
 *
 * @version 0.9.60
 */
public class TestAutoCompleteSearchText extends Application {
   private JFXAutoComplete autoCompleter;

   @Override
   public void start(Stage primaryStage) {
      VBox root = new VBox();

      HBox hbox = new HBox();
      hbox.setSpacing(5);
      final CheckBox cb = new CheckBox("Search per word");
      hbox.getChildren().add(cb);
      cb.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
            autoCompleter.searchPerWord(cb.isSelected());
         }
      });
      final CheckBox cb2 = new CheckBox("Hits from start");
      hbox.getChildren().add(cb2);
      cb2.setSelected(true);
      cb2.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
            autoCompleter.searchHitsFromStart(cb2.isSelected());
         }
      });

      final CheckBox cb3 = new CheckBox("Start search immediately");
      hbox.getChildren().add(cb3);
      cb3.setSelected(true);
      cb3.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
            if (!cb3.isSelected()) {
               autoCompleter.startSearchOnKeyCode(KeyCode.SPACE);
            } else {
               autoCompleter.startSearchOnKeyCode(null);
            }
         }
      });

      root.setSpacing(10);
      root.getChildren().add(hbox);
      TextField tf = new TextField();
      root.getChildren().add(tf);

      autoCompleter = new JFXAutoComplete(tf);
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
      autoCompleter.addAdditionalSearchItem("Containing...");

      tf.addEventHandler(AutoCompleteEvent.AUTOCOMPLETE, new EventHandler<AutoCompleteEvent>() {
         @Override
         public void handle(AutoCompleteEvent event) {
            System.out.println(event.getText() + " => " + event.getStartOffset());
         }
      });

      tf.addEventHandler(AdditionalSearchEvent.FULLTEXTSEARCH, new EventHandler<AdditionalSearchEvent>() {
         @Override
         public void handle(AdditionalSearchEvent event) {
            System.out.println("Listen for containing text: " + event.getText());
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
