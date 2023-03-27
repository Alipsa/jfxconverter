/*
Copyright (c) 2016, 2018 Herve Girod
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

import java.awt.Graphics2D;
import javafx.scene.Node;
import javafx.scene.effect.Effect;

/**
 * An interface which listens to the start or end of each Node in the conversion.
 *
 * @version 0.20
 */
public interface ConverterListener {
   /**
    * Called at the start of each Node. Do nothing by default.
    *
    * @param g2D the Graphics2D
    * @param node the Node
    */
   public default void startNode(Graphics2D g2D, Node node) {
   }

   /**
    * Called at the end of each Node. Do nothing by default.
    *
    * @param g2D the Graphics2D
    * @param node the Node
    */
   public default void endNode(Graphics2D g2D, Node node) {
   }

   /**
    * Apply an effect on the next Node to be rendered. Do nothing by default.
    *
    * @param g2D the Graphics2D
    * @param node the Node
    * @param effect the Effect
    */
   public default void applyEffect(Graphics2D g2D, Node node, Effect effect) {
   }

   /**
    * Mark the end of an effect on the previous Node which was rendered. Do nothing by default.
    *
    * @param g2D the Graphics2D
    * @param node the Node
    */
   public default void endEffect(Graphics2D g2D, Node node) {
   }
}