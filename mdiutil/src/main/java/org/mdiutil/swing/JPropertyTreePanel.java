/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2014, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.mdiutil.tree.TreeIterator;

/**
 * User-interface component which presents settings in a tree.
 * <p>
 * There are three ways to add property nodes to the tree :</p>
 * <ul>
 * <li>directly adding nodes containing properties : this is done by using either the
 * {@link #addPropertyListNode(DefaultMutableTreeNode, String, Icon, PropertyEditor)} or
 * {@link #addPropertyListNode(DefaultMutableTreeNode, String, PropertyEditor)} methods.
 * <ul>
 * <li>The <i>DefaultMutableTreeNode</i> is the parent node (which can be the
 * rootNode of the tree),</li>
 * <li>The <i>Icon</i> is the icon associated with the node,</li>
 * <li>The <i>String</i> is the name which will be presented with the node on the tree,</li>
 * <li>The {@link PropertyEditor} is the editor associated with the node</li>
 * </ul></li>
 * <li>adding empty nodes : this is done by using either the
 * {@link #addEmptyNode(DefaultMutableTreeNode, String, Icon)} or
 * {@link #addEmptyNode(DefaultMutableTreeNode, String)} methods.
 * <ul>
 * <li>The <i>DefaultMutableTreeNode</i> is the parent node,</li>
 * <li>The <i>Icon</i> is the icon associated with the node,</li>
 * <li>The <i>String</i> is the name which will be presented with the node on the tree</li>
 * </ul></li>
 * <li>adding a whole node structure : this is done by using either the
 * {@link #addNode(DefaultMutableTreeNode, DefaultMutableTreeNode, Icon)} or
 * {@link #addNode(DefaultMutableTreeNode, DefaultMutableTreeNode)} methods.
 * <ul>
 * <li>The first <i>DefaultMutableTreeNode</i> is the parent node,</li>
 * <li>The first <i>DefaultMutableTreeNode</i> is the child node to add. It can
 * contain other nodes, and the <i>UserObjects</i> associated with the nodes are the
 * {@link PropertyEditor}s associated with the nodes,</li>
 * <li>The <i>Icon</i> is the icon associated with the node,</li>
 * </ul></li>
 * </ul>
 * For each node of the tree there is a corresponding {@link PropertyEditor}.
 * <p>
 * Example: To create a new <code>JPropertyTreePanel</code> :</p>
 * <ul>
 * <li>Create the {@link PropertyEditor}s and set them visible</li>
 * <pre>
 *       PropertyEditor p1 = new PropertyEditor();
 *       JTextField tf = new JTextField("1");
 *       p1.addProperty(tf,"1","prop 1");
 *       p1.setVisible(true);
 *
 *       PropertyEditor p2 = new PropertyEditor();
 *       JTextField tf2 = new JTextField("2");
 *       p2.addProperty(tf2,"2","prop 2");
 *       p2.setVisible(true);
 * </pre>
 * <li>Create the <code>JPropertyTreePanel</code></li>
 * <pre>
 *      JPropertyTreePanel panel = new JPropertyTreePanel();
 * </pre>
 * <li>Associate editors with nodes :</li>
 * <pre>
 *     panel.addPropertyListNode(panel.getRootNode(), "node 1", p1);
 *     DefaultMutableTreeNode node = panel.addEmptyNode(panel.getRootNode(), "sub-node");
 *     panel.addPropertyListNode(node, "node 2", p2);
 * </pre>
 * </ul>
 *
 * @version 0.9.23
 */
public class JPropertyTreePanel extends JSplitPane {
   protected JTree tree;
   protected DefaultTreeModel model;
   private JPanel southPanel = null;
   protected PropertyTreeRenderer renderer;
   protected JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
   protected Map<DefaultMutableTreeNode, Object> props = new HashMap(10);

   /**
    * Constructor.
    */
   public JPropertyTreePanel() {
      super();

      // create TreeModel
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("options");
      model = new DefaultTreeModel(root);

      // create Tree and associated renderer
      tree = new JTree(model);
      renderer = new PropertyTreeRenderer();
      tree.setCellRenderer(renderer);

      // Initialize listeners in JTree
      tree.addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
               changePropertyView(node);
            }
         }
      });

      // layout components
      this.setLayout(new BorderLayout());
      this.add(split, BorderLayout.CENTER);

      JScrollPane scroll1 = new JScrollPane(tree);
      split.add(scroll1);
      southPanel = new JPanel();
   }

   /**
    * Add an empty node to the tree, and associate this node with an icon.
    *
    * @param parentnode the parent node of the newly created node
    * @param listname the name of the node
    * @param icon the optional icon to associate with the node (or null if there is no icon)
    * @return the Node
    */
   public DefaultMutableTreeNode addEmptyNode(DefaultMutableTreeNode parentnode, String listname, Icon icon) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(listname);
      if (icon != null) {
         renderer.registerIcon(listname, icon);
      }
      parentnode.add(node);

      return node;
   }

   /**
    * Add an empty node to the tree, with no icon.
    *
    * @param parentnode the parent node of the newly created node
    * @param listname the name of the node
    * @return the Node
    */
   public DefaultMutableTreeNode addEmptyNode(DefaultMutableTreeNode parentnode, String listname) {
      Icon icon = null;
      DefaultMutableTreeNode node = addEmptyNode(parentnode, listname, icon);

      return node;
   }

   /**
    * Add a custom node to the tree, with no icon.
    *
    * @param parentnode the parent node of the newly created node
    * @param childNode the child node of the newly created node
    * @see #addNode(DefaultMutableTreeNode, DefaultMutableTreeNode, Icon)
    * @return the Node
    */
   public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentnode, DefaultMutableTreeNode childNode) {
      addNode(parentnode, childNode, null);
      return childNode;
   }

   /**
    * Add a custom node to the tree, and associate it with an icon.
    * <p>
    * This Node can contain other nodes, and each of these nodes can be associated with
    * a {@link PropertyEditor} userObject. This allows to add easily a whole property tree
    * structure to the node.<p>
    * <p>
    * For example, the following code add two nodes associated with {@link PropertyEditor}s :
    * <p>
    * <ul>
    * <li>Create two {@link PropertyEditor}s :</li>
    * <pre>
    *       PropertyEditor p1 = new PropertyEditor();
    *       JTextField tf = new JTextField("1");
    *       p1.addProperty(tf,"1","prop 1");
    *       p1.setVisible(true);
    *
    *       PropertyEditor p2 = new PropertyEditor();
    *       JTextField tf2 = new JTextField("2");
    *       p2.addProperty(tf,"2","prop 2");
    *       p2.setVisible(true);
    * </pre>
    * <li>Create the nodes associated to the editors :</li>
    * <pre>
    *       DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Node 1");
    *       node1.setUserObject(p1);
    *       DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Node 2");
    *       node2.setUserObject(p2);
    * </pre>
    * <li>Create the
    * <code>JPropertyTreePanel</code></li>
    * <pre>
    *      JPropertyTreePanel panel = new JPropertyTreePanel();
    *      panel.addEmptyNode(panel.getRootNode(), node1, null);
    *      panel.addEmptyNode(panel.getRootNode(), node2, null);
    * </pre>
    * </ul>
    *
    * @param parentnode the parent node of the newly created node
    * @param childNode the child node of the newly created node
    * @param icon the optional icon to associate with the node (or null if there is no icon)
    * @return the Node
    */
   public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentnode, DefaultMutableTreeNode childNode, Icon icon) {
      if (icon != null) {
         renderer.registerIcon(childNode.getUserObject(), icon);
      }
      parentnode.add(childNode);

      TreeIterator it = new TreeIterator(childNode);
      while (it.hasNext()) {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) (it.next());
         Object userObject = node.getUserObject();
         if (userObject instanceof PropertyEditor) {
            props.put(node, userObject);
         }
      }

      return childNode;
   }

   /**
    * Add a node to the tree, and associate this node with a PropertyEditor, and an icon.
    *
    * @param parentnode the parent node of the newly created node
    * @param listname the name of the node
    * @param icon the optional icon to associate with the node (or null if there is no icon)
    * @param editor the {@link PropertyEditor} associated with the node
    * @return the Node
    */
   public DefaultMutableTreeNode addPropertyListNode(DefaultMutableTreeNode parentnode, String listname, Icon icon, PropertyEditor editor) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(listname);
      if (icon != null) {
         renderer.registerIcon(listname, icon);
      }
      parentnode.add(node);
      props.put(node, editor);

      return node;
   }

   /**
    * Add a node to the tree, with an associated PropertyEditor, but no icon.
    *
    * @param parentnode the parent node of the newly created node
    * @param listname the name of the node
    * @param editor the {@link PropertyEditor} associated with the node
    * @return the Node
    */
   public DefaultMutableTreeNode addPropertyListNode(DefaultMutableTreeNode parentnode, String listname, PropertyEditor editor) {
      DefaultMutableTreeNode node = addPropertyListNode(parentnode, listname, null, editor);
      return node;
   }

   /**
    * Return the root node of the tree.
    *
    * @return the root node of the tree
    */
   public DefaultMutableTreeNode getRootNode() {
      return (DefaultMutableTreeNode) (model.getRoot());
   }

   /**
    * This method is used internally to manage node selections in the tree :
    *
    * @param node the node
    */
   protected void changePropertyView(DefaultMutableTreeNode node) {
      JPanel panel = southPanel;
      if (props.containsKey(node)) {
         panel = (PropertyEditor) (props.get(node));
      }

      split.setLastDividerLocation(split.getDividerLocation());
      split.setRightComponent(new JScrollPane(panel));
      split.setDividerLocation(split.getLastDividerLocation());
      //split.resetToPrefereredSizes();
   }

   public JTree getTree() {
      return tree;
   }

   /**
    * Return the internal JSplitPane.
    * This method can be useful to set Divider locations for the panel.
    *
    * @return the internal JSplitPane
    */
   public JSplitPane getSplitPane() {
      return split;
   }

   /**
    * Return the internal TreeModel.
    *
    * @return the internal TreeModel
    */
   public TreeModel getTreeModel() {
      return model;
   }

   public void expandAll() {
      int i = 0;
      while (i < tree.getRowCount()) {
         tree.expandRow(i);
         i++;
      }
   }

   public void collapseAll() {
      int i = 0;
      while (i < tree.getRowCount()) {
         tree.collapseRow(i);
         i++;
      }
   }

   /**
    * This internal class is responsble for nodes rendering.
    */
   public class PropertyTreeRenderer extends DefaultTreeCellRenderer {
      private final Map<Object, Icon> iconBundle = new HashMap(10);

      public PropertyTreeRenderer() {
         super();
      }

      /**
       * Register an icon with the userObject value.
       *
       * @param value the userObject value
       * @param icon the icon
       */
      public void registerIcon(Object value, Icon icon) {
         iconBundle.put(value, icon);
      }

      /**
       * Set the value of the current tree cell to value. The only specific action is the icons setting.
       */
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
         boolean expanded, boolean leaf, int row, boolean hasFocus) {
         super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

         this.setIcon(getValueIcon(value, expanded));
         return this;
      }

      /**
       * Called internally to set the icon for a value.
       */
      protected Icon getValueIcon(Object value, boolean expanded) {
         Icon icon;
         if (iconBundle.containsKey(value)) {
            icon = (Icon) (iconBundle.get(value));
         } else {
            icon = getDefaultIcon(expanded);
         }
         return icon;
      }

      /**
       * Called internally to access to the default icons for
       * expanded and unexpanded nodes.
       */
      protected Icon getDefaultIcon(boolean expanded) {
         if (expanded) {
            return getDefaultClosedIcon();
         } else {
            return getDefaultOpenIcon();
         }
      }
   }
}
