/*
Copyright (c) 2018, Herve Girod
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
package org.jfxconverter.drivers.svg;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jfxconverter.utils.JFXInvoker;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;

/**
 * Tests for the SVGGraphics2D class.
 *
 * @since 0.20
 */
public class SVGDriver2Test {

   /**
    * Test of converting a Line.
    */
   @Test
   public void testConvertChart() throws Exception {
      System.out.println("SVGDriver2Test : testConvertChart");
      BorderPane pane = new BorderPane();
      JFXInvoker.getInstance().invokeBlocking(new Runnable() {
         @Override
         public void run() {
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            //creating the chart
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            //defining a series
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            //populating the series with data
            series.getData().add(new XYChart.Data<>(1, 23));
            series.getData().add(new XYChart.Data<>(2, 14));
            series.getData().add(new XYChart.Data<>(3, 15));
            series.getData().add(new XYChart.Data<>(4, 24));
            series.getData().add(new XYChart.Data<>(5, 34));
            series.getData().add(new XYChart.Data<>(6, 36));
            series.getData().add(new XYChart.Data<>(7, 22));
            series.getData().add(new XYChart.Data<>(8, 45));
            series.getData().add(new XYChart.Data<>(9, 43));
            series.getData().add(new XYChart.Data<>(10, 17));
            series.getData().add(new XYChart.Data<>(11, 29));
            series.getData().add(new XYChart.Data<>(12, 25));
            lineChart.getData().add(series);

            pane.setCenter(lineChart);
         }
      });

      new JFXPanel();
      SVGConverter utils = new SVGConverter();
      File file = File.createTempFile("jfxconverter", ".svg");
      utils.convert(pane, file);
      System.out.println("Wrote " + file);
      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
      Document doc = factory.createDocument(file.toURI().toString());
      assertNotNull(doc,"Document must not be null");

      //if (!file.delete()) file.deleteOnExit();
   }
}
