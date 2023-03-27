/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.mdiutil.io.BufferedReaderFactory;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.io.ReaderProvider;
import org.mdiutil.lang.SystemUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * A generic XML SAX Parser. The underlying parser can be configured for XML validation.
 *
 * <h1>Examples</h1>
 * <h2>Create a validating parser using a DTD</h2>
 * <pre>
 *   XMLSAXParser parser = new XMLSAXParser("My Parser");
 *   parser.setValidating(true);
 *   parser.setHandlerDTD(dtd);
 *   parser.setHandler(handler);
 *   parse.parse(file);
 * </pre>
 *
 * <h2>Create a validating parser using a Schema</h2>
 * <pre>
 *   XMLSAXParser parser = new XMLSAXParser("My Parser");
 *   parser.setValidating(true);
 *   parser.setSchema(schema);
 *   parser.setHandler(handler);
 *   parse.parse(file);
 * </pre>
 *
 * <h2>Create a validating parser and get the parsing exceptions</h2>
 * <pre>
 *   XMLSAXParser parser = new XMLSAXParser("My Parser");
 *   parser.setValidating(true);
 *   parser.setSchema(schema);
 *   parser.setHandler(handler);
 *   parser.showExceptions(false);
 *   parse.parse(file);
 *
 *   if (handler.hasParserExceptions()) {
 *     List&gt;ResolverSAXHandler.ExceptionResult&gt; results = handler.getExceptionResults();
 *   }
 * </pre>
 *
 * @version 1.2.40
 */
public class XMLSAXParser implements InputSourceCaching {
   private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   /**
    * The associated SAX handler.
    */
   protected ResolverSAXHandler handler;
   /**
    * The associated entity list resolver.
    */
   protected EntityListResolver resolver;
   /**
    * The features of the parser factory.
    */
   protected Map<String, Boolean> features = new HashMap<>();
   /**
    * The properties of the parser.
    */
   protected Map<String, Object> properties = new HashMap<>();
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
   protected boolean hasDefaultLSResolver = false;
   /**
    * The DTD used to validate the parsing.
    */
   protected URL dtd = null;
   /**
    * The public ID used to validate the parsing.
    */
   protected String publicID = null;
   /**
    * True if the parser is validating.
    */
   protected boolean isValidating = true;
   /**
    * True if the parser should show exceptions.
    */
   protected boolean isShowingExceptions = true;
   /**
    * True if the parser should show warnings.
    */
   protected boolean isShowingWarnings = true;
   /**
    * True if the parser is XML-Schema aware.
    */
   protected boolean isSchemaAware = false;
   /**
    * The parser factory implementation class name.
    */
   private static String FACTORY_CLASS_NAME = null;
   /**
    * The InputSource caching behavior.
    */
   private short cachingType = InputSourceCaching.CACHING_DEFAULT;
   /**
    * The default error title used for the dialog.
    */
   public final static String DEFAULT_ERROR_TITLE = "Parser Exception";
   /**
    * The error title.
    */
   protected String errorTitle = DEFAULT_ERROR_TITLE;
   /**
    * The first throwable thrown by the parser, if the parsing failed beca use of an exception in the handler. Note that SAXExceptions are handled
    * differently.
    */
   protected Throwable firstException = null;
   /**
    * The URL of the schema used for parsing the content, if a Schema is used for the parsing.
    */
   protected URL schemaURL = null;
   /**
    * True if the parser is namespace-aware.
    */
   protected boolean isNameSpaceAware = false;
   /**
    * True if the parser keep the prefixes.
    */
   protected boolean keepPrefixes = false;
   /**
    * The encoding. Can be null if it is not forced. It basically call <code>setEncoding(String)</code> on the <code>InputSource</code>.
    */
   protected String encoding = null;
   private static boolean defaultAllowNestableConnections = false;
   private static boolean revertOldReaderBehavior = false;
   private boolean allowNestableConnections = false;
   private Charset charset = Charset.forName("UTF-8");
   private boolean stoppedParsing = false;
   private Object stoppedParsingContent = null;
   /**
    * The listener which is fired if exceptions (including SAXExceptions) are encountered during the parsing.
    */
   protected ParserExceptionListener exListener = null;
   private boolean hasSchemaValidator = true;
   private URL parsedURL = null;
   private URL defaultBaseDir = null;
   private boolean xIncludeAware = true;
   private boolean xIncludeSupported = true;
   private boolean concatenateIncludes = false;
   private boolean resolverSetUp = false;
   private XMLIncluder includer = null;
   private Reader reader = null;
   private boolean readerIsClosed = false;
   private ClassLoader loader = null;
   private static Locale DEFAULT_LOCALE = null;
   private Locale locale = null;

   /**
    * Create a new non validating XML SAX Parser.
    */
   public XMLSAXParser() {
      this.allowNestableConnections = defaultAllowNestableConnections;
      this.locale = DEFAULT_LOCALE;
   }

   /**
    * Create a new non validating XML SAX Parser.
    *
    * @param handler the SAX Handler
    */
   public XMLSAXParser(ResolverSAXHandler handler) {
      this();
      this.handler = handler;
      this.locale = DEFAULT_LOCALE;
   }

   /**
    * Create a new non validating XML SAX Parser. The default title is "Parser Exception".
    *
    * @param errorTitle error title
    */
   public XMLSAXParser(String errorTitle) {
      this();
      this.errorTitle = errorTitle;
      this.locale = DEFAULT_LOCALE;
   }

   /**
    * Create a new XML SAX Parser.
    *
    * @param isValidating true if the parser is validating
    */
   public XMLSAXParser(boolean isValidating) {
      this();
      this.isValidating = isValidating;
      this.locale = DEFAULT_LOCALE;
   }

   /**
    * Create a new XML SAX Parser.
    *
    * @param errorTitle error title
    * @param isValidating true if the parser is validating
    */
   public XMLSAXParser(String errorTitle, boolean isValidating) {
      this();
      this.isValidating = isValidating;
      this.errorTitle = errorTitle;
      this.locale = DEFAULT_LOCALE;
   }

   /**
    * Set the ClassLoader. It will only be used if the {@link #isRevertingOldReaderBehavior()} is false.
    *
    * @param loader the ClassLoader
    */
   public void setClassLoader(ClassLoader loader) {
      this.loader = loader;
   }

   /**
    * Return the ClassLoader. It will only be used if the {@link #isRevertingOldReaderBehavior()} is false.
    *
    * @return the ClassLoader
    */
   public ClassLoader getClassLoader() {
      return loader;
   }

   /**
    * Set the default locale for all subsequent SAX and validation error messages.
    *
    * @param locale the default locale
    */
   public static void setDefaultLocale(Locale locale) {
      DEFAULT_LOCALE = locale;
   }

   /**
    * Set the locale for SAX and validation error messages.
    *
    * @param locale the locale
    */
   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   /**
    * Return the locale for SAX and validation error messages.
    *
    * @return the locale
    */
   public Locale getLocale() {
      return locale;
   }

   /**
    * Set if a default LSResourceResolver must be used even if no specific resolver has been set. This resolver will by default be able to resolve the
    * following resources:
    * <ul>
    * <li>http://www.w3.org/2001/xml.xsd</li>
    * <li>http://www.w3.org/2009/01/xml.xsd</li>
    * <li>http://www.w3.org/2007/08/xml.xsd</li>
    * <li>http://www.w3.org/2004/10/xml.xsd</li>
    * <li>http://www.w3.org/2001/03/xml.xsd</li>
    * <li>http://www.w3.org/2001/XMLSchema</li>
    * </ul>
    *
    * @param hasDefaultLSResolver true if a default LSResourceResolver must be used even if no specific resolver has been set
    * @see EntityListResolver
    */
   public void setDefaultLSResourceResolver(boolean hasDefaultLSResolver) {
      this.hasDefaultLSResolver = hasDefaultLSResolver;
   }

   /**
    * Return true if a default LSResourceResolver must be used even if no specific resolver has been set
    *
    * @return if a default LSResourceResolver must be used even if no specific resolver has been set
    * @see #setDefaultLSResourceResolver(boolean)
    */
   public boolean hasDefaultLSResourceResolver() {
      return hasDefaultLSResolver;
   }

   /**
    * Set if nestable connections are allowed by default (ie, an xml file inside a zip file, for example).
    *
    * @param allow true if nestable connections are allowed by default
    */
   public static void setDefaultAllowNestableConnections(boolean allow) {
      defaultAllowNestableConnections = allow;
   }

   private void setupCachingType() {
      if (resolver != null) {
         switch (cachingType) {
            case CACHING_SOURCES:
               resolver.cacheSources(true);
               break;
            case NOT_CACHING_SOURCES:
               resolver.cacheSources(false);
               break;
         }
      }
      if (handler != null) {
         handler.setInputSourceCaching(cachingType);
      }
   }

   /**
    * Set the InputSources caching behavior.
    *
    * @param cachingType the caching type
    */
   @Override
   public void setInputSourceCaching(short cachingType) {
      this.cachingType = cachingType;
      setupCachingType();
   }

   /**
    * Return the InputSources caching behavior.
    *
    * @return the caching type
    */
   @Override
   public short getInputSourceCaching() {
      return cachingType;
   }

   /**
    * Revert to the previous reader behavior prior to 1.2.7. In this case, the code to get the source to construct the reader for the XML file will
    * be:
    * <pre>
    * Reader reader = new InputStreamReader(url.openStream());
    * </pre>
    *
    * As you can see, prior to 1.2.7, the default platform Charset was used (no Charset was specified to decode the file), which could lead to
    * decoding problems in the XML files.
    *
    * @param revert true to revert to the previous reader behavior prior to 1.2.7
    */
   public static void revertOldReaderBehavior(boolean revert) {
      revertOldReaderBehavior = revert;
   }

   /**
    * Set the SAXParser factory implementation.
    *
    * @param factoryClassName the factory class name
    */
   public static void setSAXParserFactoryImplementation(String factoryClassName) {
      FACTORY_CLASS_NAME = factoryClassName;
   }

   /**
    * Reset the SAXParser factory implementation to the default.
    */
   public static void resetSAXParserFactoryImplementation() {
      FACTORY_CLASS_NAME = null;
   }

   /**
    * Return a static SAX parser factory.
    *
    * @return the SAX Parser factory
    */
   public static SAXParserFactory getStaticParserFactory() {
      return getStaticParserFactory(Thread.currentThread().getContextClassLoader());
   }

   /**
    * Return a SAX parser factory.
    *
    * @param loader the class loader
    * @return the SAX Parser factory
    */
   public static SAXParserFactory getStaticParserFactory(ClassLoader loader) {
      if (FACTORY_CLASS_NAME == null) {
         return SAXParserFactory.newInstance();
      } else {
         return SAXParserFactory.newInstance(FACTORY_CLASS_NAME, loader);
      }
   }

   /**
    * Return a SAX parser factory.
    *
    * @return the SAX Parser factory
    */
   private SAXParserFactory getParserFactory() {
      if (FACTORY_CLASS_NAME == null) {
         return SAXParserFactory.newInstance();
      } else {
         if (loader == null) {
            return SAXParserFactory.newInstance(FACTORY_CLASS_NAME, getClass().getClassLoader());
         } else {
            return SAXParserFactory.newInstance(FACTORY_CLASS_NAME, loader);
         }
      }
   }

   /**
    * True if the parser is reverting to the previous reader behavior prior to 1.2.7. In this case, the code to get the source to construct the reader
    * for the XML file will be:
    * <pre>
    * Reader reader = new InputStreamReader(url.openStream());
    * </pre>
    *
    * As you can see, prior to 1.2.7, the default platform Charset was used (no Charset was specified to decode the file), which could lead to
    * decoding problems in the XML files.
    *
    * @return true if the parser is reverting to the previous reader behavior prior to 1.2.7
    */
   public static boolean isRevertingOldReaderBehavior() {
      return revertOldReaderBehavior;
   }

   /**
    * Return true if nestable connections are allowed by default (ie, an xml file inside a zip file, for example).
    *
    * @return true if nestable connections are allowed by default
    */
   public static boolean isDefaultAllowingNestableConnections() {
      return defaultAllowNestableConnections;
   }

   /**
    * Set if nestable connections are allowed (ie, an xml file inside a zip file, for example).
    *
    * @param allow true if nestable connections are allowed
    */
   public void setAllowNestableConnections(boolean allow) {
      this.allowNestableConnections = allow;
   }

   /**
    * Return true if nestable connections are allowed (ie, an xml file inside a zip file, for example). Note that by default the result will be set by
    * {@link #isDefaultAllowingNestableConnections()}.
    *
    * @return true if nestable connections are allowed
    */
   public boolean isAllowingNestableConnections() {
      return allowNestableConnections;
   }

   /**
    * Set if this SAX Parser uses a Schema Validator to validate XML files (true by default). If this parser does not use an XML Validator, the
    * validation will be performed by using an empty SAX Handler.
    *
    * @param hasSchemaValidator true if this SAX Parser uses a Schema Validator to validate XML files
    */
   public void setSchemaValidator(boolean hasSchemaValidator) {
      this.hasSchemaValidator = hasSchemaValidator;
   }

   /**
    * Return true if this SAX Parser uses a Schema Validator to validate XML files (true by default).
    *
    * @return true if this SAX Parser uses a Schema Validator to validate XML files
    */
   public boolean hasSchemaValidator() {
      return hasSchemaValidator;
   }

   /**
    * Set the EntityListResolver which can manage entity resolution in XML files.
    *
    * @param resolver the resolver
    */
   public void setEntityListResolver(EntityListResolver resolver) {
      this.resolver = resolver;
      resolverSetUp = false;
      setupCachingType();
   }

   /**
    * Set the encoding. It will try to get the associated Charset and silently swallow UnsupportedCharsetExceptions thrown by the
    * <code> Charset.forName(encoding)</code> method.
    *
    * @param encoding the encoding
    */
   public void setEncoding(String encoding) {
      this.encoding = encoding;
      try {
         this.charset = Charset.forName(encoding);
      } catch (UnsupportedCharsetException ex) {
      }
   }

   /**
    * Return the encoding (can be null if not set).
    *
    * @return the encoding
    */
   public String getEncoding() {
      return encoding;
   }

   /**
    * Return the charset. The default value is UTF-8. The Charset will change if the {@link #setEncoding(String)} method has been set.
    *
    * @return the charset
    */
   public Charset getCharset() {
      return charset;
   }

   /**
    * Set the listener which will be fired if exceptions (including SAXExceptions) are encountered during the parsing.
    *
    * @param listener the listener
    */
   public void setExceptionListener(ParserExceptionListener listener) {
      this.exListener = listener;
   }

   /**
    * Return the listener which will be fired if exceptions (including SAXExceptions) are encountered during the parsing. If the listener is null (no
    * listener), the parser will directlydump the parsing exceptions on the out or err streams.
    *
    * @return the listener which will be fired if exceptions are encountered during the parsing
    */
   public ParserExceptionListener getExceptionListener() {
      return exListener;
   }

   /**
    * Set the parser error dialog title.
    *
    * @param errorTitle the parser error dialog title
    */
   public void setErrorTitle(String errorTitle) {
      this.errorTitle = errorTitle;
   }

   /**
    * Set the parser configuration. It allows to configure trhe parser using an XMLParserConfiguration rather than specifying separately each of the
    * parsing properties.
    *
    * @param config the parser configuration
    */
   public void setParserConfiguration(XMLParserConfiguration config) {
      this.setExceptionListener(config.exceptionListener);
      this.setEntityListResolver(config.resolver);
      this.setDefaultLSResourceResolver(config.hasDefaultLSResolver);
      this.setDefaultBaseDirectory(config.defaultBaseDir);
      this.setValidating(config.isValidating);
      this.setSchemaValidator(config.hasSchemaValidator);
      if (config.nestableConnectionsType == XMLParserConfiguration.ALLOW_NESTABLE_CONNECTIONS) {
         this.setAllowNestableConnections(true);
      } else if (config.nestableConnectionsType == XMLParserConfiguration.DONT_ALLOW_NESTABLE_CONNECTIONS) {
         this.setAllowNestableConnections(false);
      }
      this.setXIncludeAware(config.xIncludeAware);
      this.setConcatenateIncludes(config.concatenateIncludes);
      this.setSchema(config.schemaURL);
      if (config.encoding != null) {
         this.setEncoding(config.encoding);
      }
      this.setNameSpaceAware(config.isNameSpaceAware);
      this.showExceptions(config.isShowingExceptions);
      this.showWarnings(config.isShowingWarnings);
      if (config.locale != null) {
         this.setLocale(config.locale);
      }
      if (config.errorTitle != null) {
         this.setErrorTitle(config.errorTitle);
      }
      this.setInputSourceCaching(config.cachingType);
      if (!config.getFeatures().isEmpty()) {
         this.features.clear();
         this.features.putAll(config.getFeatures());
      }
      if (!config.getProperties().isEmpty()) {
         this.properties.clear();
         this.properties.putAll(config.getProperties());
      }
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
    * Set a parser factory feature.
    *
    * @param name the feature name
    * @param value the feature value
    */
   public void setFeature(String name, boolean value) {
      features.put(name, value);
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
   public void setProperties(Map<String, Object> properties) {
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

   /**
    * Set if the parser allow a Schema validation.
    *
    * @param isSchemaAware true if the parser allow a Schema validation
    */
   public void allowSchemaValidation(boolean isSchemaAware) {
      this.isSchemaAware = isSchemaAware;
   }

   /**
    * Set if the parser is validating.
    *
    * @param isValidating true if the parser is validating
    */
   public void setValidating(boolean isValidating) {
      this.isValidating = isValidating;
   }

   /**
    * Return true if the parser is validating.
    *
    * @return true if the parser is validating
    */
   public boolean isValidating() {
      return isValidating;
   }

   /**
    * Set the default base directory which will be used for relative URLs if the parent URL is unknown. It will be used in the case where XInclude are
    * used and the parsing is performed using a Stream or a Reader.
    *
    * @param baseDir the default base directory
    */
   public void setDefaultBaseDirectory(URL baseDir) {
      this.defaultBaseDir = baseDir;
   }

   /**
    * Return the default base directory which will be used for relative URLs if the parent URL is unknown. It will be used in the case where XInclude
    * are used and the parsing is performed using a Stream or a Reader.
    *
    * @return the default base directory
    */
   public URL getDefaultBaseDirectory() {
      return defaultBaseDir;
   }

   /**
    * Set the optional Schema URL to use for Schema validation. To use Schema validation, you should:
    * <ul>
    * <li>set the {@link #allowSchemaValidation(boolean)} property to true</li>
    * </ul>
    *
    * Note that it will also set the {@link #allowSchemaValidation(boolean)} property to true. Note that you should only specify the Schema if you
    * don't set an EntityResolver. If the XML file use several namespaces (and several Schemas), you should only use an EntityResolver.
    *
    * @param schemaURL the URL of the Schema
    */
   public void setSchema(URL schemaURL) {
      this.isSchemaAware = true;
      this.schemaURL = schemaURL;
   }

   /**
    * Set if the parser shows the exceptions encountered during the parsing.
    *
    * @param isShowingExceptions true if the parser shows the exceptions encountered during the parsing
    */
   public void showExceptions(boolean isShowingExceptions) {
      this.isShowingExceptions = isShowingExceptions;
   }

   /**
    * Set if the parser shows the warnings encountered during the parsing.
    *
    * @param isShowingWarnings true if the parser shows the warnings encountered during the parsing
    */
   public void showWarnings(boolean isShowingWarnings) {
      this.isShowingWarnings = isShowingWarnings;
   }

   /**
    * Return true if the parser shows the exceptions encountered during the parsing.
    *
    * @return true if the parser shows the exceptions encountered during the parsing
    */
   public boolean isShowingExceptions() {
      return isShowingExceptions;
   }

   /**
    * Return true if the parser shows the warnings encountered during the parsing.
    *
    * @return true if the parser shows the warnings encountered during the parsing
    */
   public boolean isShowingWarnings() {
      return isShowingWarnings;
   }

   /**
    * Set if the parser is namespace-aware.
    *
    * @param isNameSpaceAware true if the parser is namespace-aware
    */
   public void setNameSpaceAware(boolean isNameSpaceAware) {
      this.isNameSpaceAware = isNameSpaceAware;
   }

   /**
    * Return true if the parser is namespace-aware.
    *
    * @return true if the parser is namespace-aware
    */
   public boolean isNameSpaceAware() {
      return isNameSpaceAware;
   }

   /**
    * Set if the parser must keep the prefixes. It will ot contain attributes used as Namespace declarations (xmlns*) unless the
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
    * Return true if the parser must keep the prefixes.
    *
    * @return true if the parser must keep the prefixes
    */
   public boolean isKeepingPrefixes() {
      return keepPrefixes;
   }

   /**
    * Set if xInclude content must be concatenated. This allows to parse a file which does content xInclude children with a parser which does not
    * handle xInclude content directly.
    *
    * @param concatenateIncludes true if xInclude content must be concatenated
    */
   public void setConcatenateIncludes(boolean concatenateIncludes) {
      this.concatenateIncludes = concatenateIncludes;
   }

   /**
    * Return true if xInclude content must be concatenated. This allows to parse a file which does content xInclude children with a parser which does
    * not handle xInclude content directly.
    *
    * @return true if xInclude content must be concatenated
    */
   public boolean isConcatenatingIncludes() {
      return concatenateIncludes;
   }

   /**
    * Set if the parser is XInclude-aware.
    *
    * @param xIncludeAware true if the parser is XInclude-aware
    */
   public void setXIncludeAware(boolean xIncludeAware) {
      this.xIncludeAware = xIncludeAware;
   }

   /**
    * Return true if the parser is XInclude-aware.
    *
    * @return true if the parser is XInclude-aware
    */
   public boolean isXIncludeAware() {
      return xIncludeAware;
   }

   /**
    * Return true if the parser support XInclude. XInclude is supported if {@link #isXIncludeAware()} is true and the underlying parser support
    * XInclude. Note that this value will only be valid after the parsing.
    *
    * @return true if the parser support XInclude
    */
   public boolean isXIncludeSupported() {
      return xIncludeSupported;
   }

   /**
    * Set the SAX Handler used for the parsing.
    *
    * @param handler the SAX Handler used for the parsing
    */
   public void setHandler(ResolverSAXHandler handler) {
      this.handler = handler;
      setupCachingType();
   }

   /**
    * Return the SAX Handler used for the parsing.
    *
    * @return the SAX Handler used for the parsing
    */
   public ResolverSAXHandler getHandler() {
      return handler;
   }

   /**
    * Set the DTD used to validate the parsing.
    *
    * @param dtd the DTD URL
    * @param publicID the DTD public ID
    */
   public void setHandlerDTD(URL dtd, String publicID) {
      this.dtd = dtd;
      this.publicID = publicID;
   }

   /**
    * Return true if the parsing has been stopped by the handler before the end of the XML stream.
    *
    * @return true if the parsing has been stopped by the handler before the end of the XML stream
    */
   public boolean hasStoppedParsing() {
      return stoppedParsing;
   }

   /**
    * Return the optional additional content returned while the parsing has been stopped before the end of the XML Stream). Willbe null except if the
    * handler has sopped the parsing before the end of the XML Stream, and has provided an additional content to the {@link StopParsingException}.
    *
    * @return the optional additional content
    */
   public Object stoppedParsingAdditionalContent() {
      return stoppedParsingContent;
   }

   /**
    * Return true if the parser encountered a Java exception during the parsing. Note that SAXExceptions are not considered and are handled directly
    * by the handler.
    *
    * @return true if the parser encountered a Java exception during the parsing
    */
   public boolean hasException() {
      return firstException != null;
   }

   /**
    * Return the first exception encountered by the parser if it encountered a Java exception during the parsing. Note that SAXExceptions are not
    * considered and are handled directly by the handler.
    *
    * @return the first exception encountered by the parser (or null)
    */
   public Throwable getFirstException() {
      return firstException;
   }

   /**
    * Get an input source from a stream.
    *
    * @param stream the input stream
    * @return the source
    */
   private InputSource getSource(InputStream stream) {
      if (reader == null || readerIsClosed) {
         if (xIncludeAware && xIncludeSupported && concatenateIncludes) {
            if (includer == null) {
               includer = new XMLIncluder(stream);
               includer.setClassLoader(loader);
               includer.setDefaultBaseDirectory(defaultBaseDir);
            }
            reader = includer.getReader();
            if (reader == null) {
               reader = new BufferedReader(new InputStreamReader(stream));
            }
         } else {
            reader = new BufferedReader(new InputStreamReader(stream));
         }
         readerIsClosed = false;
      }
      InputSource source = new InputSource(reader);
      if (encoding != null) {
         source.setEncoding(encoding);
      }
      return source;
   }

   /**
    * Get an input source from a Reader.
    *
    * @param reader the Reader
    * @return the source
    */
   private InputSource getSource(Reader reader) {
      this.reader = reader;
      InputSource source = new InputSource(reader);
      if (encoding != null) {
         source.setEncoding(encoding);
      }
      return source;
   }

   private Reader getReader(URL url) throws IOException {
      if (xIncludeAware && xIncludeSupported && concatenateIncludes) {
         Reader _reader;
         if (includer != null) {
            _reader = includer.getReader();
         } else {
            includer = new XMLIncluder(url);
            includer.setClassLoader(loader);
            _reader = includer.getReader();
            if (_reader == null) {
               _reader = ReaderPathConstructor.getReaderFromURL(url, charset, loader);
            }
         }
         return _reader;
      } else {
         Reader _reader = ReaderPathConstructor.getReaderFromURL(url, charset, loader);
         return _reader;
      }
   }

   /**
    * Return an input source from an URL.
    *
    * @param url the URL
    * @param bufFac the BufferedReaderFactory to create readers (can be null)
    * @return the source
    */
   private InputSource getSource(URL url, BufferedReaderFactory bufFac) throws IOException {
      reader = null;
      // If we use a http or https protocol, the NestableURLConnection will fail, so we will use the regular way to open a
      // stream knowing a URL. The only limitation we will have is that we will not be able to use nested jars for
      // example
      if (FileUtilities.isHTTPProtocol(url)) {
         Reader _reader = new InputStreamReader(url.openStream());
         if (bufFac != null) {
            reader = bufFac.createReader(_reader);
         } else {
            reader = new BufferedReader(_reader);
         }
         // in the general case, we are able to use nested jars
      } else {
         if (!FileUtilities.exist(url)) {
            return null;
         }
         boolean isUnix = SystemUtils.isUnixPlatform();
         // the NestableURLConnection seems not to work correctly on MacOS X (and maybe
         // Linux also), so we use the regular URL stream if we are on Mac OS. We
         // don't lose a lot of features, only we can't put a jar in a jar
         if (isUnix) {
            Reader _reader;
            if (revertOldReaderBehavior) {
               _reader = new InputStreamReader(url.openStream());
            } else {
               _reader = getReader(url);
            }
            if (bufFac != null) {
               reader = bufFac.createReader(_reader);
            } else {
               reader = new BufferedReader(_reader);
            }
         } else {
            // to keep, not used for the moment because the NestableURLConnection can trigger an exception
            // in some of our cases. Will reallow it when fixed. For the moment, don't remove this code ;)
            URLConnectionProvider provider = new URLConnectionProvider();
            URLConnection con = provider.getURLConnection(allowNestableConnections, url);
            InputStream stream = con.getInputStream();
            if (stream != null) {
               stream.close();
               Reader _reader = getReader(url);
               if (bufFac != null) {
                  reader = bufFac.createReader(_reader);
               } else if (!(_reader instanceof BufferedReader)) {
                  reader = new BufferedReader(_reader);
               } else {
                  reader = (BufferedReader) _reader;
               }
            } else {
               File file = FileUtilities.getFile(url);
               // protected for the case where the URL is malformed, to avoid to throw an Exception in the catch
               // block
               if (file == null) {
                  throw new IOException("Malformed URL for " + url.toString());
               } else {
                  throw new IOException("Impossible to open file " + file.getPath());
               }
            }
         }
      }
      InputSource source = new InputSource(reader);
      if (encoding != null) {
         source.setEncoding(encoding);
      }
      return source;
   }

   private void handleCustomProperties(SAXParser parser) throws SAXNotSupportedException, SAXNotRecognizedException {
      // handle custom properties
      if (!properties.isEmpty()) {
         Iterator<Entry<String, Object>> it = properties.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            parser.setProperty(entry.getKey(), entry.getValue());
         }
      }
   }

   private void handleCustomFeatures(SAXParserFactory factory) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if (!features.isEmpty()) {
         Iterator<Entry<String, Boolean>> it = features.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, Boolean> entry = it.next();
            factory.setFeature(entry.getKey(), entry.getValue());
         }
      }
   }

   /**
    * Validate an URL without using a Schema Validator.
    *
    * @param source the InputSource
    */
   private void validateWithoutSchemaValidator(InputSource source) {
      // create a SchemaFactory capable of understanding WXS schemas
      SAXParserFactory factory = getParserFactory();
      factory.setValidating(true);
      factory.setXIncludeAware(true);
      factory.setNamespaceAware(true);

      // validate the DOM tree
      try {
         factory.setFeature("http://apache.org/xml/features/validation/schema", true);
         factory.setFeature("http://apache.org/xml/features/xinclude", true);
         factory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
         factory.setFeature("http://apache.org/xml/features/xinclude/fixup-language", false);
         // handle custom features
         handleCustomFeatures(factory);
         SAXParser parser = factory.newSAXParser();
         parser.setProperty(JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
         // handle custom properties
         handleCustomProperties(parser);

         ValidationResolverHandler vHandler = new ValidationResolverHandler();
         vHandler.setEntityResolver(resolver);
         vHandler.setErrorHandler(handler);
         setLocale(parser);
         parser.parse(source, vHandler);
      } catch (ParserConfigurationException | IOException | SAXException e) {
      }
   }

   /**
    * Configure the parser and validate an XML source if a Schema URL has been provided.
    *
    * @param fac the parser factory
    * @param source the XML source
    * @return true if the XML source has been validated against the provided Schema
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   protected boolean configureParser(SAXParserFactory fac, InputSource source) throws ParserConfigurationException, SAXException, IOException {
      return configureParser(fac, source, false);
   }

   /**
    * Configure the parser and validate an XML source if a Schema URL has been provided.
    *
    * @param fac the parser factory
    * @param source the XML source
    * @return true if the XML source has been validated against the provided Schema
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private boolean configureParser(SAXParserFactory fac, InputSource source, boolean isReader) throws ParserConfigurationException, SAXException, IOException {
      Schema schema = null;
      boolean sourceUsed = false;
      fac.setNamespaceAware(isNameSpaceAware);
      try {
         fac.setXIncludeAware(xIncludeAware);
      } catch (UnsupportedOperationException ex) {
      }
      fac.setFeature("http://xml.org/sax/features/namespace-prefixes", keepPrefixes);
      // handle custom features
      handleCustomFeatures(fac);
      if (isValidating && isSchemaAware && !isReader) {
         configureSchemaValidation(fac);
         if (schemaURL != null) {
            SchemaFactory scFac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            if (resolver != null) {
               scFac.setResourceResolver(resolver);
            }
            schema = scFac.newSchema(schemaURL);
         }
      }
      // we will only validate the content while parsing it using SAX if there is a Schema. FYI Schema validation is
      // performed outside XML parsing, contrary to DTD validation
      if (schema == null && !(schemaURL != null) && isReader) {
         fac.setValidating(isValidating);
      }

      // set the DTD of the handler only if the parser is validating and we have provided a DTD
      if ((schema == null) && (dtd != null) && (publicID != null)) {
         handler.setDTD(dtd, publicID);
      }

      // We will validate the XML file against the Schema if we are validating the content and we have a Schema
      if (schema != null && !isReader) {
         if (hasSchemaValidator) {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(handler);
            // we use the resolver for the validation too
            if (resolver != null) {
               validator.setResourceResolver(resolver);
            }
            SAXSource saxSource = new SAXSource(source);
            setLocale(validator);
            validator.validate(saxSource);
         } else {
            validateWithoutSchemaValidator(source);
         }
         sourceUsed = true;
         closeReader();
      }
      return sourceUsed;
   }

   private void closeReader() {
      if (reader != null) {
         try {
            reader.close();
            readerIsClosed = true;
         } catch (IOException ex) {
         }
      }
   }

   /**
    * Return the error panel dialog name. The default name is "Parser Exception".
    */
   private String getDialogErrorName(URL url) {
      if (url == null) {
         return errorTitle;
      } else {
         File file = new File(url.getFile());
         return errorTitle + " in " + file.getName();
      }
   }

   /**
    * Configure the features of the SAX parser factory for validation. The features allow thr parser:
    * <ul>
    * <li>To take into account manespace declarations</li>
    * <li>To not add xml:base declarations (fixup-base-uris) which would normally fail Schema processing</li>
    * <li>To not add xml:lang declarations (fixup-language) which would normally fail Schema processing</li>
    * <li>To add schema validation (only if we have defined an external Schema)</li>
    * </ul>
    *
    * @param factory the SAX parser factory
    * @return true if XInclude is supported
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   protected boolean configureSchemaValidation(SAXParserFactory factory) throws ParserConfigurationException, SAXException {
      factory.setNamespaceAware(true);
      // we only set this feature if the schema URL used to validate the XML file is null, else we will have errors during parsing
      // due to problems of elements not found in the namespace, as the XML files don't have to define their associated namespaces
      if (schemaURL == null) {
         factory.setFeature("http://apache.org/xml/features/validation/schema", true);
      }
      // handle custom features
      handleCustomFeatures(factory);
      if (xIncludeAware) {
         try {
            factory.setXIncludeAware(true);
            factory.setFeature("http://apache.org/xml/features/xinclude", true);
            factory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
            factory.setFeature("http://apache.org/xml/features/xinclude/fixup-language", false);
            // handle custom features
            handleCustomFeatures(factory);
            return true;
         } catch (UnsupportedOperationException e) {
            xIncludeSupported = false;
            return false;
         }
      } else {
         // handle custom features
         handleCustomFeatures(factory);
         return false;
      }
   }

   /**
    * Return the URL which was parsed by this Parser. Note that the result can be null if the parser was called with a
    * {@link #parse(InputStream)}.
    *
    * @return the URL which was parsed by this Parser
    */
   public URL getParsedURL() {
      return parsedURL;
   }

   /**
    * Parse an XML file with an input Stream.
    *
    * @param stream the input Stream
    */
   public void parse(InputStream stream) {
      reader = null;
      try {
         SAXParserFactory fac = getParserFactory();
         InputSource source = getSource(stream);
         if (source == null) {
            throw new IOException("Impossible to open the Stream");
         }
         setDefaultLSResolverImpl();
         boolean sourceUsed = configureParser(fac, source);
         SAXParser parser = fac.newSAXParser();
         setLocale(parser);
         // handle custom properties
         handleCustomProperties(parser);
         if (sourceUsed) {
            source = getSource(stream);
         }

         stoppedParsing = false;
         if (resolver != null) {
            handler.setEntityResolver(resolver);
         }
         parser.parse(source, handler);
         if (isShowingExceptions) {
            handleSAXExceptions(errorTitle, handler.getExceptionResults());
         }
         closeReader();
      } catch (StopParsingException e) {
         stoppedParsing = true;
         stoppedParsingContent = e.getAdditionalContent();
      } catch (SAXException e) {
         if (isShowingExceptions) {
            if (isShowingWarnings && (handler.getStatus() == ResolverSAXHandler.WARNINGS)) {
               handleSAXExceptions(errorTitle, handler.getExceptionResults());
            } else if (handler.hasErrors()) {
               handleSAXExceptions(errorTitle, handler.getExceptionResults());
            } else {
               this.handleException(errorTitle, e);
            }
         }
      } catch (Exception e) {
         Throwable t = e;
         if (e.getCause() != null) {
            t = e.getCause();
         }
         this.firstException = t;
         if (isShowingExceptions) {
            this.handleException(errorTitle, e);
         }
      }
   }

   /**
    * Parse an XML file. Note that the parser will do nothing if the file is null.
    *
    * @param file the file to parse
    */
   public void parse(File file) {
      parse(file, null);
   }

   /**
    * Parse an XML file. Note that the parser will do nothing if the file is null. Passing a BufferedReaderFactory to the method allows to perform
    * filtering on the XML content before passing it to the parser.
    *
    * @param file the file to parse
    * @param bufFac the BufferedReaderFactory to create readers (can be null)
    */
   public void parse(File file, BufferedReaderFactory bufFac) {
      reader = null;
      if (file != null) {
         try {
            URL url = file.toURI().toURL();
            parse(url, bufFac);
         } catch (MalformedURLException e) {
            this.firstException = e;
            if (isShowingExceptions) {
               this.handleException(errorTitle, e);
            }
         }
      }
   }

   private void setLocale(Validator validator) {
      if (locale != null) {
         try {
            validator.setProperty("http://apache.org/xml/properties/locale", locale);
         } catch (SAXException ex) {
         }
      }
   }

   private void setLocale(SAXParser parser) {
      if (locale != null) {
         try {
            XMLReader xmlreader = parser.getXMLReader();
            xmlreader.setProperty("http://apache.org/xml/properties/locale", locale);
         } catch (SAXException ex) {
         }
      }
   }

   /**
    * Parse an XML file with a ReaderProvider. Contrary to the {@link #parse(Reader)}, the method allows to reuse the same provider for the
    * Schema validation phase and the parsing phase.
    *
    * @param provider the ReaderProvider
    */
   public void parse(ReaderProvider provider) {
      if (provider != null) {
         try {
            SAXParserFactory fac = getParserFactory();
            InputSource source = getSource(provider.newReader());
            this.parsedURL = null;
            setDefaultLSResolverImpl();
            boolean sourceUsed = configureParser(fac, source, false);
            SAXParser parser = fac.newSAXParser();
            setLocale(parser);
            // handle custom properties
            handleCustomProperties(parser);
            if (sourceUsed) {
               source = getSource(provider.newReader());
            }
            stoppedParsing = false;
            if (resolver != null) {
               handler.setEntityResolver(resolver);
            }
            parser.parse(source, handler);
            closeReader();
            if (isShowingExceptions) {
               handleSAXExceptions(getDialogErrorName(null), handler.getExceptionResults());
            }
         } catch (StopParsingException e) {
            stoppedParsing = true;
            stoppedParsingContent = e.getAdditionalContent();
         } catch (SAXException e) {
            if (isShowingExceptions) {
               if (handler.hasErrors()) {
                  handleSAXExceptions(getDialogErrorName(null), handler.getExceptionResults());
               } else {
                  this.handleException(errorTitle, e);
               }
            }
         } catch (Exception e) {
            this.firstException = e;
            if (isShowingExceptions) {
               this.handleException(errorTitle, e);
            }
         }
      }
   }

   private void setDefaultLSResolverImpl() {
      if (hasDefaultLSResolver && !resolverSetUp) {
         if (resolver == null) {
            resolver = new EntityListResolver();
            resolver.addDefaultResolvedEntities();
         } else {
            resolver.addDefaultResolvedEntities();
         }
         resolverSetUp = true;
      }
   }

   /**
    * Parse an XML file with a Reader. Note that the parser will do nothing if the reader is null.
    *
    * Also note that this method will not validate the parsing when using a Schema. The reason is that reader may not be reused again (in our case
    * after the Schema validation phase). In this use case, use instead the {@link #parse(ReaderProvider)} method.
    *
    * @param reader the Reader to parse
    */
   public void parse(Reader reader) {
      if (reader != null) {
         try {
            SAXParserFactory fac = getParserFactory();
            InputSource source = getSource(reader);
            this.parsedURL = null;
            boolean sourceUsed = configureParser(fac, source, true);
            SAXParser parser = fac.newSAXParser();
            setLocale(parser);
            // handle custom properties
            handleCustomProperties(parser);
            if (sourceUsed) {
               source = getSource(reader);
            }
            stoppedParsing = false;
            setDefaultLSResolverImpl();
            if (resolver != null) {
               handler.setEntityResolver(resolver);
            }
            parser.parse(source, handler);
            if (isShowingExceptions) {
               handleSAXExceptions(getDialogErrorName(null), handler.getExceptionResults());
            }
         } catch (StopParsingException e) {
            stoppedParsing = true;
            stoppedParsingContent = e.getAdditionalContent();
         } catch (SAXException e) {
            if (isShowingExceptions) {
               if (handler.hasErrors()) {
                  handleSAXExceptions(getDialogErrorName(null), handler.getExceptionResults());
               } else {
                  this.handleException(errorTitle, e);
               }
            }
         } catch (Exception e) {
            this.firstException = e;
            if (isShowingExceptions) {
               this.handleException(errorTitle, e);
            }
         }
      }
   }

   /**
    * Parse an XML file with an URL. Note taht the parser will do nothing if the URL is null.
    *
    * @param url the URL to parse
    */
   public void parse(URL url) {
      parse(url, null);
   }

   /**
    * Parse an XML file with an URL. Note that the parser will do nothing if the URL is null. Passing a BufferedReaderFactory to the method allows to
    * perform filtering on the XML content before passing it to the parser.
    *
    * @param url the URL to parse
    * @param bufFac the BufferedReaderFactory to create readers (can be null)
    */
   public void parse(URL url, BufferedReaderFactory bufFac) {
      // url is always null when executing test : I put this test to avoid catching an exception
      if (url != null) {
         try {
            SAXParserFactory fac = getParserFactory();
            InputSource source = getSource(url, bufFac);
            this.parsedURL = url;
            // avoid to rethrow an Exception, it's better to log a SEVERE warning, and don't do the parsing at all in
            // this case
            if (source == null) {
               handleException("Impossible to open the URL " + url.toString());
               return;
            }
            setDefaultLSResolverImpl();
            boolean sourceUsed = configureParser(fac, source);
            SAXParser parser = fac.newSAXParser();
            setLocale(parser);
            // handle custom properties
            handleCustomProperties(parser);
            if (sourceUsed) {
               source = getSource(url, bufFac);
            }
            stoppedParsing = false;
            if (resolver != null) {
               handler.setEntityResolver(resolver);
            }
            parser.parse(source, handler);
            closeReader();
            if (isShowingExceptions) {
               handleSAXExceptions(getDialogErrorName(url), handler.getExceptionResults());
            }
         } catch (StopParsingException e) {
            stoppedParsing = true;
            stoppedParsingContent = e.getAdditionalContent();
         } catch (SAXException e) {
            if (isShowingExceptions) {
               if (handler.hasErrors()) {
                  handleSAXExceptions(getDialogErrorName(url), handler.getExceptionResults());
               } else {
                  this.handleException(errorTitle, e);
               }
            }
         } catch (Exception e) {
            this.firstException = e;
            if (isShowingExceptions) {
               this.handleException(errorTitle, e);
            }
         }
      }
   }

   /**
    * Handle an exception message (not a SAX Exception). The handling will be delegated to the {@link ParserExceptionListener} if onewas provided to
    * this parser.
    *
    * @param message the exception message
    */
   protected void handleException(String message) {
      if (exListener != null) {
         exListener.handleException(message);
      } else {
         System.err.println(message);
      }
   }

   /**
    * Handle an exception (not a SAX Exception). The handling will be delegated to the {@link ParserExceptionListener} if one was provided to this
    * parser.
    *
    * @param title the exception title
    * @param e the Exception
    */
   protected void handleException(String title, Exception e) {
      if (exListener != null) {
         exListener.handleException(title, e);
      } else {
         e.printStackTrace();
      }
   }

   /**
    * Handle the SAX Exceptions resulting from the parsing. By default it will trigger the {@link ParserExceptionListener} with an empty list of
    * exceptions.
    *
    * @param title the title
    * @see #getExceptionListener()
    */
   public void handleSAXExceptions(String title) {
      if (exListener != null) {
         exListener.handleExceptionList(title, new ArrayList<>(1));
      }
   }

   /**
    * Handle the SAX Exceptions resulting from the parsing.
    *
    * @param title the title
    * @param results the Exceptions results
    */
   protected void handleSAXExceptions(String title, List<ResolverSAXHandler.ExceptionResult> results) {
      if (!results.isEmpty()) {
         if (exListener != null) {
            exListener.handleExceptionList(title, results);
         } else {
            Iterator<ResolverSAXHandler.ExceptionResult> it = results.iterator();
            while (it.hasNext()) {
               ResolverSAXHandler.ExceptionResult result = it.next();
               int type = result.getExceptionType();
               SAXParseException ex = result.getSAXParseException();
               String message;
               if (type == ResolverSAXHandler.ERRORS) {
                  message = "ERROR: line " + ex.getLineNumber();
               } else if (type == ResolverSAXHandler.WARNINGS) {
                  message = "WARNING: line " + ex.getLineNumber();
               } else if (type == ResolverSAXHandler.FATAL) {
                  message = "FATAL: line " + ex.getLineNumber();
               } else {
                  message = "INFO: line " + ex.getLineNumber();
               }

               if (type == ResolverSAXHandler.INFO) {
                  System.out.println(message + ": " + ex.getMessage());
               } else {
                  System.err.println(message + ": " + ex.getMessage());
               }
            }
         }
      }
   }
}
