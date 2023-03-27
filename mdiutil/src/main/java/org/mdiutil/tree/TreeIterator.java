/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2018, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.tree;

import java.util.Iterator;
import java.util.Stack;
import javax.swing.tree.TreeNode;

/**
 * This class iterates through a tree, beginning with a first TreeNode ancestor.
 *
 * @version 1.2
 */
public class TreeIterator implements Iterator<TreeNode> {
   public static final int MODE_UNDER_ROOT = 0;
   public static final int MODE_AFTER_ROOT = 1;
   protected Stack<TreeNode> stack = new Stack<>();
   protected TreeNode curnode;
   protected TreeNode rootnode;
   private int level = 0;
   private int mode = MODE_UNDER_ROOT;

   /**
    * Constructor.
    *
    * @param rootnode the node where the iteration begins
    * @param mode the mode
    */
   public TreeIterator(TreeNode rootnode, int mode) {
      this.mode = mode;
      this.rootnode = rootnode;
      curnode = rootnode;

      // the first element of the stack is the root node
      stack.push(rootnode);
   }

   /**
    * Constructor.
    *
    * @param rootnode the node where the iteration begins
    */
   public TreeIterator(TreeNode rootnode) {
      this(rootnode, TreeIterator.MODE_UNDER_ROOT);
   }

   /**
    * Return true if there is a node after the current one. It is considered that there is a next node after the
    * current one if:
    * <ul>
    * <li>if there is at least one child, then there is a next node</li>
    * <li>else if the mode is {@link #MODE_AFTER_ROOT} :</li>
    * <ul>
    * <li>if there is no parent, there is no next node</li>
    * <li>else there is at least one next node</li>
    * </ul>
    * <li>else if the mode is {@link #MODE_UNDER_ROOT} :</li>
    * <ul>
    * <li>if there is no parent, there is no next node</li>
    * <li>else if the parent is the same as the parent of root node there is no next node</li>
    * <li>else there is at least one next node</li>
    * </ul>
    * </ul>
    *
    * @return true if there is a node after the current one
    */
   @Override
   public boolean hasNext() {
      boolean r = false;

      //System.out.println(curnode +" has next ?");
      // if there is at least one child, then there is a next node
      if (curnode.getChildCount() != 0) {
         r = true;
      } // else the next node can be the next sibling node
      else {
         TreeNode node = curnode;
         int i = stack.size() - 1;
         // iterate on the stack until we find a sibling at the n-th level
         while (!r && (i >= 0)) {
            /* get the last element of the stack, equivalent to peek, except
             * that here we don't really pop elements on the stack so peek is not relevant
             */
            node = stack.elementAt(i);

            /* MODE_AFTER_ROOT && MODE_UNDER_ROOT : if there is no children and no parent, there is no next node
             * MODE_UNDER_ROOT : if the parent is the same as the parent of root node there is no next node
             */
            if ((node == null)
               || ((node == rootnode) && (mode == TreeIterator.MODE_UNDER_ROOT))) {
               r = false;
            } else if (hasSibling(node)) {
               r = true;
               break;
            }
            i -= 1;
         }
      }

      return r;
   }

   /**
    * This method only performs a call to {@link #nextNode()};
    */
   @Override
   public TreeNode next() {
      return nextNode();
   }

   /**
    * Return the current level of this iterator. The level 0 is the initial node.
    *
    * @return the current level of this iterator
    */
   public int getLevel() {
      return level;
   }

   /**
    * This method is not supported by this iterator.
    */
   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   /**
    * Retrieve the next node in the tree. If there is no next node, returns null.
    *
    * @return the next Node
    */
   public TreeNode nextNode() {
      // if there is at least one child, then the next node is the first child
      if (curnode.getChildCount() != 0) {
         curnode = firstChild(curnode);
         level++;
         // else we must iterate through the stack
      } else {
         // iterate on the stack until we find a sibling at the nth level
         while (!stack.empty()) {
            /* get the last element of the stack, equivalent to peek, except
             * that here we don't really pop elements on the stack so peek is not relevant
             */
            TreeNode parent = stack.pop().getParent();
            level -= 1;

            /* MODE_AFTER_ROOT && MODE_UNDER_ROOT : if there is no children and no parent, there is no next node
             * MODE_UNDER_ROOT : if the parent is the same as the parent of root node there is no next node
             */
            if ((parent == null)
               || ((parent == rootnode.getParent()) && (mode == TreeIterator.MODE_UNDER_ROOT))) {
               curnode = null;
               break;
            } else if (hasSibling(curnode)) {
               curnode = nextSibling(curnode);
               level++;
               break;
            } else {
               curnode = parent;
            }
         }
      }
      if (curnode != null) {
         stack.push(curnode);
      }

      return curnode;
   }

   /**
    * Return the first child node of a TreeNode.
    *
    * @param node the Node
    * @return the first child node
    */
   public static final TreeNode firstChild(TreeNode node) {
      return node.getChildAt(0);
   }

   /**
    * Return the next sibling node for a TreeNode.
    *
    * @param node the Node
    * @return the next sibling, or null if there is no next sibling
    */
   public static final TreeNode nextSibling(TreeNode node) {
      TreeNode parent = node.getParent();

      // if there is no parent, there is no sibling node
      if (parent == null) {
         return null;
      } else if (parent.getChildCount() > parent.getIndex(node) + 1) {
         // else the node must not be the last of its parent, else there is no sibling
         return parent.getChildAt(parent.getIndex(node) + 1);
      } else {
         return null;
      }
   }

   /**
    * Return true if a Node has at least one sibling.
    *
    * @param node the Node
    * @return true if the Node has at least one sibling
    */
   public static final boolean hasSibling(TreeNode node) {
      TreeNode parent = node.getParent();

      // if there is no parent, there is no sibling node
      if (parent == null) {
         return false;
      } else if (parent.getChildCount() > parent.getIndex(node) + 1) {
         // else the node must not be the last of its parent, else there is no sibling
         return true;
      } else {
         return false;
      }
   }
}
