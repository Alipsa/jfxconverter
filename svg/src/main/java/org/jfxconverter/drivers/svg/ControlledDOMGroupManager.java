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
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.SVGGraphicContext;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is used by the Graphics2D SVG Generator to manage a group of Nodes that can later be added to the SVG DOM Tree
 * managed by the DOMTreeManager. It must ne used with {@link ControlledDOMTreeManager} class.
 *
 * @version 0.21
 */
public class ControlledDOMGroupManager extends DOMGroupManager implements SVGGeneratorConstants {

   /**
    * Constructor
    *
    * @param gc graphic context whose state will be reflected in the element's style attributes.
    * @param domTreeManager DOMTreeManager instance this group manager cooperates with.
    */
   public ControlledDOMGroupManager(GraphicContext gc, ControlledDOMTreeManager domTreeManager) {
      super(gc, domTreeManager);
   }

   /**
    * Processes the difference between two graphic contexts. The values in gc that are different from the values in referenceGc will be
    * present in the delta. Other values will no.
    */
   static SVGGraphicContext processDeltaGC(SVGGraphicContext gc, SVGGraphicContext referenceGc) {
      Map groupDelta = processDeltaMap(gc.getGroupContext(), referenceGc.getGroupContext());
      Map graphicElementDelta = gc.getGraphicElementContext();

      TransformStackElement[] gcTransformStack = gc.getTransformStack();
      TransformStackElement[] referenceStack = referenceGc.getTransformStack();
      int deltaStackLength = gcTransformStack.length - referenceStack.length;
      TransformStackElement[] deltaTransformStack = new TransformStackElement[deltaStackLength];

      System.arraycopy(gcTransformStack, referenceStack.length, deltaTransformStack, 0, deltaStackLength);

      SVGGraphicContext deltaGC = new SVGGraphicContext(groupDelta, graphicElementDelta, deltaTransformStack);

      return deltaGC;
   }

   /**
    * Processes the difference between two Maps. The code assumes
    * that the input Maps have the same key sets. Values in map that
    * are different from values in referenceMap are place in the
    * returned delta Map.
    */
   static Map processDeltaMap(Map map, Map referenceMap) {
      // no need to be synch => HashMap
      Map mapDelta = new HashMap<>();
      Iterator iter = map.keySet().iterator();
      while (iter.hasNext()) {
         String key = (String) iter.next();
         String value = (String) map.get(key);
         String refValue = (String) referenceMap.get(key);
         if (!value.equals(refValue)) {
            /*if(key.equals(SVG_TRANSFORM_ATTRIBUTE)){
                  // Special handling for the transform attribute.
                  // At this point in the processing, the transform
                  // in map has to be a substring of the one in
                  // referenceMap. see the addElement member.
                  value = value.substring(refValue.length()).trim();
                  }*/
            mapDelta.put(key, value);
         }
      }
      return mapDelta;
   }

   /**
    * Reset the state of this object to handle a new currentGroup
    */
   public void recycleCurrentGroup() {
      // Create new initial current group node
      currentGroup = ((ControlledDOMTreeManager) domTreeManager).getDOMFactoryAsPublic().
         createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
   }

   public Element createGroup(String id) {
      Element group = ((ControlledDOMTreeManager) domTreeManager).getDOMFactoryAsPublic().
         createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
      if (id != null) {
         group.setAttributeNS(null, SVG_ID_ATTRIBUTE, id);
      }
      currentGroup = group;
      return group;
   }

   /**
    * Adds a node to the current group, if possible
    *
    * @param element child Element to add to the group
    * @param method DRAW or FILL
    */
   @Override
   public void addElement(Element element, short method) {
      //
      // If this is the first child to be added to the
      // currentGroup, 'freeze' the style attributes.
      //
      if (!currentGroup.hasChildNodes()) {
         currentGroup.appendChild(element);

         groupGC = domTreeManager.getGraphicContextConverter().toSVG(gc);
         SVGGraphicContext deltaGC = processDeltaGC(groupGC, ((ControlledDOMTreeManager) domTreeManager).getGraphicContextAsPublic());
         ((ControlledDOMTreeManager) domTreeManager).getStyleHandlerAsPublic().
            setStyle(currentGroup, deltaGC.getGroupContext(), ((ControlledDOMTreeManager) domTreeManager).getGeneratorContextAsPublic());
         if ((method & DRAW) == 0) {
            // force stroke:none
            deltaGC.getGraphicElementContext().put(SVG_STROKE_ATTRIBUTE, SVG_NONE_VALUE);
         }
         if ((method & FILL) == 0) {
            // force fill:none
            deltaGC.getGraphicElementContext().put(SVG_FILL_ATTRIBUTE, SVG_NONE_VALUE);
         }
         ((ControlledDOMTreeManager) domTreeManager).getStyleHandlerAsPublic().
            setStyle(element, deltaGC.getGraphicElementContext(), ((ControlledDOMTreeManager) domTreeManager).getGeneratorContextAsPublic());
         setTransform(currentGroup, deltaGC.getTransformStack());
      } else {
         if (!gc.isTransformStackValid()) {
            gc.validateTransformStack();
         }
         // There are children nodes already. Find
         // out delta between current gc and group
         // context
         //
         SVGGraphicContext elementGC = domTreeManager.getGraphicContextConverter().toSVG(gc);
         SVGGraphicContext deltaGC = processDeltaGC(elementGC, groupGC);

         // If there are less than the maximum number
         // of differences, then add the node to the current
         // group and set its attributes
         trimContextForElement(deltaGC, element);
         currentGroup.appendChild(element);
         // as there already are children we put all
         // attributes (group + element) on the element itself.
         if ((method & DRAW) == 0) {
            // force stroke:none
            deltaGC.getContext().put(SVG_STROKE_ATTRIBUTE, SVG_NONE_VALUE);
         }
         if ((method & FILL) == 0) {
            // force fill:none
            deltaGC.getContext().put(SVG_FILL_ATTRIBUTE, SVG_NONE_VALUE);
         }
         ((ControlledDOMTreeManager) domTreeManager).getStyleHandlerAsPublic().
            setStyle(element, deltaGC.getContext(), ((ControlledDOMTreeManager) domTreeManager).getGeneratorContextAsPublic());
         setTransform(element, deltaGC.getTransformStack());
         currentGroup.appendChild(element);
      }
   }
}
