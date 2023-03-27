/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

/**
 * The options used by the {@link XMLNodeUtilities} and {@link XMLNodeUtilities2} classes.
 *
 * @since 1.2.40
 */
public interface XMLNodeUtilitiesOptions {
   /**
    * The option to preserve spaces when creating a tree of nodes.
    */
   public static final int PRESERVE_SPACE = 1;
   /**
    * The option to show exceptions when creating a tree of nodes.
    */
   public static final int SHOW_EXCEPTIONS = 2;
   /**
    * The option to show warnings when creating a tree of nodes.
    */
   public static final int SHOW_WARNINGS = 4;
   /**
    * The option to keep the line numbers when creating a tree of nodes.
    */
   public static final int KEEP_LINE_NUMBERS = 8;
   /**
    * The option to be aware of the namespace.
    */
   public static final int NAMESPACE_AWARE = 16;
}
