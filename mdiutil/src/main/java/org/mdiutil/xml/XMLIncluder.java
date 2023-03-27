/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2020, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.io.ReaderInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class process XML files by including the content of XInclude children.
 *
 * @version 1.2.41
 */
public class XMLIncluder extends DefaultHandler implements XMLChildrenIncluder {
   private static final Pattern NEWLINE_START = Pattern.compile("\\t*\\n(.*)");
   private URL url = null;
   private InputStream stream = null;
   private URL dir = null;
   private URL defaultBaseDir = null;
   private String includePrefix = null;
   private StringWriter writer = null;
   private SAXParserFactory fac = null;
   private boolean hasXInclude = false;
   private boolean hasFatalError = false;
   private boolean isXML = true;
   private final Stack<Node> nodes = new Stack<>();
   private StringBuilder builder = null;
   private ErrorHandler errorHandler = null;
   private Charset charset = StandardCharsets.UTF_8;
   private Locator locator = null;
   private ClassLoader loader = null;
   private String encoding = null;
   private boolean addComments = false;
   private boolean deepComments = false;
   private String tab = null;
   private String parentTab = "";

   /**
    * Constructor.
    */
   public XMLIncluder() {
   }

   /**
    * Constructor.
    *
    * @param file the file
    * @throws MalformedURLException
    */
   public XMLIncluder(File file) throws MalformedURLException {
      this.url = file.toURI().toURL();
      this.dir = FileUtilities.getParentURL(url);
      this.encoding = XMLEncodingParser.getEncoding(file);
   }

   /**
    * Constructor.
    *
    * @param url the URL
    */
   public XMLIncluder(URL url) {
      this.url = url;
      this.dir = FileUtilities.getParentURL(url);
      this.encoding = XMLEncodingParser.getEncoding(url);
   }

   /**
    * Constructor.
    *
    * @param stream the InputStream
    */
   public XMLIncluder(InputStream stream) {
      this.stream = stream;
      this.encoding = XMLEncodingParser.getEncoding(stream);
   }

   /**
    * Set the charset to use to write the content. The default charset is {@link StandardCharsets#UTF_8}.
    *
    * @param charset the charset
    */
   @Override
   public void setCharset(Charset charset) {
      this.charset = charset;
   }

   /**
    * Return the charset to use to write the content.
    *
    * @return the charset
    */
   @Override
   public Charset getCharset() {
      return charset;
   }

   /**
    * Set the includer file.
    *
    * @param file the file
    * @throws MalformedURLException
    */
   @Override
   public void setFile(File file) throws MalformedURLException {
      this.url = file.toURI().toURL();
      this.dir = FileUtilities.getParentURL(url);
      this.encoding = XMLEncodingParser.getEncoding(file);
   }

   /**
    * Set the includer URL.
    *
    * @param url the URL
    */
   @Override
   public void setURL(URL url) {
      this.url = url;
      this.dir = FileUtilities.getParentURL(url);
      this.encoding = XMLEncodingParser.getEncoding(url);
   }

   /**
    * Set the includer InputStream.
    *
    * @param stream the InputStream
    */
   @Override
   public void setInputStream(InputStream stream) {
      this.stream = stream;
      this.encoding = XMLEncodingParser.getEncoding(stream);
   }

   private XMLIncluder(URL url, SAXParserFactory fac, StringWriter writer, String tab, boolean isXML) {
      this.url = url;
      this.dir = FileUtilities.getParentURL(url);
      this.fac = fac;
      this.writer = writer;
      if (tab == null) {
         this.parentTab = "";
      } else {
         this.parentTab = tab;
      }
      this.isXML = isXML;
   }

   /**
    * Set if a comment must be added for each included file.
    *
    * @param addComments true if a comment must be added for each included file
    * @param deep true if the comments in deep include declarations must be added
    */
   @Override
   public void setAddComments(boolean addComments, boolean deep) {
      this.addComments = addComments;
      this.deepComments = deep;
   }

   /**
    * Return true if a comment must be added for each included file.
    *
    * @return true if a comment must be added for each included file
    */
   @Override
   public boolean isAddingComments() {
      return addComments;
   }

   /**
    * Return true if the comments in deep include declarations must be added.
    *
    * @return true if the comments in deep include declarations must be added
    */
   @Override
   public boolean isEnablingDeepComments() {
      return deepComments;
   }

   /**
    * Set the ClassLoader. It will only be used if {@link XMLSAXParser#isRevertingOldReaderBehavior()} is false.
    *
    * @param loader the ClassLoader
    */
   public void setClassLoader(ClassLoader loader) {
      this.loader = loader;
   }

   /**
    * Return the ClassLoader. It will only be used if {@link XMLSAXParser#isRevertingOldReaderBehavior()} is false.
    *
    * @return the ClassLoader
    */
   public ClassLoader getClassLoader() {
      return loader;
   }

   /**
    * Set the ErrorHandler which will be fired for warnings and errors when parsing the content.
    *
    * @param errorHandler the ErrorHandler
    */
   @Override
   public void setErrorHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   /**
    * Return the ErrorHandler which will be fired for warnings and errors when parsing the content.
    *
    * @return the ErrorHandler
    */
   @Override
   public ErrorHandler getErrorHandler() {
      return errorHandler;
   }

   /**
    * Return true if the parsing of the file threw a Fatal error.
    *
    * @return true if the parsing of the file threw a Fatal error
    */
   public boolean hasFatalError() {
      return hasFatalError;
   }

   /**
    * Set the default base directory for including files. This is useful to detect relative XML files when applying the xinclude declarations if the
    * input is an InputStream.
    *
    * @param baseDir the default base directory
    */
   @Override
   public void setDefaultBaseDirectory(URL baseDir) {
      this.defaultBaseDir = baseDir;
   }

   @Override
   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (uri.equals("http://www.w3.org/2001/XInclude")) {
         this.includePrefix = prefix;
      }
   }

   /**
    * Report a fatal XML parsing error.
    */
   @Override
   public void fatalError(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.fatalError(e);
         } catch (SAXException ex) {
         }
      }
      this.hasFatalError = true;
   }

   /**
    * Report an XML parsing error.
    */
   @Override
   public void error(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.error(e);
         } catch (SAXException ex) {
         }
      }
   }

   /**
    * Report an XML parsing warning.
    */
   @Override
   public void warning(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.warning(e);
         } catch (SAXException ex) {
         }
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      if (builder == null) {
         builder = new StringBuilder();
      }
      builder.append(ch, start, length);
   }

   private String writeCharactersBuilder(boolean startElement) {
      if (builder != null) {
         String str = builder.toString();
         String _tab = writeCharacterLines(str, startElement);
         builder = null;
         return _tab;
      } else {
         return null;
      }
   }

   /**
    * Return the lines arrays of a multilined text.
    *
    * @param text the text
    * @param startElement true for character lines before an element start
    * @return the lines arrays
    */
   private String writeCharacterLines(String text, boolean startElement) {
      boolean newLine = false;
      Matcher m = NEWLINE_START.matcher(text);
      if (m.matches()) {
         text = m.group(1);
         writer.append("\n");
         newLine = true;
      }
      String[] lines = text.split("\\r?\\n");
      boolean start = true;
      if (lines != null && lines.length != 0) {
         for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
               continue;
            }
            if (start) {
               writer.append(line);
               start = false;
            } else if (!line.trim().isEmpty()) {
               writer.append(line);
               if (i < lines.length - 1) {
                  writer.append("\n");
                  newLine = true;
               }
            } else if (i == lines.length - 1) {
               writer.append("\n");
               newLine = true;
               writer.append(line);
            }
         }
      }
      if (text.endsWith("\n")) {
         writer.append("\n");
         return "";
      } else if (lines == null || lines.length == 0) {
         return "";
      } else {
         if (!newLine && startElement) {
            writer.append("\n");
         }
         return lines[lines.length - 1];
      }
   }

   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      if (!nodes.isEmpty()) {
         Node parent = nodes.peek();
         parent.hasChildren = true;
         if (!parent.isStarted) {
            writer.append(">");
            parent.isStarted = true;
         }
      }
      tab = writeCharactersBuilder(true);
      StringBuilder buf = new StringBuilder();
      hasXInclude = false;
      if (includePrefix != null) {
         int i = qname.lastIndexOf(':');
         if (i != -1 && i < qname.length() - 1) {
            String maybeInclude = qname.substring(0, i);
            if (maybeInclude.equals(includePrefix) && localname.equals("include")) {
               hasXInclude = true;
            }
         }
      }
      if (hasXInclude) {
         includeChild(attr);
      } else {
         nodes.add(new Node(qname));
         writer.append(parentTab + "<" + qname);
         writeAttributes(buf, attr);
         writer.append(buf.toString());
      }
   }

   /**
    * Set the document locator.
    *
    * @param locator the locator
    */
   @Override
   public void setDocumentLocator(Locator locator) {
      this.locator = locator;
   }

   private void includeChild(Attributes attr) {
      String href = null;
      boolean _isXML = true;
      for (int i = 0; i < attr.getLength(); i++) {
         String key = attr.getQName(i);
         String value = attr.getValue(i);
         if (key.equals("href")) {
            href = value;
         } else if (key.equals("parse")) {
            _isXML = value.equals("xml");
         }
      }
      if (href != null) {
         URL childURL = null;
         if (dir != null) {
            childURL = FileUtilities.getChildURL(dir, href);
         } else {
            try {
               if (FileUtilities.isAbsoluteURI(href)) {
                  childURL = new URL(href);
               } else if (defaultBaseDir != null) {
                  childURL = FileUtilities.getChildURL(defaultBaseDir, href);
               }
               if (!FileUtilities.exist(childURL)) {
                  try {
                     errorHandler.warning(new SAXParseException("URL " + href + " does not exist", locator));
                     childURL = null;
                  } catch (SAXException ex1) {
                  }
               } else if (FileUtilities.isDirectory(childURL)) {
                  try {
                     errorHandler.warning(new SAXParseException("URL " + href + " is a directory", locator));
                     childURL = null;
                  } catch (SAXException ex1) {
                  }
               }
            } catch (MalformedURLException ex) {
               try {
                  errorHandler.warning(new SAXParseException(ex.getMessage(), locator));
                  childURL = null;
               } catch (SAXException ex1) {
               }
            }
         }
         if (childURL != null) {
            if (addComments) {
               writer.write(tab + "<!-- Included file: " + href + " -->");
               writer.write("\n");
            }
            XMLIncluder includer = new XMLIncluder(childURL, fac, writer, tab, _isXML);
            includer.setErrorHandler(errorHandler);
            if (addComments) {
               includer.setAddComments(true, deepComments);
            }
            includer.setDefaultBaseDirectory(defaultBaseDir);
            includer.setClassLoader(loader);
            includer.setCharset(charset);
            includer.parse();
         }
      }
   }

   private void writeAttributes(StringBuilder buf, Attributes attr) {
      for (int i = 0; i < attr.getLength(); i++) {
         String key = attr.getQName(i);
         String value = attr.getValue(i);
         buf.append(" ").append(key).append("=\"").append(value).append("\"");
      }
   }

   @Override
   public void endElement(String uri, String localname, String qname) throws SAXException {
      boolean hasContent = false;
      if (builder != null) {
         if (!nodes.peek().isStarted) {
            writer.append(">");
         }
         writeCharactersBuilder(false);
         hasContent = true;
      }
      if (!hasXInclude) {
         if (hasContent || nodes.peek().hasChildren) {
            writer.append(parentTab + "</" + qname + ">");
         } else {
            writer.append("/>");
         }
         nodes.pop();
      } else {
         hasXInclude = false;
      }
   }

   /**
    * Return a reader making sure the reader use the correct encoding and not the default platform encoding.
    *
    * @param url the URL
    * @return the reader
    * @throws IOException
    */
   private InputStream getStreamFromURL(URL url) throws IOException {
      if (charset == null || XMLSAXParser.isRevertingOldReaderBehavior()) {
         return url.openStream();
      } else {
         Reader _reader = ReaderPathConstructor.getReaderFromURL(url, charset, loader);
         return new ReaderInputStream(_reader, charset.newEncoder());
      }
   }

   private void parse() {
      try {
         if (isXML) {
            SAXParser parser = fac.newSAXParser();
            if (url != null) {
               try (InputStream _stream = getStreamFromURL(url)) {
                  parser.parse(_stream, this);
               }
            } else {
               parser.parse(stream, this);
            }
         } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
               StringBuilder buf = new StringBuilder();
               boolean first = true;
               String line;
               while ((line = br.readLine()) != null) {
                  if (!first || !line.startsWith("<?xml ")) {
                     buf.append(line).append("\n");
                  }
                  if (first) {
                     first = false;
                  }
               }
               writer.append(buf.toString());
            }
         }
      } catch (ParserConfigurationException | SAXException | IOException ex) {
      }
   }

   /**
    * Return a String representing the XML content.
    *
    * @return the StringReader
    */
   @Override
   public String getContent() {
      try {
         if (writer == null) {
            writer = new StringWriter();
            if (encoding != null) {
               writer.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
               writer.write("\n");
            }
            fac = SAXParserFactory.newInstance();
            fac.setNamespaceAware(true);
            SAXParser parser = fac.newSAXParser();
            if (url != null) {
               try (InputStream _stream = url.openStream()) {
                  parser.parse(_stream, this);
               }
            } else {
               parser.parse(stream, this);
            }
         }
         return writer.toString();
      } catch (ParserConfigurationException | SAXException | IOException ex) {
         return null;
      }
   }

   /**
    * Write the resulting XML to an URL.
    *
    * @param url the URL
    * @throws IOException
    */
   @Override
   public void write(URL url) throws IOException {
      URLConnection yc = url.openConnection();
      try (BufferedReader reader = new BufferedReader(getReader());
         BufferedWriter _writer = new BufferedWriter(new OutputStreamWriter(yc.getOutputStream(), charset))) {
         if (encoding != null) {
            _writer.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            _writer.newLine();
         }
         while (true) {
            String line = reader.readLine();
            if (line != null) {
               _writer.append(line);
               _writer.newLine();
            } else {
               break;
            }
         }
      }
   }

   /**
    * Write the resulting XML to a File.
    *
    * @param file the File
    * @throws IOException
    */
   @Override
   public void write(File file) throws IOException {
      try (BufferedReader reader = new BufferedReader(getReader());
         BufferedWriter _writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {
         boolean first = true;
         while (true) {
            String line = reader.readLine();
            if (line != null) {
               if (first && !line.startsWith("<?xml")) {
                  if (encoding != null) {
                     _writer.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
                     _writer.newLine();
                  }
               }
               first = false;
               _writer.append(line);
               _writer.newLine();
            } else {
               break;
            }
         }
      }
   }

   /**
    * Return a StringReader to the XML content.
    *
    * @return the StringReader
    */
   @Override
   public StringReader getReader() {
      String content = getContent();
      if (content != null) {
         return new StringReader(content);
      } else {
         return null;

      }
   }

   private static class Node {
      boolean hasChildren = false;
      boolean isStarted = false;
      String qname;

      private Node(String qname) {
         this.qname = qname;
      }

      @Override
      public String toString() {
         return qname;
      }
   }
}
