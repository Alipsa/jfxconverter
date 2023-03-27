/*------------------------------------------------------------------------------
 * Copyright (C) 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class is used to filter elements in the {@link XMLNodesIterator}.
 *
 * @since 1.2.44
 */
public class NodeFilter {
   private final String nodeName;
   private final String filterName;
   private final Map<String, String> attributeFilters = new HashMap<>();

   /**
    * Constructor.
    *
    * @param filterName the filter name
    * @param nodeName the name of the node on which the filter is applied
    */
   public NodeFilter(String filterName, String nodeName) {
      this.filterName = filterName;
      this.nodeName = nodeName;
   }

   /**
    * Return the filter name.
    *
    * @return the filter name
    */
   public String getFilterName() {
      return filterName;
   }

   /**
    * Return the name of the node on which the filter is applied.
    *
    * @return the the name
    */
   public String getNodeName() {
      return nodeName;
   }

   /**
    * Add a filer on an attribute.
    *
    * @param attrName the attribute name
    * @param attrValue the attribute value
    */
   public void addAttributeFilter(String attrName, String attrValue) {
      attributeFilters.put(attrName, attrValue);
   }

   /**
    * Return the filters on attributes
    *
    * @return the filters
    */
   public Map<String, String> getAttributeFilters() {
      return attributeFilters;
   }

   /**
    * Return true if this filter is compatible with a specified node.
    *
    * @param node the node
    * @return true if this filter is compatible with the node
    */
   public boolean isCompatibleWithNode(XMLNode node) {
      if (node.getPrefixedName().equals(nodeName)) {
         if (attributeFilters.isEmpty()) {
            return true;
         } else {
            boolean isCompatible = true;
            Iterator<Entry<String, String>> it = attributeFilters.entrySet().iterator();
            while (it.hasNext()) {
               Entry<String, String> entry = it.next();
               String attrName = entry.getKey();
               String attrValue = entry.getValue();
               if (!node.hasAttribute(attrName)) {
                  isCompatible = false;
                  break;
               } else {
                  String theValue = node.getAttributeValue(attrName);
                  if (!theValue.equals(attrValue)) {
                     isCompatible = false;
                     break;
                  }
               }
            }
            return isCompatible;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 79 * hash + Objects.hashCode(this.nodeName);
      hash = 79 * hash + Objects.hashCode(this.attributeFilters);
      return hash;
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
      final NodeFilter other = (NodeFilter) obj;
      if (!Objects.equals(this.nodeName, other.nodeName)) {
         return false;
      }
      if (!Objects.equals(this.attributeFilters, other.attributeFilters)) {
         return false;
      }
      return true;
   }
}
