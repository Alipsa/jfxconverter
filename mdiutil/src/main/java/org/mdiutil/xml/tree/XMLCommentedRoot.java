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
 * A root Node in an XML File with an associated comment.
 *
 * @version 1.2.40
 */
public class XMLCommentedRoot extends XMLRoot implements CommentedNode {
   private String comment = null;

   /**
    * Create the Node, with a local part.
    *
    * @param nodeName the Node name
    */
   public XMLCommentedRoot(String nodeName) {
      super(nodeName);
   }

   /**
    * Create the Node, with a local part and a comment.
    *
    * @param nodeName the Node name
    * @param comment the comment
    */
   public XMLCommentedRoot(String nodeName, String comment) {
      super(nodeName);
      this.comment = comment;
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    */
   public XMLCommentedRoot(QName qname) {
      super(qname);
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    * @param comment the comment
    */
   public XMLCommentedRoot(QName qname, String comment) {
      super(qname);
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

   @Override
   XMLCommentedRoot createCopyInstanceImpl() {
      XMLCommentedRoot node = new XMLCommentedRoot(qname, comment);
      node.encoding = encoding;
      return node;
   }
   
   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   @Override
   public XMLCommentedRoot copy() {
      return copy(true);
   }   

   /**
    * Create a copy of this node. It deep is true, then will also copy all the children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   @Override
   public XMLCommentedRoot copy(boolean deep) {
      XMLCommentedRoot node = createCopyInstanceImpl();
      node.cData = cData;
      node.comment = comment;
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
            child = child.copy(true);
            child.setParent(node);
            node.children.add(child);
         }
      }
      return node;
   }
}
