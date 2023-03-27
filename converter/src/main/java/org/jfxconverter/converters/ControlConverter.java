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
package org.jfxconverter.converters;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;

/**
 * A converter which convert Controls.
 *
 * @version 0.6
 */
public class ControlConverter extends RegionConverter {
   private Control control = null;

   public ControlConverter(ConverterDelegate converter, Control control) {
      super(converter, control);
      this.control = control;
   }

   /**
    * Convert the Control.
    */
   @Override
   public void convert() {
      super.convert();
   }

   /**
    * Return the additional child Node on this Node.
    * Will return null, except for a {@link javafx.scene.control.Labeled}, where it will return {@link javafx.scene.control.Labeled#getGraphic()}.
    *
    * @return the additional child Node on this Node
    */
   @Override
   public Node getAdditionalNode() {
      if (control instanceof Labeled) {
         return ((Labeled) control).getGraphic();
      } else {
         return null;
      }
   }
}
