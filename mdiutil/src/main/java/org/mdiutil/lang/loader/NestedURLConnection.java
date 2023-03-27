/*
 * Copyright (c) 2016 Erik Håkansson, http://squark.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*
Copyright (c) 2021, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.lang.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * An URLConnection used by a {@link NestedURLClassLoader}.
 *
 * @since 1.2.22
 */
class NestedURLConnection extends URLConnection implements AutoCloseable {
   private URL jarFileURL;
   private String entryName;
   private String subEntryName;
   private String file;
   private ByteArrayOutputStream entryOutputStream;
   private ByteArrayOutputStream subEntryOutputStream;

   /**
    * Constructs a URL connection to the specified URL. A connection to the object referenced by the URL is not created.
    *
    * @param url the specified URL.
    */
   NestedURLConnection(URL url) throws IOException {
      super(url);
      parseSpecs(url);
      connect();
   }

   /**
    * Modified from java.net.JarURLConnection
    *
    * @param url URL to parse
    * @throws MalformedURLException
    */
   private void parseSpecs(URL url) throws MalformedURLException {
      String spec = url.getFile();

      if (spec.startsWith("jar:")) {
         spec = spec.substring(4, spec.length());
      }

      int separator = spec.indexOf("!/");

      jarFileURL = new URL(spec.substring(0, separator++));
      entryName = null;

      /* if ! is the last letter of the innerURL, entryName is null */
      if (++separator != spec.length()) {
         entryName = spec.substring(separator, spec.length());
         int subEntrySeparator = entryName.indexOf("!/");
         if (subEntrySeparator != -1) {
            subEntryName = entryName.substring(subEntrySeparator + 2, entryName.length());
            entryName = entryName.substring(0, subEntrySeparator);
         }
      }
   }

   private InputStream getInputStream(ByteArrayOutputStream stream) {
      byte[] bytes = stream.toByteArray();
      InputStream inputStream = new ByteArrayInputStream(bytes);
      return inputStream;
   }

   @Override
   public final void connect() throws IOException {
      file = jarFileURL.getFile();
      if (entryName != null) {
         JarFile jarFile = new JarFile(file);
         int len;
         byte[] b;
         try (InputStream is = jarFile.getInputStream(jarFile.getEntry(entryName))) {
            b = new byte[2048];
            entryOutputStream = new ByteArrayOutputStream();
            while ((len = is.read(b)) > 0) {
               entryOutputStream.write(b, 0, len);
            }
         }
         if (subEntryName != null) {
            try (JarInputStream entryInputStream = new JarInputStream(getInputStream(entryOutputStream))) {
               JarEntry subEntry;
               while ((subEntry = entryInputStream.getNextJarEntry()) != null) {
                  if (subEntry.getName().equals(subEntryName)) {
                     subEntryOutputStream = new ByteArrayOutputStream();
                     if (subEntry.isDirectory()) {
                        try (JarInputStream newEntryInputStream = new JarInputStream(getInputStream(entryOutputStream)); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(subEntryOutputStream)) {
                           JarEntry _file;
                           while ((_file = newEntryInputStream.getNextJarEntry()) != null) {
                              if (_file.getName().startsWith(subEntryName)) {
                                 Path path;
                                 if ((path = Paths.get(_file.getName())).getNameCount() - Paths.get(subEntryName).getNameCount() == 1) {
                                    outputStreamWriter.append(path.getFileName().toString()).append('\n');
                                 }
                              }
                           }
                        }
                     } else {
                        b = new byte[2048];
                        while ((len = entryInputStream.read(b)) > 0) {
                           subEntryOutputStream.write(b, 0, len);
                        }
                        break;
                     }
                  }
               }
            }
            entryOutputStream.reset();
            entryOutputStream.close();
            entryOutputStream = null;
         }
      }
      connected = true;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      if (!connected) {
         connect();
      }
      if (subEntryName != null) {
         if (subEntryOutputStream != null) {
            return getInputStream(subEntryOutputStream);
         } else {
            throw new IOException("Failed to load " + subEntryName);
         }
      } else if (entryName != null) {
         if (entryOutputStream != null) {
            return getInputStream(entryOutputStream);
         } else {
            throw new IOException("Failed to load " + entryName);
         }
      } else {
         return new JarInputStream(new FileInputStream(file));
      }
   }

   @Override
   public void close() throws IOException {
      if (subEntryOutputStream != null) {
         subEntryOutputStream.reset();
         subEntryOutputStream.close();
         subEntryOutputStream = null;
      }
      if (entryOutputStream != null) {
         entryOutputStream.reset();
         entryOutputStream.close();
         entryOutputStream = null;
      }
   }
}
