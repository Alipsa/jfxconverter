/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2018 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.tree;

import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Allows to perform a deep copy of any DefaultMutableTreeNode.
 *
 * This deep copy consist of cloning each node in the tree under the node that is copied without cloning their associated Objects.
 *
 * @version 0.9.42
 */
public class NodeCloner {

   /**
    * Clone a DefaultMutableTreeNode.
    *
    * @param node the node
    * @return the clone node
    */
   public static DefaultMutableTreeNode CloneNode(DefaultMutableTreeNode node) {
      Map bridge = new HashMap<>(10);
      DefaultMutableTreeNode copiedNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).clone();
      bridge.put(node, copiedNode);

      TreeIterator it = new TreeIterator(node);
      while (it.hasNext()) {
         DefaultMutableTreeNode inNode = (DefaultMutableTreeNode) it.next();
         DefaultMutableTreeNode cpNode = (DefaultMutableTreeNode) inNode;
         bridge.put(inNode, cpNode);
         if (inNode.getParent() != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) bridge.get((DefaultMutableTreeNode) (inNode.getParent()));
            parent.add(cpNode);
         }
      }

      return copiedNode;
   }
}
