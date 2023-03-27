/*
Copyright (c) 2017, 2019 Herve Girod
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
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.jfx.autocomplete;

import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An example of JFXAutoComplete with scrollbar.
 *
 * @version 0.9.52
 */
public class TestAutoCompleteScrollbar extends Application {
   private JFXAutoComplete autoCompleter;

   @Override
   public void start(Stage primaryStage) {
      VBox root = new VBox();

      root.setSpacing(10);
      TextField tf = new TextField();
      root.getChildren().add(tf);

      autoCompleter = new JFXAutoComplete(tf);
      autoCompleter.addToDictionary("eternal");
      autoCompleter.addToDictionary("etymology");
      autoCompleter.addToDictionary("ethology");
      autoCompleter.addToDictionary("elevated");
      autoCompleter.addToDictionary("elevation");
      autoCompleter.addToDictionary("energized");
      autoCompleter.addToDictionary("end of the line");
      autoCompleter.addToDictionary("ending of the story, end of the line");
      autoCompleter.addToDictionary("end");
      autoCompleter.addToDictionary("elyseum");
      autoCompleter.addToDictionary("ethics");
      autoCompleter.addToDictionary("each person");
      autoCompleter.addToDictionary("earth and sea");
      autoCompleter.addToDictionary("ecology");

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
