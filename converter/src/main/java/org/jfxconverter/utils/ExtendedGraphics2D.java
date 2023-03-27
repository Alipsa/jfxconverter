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
package org.jfxconverter.utils;

/**
 * An interface which allows to detect Groups in the Node structure. This can be used by specific
 * {@link java.awt.Graphics2D} to allow to keep the original Node structure. For example, a SVG Graphics2D converter might
 * create a "g" Node for each Node in the tree to regroup its children.
 *
 * <h1>Usage</h1>
 * The {@link org.jfxconverter.JFXConverter} does not use this interface by default when converting a Node tree. However,
 * it is possible to use a {@link org.jfxconverter.converters.ConverterListener} to process specificaly the Groups.
 *
 * An example of usage could be:
 * <pre>
 * public void startNode(Graphics2D g2D, Node node) {
 *   if (node instanceof Parent) {
 *     if (g2D instanceof ExtendedGraphics2D) {
 *       ExtendedGraphics2D group2D = (ExtendedGraphics2D)g2D;
 *       group2D.startGroup(node.getId(), node);
 *     }
 *   }
 * }
 *
 * public void endNode(Graphics2D g2D, Node node) {
 *   if (node instanceof Parent) {
 *     if (g2D instanceof ExtendedGraphics2D) {
 *       ExtendedGraphics2D group2D = (ExtendedGraphics2D)g2D;
 *       group2D.endGroup(node);
 *     }
 *   }
 * }
 * </pre>
 *
 * @param <C> the type of Object associated with the graphics to be rendered
 * @param <E> the type of Object associated with the Effect to be rendered
 * @version 0.21
 */
public interface ExtendedGraphics2D<C, E> {
   /**
    * Start a Group. Do nothing by default.
    *
    * @param name the group name
    * @param o an Object which is associated to the group (can be null)
    */
   public default void startGroup(String name, C o) {
   }

   /**
    * End a Group. Do nothing by default.
    *
    * @param o an Object which is associated to the group (can be null)
    */
   public default void endGroup(C o) {
   }

   /**
    * Apply an Effect on the next graphics object to be rendered. Do nothing by default.
    *
    * @param o the Object on which the effect must be applied
    * @param effect the Effect
    */
   public default void applyEffect(C o, E effect) {
   }
}
