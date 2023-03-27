/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2014, 2015, 2016, 2019, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.mdiutil.io.FileUtilities;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class which can manage entity resolution in XML files. This class offer a framework to resolve resources encountered in XML files (including XML
 * Schemas).
 *
 * Note that this class is able to resolve sibling entities of an entity which has been resolved. For example, if the following systemID is resolved:
 * <ul>
 * <li>"http://location/refXSD.xsd" =&gt; "D:/myFiles/schemas/refXSD.xsd"</li>
 * </ul>
 * Then upon encountering the following entity (for example referred to in refXSD.xsd):
 * <ul>
 * <li>"http://location/childSchema.xsd"</li>
 * </ul>
 * The resolver will look for the following resource:
 * <ul>
 * <li>"D:/myFiles/schemas/childSchema.xsd"</li>
 * </ul>
 *
 * <h1>Example</h1>
 * Let's define an XMLSAXParser
 * <pre>
 * EntityListResolver resolver = new EntityListResolver();*
 * URL xsd = new URL(&lt;URL of an XSD&gt;);
 * resolver.addResolvedSystemID("http://location/refXSD.xsd", xsd);
 *
 * XMLSAXParser parser = new XMLSAXParser();
 * parser.setEntityListResolver(resolver);
 * </pre> During the parsing of an XML file:
 * <ul>
 * <li>Schema locations with a "http://location/refXSD.xsd" location will be resolved to the specified XSD</li>
 * <li>XML inclusions with a "http://location/refXSD.xsd" location will be resolved to the specified XSD</li>
 * </ul>
 *
 * <h1>Adding default resolved entities</h1>
 * The {@link #addDefaultResolvedEntities()} method allows to resolve the following entities:
 * <ul>
 * <li>http://www.w3.org/2001/xml.xsd</li>
 * <li>http://www.w3.org/2009/01/xml.xsd</li>
 * <li>http://www.w3.org/2007/08/xml.xsd</li>
 * <li>http://www.w3.org/2004/10/xml.xsd</li>
 * <li>http://www.w3.org/2001/03/xml.xsd</li>
 * <li>http://www.w3.org/2001/XMLSchema</li>
 * </ul>
 *
 * @version 1.2.39.4
 */
public class EntityListResolver implements LSResourceResolver, EntityResolver, URIResolver {
   public static final String XML_RESOURCES = "http://www.w3.org/TR/REC-xml";
   public static final String SCHEMA_RESOURCES = "http://www.w3.org/2001/XMLSchema";
   private final Map<String, URL> resolvedEntities = new HashMap<>();
   private final Map<String, URL> resolvedSystemIDs = new HashMap<>();
   private final Map<String, URL> resolvedBareSystemIDs = new HashMap<>();
   private final Map<String, URL> baseURIs = new HashMap<>();
   private URL defaultParentURI = null;
   private boolean cacheSources = false;
   private final Map<String, InputSource> streams = new HashMap<>();

   public EntityListResolver() {
   }

   public EntityListResolver(URL baseURL) {
      this();
      defaultParentURI = FileUtilities.getParentURL(baseURL);
   }

   /**
    * Add default resolved entities.
    *
    * <ul>
    * <li>http://www.w3.org/2001/xml.xsd</li>
    * <li>http://www.w3.org/2009/01/xml.xsd</li>
    * <li>http://www.w3.org/2007/08/xml.xsd</li>
    * <li>http://www.w3.org/2004/10/xml.xsd</li>
    * <li>http://www.w3.org/2001/03/xml.xsd</li>
    * <li>http://www.w3.org/2001/XMLSchema</li>
    * </ul>
    */
   public void addDefaultResolvedEntities() {
      URL resource = EntityListResolver.class.getResource("xml.xsd");
      addResolvedEntity(XML_RESOURCES, resource);
      addResolvedEntity("http://www.w3.org/2001/xml.xsd", resource);
      addResolvedEntity("http://www.w3.org/2009/01/xml.xsd", resource);
      addResolvedEntity("http://www.w3.org/2007/08/xml.xsd", resource);
      addResolvedEntity("http://www.w3.org/2004/10/xml.xsd", resource);
      addResolvedEntity("http://www.w3.org/2001/03/xml.xsd", resource);
      addResolvedEntity(XMLConstants.W3C_XML_SCHEMA_NS_URI, EntityListResolver.class.getResource("xmlSchema.xsd"));
   }

   /**
    * Clear the content of the resolver.
    */
   public void reset() {
      streams.clear();
   }

   /**
    * Set if sources are cached.
    *
    * @param cache true if sources are cached
    */
   public void cacheSources(boolean cache) {
      this.cacheSources = cache;
   }

   /**
    * Return true if sources are cached. Sources are not cached by default.
    *
    * @return true if sources are cached
    */
   public boolean isCachingSources() {
      return cacheSources;
   }

   /**
    * Add an entity resolution to the resolver, for a specific systemID.
    *
    * @param systemId the system ID
    * @param entity the URL to use for the entity resolution
    */
   public void addResolvedSystemID(String systemId, URL entity) {
      resolvedSystemIDs.put(systemId, entity);
      URL parent = FileUtilities.getParentURL(entity);
      baseURIs.put(parent.toString(), entity);
   }

   /**
    * Add an entity resolution to the resolver, for a list of systemIDs.
    *
    * @param systemIDs the list of systemIDs
    * @param entity the URL to use for the entity resolution
    */
   public void addResolvedSystemIDs(List<String> systemIDs, URL entity) {
      Iterator<String> it = systemIDs.iterator();
      while (it.hasNext()) {
         resolvedSystemIDs.put(it.next(), entity);
         URL parent = FileUtilities.getParentURL(entity);
         baseURIs.put(parent.toString(), entity);
      }
   }

   /**
    * Add an entity resolution to the resolver.
    *
    * @param publicId the public ID
    * @param entity the URL to use for the entity resolution
    */
   public final void addResolvedEntity(String publicId, URL entity) {
      resolvedEntities.put(publicId, entity);
      URL parent = FileUtilities.getParentURL(entity);
      baseURIs.put(parent.toString(), entity);

      String name = FileUtilities.getFileName(entity);
      String ext = FileUtilities.getFileExtension(entity);
      if (ext != null) {
         name = name + "." + ext;
      }
      resolvedBareSystemIDs.put(name, entity);
   }

   /**
    * Return the resolved entities in the resolver.
    *
    * @return the resolved entities in the resolver
    */
   public Map<String, URL> getResolvedEntities() {
      return resolvedEntities;
   }

   /**
    * Return true if the resolver resolves a specified systemID.
    *
    * @param systemId the system ID
    * @return true if the resolver resolves the specified system ID
    */
   public boolean isResolvingSystemID(String systemId) {
      boolean resolved = resolvedSystemIDs.containsKey(systemId);
      if (!resolved) {
         if (isHTPProtocol(systemId)) {
            resolved = resolvedEntities.containsKey(systemId);
         }
      }
      return resolved;
   }

   /**
    * Return true if the resolver is resolving system entities.
    *
    * @return true if the resolver is resolving system entities
    */
   public boolean isResolvingSystemEntities() {
      return !resolvedSystemIDs.isEmpty();
   }

   /**
    * Return true if the resolver is resolving entities.
    *
    * @return true if the resolver is resolving entities
    */
   public boolean isResolvingEntities() {
      return !resolvedEntities.isEmpty();
   }

   /**
    * Return an InputSource for a systemID and an URL.
    *
    * @param url the URL
    * @param publicId the public ID
    * @param systemId the systemID
    * @return the InputSource
    * @throws IOException if it is not posible to open the stream corresponding to the URL
    */
   protected InputSource getSource(URL url, String publicId, String systemId) throws IOException {
      if (cacheSources) {
         if (streams.containsKey(systemId)) {
            InputSource source = streams.get(systemId);
            InputStream stream = source.getByteStream();
            try {
               stream.available();
               source.getByteStream().reset();
               return source;
            } catch (IOException e) {
               source = new InputSource(url.openStream());
               source.setSystemId(systemId);
               source.setPublicId(publicId);
               streams.put(systemId, source);
               source.getByteStream().mark(0);
               return source;
            }
         } else {
            InputSource source = new InputSource(url.openStream());
            source.setSystemId(systemId);
            source.setPublicId(publicId);
            streams.put(systemId, source);
            source.getByteStream().mark(0);
            return source;
         }
      } else {
         InputSource source = new InputSource(url.openStream());
         source.setSystemId(systemId);
         source.setPublicId(publicId);
         return source;
      }
   }

   void endDocument() {
      Iterator<InputSource> it = streams.values().iterator();
      while (it.hasNext()) {
         InputSource source = it.next();
         try {
            source.getByteStream().close();
         } catch (IOException ex) {
         }
      }
   }

   /**
    * Return a baseURI or a systemId as a String. The default implementation simply return the input String.
    *
    * @param uriAsString the baseURI or systemId
    * @return the return the resolved baseURI or systemId
    */
   protected String resolveURI(String uriAsString) {
      return uriAsString;
   }

   /**
    * Resolve the entity for one systemID. Will return null if there is no entity resolution for the selected systemID.
    *
    * @param baseURI the base URI
    * @param systemId the systemID
    * @return the resolved entity
    */
   public InputSource resolveSystemID(String baseURI, String systemId) {
      if (systemId != null) {
         systemId = resolveURI(systemId);
      }
      if (baseURI != null) {
         baseURI = resolveURI(baseURI);
      }
      if (resolvedSystemIDs.containsKey(systemId)) {
         URL url = resolvedSystemIDs.get(systemId);
         try {
            InputSource source = getSource(url, null, systemId);
            return source;
         } catch (IOException ex) {
            return null;
         }
      } else if (isHTPProtocol(systemId)) {
         return resolve(systemId);
      } else if (baseURI == null) {
         if (resolvedBareSystemIDs.containsKey(systemId)) {
            URL url = resolvedBareSystemIDs.get(systemId);
            try {
               InputSource source = getSource(url, null, systemId);
               return source;
            } catch (IOException ex) {
               return null;
            }
         } else {
            Iterator<URL> it = resolvedBareSystemIDs.values().iterator();
            while (it.hasNext()) {
               URL refURL = it.next();
               URL parentURL = FileUtilities.getParentURL(refURL);
               if (parentURL == null) {
                  parentURL = defaultParentURI;
               }
               URL url = FileUtilities.getChildURL(parentURL, systemId);
               if (FileUtilities.exist(url)) {
                  try {
                     InputSource source = getSource(url, null, systemId);
                     return source;
                  } catch (IOException ex) {
                     return null;
                  }
               }
            }
            URL parentURL = defaultParentURI;
            URL url = FileUtilities.getChildURL(parentURL, systemId);
            if (FileUtilities.exist(url)) {
               try {
                  InputSource source = getSource(url, null, systemId);
                  return source;
               } catch (IOException ex) {
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         try {
            URL url;
            if (!FileUtilities.isAbsoluteURI(systemId)) {
               URL baseURL = new URL(baseURI);
               URL parentURL = FileUtilities.getParentURL(baseURL);
               url = FileUtilities.getChildURL(parentURL, systemId);
            } else {
               url = new URL(systemId);
            }
            if (FileUtilities.exist(url)) {
               InputSource source = getSource(url, null, systemId);
               return source;
            } else {
               return null;
            }
         } catch (IOException ex) {
            return null;
         }
      }
   }

   private boolean isHTPProtocol(String systemID) {
      return systemID.startsWith("http:") || systemID.startsWith("https:");
   }

   private URL getBaseURL(String baseURI) {
      if (resolvedSystemIDs.containsKey(baseURI)) {
         return resolvedSystemIDs.get(baseURI);
      } else if (resolvedEntities.containsKey(baseURI)) {
         return resolvedEntities.get(baseURI);
      } else {
         try {
            URI uri = new URI(baseURI);
            URL url = uri.toURL();
            URL parentURL = FileUtilities.getParentURL(url);
            if (baseURIs.containsKey(parentURL.toString())) {
               URL parentBase = baseURIs.get(parentURL.toString());
               URL resultURL = FileUtilities.getChildURL(parentBase, FileUtilities.getFileName(url));
               return resultURL;
            } else {
               return null;
            }
         } catch (URISyntaxException | MalformedURLException ex) {
            return null;
         }
      }
   }

   /**
    * Resolve the entity for one publicID. Will return null if there is no entity resolution for the selected publicID.
    *
    * @param publicId the public ID
    * @return the resolved entity
    */
   public InputSource resolve(String publicId) {
      if (resolvedEntities.containsKey(publicId)) {
         URL url = resolvedEntities.get(publicId);
         try {
            InputSource source = getSource(url, publicId, null);
            return source;
         } catch (IOException ex) {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Allow to resolve external resources for Schema validation. Note that newly discovered resolutions can be added to the
    * {@link #getResolvedEntities()} Map (if a child resource is found because the baseURI was discovered, it will be added to the resolution Map).
    *
    * <h1>Algorithm</h1>
    * The way the resolver work for external resources is the following:
    * <ul>
    * <li>If the systemId is resolved, then return the associated resolved URL</li>
    * <li>Else if the baseURI is resolved, get the location of the systemId relative to the parent baseURI, and return the associated URL. It willalso
    * add the resulting absolute systemId in the list of {@link #getResolvedEntities()}</li>
    * <li>Else return null, and use the default resolution</li>
    * </ul>
    *
    * <h1>Examples</h1>
    * <h1>A simple example</h1>
    * <ul>
    * <li>If <code>http://www.my.site/mySchema.xsd</code> is resolved to the URL associated to <code>D:/myDirectory/myXMLSchema.xsd</code></li>
    * <li>And systemId = <code>http://www.my.site/mySchema.xsd</code></li>
    * <li>The resource will be the <code>LSInput</code> constructed with <code>D:/myDirectory/myXMLSchema.xsd</code></li>
    * </ul>
    *
    * <h1>A more complex example</h1>
    * <ul>
    * <li>If <code>http://www.my.site/mySchema.xsd</code> is resolved to the URL associated to <code>D:/myDirectory/myXMLSchema.xsd</code></li>
    * <li>And baseURI = <code>http://www.my.site/mySchema.xsd</code> and systemId = <code>subSchema.xsd</code></li>
    * <li>The resource will be the <code>LSInput</code> constructed with <code>D:/myDirectory/subSchema.xsd</code></li>
    * <li>We will add the following resolution: <code>http://www.my.site/subSchema.xsd"</code> to <code>D:/myDirectory/subSchema.xsd</code></li>
    * </ul>
    *
    * @param type The type of the resource being resolved. For XML [XML 1.0] resources (i.e. entities), applications must use the value
    * "http://www.w3.org/TR/REC-xml". For XML Schema [XML Schema Part 1] , applications must use the value "http://www.w3.org/2001/XMLSchema"
    * @param namespaceURI The namespace of the resource being resolved, e.g. the target namespace of the XML Schema
    * @param publicId The public identifier of the external entity being referenced, or null if no public identifier was supplied or if the resource
    * is not an entity
    * @param systemId The system identifier, a URI reference [IETF RFC 2396], of the external resource being referenced, or null if no system
    * identifier was supplied
    * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI
    * @return the resolved resource
    */
   @Override
   public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
      if (systemId != null) {
         systemId = resolveURI(systemId);
      }
      if (baseURI != null) {
         baseURI = resolveURI(baseURI);
      }
      if (systemId != null) {
         if (isResolvingSystemID(systemId)) {
            URLLSInput input;
            if (resolvedSystemIDs.containsKey(systemId)) {
               // if the systemId can be resolved, get its associated URL and return it as the LSInput
               input = new URLLSInput(resolvedSystemIDs.get(systemId));
            } else {
               // if the systemId can be resolved, get its associated URL and return it as the LSInput
               input = new URLLSInput(resolvedEntities.get(systemId));
            }
            input.setBaseURI(baseURI);
            input.setPublicId(publicId);
            input.setSystemId(systemId);
            return input;
         } else if (baseURI != null) {
            resolveBaseURI(publicId, systemId, baseURI);
         }
      } else if (baseURI != null) {
         try {
            URL url = FileUtilities.getURL(null, baseURI);
            if (FileUtilities.exist(url)) {
               return new URLLSInput(url);
            }
         } catch (MalformedURLException ex) {
            return null;
         }
      }
      return null;
   }

   private LSInput resolveBaseURI(String publicId, String systemId, String baseURI) {
      if (isResolvingSystemID(baseURI)) {
         // else if the baseURI is not null and resolved, we will get the associated URL for the systemId
         // relative to the base URI URL file "parent"
         // for example, if systemId is "subSchema.xsd", and the baseURI is "http://www.my.site/mySchema.xsd":
         // - if is "http://www.my.site/mySchema.xsd" is resolved as "D:/myDirectory/myXMLSchema.xsd",
         // - we will look for the URL: "D:/myDirectory/subSchema.xsd" for our systemId
         // - and we will add the following resolution : "http://www.my.site/subSchema.xsd" is resolved
         // as "D:/myDirectory/myXMLSchema.xsd"
         URL baseURL = getBaseURL(baseURI);
         URL parentURL = FileUtilities.getParentURL(baseURL);
         if (parentURL != null) {
            URL url = FileUtilities.getChildURL(parentURL, systemId);
            if (url == null) {
               return null;
            }
            String childURI = getBaseLocation(baseURI) + systemId;
            resolvedSystemIDs.put(childURI, url);
            URLLSInput input = new URLLSInput(url);
            input.setBaseURI(baseURI);
            input.setPublicId(publicId);
            input.setSystemId(systemId);
            return input;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Return the base location of a baseURI. It is the "parent URI" of the baseURI. For example, for "http://www.w3.org/2001/xml.xsd", we will return
    * "http://www.w3.org/2001/".
    *
    * @param baseURI the baseURI
    * @return the base location of the baseURI
    */
   private String getBaseLocation(String baseURI) {
      if (baseURI.lastIndexOf('/') != -1) {
         return baseURI.substring(0, baseURI.lastIndexOf('/') + 1);
      } else {
         return baseURI + "/";
      }
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
      if (resolvedEntities.containsKey(systemId)) {
         URL url = resolvedEntities.get(systemId);
         InputStream stream = url.openStream();
         return new InputSource(stream);
      } else {
         return null;
      }
   }

   @Override
   public Source resolve(String href, String base) throws TransformerException {
      InputSource source = resolveSystemID(base, href);
      if (source != null) {
         return new SAXSource(source);
      } else {
         return null;
      }
   }

   /**
    * An LSInput implementation.
    *
    * @version 1.2.39.2
    */
   public static class URLLSInput implements LSInput {
      private InputStream stream = null;
      private String publicId = null;
      private String systemId = null;
      private String baseURI = null;
      private String encoding = null;

      public URLLSInput(URL url) {
         try {
            stream = url.openStream();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      @Override
      public Reader getCharacterStream() {
         return null;
      }

      @Override
      public void setCharacterStream(Reader characterStream) {
      }

      @Override
      public InputStream getByteStream() {
         return stream;
      }

      @Override
      public void setByteStream(InputStream byteStream) {
         this.stream = byteStream;
      }

      @Override
      public String getStringData() {
         return null;
      }

      @Override
      public void setStringData(String stringData) {
      }

      @Override
      public String getSystemId() {
         return systemId;
      }

      @Override
      public void setSystemId(String systemId) {
         this.systemId = systemId;
      }

      @Override
      public String getPublicId() {
         return publicId;
      }

      @Override
      public void setPublicId(String publicId) {
         this.publicId = publicId;
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
         return encoding;
      }

      @Override
      public void setEncoding(String encoding) {
         this.encoding = encoding;
      }

      @Override
      public boolean getCertifiedText() {
         return true;
      }

      @Override
      public void setCertifiedText(boolean certifiedText) {
      }

   }
}
