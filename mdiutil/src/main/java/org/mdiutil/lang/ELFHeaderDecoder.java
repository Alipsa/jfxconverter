/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class reads an Executable and Linkable Format (elf) executable file to detect if this File is for a 64 bits or a 32 bits
 * architecture.
 *
 * @version 0.9.51
 */
public class ELFHeaderDecoder implements HeaderDecoder {
   private final File file;
   private short arch = EXE_INVALID;
   private short endianness = ENDIAN_INVALID;

   /**
    * Constructor.
    *
    * @param file the ELF executable file
    */
   public ELFHeaderDecoder(File file) {
      this.file = file;
      parse();
   }

   private void parse() {
      try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
         ByteBuffer buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
         byte[] ident = new byte[16];
         buf.get(ident);
         if (ident[0] != 0x7F || ident[1] != 'E' || ident[2] != 'L' || ident[3] != 'F') {
            arch = EXE_INVALID;
            return;
         }

         switch (ident[4]) {
            case 1:
               arch = EXE_32BITS;
               break;
            case 2:
               arch = EXE_64BITS;
               break;
            default:
               arch = EXE_INVALID;
         }

         switch (ident[5]) {
            case 1:
               endianness = ENDIAN_LITTLE_ENDIAN;
               break;
            case 2:
               endianness = ENDIAN_BIG_ENDIAN;
               break;
            default:
               endianness = ENDIAN_INVALID;
         }
      } catch (IOException e) {
         arch = EXE_INVALID;
      }
   }

   /**
    * Return the ELF File architecture.
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
      return arch;
   }

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
   @Override
   public short getEndianness() {
      return endianness;
   }
}
