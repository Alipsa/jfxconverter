/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.mdiutil.io.ReaderInputStream;
import org.mdiutil.xml.EntityListResolver;
import org.mdiutil.xml.XMLParserConfiguration;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.ErrorHandler;

/**
 * Various utilities concerning XML content.
 *
 * @version 1.2.41
 */
public class XMLNodeUtilities implements XMLNodeUtilitiesOptions {
   /**
    * The error handler used by the underlying parser. Default is null.
    */
   private static ErrorHandler ERROR_HANDLER = null;
   /**
    * The resolver used by the underlying parser. Default is null.
    */
   private static EntityListResolver RESOLVER = null;
   /**
    * The features of the parser factory.
    */
   private static final Map<String, Boolean> FEATURES = new HashMap<>();
   /**
    * Thze properties of the parser.
    */
   private static final Map<String, Object> PROPERTIES = new HashMap<>();
   private static Charset CHARSET = StandardCharsets.UTF_8;

   private XMLNodeUtilities() {
   }

   /**
    * Set the EntityListResolver which can manage entity resolution in XML files.
    *
    * @param resolver the resolver
    */
   public static void setEntityListResolver(EntityListResolver resolver) {
      RESOLVER = resolver;
   }

   /**
    * Return the EntityListResolver which can manage entity resolution in XML files.
    *
    * @return the resolver
    */
   public static EntityListResolver getEntityListResolver() {
      return RESOLVER;
   }

   /**
    * Set the charset to use to write the content. The default charset is {@link StandardCharsets#UTF_8}.
    *
    * @param charset the charset
    */
   public static void setCharset(Charset charset) {
      CHARSET = charset;
   }

   /**
    * Return the charset to use to write the content.
    *
    * @return the charset
    */
   public static Charset getCharset() {
      return CHARSET;
   }

   /**
    * Set an error handler wich will be fired for parsing errors detected by the underlying parser.
    *
    * @param errorHandler the error handler
    */
   public static void setErrorHandler(ErrorHandler errorHandler) {
      ERROR_HANDLER = errorHandler;
   }

   /**
    * Return the error handler wich will be fired for parsing errors detected by the underlying parser. null by default.
    *
    * @return the error handler
    */
   public static ErrorHandler getErrorHandler() {
      return ERROR_HANDLER;
   }

   /**
    * Reset the configuration of the underlying parser (reatures and properties).
    */
   public static void resetConfiguration() {
      FEATURES.clear();
      PROPERTIES.clear();
   }

   /**
    * Set a underlying parser factory feature.
    *
    * @param name the feature name
    * @param value the feature value
    */
   public static void setFeature(String name, boolean value) {
      FEATURES.put(name, value);
   }

   /**
    * Return the underlying parser factory features.
    *
    * @return the features
    */
   public static Map<String, Boolean> getFeatures() {
      return FEATURES;
   }

   /**
    * Set an underlying parser property.
    *
    * @param name the property name
    * @param value the property value
    */
   public static void setProperty(String name, Object value) {
      PROPERTIES.put(name, value);
   }

   /**
    * Return the underlying parser properties.
    *
    * @return the properties
    */
   public static Map<String, Object> getProperties() {
      return PROPERTIES;
   }

   /**
    * Search for the children nodes having a specified name.
    *
    * <h1>Example</h1>
    * Suppose we have this XML file:
    * <pre>
    * &lt;root desc="example"&gt;
    *   &lt;element1 name="first" id="theID"&gt;
    *     &lt;element2 name="second"&gt;
    *       &lt;element3 name="fourth" /&gt;
    *       &lt;element2 name="fith"/&gt;
    *     &lt;/element2&gt;
    *     &lt;element3 name="third" /&gt;
    *   &lt;/element1&gt;
    * &lt;/root&gt;
    * </pre>
    *
    * Then:
    * <ul>
    * <li>If we execute <code>search(root, "element2", false)</code>, the method will return one node, which is the first "element2" child</li>
    * <li>If we execute <code>search(root, "element2", true)</code>, the method will return two nodeq, which is the two "element2" children</li>
    * </ul>
    *
    * <h1>Qualified name</h1>
    * If the name contains a colon, it will be considered as a qualified name with a prefix and a null URI.
    *
    * For example <code>search(root, "h:element", false)</code> is equivalent to
    * <code>search(root, new QName(XMLConstants.NULL_NS_URI, "element", "h"), false)</code>
    *
    * @param node the root node
    * @param name the name to search for
    * @param deep if the search should continue for children of nodes found in the search
    * @return the list of nodes with the specified name
    */
   public static List<XMLNode> search(XMLNode node, String name, boolean deep) {
      return search(node, NodeUtilitiesUtils.getQName(name), deep);
   }

   /**
    * Search for the first child node having a specified qualified name.
    *
    * @param node the root node
    * @param qname the qualified name to search for
    * @param deep if the search should continue for children of nodes found in the search
    * @return the first child node with the specified qualified name
    */
   public static XMLNode searchFirst(XMLNode node, QName qname, boolean deep) {
      if (node.hasChildren()) {
         Stack<XMLNode> stack = new Stack<>();
         return NodeUtilitiesUtils.lookForFirstChild(stack, node, qname, deep);
      } else if (NodeUtilitiesUtils.strictEquals(node.getQualifiedName(), qname)) {
         return node;
      } else {
         return null;
      }
   }

   /**
    * Search for the the first child node having a specified name.
    *
    * <h1>Example</h1>
    * Suppose we have this XML file:
    * <pre>
    * &lt;root desc="example"&gt;
    *   &lt;element1 name="first" id="theID"&gt;
    *     &lt;element2 name="second"&gt;
    *       &lt;element3 name="fourth" /&gt;
    *       &lt;element2 name="fith"/&gt;
    *     &lt;/element2&gt;
    *     &lt;element3 name="third" /&gt;
    *   &lt;/element1&gt;
    * &lt;/root&gt;
    * </pre>
    *
    * Then:
    * <ul>
    * <li>If we execute <code>searchFirst(root, "element2", false)</code>, the method will return one node, which is the first "element2" child</li>
    * <li>If we execute <code>searchFirst(root, "element2", true)</code>, the method will also return one node, which is the first "element2"
    * child</li>
    * </ul>
    *
    * <h1>Qualified name</h1>
    * If the name contains a colon, it will be considered as a qualified name with a prefix and a null URI.
    *
    * For example <code>search(root, "h:element", false)</code> is equivalent to
    * <code>searchFirst(root, new QName(XMLConstants.NULL_NS_URI, "element", "h"), false)</code>
    *
    * @param node the root node
    * @param name the name to search for
    * @param deep if the search should continue for children of nodes found in the search
    * @return the first child node with the specified name
    */
   public static XMLNode searchFirst(XMLNode node, String name, boolean deep) {
      return searchFirst(node, NodeUtilitiesUtils.getQName(name), deep);
   }

   /**
    * Search for the children nodes having a specified qualified name.
    *
    * @param node the root node
    * @param qname the qualified name to search for
    * @param deep if the search should continue for children of nodes found in the search
    * @return the list of nodes with the specified name
    */
   public static List<XMLNode> search(XMLNode node, QName qname, boolean deep) {
      List<XMLNode> list = new ArrayList<>();
      if (node.hasChildren()) {
         Stack<XMLNode> stack = new Stack<>();
         NodeUtilitiesUtils.lookForChildren(list, stack, node, qname, deep);
      } else if (NodeUtilitiesUtils.strictEquals(node.getQualifiedName(), qname)) {
         list.add(node);
      }
      return list;
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader) {
      return getRootNode(reader, false, false, false, false);
   }

   /**
    * Return the root Node from a String.
    *
    * @param str the String
    * @return the root node
    */
   public static XMLRoot getRootNode(String str) {
      StringReader reader = new StringReader(str);
      return getRootNode(reader, false, false, false, false);
   }

   /**
    * Return the root Node from a String.
    *
    * @param str the String
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(String str, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      StringReader reader = new StringReader(str);
      return getRootNode(reader, config, preserveSpace, keepLineNumbers);
   }

   /**
    * Return the root Node from a String.
    *
    * @param str the String
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(String str, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      StringReader reader = new StringReader(str);
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setValidating(false);
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      XMLTreeHandler handler = new XMLTreeHandler("UTF-8", true);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(reader);
      return handler.getRoot();
   }

   /**
    * Return the root Node from a String.
    *
    * @param str the String
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(String str, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(str, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @return the root node
    */
   public static XMLRoot getRootNode(File file) {
      return getRootNode(file, false, false, false, false);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param options the options
    * @return the root node
    */
   public static XMLRoot getRootNode(File file, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      boolean isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;

      return getRootNode(file, showExceptions, showWarnings, preserveSpace, keepLineNumbers, isNamespaceAware);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(File file, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(file, config, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(File file, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      String encoding = null;
      try (InputStream stream = new FileInputStream(file);) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(file);
      return handler.getRoot();
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(File file, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(file, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(File file, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      String encoding = null;
      try (InputStream stream = new FileInputStream(file);) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(file);
      return handler.getRoot();
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL) {
      return getRootNode(xmlURL, false, false, false, false);
   }

   /**
    * Return the root Node from an URL.
    *
    * @param xmlURL the URL
    * @param options the options
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      boolean isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;

      return getRootNode(xmlURL, showExceptions, showWarnings, preserveSpace, keepLineNumbers, isNamespaceAware);
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(reader, config, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      String encoding = null;
      try (InputStream stream = new BufferedInputStream(new ReaderInputStream(reader))) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(reader);
      return handler.getRoot();
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(reader, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setValidating(false);
      parser.setXIncludeAware(false);
      parser.setNameSpaceAware(true);
      XMLTreeHandler handler = new XMLTreeHandler();
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(reader);
      return handler.getRoot();
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param options the options
    * @return the root node
    */
   public static XMLRoot getRootNode(Reader reader, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      boolean isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;

      return getRootNode(reader, showExceptions, showWarnings, preserveSpace, keepLineNumbers, isNamespaceAware);
   }

   /**
    * Return the root Node from a String.
    *
    * @param str the String
    * @param options the options
    * @return the root node
    */
   public static XMLRoot getRootNode(String str, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      boolean isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;

      return getRootNode(str, showExceptions, showWarnings, preserveSpace, keepLineNumbers, isNamespaceAware);
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(xmlURL, config, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      String encoding = null;
      try (InputStream stream = xmlURL.openStream()) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(xmlURL);
      return handler.getRoot();
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getRootNode(xmlURL, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the root node
    */
   public static XMLRoot getRootNode(URL xmlURL, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      String encoding = null;
      try (InputStream stream = xmlURL.openStream()) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(xmlURL);
      return handler.getRoot();
   }

   /**
    * Return the Node from a String.
    *
    * @param str the String
    * @return the node
    */
   public static XMLNode getNode(String str) {
      StringReader reader = new StringReader(str);
      return getNode(reader, false, false, false, false);
   }

   /**
    * Return the Node from a String.
    *
    * @param str the String
    * @param options the options
    * @return the node
    */
   public static XMLNode getNode(String str, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      boolean isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;

      return getNode(str, showExceptions, showWarnings, preserveSpace, keepLineNumbers, isNamespaceAware);
   }

   /**
    * Return the Node from a String.
    *
    * @param str the String
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the root node
    */
   public static XMLNode getNode(String str, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      StringReader reader = new StringReader(str);
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler("UTF-8", false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the Node from a String.
    *
    * @param str the String
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(String str, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getNode(str, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the Node from a String.
    *
    * @param str the String
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the node
    */
   public static XMLNode getNode(String str, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      StringReader reader = new StringReader(str);
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler("UTF-8", false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(Reader reader, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getNode(reader, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the Node from a Reader.
    *
    * @param reader the Reader
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the node
    */
   public static XMLNode getNode(Reader reader, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the Node from an URL.
    *
    * @param url the URL
    * @return the URL
    */
   public static XMLNode getNode(URL url) {
      return getNode(url, false, false, false, false);
   }

   /**
    * Return the Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(URL xmlURL, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      return getNode(xmlURL, showExceptions, showWarnings, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the node
    */
   public static XMLNode getNode(URL xmlURL, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(xmlURL);
      return handler.getRootNode();
   }

   /**
    * Return the Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(URL xmlURL, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      return getNode(xmlURL, config, preserveSpace, keepLineNumbers, false);
   }

   /**
    * Return the Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the node
    */
   public static XMLNode getNode(URL xmlURL, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(xmlURL);
      return handler.getRootNode();
   }

   /**
    * Return the Node from a Reader.
    *
    * @param reader the Reader
    * @param options the options
    * @return the node
    */
   public static XMLNode getNode(Reader reader, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;

      return getNode(reader, showExceptions, showWarnings, preserveSpace, keepLineNumbers);
   }

   /**
    * Return the Node from a Reader.
    *
    * @param reader the Reader
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @param isNamespaceAware true if the parser must keep the namespace information
    * @return the node
    */
   public static XMLNode getNode(Reader reader, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers, boolean isNamespaceAware) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @return the root node
    */
   public static XMLNode getNode(Reader reader) {
      return getNode(reader, false, false, false, false);
   }

   /**
    * Return the Node from a file.
    *
    * @param file the file
    * @return the node
    */
   public static XMLNode getNode(File file) {
      return getNode(file, false, false, false, false);
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @param options the options
    * @return the root node
    */
   public static XMLNode getNode(File file, int options) {
      boolean showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      boolean showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      boolean preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      boolean keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;

      return getNode(file, showExceptions, showWarnings, preserveSpace, keepLineNumbers);
   }

   /**
    * Return the Node from a file.
    *
    * @param file the file
    * @param config the parser configuration
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(File file, XMLParserConfiguration config, boolean preserveSpace, boolean keepLineNumbers) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.setParserConfiguration(config);
      parser.parse(file);
      return handler.getRootNode();
   }

   /**
    * Return the Node from a file.
    *
    * @param file the file
    * @param showExceptions true if the parsing must show exceptions
    * @param showWarnings true if the parsing must show warnings
    * @param preserveSpace true if space characters must be preserved
    * @param keepLineNumbers true if line numbers in the XML File are kept
    * @return the node
    */
   public static XMLNode getNode(File file, boolean showExceptions, boolean showWarnings, boolean preserveSpace, boolean keepLineNumbers) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!FEATURES.isEmpty()) {
         parser.setFeatures(FEATURES);
      }
      if (!PROPERTIES.isEmpty()) {
         parser.setProperties(PROPERTIES);
      }
      if (RESOLVER != null) {
         parser.setEntityListResolver(RESOLVER);
      }
      parser.setXIncludeAware(false);
      parser.setValidating(false);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (ERROR_HANDLER != null) {
         handler.setErrorHandler(ERROR_HANDLER);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.showExceptions(showExceptions);
      parser.showWarnings(showWarnings);
      parser.parse(file);
      return handler.getRootNode();
   }

   /**
    * Save in a File the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @param outputFile the output file
    */
   public static void print(XMLNode node, int indentation, File outputFile) throws IOException {
      print(node, indentation, outputFile, null);
   }

   /**
    * Save in a File the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @param outputFile the output file
    * @param encoding the encoding (can be null)
    */
   public static void print(XMLNode node, int indentation, File outputFile, String encoding) throws IOException {
      char[] chars = new char[indentation];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), CHARSET))) {
         if (node instanceof XMLRoot && encoding == null) {
            XMLRoot root = (XMLRoot) node;
            encoding = root.getEncoding();
         }
         if (encoding != null) {
            NodeUtilitiesUtils.writeEncoding(writer, encoding);
         }
         NodeUtilitiesUtils.printNode(writer, node, new BoundPrefix(), "", tabS, false, true);
         writer.flush();
      }
   }

   /**
    * Save in an URL the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @param outputURL the output URL
    */
   public static void print(XMLNode node, int indentation, URL outputURL) throws IOException {
      File file = new File(outputURL.getFile());
      print(node, indentation, file);
   }

   /**
    * Print as a String the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @return the String
    */
   public static String print(XMLNode node, int indentation) {
      return print(node, indentation, (String) null);
   }

   /**
    * Print as a String the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @param encoding the encoding (vcan be null)
    * @return the String
    */
   public static String print(XMLNode node, int indentation, String encoding) {
      char[] chars = new char[indentation];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      StringBuilder buf = new StringBuilder();
      if (node instanceof XMLRoot && encoding == null) {
         XMLRoot root = (XMLRoot) node;
         encoding = root.getEncoding();
      }
      if (encoding != null) {
         NodeUtilitiesUtils.writeEncoding(buf, encoding);
      }
      NodeUtilitiesUtils.printNode(buf, node, "", tabS, true, true);
      return buf.toString();
   }
}
