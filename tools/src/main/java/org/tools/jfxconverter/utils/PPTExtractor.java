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
package org.tools.jfxconverter.utils;

import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Extracts the content of a PPT file.
 *
 * @since 0.13
 */
public class PPTExtractor {
   private File file = null;

   public PPTExtractor(File file) {
      this.file = file;
   }

   public void extract() throws IOException {
      HSLFSlideShow slides = new HSLFSlideShow(new FileInputStream(file));
      HSLFSlide slide = slides.getSlides().get(0);
      List<HSLFShape> shapes = slide.getShapes();
      Iterator<HSLFShape> it = shapes.iterator();
      while (it.hasNext()) {
         HSLFShape shape = it.next();
         System.out.println("*** Shape: " + shape.getShapeName());
         AbstractEscherOptRecord record = shape.getEscherOptRecord();
         if (record != null) {
            List<EscherProperty> properties = record.getEscherProperties();
            for (EscherProperty prop : properties) {
               System.out.println("Property: " + prop.getName() + " " + prop.getId());
               if (prop instanceof EscherSimpleProperty) {
                  EscherSimpleProperty simpleProp = (EscherSimpleProperty) prop;
                  System.out.println("Property value: " + simpleProp.getPropertyValue());
               } else {
                  System.out.println("Complex property " + prop.getName());
               }
            }
         }
      }

      slides.close();
   }

   public static void main(String[] arg) throws IOException {
      JFileChooser chooser = new JFileChooser();
      File dir = new File(System.getProperty("user.dir"));
      chooser.setCurrentDirectory(dir);
      chooser.setDialogTitle("Choose PPT");
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         PPTExtractor extractor = new PPTExtractor(file);
         extractor.extract();
      }
   }
}
