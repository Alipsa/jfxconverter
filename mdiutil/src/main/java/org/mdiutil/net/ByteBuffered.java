/*
 * Copyright 2003-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2010 Herve Girod
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

import java.nio.ByteBuffer;
import java.io.IOException;

/** This is an interface to adapt existing APIs to use {@link ByteBuffer
 *  <tt>ByteBuffers</tt>} as the underlying
 *  data format.  Only the initial producer and final consumer have to be changed.<p>
 *
 *  For example, the Zip/Jar code supports {@link java.io.InputStream <tt>InputStreams</tt>}.
 *  To make the Zip code use {@link java.nio.MappedByteBuffer <tt>MappedByteBuffers</tt>} as
 *  the underlying data structure, it can create a class of InputStream that wraps the ByteBuffer,
 *  and implements the ByteBuffered interface. A co-operating class several layers
 *  away can ask the InputStream if it is an instance of ByteBuffered, then
 *  call the {@link #getByteBuffer()} method.
 * 
 * @since 0.4.5
 */
interface ByteBuffered {

     /**
     * Returns the <tt>ByteBuffer</tt> behind this object, if this particular
     * instance has one. An implementation of <tt>getByteBuffer()</tt> is allowed
     * to return <tt>null</tt> for any reason.
     *
     * @return  The <tt>ByteBuffer</tt>, if this particular instance has one,
     *          or <tt>null</tt> otherwise.
     *
     * @throws  IOException
     *          If the ByteBuffer is no longer valid.
     *
     * @since  1.5
     */
    public ByteBuffer getByteBuffer() throws IOException;
}
