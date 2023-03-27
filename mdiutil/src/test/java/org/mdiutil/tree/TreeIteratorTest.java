/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * TreeIteratorTest
 *
 * @author GIROD
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "tree")
public class TreeIteratorTest {
   private DefaultMutableTreeNode root;
   private DefaultMutableTreeNode child1;
   private DefaultMutableTreeNode child2;
   private DefaultMutableTreeNode child3;
   private DefaultMutableTreeNode child11;
   private DefaultMutableTreeNode child12;
   private DefaultMutableTreeNode child21;

   public TreeIteratorTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
      createTree();
   }

   @After
   public void tearDown() {
   }

   private DefaultMutableTreeNode createTree() {
      root = new DefaultMutableTreeNode("root");
      child1 = new DefaultMutableTreeNode("child1");
      child2 = new DefaultMutableTreeNode("child2");
      child3 = new DefaultMutableTreeNode("child3");
      root.add(child1);
      root.add(child2);
      root.add(child3);
      child11 = new DefaultMutableTreeNode("child1_1");
      child12 = new DefaultMutableTreeNode("child1_2");
      child1.add(child11);
      child1.add(child12);
      child21 = new DefaultMutableTreeNode("child2_1");
      child2.add(child21);

      return root;
   }

   /**
    * Test of iteration from the root node.
    */
   @Test
   public void testIteration1() {
      System.out.println("TreeIteratorTest : testIteration1");

      TreeIterator it = new TreeIterator(root);
      Object node;

      int i = 0;
      while (it.hasNext()) {
         node = it.next();
         if (i == 0) {
            assertEquals(node, child1);
         } else if (i == 1) {
            assertEquals(node, child11);
         } else if (i == 2) {
            assertEquals(node, child12);
         } else if (i == 3) {
            assertEquals(node, child2);
         } else if (i == 4) {
            assertEquals(node, child21);
         } else if (i == 5) {
            assertEquals(node, child3);
         } else if (i > 5) {
            fail("too many elements");
         }
         i++;
      }
   }

   /**
    * Test of iteration from a child node, mode : all nodes after the child node.
    */
   @Test
   public void testIteration2() {
      System.out.println("TreeIteratorTest : testIteration2");

      TreeIterator it = new TreeIterator(child2, TreeIterator.MODE_AFTER_ROOT);
      Object node;

      int i = 0;
      while (it.hasNext()) {
         node = it.next();
         if (i == 0) {
            assertEquals(node, child21);
         } else if (i == 1) {
            assertEquals(node, child3);
         } else if (i > 1) {
            fail("too many elements");
         }
         i++;
      }
   }

   /**
    * Test of iteration from a child node, mode : only nodes under the child node.
    */
   @Test
   public void testIteration3() {
      System.out.println("TreeIteratorTest : testIteration3");

      TreeIterator it = new TreeIterator(child2, TreeIterator.MODE_UNDER_ROOT);
      Object node;

      int i = 0;
      while (it.hasNext()) {
         node = it.next();
         if (i == 0) {
            assertEquals(node, child21);
         } else if (i > 0) {
            fail("too many elements");
         }
         i++;
      }
   }

   /**
    * Test of iteration from a child node, mode : only nodes under a leaf node.
    */
   @Test
   public void testIteration4() {
      System.out.println("TreeIteratorTest : testIteration4");

      TreeIterator it = new TreeIterator(child12, TreeIterator.MODE_UNDER_ROOT);
      if (it.hasNext()) {
         fail("too many elements");
      }
   }
}
