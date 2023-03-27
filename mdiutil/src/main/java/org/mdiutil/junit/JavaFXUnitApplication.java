/*
 MODIFIED BSD LICENSE
 ***** BEGIN LICENSE BLOCK *****

 Copyright (c) 2014 Dassault Aviation
 Copyright (c) 2014 Hervé Girod
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the <organization>.
 4. Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 ***** END LICENSE BLOCK *****
 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on http://j661.sourceforge.net
 */
package org.mdiutil.junit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.stage.Stage;

/** 
 * A JavaFX Application used for JavaFX Unit Tests. THis class is used to be sure that the JavaFX framework is properly 
 * started for Unit Tests.
 *
 * @since 0.8
 */
public class JavaFXUnitApplication  extends Application {
   private static boolean started = false;

   @Override
   public void start(Stage stage) throws Exception {
   }

   /** 
    * Start the JavaFX Application, and wait for the javaFX framework to successfully initialize. THis will
    * delegate to the {@link #launch()} static method.
    */
   public static void startjavaFX() {
      // only start the Framework if it has not already been started
      if (! started) {
         started = true;
         ExecutorService service = Executors.newSingleThreadExecutor();
         service.execute(new Runnable() {
            @Override
            public void run() {
               JavaFXUnitApplication.launch();
            }
         });

         // wait 500 ms for the JavaFX framework to be started
         // we should use the preloader instead
         try {
            Thread.sleep(500);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   /** 
    * Launch the JavaFX Application.
    */
   public static void launch() {
      Application.launch();
   }
}
