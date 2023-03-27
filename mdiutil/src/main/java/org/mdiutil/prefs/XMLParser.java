/* Copyright (C) Sun Microsystems
 * Copyright (C) 2007, 2015 Herve Girod
 *
 * Distributable under the terms of the GNU Lesser General Public License,
 as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mdiutil.xml.XmlWriter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML Support for java.util.prefs. Methods to import and export preference
 * nodes and subtrees.
 *
 * <p>
 * FIXED : It was necessary to create this class (which does the same thing as its java
 * counterpart, in the java.util.prefs package, because the java XmlSupport class does
 * create "java" Preferences by default when an XML tree is imported, which result in the creation
 * of a node in the default java backing store.</p>
 * <p>
 * For example, in Windows, the backing store is the Windows registry, which is modified even
 * if a custom PreferencesFactory is used.</p>
 *
 * @version 0.8.1
 */
class XMLParser {
   // The required DTD URI for exported preferences
   private static final String PREFS_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";

   // The actual DTD corresponding to the URI
   private static final String PREFS_DTD
      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<!-- DTD for preferences -->"
      + "<!ELEMENT preferences (root) >"
      + "<!ATTLIST preferences"
      + " EXTERNAL_XML_VERSION CDATA \"0.0\"  >"
      + "<!ELEMENT root (map, node*) >"
      + "<!ATTLIST root"
      + "          type (system|user) #REQUIRED >"
      + "<!ELEMENT node (map, node*) >"
      + "<!ATTLIST node"
      + "          name CDATA #REQUIRED >"
      + "<!ELEMENT map (entry*) >"
      + "<!ATTLIST map"
      + "  MAP_XML_VERSION CDATA \"0.0\"  >"
      + "<!ELEMENT entry EMPTY >"
      + "<!ATTLIST entry"
      + "          key CDATA #REQUIRED"
      + "          value CDATA #REQUIRED >";
   /**
    * Version number for the format exported preferences files.
    */
   private static final String EXTERNAL_XML_VERSION = "1.0";

   /*
    * Version number for the internal map files.
    */
   private static final String MAP_XML_VERSION = "1.0";

   /**
    * Export the specified preferences node and, if subTree is true, all
    * subnodes, to the specified output stream. Preferences are exported as
    * an XML document conforming to the definition in the Preferences spec.
    *
    * @throws IOException if writing to the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws BackingStoreException if preference data cannot be read from
    * backing store.
    * @throws IllegalStateException if this node (or an ancestor) has been
    * removed with the {@link #removeNode()} method.
    */
   static void export(OutputStream os, final NetworkPreferences p, boolean subTree)
      throws IOException, BackingStoreException {

      if (p.isInRemoveLock()) {
         throw new IllegalStateException("Node has been removed");
      }

      try {
         DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = fac.newDocumentBuilder();
         DOMImplementation impl = builder.getDOMImplementation();
         DocumentType type = impl.createDocumentType("preferences", null, PREFS_DTD_URI);
         Document doc = impl.createDocument(null, "preferences", type);
         Element preferences = doc.getDocumentElement();

         /*
          Element preferences =  (Element)
          root.appendChild(doc.createElement("preferences"));
          */
         preferences.setAttribute("EXTERNAL_XML_VERSION", EXTERNAL_XML_VERSION);
         Element xmlRoot = (Element) preferences.appendChild(doc.createElement("root"));
         xmlRoot.setAttribute("type", (p.isUserNode() ? "user" : "system"));

         // Get bottom-up list of nodes from p to root, excluding root
         List ancestors = new ArrayList();

         NetworkPreferences kid = p;
         NetworkPreferences dad = kid.getParent();
         while (dad != null) {
            kid = dad;
            dad = kid.getParent();
            ancestors.add(kid);
         }

         Element e = xmlRoot;
         for (int i = ancestors.size() - 1; i >= 0; i--) {
            e.appendChild(doc.createElement("map"));
            e = (Element) e.appendChild(doc.createElement("node"));
            e.setAttribute("name", ((Preferences) ancestors.get(i)).name());
         }
         putPreferencesInXml(e, doc, p, subTree);
         XmlWriter writer = new XmlWriter();
         writer.writeDocument(doc, new OutputStreamWriter(os));
      } catch (ParserConfigurationException e) {
         throw new IOException(e.getMessage());
      }
   }

   /**
    * Put the preferences in the specified Preferences node into the
    * specified XML element which is assumed to represent a node
    * in the specified XML document which is assumed to conform to
    * PREFS_DTD. If subTree is true, create children of the specified
    * XML node conforming to all of the children of the specified
    * Preferences node and recurse.
    *
    * @throws BackingStoreException if it is not possible to read
    * the preferences or children out of the specified
    * preferences node.
    */
   private static void putPreferencesInXml(Element elt, Document doc,
      NetworkPreferences prefs, boolean subTree) throws BackingStoreException {
      NetworkPreferences[] kidsCopy = null;
      String[] kidNames = null;

      // Node is locked to export its contents and get a
      // copy of children, then lock is released,
      // and, if subTree = true, recursive calls are made on children
      synchronized (((NetworkPreferences) prefs).getLock()) {
         // Check if this node was concurrently removed. If yes
         // remove it from XML Document and return.
         if (prefs.isInRemoveLock()) {
            elt.getParentNode().removeChild(elt);
            return;
         }
         // Put map in xml element
         String[] keys = prefs.keys();
         Element map = (Element) elt.appendChild(doc.createElement("map"));
         for (int i = 0; i < keys.length; i++) {
            Element entry = (Element) map.appendChild(doc.createElement("entry"));
            entry.setAttribute("key", keys[i]);
            // NEXT STATEMENT THROWS NULL PTR EXC INSTEAD OF ASSERT FAIL
            entry.setAttribute("value", prefs.get(keys[i], null));
         }
         // Recurse if appropriate
         if (subTree) {
            /* Get a copy of kids while lock is held */
            kidNames = prefs.childrenNames();
            kidsCopy = new NetworkPreferences[kidNames.length];
            for (int i = 0; i < kidNames.length; i++) {
               kidsCopy[i] = (NetworkPreferences) (prefs.node(kidNames[i]));
            }
         }
         // release lock
      }

      if (subTree) {
         for (int i = 0; i < kidNames.length; i++) {
            Element xmlKid = (Element) elt.appendChild(doc.createElement("node"));
            xmlKid.setAttribute("name", kidNames[i]);
            putPreferencesInXml(xmlKid, doc, kidsCopy[i], subTree);
         }
      }
   }

   /**
    * Import preferences from the specified input stream, which is assumed
    * to contain an XML document in the format described in the Preferences
    * spec.
    *
    * @throws IOException if reading from the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws InvalidPreferencesFormatException Data on input stream does not
    * constitute a valid XML document with the mandated document type.
    */
   static void importPreferences(File file) throws IOException, InvalidPreferencesFormatException {
      try {
         Document doc = load(new FileInputStream(file));
         String xmlVersion = ((Element) doc.getChildNodes().item(1)).getAttribute("EXTERNAL_XML_VERSION");
         if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0) {
            throw new InvalidPreferencesFormatException(
               "Exported preferences file format version " + xmlVersion
               + " is not supported. This java installation can read"
               + " versions " + EXTERNAL_XML_VERSION + " or older. You may need"
               + " to install a newer version of JDK.");
         }

         Element xmlRoot = (Element) doc.getChildNodes().item(1).getChildNodes().item(0);

         NetworkPreferences prefsRoot;
         if (xmlRoot.getAttribute("type").equals("user")) {
            prefsRoot = (NetworkPreferences) (NetworkPreferencesFactory.getFactory().getInternalUserRoot(file));
         } else {
            prefsRoot = (NetworkPreferences) (NetworkPreferencesFactory.getFactory().getInternalSystemRoot(file));
         }
         importSubtree(prefsRoot, xmlRoot);
      } catch (SAXException e) {
         throw new InvalidPreferencesFormatException(e);
      }
   }

   /**
    * Import preferences from the specified input stream, which is assumed
    * to contain an XML document in the format described in the Preferences
    * spec.
    *
    * @throws IOException if reading from the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws InvalidPreferencesFormatException Data on input stream does not
    * constitute a valid XML document with the mandated document type.
    */
   static void importPreferences(InputStream is) throws IOException, InvalidPreferencesFormatException {
      try {
         Document doc = load(is);
         String xmlVersion = ((Element) doc.getChildNodes().item(1)).getAttribute("EXTERNAL_XML_VERSION");
         if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0) {
            throw new InvalidPreferencesFormatException(
               "Exported preferences file format version " + xmlVersion
               + " is not supported. This java installation can read"
               + " versions " + EXTERNAL_XML_VERSION + " or older. You may need"
               + " to install a newer version of JDK.");
         }

         Element xmlRoot = (Element) doc.getChildNodes().item(1).getChildNodes().item(0);

         NetworkPreferences prefsRoot;
         if (xmlRoot.getAttribute("type").equals("user")) {
            prefsRoot = (NetworkPreferences) (NetworkPreferencesFactory.getFactory().getInternalUserRoot(null));
         } else {
            prefsRoot = (NetworkPreferences) (NetworkPreferencesFactory.getFactory().getInternalSystemRoot(null));
         }
         importSubtree(prefsRoot, xmlRoot);
      } catch (SAXException e) {
         throw new InvalidPreferencesFormatException(e);
      }
   }

   /**
    * Load an XML document from specified input stream, which must
    * have the requisite DTD URI.
    */
   private static Document load(InputStream in) throws SAXException, IOException {
      Resolver r = new Resolver();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringElementContentWhitespace(true);
      dbf.setValidating(true);
      dbf.setCoalescing(true);
      dbf.setIgnoringComments(true);
      try {
         DocumentBuilder db = dbf.newDocumentBuilder();
         db.setEntityResolver(new Resolver());
         db.setErrorHandler(new EH());
         return db.parse(new InputSource(in));
      } catch (ParserConfigurationException x) {
         throw new Error(x);
      }
   }

   /**
    * Recursively traverse the specified preferences node and store
    * the described preferences into the system or current user
    * preferences tree, as appropriate.
    */
   private static void importSubtree(NetworkPreferences prefsNode, Element xmlNode) {
      NodeList xmlKids = xmlNode.getChildNodes();
      int numXmlKids = xmlKids.getLength();
      /*
       * We first lock the node, import its contents and get
       * child nodes. Then we unlock the node and go to children
       * Since some of the children might have been concurrently
       * deleted we check for this.
       */
      NetworkPreferences[] prefsKids;
      /* Lock the node */
      synchronized (prefsNode.getLock()) {
         //If removed, return silently
         if (prefsNode.isInRemoveLock()) {
            return;
         }

         // Import any preferences at this node
         Element firstXmlKid = (Element) xmlKids.item(0);
         importPrefs(prefsNode, firstXmlKid);
         prefsKids = new NetworkPreferences[numXmlKids - 1];

         // Get involved children
         for (int i = 1; i < numXmlKids; i++) {
            Element xmlKid = (Element) xmlKids.item(i);
            prefsKids[i - 1] = new NetworkPreferences(prefsNode, xmlKid.getAttribute("name"));
         }
      } // unlocked the node
      // import children
      for (int i = 1; i < numXmlKids; i++) {
         importSubtree(prefsKids[i - 1], (Element) xmlKids.item(i));
      }
   }

   /**
    * Import the preferences described by the specified XML element
    * (a map from a preferences document) into the specified
    *
    * preferences node.
    */
   private static void importPrefs(Preferences prefsNode, Element map) {
      NodeList entries = map.getChildNodes();

      for (int i = 0, numEntries = entries.getLength(); i < numEntries; i++) {
         Element entry = (Element) entries.item(i);
         prefsNode.put(entry.getAttribute("key"),
            entry.getAttribute("value"));
      }
   }

   /**
    * Export the specified Map<String,String> to a map document on
    * the specified OutputStream as per the prefs DTD. This is used
    * as the internal (undocumented) format for FileSystemPrefs.
    *
    * @throws IOException if writing to the specified output stream
    * results in an <tt>IOException</tt>.
    */
   static void exportMap(OutputStream os, Map map) throws IOException {
      try {
         DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = fac.newDocumentBuilder();
         DOMImplementation impl = builder.getDOMImplementation();
         Document doc = impl.createDocument("", "prefs", null);

         Element xmlMap = (Element) doc.appendChild(doc.createElement("map"));
         xmlMap.setAttribute("MAP_XML_VERSION", MAP_XML_VERSION);

         for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            Element xe = (Element) xmlMap.appendChild(doc.createElement("entry"));
            xe.setAttribute("key", (String) e.getKey());
            xe.setAttribute("value", (String) e.getValue());
         }

         XmlWriter writer = new XmlWriter(null, PREFS_DTD_URI, null);
         writer.writeXml(doc, new OutputStreamWriter(os));
      } catch (ParserConfigurationException e) {
         throw new IOException(e.getMessage());
      }
   }

   /**
    * Import Map from the specified input stream, which is assumed
    * to contain a map document as per the prefs DTD. This is used
    * as the internal (undocumented) format for FileSystemPrefs. The
    * key-value pairs specified in the XML document will be put into
    * the specified Map. (If this Map is empty, it will contain exactly
    * the key-value pairs int the XML-document when this method returns.)
    *
    * @throws IOException if reading from the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws InvalidPreferencesFormatException Data on input stream does not
    * constitute a valid XML document with the mandated document type.
    */
   static void importMap(InputStream is, Map m)
      throws IOException, InvalidPreferencesFormatException {
      try {
         Document doc = load(is);
         Element xmlMap = (Element) doc.getChildNodes().item(1);
         // check version
         String mapVersion = xmlMap.getAttribute("MAP_XML_VERSION");
         if (mapVersion.compareTo(MAP_XML_VERSION) > 0) {
            throw new InvalidPreferencesFormatException(
               "Preferences map file format version " + mapVersion
               + " is not supported. This java installation can read"
               + " versions " + MAP_XML_VERSION + " or older. You may need"
               + " to install a newer version of JDK.");
         }

         NodeList entries = xmlMap.getChildNodes();
         for (int i = 0, numEntries = entries.getLength(); i < numEntries; i++) {
            Element entry = (Element) entries.item(i);
            m.put(entry.getAttribute("key"), entry.getAttribute("value"));
         }
      } catch (SAXException e) {
         throw new InvalidPreferencesFormatException(e);
      }
   }

   private static class Resolver implements EntityResolver {
      public InputSource resolveEntity(String pid, String sid)
         throws SAXException {
         if (sid.equals(PREFS_DTD_URI)) {
            InputSource is;
            is = new InputSource(new StringReader(PREFS_DTD));
            is.setSystemId(PREFS_DTD_URI);
            return is;
         }
         throw new SAXException("Invalid system identifier: " + sid);
      }
   }

   private static class EH implements ErrorHandler {
      @Override
      public void error(SAXParseException x) throws SAXException {
         throw x;
      }

      @Override
      public void fatalError(SAXParseException x) throws SAXException {
         throw x;
      }

      @Override
      public void warning(SAXParseException x) throws SAXException {
         throw x;
      }
   }
}
