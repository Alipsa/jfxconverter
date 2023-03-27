/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
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
 * A Node in an XML File, with an associated comment.
 *
 * @version 1.2.40
 */
public class XMLCommentedNode extends XMLNode implements CommentedNode, NumberedNode {
   private String comment = null;
   private int lineNumber = -1;

   /**
    * Create the Node, with a local part.
    *
    * @param nodeName the Node name
    */
   public XMLCommentedNode(String nodeName) {
      super(nodeName);
   }

   /**
    * Create the Node, with a local part and a comment.
    *
    * @param nodeName the Node name
    * @param comment the comment
    */
   public XMLCommentedNode(String nodeName, String comment) {
      super(nodeName);
      this.comment = comment;
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    */
   public XMLCommentedNode(QName qname) {
      super(qname);
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    * @param comment the comment
    */
   public XMLCommentedNode(QName qname, String comment) {
      super(qname);
      this.comment = comment;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    */
   public XMLCommentedNode(XMLNode parent, String nodeName) {
      super(parent, nodeName);
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    * @param comment the comment
    */
   public XMLCommentedNode(XMLNode parent, String nodeName, String comment) {
      super(parent, nodeName);
      this.comment = comment;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param qname the Node qualified name
    */
   public XMLCommentedNode(XMLNode parent, QName qname) {
      super(parent, qname);
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param qname the Node qualified name
    * @param comment the comment
    */
   public XMLCommentedNode(XMLNode parent, QName qname, String comment) {
      super(parent, qname);
      this.comment = comment;
   }

   /**
    * Set the node comment.
    *
    * @param comment the comment
    */
   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   /**
    * Return the node comment.
    *
    * @return the node comment
    */
   @Override
   public String getComment() {
      return comment;
   }

   /**
    * Return true if the node has a comment.
    *
    * @return true if the node has a comment
    */
   @Override
   public boolean hasComment() {
      return comment != null;
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
   public XMLCommentedNode copy() {
      return copy(true);
   }

   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   @Override
   public XMLCommentedNode copy(boolean deep) {
      XMLCommentedNode node = createCopyInstanceImpl();
      node.cData = cData;
      node.comment = comment;
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
            child = child.copy();
            child.setParent(node);
            node.children.add(child);
         }
      }
      return node;
   }

   @Override
   XMLCommentedNode createCopyInstanceImpl() {
      XMLCommentedNode node = new XMLCommentedNode(nodeParent, qname);
      node.comment = comment;
      return node;
   }
}
