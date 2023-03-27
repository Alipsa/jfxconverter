/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

/**
 * This class is a straightforward NodeFilter.
 *
 * @version 0.9.6
 */
public class DefaultNodeFilter implements NodeFilter {
   private short[] types = new short[13];
   private short geneType = FILTER_ACCEPT;

   public DefaultNodeFilter() {
      for (int i = 0; i < 13; i++) {
         types[i] = -1;
      }
   }

   /**
    * Test whether a specified node is visible in the logical view of a <code>TreeWalker</code> or <code>NodeIterator</code>.
    *
    * @param n The node
    * @return A constant to determine whether the node is accepted, rejected, or skipped
    */
   @Override
   public short acceptNode(Node n) {
      short ret = geneType;
      short specret = types[n.getNodeType()];
      if (specret != -1) {
         ret = specret;
      }
      return ret;
   }

   /**
    * Add a filtering rule.
    *
    * @param type the Node type
    * @param rule the rule
    */
   public void addFilterRule(short type, short rule) {
      if (type == SHOW_ALL) {
         geneType = rule;
      }
      if (type == SHOW_ATTRIBUTE) {
         types[Node.ATTRIBUTE_NODE] = rule;
      } else if (type == SHOW_CDATA_SECTION) {
         types[Node.CDATA_SECTION_NODE] = rule;
      } else if (type == SHOW_COMMENT) {
         types[Node.COMMENT_NODE] = rule;
      } else if (type == SHOW_DOCUMENT) {
         types[Node.DOCUMENT_NODE] = rule;
      } else if (type == SHOW_DOCUMENT_FRAGMENT) {
         types[Node.DOCUMENT_FRAGMENT_NODE] = rule;
      } else if (type == SHOW_DOCUMENT_TYPE) {
         types[Node.DOCUMENT_TYPE_NODE] = rule;
      } else if (type == SHOW_ELEMENT) {
         types[Node.ELEMENT_NODE] = rule;
      } else if (type == SHOW_ENTITY) {
         types[Node.ENTITY_NODE] = rule;
      } else if (type == SHOW_ENTITY) {
         types[Node.ENTITY_NODE] = rule;
      } else if (type == SHOW_ENTITY_REFERENCE) {
         types[Node.ENTITY_REFERENCE_NODE] = rule;
      } else if (type == SHOW_NOTATION) {
         types[Node.NOTATION_NODE] = rule;
      } else if (type == SHOW_PROCESSING_INSTRUCTION) {
         types[Node.PROCESSING_INSTRUCTION_NODE] = rule;
      } else if (type == SHOW_TEXT) {
         types[Node.TEXT_NODE] = rule;
      }
   }

   /**
    * Return a filtering rule.
    *
    * @param type the element type
    * @return the rule
    */
   public short getFilterRule(short type) {
      short ret = geneType;
      short specret = types[type];
      if (specret != -1) {
         ret = specret;
      }
      return ret;
   }

}
