/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2008, 2009, 2011, 2012, 2014, 2016, 2019, 2021, 2022, 2023 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * A default SAX handler which handles SAX error and resolution events.
 *
 * @version 1.2.42
 */
public class ResolverSAXHandler extends DefaultHandler2 implements LSResourceResolver, InputSourceCaching {
   /**
    * The enumeration value for parsings which fire no errors.
    */
   public static final int NO_ERRORS = -1;
   /**
    * The enumeration value for parsings which fire informations.
    */
   public static final int INFO = 0;
   /**
    * The enumeration value for parsings which fire XML warnings in parsing.
    */
   public static final int WARNINGS = 1;
   /**
    * The enumeration value for parsings which fire XML errors in parsing.
    */
   public static final int ERRORS = 2;
   /**
    * The enumeration value for parsings which fire XML fatal errors in parsing.
    */
   public static final int FATAL = 3;
   /**
    * The default caching behavior.
    */
   public static final short CACHE_DEFAULT = 0;
   /**
    * The parsing state. Can have one of the following values:
    * <ul>
    * <li>{@link #NO_ERRORS}</li>
    * <li>{@link #INFO}</li>
    * <li>{@link #WARNINGS}</li>
    * <li>{@link #ERRORS}</li>
    * <li>{@link #FATAL}</li>
    * </ul>
    */
   public int status = NO_ERRORS;
   protected URL dtd;
   protected String publicDTD;
   /**
    * The Locator used for the handler. This is null by default.
    */
   protected Locator locator;
   private boolean startDocument = false;
   private boolean startElement = false;
   private short cachingType = InputSourceCaching.CACHING_DEFAULT;
   /**
    * The list of results (exceptions and comments) for the parsing.
    */
   protected List<ExceptionResult> results = new ArrayList<>();
   /**
    * The EntityListResolver (may be null).
    */
   protected EntityListResolver resolver = null;

   /**
    * Create a new ResolverSAXHandler with an existing list of results.
    *
    * @param results the existing list of results
    */
   public ResolverSAXHandler(List<ExceptionResult> results) {
      super();
      this.results = results;
   }

   public ResolverSAXHandler() {
      super();
   }

   /**
    * Set the InputSources caching behavior.
    *
    * @param cachingType the caching type
    */
   @Override
   public void setInputSourceCaching(short cachingType) {
      this.cachingType = cachingType;
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
    * Set the entity resolver to use with this handler (null by default).
    *
    * @param resolver the entity resolver
    */
   public void setEntityResolver(EntityListResolver resolver) {
      this.resolver = resolver;
      switch (cachingType) {
         case CACHING_SOURCES:
            resolver.cacheSources(true);
            break;
         case NOT_CACHING_SOURCES:
            resolver.cacheSources(false);
            break;
      }
   }

   /**
    * Return the entity resolver to use with this handler.
    *
    * @return the entity resolver
    */
   public EntityListResolver getEntityResolver() {
      return resolver;
   }

   /**
    * Allows to force the DTD to use for the parsing (for example a resource of the application).
    *
    * Remark: A common way to find resources is by:
    * <ul>
    * <li> Thread.currentThread().getContextClassLoader().getResource(path), with path = &gt;name of package with "/" as separator&lt;/&gt;name of
    * resource&lt;
    * </li>
    * </ul>
    *
    * @param dtd the DTD
    * @param publicDTD the DTD public ID
    */
   public void setDTD(URL dtd, String publicDTD) {
      this.dtd = dtd;
      this.publicDTD = publicDTD;
   }

   /**
    * Return this handler DTD.
    *
    * @return the DTD (may be null)
    */
   public URL getDTD() {
      return dtd;
   }

   /**
    * Return true if the document is started.
    *
    * @return true if the document is started
    */
   public final boolean isDocumentStarted() {
      return startDocument;
   }

   /**
    * Return true if the first element has been encountered.
    *
    * @return true if the first element has been encountered
    */
   public final boolean isElementStarted() {
      return startElement;
   }

   /**
    * Restart this handler. Will only reset the {@link #isDocumentStarted()} and {@link #isElementStarted()} properties. This method can be useful
    * when the user want to reuse the same handler without clearing its results.
    */
   public void restart() {
      startElement = false;
      startDocument = false;
      results.clear();
   }

   /**
    * Reset this handler. It will restart the handler, and clear its results.
    */
   public void reset() {
      results.clear();
      startElement = false;
      startDocument = false;
      status = NO_ERRORS;
   }

   /**
    * Return the list of exceptions results encountered during parsing. Note that some Exception results can be comments on the parsing and thus not
    * have any associated SAXParseException.
    *
    * @return the list of exceptions results encountered during parsing
    */
   public List<ExceptionResult> getExceptionResults() {
      return results;
   }

   /**
    * Return the exceptions results encountered during parsing, sorted by their line number.
    *
    * @return the exceptions results encountered during parsing
    */
   public SortedMap<Integer, List<ExceptionResult>> getExceptionsResultsByLine() {
      // first sort the exceptions
      SortedMap<Integer, List<ExceptionResult>> sortedExceptions = new TreeMap<>();
      Iterator<ExceptionResult> it = results.iterator();
      while (it.hasNext()) {
         ExceptionResult result = it.next();
         SAXParseException ex = result.getSAXParseException();
         int lineNumber = ex.getLineNumber();
         if (sortedExceptions.containsKey(lineNumber)) {
            sortedExceptions.get(lineNumber).add(result);
         } else {
            List<ExceptionResult> list = new ArrayList<>();
            list.add(result);
            sortedExceptions.put(lineNumber, list);
         }
      }
      return sortedExceptions;
   }

   /**
    * Return the list of exceptions encountered during parsing.
    *
    * @return the list of exceptions encountered during parsing
    */
   public List<SAXParseException> getExceptions() {
      List<SAXParseException> exceptions = new ArrayList(results.size());

      SortedMap<Integer, List<ExceptionResult>> sortedExceptions = getExceptionsResultsByLine();
      // then iterate through the results
      Iterator<List<ExceptionResult>> it = sortedExceptions.values().iterator();
      while (it.hasNext()) {
         List<ExceptionResult> list = it.next();
         Iterator<ExceptionResult> it2 = list.iterator();
         while (it2.hasNext()) {
            ExceptionResult result = it2.next();
            if (result.getSAXParseException() != null) {
               exceptions.add(result.getSAXParseException());
            }
         }
      }
      return exceptions;
   }

   /**
    * Return true if the parser has some parser exceptions.
    *
    * @return true if the parser has some parser exceptions
    */
   public boolean hasParserExceptions() {
      return status > INFO;
   }

   /**
    * Return the first SAX exception encountered during parsing.
    *
    * @return the first SAX exception encountered during parsing
    */
   public SAXParseException getFirstException() {
      if (hasParserExceptions()) {
         return null;
      } else {
         SAXParseException ex = null;
         for (int i = 0; i < results.size(); i++) {
            ExceptionResult result = results.get(i);
            if (result.getSAXParseException() != null) {
               ex = result.getSAXParseException();
               break;
            }
         }
         return ex;
      }
   }

   @Override
   public void endDTD() {
      this.startDocument = true;
   }

   /**
    * Receive notification of the beginning of the document. This will reset the {@link #hasStartedElements} property.
    *
    */
   @Override
   public void startDocument() throws SAXException {
      this.startDocument = true;
      if (resolver != null) {
         resolver.reset();
      }
   }

   /**
    * Return true if the document content (after the DTD) has already started to be parsed.
    *
    * @return true if the document content (after the DTD) has already started to be parsed
    */
   public boolean hasStartedElements() {
      return startDocument;
   }

   /**
    * Receive notification of the beginning of an element. The basic implementation set the {@link #hasStartedElements} property to true.
    * Sub-implementations of this class should then perform <code>super.startElement(...)</code>.
    *
    * @param uri The Namespace URI
    * @param localname The local name (without prefix)
    * @param qname The qualified name (with prefix)
    * @param attr The attributes attached to the element.
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      if (!startElement) {
         startElement = true;
      }
   }

   /**
    * Return the document locator. Note that by default the locator used by this class is null.
    */
   @Override
   public void setDocumentLocator(Locator locator) {
      this.locator = locator;
   }

   /**
    * Return the document locator.
    *
    * @return the document locator
    */
   public Locator getDocumentLocator() {
      return locator;
   }

   /**
    * Return true if there is at least one warning after the parsing.
    *
    * @return true if there is at least one warning after the parsing
    */
   public boolean hasErrors() {
      return status >= WARNINGS;
   }

   /**
    * Return the final result status.
    *
    * @return the final result status
    */
   public int getStatus() {
      return status;
   }

   /**
    * Force the status.
    *
    * @param status the status
    */
   public void setStatus(int status) {
      this.status = status;
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * has been defined for the handler.
    *
    * @param e the SAX exception
    * @param properties the additional properties
    */
   public void error(SAXParseException e, Object properties) {
      if ((startDocument && startElement) || ((publicDTD == null) && (resolver == null))) {
         ExceptionResult result = new ExceptionResult(e, ERRORS);
         result.setAdditionalProperties(properties);
         results.add(result);
         if (status < ERRORS) {
            status = ERRORS;
         }
         errorImpl(e);
      }
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * has been defined for the handler.
    *
    * @param e the SAX exception
    */
   @Override
   public void error(SAXParseException e) {
      if ((startDocument && startElement) || ((publicDTD == null) && (resolver == null))) {
         results.add(new ExceptionResult(e, ERRORS));
         if (status < ERRORS) {
            status = ERRORS;
         }
         errorImpl(e);
      }
   }

   /**
    * Do nothing by default. This will only effectively be called if an error has been acknowledged in {@link #error(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   protected void errorImpl(SAXParseException e) {
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * or resolver has been defined for the handler.
    *
    * @param e the SAX exception
    * @param properties the additional properties
    */
   public void fatalError(SAXParseException e, Object properties) {
      ExceptionResult result = new ExceptionResult(e, FATAL);
      result.setAdditionalProperties(properties);
      results.add(result);
      if (status < FATAL) {
         status = FATAL;
      }
      fatalErrorImpl(e);
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * or resolver has been defined for the handler.
    *
    * @param e the SAX exception
    */
   @Override
   public void fatalError(SAXParseException e) {
      results.add(new ExceptionResult(e, FATAL));
      if (status < FATAL) {
         status = FATAL;
      }
      fatalErrorImpl(e);
   }

   /**
    * Do nothing. This will only effectively be called if a fatal error has been acknowledged in {@link #fatalError(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   protected void fatalErrorImpl(SAXParseException e) {
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * or resolver has been defined for the handler.
    *
    * @param e the SAX exception
    * @param properties the additional properties
    */
   public void warning(SAXParseException e, Object properties) {
      if ((startDocument && startElement) || ((publicDTD == null) && (resolver == null))) {
         ExceptionResult result = new ExceptionResult(e, WARNINGS);
         result.setAdditionalProperties(properties);
         results.add(result);
         if (status < WARNINGS) {
            status = WARNINGS;
         }
         warningImpl(e);
      }
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the beginning of the Document <b>and</b> an alternated public DTD
    * or resolver has been defined for the handler.
    *
    * @param e the SAX exception
    */
   @Override
   public void warning(SAXParseException e) {
      if ((startDocument && startElement) || ((publicDTD == null) && (resolver == null))) {
         results.add(new ExceptionResult(e, WARNINGS));
         if (status < WARNINGS) {
            status = WARNINGS;
         }
         warningImpl(e);
      }
   }

   /**
    * Do nothing. This will only effectively be called if a warning has been acknowledged in {@link #warning(SAXParseException)}.
    *
    * @param e the SAXParseException
    */
   protected void warningImpl(SAXParseException e) {
   }

   /**
    * Add an information to the results.
    *
    * @param message the information message
    * @param properties the additional properties
    */
   public void info(String message, Object properties) {
      InformationResult result = new InformationResult(message);
      result.setAdditionalProperties(properties);
      results.add(result);
      if (status < INFO) {
         status = INFO;
      }
   }

   /**
    * Add an information to the results.
    *
    * @param message the information message
    */
   public void info(String message) {
      results.add(new InformationResult(message));
      if (status < INFO) {
         status = INFO;
      }
   }

   /**
    * Called at the end of the XML document.
    *
    * @throws SAXException
    */
   @Override
   public void endDocument() throws SAXException {
      if (resolver != null) {
         resolver.endDocument();
      }
   }

   /**
    * Resolves publicIDs and systemIDs.
    * <ul>
    * <li>If a systemID is found, and a DTD URL is set, then it will look for this URL, and not look in the files system</li>
    * <li>Else the entity will be searched in the file system</li>
    * </ul>
    *
    * @param name the entity name
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
    * @return the Input Source
    */
   @Override
   public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
      try {
         if (publicDTD != null) {
            return super.resolveEntity(publicDTD, null);
         } else {
            return null;
         }
      } catch (SAXException ex) {
         return null;
      }
   }

   /**
    * Resolves publicIDs and systemIDs.
    * <ul>
    * <li>If a systemID is found, and a DTD URL is set, then it will look for this URL, and not look in the file system</li>
    * <li>Else the entity will be searched in the file system</li>
    * </ul>
    *
    * @param name the entity name
    * @param publicID The public identifier of the external entity being referenced, or null if no public identifier was supplied
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no system identifier was supplied
    * @return the Input Source
    */
   @Override
   public InputSource resolveEntity(String name, String publicID, String baseURI, String systemID) throws SAXException, IOException {
      if (startDocument) {
         if (publicID == null) {
            if (resolver != null) {
               InputSource source = resolver.resolveSystemID(baseURI, systemID);
               // if the resolver do not resolve the systemID, and there is a DTD, we use this DTD for the
               // source
               if (source == null && publicDTD != null) {
                  source = new InputSource(dtd.openStream());
               }
               return source;
            } else if (publicDTD != null) {
               InputSource source = new InputSource(dtd.openStream());
               return source;
            } else {
               return null;
            }
         } else if (resolver != null) {
            return resolver.resolve(publicID);
         } else if (dtd != null) {
            InputSource source = new InputSource(dtd.openStream());
            return source;
         } else {
            return null;
         }
      } else if (dtd == null) {
         if (resolver != null) {
            return resolver.resolveSystemID(baseURI, systemID);
         } else if (this.publicDTD != null) {
            InputSource source = new InputSource(publicDTD);
            return source;
         } else {
            return null;
         }
      } else {
         InputSource source = new InputSource(dtd.openStream());
         return source;
      }
   }

   /**
    * Resolves publicIDs and systemIDs.
    * <ul>
    * <li>look for this URL, and not look in the file system</li>
    * <li>Else the entity will be searched in the file system</li>
    * </ul>
    *
    * @param name the entity name
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no system identifier was supplied
    * @return the Input Source
    */
   @Override
   public InputSource resolveEntity(String name, String systemID) throws SAXException, IOException {
      if (startDocument) {
         if (resolver != null) {
            return resolver.resolveSystemID(null, systemID);
         } else {
            return null;
         }
      } else if (dtd == null) {
         if (resolver != null) {
            return resolver.resolveSystemID(null, systemID);
         } else if (this.publicDTD != null) {
            InputSource source = new InputSource(dtd.openStream());
            return source;
         } else {
            return null;
         }
      } else {
         InputSource source = new InputSource(dtd.openStream());
         return source;
      }
   }

   /**
    * Allow the application to resolve external resources.
    *
    * @param type The type of the resource being resolved. For XML resources applications must use the value
    * <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema, applications must use the value <code>"http://www.w3.org/2001/XMLSchema"</code>.
    * @param namespaceURI The namespace of the resource being resolved
    * @param publicID The public identifier of the external entity being referenced, or null if no public identifier was supplied
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no system identifier was supplied
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
    * @return The input source, or null to request that the parser open a regular URI connection to the resource
    */
   @Override
   public LSInput resolveResource(String type, String namespaceURI, String publicID, String systemID, String baseURI) {
      try {
         InputSource source = resolveEntity(null, publicID, systemID, baseURI);
         return new SourceWrapper(source, baseURI);
      } catch (SAXException | IOException ex) {
         return null;
      }
   }

   /**
    * A class which wraps around an {@link InputSource} to create a {@link LSInput}.
    *
    * @version 0.9.6
    */
   public class SourceWrapper implements LSInput {
      private InputSource source = null;
      private String baseURI = null;
      private String stringData = null;
      private boolean certified = true;

      private SourceWrapper(InputSource source, String baseURI) {
         this.source = source;
         this.baseURI = baseURI;
      }

      @Override
      public Reader getCharacterStream() {
         return source.getCharacterStream();
      }

      @Override
      public void setCharacterStream(Reader characterStream) {
         source.setCharacterStream(characterStream);
      }

      @Override
      public InputStream getByteStream() {
         return source.getByteStream();
      }

      @Override
      public void setByteStream(InputStream byteStream) {
         source.setByteStream(byteStream);
      }

      @Override
      public String getStringData() {
         return stringData;
      }

      @Override
      public void setStringData(String stringData) {
         this.stringData = stringData;
      }

      @Override
      public String getSystemId() {
         return source.getSystemId();
      }

      @Override
      public void setSystemId(String systemID) {
         source.setSystemId(systemID);
      }

      @Override
      public String getPublicId() {
         return source.getPublicId();
      }

      @Override
      public void setPublicId(String publicID) {
         source.setPublicId(publicID);
      }

      @Override
      public String getBaseURI() {
         return baseURI;
      }

      @Override
      public void setBaseURI(String baseURI) {
         this.baseURI = baseURI;
      }

      @Override
      public String getEncoding() {
         return source.getEncoding();
      }

      @Override
      public void setEncoding(String encoding) {
         source.setEncoding(encoding);
      }

      @Override
      public boolean getCertifiedText() {
         return certified;
      }

      @Override
      public void setCertifiedText(boolean certified) {
         this.certified = certified;
      }
   }

   /**
    * Represent an exception result returned after the parsing.
    *
    * @version 1.2.25.5
    */
   public static class ExceptionResult {
      private SAXParseException exception = null;
      private Object properties = null;
      private int exceptionType = NO_ERRORS;

      /**
       * Constructor.
       *
       * @param exception the SAX Exception
       * @param exceptionType the exception type
       */
      public ExceptionResult(SAXParseException exception, int exceptionType) {
         this.exception = exception;
         this.exceptionType = exceptionType;
      }

      /**
       * Return the exception type. Valid types are:
       * <ul>
       * <li>{@link #WARNINGS}</li>
       * <li>{@link #ERRORS}</li>
       * <li>{@link #FATAL}</li>
       * <li>{@link #INFO}:note that in this case, you should use the {@link InformationResult} class</li>
       * </ul>
       *
       * @return the exception type
       */
      public int getExceptionType() {
         return exceptionType;
      }

      /**
       * Add additional properties to the result. Note that these properties are not handled by the <code>ResolverSAXHandler</code> class at all. The
       * client code is free to use any class for these properties.
       *
       * @param properties the properties
       */
      public void setAdditionalProperties(Object properties) {
         this.properties = properties;
      }

      /**
       * Return the additional properties on the result. By default it will return null if the {@link #setAdditionalProperties(Object)}
       * method has not been called.
       *
       * @return the properties
       */
      public Object getAdditionalProperties() {
         return properties;
      }

      /**
       * Return true if there are the additional properties on the result. By default it will return false if the
       * {@link #setAdditionalProperties(Object)} method has not been called.
       *
       * @return true if there are the additional properties
       */
      public boolean hasAdditionalProperties() {
         return properties != null;
      }

      /**
       * Return the associated SAXParseException.
       *
       * @return the SAXParseException
       */
      public SAXParseException getSAXParseException() {
         return exception;
      }
   }

   /**
    * Represent an information result returned after the parsing.
    *
    * @version 1.2.25.5
    */
   public static class InformationResult extends ExceptionResult {
      private String message = null;
      private boolean isLocalized = false;

      /**
       * Constructor.
       *
       * @param message the message
       * @param isLocalized true if the message is localized, which means that the message correspond to a specific lne in the XML document
       */
      public InformationResult(String message, boolean isLocalized) {
         super(null, INFO);
         this.message = message;
         this.isLocalized = isLocalized;
      }

      /**
       * Constructor. The InformationResult wil not be localized.
       *
       * @param message the message
       */
      public InformationResult(String message) {
         super(null, INFO);
         this.message = message;
      }

      /**
       * Return true if the message correspond to a specific lne in the XML document.
       *
       * @return true if the message correspond to a specific lne in the XML document
       */
      public boolean isLocalized() {
         return isLocalized;
      }

      /**
       * Return the information message.
       *
       * @return the information message
       */
      public String getMessage() {
         return message;
      }
   }
}
