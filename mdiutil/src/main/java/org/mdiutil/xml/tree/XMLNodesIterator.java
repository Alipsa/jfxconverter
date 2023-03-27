/*------------------------------------------------------------------------------
 * Copyright (C) 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A tree walker for xml nodes.
 *
 * @version 1.2.44
 */
public class XMLNodesIterator implements Iterator<XMLNode> {
   private final XMLNode rootNode;
   private XMLNode currentNode;
   private int level = 0;
   private boolean started = false;
   private boolean manualFilters = false;
   private Map<String, NodeFilter> parentFilters = null;
   private final Map<String, Boolean> inParentFilters = new HashMap<>();
   private String currentFilter = null;

   /**
    * Create a new XMLNodesIterator.
    *
    * @param rootNode the root Node of this iterator
    */
   public XMLNodesIterator(XMLNode rootNode) {
      this.rootNode = rootNode;
      this.currentNode = rootNode;
   }

   /**
    * Set the filters to check.
    *
    * @param filters the filters
    */
   public final void setParentFilters(NodeFilter... filters) {
      if (filters == null || filters.length == 0) {
         setParentFilters((Map<String, NodeFilter>) null);
      } else {
         Map<String, NodeFilter> _parentFilters = new HashMap<>();
         for (int i = 0; i < filters.length; i++) {
            _parentFilters.put(filters[i].getFilterName(), filters[i]);
         }
         setParentFilters(_parentFilters);
      }
   }

   /**
    * Set the parent filters to check.
    *
    * @param parentFilters the filters
    */
   public final void setParentFilters(Map<String, NodeFilter> parentFilters) {
      if (parentFilters == null || parentFilters.isEmpty()) {
         this.manualFilters = false;
      } else {
         this.manualFilters = true;
         this.parentFilters = parentFilters;
         Iterator<String> it = parentFilters.keySet().iterator();
         while (it.hasNext()) {
            String key = it.next();
            inParentFilters.put(key, Boolean.FALSE);
         }
      }
      addToParentFilters(rootNode);
   }

   /**
    * Return true if there is a filter name for the current node.
    *
    * @return true if there is a filter name for the current node
    */
   public boolean hasCurrentFilterName() {
      return currentFilter != null;
   }

   /**
    * Return the filter name for the current node.
    *
    * @return the filter name for the current node
    */
   public String getCurrentFilterName() {
      return currentFilter;
   }

   /**
    * Return the states of the parent tags.
    *
    * @return the states of the parent tags
    */
   public Map<String, Boolean> getParentFilterStates() {
      return inParentFilters;
   }

   private void clearParentFilters() {
      currentFilter = null;
      if (manualFilters) {
         inParentFilters.clear();
         if (!parentFilters.isEmpty()) {
            Iterator<String> it = parentFilters.keySet().iterator();
            while (it.hasNext()) {
               inParentFilters.put(it.next(), Boolean.FALSE);
            }
         }
      }
   }

   /**
    * Add a node to the parent tags.
    *
    * @param node the node
    */
   public void addToParentFilters(XMLNode node) {
      currentFilter = null;
      if (manualFilters) {
         Iterator<Entry<String, NodeFilter>> it = parentFilters.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, NodeFilter> entry = it.next();
            NodeFilter filter = entry.getValue();
            if (filter.isCompatibleWithNode(node)) {
               currentFilter = entry.getKey();
               inParentFilters.put(entry.getKey(), Boolean.TRUE);
               break;
            }
         }
      }
   }

   private void removeFromParentFilters(XMLNode node) {
      if (manualFilters) {
         Iterator<Entry<String, Boolean>> it = inParentFilters.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, Boolean> entry = it.next();
            if (entry.getValue()) {
               String filterName = entry.getKey();
               NodeFilter theFilter = parentFilters.get(filterName);
               if (theFilter.isCompatibleWithNode(node)) {
                  inParentFilters.put(filterName, Boolean.FALSE);
               }
            }
         }
      }
   }

   /**
    * Return true if the current node is inside the specified parent filter.
    *
    * @param filterName the filter name
    * @return true if the current node is inside the specified parent tag
    */
   public boolean inParentFilter(String filterName) {
      if (manualFilters && inParentFilters.containsKey(filterName)) {
         return inParentFilters.get(filterName);
      } else {
         return false;
      }
   }

   /**
    * Set the current Node.
    *
    * @param node the node
    */
   public void setCurrentNode(XMLNode node) {
      currentNode = node;
      clearParentFilters();
      addToParentFilters(node);
      level = 0;
      if (!(node instanceof XMLRoot)) {
         level++;
         while (true) {
            node = node.getParent();
            if (node instanceof XMLRoot) {
               break;
            } else {
               level++;
            }
         }
      }
   }

   /**
    * Return the level in the XML subtree, the 0 value designing the root Node.
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
   public XMLNode firstChild() {
      XMLNode result = currentNode.getFirstChild();
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
   public XMLNode getCurrentNode() {
      return currentNode;
   }

   /**
    * The root node of the TreeWalker, as specified when it was created.
    *
    * @return the root node
    */
   public XMLNode getRoot() {
      return rootNode;
   }

   /**
    * Moves the TreeWalker to the last visible child of the current node, and returns the new node.
    *
    * @return the node
    */
   public XMLNode lastChild() {
      XMLNode result = currentNode.getLastChild();
      if (result != null) {
         level++;
         currentNode = result;
      }
      return result;
   }

   /**
    * Moves the iterator to the next node in document order relative to the current node, and returns this node.
    *
    * @return the next Node
    */
   @Override
   public XMLNode next() {
      if (!started) {
         started = true;
         return currentNode;
      } else {
         return nextNode();
      }
   }

   /**
    * Moves the TreeWalker to the next visible node in document order relative to the current node, and returns the new
    * node.
    *
    * @return the node
    */
   public XMLNode nextNode() {
      started = true;
      XMLNode result = getNextNode(currentNode);
      currentNode = result;
      return result;
   }

   /**
    * Return true if there is a next node.
    *
    * @return true if there is a next node
    */
   @Override
   public boolean hasNext() {
      if (currentNode == null) {
         return false;
      }

      // Go to the first child
      XMLNode n = currentNode.getFirstChild();
      if (n != null) {
         return true;
      }

      // if this is the rootNode and we are in MODE_ROOT, we don't allow to go further
      if (currentNode == rootNode) {
         return false;
      }

      // Go to the next sibling
      n = currentNode.getNextSibling();
      if (n != null) {
         return true;
      }

      // Go to the first sibling of one of the ancestors
      n = currentNode;
      while (((n = n.getParent()) != null) && (n != rootNode)) {
         XMLNode t = n.getNextSibling();
         if (t != null) {
            return true;
         }
      }
      return false;
   }

   /**
    * Moves the TreeWalker to the next sibling of the current node, and returns the new node.
    *
    * @return the node
    */
   public XMLNode nextSibling() {
      started = true;
      // if this is the rootNode and we are in MODE_ROOT, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      XMLNode result = currentNode.getNextSibling();
      if (result != null) {
         removeFromParentFilters(currentNode);
         addToParentFilters(result);
         currentNode = result;
      }
      return result;
   }

   /**
    * Moves the TreeWalker to the previous sibling of the current node, and returns the new node.
    *
    * @return the node
    */
   public XMLNode previousSibling() {
      started = true;
      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      XMLNode result = currentNode.getPreviousSibling();
      if (result != null) {
         removeFromParentFilters(currentNode);
         addToParentFilters(result);
         currentNode = result;
      }
      return result;
   }

   /**
    * Moves to and returns the closest visible ancestor node of the current node.
    *
    * @return the node
    */
   public XMLNode parentNode() {
      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      XMLNode result = currentNode.getParent();
      if (result != null) {
         removeFromParentFilters(currentNode);
         addToParentFilters(result);
         currentNode = result;
         level--;
      }
      return result;
   }

   /**
    * Moves the TreeWalker to the previous visible node in document order relative to the current node, and returns the
    * new node.
    *
    * @return the node
    */
   public XMLNode previousNode() {
      started = true;
      XMLNode result;
      if (currentNode == null) {
         return null;
      }

      // if this is the rootNode, we don't allow to go further
      if (currentNode == rootNode) {
         return null;
      }

      XMLNode n = currentNode.getPreviousSibling();

      // Go to the parent of a first child
      if (n == null) {
         result = currentNode.getParent();
         level--;
         return result;
      }

      // Go to the last child of child...
      XMLNode t;
      while ((t = n.getLastChild()) != null) {
         level++;
         n = t;
      }
      result = n;
      return result;
   }

   private XMLNode getNextNode(XMLNode node) {
      if (node == null) {
         return null;
      }

      // Go to the first child
      XMLNode n = node.getFirstChild();
      if (n != null) {
         addToParentFilters(n);
         level++;
         return n;
      }

      // if this is the rootNode and we are in MODE_ROOT, we don't allow to go further
      if (node == rootNode) {
         return null;
      }

      // Go to the next sibling
      n = node.getNextSibling();
      if (n != null) {
         removeFromParentFilters(node);
         addToParentFilters(n);
         return n;
      }

      // Go to the first sibling of one of the ancestors
      n = node;
      while (((n = n.getParent()) != null) && (n != rootNode)) {
         removeFromParentFilters(n);
         level--;
         XMLNode t = n.getNextSibling();
         if (t != null) {
            addToParentFilters(t);
            return t;
         }
      }
      return null;
   }
}
