/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import javax.xml.namespace.QName;

/**
 * A root Node in an XML File.
 *
 * @version 1.2.40
 */
public class XMLRoot extends XMLNode {
   /**
    * The XML encoding.
    */
   protected String encoding = null;

   /**
    * Create the root Node, with a local part.
    *
    * @param nodeName the Node name
    * @see XMLNode#XMLNode(String)
    */
   public XMLRoot(String nodeName) {
      super(nodeName);
   }

   /**
    * Create the root Node.
    *
    * @param qName the Node qualified name
    */
   public XMLRoot(QName qName) {
      super(qName);
   }

   /**
    * Set the encoding of the XML file.
    *
    * @param encoding the encoding of the XML file
    */
   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   /**
    * Return the declared encoding of the XML file. Return null if there is no declared encoding in the file.
    *
    * @return the declared encoding of the XML file
    */
   public String getEncoding() {
      return encoding;
   }

   @Override
   XMLRoot createCopyInstanceImpl() {
      XMLRoot node = new XMLRoot(qname);
      node.encoding = encoding;
      return node;
   }

   private XMLCommentedRoot createCommentedRootCopyInstanceImpl() {
      XMLCommentedRoot node = new XMLCommentedRoot(qname);
      node.encoding = encoding;
      return node;
   }

   /**
    * Create a copy of this node as a commented root. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   @Override
   public XMLCommentedRoot copyAsCommentedNode() {
      XMLCommentedRoot node = createCommentedRootCopyInstanceImpl();
      node.cData = cData;
      if (boundPrefixes != null) {
         node.boundPrefixes = new HashMap<>(boundPrefixes);
      }            
      Iterator<Entry<SortableQName, String>> it = attributes.entrySet().iterator();
      while (it.hasNext()) {
         Entry<SortableQName, String> entry = it.next();
         node.attributes.put(entry.getKey(), entry.getValue());
      }
      Iterator<XMLNode> it2 = children.iterator();
      while (it2.hasNext()) {
         XMLNode child = it2.next();
         child = child.copyAsCommentedNode();
         child.setParent(node);
         node.children.add(child);
      }

      return node;
   }

   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   @Override
   public XMLRoot copy() {
      return copy(true);
   }

   /**
    * Create a copy of this node. It deep is true, then will also copy all the children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   @Override
   public XMLRoot copy(boolean deep) {
      XMLRoot node = createCopyInstanceImpl();
      node.cData = cData;
      node.encoding = encoding;
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


   @Override
   public int hashCode() {
      int hash = 7;
      hash = 53 * hash + Objects.hashCode(this.encoding);
      hash = 53 * hash + Objects.hashCode(this.qname);
      hash = 53 * hash + Objects.hashCode(this.children);
      hash = 53 * hash + Objects.hashCode(this.attributes);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final XMLRoot other = (XMLRoot) obj;
      if (!Objects.equals(this.encoding, other.encoding)) {
         return false;
      }
      if (!Objects.equals(this.qname, other.qname)) {
         return false;
      }
      if (!Objects.equals(this.children, other.children)) {
         return false;
      }
      if (!Objects.equals(this.attributes, other.attributes)) {
         return false;
      }
      return true;
   }

}
