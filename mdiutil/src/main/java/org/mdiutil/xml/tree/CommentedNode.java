/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

/**
 * A node with comments.
 *
 * @since 1.2.39
 */
public interface CommentedNode {
   /**
    * Set the node comment. Do nothing by default.
    *
    * @param comment the comment
    */
   public default void setComment(String comment) {
   }

   /**
    * Return the node comment. Return null by default.
    *
    * @return the node comment
    */
   public default String getComment() {
      return null;
   }

   /**
    * Return true if the node has a comment. Return false by default.
    *
    * @return true if the node has a comment
    */
   public default boolean hasComment() {
      return false;
   }
}
