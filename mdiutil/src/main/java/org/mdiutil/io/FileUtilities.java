/*------------------------------------------------------------------------------
 * Copyright (C) 2008-2013, 2015-2022 Herve Girod
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
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Various File utilities to deal with URLs.
 *
 * @version 1.2.29.3
 */
public class FileUtilities {
   private static final Pattern PAT = Pattern.compile("(.*)/\\w+/\\x2E\\x2E/(.*)");
   private static final Pattern PAT2 = Pattern.compile("(.*)/\\w+/\\x2E\\x2E");
   private static final Pattern PAT_HOST = Pattern.compile("(\\w+)://localhost(.*)");
   private static final Pattern PAT_SUBPROTOCOL = Pattern.compile("(\\w+):.*");
   /**
    * The option used if 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @see #isURLFound(URL, Proxy, String, int, int)
    */
   public static final int OPTION_ACCEPT301CODE = 1;
   /**
    * The option used if redirection links will be followed and used to check that the links exists. For example:
    * <ul>
    * <li>"http://docs.oracle.com/javase/8/docs/api/java/awt/Button.html": this link is found with a 301 code and the
    * redirection links to the same link with an https protocol, it will be considered as existing</li>
    * <li>"http://docs.oracle.com/javase/8/docs/api/java/awt/Button1111.html": this link is found with a 301 code and the
    * redirection links to a special 404 Oracle link, it will be considered as not existing</li>
    * </ul>
    *
    * @see #isURLFound(URL, Proxy, String, int, int)
    */
   public static final int OPTION_FOLLOWREDIRECT = 2;
   /**
    * The default option for the {@link #exist(File, short)} and {@link #exist(URL, short)}.
    */
   public static final short OPTION_FILEEXIST_DEFAULT = 0;
   /**
    * The option for the {@link #exist(File, short)} and {@link #exist(URL, short)} by trying to
    * read the beginning of the file to check the file existence.
    */
   public static final short OPTION_FILEEXIST_READ = 0;
   /**
    * The option for the {@link #exist(File, short)} and {@link #exist(URL, short)} by getting the
    * file length to check the file existence.
    */
   public static final short OPTION_FILEEXIST_LENGTH = 1;
   /**
    * The option for the {@link #exist(File, short)} and {@link #exist(URL, short)} by considering that
    * URLs with queries don't exist.
    */
   public static final short OPTION_FILEEXIST_SKIP_QUERIES = 4;
   /**
    * The option for the {@link #exist(File, short)} and {@link #exist(URL, short)} by considering that
    * URLs which point to php resources don't exist.
    */
   public static final short OPTION_FILEEXIST_SKIP_PHP = 8;
   /**
    * The option for the {@link #exist(File, short)} and {@link #exist(URL, short)} by setting by default
    * that http resources exists.
    */
   public static final short OPTION_FILEEXIST_HTTP_FORCETRUE = 16;
   /**
    * The default option for the {@link #getChildURL(URL, String, int)}.
    */
   public static final short OPTION_CHILDURL_DEFAULT = 0;
   /**
    * The option for the {@link #getChildURL(URL, String, int)} to escape the result.
    */
   public static final short OPTION_CHILDURL_ESCAPE = 1;
   /**
    * The option for the {@link #getChildURL(URL, String, int)} to check if the resulting file exists.
    */
   public static final short OPTION_CHILDURL_FILTEREXIST = 2;
   /**
    * The option for the {@link #getChildURL(URL, String, int)} to check if the resulting file exists only
    * for file protocols.
    */
   public static final short OPTION_CHILDURL_FILTEREXIST_FILE = 4;
   /**
    * The option for the {@link #getChildURL(URL, String, int)} to check if the resulting file exists only
    * for http and https protocols.
    */
   public static final short OPTION_CHILDURL_FILTEREXIST_HTTP = 8;
   private static boolean filePermissionsSet = false;
   private static boolean hasFilePermissions = true;
   private static final Set<String> PROTOCOLS = new HashSet<>();
   private static final Set<String> PROTOCOLS2 = new HashSet<>();
   // used for the https protocol
   private static SSLContext sslContext = null;
   private static AllHostValid allHostValid = null;
   private static ExtSSLSocketFactory sslSocketFactory = null;
   /**
    * The Firefox 10 user agent.
    */
   public static final String FIREFOX10_USERAGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)";
   /**
    * The IE9 user agent.
    */
   public static final String IE9_USERAGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; "
      + ".NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; SiteKiosk 7.8 Build 328)";

   static {
      PROTOCOLS.add("file");
      PROTOCOLS.add("http");
      PROTOCOLS.add("https");
   }

   static {
      PROTOCOLS2.add("file");
      PROTOCOLS2.add("http");
      PROTOCOLS2.add("https");
      PROTOCOLS2.add("jar");
      PROTOCOLS2.add("zip");
      PROTOCOLS2.add("gzip");
   }

   protected FileUtilities() {
   }

   /**
    * Return true if the URL use a file, http, or https protocol.
    *
    * @param url the URL
    * @return true if the URL use a file, http, or https protocol
    */
   public static boolean useFileOrHTTPProtocol(String url) {
      Iterator<String> it = PROTOCOLS.iterator();
      while (it.hasNext()) {
         String prot = it.next();
         if (url.startsWith(prot + ":")) {
            return true;
         }
      }
      return false;
   }

   /**
    * Return true if the URL use a http, or https protocol.
    *
    * @param url the URL
    * @return true if the URL use a http, or https protocol
    */
   public static boolean useHTTPProtocol(String url) {
      return url.startsWith("http:") || url.startsWith("https:");
   }

   /**
    * Return true if the protocol is a file, http, or https protocol.
    *
    * @param url the URL
    * @return true if the protocol is a file, http, or https protocol
    */
   public static boolean isFileOrHTTPProtocol(URL url) {
      return PROTOCOLS.contains(url.getProtocol());
   }

   /**
    * Return true if the protocol is a https protocol.
    *
    * @param url the URL
    * @return true if the protocol is a https protocol.
    */
   public static boolean isHTTPSProtocol(URL url) {
      String protocol = url.getProtocol();
      if (protocol.equals("https")) {
         return true;
      } else if (protocol.equals("jar") || protocol.equals("zip")) {
         String path = url.getPath();
         try {
            URL nestedURL = new URL(path);
            protocol = nestedURL.getProtocol();
            return protocol.equals("https");
         } catch (MalformedURLException ex) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return true if the protocol is a http, or https protocol.
    *
    * @param url the URL
    * @return true if the protocol is a http, or https protocol.
    */
   public static boolean isHTTPProtocol(URL url) {
      if (url == null) {
         return false;
      }
      String protocol = url.getProtocol();
      if (protocol.equals("http") || protocol.equals("https")) {
         return true;
      } else if (protocol.equals("jar") || protocol.equals("zip")) {
         String path = url.getPath();
         try {
            URL nestedURL = new URL(path);
            protocol = nestedURL.getProtocol();
            return protocol.equals("http") || protocol.equals("https");
         } catch (MalformedURLException ex) {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return true if the protocol is a https protocol.
    *
    * @param path the path
    * @return true if the protocol is a https protocol
    */
   public static boolean isHTTPSProtocol(String path) {
      try {
         URL url = new URL(path);
         String protocol = url.getProtocol();
         if (protocol.equals("https")) {
            return true;
         } else if (protocol.equals("jar") || protocol.equals("zip")) {
            url.getPath();
            try {
               URL nestedURL = new URL(path);
               protocol = nestedURL.getProtocol();
               return protocol.equals("https");
            } catch (MalformedURLException ex) {
               return false;
            }
         } else {
            return false;
         }
      } catch (MalformedURLException ex) {
         return false;
      }
   }

   /**
    * Return true if the protocol is a http, or https protocol.
    *
    * @param path the path
    * @return true if the protocol is a http, or https protocol
    */
   public static boolean isHTTPProtocol(String path) {
      try {
         URL url = new URL(path);
         String protocol = url.getProtocol();
         if (protocol.equals("http") || protocol.equals("https")) {
            return true;
         } else if (protocol.equals("jar") || protocol.equals("zip")) {
            url.getPath();
            try {
               URL nestedURL = new URL(path);
               protocol = nestedURL.getProtocol();
               return protocol.equals("http") || protocol.equals("https");
            } catch (MalformedURLException ex) {
               return false;
            }
         } else {
            return false;
         }
      } catch (MalformedURLException ex) {
         return false;
      }
   }

   /**
    * Return true if the application has the right to get Files in the local network.
    *
    * @return true if the application has the right to get Files in the local network
    */
   public static boolean checkFilePermissions() {
      if (!filePermissionsSet) {
         try {
            // only do this to see if we trigger a Security Exception
            System.getProperty("user.dir");
         } catch (SecurityException e) {
            hasFilePermissions = false;
         }
         filePermissionsSet = true;
      }
      return hasFilePermissions;
   }

   /**
    * Return true if the URL or the resource denoted by this URL exist. Remark that this method will return false if the URL is null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * The method used to check the URL availability defer to {@link #exist(URL, short)} with the {@link #OPTION_FILEEXIST_READ}
    * option.
    *
    * @param url the URL
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(URL url) {
      return exist(url, OPTION_FILEEXIST_READ);
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * @param url the URL
    * @param option the option to detected if the file exist. The following optinos are available:
    * <ul>
    * <li>{@link #OPTION_FILEEXIST_READ}: by trying to read the beginning of the file</li>
    * <li>{@link #OPTION_FILEEXIST_LENGTH}: by getting a not null length for the file</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_HTTP_FORCETRUE}: by considering that URL which point to http or https resources
    * exist (if queries or php resources areot skipped)</li>
    * </ul>
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(URL url, short option) {
      return exist(url, (int) option);
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * @param url the URL
    * @param option the option to detected if the file exist. The following optinos are available:
    * <ul>
    * <li>{@link #OPTION_FILEEXIST_READ}: by trying to read the beginning of the file</li>
    * <li>{@link #OPTION_FILEEXIST_LENGTH}: by getting a not null length for the file</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_HTTP_FORCETRUE}: by considering that URL which point to http or https resources
    * exist (if queries or php resources areot skipped)</li>
    * </ul>
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(URL url, int option) {
      if (url == null) {
         return false;
      } else {
         if ((option & OPTION_FILEEXIST_SKIP_QUERIES) == OPTION_FILEEXIST_SKIP_QUERIES) {
            if (url.getQuery() != null) {
               return false;
            }
         }
         if ((option & OPTION_FILEEXIST_SKIP_PHP) == OPTION_FILEEXIST_SKIP_PHP) {
            String path = url.getPath();
            if (path.toLowerCase().endsWith(".php")) {
               return false;
            }
         }
         if ((option & OPTION_FILEEXIST_HTTP_FORCETRUE) == OPTION_FILEEXIST_HTTP_FORCETRUE) {
            String protocol = url.getProtocol();
            if (protocol.equals("http") || protocol.equals("https")) {
               return true;
            }
         }
         if ((option & OPTION_FILEEXIST_LENGTH) == OPTION_FILEEXIST_LENGTH) {
            String path = url.getFile();
            if (path.startsWith("file:")) {
               path = path.substring(5);
            }
            int entryPos = path.indexOf('!');
            if (entryPos == -1) {
               File file = new File(path);
               return file.length() != 0;
            } else {
               path = path.substring(0, entryPos);
               File file = new File(path);
               return file.length() != 0;
            }
         } else {
            String path = url.getFile();
            try {
               if (path.startsWith("file:")) {
                  path = path.substring(5);
               }
               int entryPos = path.indexOf('!');
               if (entryPos != -1) {
                  path = path.substring(0, entryPos);
                  url = new URL("file", "", -1, path);
               }
               InputStream stream = url.openStream();
               stream.close();
               return true;
            } catch (IOException e) {
               return false;
            }
         }
      }
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is
    * null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * The method used to check the file availability defer to {@link #exist(File, short)} with the {@link #OPTION_FILEEXIST_READ}
    * option.
    *
    * @param file the file
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(File file) {
      return exist(file, OPTION_FILEEXIST_READ);
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is
    * null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * @param file the file
    * @param option the option to detected if the file exist. The following options are available:
    * <ul>
    * <li>{@link #OPTION_FILEEXIST_READ}: by trying to read the beginning of the file</li>
    * <li>{@link #OPTION_FILEEXIST_LENGTH}: by getting a not null length for the file</li>
    * </ul>
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(File file, short option) {
      return exist(file, (int) option);
   }

   /**
    * Return true if the file or the resource denoted by this URL exist. Remark that this method will return false if the URL is
    * null.
    * Note that this method does not use the File.exist() method, so it will work on sandboxed environments like Applets.
    *
    * @param file the file
    * @param option the option to detected if the file exist. The following options are available:
    * <ul>
    * <li>{@link #OPTION_FILEEXIST_READ}: by trying to read the beginning of the file</li>
    * <li>{@link #OPTION_FILEEXIST_LENGTH}: by getting a not null length for the file</li>
    * </ul>
    * @return true if the file or the resource denoted by this URL exist
    */
   public static boolean exist(File file, int option) {
      if (file == null) {
         return false;
      } else {
         if ((option & OPTION_FILEEXIST_LENGTH) == OPTION_FILEEXIST_LENGTH) {
            return file.length() != 0;
         } else {
            try {
               URL url = getProtectedURL(file);
               if (url == null) {
                  return false;
               } else {

                  try {
                     InputStream stream = url.openStream();
                     stream.close();
                     return true;
                  } catch (IOException e) {
                     return false;
                  }
               }
            } catch (MalformedURLException e) {
               return false;
            }
         }
      }
   }

   /**
    * Return an URL corresponding to an entry in a Jar file.
    *
    * @param url the parent URL
    * @param entryPath the path of the child relative to the parent
    *
    * @return the child URL
    * @throws MalformedURLException if the resulting URL is invalid, or the protocol is not file:
    */
   public static URL getJarEntryURL(URL url, String entryPath) throws MalformedURLException {
      String protocol = url.getProtocol();
      if (!protocol.equals("file")) {
         throw new MalformedURLException("The Protocol must be file:");
      }
      String file = url.getFile();
      String host = url.getHost();
      int port = url.getPort();
      protocol = "jar";
      file = "file:" + file + "!/" + entryPath;
      return new URL(protocol, host, port, file);
   }

   /**
    * Return an URL corresponding to an entry in a Jar file.
    *
    * @param url the parent URL
    * @param entryPath the path of the child relative to the parent
    * @param handler the URLStreamHandler
    *
    * @return the child URL
    * @throws MalformedURLException if the resulting URL is invalid, or the protocol is not file:
    */
   public static URL getJarEntryURL(URL url, String entryPath, URLStreamHandler handler) throws MalformedURLException {
      String protocol = url.getProtocol();
      if (!protocol.equals("file")) {
         throw new MalformedURLException("The Protocol must be file:");
      }
      String file = url.getFile();
      String host = url.getHost();
      int port = url.getPort();
      protocol = "jar";
      file = "file:" + file + "!/" + entryPath;
      return new URL(protocol, host, port, file, handler);
   }

   /**
    * Return an URL by its relative path from a parent URL. Special characters will be escaped.
    *
    * @param parentURL the parent URL
    * @param path the path of the child relative to the parent
    * @param options the options. The following options are available:
    * <ul>
    * <li>{@link #OPTION_CHILDURL_ESCAPE}: if space characters will be escaped</li>
    * <li>{@link #OPTION_CHILDURL_FILTEREXIST}: if not existing resources will be filtered</li>
    * <li>{@link #OPTION_CHILDURL_FILTEREXIST_FILE}: if not existing non http resources will be filtered</li>
    * <li>{@link #OPTION_CHILDURL_FILTEREXIST_HTTP}: if not existing http resources will be filtered</li>
    * </ul>
    *
    * @return the child URL
    */
   public static URL getChildURL(URL parentURL, String path, int options) {
      boolean escape = (options & OPTION_CHILDURL_ESCAPE) == OPTION_CHILDURL_ESCAPE;
      boolean filterExistingURLs = (options & OPTION_CHILDURL_FILTEREXIST) == OPTION_CHILDURL_FILTEREXIST;
      boolean filterExistingURLsFile;
      boolean filterExistingURLsHttp;
      if (filterExistingURLs) {
         filterExistingURLsFile = true;
         filterExistingURLsHttp = true;
      } else {
         filterExistingURLsFile = (options & OPTION_CHILDURL_FILTEREXIST_FILE) == OPTION_CHILDURL_FILTEREXIST_FILE;
         filterExistingURLsHttp = (options & OPTION_CHILDURL_FILTEREXIST_HTTP) == OPTION_CHILDURL_FILTEREXIST_HTTP;
      }
      try {
         if (escape) {
            path = replaceEscapedSequences(path, escape);
         }
         path = path.replace("\\", "/");
         boolean isAbsolutePath = isAbsolute(path);
         if (isAbsolutePath) {
            URL url;
            if (useHTTPProtocol(path)) {
               url = new URL(path);
            } else {
               File pathFile = new File(path);
               url = pathFile.toURI().toURL();
            }
            return filterExistingURL(url, filterExistingURLsFile, filterExistingURLsHttp);
         } else if ((parentURL != null) && (parentURL.getFile() != null)) {
            String protocol = parentURL.getProtocol();
            String host = parentURL.getHost();
            int port = parentURL.getPort();
            String file = parentURL.getFile();
            file = file.replace("\\", "/");
            if ((protocol.equals("jar") || protocol.equals("zip")) && file.startsWith("file:")) {
               String childFile = file + "/" + path;
               URL childURL = new URL(protocol, host, port, childFile);
               return filterExistingURL(childURL, filterExistingURLsFile, filterExistingURLsHttp);
            }
            String childFile = null;
            URL childURL = null;

            if (!isFileOrHTTPProtocol(parentURL)) {
               childFile = new File(new File(file), path).getPath();
               if (escape) {
                  childFile = replaceEscapedSequences(childFile, true);
               }
               childFile = childFile.replace('\\', '\u002F').replace("\u002F.\u002F", "\u002F");
               if (childFile.startsWith("//")) {
                  childFile = childFile.substring(2);
               }
               childFile = getCorrectedPath(childFile);
            } else if (isHTTPProtocol(parentURL)) {
               if (path.startsWith("/")) {
                  path = path.replace('\\', '\u002F').replace("\u002F.\u002F", "\u002F");
               } else {
                  path = "/" + path.replace('\\', '\u002F').replace("\u002F.\u002F", "\u002F");
               }
               childURL = new URL(protocol, host, port, file + path);
            } else {
               childFile = new File(new File(file), path).getPath();
               if (escape) {
                  childFile = replaceEscapedSequences(childFile, true);
               }
               childFile = childFile.replace('\\', '\u002F').replace("\u002F.\u002F", "\u002F");
               childFile = getCorrectedPath(childFile);
            }
            if (childFile != null) {
               if (childFile.endsWith(".jar") || childFile.endsWith(".apk") || childFile.endsWith(".dex")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "jar:" + childFile.substring(5);
                  }
               } else if (childFile.endsWith(".zip")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "zip:" + childFile.substring(5);
                  }
               } else if (childFile.endsWith(".gzip")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "gzip:" + childFile.substring(5);
                  }
               }

               if (escape) {
                  childFile = encodeURL(childFile);
               }
               childURL = new URL(protocol, host, port, childFile);
            }
            return filterExistingURL(childURL, filterExistingURLsFile, filterExistingURLsHttp);
         } else {
            return null;
         }
      } catch (MalformedURLException ex) {
         ex.printStackTrace();
         return null;
      }
   }

   /**
    * Return an URL by its relative path from a parent URL. Special characters will be escaped.
    *
    * @param parentURL the parent URL
    * @param path the path of the child relative to the parent
    *
    * @return the child URL
    */
   public static URL getChildURL(URL parentURL, String path) {
      return getChildURL(parentURL, path, false);
   }

   private static String getCorrectedPath(String path) {
      Matcher m = PAT_SUBPROTOCOL.matcher(path);
      if (path.charAt(0) != '/') {
         if (!m.matches()) {
            path = "/" + path;
         } else if (m.matches()) {
            String group = m.group(1);
            if (!PROTOCOLS2.contains(group)) {
               path = "/" + path;
            }
         }
      }
      return path;
   }

   /**
    * Return an URL by its relative path from a parent URL. Spaces in paths will be escaped by a "%20" String.
    *
    * @param parentURL the parent URL
    * @param path the path of the child relative to the parent
    * @param filterExistingURLs if true, return true if the resulting URL does not exist
    *
    * @return the child URL
    */
   public static URL getChildURL(URL parentURL, String path, boolean filterExistingURLs) {
      return getChildURL(parentURL, path, filterExistingURLs, true);
   }

   /**
    * Return an URL by its relative path from a parent URL.
    *
    * @param parentURL the parent URL
    * @param path the path of the child relative to the parent
    * @param filterExistingURLs if true, return true if the resulting URL does not exist
    * @param escape true if spaces in file paths must be escaped by a "%20" String
    *
    * @return the child URL
    */
   public static URL getChildURL(URL parentURL, String path, boolean filterExistingURLs, boolean escape) {
      int option = 0;
      if (escape) {
         option += OPTION_CHILDURL_ESCAPE;
      }
      if (filterExistingURLs) {
         option += OPTION_CHILDURL_FILTEREXIST;
      }
      return getChildURL(parentURL, path, option);
   }

   private static URL filterExistingURL(URL url, boolean filterExistingURLsFile, boolean filterExistingURLsFileHttp) {
      if (filterExistingURLsFile || filterExistingURLsFileHttp) {
         int option = 0;
         if (!filterExistingURLsFileHttp) {
            option += OPTION_FILEEXIST_HTTP_FORCETRUE;
         }
         if (exist(url, option)) {
            return url;
         } else {
            System.err.println("URL " + url.getPath() + " does not exist, skipped");
            return null;
         }
      } else {
         return url;
      }
   }

   /**
    * Return the parent URL of a given URL.
    *
    * @param url the URL
    *
    * @return the parent URL
    */
   public static URL getParentURL(URL url) {
      return getParentURL(url, false);
   }

   /**
    * Return the parent URL of a given URL.
    *
    * @param url the URL
    * @param endSeparator true if an end separator ("/") must be added at the end of the parent path
    *
    * @return the parent URL
    */
   public static URL getParentURL(URL url, boolean endSeparator) {
      try {
         if (url.getFile() != null) {
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String file = url.getFile();
            file = replaceEscapedSequences(file, true);
            File parentFile = new File(file).getParentFile();
            if (parentFile != null) {
               String parentFilePath = new File(file).getParentFile().getPath();
               parentFilePath = parentFilePath.replace('\\', '\u002F');
               parentFilePath = getCorrectedPath(parentFilePath);
               if (endSeparator && !parentFilePath.endsWith("/")) {
                  parentFilePath += "/";
               }
               URL parentURL = new URL(protocol, host, port, parentFilePath);
               return parentURL;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (MalformedURLException ex) {
         ex.printStackTrace();
         return null;
      }
   }

   /**
    * Normalize an URL by removing "." and ".." in the path part of the URL. Note that in some cases the
    * <code>URI.normalize()</code> method does not normalize some URIs. This method will never throw a
    * {@link MalformedURLException} but instead return the initial URL in this case.
    *
    * @param url the URL
    * @return the normalized URL
    */
   public static URL normalize(URL url) {
      String protocol = url.getProtocol();
      String host = url.getHost();
      int port = url.getPort();
      String file = url.getFile();
      String newFile;
      List<String> parts = new ArrayList<>();
      StringTokenizer tok = new StringTokenizer(file, "/");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         if (!tk.isEmpty()) {
            if (tk.equals("..")) {
               if (parts.size() > 0) {
                  parts.remove(parts.size() - 1);
               }
            } else if (!tk.equals(".")) {
               parts.add(tk);
            }
         }
      }
      StringBuilder buf = new StringBuilder();

      String firstLetter = file.substring(0, 1);
      if (firstLetter.equals("/")) {
         buf.append("/");
      }

      Iterator<String> it = parts.iterator();
      while (it.hasNext()) {
         String part = it.next();
         buf.append(part);
         if (it.hasNext()) {
            buf.append("/");
         }
      }
      String ref = url.getRef();
      if (ref != null) {
         buf.append("#").append(ref);
      }
      newFile = buf.toString();
      try {
         URL newURL = new URL(protocol, host, port, newFile);
         return newURL;
      } catch (MalformedURLException ex) {
         return url;
      }
   }

   /**
    * Get the directory whose name is defined by path. This method will return null if one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path</li>
    * </ul>.
    * If path is not a directory, the parent file will be returned.
    *
    * @param path the path
    * @return the directory whose name is defined by path
    */
   public static File getDirectory(String path) {
      if (path == null) {
         return null;
      } else {
         File file = new File(path);
         if (file.exists()) {
            if (file.isDirectory()) {
               return file;
            } else {
               return file.getParentFile();
            }
         } else {
            return null;
         }
      }
   }

   /**
    * Return the body name of a File (before the extension if there is an extension, else this is the name of the file itself).
    *
    * @param file the File
    * @return the body name of the File
    */
   public static String getFileNameBody(File file) {
      String name = file.getName();
      if (name.lastIndexOf('.') == -1) {
         return name;
      } else {
         return name.substring(0, name.lastIndexOf('.'));
      }
   }

   /**
    * Return the body name of a URL (before the extension if there is an extension, else this is the name of the file itself).
    *
    * @param url the URL
    * @return the body name of the URL
    */
   public static String getFileNameBody(URL url) {
      String name = replaceEscapedSequences(url.getPath(), false);
      if (name.startsWith("/")) {
         name = name.substring(1);
      }
      if (name.lastIndexOf('.') == -1) {
         return name;
      } else {
         return name.substring(0, name.lastIndexOf('.'));
      }
   }

   /**
    * Return the file name without the path, nor the extension. For example the File "C:/myDir/myFile.xml" will return myFile.
    *
    * @param file the File
    * @return the file name without the path, nor the extension
    */
   public static String getFileName(File file) {
      String body = getFileNameBody(file);
      if (body.lastIndexOf('/') == -1) {
         return body;
      } else {
         return body.substring(body.lastIndexOf('/') + 1);
      }
   }

   /**
    * Filter a path name for an URL, replacing spaces characters by their percent encoding equivalent.
    *
    * @param url the url
    * @return the path after replacing spaces characters by their percent encoding equivalent
    */
   public static URL fixURLPath(URL url) {
      String content = url.toString();
      if (content.contains(" ")) {
         content = content.replace(" ", "%20");
         try {
            url = new URL(content);
         } catch (MalformedURLException ex) {
         }
      }
      return url;
   }

   /**
    * Filter a path name for an URL, replacing spaces characters by their percent encoding equivalent.
    *
    * @param path the path
    * @return the path after replacing spaces characters by their percent encoding equivalent
    */
   public static String encodeURL(String path) {
      if (!path.contains(" ")) {
         return path;
      } else {
         StringBuilder buf = new StringBuilder();
         int offset = 0;
         while (true) {
            int index = path.indexOf(' ', offset);
            if (index != -1) {
               buf.append(path.substring(offset, index));
               buf.append("%20");
               offset = index + 1;
            } else {
               buf.append(path.substring(offset));
               break;
            }
         }
         return buf.toString();
      }
   }

   /**
    * Replace the percent-encoding escape sequences in a URL path by their equivalent characters.
    *
    * @param path the path
    * @param keepEscapedSpaces allow to keep the escaping of spaces (%20)
    * @return the path after replacing the percent-encoding escape sequences by their equivalent characters
    */
   public static String replaceEscapedSequences(String path, boolean keepEscapedSpaces) {
      if (!path.contains("%")) {
         return path;
      } else {
         StringBuilder buf = new StringBuilder();
         int offset = 0;
         while (true) {
            int index = path.indexOf('%', offset);
            if (index != -1) {
               buf.append(path.substring(offset, index));
               String word = path.substring(index + 1, index + 3);
               if (keepEscapedSpaces && word.equals("20")) {
                  buf.append("%20");
               } else {
                  char c = (char) (int) Integer.decode("0x" + path.substring(index + 1, index + 3));
                  buf.append(c);
               }
               offset = index + 3;
            } else {
               buf.append(path.substring(offset));
               break;
            }
         }
         return buf.toString();
      }
   }

   /**
    * Replace the percent-encoding escape sequences in a URL path by their equivalent characters.
    *
    * @param path the path
    * @return the path after replacing the percent-encoding escape sequences by their equivalent characters
    */
   public static String replaceEscapedSequences(String path) {
      return replaceEscapedSequences(path, true);
   }

   /**
    * Return the file name without the path, but keeping the extension. For example the File "C:/myDir/myFile.xml" will return myFile.xml.
    *
    * @param file the File
    * @return the file name without the path, but keeping the extension
    */
   public static String getFileNameWithExtension(File file) {
      String name = file.getName();
      if (name.lastIndexOf('/') == -1) {
         return name;
      } else {
         return name.substring(name.lastIndexOf('/') + 1);
      }
   }

   /**
    * Return the URL name without the path, but keeping the extension.
    * For example the URL "file:///C:/myDir/myFile.xml" will return myFile.xml.
    *
    * @param url the URL
    * @return the URL name without the whole body
    */
   public static String getFileNameWithExtension(URL url) {
      String name = replaceEscapedSequences(url.getPath(), false);
      if (name.lastIndexOf('/') == -1) {
         return name;
      } else {
         return name.substring(name.lastIndexOf('/') + 1);
      }
   }

   /**
    * Return the URL name without the path, nor the extension. For example the URL "file:///C:/myDir/myFile.xml" will return myFile.
    *
    * @param url the URL
    * @return the URL name without the extension
    */
   public static String getFileName(URL url) {
      String body = getFileNameBody(url);
      if (body.lastIndexOf('/') == -1) {
         return body;
      } else {
         return body.substring(body.lastIndexOf('/') + 1);
      }
   }

   /**
    * Return the extension of a File.
    *
    * @param file the File
    * @return the File extension of the file, or null if the file does not have an extension
    */
   public static String getFileExtension(File file) {
      String name = file.getName();
      if (name.lastIndexOf('.') == -1) {
         return null;
      } else {
         return name.substring(name.lastIndexOf('.') + 1);
      }
   }

   /**
    * Return the extension of a URL.
    *
    * @param url the URL
    * @return the File extension of the URL, or null if the URL does not have an extension
    */
   public static String getFileExtension(URL url) {
      String name = url.getPath();
      if (name.lastIndexOf('.') == -1) {
         return null;
      } else {
         return name.substring(name.lastIndexOf('.') + 1);
      }
   }

   /**
    * Return a File for an URL, replacing percent encoding characters by their equivalent characters.
    *
    * @param url the URL
    * @return the associated file, or null if the syntax of the associated URI is incorrect, or if the file does not exist
    */
   public static File getFile(URL url) {
      try {
         if (url != null) {
            URI uri = url.toURI();
            String path = uri.getPath();
            if (path != null) {
               return new File(uri.getPath());
            } else {
               // falback
               String file = url.getPath();
               return new File(file);
            }
         } else {
            return null;
         }
      } catch (URISyntaxException ex) {
         return null;
      }
   }

   /**
    * Return true if an URL correspond to a directory, including directories in jar or zip files.
    *
    * @param url the URL
    * @return true if the URL correspond to a directory
    */
   public static boolean isDirectory(URL url) {
      String prot = url.getProtocol();
      if (prot.equals("file")) {
         File file = new File(url.getFile());
         return file.isDirectory();
      } else if (prot.equals("jar") || prot.equals("zip")) {
         String file = url.getFile();
         return file.endsWith("/");
      } else {
         return false;
      }
   }

   /**
    * Return the list of children of a URL designing a directory, including directories in jar or zip files.
    *
    * <h1>Use Cases</h1>
    * <h2>For normal files</h2>
    * In normal cases (if the URL is not a zip file, and is not inside a zip or jar file), the method will return the same result as
    * {@link File#listFiles()}, in a List rather than an array.
    *
    * <h2>For zip or jar files</h2>
    * If the file is a zip or jar file, and the <code>strict</code> parameter is false, it will return the first level entries of
    * the zip file. For example, for this zip file:
    * <pre>
    * myZipFile.zip
    * ==&gt; entry1
    * ==&gt; entry2
    * ==&gt; directory1/entry3
    * ==&gt; directory1/entry4
    * </pre>
    *
    * The method will return the following list:
    * <ul>
    * <li>The URL for <code>myZipFile.zip!entry1</code>
    * <li>The URL for <code>myZipFile.zip!entry2</code>
    * <li>The URL for <code>myZipFile.zip!directory1</code>
    * </ul>
    *
    * <h2>For URL entries in zip or jar files</h2>
    * If the file is an URL entry in a zip or jar file it will return the first level entries of this entry.
    * For example, for this zip file:
    * <pre>
    * myZipFile.zip
    * ==&gt; entry1
    * ==&gt; entry2
    * ==&gt; directory1/entry3
    * ==&gt; directory1/entry4
    * </pre>
    *
    * The method will return the following list for the URL for the <code>myZipFile.zip!directory1/</code> entry:
    * <ul>
    * <li>The URL for <code>myZipFile.zip!entry1</code>
    * <li>The URL for <code>myZipFile.zip!entry2</code>
    * </ul>
    *
    * Note that in all cases, it is perfectly possible to reuse an URL returned by the method:
    * <ul>
    * <li>To open the URL as a Stream if it correspond to an existing entry</li>
    * <li>To apply this method with one of the returned URLs</li>
    * </ul>
    *
    * @param url the URL
    * @param strict true if children in jar or zip files should not be taken into account, except if the parent URL is itself a child of a zip or jar
    * file
    * @return the list of children
    */
   public static List<URL> getChildren(URL url, boolean strict) {
      try {
         String prot = url.getProtocol();
         if (prot.equals("file")) {
            File file = new File(url.getFile());
            if (!strict && (file.getName().endsWith("zip") || file.getName().endsWith("jar"))) {
               List<URL> children = getChildrenForZip(file.toURI().toURL());
               return children;
            } else {
               File[] files = file.listFiles();
               if (files == null) {
                  return new ArrayList<>();
               }
               List<URL> children = new ArrayList<>(files.length);
               for (int i = 0; i < files.length; i++) {
                  URL child = files[i].toURI().toURL();
                  children.add(child);
               }
               return children;
            }
         } else if (prot.equals("jar") || prot.equals("zip")) {
            List<URL> children = getChildrenForZip(url);
            return children;
         } else {
            return new ArrayList<>();
         }
      } catch (IOException ex) {
         return null;
      }
   }

   private static List<URL> getChildrenForZip(URL url) throws IOException {
      String filePath = url.getFile();
      String filePath2 = filePath;
      String parentPath = null;
      if (url.getFile().indexOf('!') != -1) {
         String parentPath1 = filePath.substring(0, filePath.lastIndexOf('!'));
         parentPath = filePath.substring(filePath.lastIndexOf('!') + 2);
         if (parentPath1.indexOf(':') != -1) {
            filePath2 = parentPath1.substring(parentPath1.indexOf(':') + 1);
         } else {
            filePath2 = parentPath1;
         }
      }
      if (filePath.charAt(filePath.length() - 1) == '/') {
         filePath = filePath.substring(0, filePath.length() - 1);
      }
      File file = new File(filePath2);
      ZipFile zip = new ZipFile(file);
      List<URL> children = new ArrayList<>();
      Set<String> directories = new TreeSet<>();
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
         ZipEntry entry = entries.nextElement();
         String name = entry.getName();
         String entryPath = name.substring(name.indexOf('!') + 1);
         if (parentPath == null) {
            if (entryPath.indexOf('/') == -1) {
               children.add(new URL("jar:file:" + filePath + "!/" + name));
            } else {
               String parentDirPath = entryPath.substring(0, entryPath.indexOf('/'));
               if (!directories.contains(parentDirPath)) {
                  directories.add(parentDirPath);
                  children.add(new URL("jar:file:" + filePath + "!/" + parentDirPath + "/"));
               }
            }
         } else if (entryPath.indexOf('/') != -1) {
            int slashIndex = entryPath.lastIndexOf('/');
            if (slashIndex == entryPath.length() - 1) {
               slashIndex = entryPath.indexOf('/');
            }
            String parentDirPath = entryPath.substring(0, slashIndex);
            if (parentPath.charAt(parentPath.length() - 1) == '/') {
               parentPath = parentPath.substring(0, parentPath.length() - 1);
            }
            String dirPath2 = entryPath.substring(slashIndex + 1);
            int secondIndex = dirPath2.indexOf('/');
            if (parentDirPath.equals(parentPath) && (secondIndex == - 1 || secondIndex == dirPath2.length() - 1)) {
               children.add(new URL("jar:" + filePath + "/" + dirPath2));
            }
         }
      }
      return children;
   }

   /**
    * Return the File whose name is defined by path. This method will return null if one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path</li>
    * </ul>
    *
    * @param path the path
    * @return the File
    */
   public static File getFile(String path) {
      if (path == null) {
         return null;
      } else {
         File file = new File(path);
         if (file.exists()) {
            return file;
         } else {
            return null;
         }
      }
   }

   /**
    * Return a name without blank, and containing only ASCII characters.
    *
    * @param name the name
    * @return the escape name
    */
   public static String getEscapedName(String name) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < name.length(); i++) {
         char c = name.charAt(i);
         if (c == ' ') {
            buf.append('_');
         } else if ((c > 0x20) && (c <= 0x7F)) {
            buf.append(c);
         } else {
            buf.append('_');
         }
      }
      return buf.toString();

   }

   /**
    * Return the File whose name is defined by path, and which may be defined relative to a parent directory.
    * This method will return null if one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path</li> </ul>
    *
    * @param dir the base directory
    * @param path the path
    * @return the File
    */
   public static File getFile(File dir, String path) {
      if (path == null) {
         return null;
      } else {
         File file = new File(path);
         if (!file.isAbsolute()) {
            file = new File(dir, path);
         }
         if (file.exists()) {
            return file;
         } else {
            return null;
         }
      }
   }

   /**
    * Create the File whose name is defined by path, and which may be defined relative to a parent directory. This method will
    * return null if one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path is not absolute, and dir does not exist or is not a directory</li>
    * </ul>
    *
    * @param dir the base directory
    * @param path the path
    * @return the File
    */
   public static File createFile(File dir, String path) {
      if (path == null) {
         return null;
      } else {
         File file = new File(path);
         if (!file.isAbsolute()) {
            if ((!dir.exists()) || (!dir.isDirectory())) {
               return null;
            } else {
               file = new File(dir, path);
            }
         }
         return file;
      }
   }

   /**
    * Return the URL corresponding to a File, avoiding to throw a SecurityException in secured environments like Applets.
    *
    * @param file the file
    * @return the URL corresponding to the File
    * @throws MalformedURLException if the conversion to an URL of the File lead to a MalformedURLException
    */
   public static URL getProtectedURL(File file) throws MalformedURLException {
      URL url;
      try {
         String fileS = replaceEscapedSequences(file.getAbsolutePath(), false);
         int entryPos = fileS.indexOf('!');
         fileS = fileS.replace("\\", "/");
         if (entryPos != -1) {
            url = new URL("jar", "", -1, "file:/" + fileS);
         } else {
            file = new File(fileS);
            url = file.toURI().toURL();
         }
      } catch (SecurityException e) {
         url = new URL("file:" + file.getPath());
      }
      return url;
   }

   /**
    * Return the URL whose name is defined by path. The path can be defined as a URL, or as a relative or absolute File. This method will return null if
    * one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path or URL protocol</li>
    * </ul>
    * If the baseDir does not exist, or the path points to a non existing File, the method will just return a null URL. Otherwise,
    * a MalformedURLException will be thrown if the path itself is not correctly defined.
    *
    * @param baseDir the base directory
    * @param path the relative or absolute path
    * @return the resulting URL
    * @throws MalformedURLException if the conversion to an URL of the path lead to a MalformedURLException
    */
   public static URL getURL(File baseDir, String path) throws MalformedURLException {
      return getURL(baseDir, path, true);
   }

   /**
    * Return the absolute path of an URL, using the file separator of the platform. The path can be defined as a URL, or as a relative or absolute File. This method will return null if
    * one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path or URL protocol</li>
    * </ul>
    * If the baseDir does not exist, or the path points to a non existing File, the method will just return a null URL. Otherwise,
    * a MalformedURLException will be thrown if the path itself is not correctly defined.
    *
    * @param baseDir the base directory
    * @param path the relative or absolute path
    * @return the path
    * @throws MalformedURLException if the conversion to an URL of the path lead to a MalformedURLException
    */
   public static String getAbsolutePath(File baseDir, String path) throws MalformedURLException {
      return getAbsolutePath(baseDir, path, true);
   }

   /**
    * Return the absolute path of an URL, using the file separator of the platform. The path can be defined as a URL, or as a relative or absolute File. This method will return null if
    * one of these conditions is true:
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path or URL protocol</li>
    * </ul>
    *
    * If <code>filterExistingURLs</code> is true;
    * <ul>
    * <li>If the baseDir does not exist, or the path points to a non existing File, the method will just return an empty String</li>
    * </ul>
    * Otherwise, a MalformedURLException will be thrown if the path itself is not correctly defined.
    *
    * @param baseDir the base directory
    * @param path the relative or absolute path
    * @param filterExistingURLs if true, return true for a not existing URLs
    * @return the path
    * @throws MalformedURLException if the conversion to an URL of the path lead to a MalformedURLException
    */
   public static String getAbsolutePath(File baseDir, String path, boolean filterExistingURLs) throws MalformedURLException {
      URL url = getURL(baseDir, path, filterExistingURLs);
      if (url == null) {
         return "";
      }
      String _path = url.getPath();
      if (_path.startsWith("/")) {
         _path = _path.substring(1);
      }
      String sep = System.getProperty("file.separator");
      _path = _path.replace('/', sep.charAt(0));
      return _path;
   }

   /**
    * Return the URL whose name is defined by path. The path can be defined as a URL, or as a relative or absolute File. This method will return null if
    * one of these conditions is true :
    * <ul>
    * <li>path is null</li>
    * <li>path does not define an existing file path or URL protocol</li>
    * </ul>
    *
    * If <code>filterExistingURLs</code> is true;
    * <ul>
    * <li>If the baseDir does not exist, or the path points to a non existing File, the method will just return a null URL</li>
    * </ul>
    * Otherwise, a MalformedURLException will be thrown if the path itself is not correctly defined.
    *
    * @param baseDir the base directory
    * @param path the relative or absolute path
    * @param filterExistingURLs if true, return true for a not existing URLs
    * @return the resulting URL
    * @throws MalformedURLException if the conversion to an URL of the path lead to a MalformedURLException
    */
   public static URL getURL(File baseDir, String path, boolean filterExistingURLs) throws MalformedURLException {
      URL url = null;
      if ((path == null) || (path.equals("."))) {
         url = getProtectedURL(baseDir);
      } else {
         try {
            path = path.replace("\\", "/");
            url = new URL(path);
         } catch (MalformedURLException e) {
            File file = getAbsoluteFile(baseDir, path);
            int entryPos = path.indexOf('!');
            if (entryPos != -1) {
               String thePath = path.substring(0, entryPos);
               File theFile = getAbsoluteFile(baseDir, thePath);
               if (!filterExistingURLs || theFile.exists()) {
                  url = getProtectedURL(file);
               }
            } else {
               if (!filterExistingURLs || file.exists()) {
                  url = getProtectedURL(file);
               }
            }
         }
      }
      return url;
   }

   /**
    * Return true if a path is absolute.
    *
    * @param path the path
    * @return true if the path is absolute
    */
   public static boolean isAbsolute(String path) {
      boolean isHTTP = isHTTPProtocol(path);
      path = replaceEscapedSequences(path);
      if (!isHTTP) {
         File file = new File(path);
         return file.isAbsolute();
      } else {
         try {
            URL url = new URL(path);
            return url.toURI().isAbsolute();
         } catch (MalformedURLException | URISyntaxException ex) {
            return false;
         }
      }
   }
   /**
    * Return true if an URI path is absolute.
    *
    * @param path the URI path
    * @return true if the URI is absolute
    */
   public static boolean isAbsoluteURI(String path) {
      path = replaceEscapedSequences(path);
      if (path.startsWith(".") || path.startsWith("/")) {
         return false;
      }
      return path.contains(":");
   }

   /**
    * Return the absolute path of an URL, using the file separator of the platform. If the relativePath if already absolute, the
    * parent baseURL will not be used, else the path will be constructed using the baseURL and the relativePath.
    *
    * @param baseURL the base URL
    * @param relativePath the relative path
    * @return the path
    */
   public static String getAbsolutePath(URL baseURL, String relativePath) {
      URL url = getAbsoluteURL(baseURL, relativePath);
      if (url == null) {
         return "";
      }
      String _path = url.getPath();
      if (_path.startsWith("/")) {
         _path = _path.substring(1);
      }
      _path = _path.replaceAll("//", "/");
      String sep = System.getProperty("file.separator");
      _path = _path.replace('/', sep.charAt(0));
      return _path;
   }

   /**
    * Return the File corresponding to a String-defined path, that can be defined as absolute, or as relative to a
    * base directory. The file can also be an URL corresponding to an HTTP protocol.
    *
    * If the relativePath if already absolute, the parent baseURL will not be used, else the resulting URL will be constructed using the
    * baseURL and the relativePath.
    *
    * @param baseURL the base URL
    * @param relativePath the relative path
    * @return the resulting URL
    */
   public static URL getAbsoluteURL(URL baseURL, String relativePath) {
      boolean isHTTP = isHTTPProtocol(relativePath);
      // this part is to avoid to add the baseURL if the local URL is not relative
      if (!isHTTP) {
         try {
            File file = new File(relativePath);
            if (file.isAbsolute()) {
               return file.toURI().toURL();
            }
         } catch (MalformedURLException ex) {
            return null;
         }
      }
      relativePath = replaceEscapedSequences(relativePath);
      if (isHTTP) {
         try {
            URL url = new URL(relativePath);
            return url;
         } catch (MalformedURLException ex) {
            return null;
         }
      } else {
         String protocol = baseURL.getProtocol();
         String host = baseURL.getHost();
         String path = baseURL.getFile();
         if (relativePath.startsWith("/")) {
            if (path.endsWith("/")) {
               path += relativePath.substring(1);
            } else {
               path += relativePath;
            }
         } else if (path.endsWith("/")) {
            path += relativePath;
         } else {
            path += "/" + relativePath;
         }
         try {
            URL url = new URL(protocol, host, path);
            return url;
         } catch (MalformedURLException ex) {
            return null;
         }
      }
   }

   /**
    * Return the File corresponding to a String-defined path, that can be defined as absolute, or as relative to a
    * base directory. The file can also be an URL corresponding to an HTTP protocol.
    *
    * @param baseDir the base directory
    * @param relativePath the relative path
    * @return the resulting File
    */
   public static File getAbsoluteFile(File baseDir, String relativePath) {
      relativePath = replaceEscapedSequences(relativePath);
      if (isHTTPProtocol(relativePath)) {
         try {
            URL url = new URL(relativePath);
            return getFile(url);
         } catch (MalformedURLException ex) {
            return null;
         }
      } else {
         File file = new File(relativePath);
         if (file.isAbsolute()) {
            return file;
         } else {
            try {
               URL url = getProtectedURL(file);
               if (isHTTPProtocol(url)) {
                  return file;
               } else {
                  String baseDirS = baseDir.getAbsolutePath();
                  baseDirS = replaceEscapedSequences(baseDirS);
                  baseDir = new File(baseDirS);
                  return new File(baseDir, relativePath);
               }
            } catch (MalformedURLException ex) {
               String baseDirS = baseDir.getAbsolutePath();
               baseDirS = replaceEscapedSequences(baseDirS);
               baseDir = new File(baseDirS);
               return new File(baseDir, relativePath);
            }
         }
      }
   }

   /**
    * Return a File abstract representation, making sure that it have the desired extension.
    *
    * @param file the File
    * @param exts the array of allowed extensions
    * @return a File which is the same as the inpout file if it has one of the allowed extensions, or a File
    * constructed to have the first extension
    */
   public static File getCompatibleFile(File file, String[] exts) {
      String _ext = getFileExtension(file);
      if (_ext != null) {
         _ext = _ext.toLowerCase();
      }
      List<String> _exts = Arrays.asList(exts);
      if (_ext != null && _exts.contains(_ext)) {
         return file;
      } else {
         return getCompatibleFile(file, exts[0]);
      }
   }

   /**
    * Return a File abstract representation, making sure that it have the desired extension.
    *
    * @param file the File
    * @param exts the array of allowed extensions
    * @param defaultExt the default extension
    * @return a File which is the same as the inpout file if it has one of the allowed extensions, or a File
    * constructed to have the default extension
    */
   public static File getCompatibleFile(File file, String[] exts, String defaultExt) {
      String _ext = getFileExtension(file);
      if (_ext != null) {
         _ext = _ext.toLowerCase();
      }
      List<String> _exts = Arrays.asList(exts);
      if (_ext != null && _exts.contains(_ext)) {
         return file;
      } else {
         return getCompatibleFile(file, defaultExt);
      }
   }

   /**
    * Return a File abstract representation, making sure that it have the desired extension.
    *
    * @param file the File
    * @param ext the of allowed extension
    * @return a File which is the same as the inpout file if it has one of the allowed extensions, or a File
    * constructed to have the extension
    */
   public static File getCompatibleFile(File file, String ext) {
      if (ext.lastIndexOf('.') != -1) {
         ext = ext.substring(ext.lastIndexOf('.'));
      }
      String _ext = getFileExtension(file);
      if (_ext != null) {
         _ext = _ext.toLowerCase();
      }
      if (_ext != null && _ext.equals(ext)) {
         return file;
      } else {
         String name = file.getName();
         int index = name.lastIndexOf('.');
         if (index == -1) {
            name = name + "." + ext;
         } else {
            name = name.substring(0, index);
         }
         file = new File(file.getParent(), name);
         return file;
      }
   }

   /**
    * Return the text corresponding to a File.
    *
    * @param file the File
    * @return the File content as text
    * @throws IOException
    */
   public static String getText(File file) throws IOException {
      return getText(file.toURI().toURL());
   }

   /**
    * Return the text corresponding to a URL.
    *
    * @param url the URL
    * @return the File content as text
    * @throws IOException
    */
   public static String getText(URL url) throws IOException {
      StringBuilder buf;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
         buf = new StringBuilder();
         boolean first = true;
         while (true) {
            if (!first) {
               buf.append('\n');
            }
            String line = reader.readLine();
            if (line != null) {
               buf.append(line);
            } else {
               break;
            }
            first = false;
         }
      }
      return buf.toString();
   }

   /**
    * Return a list of Files defined by their list of URLs. Note that even URL which do not exist on the network will still be added to the result.
    *
    * @param list the list of URLs
    * @return the associated Files array
    */
   public static File[] getFilesFromURLList(List<URL> list) {
      return getFilesFromURLList(list, false);
   }

   /**
    * Return a list of Files defined by their list of URLs.
    *
    * @param list the list of URLs
    * @param filterExistingURLs true if only URL which exist on the network will be added to the result
    * @return the associated Files array
    */
   public static File[] getFilesFromURLList(List<URL> list, boolean filterExistingURLs) {
      if (list == null) {
         return null;
      }
      File[] files = new File[list.size()];
      int i = 0;
      Iterator<URL> it = list.iterator();
      while (it.hasNext()) {
         URL url = it.next();
         if ((url != null) && ((!filterExistingURLs) || (exist(url)))) {
            files[i] = new File(replaceEscapedSequences(url.getFile()));
            // we must increment the index!!
            i++;
         } else if (filterExistingURLs) {
            if (url == null) {
               System.err.println("Encountered a null URL, skipped");
            } else if (!exist(url)) {
               System.err.println("URL " + url.getPath() + " does not exist, skipped");
            }
         }
      }
      return files;
   }

   /**
    * Return a list of URLs defined by their list of Files. Note that even Files which do not exist on the network will still be
    * added to the result.
    *
    * @param files the FIles array
    * @return the list of URLs
    */
   public static List<URL> getURLListFromFiles(File[] files) {
      return getURLListFromFiles(files, false);
   }

   /**
    * Return a list of URLs defined by their list of Files.
    *
    * @param files the list of Files
    * @param filterExistingFiles true if only Files which exist on the network will be added to the result
    * @return the list of URLs
    */
   public static List<URL> getURLListFromFiles(File[] files, boolean filterExistingFiles) {
      if (files == null) {
         return null;
      }
      List<URL> list = new ArrayList<>(files.length);
      for (int i = 0; i < files.length; i++) {
         try {
            if (files[i] != null) {
               if ((!filterExistingFiles) || exist(files[i])) {
                  list.add(files[i].toURI().toURL());
               } else {
                  System.err.println("File " + files[i].getPath() + " does not exist, skipped");
               }
            } else {
               System.err.println("Encountered a null File, skipped");
            }
         } catch (MalformedURLException ex) {
            ex.printStackTrace();
         }
      }
      return list;
   }

   /**
    * Return a List of URLs from a String separated with a ";".
    *
    * @param baseDir the base directory
    * @param urlList the list of URLs as a String separated with a ";"
    * @return the list of URLs
    * @throws MalformedURLException if the conversion to an URL of one of the Strings lead to a MalformedURLException
    */
   public static List<URL> getURLsFromString(File baseDir, String urlList) throws MalformedURLException {
      return getURLsFromString(baseDir, urlList, false);
   }

   /**
    * Return a List of URLs from a String separated with a ";".
    *
    * @param baseDir the base directory
    * @param urlList the list of URLs as a String separated with a ";"
    * @param filterExistingURLs if true, remove not existing URLs from the list
    * @return the list of URLs
    * @throws MalformedURLException if the conversion to an URL of one of the Files lead to a MalformedURLException
    */
   public static List<URL> getURLsFromString(File baseDir, String urlList, boolean filterExistingURLs) throws MalformedURLException {
      StringTokenizer tk = new StringTokenizer(urlList, ";");
      List<URL> list = new ArrayList<>(tk.countTokens());
      while (tk.hasMoreTokens()) {
         String path = tk.nextToken();
         File file = getAbsoluteFile(baseDir, path);
         if (filterExistingURLs) {
            if (file.exists()) {
               list.add(file.toURI().toURL());
            } else {
               System.err.println("URL " + path + " does not exist, skipped");
            }
         } else {
            list.add(file.toURI().toURL());
         }
      }
      return list;
   }

   /**
    * Return a List of URLs from a String separated with a ";".
    *
    * @param baseDir the base directory
    * @param urlList the list of URLs as a String separated with a ";"
    * @return the list of URLs
    * @throws MalformedURLException if the conversion to an URL of one of the Files lead to a MalformedURLException
    */
   public static List<URL> getURLsFromString(URL baseDir, String urlList) throws MalformedURLException {
      return getURLsFromString(baseDir, urlList, false);
   }

   /**
    * Return a List of URLs from a String separated with a ";".
    *
    * @param baseDir the base directory
    * @param urlList the list of URLs as a String separated with a ";"
    * @param filterExistingURLs if true, remove not existing URLs from the list
    * @return the list of URLs
    */
   public static List<URL> getURLsFromString(URL baseDir, String urlList, boolean filterExistingURLs) throws MalformedURLException {
      StringTokenizer tk = new StringTokenizer(urlList, ";");
      List<URL> list = new ArrayList<>(tk.countTokens());
      while (tk.hasMoreTokens()) {
         String path = tk.nextToken();
         URL url = getChildURL(baseDir, path);
         if (filterExistingURLs) {
            if (exist(url)) {
               list.add(url);
            } else {
               System.err.println("URL " + path + " does not exist, skipped");
            }
         } else {
            list.add(url);
         }
      }
      return list;
   }

   /**
    * Return a list of URLs from a String. For example: "url1;url2" will return an array of url1, url2.
    *
    * @param dir the base directory
    * @param paths the list of URLs as a String separated by ";"
    * @return the list of URLs
    */
   public static URL[] getURLs(File dir, String paths) {
      return getURLs(dir, paths, false);
   }

   /**
    * Return a list of URLs from a String. For example: "url1;url2" will return an array of url1, url2.
    *
    * @param dir the base directory
    * @param paths the list of URLs as a String separated by ";"
    * @param filterExistingURLs if true, remove not existing URLs from the list
    * @return the list of URLs
    */
   public static URL[] getURLs(File dir, String paths, boolean filterExistingURLs) {
      if (paths == null) {
         return null;
      } else {
         List<URL> urls = new ArrayList<>();
         StringTokenizer tk = new StringTokenizer(paths, ";");
         while (tk.hasMoreTokens()) {
            String path = tk.nextToken();
            try {
               URL url = getURL(dir, path);
               if (filterExistingURLs) {
                  if (exist(url)) {
                     urls.add(url);
                  } else {
                     System.err.println("URL " + path + " does not exist, skipped");
                  }
               } else {
                  urls.add(url);
               }
            } catch (MalformedURLException e) {
               System.err.println(e.getMessage());
            }
         }
         URL[] _urls = new URL[urls.size()];
         for (int i = 0; i < urls.size(); i++) {
            _urls[i] = urls.get(i);
         }
         return _urls;
      }
   }

   /**
    * Return true if a file is an ancestor of another file.
    *
    * @param ancestor the ancestor file
    * @param file the file
    * @return true if the ancestor file is an ancestor of file
    */
   public static boolean isAncestor(File ancestor, File file) {
      if (file.equals(ancestor)) {
         return true;
      }
      File currentFile = file;
      while (true) {
         currentFile = currentFile.getParentFile();
         if (currentFile == null) {
            return false;
         }
         if (currentFile.equals(ancestor)) {
            return true;
         }
      }
   }

   /**
    * Return true if an URL is an ancestor of another URL.
    *
    * @param ancestor the ancestor URL
    * @param url the URL
    * @return true if the ancestor URL is an ancestor of url
    */
   public static boolean isAncestor(URL ancestor, URL url) {
      if (!ancestor.getProtocol().equals(url.getProtocol())) {
         return false;
      }
      if (ancestor.getPort() != url.getPort()) {
         return false;
      }
      if (url.equals(ancestor)) {
         return true;
      }
      URL currentURL = url;
      while (true) {
         currentURL = getParentURL(currentURL, true);
         if (currentURL == null) {
            return false;
         }
         if (sameURL(ancestor, currentURL)) {
            return true;
         }
      }
   }

   /**
    * Return true if a file is relative to a directory (that is: the directory is one of the file ancestors).
    *
    * @param baseDir the directory
    * @param file the file
    * @return true if the file is relative to the directory
    */
   public static boolean isRelativeTo(File baseDir, File file) {
      if (baseDir == null) {
         return false;
      }

      File parent = file;
      boolean isRelativeTo = false;
      while (true) {
         parent = parent.getParentFile();
         if (parent == null) {
            break;
         } else if (parent.equals(baseDir)) {
            isRelativeTo = true;
            break;
         }
      }
      return isRelativeTo;
   }

   /**
    * Return the path of an URL relative to a directory.
    * Defer to {@link #getRelativePath(URL, URL, boolean)} with the <code>strict</code> parameter set to true.
    *
    * @param baseURL the base directory
    * @param url the URL
    * @return the path of the URL relative to the directory
    */
   public static String getRelativePath(URL baseURL, URL url) {
      return getRelativePath(baseURL, url, true);
   }

   /**
    * Return the drive name of a file.
    *
    * @param file the file
    * @return the drive name of the file
    */
   public static String getDrive(File file) {
      if (file == null) {
         return null;
      }
      Path path = file.toPath().getRoot();
      if (path == null) {
         return null;
      } else {
         return path.toString();
      }
   }

   private static boolean isSameDrive(File file1, File file2) {
      String drive1 = getDrive(file1);
      String drive2 = getDrive(file2);
      if (drive1 == null && drive2 == null) {
         return true;
      } else if (drive1 == null && drive2 != null) {
         return false;
      } else if (drive1 != null && drive2 == null) {
         return false;
      } else {
         return drive1.equals(drive2);
      }
   }

   /**
    * Return the path of an URL relative to a directory.
    *
    * @param baseURL the base directory
    * @param url the URL
    * @param strict true if this will fall back to the absolute path of the URL if the File is not under the base directory.
    * If this parameter is true, this will have the same result as {@link #getRelativePath(File, File)}
    * @return the path of the URL relative to the directory
    */
   public static String getRelativePath(URL baseURL, URL url, boolean strict) {
      if (isHTTPProtocol(url)) {
         return url.getPath();
      }
      URL parent = url;
      String path = url.getPath();
      if (!isSameDrive(FileUtilities.getFile(baseURL), FileUtilities.getFile(url))) {
         return url.getPath();
      }
      boolean isUnderBaseDir = false;
      while (true) {
         parent = FileUtilities.getParentURL(parent);
         if (parent == null) {
            break;
         }
         if (FileUtilities.sameURL(parent, baseURL)) {
            isUnderBaseDir = true;
            String path1 = baseURL.getPath();
            String path2 = url.getPath();
            int index = path2.lastIndexOf(path1);
            if (index != -1) {
               String dirPath = path1.substring(index);
               path = path2.substring(dirPath.length());
            }
         }
      }
      if (!isUnderBaseDir && !strict) {
         String newPath = convertToRelativePath(baseURL, url);
         // if the path is null, we don't have any common root, so we will return the file path
         if (newPath != null) {
            path = newPath;
         }
      }
      path = path.replace('\\', '/');
      path = path.replace("%23", "#");
      if (path.startsWith("/")) {
         path = path.substring(1);
      }
      return path;
   }

   /**
    * Return the path of a File relative to a directory. This will fall back to the absolute path of the File if the File is not under
    * the base directory.
    * Defer to {@link #getRelativePath(File, File, boolean)} with the <code>strict</code> parameter set to true.
    *
    * @param baseDir the base directory
    * @param file the File
    * @return the path of the File relative to the directory
    */
   public static String getRelativePath(File baseDir, File file) {
      return getRelativePath(baseDir, file, true);
   }

   /**
    * Return the path of a File relative to a directory.
    *
    * @param baseDir the base directory
    * @param file the File
    * @param strict true if this will fall back to the absolute path of the File if the File is not under the base directory.
    * If this parameter is true, this will have the same result as {@link #getRelativePath(File, File)}
    * @return the path of the File relative to the directory
    */
   public static String getRelativePath(File baseDir, File file, boolean strict) {
      File parent = file;
      String path = file.getAbsolutePath();
      boolean isUnderBaseDir = false;
      while (true) {
         parent = parent.getParentFile();
         if (parent == null) {
            break;
         }
         if (parent.equals(baseDir)) {
            isUnderBaseDir = true;
            try {
               String path1 = baseDir.getCanonicalPath();
               String path2 = file.getCanonicalPath();
               int index = path2.lastIndexOf(path1);
               if (index != -1) {
                  String dirPath = path1.substring(index);
                  path = path2.substring(dirPath.length() + 1);
               }
            } catch (IOException e) {
               System.err.println(e.getMessage());
            }
         }
      }
      if (!isUnderBaseDir && !strict) {
         String newPath = convertToRelativePath(baseDir, file);
         // if the path is null, we don't have any common root, so we will return the file path
         if (newPath != null) {
            path = newPath;
         }
      }
      path = path.replace('\\', '/');
      return path;
   }

   private static String convertToRelativePath(URL baseDir, URL url) {
      StringBuilder relPathBuilder = null;
      String absPath = baseDir.getPath();
      String relPath = url.getPath();

      absPath = absPath.replaceAll("\\\\", "/");
      relPath = relPath.replaceAll("\\\\", "/");
      String[] absoluteDirs = absPath.split("/");
      String[] relativeDirs = relPath.split("/");

      // find the shortest of the two paths, we will only check up to the associated root index
      int length = absoluteDirs.length < relativeDirs.length ? absoluteDirs.length : relativeDirs.length;

      // this is the position of the last common root of the two paths (if we find it of course)
      int lastCommonRoot = -1;
      int index;

      // iterate through the roots to find the last common root of the two paths
      for (index = 0; index < length; index++) {
         if (absoluteDirs[index].equals(relativeDirs[index])) {
            lastCommonRoot = index;
         } else {
            break;
         }
      }
      // if it is -1, there is no common root at all (means that we are in another drive), so in that case we will
      // return null
      if (lastCommonRoot != -1) {
         relPathBuilder = new StringBuilder();
         for (index = lastCommonRoot + 1; index < absoluteDirs.length; index++) {
            if (absoluteDirs[index].length() > 0) {
               relPathBuilder.append("../");
            }
         }
         for (index = lastCommonRoot + 1; index < relativeDirs.length - 1; index++) {
            relPathBuilder.append(relativeDirs[index]).append("/");
         }
         relPathBuilder.append(relativeDirs[relativeDirs.length - 1]);
         if (url.getRef() != null) {
            relPathBuilder.append("#").append(url.getRef());
         }
      }
      return relPathBuilder == null ? null : relPathBuilder.toString();
   }

   private static String convertToRelativePath(File baseDir, File file) {
      StringBuilder relPathBuilder = null;
      String absPath = baseDir.getAbsolutePath();
      String relPath = file.getAbsolutePath();

      absPath = absPath.replaceAll("\\\\", "/");
      relPath = relPath.replaceAll("\\\\", "/");
      String[] absoluteDirs = absPath.split("/");
      String[] relativeDirs = relPath.split("/");

      // find the shortest of the two paths, we will only check up to the associated root index
      int length = absoluteDirs.length < relativeDirs.length ? absoluteDirs.length : relativeDirs.length;

      // this is the position of the last common root of the two paths (if we find it of course)
      int lastCommonRoot = -1;
      int index;

      // iterate through the roots to find the last common root of the two paths
      for (index = 0; index < length; index++) {
         if (absoluteDirs[index].equals(relativeDirs[index])) {
            lastCommonRoot = index;
         } else {
            break;
         }
      }
      // if it is -1, there is no common root at all (means that we are in another drive), so in that case we will
      // return null
      if (lastCommonRoot != -1) {
         relPathBuilder = new StringBuilder();
         for (index = lastCommonRoot + 1; index < absoluteDirs.length; index++) {
            if (absoluteDirs[index].length() > 0) {
               relPathBuilder.append("../");
            }
         }
         for (index = lastCommonRoot + 1; index < relativeDirs.length - 1; index++) {
            relPathBuilder.append(relativeDirs[index]).append("/");
         }
         relPathBuilder.append(relativeDirs[relativeDirs.length - 1]);
      }
      return relPathBuilder == null ? null : relPathBuilder.toString();
   }

   /**
    * Collapse an URL to a representation without any <code>..</code> constructs.
    *
    * @param url the URL
    * @return the collapse URL representation
    * @throws MalformedURLException if the collapsing of the URL leads to a malformed URL (this should normally never happen, except if the
    * input URL is itself Malformed)
    */
   public static URL collapse(URL url) throws MalformedURLException {
      String path = url.toExternalForm();
      while (true) {
         Matcher m = PAT.matcher(path);
         if (m.matches()) {
            path = m.group(1) + "/" + m.group(2);
         } else {
            m = PAT2.matcher(path);
            if (m.matches()) {
               path = m.group(1);
            } else {
               break;
            }
         }
      }
      return new URL(path);
   }

   /**
    * Check if two URLs are the same, taking into account double slash and .. constructs.
    *
    * @param url1 the first URL
    * @param url2 the second URL
    * @return true if the two URL representations represent the same URL
    */
   public static boolean sameURL(URL url1, URL url2) {
      String path1 = url1.toExternalForm();
      if (!path1.contains("//")) {
         path1 = path1.replace("file:/", "file://");
         path1 = path1.replace("http:/", "http://");
         path1 = path1.replace("\\", "/");
      }
      Matcher m = PAT_HOST.matcher(path1);
      if (m.matches()) {
         path1 = m.group(1) + ":/" + m.group(2);
      }

      String path2 = url2.toExternalForm();
      if (!path2.contains("//")) {
         path2 = path2.replace("file:/", "file://");
         path2 = path2.replace("http:/", "http://");
         path2 = path2.replace("\\", "/");
      }
      m = PAT_HOST.matcher(path2);
      if (m.matches()) {
         path2 = m.group(1) + ":/" + m.group(2);
      }
      try {
         if (path1.charAt(path1.length() - 1) == '/') {
            path1 = path1.substring(0, path1.length() - 1);
         }
         if (path2.charAt(path2.length() - 1) == '/') {
            path2 = path2.substring(0, path2.length() - 1);
         }
         path1 = path1.replaceAll("//", "/");
         path2 = path2.replaceAll("//", "/");
         url1 = collapse(new URL(path1));
         url2 = collapse(new URL(path2));
         return url1.sameFile(url2);
      } catch (MalformedURLException ex) {
         return false;
      }
   }

   /**
    * Return the response code for an URL, without using a timeOut. Some of the following codes can occur:
    * <ul>
    * <li>200: the standard response for successful HTTP requests</li>
    * <li>404: the URL was not found</li>
    * <li>403: the request was valid but access to the URL is forbidden</li>
    * <li>...</li>
    * <li>-1 will be returned for an URL which does not correspond to an URL connection
    * </ul>
    * The complete list of HTTP return codes can be found on wikipedia here:
    * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>.
    *
    * Note that if the request timeOut, the method will return a 404 code.
    *
    * @param url the URL
    * @param proxy the proxy (can be null, in that case no proxy will be used)
    * @return the response code
    * @throws IOException if an I/O exception occurs
    */
   public static int getResponseCode(URL url, Proxy proxy) throws IOException {
      return getResponseCode(url, proxy, null, -1);
   }

   /**
    * Return the response code for an URL. Some of the following codes can occur:
    * <ul>
    * <li>200: the standard response for successful HTTP requests</li>
    * <li>404: the URL was not found</li>
    * <li>403: the request was valid but access to the URL is forbidden</li>
    * <li>...</li>
    * <li>-1 will be returned for an URL which does not correspond to an URL connection
    * </ul>
    * The complete list of HTTP return codes can be found on wikipedia here:
    * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>.
    *
    * Note that if the request timeOut, the method will return a 404 code.
    *
    * @param url the URL
    * @param proxy the proxy (can be null, in that case no proxy will be used)
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return the response code
    * @throws IOException if an I/O exception occurs
    */
   public static int getResponseCode(URL url, Proxy proxy, int timeOut) throws IOException {
      return getResponseCode(url, proxy, null, timeOut);
   }

   /**
    * Return the response code for an URL, witbout using a timeOut.
    *
    * @param url the URL
    * @param proxy the proxy (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link #FIREFOX10_USERAGENT} will be used by default
    * @return the response code
    * @throws IOException if an I/O exception occurs
    * @see #getResponseCode(URL, Proxy, String, int)
    */
   public static int getResponseCode(URL url, Proxy proxy, String userAgent) throws IOException {
      return getResponseCode(url, proxy, userAgent, -1);
   }

   /**
    * Return the response code for an URL. Some of the following codes can occur:
    * <ul>
    * <li>200: the standard response for successful HTTP requests</li>
    * <li>404: the URL was not found</li>
    * <li>403: the request was valid but access to the URL is forbidden</li>
    * <li>...</li>
    * <li>-1 will be returned for an URL which does not correspond to an URL connection</li>
    * </ul>
    *
    * The complete list of http return codes can be found on wikipedia here:
    * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>.
    *
    * Note that if the request timeOut, the method will return a 404 code.
    *
    * <h1>Algorithm</h1>
    * For local URLs, the method will just return -1.
    * <br/>
    * For http or https URLs, the method will:
    * <ul>
    * <li>Open an <code>HttpURLConnection</code> on the URL</li>
    * <li>Send the "GET" method on this connection</li>
    * <li>Return the connection status code</li>
    * </ul>
    *
    * @param url the URL
    * @param proxy the proxy (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link #FIREFOX10_USERAGENT} will be used by default
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return the response code
    * @throws IOException if an I/O exception occurs
    */
   public static int getResponseCode(URL url, Proxy proxy, String userAgent, int timeOut) throws IOException {
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
         huc.setRequestMethod("GET");
         huc.setInstanceFollowRedirects(true);
         if (timeOut > 0) {
            huc.setConnectTimeout(timeOut);
         }
         try {
            huc.connect();
            int ret = huc.getResponseCode();
            return ret;
         } catch (UnknownHostException | SocketTimeoutException e) {
            return 404;
         }
      } else {
         return -1;
      }
   }

   /**
    * Return the response code for an URL, without a proxy.
    *
    * @param url the URL
    * @return the response code
    * @throws IOException if an I/O exception occurs
    * @see #getResponseCode(URL, Proxy, int)
    */
   public static int getResponseCode(URL url) throws IOException {
      return getResponseCode(url, null, null);
   }

   /**
    * Return the response code for an URL, without a proxy.
    *
    * @param url the URL
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return the response code
    * @throws IOException if an I/O exception occurs
    * @see #getResponseCode(URL, Proxy)
    */
   public static int getResponseCode(URL url, int timeOut) throws IOException {
      return getResponseCode(url, null, null, timeOut);
   }

   /**
    * Return true if an url is an archive.
    *
    * @param url the url
    * @return true if the url is an archive
    */
   public static boolean isArchive(URL url) {
      File file = new File(url.getFile());
      return isArchive(file);
   }

   /**
    * Return true if a File is an archive.
    *
    * @param f the File
    * @return true if the File is an archive
    */
   public static boolean isArchive(File f) {
      int fileSignature = 0;
      try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
         fileSignature = raf.readInt();
      } catch (IOException e) {
         return false;
      }
      return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
   }

   /**
    * Return true if an URL is found, without using a timeOut. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String, int, int)} with no proxy and a default FireFox 10 User Agent.
    * For this method, 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @param url the URL
    * @return true if the URL is found
    * @see #isURLFound(URL, Proxy, String, int, int)
    */
   public static boolean isURLFound(URL url) {
      return isURLFound(url, null, null, OPTION_ACCEPT301CODE, -1);
   }

   /**
    * Return true if an URL is found, without using a timeOut. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String, int, int)} with no proxy and a default FireFox 10 User Agent.
    *
    * <h2>Options</h2>
    * There are several options that can be used to check the existence of the URL:
    * <ul>
    * <li>{@link #OPTION_ACCEPT301CODE}: if 301 or 308 return codes will be accepted as valid responses (URL found)</li>
    * <li>{@link #OPTION_FOLLOWREDIRECT}: if redirection links will be followed and used to check that the links exists</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * </ul>
    * These options are not mutually incompatible. You can do for example:
    * <pre>
    * boolean found = isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE | FileUtilities.OPTION_FOLLOWREDIRECT);
    * </pre>
    *
    * @param url the URL
    * @param options the options
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, int options) {
      return isURLFound(url, null, null, options, -1);
   }

   /**
    * Return true if an URL is found. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String, int, int)} with no proxy and a default FireFox 10 User Agent.
    *
    * <h2>Options</h2>
    * There are several options that can be used to check the existence of the URL:
    * <ul>
    * <li>{@link #OPTION_ACCEPT301CODE}: if 301 or 308 return codes will be accepted as valid responses (URL found)</li>
    * <li>{@link #OPTION_FOLLOWREDIRECT}: if redirection links will be followed and used to check that the links exists</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * </ul>
    * These options are not mutually incompatible. You can do for example:
    * <pre>
    * boolean found = isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE | FileUtilities.OPTION_FOLLOWREDIRECT);
    * </pre>
    *
    * @param url the URL
    * @param options the options
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, int options, int timeOut) {
      return isURLFound(url, null, null, options, timeOut);
   }

   /**
    * Return true if an URL is found, without using a timeOut. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String, int, int)} with a default FireFox 10 User Agent.
    * For this method, 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy) {
      return isURLFound(url, proxy, null, -1);
   }

   /**
    * Return true if an URL is found. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String)} with a default FireFox 10 User Agent.
    * For this method, 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy, int timeOut) {
      return isURLFound(url, proxy, null, timeOut);
   }

   /**
    * Return true if an URL is found. It can be a local File or an http or https URL. Defer to
    * {@link #isURLFound(URL, Proxy, String)} with a default FireFox 10 User Agent.
    *
    * <h2>Options</h2>
    * There are several options that can be used to check the existence of the URL:
    * <ul>
    * <li>{@link #OPTION_ACCEPT301CODE}: if 301 or 308 return codes will be accepted as valid responses (URL found)</li>
    * <li>{@link #OPTION_FOLLOWREDIRECT}: if redirection links will be followed and used to check that the links exists</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * </ul>
    * These options are not mutually incompatible. You can do for example:
    * <pre>
    * boolean found = isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE | FileUtilities.OPTION_FOLLOWREDIRECT);
    * </pre>
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param options the options
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy, int options, int timeOut) {
      return isURLFound(url, proxy, null, options, timeOut);
   }

   /**
    * Return true if an URL is found, without using a timeOut.
    * For this method, 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link #FIREFOX10_USERAGENT} will be used by default
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy, String userAgent) {
      return isURLFound(url, proxy, userAgent, OPTION_ACCEPT301CODE);
   }

   /**
    * Return true if an URL is found. For this method, 301 or 308 return codes will be accepted as valid responses (URL found).
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link #FIREFOX10_USERAGENT} will be used by default
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy, String userAgent, int timeOut) {
      return isURLFound(url, proxy, userAgent, OPTION_ACCEPT301CODE, timeOut);
   }

   private static synchronized void createSSLContext() {
      try {
         sslContext = SSLContext.getInstance("SSL");
         sslContext.init(null, new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
         sslSocketFactory = new ExtSSLSocketFactory(sslContext.getSocketFactory());
         allHostValid = new AllHostValid();
      } catch (KeyManagementException | NoSuchAlgorithmException ex) {
      }
   }

   /**
    * Return true if an URL is found. It can be a local File or an http or https URL.
    *
    * <h1>Algorithm</h1>
    * For local URLs, the method will just check if the URL exists and is a File.
    * For http or https URLs, the method will perform the following algorithm:
    * <ul>
    * <li>Open an <code>HttpURLConnection</code> on the URL</li>
    * <li>Send the "GET" method on this connection</li>
    * <li>Return the connection status code</li>
    * <li>Check the status code value and the content of the returned URL</li>
    * </ul>
    *
    * <h1>Checking http or https status code</h1>
    * For http and https protocols, the expected status codes for found URLs are:
    * <ul>
    * <li>200: The standard response for successful http requests. In that case the URL will be considered to be found</li>
    * <li>301: Permanent URL redirection, meaning current links or records using the URL that the response is received for should be updated. It is
    * usually the case for http URLs which should be updated to https. If <code>accept301Code</code> is set to false, this will lead to return
    * false</li>
    * <li>308: Permanent redirect, similar to 301</li>
    * </ul>
    *
    * Note that if the request timeOut, the method will return false.
    *
    * For 301 and 308 response codes, the path content of the returned URL for the <code>HttpURLConnection</code> will be checked:
    * <ul>
    * <li>If the paths of the input URL and the returned URLs are identical, the URL is considered to be found</li>
    * <li>If the paths of the input URL and the returned URLs are different, the URL is considered not to be found</li>
    * </ul>
    *
    * <h2>Options</h2>
    * There are several options that can be used to check the existence of the URL:
    * <ul>
    * <li>{@link #OPTION_ACCEPT301CODE}: if 301 or 308 return codes will be accepted as valid responses (URL found)</li>
    * <li>{@link #OPTION_FOLLOWREDIRECT}: if redirection links will be followed and used to check that the links exists</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_QUERIES}: by considering that URL with queries don't exist</li>
    * <li>{@link #OPTION_FILEEXIST_SKIP_PHP}: by considering that URL which point to php resources don't exist</li>
    * </ul>
    * These options are not mutually incompatible. You can do for example:
    * <pre>
    * boolean found = isURLFound(url, FileUtilities.OPTION_ACCEPT301CODE | FileUtilities.OPTION_FOLLOWREDIRECT);
    * </pre>
    *
    * For example:
    * <ul>
    * <li>"http://docs.oracle.com/javase/8/docs/api/java/awt/Button.html": this link is found with a 301 code and the
    * redirection links to the same link with an https protocol, it will be considered as existing</li>
    * <li>"http://docs.oracle.com/javase/8/docs/api/java/awt/Button1111.html": this link is found with a 301 code and the
    * redirection links to a special 404 Oracle link, it will be considered as not existing</li>
    * </ul>
    *
    * <h2>Examples</h2>
    * If the input URL is "http://en.wikpedia.org/wiki/markdown", the wikipedia server will return
    * "http://en.wikipedia.org/" with a 301 response code. In that case the two paths differ, so the method will return false.
    *
    * If the input URL is "http://www.flickr.com", the flickr server will return
    * "http://www.flickr.com" with a 301 response code. In that case the two paths are identical, so the method will return true.
    *
    * @param url the URL
    * @param proxy the proxy for http URLs (can be null, in that case no proxy will be used)
    * @param userAgent the user agent. If the user agent is null, the {@link #FIREFOX10_USERAGENT} will be used by default
    * @param options the options
    * @param timeOut the timeOut (-1 for no timeOut)
    * @return true if the URL is found
    */
   public static boolean isURLFound(URL url, Proxy proxy, String userAgent, int options, int timeOut) {
      if (isHTTPProtocol(url)) {
         boolean accept301Code = (options & OPTION_ACCEPT301CODE) == OPTION_ACCEPT301CODE;
         boolean followRedirects = (options & OPTION_FOLLOWREDIRECT) == OPTION_FOLLOWREDIRECT;
         if ((options & OPTION_FILEEXIST_SKIP_QUERIES) == OPTION_FILEEXIST_SKIP_QUERIES) {
            if (url.getQuery() != null) {
               return false;
            }
         }
         if ((options & OPTION_FILEEXIST_SKIP_PHP) == OPTION_FILEEXIST_SKIP_PHP) {
            String path = url.getPath();
            if (path.toLowerCase().endsWith(".php")) {
               return false;
            }
         }
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
            boolean isHTTPs = isHTTPSProtocol(url);
            if (isHTTPs && sslContext == null) {
               createSSLContext();
            }
            if (con instanceof HttpURLConnection) {
               HttpURLConnection huc = (HttpURLConnection) con;
               huc.setRequestProperty("User-Agent", userAgent);
               if (con instanceof HttpsURLConnection && allHostValid != null && sslContext != null) {
                  HttpsURLConnection conHttps = (HttpsURLConnection) con;
                  conHttps.setHostnameVerifier(allHostValid);
                  conHttps.setSSLSocketFactory(sslSocketFactory);
               }
               huc.setRequestMethod("HEAD");
               huc.setInstanceFollowRedirects(true);
               if (timeOut > 0) {
                  huc.setConnectTimeout(timeOut);
               }
               try {
                  huc.connect();
                  int code = huc.getResponseCode();
                  URL responseURL = huc.getURL();
                  if (code == 200) {
                     huc.disconnect();
                     return true;
                  } else if (code == 301 || code == 308) {
                     if (!accept301Code || responseURL == null) {
                        huc.disconnect();
                        return false;
                     } else {
                        if (followRedirects) {
                           String redirectURL = huc.getHeaderField("Location");
                           responseURL = getFinalURL(url, redirectURL, false);
                           huc.disconnect();
                           if (url.equals(responseURL)) {
                              return true;
                           } else {
                              String path1 = url.getPath().toLowerCase();
                              String path2 = responseURL.getPath().toLowerCase();
                              return path1.equals(path2);
                           }
                        } else {
                           huc.disconnect();
                           if (url.equals(responseURL)) {
                              return true;
                           } else {
                              String path1 = url.getPath().toLowerCase();
                              String path2 = responseURL.getPath().toLowerCase();
                              return path1.equals(path2);
                           }
                        }
                     }
                  } else {
                     huc.disconnect();
                     return false;
                  }
               } catch (UnknownHostException | SocketTimeoutException e) {
                  return false;
               }
            } else {
               return false;
            }
         } catch (IOException e) {
            return false;
         }
      } else {
         try {
            URLConnection con = url.openConnection();
            con.getInputStream();
            return true;
         } catch (IOException ex) {
            return false;
         }
      }
   }

   /**
    * Return the final URL after applying all the possible redirections. The initial provided URL will be returned if:
    * <ul>
    * <li>The protocol is neither "http" nor "https"</li>
    * <li>or there are no redirections</li>
    * </ul>
    *
    * A null URL will be returned if:
    * <ul>
    * <li>The protocol is "http" nor "https" and the URL is not found</li>
    * </ul>
    *
    * @param url
    * @return the final URL after applying all the possible redirections
    * @throws IOException
    */
   public static URL getRedirectURL(URL url) throws IOException {
      if (isHTTPProtocol(url)) {
         try {
            URLConnection con = url.openConnection();
            boolean isHTTPs = isHTTPSProtocol(url);
            if (isHTTPs && sslContext == null) {
               createSSLContext();
            }
            if (con instanceof HttpURLConnection) {
               HttpURLConnection huc = (HttpURLConnection) con;
               huc.setRequestProperty("User-Agent", FIREFOX10_USERAGENT);
               if (con instanceof HttpsURLConnection && allHostValid != null && sslContext != null) {
                  HttpsURLConnection conHttps = (HttpsURLConnection) con;
                  conHttps.setHostnameVerifier(allHostValid);
                  conHttps.setSSLSocketFactory(sslSocketFactory);
               }
               huc.setRequestMethod("HEAD");
               huc.setInstanceFollowRedirects(true);
               try {
                  huc.connect();
                  int code = huc.getResponseCode();
                  if (code == 200) {
                     huc.disconnect();
                     return url;
                  } else if (code == 301 || code == 308) {
                     String redirectURL = huc.getHeaderField("Location");
                     huc.disconnect();
                     ((HttpURLConnection) con).disconnect();
                     url = getFinalURL(url, redirectURL, true);
                     return url;
                  } else {
                     huc.disconnect();
                     return null;
                  }
               } catch (IOException e) {
                  return null;
               }
            } else {
               return url;
            }
         } catch (IOException e) {
            return null;
         }
      } else {
         return url;
      }
   }

   private static URL getFinalURL(URL url, String locationURL, boolean keepHTTPs) throws IOException {
      if (locationURL.endsWith("/") && !(url.getPath().endsWith("/"))) {
         locationURL = locationURL.substring(0, locationURL.length() - 1);
      }
      if (locationURL.startsWith("https") && sslContext == null) {
         createSSLContext();
      }
      HttpURLConnection con = (HttpURLConnection) new URL(locationURL).openConnection();
      if (con instanceof HttpsURLConnection && allHostValid != null && sslContext != null) {
         HttpsURLConnection conHttps = (HttpsURLConnection) con;
         conHttps.setHostnameVerifier(allHostValid);
         conHttps.setSSLSocketFactory(sslSocketFactory);
      }
      con.setInstanceFollowRedirects(true);
      con.connect();
      con.getInputStream();

      URL finalURL = new URL(locationURL);
      if (!keepHTTPs && finalURL.getProtocol().equals("https") && url.getProtocol().equals("http")) {
         finalURL = new URL(url.getProtocol(), finalURL.getHost(), finalURL.getPort(), finalURL.getFile());
      }
      if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
         String redirectUrl = con.getHeaderField("Location");
         if (!redirectUrl.toLowerCase().endsWith(url.getPath().toLowerCase())) {
            redirectUrl = redirectUrl + url.getPath();
         }
         con.disconnect();
         finalURL = getFinalURL(url, redirectUrl, keepHTTPs);
         return finalURL;
      }
      return finalURL;

   }

   private static class AllHostValid implements HostnameVerifier {
      @Override
      public boolean verify(String hostname, SSLSession session) {
         return true;
      }
   }

   private static class DefaultTrustManager implements X509TrustManager {
      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return null;
      }
   }
}
