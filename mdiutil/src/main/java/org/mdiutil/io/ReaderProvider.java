/*------------------------------------------------------------------------------
* Copyright (C) 2017 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.Reader;

/**
 * This interface allows to get readers. This interface allows to get new Readers from the same "source" in cases where
 * it would be necessary to reuse them (many Readers can not be reused again).
 *
 * @since 0.9.25
 */
public interface ReaderProvider {
   /**
    * Return a new reader.
    *
    * @return the reader
    */
   public Reader newReader();
}
