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

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * This class is used by the SVGGraphics2D SVG Generator to manage controlled addition of new Nodes to the SVG DOM Tree.
 * It must be used with {@link ControlledDOMGroupManager} class.
 *
 * @version 0.21
 */
public class ControlledDOMTreeManager extends DOMTreeManager implements SVGGeneratorConstants {
   protected Element currentTopLevelGroup;
   protected SVGGraphicContext mydefaultGC;

   /**
    * Constructor
    *
    * @param gc default graphic context state
    * @param generatorContext the ALGSVGGeneratorContext
    */
   public ControlledDOMTreeManager(GraphicContext gc, SVGGeneratorContext generatorContext) {
      super(gc, generatorContext, DEFAULT_MAX_GC_OVERRIDE);

      // Build the default GC descriptor
      mydefaultGC = getGraphicContextConverter().toSVG(gc);

      this.currentTopLevelGroup = this.topLevelGroup;
   }

   public DOMGroupManager getCompatibleGroupManager(GraphicContext gc) {
      return new ControlledDOMGroupManager(gc, this);
   }

   /**
    * Return the current top level group.
    *
    * @return the current top level group
    */
   public Element getCurrentTopLevelGroup() {
      return currentTopLevelGroup;
   }

   @Override
   public void applyDefaultRenderingStyle(Element element) {
      Map groupDefaults = mydefaultGC.getGroupContext();
      generatorContext.getStyleHandler().setStyle(element, groupDefaults, generatorContext);
   }

   public Element addNewTopLevelGroup(Element parent, String id, ControlledDOMGroupManager manager) {
      Element group = manager.createGroup(id);
      appendGroup(parent, group, manager);
      currentTopLevelGroup = group;

      return group;
   }

   public Document getDOMFactoryAsPublic() {
      return generatorContext.getDOMFactory();
   }

   public SVGGeneratorContext getGeneratorContextAsPublic() {
      return generatorContext;
   }

   public StyleHandler getStyleHandlerAsPublic() {
      return generatorContext.getStyleHandler();
   }

   public SVGGraphicContext getGraphicContextAsPublic() {
      return mydefaultGC;
   }

   /**
    * When a group is appended to the tree by this call, all the other group managers are requested to start new groups, in
    * order to preserve the Z-order.
    *
    * @param group new group to be appended to the topLevelGroup
    * @param groupManager DOMTreeManager that produced the group.
    */
   public void appendGroup(Element parentGroup, Element group, ControlledDOMGroupManager groupManager) {
      parentGroup.appendChild(group);
      int nManagers = groupManagers.size();
      for (int i = 0; i < nManagers; i++) {
         ControlledDOMGroupManager gm = (ControlledDOMGroupManager) groupManagers.get(i);
         if (gm != groupManager) {
            gm.recycleCurrentGroup();
         }
      }
   }

   public Element getTopLevelGroup(ControlledDOMGroupManager manager) {
      Element topLevelGroup = getTopLevelGroup();

      appendGroup(this.topLevelGroup, topLevelGroup, manager);
      return topLevelGroup;
   }
}
