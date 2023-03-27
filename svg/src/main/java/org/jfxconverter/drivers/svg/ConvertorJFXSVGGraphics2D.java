/*
Copyright (c) 2016, 2017, 2018 Herve Girod
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

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import org.jfxconverter.utils.ExtendedGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Stack;

/**
 * A SVGGraphics2D which has an added extension handler to handle Groups and Effects.
 *
 * @version 0.21
 */
public class ConvertorJFXSVGGraphics2D extends ConvertorSVGGraphics2D implements ExtendedGraphics2D<Node, Effect> {
   private ControlledDOMGroupManager groupmanager = null;
   private ControlledDOMTreeManager treemanager = null;
   private boolean supportGroups = false;
   private Element root = null;
   private final Stack<Element> groups = new Stack<>();

   public ConvertorJFXSVGGraphics2D(Document doc) {
      super(doc);
   }

   /**
    * Set if grouping is supported. If supported, a {@link org.apache.poi.hslf.usermodel.HSLFGroupShape} will be created
    * when the method {@link #startGroup(String, Node)} is called.
    *
    * @param b true if grouping is supported
    */
   public void supportGroups(boolean b) {
      this.supportGroups = b;
      // register managers
      if (treemanager == null) {
         treemanager = new ControlledDOMTreeManager(getGraphicContext(), getGeneratorContext());
         groupmanager = new ControlledDOMGroupManager(getGraphicContext(), treemanager);
         treemanager.addGroupManager(groupmanager);
         setDOMTreeManager(treemanager);
         setDOMGroupManager(groupmanager);

         // initialize root
         root = treemanager.getTopLevelGroup(groupmanager);
      }
   }

   /**
    * Return true if grouping is supported.
    *
    * @return true if grouping is supported
    */
   public boolean isSupportingGroups() {
      return supportGroups;
   }

   /**
    * Start a Group.
    *
    * @param name the group name
    * @param node the Node
    */
   @Override
   public void startGroup(String name, Node node) {
      if (supportGroups) {
         // Object directly under the root
         if (groups.isEmpty()) {
            Element obj = treemanager.addNewTopLevelGroup(root, name, groupmanager);
            groups.push(obj);
         } else {
            Element parent = groups.peek();
            Element obj = treemanager.addNewTopLevelGroup(parent, name, groupmanager);
            groups.push(obj);
         }
      }
   }

   /**
    * End a Group.
    *
    * @param node the Node
    */
   @Override
   public void endGroup(Node node) {
      if (!groups.isEmpty()) {
         groups.pop();
      }
   }

   @Override
   public void applyEffect(Node node, Effect effect) {

   }
}
