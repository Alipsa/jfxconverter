/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * This class implements the interface {@link TreeWalker}.
 *
 * @version 1.2.10
 */
public class XMLTreeWalker implements TreeWalker {
   /**
    * The mode for traversing under the root node.
    */
   public static final int MODE_ROOT = 0;

   /**
    * The mode for traversing from the root node to another node.
    */
   public static final int MODE_SLICE = 1;
   private int mode = MODE_ROOT;
   private final Node rootNode;
   private Node lastNode = null;
   private Node currentNode;
   private NodeFilter filter = null;
   private boolean expand;
   private int level = 0;
   private int nodeType = Node.ELEMENT_NODE;

   /**
    * Create a new XMLTreeWalker, in the default {@link #MODE_ROOT} mode.
    *
    * @param rootNode the root Node of this walker
    */
   public XMLTreeWalker(Node rootNode) {
      this.rootNode = rootNode;
      this.currentNode = rootNode;
   }

   /**
    * Create a new XMLTreeWalker, in the {@link #MODE_SLICE} mode.
    * The DOM hierarchy from the rootNode to the Node previous to the lastNode will be traversed.
    * If the lastNode is null, the DOM tree will be traversed up to the last DOM Node.
    *
    * @param rootNode the root Node of this walker
    * @param lastNode the last Node of this walker
    */
   public XMLTreeWalker(Node rootNode, Node lastNode) {
      this.rootNode = rootNode;
      this.currentNode = rootNode;
      this.lastNode = lastNode;
      this.mode = MODE_SLICE;
   }

   /**
    * Create a new XMLTreeWalker.
    *
    * @param doc the Document whose DocumentElement will be used as root Node
    */
   public XMLTreeWalker(Document doc) {
      this.rootNode = doc.getDocumentElement();
      this.currentNode = rootNode;
   }

   /**
    * Return the mode used to traverse the DOM tree.
    * <ul>
    * <li>{@link #MODE_ROOT} : only the Nodes under the {@link #getRoot()} Node are traversed</li>
    * <li>{@link #MODE_SLICE} : the hierarchy from the {@link #getRoot()} Node to the Node previous to
    * {@link #getLastNode()} is traversed</li>
    * </ul>
    *
    * @return the mode
    */
   public int getTraversalMode() {
      return mode;
   }

   /**
    * Not used for the moment.
    */
   public void setNodeType(int type) {
      nodeType = type;
   }

   /**
    * Not used for the moment.
    */
   public void SetFilter(NodeFilter filter) {
      this.filter = filter;
   }

   /**
    * Not used for the moment.
    */
   public void setExpandEntityReferences(boolean expand) {
      this.expand = expand;
   }

   @Override
   public void setCurrentNode(Node node) {
      currentNode = node;
      level = 0;
   }

   /**
    * Return the filter used to screen nodes.
    *
    * @return the filter
    */
   @Override
   public NodeFilter getFilter() {
      return filter;
   }

   /**
    * Return the level in the XML subtree, the 0 value designing the root Node. The level will always be &gt= 0 in
    * the {@link #MODE_ROOT} mode, but it can be &lt 0 in the {@link #MODE_SLICE} mode, as the last Node
    * can be higher in the DOM hierarchy than the root node.
    *
    * @return the level
    */
   public int getLevel() {
      return level;
   }

   /**
    * Moves the TreeWalker to the first visible child of the current node, and returns the new node.
    *
    * @return the first child
    */
   @Override
   public Node firstChild() {
      Node result = currentNode.getFirstChild();
      if (result != null) {
         level++;
         currentNode = result;
      }
      return result;
   }

   /**
    * Return the node at which the TreeWalker is currently positioned.
    *
    * @return the node
    */
   @Override
   public Node getCurrentNode() {
      return currentNode;
   }

   /**
    * Return the value of this flag determines whether the children of entity reference nodes are visible
    * to the TreeWalker.
    *
    * @return true if the children of entity reference nodes are visible to the TreeWalker
    */
   @Override
   public boolean getExpandEntityReferences() {
      return expand;
   }

   /**
    * The root node of the TreeWalker, as specified when it was created.
    * It is guaranteed that the TreeWalker will never walk outside the subtree defined by this rootNode.
    *
    * @return the root node
    */
   @Override
   public Node getRoot() {
      return rootNode;
   }

   /**
    * Return the last Node of the DOM tree to traverse (more specifically, the DOM tree, the walker
    * will stop the iteration just before this Node. If the mode is {@link #MODE_ROOT}, this is guaranteed to be null.
    *
    * @return the last Node of the DOM tree to traverse
    */
   public Node getLastNode() {
      return lastNode;
   }

   /**
    * This attribute determines which node types are presented via the TreeWalker.
    *
    * @return the node types
    */
   @Override
   public int getWhatToShow() {
      return nodeType;
   }

   /**
    * Moves the TreeWalker to the last visible child of the current node, and returns the new node.
    */
   @Override
   public Node lastChild() {
      Node result = currentNode.getLastChild();
      if (result != null) {
         level++;
         currentNode = result;
      }
      return result;
   }

   /**
    * Moves the TreeWalker to the next visible node in document order relative
    * to the current node, and returns the new node.
    * <ul>
    * <li>If the mode {@link #MODE_ROOT}, we traverse the DOM tree under the {@link #getRoot()} node</li>
    * <li>If the mode {@link #MODE_SLICE}, we traverse the DOM tree beginning from the
    * {@link #getRoot()} node,
    * </ul>
    */
   @Override
   public Node nextNode() {
      Node result = getNextNode(currentNode);
      currentNode = result;
      return result;
   }

   /**
    * Moves the TreeWalker to the next sibling of the current node, and returns the new node.
    */
   @Override
   public Node nextSibling() {
      // if this is the rootNode and we are in MODE_ROOT, we don't allow to go further
      if ((currentNode == rootNode) && (mode == MODE_ROOT)) {
         return null;
      }

      Node result = currentNode.getNextSibling();
      if (result != null) {
         // in MODE_ROOT this is a valid Node to return
         if (mode == MODE_ROOT) {
            currentNode = result;
         } // in MODE_SLICE we must end the traversal if this is the last Node (or null !!)
         else if (result != lastNode) {
            currentNode = result;
         }
      }
      return result;
   }

   /**
    * Moves the TreeWalker to the previous sibling of the current node, and returns the new node.
    */
   @Override
   public Node previousSibling() {
      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      Node result = currentNode.getPreviousSibling();
      if (result != null) {
         currentNode = result;
      }
      return result;
   }

   /**
    * Moves to and returns the closest visible ancestor node of the current node.
    */
   @Override
   public Node parentNode() {
      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      Node result = currentNode.getParentNode();
      if (result != null) {
         currentNode = result;
         level--;
      }
      return result;
   }

   /**
    * Moves the TreeWalker to the previous visible node in document order relative
    * to the current node, and returns the new node.
    */
   @Override
   public Node previousNode() {
      Node result;
      if (currentNode == null) {
         return null;
      }

      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      Node n = currentNode.getPreviousSibling();

      // Go to the parent of a first child
      if (n == null) {
         result = currentNode.getParentNode();
         level--;
         return result;
      }

      // Go to the last child of child...
      Node t;
      while ((t = n.getLastChild()) != null) {
         level++;
         n = t;
      }
      result = n;
      return result;
   }

   private Node getNextNode(Node node) {
      if (node == null) {
         return null;
      }

      // Go to the first child
      Node n = node.getFirstChild();
      if (n != null) {
         level++;
         return n;
      }

      // if this is the rootNode and we are in MODE_ROOT, we don't allow to go further
      if ((node == rootNode) && (mode == MODE_ROOT)) {
         return null;
      }

      // Go to the next sibling
      n = node.getNextSibling();
      if (n != null) {
         // in MODE_ROOT this is a valid Node to return
         if (mode == MODE_ROOT) {
            return n;
         } // in MODE_SLICE we must end the traversal if this is the last Node (or null !!)
         else if (n == lastNode) {
            return null;
         } else {
            return n;
         }
      }

      // Go to the first sibling of one of the ancestors
      n = node;
      while (((n = n.getParentNode()) != null) && (n != rootNode)) {
         level--;
         Node t = n.getNextSibling();
         if (t != null) {
            // in MODE_ROOT this is a valid Node to return
            if (mode == MODE_ROOT) {
               return t;
            } // in MODE_SLICE we must end the traversal if this is the last Node (or null !!)
            else if (t == lastNode) {
               return null;
            } else {
               return t;
            }
         }
      }
      return null;
   }
}
