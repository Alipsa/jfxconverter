/*------------------------------------------------------------------------------
* Copyright (C) 2019 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * InputStream implementation that transforms a Reader to a byte stream.
 *
 * @since 0.9.54
 */
public class ReaderInputStream extends InputStream {
   private static final int DEFAULT_SIZE = 1024;
   private final Reader reader;
   private final CharsetEncoder encoder;
   /**
    * CharBuffer used as input for the decoder.
    */
   private final CharBuffer inputBuffer;

   /**
    * ByteBuffer used as output for the decoder.
    */
   private final ByteBuffer decodingBuffer;
   private CoderResult cResult;
   private boolean hasEnded = false;

   /**
    * Constructor.
    *
    * @param reader the target Reader
    * @param encoder the charset encoder
    */
   public ReaderInputStream(Reader reader, CharsetEncoder encoder) {
      this(reader, encoder, DEFAULT_SIZE);
   }

   /**
    * Constructor.
    *
    * @param reader the target Reader
    * @param encoder the charset encoder
    * @param bufferSize the size of the input buffer in number of characters
    */
   public ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
      this.reader = reader;
      this.encoder = encoder;
      this.inputBuffer = CharBuffer.allocate(bufferSize);
      this.decodingBuffer = ByteBuffer.allocate(128);

   }

   /**
    * Constructor.
    *
    * @param reader the target {@link Reader}
    * @param charset the charset encoding
    * @param bufferSize the size of the input buffer in number of characters
    */
   public ReaderInputStream(Reader reader, Charset charset, int bufferSize) {
      this(reader, charset.newEncoder(), bufferSize);
   }

   /**
    * Construct a new ReaderInputStream with a default input buffer size of 1024 characters.
    *
    * @param reader the target Reader
    * @param charset the charset encoding
    */
   public ReaderInputStream(Reader reader, Charset charset) {
      this(reader, charset, DEFAULT_SIZE);
   }

   /**
    * Construct a new ReaderInputStream that uses the default character encoding
    * with a default input buffer size of 1024 characters.
    *
    * @param reader the target Reader
    */
   public ReaderInputStream(Reader reader) {
      this(reader, Charset.defaultCharset());
   }

   /**
    * Fills the internal buffer from the reader.
    *
    * @throws IOException If an I/O error occurs
    */
   private void fillDecodingBuffer() throws IOException {
      if (!hasEnded && (cResult == null || cResult.isUnderflow())) {
         inputBuffer.compact();
         int pos = inputBuffer.position();
         int c = reader.read(inputBuffer);
         if (c == -1) {
            hasEnded = true;
         } else {
            inputBuffer.position(pos + c);
         }
      }
      decodingBuffer.compact();
      cResult = encoder.encode(inputBuffer, decodingBuffer, hasEnded);
      decodingBuffer.flip();
   }

   /**
    * Read the specified number of bytes into an array.
    *
    * @param b the byte array to read into
    * @param off the offset to start reading bytes into
    * @param len the number of bytes to read
    * @return the number of bytes read or -1
    * if the end of the stream has been reached
    * @throws IOException if an I/O error occurs
    */
   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      int read = 0;
      if (len == 0) {
         return 0;
      }
      while (len > 0) {
         if (decodingBuffer.hasRemaining()) {
            int c = Math.min(decodingBuffer.remaining(), len);
            decodingBuffer.get(b, off, c);
            off += c;
            len -= c;
            read += c;
         } else {
            fillDecodingBuffer();
            if (hasEnded && !decodingBuffer.hasRemaining()) {
               break;
            }
         }
      }
      return read == 0 && hasEnded ? -1 : read;
   }

   /**
    * Read the specified number of bytes into an array.
    *
    * @param b the byte array to read into
    * @return the number of bytes read or -1 if the end of the stream has been reached
    * @throws IOException if an I/O error occurs
    */
   @Override
   public int read(byte[] b) throws IOException {
      return read(b, 0, b.length);
   }

   /**
    * Read a single byte.
    *
    * @return either the byte read or -1 if the end of the stream has been reached
    * @throws IOException if an I/O error occurs
    */
   @Override
   public int read() throws IOException {
      while (true) {
         if (decodingBuffer.hasRemaining()) {
            return decodingBuffer.get() & 0xFF;
         }
         fillDecodingBuffer();
         if (hasEnded && !decodingBuffer.hasRemaining()) {
            return -1;
         }
      }
   }

   /**
    * Close the stream.
    *
    * @throws IOException if an I/O error occurs
    */
   @Override
   public void close() throws IOException {
      reader.close();
   }
}
