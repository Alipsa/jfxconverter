/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class reads a Windows Portable Executable File to detect if this File is for a 64 bits or a 32 bits
 * architecture.
 *
 * @version 0.9.45
 */
public class PEHeaderDecoder implements HeaderDecoder {
   private final File file;

   /**
    * Constructor.
    *
    * @param file the Windows Portable Executable File
    */
   public PEHeaderDecoder(File file) {
      this.file = file;
   }

   /**
    * Return a Windows Portable Executable File architecture.
    * The possible returned values are:
    * <ul>
    * <li>{@link #EXE_INVALID}: the value for invalid Portable Executable Files (can not exist,
    * not being readable, or not being a valid Portable Executable File)</li>
    * <li>{@link #EXE_32BITS}: the value for 32 bits executables</li>
    * <li>{@link #EXE_64BITS}: the value for 64 bits executables</li>
    * </ul>
    *
    * @return the executable architecture
    */
   @Override
   public short getExecutableType() {
      int machineType;
      try (InputStream is = new FileInputStream(file)) {
         int magic = is.read() | is.read() << 8;
         if (magic != 0x5A4D) {
            return EXE_INVALID;
         }
         for (int i = 0; i < 58; i++) {
            is.read();
         }
         int address = is.read() | is.read() << 8 | is.read() << 16 | is.read() << 24;
         for (int i = 0; i < address - 60; i++) {
            is.read();
         }
         machineType = is.read() | is.read() << 8;
      } catch (IOException e) {
         return EXE_INVALID;
      }
      return machineType == 0x8664 ? EXE_64BITS : EXE_32BITS;
   }

   /**
    * Return {@link #ENDIAN_BIG_ENDIAN}.
    *
    * @return {@link #ENDIAN_BIG_ENDIAN}
    */
   @Override
   public short getEndianness() {
      return ENDIAN_LITTLE_ENDIAN;
   }
}
