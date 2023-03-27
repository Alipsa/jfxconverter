/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.namespace.QName;

/**
 * A Node in an XML File with a line number.
 *
 * @version 1.2.40
 */
public class XMLNumberedNode extends XMLNode {
   private int lineNumber = -1;

   /**
    * Create the Node, with a local part.
    *
    * @param nodeName the Node name
    * @see XMLNode#XMLNode(String)
    */
   public XMLNumberedNode(String nodeName) {
      super(nodeName);
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    */
   public XMLNumberedNode(QName qname) {
      super(qname);
   }

   /**
    * Create the Node.
    *
    * @param nodeName the Node name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedNode(String nodeName, int lineNumber) {
      super(nodeName);
      this.lineNumber = lineNumber;
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedNode(QName qname, int lineNumber) {
      super(qname);
      this.lineNumber = lineNumber;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    */
   public XMLNumberedNode(XMLNode parent, String nodeName) {
      super(parent, nodeName);
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param qname the Node qualified name
    */
   public XMLNumberedNode(XMLNode parent, QName qname) {
      super(parent, qname);
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedNode(XMLNode parent, String nodeName, int lineNumber) {
      super(parent, nodeName);
      this.lineNumber = lineNumber;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param qname the Node qualified name
    * @param lineNumber the line number of the Node in the XML file
    */
   public XMLNumberedNode(XMLNode parent, QName qname, int lineNumber) {
      super(parent, qname);
      this.lineNumber = lineNumber;
   }

   @Override
   XMLNumberedNode createCopyInstanceImpl() {
      XMLNumberedNode node = new XMLNumberedNode(nodeParent, qname, lineNumber);
      return node;
   }
   
   /**
    * Set the line number of the Node in the XML file. Do ntohing by default.
    *
    * @param lineNumber the line number
    */
   @Override
   public void setLineNumber(int lineNumber) {
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
   
   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   @Override
   public XMLNumberedNode copy() {
      return copy(true);
   }     

   /**
    * Create a copy of this node. It deep is true, then will also copy all the children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   @Override
   public XMLNumberedNode copy(boolean deep) {
      XMLNumberedNode node = createCopyInstanceImpl();
      node.cData = cData;
      node.lineNumber = lineNumber;
      if (boundPrefixes != null) {
         node.boundPrefixes = new HashMap<>(boundPrefixes);
      }            
      Iterator<Entry<SortableQName, String>> it = attributes.entrySet().iterator();
      while (it.hasNext()) {
         Entry<SortableQName, String> entry = it.next();
         node.attributes.put(entry.getKey(), entry.getValue());
      }
      if (deep) {
         Iterator<XMLNode> it2 = children.iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            child = child.copy(true);
            child.setParent(node);
            node.children.add(child);
         }
      }
      return node;
   }
}
