/*------------------------------------------------------------------------------
 * Copyright (C) 2015 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * A SAX handler used to validate an XML file. This is used internally by the {@link XMLSAXParser} to validate an
 * XML File without using a Schema Validator.
 *
 * @since 0.8.5
 * @see XMLSAXParser#setSchemaValidator(boolean)
 */
public class ValidationResolverHandler extends DefaultHandler2 implements LSResourceResolver {
   private EntityListResolver resolver = null;
   private ResolverSAXHandler errorHandler = null;

   public ValidationResolverHandler() {
      super();
   }

   /**
    * Set the associated error handler.
    *
    * @param errorHandler the associated error handler
    */
   public void setErrorHandler(ResolverSAXHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the
    * beginning of the Document <b>and</b> an alternated public DTD has been defined for the
    * handler.
    */
   @Override
   public void error(SAXParseException e) {
      if (errorHandler != null) {
         errorHandler.error(e);
      } else {
         System.err.println("ERROR: " + e.getMessage());
      }
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the
    * beginning of the Document <b>and</b> an alternated public DTD or resolver has been defined for the
    * handler.
    */
   @Override
   public void fatalError(SAXParseException e) {
      if (errorHandler != null) {
         errorHandler.fatalError(e);
      } else {
         System.err.println("FATAL: " + e.getMessage());
      }
   }

   /**
    * Parsing errors will be silently ignored if they are encountered <b>before</b> the
    * beginning of the Document <b>and</b> an alternated public DTD or resolver has been defined for the
    * handler.
    */
   @Override
   public void warning(SAXParseException e) {
      if (errorHandler != null) {
         errorHandler.warning(e);
      } else {
         System.err.println("WARNING: " + e.getMessage());
      }
   }

   /**
    * Set the entity resolver to use with this handler(null by default).
    *
    * @param resolver the entity resolver
    */
   public void setEntityResolver(EntityListResolver resolver) {
      this.resolver = resolver;
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
    * Resolves publicIDs and systemIDs.
    * <ul>
    * <li>look for this URL, and not look in the file system</li>
    * <li>Else the entity will be searched in the file system</li>
    * </ul>
    *
    * @param publicID the publicID
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no
    * system identifier was supplied
    * @return the Input Source
    */
   @Override
   public InputSource resolveEntity(String publicID, String systemID) throws SAXException, IOException {
      if (systemID == null) {
         if (resolver.isResolvingEntities()) {
            InputSource source = resolver.resolve(publicID);
            return source;
         } else {
            return super.resolveEntity(publicID, systemID);
         }
      } else {
         return super.resolveEntity(publicID, systemID);
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
    * @param publicID The public identifier of the external entity being referenced, or null if no public identifier was
    * supplied
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no
    * system identifier was supplied
    * @return the Input Source
    */
   @Override
   public InputSource resolveEntity(String name, String publicID, String baseURI, String systemID) throws SAXException, IOException {
      if (publicID != null) {
         return resolver.resolve(publicID);
      } else if (systemID != null) {
         return resolver.resolveSystemID(null, systemID);
      } else {
         return null;
      }
   }

   /**
    * Allow the application to resolve external resources.
    *
    * @param type The type of the resource being resolved. For XML resources applications must use the value
    * <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema, applications must use the value
    * <code>"http://www.w3.org/2001/XMLSchema"</code>.
    * @param namespaceURI The namespace of the resource being resolved
    * @param publicID The public identifier of the external entity being referenced, or null if no public identifier was
    * supplied
    * @param systemID The system identifier, a URI reference of the external resource being referenced, or null if no
    * system identifier was supplied
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
    * @return The input source, or null to request that the parser open a regular URI connection to the
    * resource
    */
   @Override
   public LSInput resolveResource(String type, String namespaceURI, String publicID, String systemID, String baseURI) {
      try {
         InputSource source = resolveEntity(null, publicID, systemID, baseURI);
         return new SourceWrapper(source, baseURI);
      } catch (SAXException ex) {
         return null;
      } catch (IOException ex) {
         return null;
      }
   }

   /**
    * A class which wraps around an {@link InputSource} to create a {@link LSInput}.
    */
   private static class SourceWrapper implements LSInput {
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
}
