/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class hold a local copy of several XML standard reference files. it contains:
 * <ul>
 * <li>A copy of the XML Schema DTD: <code>http://www.w3.org/2001/XMLSchema.dtd</code></li>
 * <li>A copy of the XML Schema file: <code>http://www.w3.org/2001/xml.xsd</code></li>
 * </ul>
 *
 * <p>
 * To define a resolver for these reference files, see {@link EntityListResolver#addResolvedSystemIDs(List, URL)}.
 * </p>
 *
 * @version 1.2.15
 * @see EntityListResolver
 */
public class XMLLocations {
   private static XMLLocations locations = null;
   private URL schemasDTD = null;
   private URL xmlXSD = null;
   private final String xmlXSDLocation = "http://www.w3.org/2001/xml.xsd";
   private final List<String> xmlXSDLocations = new ArrayList<>(2);
   private final String schemasDTDLocation = "http://www.w3.org/2001/XMLSchema.dtd";
   private final List<String> schemasDTDLocations = new ArrayList<>(2);

   private XMLLocations() {
      schemasDTD = XMLLocations.class.getResource("locations/XMLSchema.dtd");
      xmlXSD = XMLLocations.class.getResource("xml.xsd");
      xmlXSDLocations.add(xmlXSDLocation);
      xmlXSDLocations.add("xml.xsd");
      schemasDTDLocations.add(schemasDTDLocation);
      schemasDTDLocations.add("XMLSchema.dtd");
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static XMLLocations getInstance() {
      if (locations == null) {
         locations = new XMLLocations();
      }
      return locations;
   }

   /**
    * Return the local copy of the XML Schema DTD: <code>http://www.w3.org/2001/XMLSchema.dtd</code>.
    *
    * @return the local copy of the XML Schema DTD
    */
   public URL getXMLSchemaDTD() {
      return locations.schemasDTD;
   }

   /**
    * Return the local copy of the XML Schema file: <code>http://www.w3.org/2001/xml.xsd</code>.
    *
    * @return the local copy of the XML Schema file
    */
   public URL getXMLSchema() {
      return locations.xmlXSD;
   }

   /**
    * Return the list of URI locations for the XML Schema file. Currently the list of URIs is:
    * <ul>
    * <li><code>http://www.w3.org/2001/xml.xsd</code></li>
    * <li><code>xml.xsd</code></li>
    * </ul>
    *
    * @return the list of URI locations for the XML Schema file
    */
   public List<String> getXMLSchemaLocations() {
      return locations.xmlXSDLocations;
   }

   /**
    * Return the list of URI locations for the XML Schema DTD. Currently the list of URIs is:
    * <ul>
    * <li><code>http://www.w3.org/2001/XMLSchema.dtd</code></li>
    * <li><code>XMLSchema.dtd</code></li>
    * </ul>
    *
    * @return the list of URI locations for the XML Schema DTD
    */
   public List<String> getSchemaDTDLocations() {
      return locations.schemasDTDLocations;
   }
}
