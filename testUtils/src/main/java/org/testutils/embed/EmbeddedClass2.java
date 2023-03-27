/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.testutils.embed;

import org.testutils.embed.filtered.FilteredClass;

/**
 *
 * @since 1.2.22
 */
public class EmbeddedClass2 {
   private FilteredClass filtered = null;

   public EmbeddedClass2() {
      this.filtered = new FilteredClass();
   }

   public FilteredClass getFilteredClass() {
      return filtered;
   }
}
