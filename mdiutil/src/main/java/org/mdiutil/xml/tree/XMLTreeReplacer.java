/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * This class allows to change the attributes of some nodes in a XML document.
 * <ul>
 * <li>The {@link NodePath} element represent one path in the XML tree</li>
 * <li>The {@link Node} element represent one element in the XML tree. A node can specify one or several attribute change for this element</li>
 * </ul>
 *
 * <h1>Examples</h1>
 * Suppose the following XML file:
 * <pre>
 * &lt;root desc="example"&gt;
 *   &lt;element name="first"&gt;
 *     &lt;element name="second"/&gt;
 *     &lt;element name="third"/&gt;
 *   &lt;/element&gt;
 * &lt;/root&gt;
 * </pre>
 *
 * And the NodePath:
 * <pre>
 * NodePath path = new NodePath();
 * path.addNode("root");
 * path.addNode("element");
 * Node node = path.addNode("element", 1);
 * node.setAttribute("name", "changed");
 * </pre>
 *
 * Then the result of the {@link #replace(File, File, NodePath...)}
 * with the NodePath will be:
 * <pre>
 * &lt;root desc="example"&gt;
 *   &lt;element name="first"&gt;
 *     &lt;element name="second"/&gt;
 *     &lt;element name="changed"/&gt;
 *   &lt;/element&gt;
 * &lt;/root&gt;
 * </pre>
 *
 * @version 1.2.20
 */
public class XMLTreeReplacer {
   private int tab = 3;

   public XMLTreeReplacer() {
   }

   /**
    * Set the tabulation for the XML file for each child node
    *
    * @param tab the tabulation
    */
   public void setTab(int tab) {
      this.tab = tab;
   }

   /**
    * Return the tabulation for the XML file for each child node
    *
    * @return the tabulation
    */
   public int getTab() {
      return tab;
   }

   /**
    * Replace the attributes of some nodes in a XML document.
    *
    * @param root the root of the XML document
    * @param outputFile the ouput file
    * @param paths the list of paths to change
    * @return true if the replacement could be performed
    */
   public boolean replace(XMLNode root, File outputFile, NodePath... paths) throws IOException {
      try {
         URL outputURL = outputFile.toURI().toURL();
         return replace(root, outputURL, paths);
      } catch (MalformedURLException ex) {
         return false;
      }
   }

   /**
    * Replace the attributes of some nodes in a XML document.
    *
    * @param inputFile the input XML document
    * @param outputFile the ouput file
    * @param paths the list of paths to change
    * @return true if the replacement could be performed
    */
   public boolean replace(File inputFile, File outputFile, NodePath... paths) throws IOException {
      try {
         URL inputURL = inputFile.toURI().toURL();
         URL outputURL = outputFile.toURI().toURL();
         return replace(inputURL, outputURL, paths);
      } catch (MalformedURLException ex) {
         return false;
      }
   }

   /**
    * Replace the attributes of some nodes in a XML document.
    *
    * @param inputURL the input XML document
    * @param outputURL the ouput URL
    * @param paths the list of paths to change
    * @return true if the replacement could be performed
    */
   public boolean replace(URL inputURL, URL outputURL, NodePath... paths) throws IOException {
      XMLNode root = XMLNodeUtilities.getRootNode(inputURL);
      return replace(root, outputURL, paths);
   }

   /**
    * Replace the attributes of some nodes in a XML document.
    *
    * @param root the root of the XML document
    * @param outputURL the ouput URL
    * @param paths the list of paths to change
    * @return true if the replacement could be performed
    */
   public boolean replace(XMLNode root, URL outputURL, NodePath... paths) throws IOException {
      if (root != null) {
         boolean replaced = true;
         for (int i = 0; i < paths.length; i++) {
            replaced = replace(root, outputURL, paths[i]) && replaced;
         }
         return replaced;
      } else {
         return false;
      }
   }

   /**
    * Replace the attributes of some nodes in a XML document. The initial root will not be modified, but the result will be a copy of the input root with the
    * changed attributes.
    *
    * @param root the root of the XML document
    * @param paths the list of paths to change
    * @return true if the replacement could be performed
    */
   public XMLNode replace(XMLNode root, NodePath... paths) throws IOException {
      if (root != null) {
         XMLNode output = root.copy();
         boolean replaced = true;
         for (int i = 0; i < paths.length; i++) {
            replaced = replaceImpl(output, paths[i]) && replaced;
         }
         if (replaced) {
            return output;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean replaceImpl(XMLNode root, NodePath path) throws IOException {
      List<Node> nodes = path.getPath();
      XMLNode elt = root;
      boolean replaced = true;
      for (int treeIndex = 0; treeIndex < nodes.size(); treeIndex++) {
         replaced = replaceElement(elt, nodes, treeIndex);
         if (replaced && treeIndex < nodes.size() - 1) {
            elt = getChild(elt, nodes, treeIndex);
            if (elt == null) {
               replaced = false;
               break;
            }
         }
      }
      return replaced;
   }

   private boolean replace(XMLNode root, URL outputURL, NodePath path) throws IOException {
      List<Node> nodes = path.getPath();
      XMLNode elt = root;
      boolean replaced = true;
      for (int treeIndex = 0; treeIndex < nodes.size(); treeIndex++) {
         replaced = replaceElement(elt, nodes, treeIndex);
         if (replaced && treeIndex < nodes.size() - 1) {
            elt = getChild(elt, nodes, treeIndex);
            if (elt == null) {
               replaced = false;
               break;
            }
         }
      }
      if (replaced) {
         XMLNodeUtilities.print(root, tab, outputURL);
      }
      return replaced;
   }

   private XMLNode getChild(XMLNode node, List<Node> nodes, int treeIndex) {
      Node rep = nodes.get(treeIndex + 1);
      if (node.hasChildren()) {
         XMLNode theChild = null;
         List<XMLNode> children = node.getChildren();
         int childIndex = rep.getOccurrence();
         QName qName = rep.getQName();
         String ifQName = rep.getIfQName();
         String ifValue = rep.getIfValue();
         for (int i = 0; i < children.size(); i++) {
            XMLNode curChild = children.get(i);
            if (SortableQName.compareQNames(curChild.getQualifiedName(), qName)) {
               if (i == childIndex) {
                  theChild = curChild;
                  break;
               } else if (childIndex == -1 && ifQName == null) {
                  theChild = curChild;
                  break;
               } else if (childIndex == -1 && ifQName != null) {
                  String attrValue = curChild.getAttributeValue(ifQName);
                  if (attrValue != null && attrValue.equals(ifValue)) {
                     theChild = curChild;
                     break;
                  }
               } else if (i > childIndex && childIndex != -1) {
                  theChild = null;
               }
            }
         }
         if (theChild != null) {
            return theChild;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean replaceElement(XMLNode node, List<Node> changes, int treeIndex) {
      Node rep = changes.get(treeIndex);
      boolean hasIfValue = false;
      String ifQName = rep.getIfQName();
      if (ifQName != null) {
         String ifValue = rep.getIfValue();
         String valueS = node.getAttributeValue(ifQName);
         hasIfValue = valueS.equals(ifValue);
      }
      if (SortableQName.compareQNames(rep.getQName(), node.getQualifiedName()) && (ifQName == null || hasIfValue)) {
         if (!rep.getAttributes().isEmpty()) {
            Map<String, String> attributes = rep.getAttributes();
            Iterator<String> it = attributes.keySet().iterator();
            while (it.hasNext()) {
               String attrName = it.next();
               if (node.hasAttribute(attrName)) {
                  node.addAttribute(attrName, attributes.get(attrName));
               }
            }
         }
         return true;
      } else {
         return false;
      }
   }

   /**
    * A path in an XML document. The path represent the nodes from the root of the XML document to the last Node.
    *
    * @version 1.2.20
    */
   public static class NodePath {
      private final List<Node> path;

      public NodePath() {
         path = new ArrayList<>();
      }

      /**
       * Constructor.
       *
       * @param nodes the nodes
       */
      public NodePath(Node... nodes) {
         path = new ArrayList<>();
         for (int i = 0; i < nodes.length; i++) {
            path.add(nodes[i]);
         }
      }

      /**
       * Constructor.
       *
       * @param path the list of nodes which constitute the path
       */
      public NodePath(List<Node> path) {
         this.path = path;
      }

      /**
       * Add a Node to the path.
       *
       * @param node the Node
       */
      public void addNode(Node node) {
         path.add(node);
      }

      /**
       * Add a Node to the path.
       *
       * @param qname the Node name
       */
      public void addNode(String qname) {
         path.add(new Node(qname));
      }

      /**
       * Add a Node to the path.
       *
       * @param qname the Node name
       * @param occ the occurrence of the Node
       * @return the Node
       */
      public Node addNode(String qname, int occ) {
         Node node = new Node(qname, occ);
         path.add(node);
         return node;
      }

      /**
       * Add a Node to the path.
       *
       * @param qname the Node name
       * @param ifQName the attribute qualified name to check for the change
       * @param ifValue the attribute value to use check the change
       * @return the Node
       */
      public Node addNode(String qname, String ifQName, String ifValue) {
         Node node = new Node(qname, ifQName, ifValue);
         path.add(node);
         return node;
      }

      /**
       * Return the list of nodes which constitute the path.
       *
       * @return the list of nodes which constitute the path
       */
      public List<Node> getPath() {
         return path;
      }
   }

   /**
    * A node in an XML document. The Node has at least an attribute change if {@link #hasChange()} is true.
    *
    * The Node has at least one change if:
    * <ul>
    * <li>The {@link #hasChange()} method return true</li>
    * <li>The {@link #getOccurrence()} value is greater or equal than 0, and the index of the node in its parent is equal to {@link #getOccurrence()}</li>
    * <li>Or the {@link #getOccurrence()} value is -1, {@link #getIfQName()} is not null and the value of the corresponding attribute in the same Node
    * has the {@link #getIfValue()} value</li>
    * </ul>
    *
    * @version 1.2.20
    */
   public static class Node {
      private final Map<String, String> attributes = new HashMap<>();
      private int occ = 0;
      private final QName qname;
      private String ifQName = null;
      private String ifValue = null;

      /**
       * Constructor.
       *
       * @param qname the Node name
       */
      public Node(QName qname) {
         this.qname = qname;
      }

      /**
       * Constructor.
       *
       * @param qname the Node name
       */
      public Node(String qname) {
         this.qname = new QName(qname);
      }

      /**
       * Constructor.
       *
       * @param qname the Node name
       * @param ifQName the attribute qualified name to check for the change
       * @param ifValue the attribute value to use check the change
       */
      public Node(QName qname, String ifQName, String ifValue) {
         this.ifQName = ifQName;
         this.ifValue = ifValue;
         this.occ = -1;
         this.qname = qname;
      }

      /**
       * Constructor.
       *
       * @param qname the Node name
       * @param ifQName the attribute qualified name to check for the change
       * @param ifValue the attribute value to use check the change
       */
      public Node(String qname, String ifQName, String ifValue) {
         this.ifQName = ifQName;
         this.ifValue = ifValue;
         this.occ = -1;
         this.qname = new QName(qname);
      }

      /**
       * Constructor.
       *
       * @param qname the Node name
       * @param occ the occurrence of the Node
       */
      public Node(QName qname, int occ) {
         this.occ = occ;
         this.qname = qname;
      }

      /**
       * Constructor.
       *
       * @param qname the Node name
       * @param occ the occurrence of the Node
       */
      public Node(String qname, int occ) {
         this.occ = occ;
         this.qname = new QName(qname);
      }

      /**
       * Set an attribute change
       *
       * @param name the attribute name
       * @param value the attribute value
       */
      public void setAttribute(String name, String value) {
         attributes.put(name, value);
      }

      /**
       * Return the attributes changes.
       *
       * @return the attributes changes
       */
      public Map<String, String> getAttributes() {
         return attributes;
      }

      /**
       * Return the Node occurrence.
       *
       * @return the Node occurrence
       */
      public int getOccurrence() {
         return occ;
      }

      /**
       * Return true if the Node has a change.
       *
       * @return true if the Node has a change
       */
      public boolean hasChange() {
         return attributes.isEmpty();
      }

      /**
       * Return the attribute qualified name to check for the change.
       *
       * @return the attribute qualified name to check for the change
       */
      public String getIfQName() {
         return ifQName;
      }

      /**
       * Return the attribute value to use check the change.
       *
       * @return the attribute value to use check the change
       */
      public String getIfValue() {
         return ifValue;
      }

      /**
       * Return the Node qualified name.
       *
       * @return the Node qualified name
       */
      public QName getQName() {
         return qname;
      }

      /**
       * Return the Node name.
       *
       * @return the Node name
       */
      public String getName() {
         return qname.getLocalPart();
      }
   }

}
