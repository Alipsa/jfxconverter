/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2018, 2020, 2021, 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.mdiutil.xml.ResolverSAXHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Parse an XML File and return the associated tree of Nodes.
 *
 * @version 1.2.43
 */
public class XMLTreeHandler extends ResolverSAXHandler {
   private static final String SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
   private XMLNode node = null;
   private final Stack<XMLNode> nodes = new Stack<>();
   private XMLNode root = null;
   private String encoding = null;
   private StringBuilder buf = null;
   private boolean preserveSpace = false;
   private boolean keepLineNumbers = false;
   private String comment = null;
   private final boolean asRoot;
   private ErrorHandler errorHandler = null;
   private final Set<String> currentMappings = new HashSet<>();
   private final Map<String, String> prefixMappings = new HashMap<>();

   /**
    * Constructor.
    */
   public XMLTreeHandler() {
      this.asRoot = true;
   }

   /**
    * Constructor.
    *
    * @param asRoot true if the root node must be a {@link XMLRoot}.
    */
   public XMLTreeHandler(boolean asRoot) {
      this.asRoot = asRoot;
   }

   /**
    * Constructor.
    *
    * @param encoding the encoding of the XML file
    */
   public XMLTreeHandler(String encoding) {
      this.encoding = encoding;
      this.asRoot = true;
   }

   /**
    * Constructor.
    *
    * @param encoding the encoding of the XML file
    * @param asRoot true if the root node must be a {@link XMLRoot}.
    */
   public XMLTreeHandler(String encoding, boolean asRoot) {
      this.encoding = encoding;
      this.asRoot = asRoot;
   }

   /**
    * Constructor.
    *
    * @param encoding the encoding of the XML file
    * @param asRoot true if the root node must be a {@link XMLRoot}.
    * @param comment the comment to add for the root node
    */
   public XMLTreeHandler(String encoding, boolean asRoot, String comment) {
      this.encoding = encoding;
      this.asRoot = asRoot;
      this.comment = comment;
   }

   /**
    * Set an error handler wich will be fired for parsing errors detected by the parser.
    *
    * @param errorHandler the error handler
    */
   public void setErrorHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   /**
    * Return the error handler wich will be fired for parsing errors detected by the parser. null by default.
    *
    * @return the error handler
    */
   public ErrorHandler getErrorHandler() {
      return errorHandler;
   }

   /**
    * Set if space characters must be preserved.
    *
    * @param preserveSpace true if space characters must be preserved
    */
   public void preserveSpace(boolean preserveSpace) {
      this.preserveSpace = preserveSpace;
   }

   /**
    * Return true if space characters must be preserved.
    *
    * @return true if space characters must be preserved
    */
   public boolean isPreservingSpace() {
      return preserveSpace;
   }

   /**
    * Set if line numbers in the XML File are kept.
    *
    * @param keepLineNumbers true if line numbers in the XML File are kept
    */
   public void setKeepLineNumbers(boolean keepLineNumbers) {
      this.keepLineNumbers = keepLineNumbers;
   }

   /**
    * Return true if line numbers in the XML File are kept.
    *
    * @return true if line numbers in the XML File are kept
    */
   public boolean isKeepingLineNumbers() {
      return keepLineNumbers;
   }

   /**
    * Return the root node.
    *
    * @return the root node
    */
   public XMLNode getRootNode() {
      return root;
   }

   /**
    * Return the root node, as a {@link XMLRoot}.
    *
    * @return the root node
    */
   public XMLRoot getRoot() {
      if (root instanceof XMLRoot) {
         return (XMLRoot) root;
      } else {
         return null;
      }
   }

   /**
    * Receive notification of the beginning of an element.
    *
    * @param uri the Namespace URI
    * @param localname the local name (without prefix), or the empty string if Namespace processing is not being performed
    * @param qname The qualified name (with prefix), or the empty string if qualified names are not available
    * @param attr the specified or defaulted attributes
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      super.startElement(uri, localname, qname, attr);
      parseElement(uri, qname, attr);
   }

   private String stripCDATAContent(String cdata) {
      if (!preserveSpace) {
         cdata = cdata.trim();
      }
      return cdata;
   }

   @Override
   public void endElement(String uri, String localname, String qname) {
      if (!nodes.empty()) {
         node = nodes.pop();
         if (buf != null) {
            String cdata = buf.toString();
            cdata = stripCDATAContent(cdata);
            if (!cdata.isEmpty()) {
               node.setCDATA(cdata);
            }
            buf = null;
         }
         node = node.getParent();
      }
   }

   @Override
   public void characters(char[] chararacters, int start, int length) {
      if (buf != null) {
         buf.append(chararacters, start, length);
      }
   }

   private QName getQName(String uri, String nodeName) {
      QName qname;
      int colonIndex = nodeName.indexOf(':');
      if (colonIndex != -1) {
         String localpart = nodeName.substring(colonIndex + 1);
         String prefix = nodeName.substring(0, colonIndex);
         if (prefixMappings.containsKey(prefix)) {
            qname = new QName(prefixMappings.get(prefix), localpart, prefix);
         } else {
            qname = new QName(XMLConstants.NULL_NS_URI, localpart, prefix);
         }
      } else {
         qname = new QName(nodeName);
      }
      return qname;
   }

   /**
    * Return the current node.
    *
    * @return the node
    */
   protected XMLNode getCurrentNode() {
      return node;
   }

   @Override
   public void startPrefixMapping(String prefix, String uri) {
      currentMappings.add(prefix);
      prefixMappings.put(prefix, uri);
   }

   @Override
   public void endPrefixMapping(String prefix) {
      currentMappings.remove(prefix);
      prefixMappings.remove(prefix);
   }

   /**
    * Create a node.
    *
    * @param qname the qualified name of the node
    * @param attr the node attributes
    * @return the node
    */
   protected XMLNode createNodeImpl(QName qname, Attributes attr) {
      XMLNode childNode;
      if (node == null) {
         if (asRoot) {
            if (keepLineNumbers) {
               if (comment == null) {
                  root = new XMLNumberedRoot(qname, locator.getLineNumber());
               } else {
                  root = new XMLCommentedRoot(qname, comment);
                  root.setLineNumber(locator.getLineNumber());
               }
            } else if (comment != null) {
               root = new XMLCommentedRoot(qname, comment);
            } else {
               root = new XMLRoot(qname);
            }
            ((XMLRoot) root).setEncoding(encoding);
         } else {
            if (keepLineNumbers) {
               if (comment == null) {
                  root = new XMLNumberedNode(qname, locator.getLineNumber());
               } else {
                  root = new XMLCommentedNode(qname, comment);
                  root.setLineNumber(locator.getLineNumber());
               }
            } else {
               if (comment == null) {
                  root = new XMLNode(qname);
               } else {
                  root = new XMLCommentedNode(qname);
               }
            }
         }
         childNode = root;
      } else {
         if (keepLineNumbers) {
            childNode = new XMLNumberedNode(qname, locator.getLineNumber());
         } else {
            childNode = new XMLNode(node, qname);
         }
      }
      Iterator<Entry<String, String>> it = prefixMappings.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> mapping = it.next();
         String prefix = mapping.getKey();
         if (currentMappings.contains(prefix)) {
            currentMappings.remove(prefix);
            childNode.bindPrefix(prefix, mapping.getValue());
         }
      }
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         int colonIndex = attrname.indexOf(':');
         QName aqname;
         if (colonIndex == -1) {
            aqname = new QName(attrname);
         } else {
            String localpart = attrname.substring(colonIndex + 1);
            String prefix = attrname.substring(0, colonIndex);
            if (prefixMappings.containsKey(localpart)) {
               if (prefix != null && !prefix.equals("xmlns")) {
                  aqname = new QName(prefixMappings.get(localpart), localpart, prefix);
               } else {
                  aqname = null;
               }
            } else {
               aqname = new QName(XMLConstants.NULL_NS_URI, localpart, prefix);
            }
         }
         if (aqname != null) {
            if (aqname.getNamespaceURI().equals(SCHEMA_INSTANCE) && aqname.getLocalPart().equals("schemaLocation")) {
               this.startPrefixMapping("", SCHEMA_INSTANCE);
            }
            childNode.addAttribute(aqname, attrvalue);
         }
      }
      return childNode;
   }

   /**
    * Parse a node.
    *
    * @param uri the uri
    * @param qname the node qualified name
    * @param attr the node attributes
    */
   private void parseElement(String uri, String name, Attributes attr) {
      XMLNode childNode;
      if (buf != null && node != null) {
         String cdata = buf.toString();
         cdata = stripCDATAContent(cdata);
         if (!cdata.isEmpty()) {
            node.setCDATA(cdata);
         }
         buf = null;
      }
      QName qname = getQName(uri, name);
      childNode = createNodeImpl(qname, attr);
      buf = new StringBuilder();
      if (node != null) {
         node.addChild(childNode);
      }
      nodes.push(childNode);
      node = childNode;
   }

   /**
    * Do nothing. This will only effectively be called if a fatal error has been acknowledged in {@link #fatalError(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   @Override
   protected void fatalErrorImpl(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.fatalError(e);
         } catch (SAXException ex) {
         }
      }
   }

   /**
    * Do nothing by default. This will only effectively be called if an error has been acknowledged in {@link #error(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   @Override
   protected void errorImpl(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.error(e);
         } catch (SAXException ex) {
         }
      }
   }

   /**
    * Do nothing. This will only effectively be called if a warning has been acknowledged in {@link #warning(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   @Override
   protected void warningImpl(SAXParseException e) {
      if (errorHandler != null) {
         try {
            errorHandler.warning(e);
         } catch (SAXException ex) {
         }
      }
   }
}
