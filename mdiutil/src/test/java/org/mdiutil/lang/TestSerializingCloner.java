/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

/**
 * A test class (not serializable) for the SerializingCloner class.
 *
 * @since 0.23
 */
public class TestSerializingCloner {
   public int int1;
   public String str1 = null;

   public TestSerializingCloner(int int1, String str1) {
      this.int1 = int1;
      this.str1 = str1;
   }
}
