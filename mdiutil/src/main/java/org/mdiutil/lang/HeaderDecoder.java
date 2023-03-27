/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

/**
 * The interface for executable files header decoders.
 *
 * @since 0.9.17
 */
public interface HeaderDecoder {
   /**
    * The value for invalid architectures.
    */
   public static final short EXE_INVALID = 0;
   /**
    * The value for 32 bits executables.
    */
   public static final short EXE_32BITS = 1;
   /**
    * The value for 64 bits executables.
    */
   public static final short EXE_64BITS = 2;
   /**
    * The value for universal binaries executables.
    */
   public static final short EXE_UNIVERSAL = 3;
   /**
    * The value for invalid endianness.
    */
   public static final short ENDIAN_INVALID = 0;
   /**
    * The value for little-endian executables.
    */
   public static final short ENDIAN_LITTLE_ENDIAN = 1;
   /**
    * The value for big-endian executables.
    */
   public static final short ENDIAN_BIG_ENDIAN = 2;

   /**
    * Return the executable architecture.
    * The possible returned values are:
    * <ul>
    * <li>{@link #EXE_INVALID}: the value for invalid Portable Executable Files (can not exist,
    * not being readable, or not being a valid Portable Executable File)</li>
    * <li>{@link #EXE_32BITS}: the value for 32 bits executables</li>
    * <li>{@link #EXE_64BITS}: the value for 64 bits executables</li>
    * <li>{@link #EXE_UNIVERSAL}: the value for universal binaries executables</li>
    * </ul>
    *
    * @return the executable architecture
    */
   public short getExecutableType();

   /**
    * Return the executable endianness. The possible returned values are:
    * <ul>
    * <li>{@link #ENDIAN_INVALID}: the value if the indianess could be be detected</li>
    * <li>{@link #ENDIAN_LITTLE_ENDIAN}: the value little-endian executable files</li>
    * <li>{@link #ENDIAN_BIG_ENDIAN}: the value big-endian executable files</li>
    * </ul>
    *
    * @return the executable endianness
    */
   public short getEndianness();
}
