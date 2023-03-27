/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2020, 2021, 2022, 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.mdiutil.lang.HTMLEscaper;

/**
 * A Node in an XML File.
 *
 * @version 1.2.44
 */
public class XMLNode implements Iterable<XMLNode>, CommentedNode, NumberedNode {
   private static final String XMNLS = "xùnls";
   private static final String SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
   private static final Pattern SCHEMA_LOC = Pattern.compile("(\\S+)\\s+(\\S+)");
   /**
    * The node name.
    */
   protected QName qname = null;
   /**
    * The node parent.
    */
   protected XMLNode nodeParent = null;
   /**
    * The children nodes.
    */
   protected final List<XMLNode> children = new ArrayList<>();
   /**
    * The attributes.
    */
   protected final Map<SortableQName, String> attributes = new TreeMap<>();

   /**
    * The CDATA content.
    */
   protected String cData = null;
   /**
    * The bounded prefixes.
    */
   protected Map<String, String> boundPrefixes = null;
   /**
    * The schemaLocation declaration.
    */
   private SortableQName schemaLocationDecl = null;
   /**
    * The schemaLocation value.
    */
   private String schemaLocationValue = null;

   /**
    * Create the Node, with a local part.
    *
    * <h1>Qualified name</h1>
    * If the name contains a colon, it will be considered as a qualified name
    * with a prefix and a null URI.
    *
    * For example <code>new XMLNode("h:element")</code> is equivalent to
    * <code>new XMLNode(new QName(XMLConstants.NULL_NS_URI, "element", "h"))</code>
    *
    * But <code>new XMLNode("element")</code> is equivalent to
    * <code>new XMLNode(new QName(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX))</code>
    *
    * @param nodeName the Node name
    */
   public XMLNode(String nodeName) {
      int colonIndex = nodeName.indexOf(':');
      if (colonIndex != -1) {
         String localPart = nodeName.substring(colonIndex + 1);
         String prefix = nodeName.substring(0, colonIndex);
         this.qname = new QName(XMLConstants.NULL_NS_URI, localPart, prefix);
      } else {
         this.qname = new QName(nodeName);
      }
   }

   /**
    * Create the Node.
    *
    * @param qname the Node qualified name
    */
   public XMLNode(QName qname) {
      this.qname = qname;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    */
   public XMLNode(XMLNode parent, String nodeName) {
      this.qname = new QName(nodeName);
      this.nodeParent = parent;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param qname the Node qualified name
    */
   public XMLNode(XMLNode parent, QName qname) {
      this.qname = qname;
      this.nodeParent = parent;
   }

   /**
    * Bind a prefix to an URI.
    *
    * @param prefix the prefix
    * @param uri the URI
    */
   public void bindPrefix(String prefix, String uri) {
      if (boundPrefixes == null) {
         boundPrefixes = new HashMap<>();
      }
      if (!boundPrefixes.containsKey(uri)) {
         boundPrefixes.put(uri, prefix);
      }
   }

   /**
    * Set the bound prefixes.
    *
    * @param boundPrefixes the prefixes.
    */
   public void setBoundPrefixes(Map<String, String> boundPrefixes) {
      this.boundPrefixes = boundPrefixes;
   }

   /**
    * Return the prefixes bounded to URIs.
    *
    * @return the bound prefixes
    */
   public Map<String, String> getBoundPrefixes() {
      return boundPrefixes;
   }

   /**
    * Return true if there are the prefixes bounded to URIs.
    *
    * @return true if there are the prefixes bounded to URIs
    */
   public boolean hasBoundPrefixes() {
      return boundPrefixes != null;
   }

   /**
    * Return true if there is a prefix bounded with an URI declaration.
    *
    * @param uri the URI declaration
    * @return true if there is a prefix bounded with the URI declaration
    */
   public boolean hasBoundPrefix(String uri) {
      if (boundPrefixes == null) {
         return false;
      }
      return boundPrefixes.containsKey(uri);
   }

   /**
    * Return the prefix bounded with an URI declaration.
    *
    * @param uri the URI declaration
    * @return the prefix
    */
   public String getBoundedPrefix(String uri) {
      if (boundPrefixes == null) {
         return null;
      }
      return boundPrefixes.get(uri);
   }

   /**
    * Return the schema location declaration.
    *
    * @return the schema location declaration
    */
   public SortableQName getSchemaLocationDeclaration() {
      return schemaLocationDecl;
   }

   /**
    * Return the schema location value.
    *
    * @return the schema location value
    */
   public String getSchemaLocationValue() {
      return schemaLocationValue;
   }

   /**
    * Return true if there is a schema location declaration.
    *
    * @return true if there is a schema location declaration
    */
   public boolean hasSchemaLocationDeclaration() {
      return schemaLocationDecl != null;
   }

   private boolean bindSchemaLocationToEmptyPrefix(String prefix, String localPart, String value) {
      if (boundPrefixes != null && boundPrefixes.containsKey(SCHEMA_INSTANCE)) {
         String _prefix = boundPrefixes.get(SCHEMA_INSTANCE);
         if (_prefix.equals(prefix) && localPart.equals("schemaLocation")) {
            Matcher m = SCHEMA_LOC.matcher(value);
            if (m.matches()) {
               schemaLocationValue = m.group(1);
               schemaLocationDecl = new SortableQName(SCHEMA_INSTANCE, "schemaLocation", prefix);
               this.bindPrefix("", schemaLocationValue);
               return true;
            }
         }
      }
      return false;
   }

   private XMLCommentedNode createCommentedCopyInstanceImpl() {
      XMLCommentedNode node = new XMLCommentedNode(nodeParent, qname);
      return node;
   }

   XMLNode createCopyInstanceImpl() {
      XMLNode node = new XMLNode(nodeParent, qname);
      return node;
   }

   /**
    * Create a copy of this node as a commented node. It will also copy all the
    * children under this node.
    *
    * @return the Node copy.
    */
   public XMLNode copyAsCommentedNode() {
      XMLCommentedNode node = createCommentedCopyInstanceImpl();
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
    * Create a copy of this node. It will also copy all the children under this
    * node.
    *
    * @return the Node copy.
    */
   public XMLNode copy() {
      return copy(true);
   }

   /**
    * Create a copy of this node. It deep is true, then will also copy all the
    * children under this node.
    *
    * @param deep true for a deep copy
    * @return the Node copy.
    */
   public XMLNode copy(boolean deep) {
      XMLNode node = createCopyInstanceImpl();
      node.cData = cData;
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

   /**
    * Return the list of all children of this node having a specified qualified
    * name. The list will include all children, including children of children
    * Nodes.
    *
    * @param qname the qualified name
    * @return the list
    */
   public List<XMLNode> getAllChildren(QName qname) {
      List<XMLNode> list = new ArrayList<>();
      Iterator<XMLNode> it = children.iterator();
      while (it.hasNext()) {
         XMLNode node = it.next();
         if (node.getQualifiedName().equals(qname)) {
            list.add(node);
         }
         List<XMLNode> childList = node.getAllChildren(qname);
         list.addAll(childList);
      }
      return list;
   }

   /**
    * Return the list of all children of this node having a specified name. The
    * list will include all children, including children of children Nodes.
    *
    * @param name the name
    * @return the list
    */
   public List<XMLNode> getAllChildren(String name) {
      List<XMLNode> list = new ArrayList<>();
      Iterator<XMLNode> it = children.iterator();
      while (it.hasNext()) {
         XMLNode node = it.next();
         if (node.getName().equals(name)) {
            list.add(node);
         }
         List<XMLNode> childList = node.getAllChildren(name);
         list.addAll(childList);
      }
      return list;
   }

   /**
    * Return the Node parent (or null if the Node is the root of the XML File).
    *
    * @return the Node parent (or null if the Node is the root of the XML File)
    */
   public XMLNode getParent() {
      return nodeParent;
   }

   /**
    * Set the Node parent.
    *
    * @param parent the Node parent
    */
   protected void setParent(XMLNode parent) {
      this.nodeParent = parent;
   }

   /**
    * Return the Node "complete" name, including the prefix.
    *
    * @return the Node name including the prefix
    */
   public String getPrefixedName() {
      if (qname.getPrefix() != null && !qname.getPrefix().isEmpty()) {
         return qname.getPrefix() + ":" + qname.getLocalPart();
      } else {
         return qname.getLocalPart();
      }
   }

   /**
    * Return the Node local part name. Same result as {@link #getLocalPart()}.
    *
    * @return the Node local part name
    */
   public String getName() {
      return qname.getLocalPart();
   }

   /**
    * Return the Node local part name.
    *
    * @return the Node local part name
    */
   public String getLocalPart() {
      return qname.getLocalPart();
   }

   /**
    * Return the Node namespace URI.
    *
    * @return the Node namespace URI
    */
   public String getNamespaceURI() {
      return qname.getNamespaceURI();
   }

   /**
    * Return the Node prefix.
    *
    * @return the Node prefix
    */
   public String getPrefix() {
      return qname.getPrefix();
   }

   /**
    * Return the Node qualified name.
    *
    * @return the Node qualified name
    */
   public QName getQualifiedName() {
      return qname;
   }

   /**
    * Return the ordered list of children of this Node.
    *
    * @return the ordered list of children of this Node
    */
   public List<XMLNode> getChildren() {
      return children;
   }

   /**
    * Return the i-th child of this node.
    *
    * @param i the child index
    * @return the i-th child of this node
    */
   public XMLNode getChild(int i) {
      if (children.size() <= i) {
         return null;
      } else {
         return children.get(i);
      }
   }

   /**
    * Return the first child of the Node.
    *
    * @return the first child of the Node
    */
   public XMLNode getFirstChild() {
      if (children.isEmpty()) {
         return null;
      } else {
         return children.get(0);
      }
   }

   /**
    * Return the last child of the Node.
    *
    * @return the last child of the Node
    */
   public XMLNode getLastChild() {
      if (children.isEmpty()) {
         return null;
      } else {
         return children.get(children.size() - 1);
      }
   }

   /**
    * Return the next sibling of the Node.
    *
    * @return the next sibling of the Node
    */
   public XMLNode getNextSibling() {
      if (nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            for (int i = 0; i < siblings.size(); i++) {
               XMLNode sibling = siblings.get(i);
               if (sibling == this) {
                  if (siblings.size() > i + 1) {
                     return siblings.get(i + 1);
                  } else {
                     return null;
                  }
               }
            }
            return null;
         }
      }
   }

   /**
    * Return the previous sibling of the Node.
    *
    * @return the previous sibling of the Node
    */
   public XMLNode getPreviousSibling() {
      if (nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            for (int i = 0; i < siblings.size(); i++) {
               XMLNode sibling = siblings.get(i);
               if (sibling == this) {
                  if (i > 0) {
                     return siblings.get(i - 1);
                  } else {
                     return null;
                  }
               }
            }
            return null;
         }
      }
   }

   /**
    * Add a child to this Node.
    *
    * @param child the Node child
    */
   public void addChild(XMLNode child) {
      children.add(child);
      child.setParent(this);
   }

   /**
    * Return true if this Node has children.
    *
    * @return true if this Node has children
    */
   public boolean hasChildren() {
      return !children.isEmpty();
   }

   /**
    * Return the number of children of the Node.
    *
    * @return the number of children of the Node
    */
   public int countChildren() {
      return children.size();
   }

   /**
    * Return the number of attributes of the Node.
    *
    * @return the number of attributes of the Node
    */
   public int countAttributes() {
      return attributes.size();
   }

   /**
    * Return the Map of attributes for this node.
    *
    * @return the Map of attributes for this node
    */
   public Map<SortableQName, String> getAttributes() {
      return attributes;
   }

   /**
    * Return true if the node has an attribute for a specified name.
    *
    * @param qname the attribute qualified name
    * @return true if the node has an attribute for the specified name
    */
   public boolean hasAttribute(QName qname) {
      return attributes.containsKey(new SortableQName(qname));
   }

   /**
    * Return true if the node has an attribute for a specified name.
    *
    * @param name the attribute name
    * @return true if the node has an attribute for the specified name
    */
   public boolean hasAttribute(String name) {
      int index = name.indexOf(':');
      if (index == -1) {
         return attributes.containsKey(getSortableQName(name));
      } else {
         String prefix = name.substring(0, index);
         String localPart = name.substring(index + 1);
         return attributes.containsKey(new SortableQName(null, localPart, prefix));
      }
   }

   private QName getQName(String name) {
      int index = name.indexOf(':');
      if (index == -1) {
         return new QName(name);
      } else {
         String prefix = name.substring(0, index);
         String localPart = name.substring(index + 1);
         return new QName(XMLConstants.NULL_NS_URI, localPart, prefix);
      }
   }

   private SortableQName getSortableQName(String name) {
      int index = name.indexOf(':');
      if (index == -1) {
         return new SortableQName(name);
      } else {
         String prefix = name.substring(0, index);
         String localPart = name.substring(index + 1);
         return new SortableQName(XMLConstants.NULL_NS_URI, localPart, prefix);
      }
   }

   /**
    * Return the value of an attribute of a specified name.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public String getAttributeValue(String attrName) {
      return attributes.get(getSortableQName(attrName));
   }

   /**
    * Return the value of an attribute of a specified name.
    *
    * @param qname the attribute qualified name
    * @return the value of the attribute
    */
   public String getAttributeValue(QName qname) {
      return attributes.get(new SortableQName(qname));
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a float.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a float
    */
   public boolean attributeValueIsFloat(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsFloat(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a float.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a float
    */
   public boolean attributeValueIsFloat(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            Float.parseFloat(attrvalue);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a float.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public float getAttributeValueAsFloat(String attrName) {
      return getAttributeValueAsFloat(attrName, 0f);
   }

   /**
    * Return the value of an attribute of a specified name as a float.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public float getAttributeValueAsFloat(String attrName, float defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsFloat(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a float.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public float getAttributeValueAsFloat(QName qname, float defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            float f = Float.parseFloat(attrvalue);
            return f;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a
    * double.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a double
    */
   public boolean attributeValueIsDouble(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsDouble(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a
    * double.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a double
    */
   public boolean attributeValueIsDouble(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            Double.parseDouble(attrvalue);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a double.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public double getAttributeValueAsDouble(String attrName) {
      return getAttributeValueAsDouble(attrName, 0d);
   }

   /**
    * Return the value of an attribute of a specified name as a double.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public double getAttributeValueAsDouble(String attrName, double defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsDouble(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a double.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public double getAttributeValueAsDouble(QName qname, double defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            double l = Double.parseDouble(attrvalue);
            return l;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a long.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a long
    */
   public boolean attributeValueIsLong(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsLong(_qname, true);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a long.
    *
    * @param attrName the attribute name
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return true if the attribute can be parsed as a long
    */
   public boolean attributeValueIsLong(String attrName, boolean strict) {
      QName _qname = getQName(attrName);
      return attributeValueIsLong(_qname, strict);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a long.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a long
    */
   public boolean attributeValueIsLong(QName qname) {
      return attributeValueIsLong(qname, true);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a long.
    *
    * @param qname the attribute qualified name
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return true if the attribute can be parsed as a long
    */
   public boolean attributeValueIsLong(QName qname, boolean strict) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            if (strict) {
               Long.parseLong(attrvalue);
            } else {
               Float.parseFloat(attrvalue);
            }
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a long.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public long getAttributeValueAsLong(String attrName) {
      return getAttributeValueAsLong(attrName, 0L);
   }

   /**
    * Return the value of an attribute of a specified name as a long.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public long getAttributeValueAsLong(String attrName, long defaultValue) {
      return getAttributeValueAsLong(attrName, defaultValue, true);
   }

   /**
    * Return the value of an attribute of a specified name as a long.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the attribute
    */
   public long getAttributeValueAsLong(String attrName, long defaultValue, boolean strict) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsLong(_qname, defaultValue, strict);
   }

   /**
    * Return the value of an attribute of a specified name as a long.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the attribute
    */
   public long getAttributeValueAsLong(QName qname, long defaultValue, boolean strict) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            long l;
            if (strict) {
               l = Long.parseLong(attrvalue);
            } else {
               l = (int) Float.parseFloat(attrvalue);
            }
            return l;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as an int.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as an int
    */
   public boolean attributeValueIsInt(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsInt(_qname, true);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as an int.
    *
    * @param attrName the attribute name
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return true if the attribute can be parsed as an int
    */
   public boolean attributeValueIsInt(String attrName, boolean strict) {
      QName _qname = getQName(attrName);
      return attributeValueIsInt(_qname, strict);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as an int.
    *
    * @param qname the attribute qualified name
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return true if the attribute can be parsed as an int
    */
   public boolean attributeValueIsInt(QName qname, boolean strict) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            if (strict) {
               Integer.parseInt(attrvalue);
            } else {
               Float.parseFloat(attrvalue);
            }
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(String attrName) {
      return getAttributeValueAsInt(attrName, 0);
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(String attrName, int defaultValue) {
      return getAttributeValueAsInt(attrName, defaultValue, true);
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(String attrName, int defaultValue, boolean strict) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsInt(_qname, defaultValue, strict);
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(QName qname, int defaultValue, boolean strict) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            int i;
            if (strict) {
               i = Integer.parseInt(attrvalue);
            } else {
               i = (int) Float.parseFloat(attrvalue);
            }
            return i;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a short.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a short
    */
   public boolean attributeValueIsShort(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsShort(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a short.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a short
    */
   public boolean attributeValueIsShort(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            Short.parseShort(attrvalue);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a short.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public short getAttributeValueAsShort(String attrName) {
      return getAttributeValueAsShort(attrName, (short) 0);
   }

   /**
    * Return the value of an attribute of a specified name as a short.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public short getAttributeValueAsShort(String attrName, short defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsShort(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a short.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public short getAttributeValueAsShort(QName qname, short defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            short s = Short.parseShort(attrvalue);
            return s;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a byte.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a byte
    */
   public boolean attributeValueIsByte(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsByte(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a byte.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a byte
    */
   public boolean attributeValueIsByte(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            Byte.parseByte(attrvalue);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a byte.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public byte getAttributeValueAsByte(String attrName) {
      return getAttributeValueAsByte(attrName, (byte) 0);
   }

   /**
    * Return the value of an attribute of a specified name as a byte.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public byte getAttributeValueAsByte(String attrName, byte defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsByte(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a byte.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public byte getAttributeValueAsByte(QName qname, byte defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            byte s = Byte.parseByte(attrvalue);
            return s;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a char.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a char
    */
   public boolean attributeValueIsChar(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsChar(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a char.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a char
    */
   public boolean attributeValueIsChar(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            int i = Integer.parseInt(attrvalue);
            if (i > Character.MAX_VALUE || i < Character.MIN_VALUE) {
               return false;
            } else {
               return true;
            }
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a char.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public char getAttributeValueAsChar(String attrName) {
      return getAttributeValueAsChar(attrName, (char) 0);
   }

   /**
    * Return the value of an attribute of a specified name as a char.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public char getAttributeValueAsChar(String attrName, char defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsChar(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a char.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public char getAttributeValueAsChar(QName qname, char defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         try {
            int i = Integer.parseInt(attrvalue);
            if (i > Character.MAX_VALUE || i < Character.MIN_VALUE) {
               return defaultValue;
            } else {
               return (char) i;
            }
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a
    * boolean.
    *
    * @param attrName the attribute name
    * @return true if the attribute can be parsed as a char
    */
   public boolean attributeValueIsBoolean(String attrName) {
      QName _qname = getQName(attrName);
      return attributeValueIsBoolean(_qname);
   }

   /**
    * Return true if an attribute of a specified name can be parsed as a
    * boolean.
    *
    * @param qname the attribute qualified name
    * @return true if the attribute can be parsed as a boolean
    */
   public boolean attributeValueIsBoolean(QName qname) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         switch (attrvalue) {
            case "true":
               return true;
            case "false":
               return false;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a char.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public boolean getAttributeValueAsBoolean(String attrName) {
      return getAttributeValueAsBoolean(attrName, false);
   }

   /**
    * Return the value of an attribute of a specified name as a char.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public boolean getAttributeValueAsBoolean(String attrName, boolean defaultValue) {
      QName _qname = getQName(attrName);
      return getAttributeValueAsBoolean(_qname, defaultValue);
   }

   /**
    * Return the value of an attribute of a specified name as a boolean.
    *
    * @param qname the attribute qualified name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public boolean getAttributeValueAsBoolean(QName qname, boolean defaultValue) {
      SortableQName sqname = new SortableQName(qname);
      if (attributes.containsKey(sqname)) {
         String attrvalue = attributes.get(sqname);
         switch (attrvalue) {
            case "true":
               return true;
            case "false":
               return false;
            default:
               return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, String value) {
      if (qname.getPrefix().equals(XMNLS)) {
         boundPrefixes.put(qname.getLocalPart(), value);
      } else {
         bindSchemaLocationToEmptyPrefix(qname.getPrefix(), qname.getLocalPart(), value);
         attributes.put(new SortableQName(qname), value);
      }
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, String value) {
      if (attrName.equals(XMNLS)) {
         if (boundPrefixes == null) {
            boundPrefixes = new HashMap<>();
         }
         boundPrefixes.put(value, "");
      } else {
         SortableQName _qname = new SortableQName(attrName);
         if (_qname.getPrefix().equals("xmnls")) {
            if (boundPrefixes == null) {
               boundPrefixes = new HashMap<>();
            }
            boundPrefixes.put(value, _qname.getLocalPart());
         } else {
            bindSchemaLocationToEmptyPrefix(qname.getPrefix(), qname.getLocalPart(), value);
            attributes.put(_qname, value);
         }
      }
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, float value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Float.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, float value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Float.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, double value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Double.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, double value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Double.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, int value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Integer.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, int value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Integer.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, long value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Long.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, long value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Long.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, short value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Short.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, short value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Short.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, byte value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Byte.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, byte value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Byte.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, char value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, Integer.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, char value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, Integer.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param qname the attribute qualified name
    * @param value the attribute value
    */
   public void addAttribute(QName qname, boolean value) {
      SortableQName _qname = new SortableQName(qname);
      attributes.put(_qname, value ? "true" : "false");
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, boolean value) {
      SortableQName _qname = getSortableQName(attrName);
      attributes.put(_qname, value ? "true" : "false");
   }

   /**
    * Set the CDATA content for the node.
    *
    * @param cData the CDATA content
    */
   public void setCDATA(String cData) {
      this.cData = cData;
   }

   /**
    * Return the CDATA content for the node. If the CDATA content is empty, it
    * will return null.
    *
    * @return the CDATA content
    */
   public String getCDATA() {
      return cData;
   }

   /**
    * Return the escaped CDATA content for the node. If the CDATA content is
    * empty, it will return null.
    *
    * @return the escaped CDATA content
    */
   public String getEscapedCDATA() {
      if (cData != null) {
         return HTMLEscaper.escapeXMLCDATA(cData);
      } else {
         return null;
      }
   }

   /**
    * Return true if the value of the CDATA is a boolean.
    *
    * @return true if the value of the CDATA is a boolean
    */
   public boolean CDATAValueIsBoolean() {
      if (cData != null) {
         switch (cData) {
            case "true":
               return true;
            case "false":
               return false;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the CDATA content for the node. If the CDATA content is empty, it
    * will return an empty string.
    *
    * @return the CDATA content
    */
   public String getCDATAValueAsString() {
      if (cData == null) {
         return "";
      } else {
         return cData;
      }
   }

   /**
    * Return the value of the CDATA as a boolean.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public boolean getCDATAValueAsBoolean(boolean defaultValue) {
      if (cData != null) {
         switch (cData) {
            case "true":
               return true;
            case "false":
               return false;
            default:
               return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a short.
    *
    * @return true if the value of the CDATA is a short
    */
   public boolean CDATAValueIsShort() {
      if (cData != null) {
         try {
            Short.parseShort(cData);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a short.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public short getCDATAValueAsShort(short defaultValue) {
      if (cData != null) {
         try {
            short s = Short.parseShort(cData);
            return s;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a byte.
    *
    * @return true if the value of the CDATA is a byte
    */
   public boolean CDATAValueIsByte() {
      if (cData != null) {
         try {
            Byte.parseByte(cData);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a byte.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public byte getCDATAValueAsByte(byte defaultValue) {
      if (cData != null) {
         try {
            byte b = Byte.parseByte(cData);
            return b;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a char.
    *
    * @return true if the value of the CDATA is a char
    */
   public boolean CDATAValueIsChar() {
      if (cData != null) {
         try {
            int i = Integer.parseInt(cData);
            if (i > Character.MAX_VALUE || i < Character.MIN_VALUE) {
               return false;
            } else {
               return true;
            }
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a char.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public char getCDATAValueAsChar(char defaultValue) {
      if (cData != null) {
         try {
            int i = Integer.parseInt(cData);
            if (i > Character.MAX_VALUE || i < Character.MIN_VALUE) {
               return defaultValue;
            } else {
               return (char) i;
            }
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is an int.
    *
    * @return true if the value of the CDATA is an int
    */
   public boolean CDATAValueIsInt() {
      return CDATAValueIsInt(true);
   }

   /**
    * Return true if the value of the CDATA is an int.
    *
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    *
    * @return true if the value of the CDATA is an int
    */
   public boolean CDATAValueIsInt(boolean strict) {
      if (cData != null) {
         try {

            if (strict) {
               Integer.parseInt(cData);
            } else {
               Float.parseFloat(cData);
            }
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as an int.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public int getCDATAValueAsInt(int defaultValue) {
      return getCDATAValueAsInt(defaultValue, false);
   }

   /**
    * Return the value of the CDATA as an int.
    *
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the CDATA
    */
   public int getCDATAValueAsInt(int defaultValue, boolean strict) {
      if (cData != null) {
         try {
            int i;
            if (strict) {
               i = Integer.parseInt(cData);
            } else {
               i = (int) Float.parseFloat(cData);
            }
            return i;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a long.
    *
    * @return true if the value of the CDATA is a long
    */
   public boolean CDATAValueIsLong() {
      return CDATAValueIsLong(true);
   }

   /**
    * Return true if the value of the CDATA is a long.
    *
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    *
    * @return true if the value of the CDATA is a long
    */
   public boolean CDATAValueIsLong(boolean strict) {
      if (cData != null) {
         try {
            if (strict) {
               Long.parseLong(cData);
            } else {
               Float.parseFloat(cData);
            }
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a long.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public long getCDATAValueAsLong(long defaultValue) {
      return getCDATAValueAsLong(defaultValue, false);
   }

   /**
    * Return the value of the CDATA as a long.
    *
    * @param defaultValue the default value
    * @param strict true if the parsing must be strict (ie decimal values are
    * not allowed)
    * @return the value of the CDATA
    */
   public long getCDATAValueAsLong(long defaultValue, boolean strict) {
      if (cData != null) {
         try {
            long l;
            if (strict) {
               l = Long.parseLong(cData);
            } else {
               l = (int) Float.parseFloat(cData);
            }
            return l;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a float.
    *
    * @return true if the value of the CDATA is a float
    */
   public boolean CDATAValueIsFloat() {
      if (cData != null) {
         try {
            Float.parseFloat(cData);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a float.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public float getCDATAValueAsFloat(float defaultValue) {
      if (cData != null) {
         try {
            float f = Float.parseFloat(cData);
            return f;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if the value of the CDATA is a double.
    *
    * @return true if the value of the CDATA is a double
    */
   public boolean CDATAValueIsDouble() {
      if (cData != null) {
         try {
            Double.parseDouble(cData);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the value of the CDATA as a double.
    *
    * @param defaultValue the default value
    * @return the value of the CDATA
    */
   public double getCDATAValueAsDouble(double defaultValue) {
      if (cData != null) {
         try {
            double d = Double.parseDouble(cData);
            return d;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return true if there is a the CDATA content for the node.
    *
    * @return true if there is a the CDATA content for the nodet
    */
   public boolean hasCDATA() {
      return cData != null;
   }

   /**
    * Search for the children nodes having a specified name. See
    * {@link XMLNodeUtilities#search(XMLNode, String, boolean)}.
    *
    * @param name the name to search for
    * @param deep if the search should continue for children of nodes found in
    * the search
    * @return the list of nodes with the specified name
    */
   public List<XMLNode> search(String name, boolean deep) {
      return XMLNodeUtilities.search(this, name, deep);
   }

   /**
    * Search for the the first child node having a specified qualified name.
    * See
    * {@link XMLNodeUtilities#searchFirst(XMLNode, QName, boolean)}.
    *
    * @param qname the qualified name to search for
    * @param deep if the search should continue for children of nodes found in
    * the search
    * @return the list of nodes with the specified name
    */
   public XMLNode searchFirst(QName qname, boolean deep) {
      return XMLNodeUtilities.searchFirst(this, qname, deep);
   }

   /**
    * Search for the first child node having a specified name. See
    * {@link XMLNodeUtilities#searchFirst(XMLNode, String, boolean)}.
    *
    * @param name the name to search for
    * @param deep if the search should continue for children of nodes found in
    * the search
    * @return the list of nodes with the specified name
    */
   public XMLNode searchFirst(String name, boolean deep) {
      return XMLNodeUtilities.searchFirst(this, name, deep);
   }

   /**
    * Search for the children nodes having a specified qualified name. See
    * {@link XMLNodeUtilities#search(XMLNode, QName, boolean)}.
    *
    * @param qname the qualified name to search for
    * @param deep if the search should continue for children of nodes found in
    * the search
    * @return the list of nodes with the specified name
    */
   public List<XMLNode> search(QName qname, boolean deep) {
      return XMLNodeUtilities.search(this, qname, deep);
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 71 * hash + Objects.hashCode(this.qname);
      hash = 71 * hash + Objects.hashCode(this.children);
      hash = 71 * hash + Objects.hashCode(this.attributes);
      hash = 71 * hash + Objects.hashCode(this.cData);
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
      final XMLNode other = (XMLNode) obj;
      if (!Objects.equals(this.cData, other.cData)) {
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

   /**
    * Return the node as a String.
    *
    * @param indentation the indentation
    * @return the node as a String
    */
   public String toString(int indentation) {
      String str = XMLNodeUtilities.print(this, indentation);
      return str;
   }

   @Override
   public String toString() {
      return toString(2);
   }

   /**
    * Return an iterator on the Nodes under this node.
    *
    * @return the iterator
    */
   @Override
   public Iterator<XMLNode> iterator() {
      XMLNodesIterator walker = new XMLNodesIterator(this);
      return walker;
   }

   /**
    * A prefix bounded to an URI.
    *
    * @since 1.2.40
    */
   public static class BoundPrefix {

      /**
       * The prefix.
       */
      public final String prefix;
      /**
       * True if the prefix is defined for this node.
       */
      public final boolean isDefined;

      private BoundPrefix(String prefix) {
         this.prefix = prefix;
         this.isDefined = true;
      }

      private BoundPrefix(BoundPrefix prefix) {
         this.prefix = prefix.prefix;
         this.isDefined = false;
      }

      public boolean hasPrefix() {
         return !prefix.isEmpty();
      }
   }
}
