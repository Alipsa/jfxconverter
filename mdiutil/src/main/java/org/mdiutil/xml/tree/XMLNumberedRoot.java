/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * A root Node in an XML File with a comment.
 *
 * @version 1.2.40
 */
public class XMLNumberedRoot extends XMLRoot {
   private int lineNumber = -1;

   /**
    * Create the root Node, with a local part.
    *
    * @param nodeName the Node name
    * @see XMLNode#XMLNode(String)
    */
   public XMLNumberedRoot(String nodeName) {
      super(nodeName);
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    */
   public XMLNumberedRoot(QName qname) {
      super(qname);
   }

   /**
    * Create the root Node.
    *
    * @param nodeName the Node name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedRoot(String nodeName, int lineNumber) {
      super(nodeName);
      this.lineNumber = lineNumber;
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedRoot(QName qname, int lineNumber) {
      super(qname);
      this.lineNumber = lineNumber;
   }

   /**
    * Return the line number of the Node in the XML file.
    *
    * @return the line number
    */
   @Override
   public int getLineNumber() {
      return lineNumber;
   }

   /**
    * Return true if the Node has an associated line number.
    *
    * @return true if the Node has an associated line number
    */
   @Override
   public boolean hasLineNumber() {
      return lineNumber != -1;
   }

   @Override
   XMLNumberedRoot createCopyInstanceImpl() {
      XMLNumberedRoot node = new XMLNumberedRoot(qname, lineNumber);
      node.encoding = encoding;
      node.lineNumber = lineNumber;
      return node;
   }
   
   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   @Override
   public XMLNumberedRoot copy() {
      return copy(true);
   }      

   /**
    * Create a copy of this node. It deep is true, then will also copy all the children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   @Override
   public XMLNumberedRoot copy(boolean deep) {
      XMLNumberedRoot node = createCopyInstanceImpl();
      node.cData = cData;
      node.lineNumber = lineNumber;
      node.encoding = encoding;
      if (boundPrefixes != null) {
         node.boundPrefixes = new HashMap<>(boundPrefixes);
      }            
      Iterator<Map.Entry<SortableQName, String>> it = attributes.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<SortableQName, String> entry = it.next();
         node.attributes.put(entry.getKey(), entry.getValue());
      }
      if (deep) {
         Iterator<XMLNode> it2 = children.iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            child = child.copy();
            child.setParent(node);
            node.children.add(child);
         }
      }

      return node;
   }
}
