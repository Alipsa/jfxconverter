/*------------------------------------------------------------------------------
 * Copyright (C) 2010, 2011, 2018, 2020, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.mdiutil.io.FileUtilities;

/**
 * An URLConnection that can access URLs in nested archives (for example a resource in a jar nested in another jar). Note that:
 * <ul>
 * <li>this class does not allow to get an OutputStream to write in the archive. The {@link #getOutputStream()} will
 * invariably return null</li>
 * <li>For an URL which does not correspond to a nested archive, the regular Java {@link #getInputStream()} method
 * will be used internally, so this class may be used even for not nested URLs</li>
 * </ul>
 *
 * Note that to be able to use this connection with nested archives, you will need to install the
 * {@link ZipStreamHandlerFactory}, because by default Java does not accept to handle nested jar protocols (and the jar
 * protocol is used even for zip files).
 *
 * @version 1.2.26
 */
public class NestableURLConnection extends URLConnection {
   protected String urlPath;
   protected boolean firstEntry = false;
   private boolean acceptAltProtocols = false;

   /**
    * Constructs a new connection for the URL.
    *
    * @param url the URL of this connection.
    */
   public NestableURLConnection(URL url) {
      super(url);
      urlPath = url.toString();
   }

   /**
    * Constructs a new connection.
    */
   public NestableURLConnection() {
      super(null);
   }

   /**
    * Set if the connection accept alternate protocols, in addition to zip. For the moment, the only
    * alternate protocol which is handled is gzip. The connection will consider that an
    * entry with the ".gz" extension is a gzip file.
    *
    * @param acceptAltProtocols true if the connection accept alternate protocols
    */
   public void setAcceptAlternateProtocols(boolean acceptAltProtocols) {
      this.acceptAltProtocols = acceptAltProtocols;
   }

   /**
    * Return true if the connection accept alternate protocols, in addition to zip.
    *
    * @return true if the connection accept alternate protocols
    */
   public boolean isAcceptingAlternateProtocols() {
      return acceptAltProtocols;
   }

   /**
    * Set the connection URL.
    *
    * @param url the url
    */
   public void setURL(URL url) {
      this.url = url;
      urlPath = url.toString();
   }

   /**
    * Constructs a new connection for the URL.
    *
    * @param url the URL of this connection.
    * @param firstEntry true for the first entry
    */
   public NestableURLConnection(URL url, boolean firstEntry) {
      this(url);
      this.firstEntry = firstEntry;
   }

   /**
    * Advertise that this connection is connected. The {@link #connected} field will be set to true.
    *
    * @exception IOException if an I/O error occurs while opening the connection
    */
   @Override
   public void connect() throws IOException {
      connected = true;
   }

   private String getNestedURL() throws IOException {
      int sep = urlPath.indexOf("!/");
      int start = urlPath.indexOf(':') + 1;

      for (int i = start, end = urlPath.indexOf("/") - 1; (i = urlPath.indexOf(":", i)) < end;) {
         int cursep = urlPath.indexOf("!/", sep + 2);
         if (cursep < 0) {
            break;
         }
         sep = cursep;
         ++i;
      }

      return urlPath.substring(start, sep);
   }

   private boolean isGzipEntry(ZipEntry entry) {
      return entry.getName().toLowerCase().endsWith(".gz");
   }

   /**
    * Creates the input stream for the URL. Note that for an URL which does not correspond to a nested archive, the regular
    * Java {@link #getInputStream()} method will be used internally, so this method may be used even for not nested URLs.
    *
    * @return the input stream for the URL.
    * @exception IOException if an I/O error occurs while creating the input stream
    */
   @Override
   public InputStream getInputStream() throws IOException {
      int start = urlPath.indexOf(':') + 1;
      if (start > urlPath.length() || urlPath.charAt(start) == '/') {
         return url.openStream();
      }

      if (FileUtilities.useFileOrHTTPProtocol(urlPath)) {
         return url.openStream();
      }

      int sep = urlPath.indexOf("!/");
      if (sep < 0) {
         throw new MalformedURLException("Missing separator " + urlPath);
      }

      String nestedURL = getNestedURL();

      sep = urlPath.indexOf(nestedURL) + nestedURL.length();
      int nextSep = urlPath.indexOf("!/", sep + 2);

      // Use the default Java openStream() for a file scheme.
      InputStream inputStream;
      ZipEntry inputZipEntry;
      boolean isKnownProtocol = FileUtilities.useFileOrHTTPProtocol(nestedURL);
      if (!isKnownProtocol || (firstEntry)) {
         if (firstEntry) {
            nestedURL = urlPath;
         } else {
            if (!nestedURL.contains("!/")) {
               nestedURL = "file:" + nestedURL.substring(4);
            }
         }
         inputStream = createInputStream(nestedURL);
      } else {
         String entry = nextSep < 0 ? urlPath.substring(sep + 2) : urlPath.substring(sep + 2, nextSep);

         sep = nextSep;
         nextSep = urlPath.indexOf("!/", sep + 2);

         // Go directly to the right entry in the zip file
         final ZipFile zipFile = new ZipFile(FileUtilities.replaceEscapedSequences(nestedURL.substring(5)));
         inputZipEntry = zipFile.getEntry(entry);
         InputStream zipEntryInputStream = inputZipEntry == null ? null : zipFile.getInputStream(inputZipEntry);
         if (zipEntryInputStream == null) {
            zipFile.close();
            // return null instead of throwing an Exception if the URL does not exist
            return null;
         }
         boolean isGzipEntry = acceptAltProtocols && isGzipEntry(inputZipEntry);
         if (isGzipEntry) {
            zipEntryInputStream = new GZIPInputStream(zipEntryInputStream);
         }
         inputStream = new FilterInputStream(zipEntryInputStream) {
            @Override
            public void close() throws IOException {
               super.close();
               zipFile.close();
            }
         };
      }

      // Loop over the archive paths.
      LOOP:
      while (sep > 0) {
         // The entry name to be matched.
         String entry = nextSep < 0 ? urlPath.substring(sep + 2) : urlPath.substring(sep + 2, nextSep);

         // Wrap the input stream as a zip stream to scan it's contents for a match.
         // Don't use try-with-resources here because we only want to close it if there is an internal IOException
         ZipInputStream zipInputStream = new ZipInputStream(inputStream);
         while (zipInputStream.available() >= 0) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) {
               break;
            } else if (firstEntry) {
               inputZipEntry = zipEntry;
               inputStream = zipInputStream;
               sep = -1;
               continue LOOP;
            } else if (entry.equals(zipEntry.getName())) {
               inputZipEntry = zipEntry;
               inputStream = zipInputStream;

               // Skip to the next archive path and continue the loop.
               sep = nextSep;
               nextSep = urlPath.indexOf("!/", sep + 2);
               continue LOOP;
            }
         }
         throw new IOException("Archive entry not found " + urlPath);
      }

      return inputStream;
   }

   /**
    * Creates an input stream for the nested URL by calling {@link URL#openStream() opening} a stream on it.
    *
    * @param nestedURL the nested URL for which a stream is required.
    * @return the open stream of the nested URL.
    */
   private InputStream createInputStream(String nestedURL) throws IOException {
      return new URL(nestedURL).openStream();
   }

   /**
    * Return null.
    *
    * @return null
    */
   @Override
   public OutputStream getOutputStream() throws IOException {
      return null;
   }
}
