/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

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
import org.mdiutil.xml.EntityListResolver;
import org.mdiutil.xml.XMLParserConfiguration;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.ErrorHandler;

/**
 * Various utilities concerning XML content.
 *
 * @version 1.2.41
 */
public class XMLNodeUtilities2 implements XMLNodeUtilitiesOptions {
   /**
    * The error handler used by the underlying parser. Default is null.
    */
   private ErrorHandler errorHandler = null;
   /**
    * The resolver used by the underlying parser. Default is null.
    */
   private EntityListResolver resolver = null;
   /**
    * The features of the parser factory.
    */
   private final Map<String, Boolean> features = new HashMap<>();
   /**
    * The properties of the parser.
    */
   private final Map<String, Object> properties = new HashMap<>();
   private boolean showExceptions = false;
   private boolean showWarnings = false;
   private boolean preserveSpace = false;
   private boolean keepLineNumbers = false;
   private boolean isNamespaceAware = false;
   private boolean keepPrefixes = false;
   private boolean isValidating = false;
   private Charset charset = StandardCharsets.UTF_8;
   private Map<String, String> prefixMappings = null;

   public XMLNodeUtilities2() {
   }

   public XMLNodeUtilities2(XMLNodeUtilities2 utils) {
      this.showExceptions = utils.showExceptions;
      this.showWarnings = utils.showWarnings;
      this.isNamespaceAware = utils.isNamespaceAware;
      this.keepLineNumbers = utils.keepLineNumbers;
      this.keepPrefixes = utils.keepPrefixes;
      this.preserveSpace = utils.preserveSpace;
      this.resolver = utils.resolver;
      this.isValidating = utils.isValidating;
      this.charset = utils.charset;
      this.errorHandler = utils.errorHandler;
      if (utils.prefixMappings != null) {
         this.prefixMappings = new HashMap<>(utils.prefixMappings);
      }
      this.features.putAll(utils.getFeatures());
      this.properties.putAll(utils.getProperties());
   }

   /**
    * Apply a parser configuration.
    *
    * @param config the configuration
    */
   public void applyConfiguration(XMLParserConfiguration config) {
      showExceptions = config.isShowingExceptions;
      showWarnings = config.isShowingWarnings;
      isNamespaceAware = config.isNameSpaceAware;
      resolver = config.resolver;
      isValidating = config.isValidating;

      features.clear();
      features.putAll(config.getFeatures());
      properties.clear();
      properties.putAll(config.getProperties());
   }

   /**
    * Set the charset to use to write the content. The default charset is {@link StandardCharsets#UTF_8}.
    *
    * @param charset the charset
    */
   public void setCharset(Charset charset) {
      this.charset = charset;
   }

   /**
    * Return the charset to use to write the content.
    *
    * @return the charset
    */
   public Charset getCharset() {
      return charset;
   }

   /**
    * Set if the underlying parser is validating.
    *
    * @param isValidating true if the underlying parser is validating
    */
   public void setValidating(boolean isValidating) {
      this.isValidating = isValidating;
   }

   /**
    * Return true if the underlying parser is validating.
    *
    * @return true if the underlying parser is validating
    */
   public boolean isValidating() {
      return isValidating;
   }

   /**
    * Set the underlying parser options.
    *
    * @param options the options
    */
   public void setOptions(int options) {
      showExceptions = (options & SHOW_EXCEPTIONS) == SHOW_EXCEPTIONS;
      showWarnings = (options & SHOW_WARNINGS) == SHOW_WARNINGS;
      preserveSpace = (options & PRESERVE_SPACE) == PRESERVE_SPACE;
      keepLineNumbers = (options & KEEP_LINE_NUMBERS) == KEEP_LINE_NUMBERS;
      isNamespaceAware = (options & NAMESPACE_AWARE) == NAMESPACE_AWARE;
   }

   /**
    * Set if the parser shows the exceptions encountered during the parsing.
    *
    * @param isShowingExceptions true if the parser shows the exceptions encountered during the parsing
    */
   public void showExceptions(boolean isShowingExceptions) {
      this.showExceptions = isShowingExceptions;
   }

   /**
    * Return true if the parser shows the exceptions encountered during the parsing.
    *
    * @return true if the parser shows the exceptions encountered during the parsing
    */
   public boolean isShowingExceptions() {
      return showExceptions;
   }

   /**
    * Set if the parser shows the warnings encountered during the parsing.
    *
    * @param isShowingWarnings true if the parser shows the warnings encountered during the parsing
    */
   public void showWarnings(boolean isShowingWarnings) {
      this.showWarnings = isShowingWarnings;
   }

   /**
    * Return true if the parser shows the warnings encountered during the parsing.
    *
    * @return true if the parser shows the warnings encountered during the parsing
    */
   public boolean isShowingWarnings() {
      return showWarnings;
   }

   /**
    * Set if spaces should be preserved.
    *
    * @param preserveSpace true if spaces should be preserved
    */
   public void preserveSpaces(boolean preserveSpace) {
      this.preserveSpace = preserveSpace;
   }

   /**
    * Return true if spaces should be preserved.
    *
    * @return true if spaces should be preserved
    */
   public boolean isPreservingSpaces() {
      return preserveSpace;
   }

   /**
    * Set if the line numbers should be kept during the parsing.
    *
    * @param keepLineNumbers true if the line numbers should be kept during the parsing
    */
   public void keepLineNumbers(boolean keepLineNumbers) {
      this.keepLineNumbers = keepLineNumbers;
   }

   /**
    * Return true if the line numbers should be kept during the parsing.
    *
    * @return true if the line numbers should be kept during the parsing
    */
   public boolean isKeepingLineNumbers() {
      return keepLineNumbers;
   }

   /**
    * Set if the parser is namespace-aware.
    *
    * @param isNameSpaceAware true if the parser is namespace-aware
    */
   public void setNameSpaceAware(boolean isNameSpaceAware) {
      this.isNamespaceAware = isNameSpaceAware;
   }

   /**
    * Return true if the parser is namespace-aware.
    *
    * @return true if the parser is namespace-aware
    */
   public boolean isNameSpaceAware() {
      return isNamespaceAware;
   }

   /**
    * Set if the underlying parser must keep the prefixes. It will ot contain attributes used as Namespace declarations (xmlns*)
    * unless the http://xml.org/sax/features/namespace-prefixes feature is set to true (it is false by default).
    *
    * @param keepPrefixes true if the parser must keep the prefixes
    */
   public void setKeepPrefixes(boolean keepPrefixes) {
      // see https://xerces.apache.org/xerces-j/features.html
      // see https://stackoverflow.com/questions/5416637/how-to-get-xmlnsxxx-attribute-if-set-setnamespaceawaretrue-in-sax
      this.keepPrefixes = keepPrefixes;
   }

   /**
    * Return true if the underlying parser must keep the prefixes.
    *
    * @return true if the parser must keep the prefixes
    */
   public boolean isKeepingPrefixes() {
      return keepPrefixes;
   }

   /**
    * Set the EntityListResolver which can manage entity resolution in XML files.
    *
    * @param resolver the resolver
    */
   public void setEntityListResolver(EntityListResolver resolver) {
      this.resolver = resolver;
   }

   /**
    * Return the EntityListResolver which can manage entity resolution in XML files.
    *
    * @return the resolver
    */
   public EntityListResolver getEntityListResolver() {
      return resolver;
   }

   /**
    * Set an error handler wich will be fired for parsing errors detected by the underlying parser.
    *
    * @param errorHandler the error handler
    */
   public void setErrorHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   /**
    * Return the error handler wich will be fired for parsing errors detected by the underlying parser. null by default.
    *
    * @return the error handler
    */
   public ErrorHandler getErrorHandler() {
      return errorHandler;
   }

   /**
    * Reset the configuration of the underlying parser (reatures and properties).
    */
   public void resetConfiguration() {
      features.clear();
      properties.clear();
   }

   /**
    * Set a underlying parser factory feature.
    *
    * @param name the feature name
    * @param value the feature value
    */
   public void setFeature(String name, boolean value) {
      features.put(name, value);
   }

   /**
    * Return the underlying parser factory features.
    *
    * @return the features
    */
   public Map<String, Boolean> getFeatures() {
      return features;
   }

   /**
    * Set an underlying parser property.
    *
    * @param name the property name
    * @param value the property value
    */
   public void setProperty(String name, Object value) {
      properties.put(name, value);
   }

   /**
    * Return the underlying parser properties.
    *
    * @return the properties
    */
   public Map<String, Object> getProperties() {
      return properties;
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
   public List<XMLNode> search(XMLNode node, String name, boolean deep) {
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
   public XMLNode searchFirst(XMLNode node, QName qname, boolean deep) {
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
   public XMLNode searchFirst(XMLNode node, String name, boolean deep) {
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
   public List<XMLNode> search(XMLNode node, QName qname, boolean deep) {
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
    * Return the Node from a String.
    *
    * @param str the String
    * @return the node
    */
   public XMLNode getNode(String str) {
      StringReader reader = new StringReader(str);
      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler("UTF-8", false);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the Node from a Reader.
    *
    * @param reader the Reader
    * @return the node
    */
   public XMLNode getNode(Reader reader) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler(false);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.parse(reader);
      return handler.getRootNode();
   }

   /**
    * Return the Node from a file.
    *
    * @param file the file
    * @return the root node
    */
   public XMLNode getNode(File file) {
      String encoding = null;
      try (InputStream stream = new FileInputStream(file);) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler(encoding, false);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
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
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @return the root node
    */
   public XMLNode getNode(URL xmlURL) {
      return getNode(xmlURL, null);
   }

   /**
    * Return the root Node from an XML URL, with a comment.
    *
    * @param xmlURL the XML URL
    * @param comment the comment
    * @return the root node
    */
   public XMLNode getNode(URL xmlURL, String comment) {
      String encoding = null;
      try (InputStream stream = xmlURL.openStream()) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler(encoding, false, comment);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
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
    * Return the root Node from a String.
    *
    * @param str the String
    * @return the root node
    */
   public XMLRoot getRootNode(String str) {
      StringReader reader = new StringReader(str);
      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler("UTF-8");
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.parse(reader);
      return handler.getRoot();
   }

   /**
    * Return the root Node from a file.
    *
    * @param file the file
    * @return the root node
    */
   public XMLRoot getRootNode(File file) {
      String encoding = null;
      try (InputStream stream = new FileInputStream(file);) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
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
    * Return the root Node from a Reader.
    *
    * @param reader the Reader
    * @return the root node
    */
   public XMLRoot getRootNode(Reader reader) {
      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler();
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
      }
      handler.preserveSpace(preserveSpace);
      handler.setKeepLineNumbers(keepLineNumbers);
      parser.setHandler(handler);
      parser.parse(reader);
      return handler.getRoot();
   }

   /**
    * Return the root Node from an XML URL.
    *
    * @param xmlURL the XML URL
    * @return the root node
    */
   public XMLRoot getRootNode(URL xmlURL) {
      String encoding = null;
      try (InputStream stream = xmlURL.openStream()) {
         XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
         encoding = xmlStreamReader.getCharacterEncodingScheme();
      } catch (IOException | XMLStreamException ex) {
      }

      XMLSAXParser parser = new XMLSAXParser();
      if (!features.isEmpty()) {
         parser.setFeatures(features);
      }
      if (!properties.isEmpty()) {
         parser.setProperties(properties);
      }
      if (resolver != null) {
         parser.setEntityListResolver(resolver);
      }
      parser.setNameSpaceAware(isNamespaceAware);
      parser.setXIncludeAware(false);
      parser.setValidating(isValidating);
      parser.setKeepPrefixes(keepPrefixes);
      XMLTreeHandler handler = new XMLTreeHandler(encoding);
      if (errorHandler != null) {
         handler.setErrorHandler(errorHandler);
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
    * Save in a File the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param indentation the indentation for the XML file for each child node
    * @param outputFile the output file
    */
   public void print(XMLNode node, int indentation, File outputFile) throws IOException {
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
   public void print(XMLNode node, int indentation, File outputFile, String encoding) throws IOException {
      char[] chars = new char[indentation];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset))) {
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
   public void print(XMLNode node, int indentation, URL outputURL) throws IOException {
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
   public String print(XMLNode node, int indentation) {
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
   public String print(XMLNode node, int indentation, String encoding) {
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
