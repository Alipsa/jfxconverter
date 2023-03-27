/*
Copyright (c) 2016, Herve Girod
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
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.tools.jfxconverter.samples.svg;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

/**
 * A Sample for SVG conversion.
 *
 * @since 0.12
 */
public class Sample6 extends AbstractSVGSample {
   @Override
   protected Dimension2D getDimension() {
      return new Dimension2D(500, 500);
   }

   @Override
   protected Node getNode() {
      Pane pane = new Pane();
      pane.setPrefWidth(500);
      pane.setPrefHeight(500);
      //define axes
      final NumberAxis xAxis = new NumberAxis();
      xAxis.setLabel("Number of Month");
      xAxis.setAnimated(false);

      final NumberAxis yAxis = new NumberAxis();
      yAxis.setLabel("Stock");
      yAxis.setAnimated(false);

      //creating line chart
      LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
      lineChart.setTitle("Stock Monitoring, 2010");
      lineChart.setAnimated(false);

      //define a series
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName("My portfolio");

      //populate the series with data
      series.getData().add(new XYChart.Data<>(1.0, 23.0));
      series.getData().add(new XYChart.Data<>(12.0, 44.0));

      //add series to line chart
      lineChart.getData().add(series);

      pane.getChildren().add(lineChart);
      return pane;
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }
}
