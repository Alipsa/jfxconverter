/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

/**
 * The interface specifying the behavior for InputSource caching in the {@link EntityListResolver}.
 *
 * $@since 1.2.39
 */
public interface InputSourceCaching {
   /**
    * The default behavior for caching InputSources. Will defer the behavior to the {@link EntityListResolver}.
    */
   public static short CACHING_DEFAULT = 0;
   /**
    * Force caching InputSources.
    */
   public static short CACHING_SOURCES = 1;
   /**
    * Force not caching InputSources.
    */
   public static short NOT_CACHING_SOURCES = 2;

   /**
    * Set the InputSources caching behavior.
    *
    * @param cachingType the caching type
    */
   public void setInputSourceCaching(short cachingType);

   /**
    * Return the InputSources caching behavior.
    *
    * @return the caching type
    */
   public short getInputSourceCaching();
}
