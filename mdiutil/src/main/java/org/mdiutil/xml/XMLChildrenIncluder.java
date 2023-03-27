/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import org.xml.sax.ErrorHandler;

/**
 * This interface process XML files by including the content of XInclude children.
 *
 * @version 1.2.41
 */
public interface XMLChildrenIncluder {
   /**
    * Set the includer file.
    *
    * @param file the file
    * @throws MalformedURLException
    */
   public void setFile(File file) throws MalformedURLException;

   /**
    * Set the includer URL.
    *
    * @param url the URL
    */
   public void setURL(URL url);

   /**
    * Set the includer InputStream.
    *
    * @param stream the InputStream
    */
   public void setInputStream(InputStream stream);

   /**
    * Set if a comment must be added for each included file.
    *
    * @param addComments true if a comment must be added for each included file
    */
   public default void setAddComments(boolean addComments) {
      setAddComments(addComments, false);
   }

   /**
    * Set if a comment must be added for each included file.
    *
    * @param addComments true if a comment must be added for each included file
    * @param deep true if the comments in deep include declarations must be added
    */
   public void setAddComments(boolean addComments, boolean deep);

   /**
    * Return true if a comment must be added for each included file.
    *
    * @return true if a comment must be added for each included file
    */
   public boolean isAddingComments();

   /**
    * Set the charset to use to write the content. The default charset is {@link java.nio.charset.StandardCharsets#UTF_8}.
    *
    * @param charset the charset
    */
   public void setCharset(Charset charset);

   /**
    * Return the charset to use to write the content.
    *
    * @return the charset
    */
   public Charset getCharset();

   /**
    * Return true if the comments in deep include declarations must be added.
    *
    * @return true if the comments in deep include declarations must be added
    */
   public boolean isEnablingDeepComments();

   /**
    * Set the ErrorHandler which will be fired for warnings and errors when
    * parsing the content.
    *
    * @param errorHandler the ErrorHandler
    */
   public void setErrorHandler(ErrorHandler errorHandler);

   /**
    * Return the ErrorHandler which will be fired for warnings and errors when
    * parsing the content.
    *
    * @return the ErrorHandler
    */
   public ErrorHandler getErrorHandler();

   /**
    * Set the default base directory for including files. This is useful to
    * detect relative XML files when applying the xinclude declarations if the
    * input is an InputStream.
    *
    * @param baseDir the default base directory
    */
   public void setDefaultBaseDirectory(URL baseDir);

   /**
    * Set the SAXParser factory implementation.
    *
    * @param factoryClassName the factory class name
    */
   public default void setSAXParserFactoryImplementation(String factoryClassName) {
      XMLSAXParser.setSAXParserFactoryImplementation(factoryClassName);
   }

   /**
    * Reset the SAXParser factory implementation to the default.
    */
   public static void resetSAXParserFactoryImplementation() {
      XMLSAXParser.resetSAXParserFactoryImplementation();
   }

   /**
    * Return a String representing the XML content.
    *
    * @return the StringReader
    */
   public String getContent();

   /**
    * Return a Reader to the XML content.
    *
    * @return the StringReader
    */
   public Reader getReader();

   /**
    * Write the resulting XML to a File.
    *
    * @param file the File
    * @throws IOException
    */
   public void write(File file) throws IOException;

   /**
    * Write the resulting XML to an URL.
    *
    * @param url the URL
    * @throws IOException
    */
   public void write(URL url) throws IOException;
}
