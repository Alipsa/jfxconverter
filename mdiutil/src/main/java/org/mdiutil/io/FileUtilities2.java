/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import static org.mdiutil.io.FileUtilities.FIREFOX10_USERAGENT;
import static org.mdiutil.io.FileUtilities.isHTTPProtocol;

/**
 * Various File utilities to deal with HTTP URLs.
 *
 * @version 0.9.34
 */
public class FileUtilities2 {
   private FileUtilities2() {
   }

   /**
    * Return the URL content as a String. Note that:
    * <ul>
    * <li>The method is able to deal with HTTP redirections (codes 301, 302, and 303)</li>
    * <li>This method will return null for a local URL</li>
    * </ul>
    *
    * @param url the URL
    * @return the URL content as a String
    */
   public static String getHTTPContentAsText(URL url) {
      return getHTTPContentAsText(url, null, null, -1);
   }

   /**
    * Return the URL content as a String.
    * <ul>
    * <li>The method is able to deal with HTTP redirections (codes 301, 302, and 303)</li>
    * <li>This method will return null for a local URL</li>
    * </ul>
    *
    * @param url the URL
    * @param timeout the timeout in ms
    * @return the URL content as a String
    */
   public static String getHTTPContentAsText(URL url, int timeout) {
      return getHTTPContentAsText(url, null, null, timeout);
   }

   /**
    * Return the URL content as a String, without a timeout. Note that this method will return null for a local URL.
    * <ul>
    * <li>The method is able to deal with HTTP redirections (codes 301, 302, and 303)</li>
    * <li>This method will return null for a local URL</li>
    * </ul>
    *
    * The complete list of http return codes can be found on wikipedia here:
    * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>.
    *
    * <h1>Use case</h1>
    * This class allow to use HTTP parser libraries such as <a href="https://jsoup.org/">JSoup</a> without having to checking initially the
    * validity of the URL connection, then doing this all over again with the library.
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link FileUtilities#FIREFOX10_USERAGENT} will be used by default
    * @return the URL content as a String
    */
   public static String getHTTPContentAsText(URL url, Proxy proxy, String userAgent) {
      return getHTTPContentAsText(url, proxy, userAgent, -1);
   }

   /**
    * Return the URL content as a String. Note that this method will return null for a local URL.
    * <ul>
    * <li>The method is able to deal with HTTP redirections (codes 301, 302, and 303)</li>
    * <li>This method will return null for a local URL</li>
    * </ul>
    *
    * The complete list of http return codes can be found on wikipedia here:
    * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>.
    *
    * <h1>Use case</h1>
    * This class allow to use HTTP parser libraries such as <a href="https://jsoup.org/">JSoup</a> without having to checking initially the
    * validity of the URL connection, then doing this all over again with the library.
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link FileUtilities#FIREFOX10_USERAGENT} will be used by default
    * @param timeout the timeout in ms (-1 for no timeOut)
    * @return the URL content as a String
    */
   public static String getHTTPContentAsText(URL url, Proxy proxy, String userAgent, int timeout) {
      if (isHTTPProtocol(url)) {
         try {
            if (userAgent == null) {
               userAgent = FIREFOX10_USERAGENT;
            }
            URLConnection con;
            if (proxy != null) {
               con = url.openConnection(proxy);
            } else {
               con = url.openConnection();
            }
            if (con instanceof HttpURLConnection) {
               HttpURLConnection huc = (HttpURLConnection) con;
               huc.setRequestProperty("User-Agent", userAgent);
               if (timeout > 0) {
                  huc.setConnectTimeout(timeout);
               }
               huc.setRequestMethod("GET");
               huc.setRequestProperty("Accept-Charset", "UTF-8");
               huc.setInstanceFollowRedirects(true);
               HttpURLConnection.setFollowRedirects(true);
               try {
                  huc.connect();
                  int code = huc.getResponseCode();
                  if (code == HttpURLConnection.HTTP_OK) {
                     return getString(huc);
                  } else if (code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM
                     || code == HttpURLConnection.HTTP_SEE_OTHER) {
                     URL newURL = new URL(huc.getHeaderField("Location"));
                     huc = (HttpURLConnection) newURL.openConnection();
                     huc.setRequestProperty("User-Agent", userAgent);
                     return getString(huc);
                  } else {
                     return null;
                  }
               } catch (UnknownHostException e) {
                  return null;
               }
            } else {
               return null;
            }
         } catch (IOException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String getString(HttpURLConnection huc) {
      try {
         StringBuilder buf;
         try (InputStream stream = huc.getInputStream()) {
            buf = new StringBuilder();
            boolean start = false;
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            while (true) {
               String line = in.readLine();
               if (line == null) {
                  break;
               } else if (start) {
                  buf.append('\n');
               } else {
                  start = true;
               }
               buf.append(line);
            }
         }
         return buf.toString();
      } catch (IOException ex) {
         return null;
      }
   }

   /**
    * Return the length of a URL, accessed overa Proxy. Note that this method will work even if the URL is not a local file.
    *
    * @param url the URL
    * @param proxy the proxy
    * @return the URL length
    */
   public static long getLength(URL url, Proxy proxy) throws IOException {
      if (isHTTPProtocol(url)) {
         URLConnection connection = url.openConnection(proxy);
         return connection.getContentLengthLong();
      } else {
         File file = new File(url.getFile());
         return file.length();
      }
   }

   /**
    * Return the length of a URL. Note that this method will work even if the URL is not a local file.
    *
    * @param url the URL
    * @return the URL length
    */
   public static long getLength(URL url) throws IOException {
      if (isHTTPProtocol(url)) {
         URLConnection connection = url.openConnection();
         long length = connection.getContentLengthLong();
         if (length > -1) {
            return length;
         }
         // Header lacks content-length, read the entire stream
         final int chunkSize = 1024;
         byte[] buffer = new byte[chunkSize];
         int chunkBytesRead = 0;
         length = 0;
         InputStream inputStream = connection.getInputStream();
         while((chunkBytesRead = inputStream.read(buffer)) != -1) {
            length += chunkBytesRead;
         }
         return length;

      } else {
         File file = new File(url.getFile());
         return file.length();
      }
   }
}
