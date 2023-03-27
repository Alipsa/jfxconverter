/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.net;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is able to construct a nestable URL with arbitrary levels of nested zip elements.
 *
 * @version 1.2.26
 */
public class NestableURLConstructor {
   private final URL url;
   private NestedEntry firstEntry = null;
   private NestedEntry lastEntry = null;
   private boolean useZipProtocol = true;

   /**
    * Constructor. The wrapper protocol will be "zip:" by default.
    *
    * @param url the URL
    */
   public NestableURLConstructor(URL url) {
      this(url, true);
   }

   /**
    * Constructor.
    *
    * @param url the URL
    * @param useZipProtocol true if the "zip:" protocol must be used, else the "jar:" ptrotocol will be used
    */
   public NestableURLConstructor(URL url, boolean useZipProtocol) {
      this.url = url;
      this.useZipProtocol = useZipProtocol;
   }

   /**
    * Constructor. The wrapper protocol will be "zip:" by default.
    *
    * @param file the file
    * @throws MalformedURLException for a malformed URL exception
    */
   public NestableURLConstructor(File file) throws MalformedURLException {
      this(file, true);
   }

   /**
    * Constructor.
    *
    * @param file the file
    * @param useZipProtocol true if the "zip:" protocol must be used, else the "jar:" ptrotocol will be used
    * @throws MalformedURLException for a malformed URL exception
    */
   public NestableURLConstructor(File file, boolean useZipProtocol) throws MalformedURLException {
      this.url = file.toURI().toURL();
      this.useZipProtocol = useZipProtocol;
   }

   /**
    * Add a nested entry. The last one will be a termial entry.
    *
    * @param entry the entry path from its parent
    */
   public void addNestedEntry(String entry) {
      NestedEntry theEntry = new NestedEntry(entry);
      if (firstEntry == null) {
         firstEntry = theEntry;
      } else {
         lastEntry.child = theEntry;
      }
      lastEntry = theEntry;
   }

   private String getProtocol() {
      String protocol = useZipProtocol ? "zip:" : "jar:";
      StringBuilder buf = new StringBuilder();
      buf.append(protocol);
      NestedEntry current = firstEntry;
      while (true) {
         if (current.child != null) {
            buf.append(protocol);
            current = current.child;
         } else {
            break;
         }
      }
      return buf.toString();
   }

   /**
    * Count the number of levels of entries.
    *
    * @return the number of levels of entries
    */
   public int countEntryLevels() {
      int levels = 0;
      NestedEntry current = firstEntry;
      while (true) {
         if (current.child != null) {
            levels++;
            current = current.child;
         } else {
            break;
         }
      }
      return levels;
   }

   /**
    * Return the URL.
    *
    * Note that to be able to use this method, you will need to install the
    * {@link ZipStreamHandlerFactory} if you did not set the <code>useZipProtocol</code> argument
    * to false, because by default Java does not accept to handle zip protocols.
    *
    * If you don't do this prior to using this method, you will encounter an exception because zip
    * protocols are not handled by default in the JDK.
    *
    * @return the URL
    * @throws MalformedURLException
    */
   public URL getURL() throws MalformedURLException {
      String spec = getURLSpec();
      return new URL(spec);
   }

   /**
    * Return the URL specification. To create an URL from this spec, just do:
    * <pre>
    * URL url = new URL(spec);
    * </pre>
    *
    * @return the URL specification
    * @throws MalformedURLException
    */
   public String getURLSpec() throws MalformedURLException {
      if (firstEntry == null) {
         throw new MalformedURLException("No entry defined");
      }
      StringBuilder buf = new StringBuilder();
      String protocol = getProtocol();
      buf.append(protocol);
      buf.append(url.toString()).append("!/");
      NestedEntry current = firstEntry;
      while (true) {
         if (current.child != null) {
            buf.append(current.entry).append("!/");
            current = current.child;
         } else {
            buf.append(current.entry);
            break;
         }
      }
      return buf.toString();
   }

   /**
    * A nested entry.
    *
    * @version 1.2.26
    */
   private class NestedEntry {
      private final String entry;
      private NestedEntry child = null;

      private NestedEntry(String entry) {
         this.entry = entry;
      }
   }
}
