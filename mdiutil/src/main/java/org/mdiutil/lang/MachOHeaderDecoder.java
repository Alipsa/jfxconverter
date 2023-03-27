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
import java.nio.ByteOrder;
import static org.mdiutil.lang.HeaderDecoder.ENDIAN_INVALID;
import static org.mdiutil.lang.HeaderDecoder.EXE_INVALID;

/**
 * This class reads a Mach-O executable File to detect if this File is for a 64 bits or a 32 bits architecture. Note that
 * Universal binaries formats are detected but not decoded.
 *
 * @version 0.9.51
 */
public class MachOHeaderDecoder implements HeaderDecoder {
   private final File file;
   private short arch = EXE_INVALID;
   private short endianness = ENDIAN_INVALID;
   private static final int OBJECT32 = 0xFEEDFACE;
   private static final int OBJECT64 = 0xFEEDFACF;
   private static final int FAT = 0xCAFEBABE;

   /**
    * Constructor.
    *
    * @param file the Mach-O executable file
    */
   public MachOHeaderDecoder(File file) {
      this.file = file;
      parse();
   }

   private void parse() {
      try (RandomAccessFile binary = new RandomAccessFile(file, "r")) {
         binary.seek(0);
         byte[] magicBytes = new byte[4];
         binary.readFully(magicBytes);

         for (int i = 0; i < 2; i++) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(magicBytes);
            endianness = (i == 0) ? ENDIAN_BIG_ENDIAN : ENDIAN_LITTLE_ENDIAN;
            ByteOrder byteOrder = (i == 0) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
            byteBuffer.order(byteOrder);
            switch (byteBuffer.getInt()) {
               case FAT:
                  arch = EXE_UNIVERSAL;
                  break;
               case OBJECT32:
                  arch = EXE_32BITS;
                  break;
               case OBJECT64:
                  arch = EXE_64BITS;
                  break;
               default:
                  arch = EXE_INVALID;
            }
         }
      } catch (IOException ex) {
         arch = EXE_INVALID;
      }
   }

   /**
    * Return the Mach-O File architecture.
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
