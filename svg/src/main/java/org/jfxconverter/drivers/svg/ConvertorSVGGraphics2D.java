/*
Copyright (c) 2016, 2017, 2020 Herve Girod
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

import org.apache.batik.svggen.SVGCSSStyler;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.*;

import java.io.IOException;
import java.io.Writer;

/**
 * A SVGGraphics2D which has an added extension handler to handle Color gradients.
 *
 * @version 0.23
 */
public class ConvertorSVGGraphics2D extends SVGGraphics2D {
   public ConvertorSVGGraphics2D(Document doc) {
      super(doc);
      this.setExtensionHandler(new SVGExtensionHandler());
   }

   /**
    * Return the svg root node of the SVG document.
    *
    * @return the svg root node of the SVG document
    */
   @Override
   public Element getRoot() {
      Document doc = getDOMFactory();

      Element elt = doc.getDocumentElement();
      if (elt.getTagName().equals(SVGConstants.SVG_SVG_TAG)) {
         return super.getRoot(elt);
      } else {
         NodeList list = elt.getElementsByTagName(SVGConstants.SVG_SVG_TAG);
         if ((list == null) || (list.getLength() == 0)) {
            return super.getRoot(null);
         } else {
            return super.getRoot((Element) (list.item(0)));
         }
      }
   }

   /**
    * Stream the content.
    * Changes from the Batik original class: use our custom XmlWriter implementation.
    *
    * @param svgRoot root element to stream out
    * @param writer output
    * @param useCss defines whether the output SVG should use CSS style
    * @param escaped defines if the characters will be escaped
    * properties as opposed to plain attributes.
    */
   @Override
   public void stream(Element svgRoot, Writer writer, boolean useCss, boolean escaped) throws SVGGraphics2DIOException {
      Node rootParent = svgRoot.getParentNode();
      Node nextSibling = svgRoot.getNextSibling();

      try {
         //
         // Enforce that the default and xlink namespace
         // declarations appear on the root element
         //
         svgRoot.setAttributeNS(XMLNS_NAMESPACE_URI,
            XMLNS_PREFIX,
            SVG_NAMESPACE_URI);

         svgRoot.setAttributeNS(XMLNS_NAMESPACE_URI,
            XMLNS_PREFIX + ":" + XLINK_PREFIX,
            XLINK_NAMESPACE_URI);

         DocumentFragment svgDocument = svgRoot.getOwnerDocument().createDocumentFragment();
         svgDocument.appendChild(svgRoot);

         if (useCss) {
            SVGCSSStyler.style(svgDocument);
         }

         XmlWriter.writeXml(svgDocument, writer, escaped);
         writer.flush();
      } catch (SVGGraphics2DIOException e) {
         // this catch prevents from catching an SVGGraphics2DIOException
         // and wrapping it again in another SVGGraphics2DIOException
         // as would do the second catch (XmlWriter throws SVGGraphics2DIO
         // Exception but flush throws IOException)
         generatorCtx.getErrorHandler().handleError(e);
      } catch (IOException io) {
         generatorCtx.getErrorHandler().handleError(new SVGGraphics2DIOException(io));
      } finally {
         // Restore the svgRoot to its original tree position
         if (rootParent != null) {
            if (nextSibling == null) {
               rootParent.appendChild(svgRoot);
            } else {
               rootParent.insertBefore(svgRoot, nextSibling);
            }
         }
      }
   }
}
