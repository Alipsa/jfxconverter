/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.xml.EntityListResolver;
import org.mdiutil.xml.XMLChildrenIncluder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * This class process XML files by including the content of XInclude children.
 *
 * @version 1.2.41
 */
public class XMLNodeIncluder implements XMLChildrenIncluder {
   private static final String XML_INCLUDE_URI = "http://www.w3.org/2001/XInclude";
   private URL url = null;
   private InputStream stream = null;
   private boolean addComments = false;
   private boolean deepComments = false;
   private int indentation = 2;
   private boolean keepPrefixes = false;
   private Set<String> skippedPrefixes = null;
   private boolean skipAllPrefixes = false;
   private EntityListResolver resolver = null;
   private ErrorHandler errorHandler = null;
   private Charset charset = StandardCharsets.UTF_8;
   private URL defaultBaseDir = null;

   /**
    * Constructor.
    */
   public XMLNodeIncluder() {
   }

   /**
    * Constructor.
    *
    * @param file the file
    * @throws MalformedURLException
    */
   public XMLNodeIncluder(File file) throws MalformedURLException {
      this.url = file.toURI().toURL();
   }

   /**
    * Constructor.
    *
    * @param url the URL
    */
   public XMLNodeIncluder(URL url) {
      this.url = url;
   }

   /**
    * Constructor.
    *
    * @param stream the InputStream
    */
   public XMLNodeIncluder(InputStream stream) {
      this.stream = stream;
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
   }

   /**
    * Set the includer URL.
    *
    * @param url the URL
    */
   @Override
   public void setURL(URL url) {
      this.url = url;
   }

   /**
    * Set the includer InputStream.
    *
    * @param stream the InputStream
    */
   @Override
   public void setInputStream(InputStream stream) {
      this.stream = stream;
   }

   /**
    * Set true if all the prefixes in the included children must be skipped.
    *
    * @param skip true if all the prefixes in the included children must be skipped
    */
   public void skipAllPrefixes(boolean skip) {
      this.skipAllPrefixes = skip;
   }

   /**
    * Set a list of prefixes to skip.
    *
    * @param prefixes the prefixes
    */
   public void skipPrefixes(String... prefixes) {
      if (prefixes == null || prefixes.length == 0) {
         skippedPrefixes = null;
      } else {
         skippedPrefixes = new HashSet<>();
         for (int i = 0; i < prefixes.length; i++) {
            skippedPrefixes.add(prefixes[i]);
         }
      }
   }

   /**
    * Set a list of prefixes to skip.
    *
    * @param prefixes the prefixes
    */
   public void skipPrefixes(Set<String> prefixes) {
      skippedPrefixes = prefixes;
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
    * Set if the underlying parser must keep the prefixes. It will ot contain attributes used as Namespace declarations (xmlns*) unless the
    * http://xml.org/sax/features/namespace-prefixes feature is set to true (it is false by default).
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
    * Set the default base directory for including files. This is useful to detect relative XML files when applying the xinclude declarations if the
    * input is an InputStream.
    *
    * @param baseDir the default base directory
    */
   @Override
   public void setDefaultBaseDirectory(URL baseDir) {
      this.defaultBaseDir = baseDir;
   }

   /**
    * Set the indentation.
    *
    * @param indentation the indentation
    */
   public void setIndentation(int indentation) {
      this.indentation = indentation;
   }

   /**
    * Return the indentation.
    *
    * @return the indentation
    */
   public int getIndentation() {
      return indentation;
   }

   private XMLNode getNodeWithInclusion(XMLNodeUtilities2 utils, URL parentURL, XMLNode node, boolean fromRoot, boolean currentAddComments) {
      XMLNode resultNode;
      if (node instanceof XMLRoot) {
         if (node.hasComment()) {
            resultNode = new XMLCommentedRoot(node.getQualifiedName(), node.getComment());
         } else {
            resultNode = new XMLRoot(node.getQualifiedName());
         }
         ((XMLRoot) resultNode).setEncoding(((XMLRoot) node).getEncoding());
      } else {
         if (node.hasComment()) {
            resultNode = new XMLCommentedNode(node.getQualifiedName(), node.getComment());
         } else {
            resultNode = new XMLNode(node.getQualifiedName());
         }
      }
      Iterator<Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Entry<SortableQName, String> entry = it.next();
         QName qname = entry.getKey().getQName();
         resultNode.addAttribute(qname, entry.getValue());
      }
      if (resultNode.hasBoundPrefix(XML_INCLUDE_URI)) {
         resultNode.getBoundPrefixes().remove(XML_INCLUDE_URI);
      }
      if (node.hasBoundPrefixes()) {
         Iterator<Entry<String, String>> it2 = node.getBoundPrefixes().entrySet().iterator();
         while (it2.hasNext()) {
            Entry<String, String> entry = it2.next();
            // check if we bind the prefix
            mayBindPrefix(resultNode, fromRoot, entry.getValue(), entry.getKey());
         }
      }
      appendChildren(utils, parentURL, node, resultNode, currentAddComments);
      return resultNode;
   }

   private void mayBindPrefix(XMLNode resultNode, boolean fromRoot, String prefix, String uri) {
      /* We always bind the prefix in the following cases:
       * - we are in the root node
       * - the prefix is empty ("xmlns")
       * If we are not in these cases, we don't bind the prefix if:
       * - skipAllPrefixes is true
       * - or the prefix is in the skippedPrefixes list
       */
      if (fromRoot || prefix.isEmpty()) {
         resultNode.bindPrefix(prefix, uri);
      } else if (!skipAllPrefixes && (skippedPrefixes == null || !skippedPrefixes.contains(prefix))) {
         resultNode.bindPrefix(prefix, uri);
      }
   }

   private void appendChildren(XMLNodeUtilities2 utils, URL parentURL, XMLNode node, XMLNode resultNode, boolean currentAddComments) {
      Iterator<XMLNode> it2 = node.getChildren().iterator();
      while (it2.hasNext()) {
         XMLNode child = it2.next();
         QName qname = child.getQualifiedName();
         if (qname.getNamespaceURI().equals(XML_INCLUDE_URI)) {
            if (child.hasAttribute("href")) {
               String href = child.getAttributeValue("href");
               boolean _currentAddComments0 = currentAddComments;
               URL theURL = FileUtilities.getChildURL(parentURL, href);
               if (FileUtilities.exist(theURL)) {
                  XMLNode theNode;
                  if (currentAddComments) {
                     theNode = utils.getNode(theURL, "Included File " + href);
                     if (!deepComments) {
                        _currentAddComments0 = false;
                     }
                  } else {
                     theNode = utils.getNode(theURL);
                  }
                  URL parentURL2 = FileUtilities.getParentURL(theURL);
                  XMLNodeUtilities2 utils2 = new XMLNodeUtilities2(utils);
                  theNode = getNodeWithInclusion(utils2, parentURL2, theNode, false, _currentAddComments0);
                  resultNode.addChild(theNode);
               } else {
                  appendError("File at " + href + " location does not exist", child);
               }
            } else {
               appendError("No href property for include node", child);
            }
         } else {
            XMLNode theChild = child.copy(false);
            resultNode.addChild(theChild);
            appendChildren(utils, parentURL, child, theChild, currentAddComments);
         }
      }
   }

   private void appendError(String message, XMLNode node) {
      LocatorImpl locator = new LocatorImpl();
      locator.setLineNumber(node.getLineNumber());
      SAXParseException exception = new SAXParseException(message, locator);
      if (errorHandler != null) {
         try {
            errorHandler.error(exception);
         } catch (SAXException ex) {
         }
      } else {
         System.err.println(exception.getMessage());
      }
   }

   /**
    * Return a String representing the XML content.
    *
    * @return the StringReader
    */
   @Override
   public String getContent() {
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.setNameSpaceAware(false);
      utils.showExceptions(false);
      utils.showWarnings(false);
      utils.keepLineNumbers(true);
      utils.setKeepPrefixes(keepPrefixes);
      utils.setEntityListResolver(resolver);
      utils.setErrorHandler(errorHandler);
      utils.setKeepPrefixes(keepPrefixes);
      XMLRoot root = null;
      if (url != null) {
         root = utils.getRootNode(url);
      } else if (stream != null) {
         InputStreamReader reader = new InputStreamReader(stream);
         root = utils.getRootNode(reader);
      }
      root = (XMLRoot) getNodeWithInclusion(utils, defaultBaseDir, root, true, addComments);
      if (root != null) {
         return root.toString(indentation);
      } else {
         return null;
      }
   }

   @Override
   public Reader getReader() {
      String content = getContent();
      if (content != null) {
         return new StringReader(content);
      } else {
         return null;
      }
   }

   @Override
   public void write(File file) throws IOException {
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.setNameSpaceAware(true);
      utils.showExceptions(false);
      utils.showWarnings(false);
      utils.keepLineNumbers(true);
      utils.setCharset(charset);
      utils.setKeepPrefixes(keepPrefixes);
      utils.setErrorHandler(errorHandler);
      utils.setEntityListResolver(resolver);
      XMLRoot root = null;
      if (url != null) {
         root = utils.getRootNode(url);
      } else if (stream != null) {
         InputStreamReader reader = new InputStreamReader(stream);
         root = utils.getRootNode(reader);
      }
      URL parentURL = FileUtilities.getParentURL(url);
      root = (XMLRoot) getNodeWithInclusion(utils, parentURL, root, true, addComments);
      if (root != null) {
         utils.print(root, indentation, file);
      }
   }

   @Override
   public void write(URL url) throws IOException {
      XMLNodeUtilities2 utils = new XMLNodeUtilities2();
      utils.setNameSpaceAware(true);
      utils.keepLineNumbers(true);
      utils.showExceptions(false);
      utils.showWarnings(false);
      utils.setCharset(charset);
      utils.setKeepPrefixes(keepPrefixes);
      utils.setErrorHandler(errorHandler);
      utils.setEntityListResolver(resolver);
      XMLRoot root = null;
      if (url != null) {
         root = utils.getRootNode(url);
      } else if (stream != null) {
         InputStreamReader reader = new InputStreamReader(stream);
         root = utils.getRootNode(reader);
      }
      URL parentURL = FileUtilities.getParentURL(url);
      root = (XMLRoot) getNodeWithInclusion(utils, parentURL, root, true, addComments);
      if (root != null) {
         utils.print(root, indentation, url);
      }
   }
}
