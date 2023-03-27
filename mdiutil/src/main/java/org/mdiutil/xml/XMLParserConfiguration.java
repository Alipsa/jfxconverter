/*------------------------------------------------------------------------------
 * Copyright (C) 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The XMLParser configuration. It allows to configure in a simpler way a {@link XMLSAXParser}.
 *
 * @version 1.2.39
 * @see XMLSAXParser#setParserConfiguration(XMLParserConfiguration)
 */
public class XMLParserConfiguration {
   /**
    * {@value}: nestable connections are allowed.
    */
   public static final short ALLOW_NESTABLE_CONNECTIONS = 0;
   /**
    * {@value}: nestable connections are not allowed.
    */
   public static final short DONT_ALLOW_NESTABLE_CONNECTIONS = 1;
   /**
    * The XML encoding. Null by default.
    */
   public String encoding = null;
   /**
    * The EntityListResolver. Null by default.
    */
   public EntityListResolver resolver = null;
   /**
    * True if a default resource resolver should be set even if no resolver has been specified. The resolver will by default be able to resolve the
    * following resources:
    * <ul>
    * <li>http://www.w3.org/2001/xml.xsd</li>
    * <li>http://www.w3.org/2009/01/xml.xsd</li>
    * <li>http://www.w3.org/2007/08/xml.xsd</li>
    * <li>http://www.w3.org/2004/10/xml.xsd</li>
    * <li>http://www.w3.org/2001/03/xml.xsd</li>
    * <li>http://www.w3.org/2001/XMLSchema</li>
    * </ul>
    */
   public boolean hasDefaultLSResolver = false;

   /**
    * Specifies if nestable connections are allowed (ie, an xml file inside a zip file, for example). There are several
    * values:
    * <ul>
    * <li>{@link #ALLOW_NESTABLE_CONNECTIONS}: nestable connections are allowed</li>
    * <li>{@link #DONT_ALLOW_NESTABLE_CONNECTIONS}: nestable connections are not allowed</li>
    * <li>All other values: keep the default parser behavior</li>
    * </ul>
    */
   public short nestableConnectionsType = -1;
   /**
    * The parser exception listener. Null by default.
    */
   public ParserExceptionListener exceptionListener = null;
   /**
    * True if the parser is namespace-aware. False by default.
    */
   public boolean isNameSpaceAware = false;
   /**
    * True if the parser is validating. True by default.
    */
   public boolean isValidating = true;
   /**
    * True if the Parser uses a Schema Validator to validate XML files (true by default). If this parser does not use an XML Validator, the
    * validation will be performed by using an empty SAX Handler. True by default.
    */
   public boolean hasSchemaValidator = true;
   /**
    * True if the parser is XInclude-aware. True by default.
    */
   public boolean xIncludeAware = true;
   /**
    * True if xInclude content must be concatenated. This allows to parse a file which does content xInclude children with a parser which does not
    * handle xInclude content directly. False by default.
    */
   public boolean concatenateIncludes = false;
   /**
    * True if the parser shows the exceptions encountered during the parsing. True by default.
    */
   public boolean isShowingExceptions = true;
   /**
    * True if the parser shows the warnings encountered during the parsing. True by default.
    */
   public boolean isShowingWarnings = true;
   /**
    * The parser error dialog title. Null by default, which means that a default error title will be used.
    */
   public String errorTitle = null;
   /**
    * The default Locale for warnings and error messages.
    */
   public Locale locale = null;
   /**
    * The optional Schema URL to use for Schema validation. Null by default.
    */
   public URL schemaURL = null;
   /**
    * The default base directory which will be used for relative URLs if the parent URL is unknown. Null by default.
    */
   public URL defaultBaseDir = null;
   /**
    * The InputSource caching behavior.
    */
   public short cachingType = InputSourceCaching.CACHING_DEFAULT;   
   /**
    * The features of the parser factory.
    */
   private final Map<String, Boolean> features = new HashMap<>();
   /**
    * The properties of the parser.
    */
   private final Map<String, Object> properties = new HashMap<>();

   public XMLParserConfiguration() {
   }

   /**
    * Set a parser factory feature.
    *
    * @param name the feature name
    * @param value the feature value
    */
   public void setFeature(String name, boolean value) {
      features.put(name, value);
   }
   
   /**
    * Set the parser factory features.
    *
    * @param features the features
    */
   public void setFeatures(Map<String, Boolean> features) {
      this.features.clear();
      this.features.putAll(features);
   }   

   /**
    * Return the parser factory features.
    *
    * @return the features
    */
   public Map<String, Boolean> getFeatures() {
      return features;
   }

   /**
    * Set a parser property.
    *
    * @param name the property name
    * @param value the property value
    */
   public void setProperty(String name, Object value) {
      properties.put(name, value);
   }
   
   /**
    * Set the parser properties.
    *
    * @param properties the properties
    */
   public void setProperties(Map<String, Boolean> properties) {
      this.properties.clear();
      this.properties.putAll(properties);
   }      

   /**
    * Return the parser properties.
    *
    * @return the properties
    */
   public Map<String, Object> getProperties() {
      return properties;
   }
}
