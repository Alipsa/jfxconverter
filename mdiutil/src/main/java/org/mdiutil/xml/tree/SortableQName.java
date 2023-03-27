/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * A sortable QName.
 *
 * @version 1.2.39.4
 */
public class SortableQName implements Serializable, Comparable<SortableQName> {
   // we use the same serial UID as the QName class
   private static final long serialVersionUID = -9120448754896609940L;
   private QName qname;

   /**
    * Constructor.
    *
    * @param qname the qualified name
    *
    * @throws IllegalArgumentException When <code>localPart</code> is <code>null</code>
    */
   public SortableQName(QName qname) {
      this.qname = qname;
   }

   /**
    * Constructor specifying the Namespace URI and local part.
    *
    * @param namespaceURI Namespace URI of the <code>QName</code>
    * @param localPart local part of the <code>QName</code>
    *
    * @throws IllegalArgumentException When <code>localPart</code> is <code>null</code>
    */
   public SortableQName(final String namespaceURI, final String localPart) {
      this.qname = new QName(namespaceURI, localPart);
   }

   /**
    * Constructor specifying the Namespace URI, local part and prefix.
    *
    * @param namespaceURI Namespace URI of the <code>QName</code>
    * @param localPart local part of the <code>QName</code>
    * @param prefix prefix of the <code>QName</code>
    *
    * @throws IllegalArgumentException When <code>localPart</code> or <code>prefix</code> is <code>null</code>
    */
   public SortableQName(String namespaceURI, String localPart, String prefix) {
      this.qname = new QName(namespaceURI, localPart, prefix);
   }

   /**
    * Constructor specifying the name.
    *
    * @param name the name can be only the local part, or also contain the prefix.
    *
    * @throws IllegalArgumentException When <code>name</code> is <code>null</code>
    */
   public SortableQName(String name) {
      int index = name.indexOf(':');
      if (index == -1) {
         this.qname = new QName(name);
      } else {
         String prefix = name.substring(0, index);
         String localPart = name.substring(index + 1);
         this.qname = new QName(XMLConstants.NULL_NS_URI, localPart, prefix);
      }
   }

   /**
    * Return the underlying qualified name.
    *
    * @return the underlying qualified name
    */
   public QName getQName() {
      return qname;
   }

   /**
    * Return the Namespace URI of the <code>QName</code>.
    *
    * @return the Namespace URI of the <code>QName</code>
    */
   public String getNamespaceURI() {
      return qname.getNamespaceURI();
   }

   /**
    * Return the local part of the <code>QName</code>.
    *
    * @return the local part of the <code>QName</code>
    */
   public String getLocalPart() {
      return qname.getLocalPart();
   }

   /**
    * Return the prefix of this <code>QName</code>.
    *
    * @return the prefix of this <code>QName</code>
    */
   public String getPrefix() {
      return qname.getPrefix();
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 29 * hash + Objects.hashCode(this.qname);
      return hash;
   }

   @Override
   public String toString() {
      if (getPrefix() == null) {
         return getLocalPart();
      } else {
         return getPrefix() + ":" + getLocalPart();
      }
   }

   /**
    * Compare two qualified names including the prefix in the comparison.
    *
    * @param qname1 the first qualified name
    * @param qname2 the second qualified name
    * @return true if the two qualified names are equal includiong their prefix
    */
   public static boolean compareQNames(QName qname1, QName qname2) {
      if (!Objects.equals(qname1, qname2)) {
         return false;
      }
      String prefix = qname1.getPrefix();
      String otherPrefix = qname2.getPrefix();
      if (!Objects.equals(prefix, otherPrefix)) {
         return false;
      }
      return true;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final SortableQName other = (SortableQName) obj;
      if (!Objects.equals(this.qname, other.qname)) {
         return false;
      }
      String prefix = this.qname.getPrefix();
      String otherPrefix = other.qname.getPrefix();
      if (!Objects.equals(prefix, otherPrefix)) {
         return false;
      }
      return true;
   }

   @Override
   public int compareTo(SortableQName qname) {
      String uri = qname.getNamespaceURI();
      if (uri != null && uri.equals(XMLConstants.NULL_NS_URI)) {
         uri = null;
      }
      String myURI = this.getNamespaceURI();
      if (myURI != null && myURI.equals(XMLConstants.NULL_NS_URI)) {
         myURI = null;
      }
      String prefix = qname.getPrefix();
      if (prefix != null && prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         prefix = null;
      }
      String myPrefix = this.getPrefix();
      if (myPrefix != null && myPrefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         myPrefix = null;
      }
      if (myURI != null && uri == null) {
         return 1;
      } else if (myURI == null && uri != null) {
         return -1;
      } else if (myURI != null && uri != null) {
         if (!myURI.equals(uri)) {
            return myURI.compareTo(uri);
         }
      }
      if (myPrefix != null && prefix == null) {
         return 1;
      } else if (myPrefix == null && prefix != null) {
         return -1;
      } else if (myPrefix != null && prefix != null) {
         if (!myPrefix.equals(prefix)) {
            return myPrefix.compareTo(prefix);
         }
      }
      String localPart = qname.getLocalPart();
      String myLocalPart = this.getLocalPart();
      return myLocalPart.compareTo(localPart);
   }
}
