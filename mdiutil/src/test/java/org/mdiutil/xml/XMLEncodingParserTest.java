/*------------------------------------------------------------------------------
* Copyright (C) 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @since 1.2.39
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLEncodingParserTest {
   
   public XMLEncodingParserTest() {
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
    * Test of getEncoding method, of class XMLEncodingParser.
    */
   @Test
   public void testGetEncoding() {
      System.out.println("XMLEncodingParserTest : getEncoding");
      URL url = XMLEncodingParserTest.class.getResource("resources/encoding1.xml");
      String encoding = XMLEncodingParser.getEncoding(url);
      assertNotNull("Encoding must no be null", encoding);
      assertEquals("Encoding must be UTF-8", "UTF-8", encoding);
   }
   
   /**
    * Test of getEncoding method, of class XMLEncodingParser.
    */
   @Test
   public void testGetEncoding2() {
      System.out.println("XMLEncodingParserTest : testGetEncoding2");
      URL url = XMLEncodingParserTest.class.getResource("resources/encoding2.xml");
      String encoding = XMLEncodingParser.getEncoding(url);
      assertNotNull("Encoding must no be null", encoding);
      assertEquals("Encoding must be ISO-8859-1", "ISO-8859-1", encoding);
   }   
   
   /**
    * Test of getEncoding method, of class XMLEncodingParser.
    */
   @Test
   public void testGetEncoding3() {
      System.out.println("XMLEncodingParserTest : testGetEncoding3");
      URL url = XMLEncodingParserTest.class.getResource("resources/encoding3.xml");
      String encoding = XMLEncodingParser.getEncoding(url);
      assertNotNull("Encoding must no be null", encoding);
      assertEquals("Encoding must be UTF-8", "UTF-8", encoding);
   }      
}
