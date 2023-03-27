/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2010, 2019 Herve Girod
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package org.mdiutil.net;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Utility class for caching per-thread decoders and encoders.
 *
 * @version 1.2.4
 */
class ThreadLocalCoders {
   private static final int CACHE_SIZE = 3;

   private static abstract class Cache {
      // Thread-local reference to array of cached objects, in LRU order
      private final ThreadLocal cache = new ThreadLocal();
      private final int size;

      Cache(int size) {
         this.size = size;
      }

      abstract Object create(Object name);

      private void moveToFront(Object[] oa, int i) {
         Object ob = oa[i];
         for (int j = i; j > 0; j--) {
            oa[j] = oa[j - 1];
         }
         oa[0] = ob;
      }

      abstract boolean hasName(Object ob, Object name);

      Object forName(Object name) {
         Object[] oa = (Object[]) cache.get();
         if (oa == null) {
            oa = new Object[size];
            cache.set(oa);
         } else {
            for (int i = 0; i < oa.length; i++) {
               Object ob = oa[i];
               if (ob == null) {
                  continue;
               }
               if (hasName(ob, name)) {
                  if (i > 0) {
                     moveToFront(oa, i);
                  }
                  return ob;
               }
            }
         }

         // Create a new object
         Object ob = create(name);
         oa[oa.length - 1] = ob;
         moveToFront(oa, oa.length - 1);
         return ob;
      }
   }
   private static Cache decoderCache = new Cache(CACHE_SIZE) {
      @Override
      boolean hasName(Object ob, Object name) {
         if (name instanceof String) {
            return (((CharsetDecoder) ob).charset().name().equals(name));
         }
         if (name instanceof Charset) {
            return ((CharsetDecoder) ob).charset().equals(name);
         }
         return false;
      }

      @Override
      Object create(Object name) {
         if (name instanceof String) {
            return Charset.forName((String) name).newDecoder();
         }
         if (name instanceof Charset) {
            return ((Charset) name).newDecoder();
         }
         assert false;
         return null;
      }
   };

   public static CharsetDecoder decoderFor(Object name) {
      CharsetDecoder cd = (CharsetDecoder) decoderCache.forName(name);
      cd.reset();
      return cd;
   }
   private static final Cache ENCODER_CACHE = new Cache(CACHE_SIZE) {
      @Override
      boolean hasName(Object ob, Object name) {
         if (name instanceof String) {
            return (((CharsetEncoder) ob).charset().name().equals(name));
         }
         if (name instanceof Charset) {
            return ((CharsetEncoder) ob).charset().equals(name);
         }
         return false;
      }

      @Override
      Object create(Object name) {
         if (name instanceof String) {
            return Charset.forName((String) name).newEncoder();
         }
         if (name instanceof Charset) {
            return ((Charset) name).newEncoder();
         }
         assert false;
         return null;
      }
   };

   public static CharsetEncoder encoderFor(Object name) {
      CharsetEncoder ce = (CharsetEncoder) ENCODER_CACHE.forName(name);
      ce.reset();
      return ce;
   }
}
