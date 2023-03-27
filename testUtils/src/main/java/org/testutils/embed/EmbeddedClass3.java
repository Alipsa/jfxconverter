/*------------------------------------------------------------------------------
* Copyright (C) 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.testutils.embed;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @since 1.2.22
 */
public class EmbeddedClass3 {
   private List<Integer> list = null;

   public EmbeddedClass3() {
      list = new ArrayList<>();
      list.add(2);
   }

   public List<Integer> getValue() {
      return list;
   }
}
