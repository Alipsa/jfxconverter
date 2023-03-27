/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

/**
 * A node with an associated line number.
 *
 * @version 1.2.40
 */
public interface NumberedNode {
   /**
    * Set the line number of the Node in the XML file. Do nothing by default.
    *
    * @param lineNumber the line number
    */
   public default void setLineNumber(int lineNumber) {       
   }
   
   /**
    * Return true if the Node has an associated line number. Return false by default.
    *
    * @return true if the Node has an associated line number
    */
   public default boolean hasLineNumber() {
      return false;
   }

   /**
    * Return the line number of the Node in the XML file. Return -1 by default.
    *
    * @return the line number
    */
   public default int getLineNumber() {
      return -1;
   }
}
