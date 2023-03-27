/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the XMLSAXParser class.
 *
 * @version 0.9.53
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLSAXParser5Test {

   public XMLSAXParser5Test() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Check that the handler on the reader is correctly removed.
    */
   @Test
   public void testParseAndDelete() throws Exception {
      System.out.println("XMLSAXParser5Test : testParseAndDelete");

      XMLSAXParser parser = new XMLSAXParser();
      parser.setValidating(false);
      DefaultParserExceptionListener listener = new DefaultParserExceptionListener();
      ResolverSAXHandler handler = new ResolverSAXHandler();
      parser.setHandler(handler);
      parser.showExceptions(true);
      parser.showWarnings(true);
      URL xmlURL = XMLSAXParser5Test.class.getResource("resources/simpleXMLWithoutDTD.xml");
      Path source = Paths.get(xmlURL.toURI());
      File targetFile = File.createTempFile("temp", "xml");
      Path target = Paths.get(targetFile.toURI());
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

      Reader reader = new FileReader(targetFile);
      parser.parse(reader);
      List<SAXParseException> exceptions = parser.getHandler().getExceptions();
      assertTrue("Must have no SAXParserException", exceptions.isEmpty());
      assertTrue("Must have no SAXException", listener.getExceptions().isEmpty());

      targetFile.delete();
   }
}
