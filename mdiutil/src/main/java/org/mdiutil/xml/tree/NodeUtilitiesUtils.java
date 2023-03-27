/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.mdiutil.lang.HTMLEscaper;

/**
 * An utility class proposing methods used internally by the {@link XMLNodeUtilities} and {@link XMLNodeUtilities2} classes.
 *
 * @since 1.2.40
 */
public class NodeUtilitiesUtils {

   private NodeUtilitiesUtils() {
   }

   /**
    * Creates a QName for a name which can have a colon.
    *
    * @param name the name
    * @return the QName
    */
   static QName getQName(String name) {
      int colonIndex = name.indexOf(":");
      if (colonIndex != -1) {
         return new QName(XMLConstants.NULL_NS_URI, name.substring(colonIndex + 1), name.substring(0, colonIndex));
      } else {
         return new QName(name);
      }
   }

   /**
    * Return the first child node with a specified QName.
    *
    * @param stack the nodes stack
    * @param node the node
    * @param qname the QName
    * @param deep true if search must look under children nodes
    * @return the first child node or null
    */
   static XMLNode lookForFirstChild(Stack<XMLNode> stack, XMLNode node, QName qname, boolean deep) {
      if (strictEquals(node.getQualifiedName(), qname)) {
         return node;
      }
      if (!deep && !stack.isEmpty()) {
         return null;
      }
      if (node.hasChildren()) {
         stack.push(node);
         XMLNode foundNode = null;
         Iterator<XMLNode> it = node.getChildren().iterator();
         while (it.hasNext()) {
            XMLNode child = it.next();
            foundNode = lookForFirstChild(stack, child, qname, deep);
            if (foundNode != null) {
               return foundNode;
            }
         }
         stack.pop();
         return foundNode;
      } else {
         return null;
      }
   }

   /**
    * Return the children with a specified QName.
    *
    * @param list the lsit of children nodes to return
    * @param stack the nodes stack
    * @param node the node
    * @param qname the QName
    * @param deep true if search must look under children nodes
    * @return the first child node or null
    */
   static boolean lookForChildren(List<XMLNode> list, Stack<XMLNode> stack, XMLNode node, QName qname, boolean deep) {
      if (strictEquals(node.getQualifiedName(), qname)) {
         list.add(node);
      }
      if (!deep && !stack.isEmpty()) {
         return !list.isEmpty();
      }
      if (node.hasChildren()) {
         stack.push(node);
         boolean found = false;
         Iterator<XMLNode> it = node.getChildren().iterator();
         while (it.hasNext()) {
            XMLNode child = it.next();
            lookForChildren(list, stack, child, qname, deep);
         }
         stack.pop();
         return found;
      } else {
         return false;
      }
   }

   /**
    * Return true if two QNames are equals.
    *
    * @param name1 the first QName
    * @param name2 the second QName
    * @return true if the two QNames are equals
    */
   static boolean strictEquals(QName name1, QName name2) {
      if (name1.equals(name2)) {
         String prefix1 = name1.getPrefix();
         String prefix2 = name2.getPrefix();
         if (prefix1 == null && prefix2 == null) {
            return true;
         } else if (prefix1 != null && prefix2 != null) {
            return prefix1.equals(prefix2);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the name of anattribute with a qualified name.
    *
    * @param qname the qualified name
    * @return the name as a String
    */
   static String getAttrName(SortableQName qname) {
      String localPart = qname.getLocalPart();
      if (localPart.equals("xmlns") || localPart.startsWith("xmlns:")) {
         return localPart;
      } else {
         String prefix = qname.getPrefix();
         if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return localPart;
         } else {
            return prefix + ":" + qname.getLocalPart();
         }
      }
   }

   /**
    * Prints a node.
    *
    * @param buf the StringBuilder
    * @param node the node
    * @param tabTotal the total value for tabs
    * @param tab the tab
    * @param isFirst true if this is the first child of its parent
    * @param isLast true if this is the last child of its parent
    */
   static void printNode(StringBuilder buf, XMLNode node, String tabTotal, String tab, boolean isFirst, boolean isLast) {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }
      if (!parentHasCData | (parentHasCData && !isFirst)) {
         buf.append(tabTotal);
      }
      if (node.hasComment()) {
         buf.append("<!-- ");
         buf.append(node.getComment().trim());
         buf.append(" -->");
         buf.append("\n");
         if (!parentHasCData | (parentHasCData && !isFirst)) {
            buf.append(tabTotal);
         }
      }
      buf.append("<");
      NodeUtilitiesUtils.writeNodeQName(buf, node);
      writeBoundPrefixes(buf, node);
      Iterator<Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Entry<SortableQName, String> entry = it.next();
         buf.append(" ").append(NodeUtilitiesUtils.getAttrName(entry.getKey())).append("=\"").append(entry.getValue()).append("\"");
      }
      if (node.hasChildren()) {
         if (node.hasCDATA()) {
            buf.append(">").append(node.getEscapedCDATA());
         } else {
            buf.append(">\n");
         }
         String tabChildren = tabTotal + tab;
         boolean isFirstInParent = true;
         Iterator<XMLNode> it2 = node.getChildren().iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            printNode(buf, child, tabChildren, tab, isFirstInParent, false);
            isFirstInParent = false;
         }
         if (!parentHasCData) {
            buf.append(tabTotal);
         }
         buf.append("</");
         NodeUtilitiesUtils.endWriteNodeQName(buf, node);
         buf.append(">");
         if (!isLast) {
            buf.append("\n");
         }
      } else {
         if (node.getCDATA() != null) {
            buf.append(">").append(node.getEscapedCDATA());
            buf.append("</");
            NodeUtilitiesUtils.endWriteNodeQName(buf, node);
            buf.append(">");
         } else {
            buf.append("/>");
         }
         if (!isLast) {
            buf.append("\n");
         }
      }
   }

   /**
    * Prints a node.
    *
    * @param writer the writer
    * @param node the node
    * @param tabTotal the total value for tabs
    * @param tab the tab
    * @param isFirst true if this is the first child of its parent
    * @param isLast true if this is the last child of its parent
    */
   static void printNode(BufferedWriter writer, XMLNode node, BoundPrefix boundPrefix, String tabTotal, String tab, boolean isFirst, boolean isLast) throws IOException {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }
      if (!parentHasCData | (parentHasCData && !isFirst)) {
         writer.write(tabTotal);
      }
      if (node.hasComment()) {
         writer.write("<!-- ");
         writer.write(node.getComment().trim());
         writer.write(" -->");
         writer.newLine();
         if (!parentHasCData | (parentHasCData && !isFirst)) {
            writer.write(tabTotal);
         }
      }
      writer.write("<");
      writeNodeQName(writer, node);
      boundPrefix = new BoundPrefix(boundPrefix);
      SortableQName schemaLocationDecl = boundPrefix.schemaLocationDecl;
      String schemaLocationValue = boundPrefix.schemaLocationValue;
      Map<String, String> localPrefixes = boundPrefix.getResultBoundPrefixes(node);
      writeBoundPrefixes(writer, localPrefixes);
      Iterator<Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Entry<SortableQName, String> entry = it.next();
         SortableQName qname = entry.getKey();
         writer.write(" ");
         writer.write(getAttrName(qname));
         writer.write("=\"");
         writer.write(HTMLEscaper.escapeXMLAttribute(entry.getValue()));
         writer.write("\"");
      }
      if (node.hasChildren()) {
         writer.write(">");
         boolean hasCData = false;
         if (node.getCDATA() != null) {
            writer.write(node.getEscapedCDATA());
            hasCData = true;
         } else {
            writer.newLine();
         }
         String tabChildren = tabTotal + tab;
         List<XMLNode> children = node.getChildren();
         boolean hasMoreThanOneChild = children.size() > 1;
         boolean isFirstForParent = true;
         Iterator<XMLNode> it2 = children.iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            printNode(writer, child, boundPrefix, tabChildren, tab, isFirstForParent, false);
            isFirstForParent = false;
         }
         if (!hasCData || hasMoreThanOneChild) {
            writer.write(tabTotal);
         }
         writer.write("</");
         endWriteNodeQName(writer, node);
         writer.write(">");
         if (!isLast) {
            writer.newLine();
         }
      } else {
         if (node.getCDATA() != null) {
            writer.write(">");
            writer.write(node.getEscapedCDATA());
            writer.write("</");
            endWriteNodeQName(writer, node);
            writer.write(">");
         } else {
            writer.write("/>");
         }
         if (!isLast) {
            writer.newLine();
         }
      }
   }

   private static void writeBoundPrefixes(StringBuilder buf, XMLNode node) {
      if (node.hasBoundPrefixes()) {
         Iterator<Entry<String, String>> it = node.getBoundPrefixes().entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String prefix = entry.getValue();
            buf.append(" ");
            if (!prefix.isEmpty()) {
               buf.append("xmlns:").append(prefix);
            } else {
               buf.append("xmlns");
            }
            buf.append("=\"").append(entry.getKey()).append("\"");
         }
      }
   }

   private static void writeBoundPrefixes(BufferedWriter writer, Map<String, String> boundPrefixes) throws IOException {
      if (boundPrefixes != null) {
         Iterator<Entry<String, String>> it = boundPrefixes.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String prefix = entry.getValue();
            writer.write(" ");
            if (!prefix.isEmpty()) {
               writer.write("xmlns:");
               writer.write(prefix);
            } else {
               writer.write("xmlns");
            }
            writer.write("=\"");
            writer.write(entry.getKey());
            writer.write("\"");
         }
      }
   }

   static void endWriteNodeQName(StringBuilder buf, XMLNode node) {
      String name;
      // prefix
      String prefix = node.getPrefix();
      if (prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         name = prefix + ":" + node.getName();
      } else {
         name = node.getName();
      }
      buf.append(name);
   }

   static void writeNodeQName(StringBuilder buf, XMLNode node) {
      // namespace URI
      String namespaceURI = node.getNamespaceURI();
      if (namespaceURI.equals(XMLConstants.NULL_NS_URI)) {
         namespaceURI = null;
      }
      String name;
      // prefix
      String prefix = node.getPrefix();
      if (prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         name = prefix + ":" + node.getName();
         buf.append(name);
         if (namespaceURI != null) {
            buf.append(" xmlns:");
            buf.append(prefix);
            buf.append("=\"");
            buf.append(namespaceURI);
            buf.append("\"");
         }
      } else {
         name = node.getName();
         buf.append(name);
         if (namespaceURI != null) {
            buf.append(" xmlns=\"");
            buf.append(namespaceURI);
            buf.append("\"");
         }
      }
   }

   private static void endWriteNodeQName(BufferedWriter writer, XMLNode node) throws IOException {
      String name;
      // prefix
      String prefix = node.getPrefix();
      if (prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         name = prefix + ":" + node.getName();
      } else {
         name = node.getName();
      }
      writer.write(name);
   }

   private static void writeNodeQName(BufferedWriter writer, XMLNode node) throws IOException {
      // namespace URI
      String namespaceURI = node.getNamespaceURI();
      if (namespaceURI.equals(XMLConstants.NULL_NS_URI)) {
         namespaceURI = null;
      }
      String name;
      // prefix
      String prefix = node.getPrefix();
      if (prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         name = prefix + ":" + node.getName();
         writer.write(name);
         if (namespaceURI != null) {
            writer.write(" xmlns:");
            writer.write(prefix);
            writer.write("=\"");
            writer.write(namespaceURI);
            writer.write("\"");
         }
      } else {
         name = node.getName();
         writer.write(name);
         if (namespaceURI != null) {
            writer.write(" xmlns=\"");
            writer.write(namespaceURI);
            writer.write("\"");
         }
      }
   }

   static void writeEncoding(StringBuilder buf, String encoding) {
      if (encoding != null) {
         buf.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>");
         buf.append("\n");
      }
   }

   static void writeEncoding(BufferedWriter writer, String encoding) throws IOException {
      if (encoding != null) {
         writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
         writer.newLine();
      }
   }
}
