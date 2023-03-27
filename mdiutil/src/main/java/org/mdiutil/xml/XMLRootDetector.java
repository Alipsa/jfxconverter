/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2016, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.mdiutil.xml.tree.XMLNode;

/**
 * This class detect the root element ofr only its name name for an XML file. Note that the class makes a best effort to catch any internal exceptions.
 *
 * @version 1.2.40
 */
public class XMLRootDetector {
   /**
    * The type for invalid result. It will only have this type before the first invocation.
    */
   public static final short INVALID = -1;
   /**
    * The type for correct result for the last invocation.
    */
   public static final short RESULT_OK = 0;
   /**
    * The type for no input result for the last invocation.
    */
   public static final short NO_INPUT = 1;
   /**
    * The type for invalid input result for the last invocation.
    */
   public static final short INVALID_INPUT = 2;
   private XMLInputFactory factory = null;
   private short resultType = INVALID;

   public XMLRootDetector() {
      factory = XMLInputFactory.newInstance();
   }

   private void close(InputStream stream) {
      try {
         if (stream != null) {
            stream.close();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void close(Reader reader) {
      try {
         if (reader != null) {
            reader.close();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Return the type of the last detection result. The values can be:
    * <ul>
    * <li>{@link #INVALID}: invalid result. It will only have this type before the first invocation.</li>
    * <li>{@link #RESULT_OK}: correct result</li>
    * <li>{@link #INVALID_INPUT}: invalid input (the input file is an invalid XML file)</li>
    * <li>{@link #NO_INPUT}: no input (the input file does not exist)</li>
    * </ul>
    *
    * @return he type of the last detection result
    */
   public short getLastResultType() {
      return resultType;
   }

   /**
    * Return true if the name of the root element of an XML file is the specified name.
    *
    * @param file the file
    * @param rootName the specified name of the root element to check
    * @return true if the name of the root element of an XML file is the specified name
    */
   public boolean checkRootName(File file, String rootName) {
      String name = getRootName(file);
      return name != null && name.equals(rootName);
   }

   /**
    * Return true if the qualified name of the root element of an XML file is the specified qualified name.
    *
    * @param file the file
    * @param rootName the specified qualified name of the root element to check
    * @return true if the qualified name of the root element of an XML file is the specified qualified name
    */
   public boolean checkRootQualifiedName(File file, QName rootName) {
      QName qname = getQualifiedRootName(file);
      return qname != null && qname.equals(rootName);
   }

   /**
    * Return true if the name of the root element of a Reader parsed as an XML file is the specified name.
    *
    * @param reader the Reader
    * @param rootName the specified name of the root element to check
    * @return true if the name of the root element of an XML file is the specified name
    */
   public boolean checkRootName(Reader reader, String rootName) {
      String name = getRootName(reader);
      return name != null && name.equals(rootName);
   }

   /**
    * Return true if the qualified name of the root element of a Reader parsed as an XML file is the specified qualified name.
    *
    * @param reader the Reader
    * @param rootName the specified qualified name of the root element to check
    * @return true if the name of the root element of an XML file is the specified qualified name
    */
   public boolean checkRootQualifiedName(Reader reader, QName rootName) {
      QName qname = getQualifiedRootName(reader);
      return qname != null && qname.equals(rootName);
   }

   /**
    * Return true if the name of the root element of an XML URL is the specified name.
    *
    * @param url the URL
    * @param rootName the specified name of the root element to check
    * @return true if the name of the root element of an XML file is the specified name
    */
   public boolean checkRootName(URL url, String rootName) {
      String name = getRootName(url);
      return name != null && name.equals(rootName);
   }

   /**
    * Return true if the qualified name of the root element of an XML URL is the specified qualified name.
    *
    * @param url the URL
    * @param rootName the specified qualified name of the root element to check
    * @return true if the qualified name of the root element of an XML file is the specified qualified name
    */
   public boolean checkRootName(URL url, QName rootName) {
      QName qname = getQualifiedRootName(url);
      return qname != null && qname.equals(rootName);
   }

   /**
    * Return the root element of a Reader parsed as an XML file.
    *
    * @param reader the Reader
    * @return the root element of the Reader parsed as an XML file
    */
   public XMLNode getRoot(Reader reader) {
      try {
         XMLEventReader xmlreader = factory.createXMLEventReader(reader);
         XMLNode node = readFirstNode(xmlreader);
         reader.close();
         return node;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(reader);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(reader);
         return null;
      }
   }

   /**
    * Return the root element of an XML URL.
    *
    * @param url the URL
    * @return the root element of an XML URL
    */
   public XMLNode getRoot(URL url) {
      InputStream stream = null;
      try {
         stream = url.openStream();
         XMLEventReader reader = factory.createXMLEventReader(stream);
         XMLNode node = readFirstNode(reader);
         reader.close();
         close(stream);
         return node;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   /**
    * Return the root element of an XML File.
    *
    * @param file the file
    * @return the root element
    */
   public XMLNode getRoot(File file) {
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(new FileInputStream(file));
         XMLEventReader reader = factory.createXMLEventReader(stream);
         XMLNode node = readFirstNode(reader);
         reader.close();
         close(stream);
         return node;
      } catch (FileNotFoundException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   /**
    * Return the name of the root element of an XML URL.
    *
    * @param url the URL
    * @return the name of the root element
    */
   public String getRootName(URL url) {
      InputStream stream = null;
      try {
         stream = url.openStream();
         XMLEventReader reader = factory.createXMLEventReader(stream);
         String eltName = readFirstElementName(reader);
         reader.close();
         close(stream);
         return eltName;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   /**
    * Return the name of the root element of a Reader parsed as an XML file.
    *
    * @param reader the Reader
    * @return the name of the root element
    */
   public String getRootName(Reader reader) {
      try {
         XMLEventReader xmlreader = factory.createXMLEventReader(reader);
         String eltName = readFirstElementName(xmlreader);
         reader.close();
         return eltName;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(reader);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(reader);
         return null;
      }
   }

   /**
    * Return the name of the root element of an XML File.
    *
    * @param file the file
    * @return the name of the root element
    */
   public String getRootName(File file) {
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(new FileInputStream(file));
         XMLEventReader reader = factory.createXMLEventReader(stream);
         String eltName = readFirstElementName(reader);
         reader.close();
         close(stream);
         return eltName;
      } catch (FileNotFoundException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   /**
    * Return the qualified name of the root element of an XML URL.
    *
    * @param url the URL
    * @return the qualified name of the root element
    */
   public QName getQualifiedRootName(URL url) {
      InputStream stream = null;
      try {
         stream = url.openStream();
         XMLEventReader reader = factory.createXMLEventReader(stream);
         QName qname = readFirstElementQName(reader);
         reader.close();
         close(stream);
         return qname;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   /**
    * Return the qualified name of the root element of a Reader parsed as an XML file.
    *
    * @param reader the Reader
    * @return the qualified name of the root element
    */
   public QName getQualifiedRootName(Reader reader) {
      try {
         XMLEventReader xmlreader = factory.createXMLEventReader(reader);
         QName qName = readFirstElementQName(xmlreader);
         reader.close();
         return qName;
      } catch (IOException ex) {
         resultType = NO_INPUT;
         close(reader);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(reader);
         return null;
      }
   }

   /**
    * Return the qualified name of the root element of an XML File.
    *
    * @param file the file
    * @return the qualified name of the root element
    */
   public QName getQualifiedRootName(File file) {
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(new FileInputStream(file));
         XMLEventReader reader = factory.createXMLEventReader(stream);
         QName qName = readFirstElementQName(reader);
         reader.close();
         close(stream);
         return qName;
      } catch (FileNotFoundException ex) {
         resultType = NO_INPUT;
         close(stream);
         return null;
      } catch (XMLStreamException ex) {
         resultType = INVALID_INPUT;
         close(stream);
         return null;
      }
   }

   private QName readFirstElementQName(XMLEventReader reader) throws XMLStreamException {
      while (reader.hasNext()) {
         XMLEvent xmlevt = reader.nextEvent();
         if (xmlevt.isStartElement()) {
            StartElement elt = xmlevt.asStartElement();
            QName qname = elt.getName();
            return qname;
         }
      }
      return null;
   }

   private String readFirstElementName(XMLEventReader reader) throws XMLStreamException {
      while (reader.hasNext()) {
         XMLEvent xmlevt = reader.nextEvent();
         if (xmlevt.isStartElement()) {
            StartElement elt = xmlevt.asStartElement();
            String name = elt.getName().getLocalPart();
            return name;
         }
      }
      return null;
   }

   private XMLNode readFirstNode(XMLEventReader reader) throws XMLStreamException {
      while (reader.hasNext()) {
         XMLEvent xmlevt = reader.nextEvent();
         if (xmlevt.isStartElement()) {
            StartElement elt = xmlevt.asStartElement();
            XMLNode node = parseElement(elt);
            return node;
         }
      }
      return null;
   }

   private XMLNode parseElement(StartElement elt) {
      XMLNode node = new XMLNode(elt.getName());
      Iterator<Attribute> it = elt.getAttributes();
      while (it.hasNext()) {
         Attribute attr = it.next();
         QName qname = attr.getName();
         String attrvalue = attr.getValue();
         node.addAttribute(qname, attrvalue);
      }
      return node;
   }
}
