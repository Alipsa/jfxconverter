/*------------------------------------------------------------------------------
* Copyright (C) 2016, 2017, 2018, 2020, 2021, 2022 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test the XMLNodeUtilities print method.
 *
 * @version 1.2.34
 */
@RunWith(OrderedRunner.class)
@Category(cat = "xml")
@Category(cat = "xmltree")
public class XMLNodeUtilitiesPrintTest {

   public XMLNodeUtilitiesPrintTest() {
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
    * Test of print method.
    */
   @Test
   public void testPrint() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrint2() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint2.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element id=\"theID\" name=\"first\">").append("\n");
      buf.append("    <element id=\"theID2\" name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrint3() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint3");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint2.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2, "ISO-8859-1");
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element id=\"theID\" name=\"first\">").append("\n");
      buf.append("    <element id=\"theID2\" name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrint4() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint4");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint6.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2);
      StringBuilder buf = new StringBuilder();
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrint5() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint5");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint6.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2, "UTF-8");
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrint6() {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrint6");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint7.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      String xml = XMLNodeUtilities.print(root, 2, "UTF-8");
      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">the first element<element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\">the third element</element>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrintInFile() throws IOException {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrintInFile");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      File file = File.createTempFile("testMDIUtilities", ".xml");
      XMLNodeUtilities.print(root, 2, file);

      XMLNode root2 = XMLNodeUtilities.getRootNode(file.toURI().toURL());
      String xml = XMLNodeUtilities.print(root2, 2);

      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      file.delete();
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrintInFile2() throws IOException {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrintInFile2");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint4.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      File file = File.createTempFile("testMDIUtilities", ".xml");
      XMLNodeUtilities.print(root, 2, file);

      XMLNode root2 = XMLNodeUtilities.getRootNode(file.toURI().toURL());
      String xml = XMLNodeUtilities.print(root2, 2);

      StringBuilder buf = new StringBuilder();
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      file.delete();
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrintInFile3() throws IOException {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrintInFile3");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint5.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      File file = File.createTempFile("testMDIUtilities", ".xml");
      XMLNodeUtilities.print(root, 2, file);

      XMLNode root2 = XMLNodeUtilities.getRootNode(file.toURI().toURL());
      String xml = XMLNodeUtilities.print(root2, 2);

      StringBuilder buf = new StringBuilder();
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\">").append("\n");
      buf.append("      <element name=\"fourth\"/>").append("\n");
      buf.append("    </element>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      file.delete();
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrintInFile4() throws IOException {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrintInFile4");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      File file = File.createTempFile("testMDIUtilities", ".xml");
      XMLNodeUtilities.print(root, 2, file, "ISO-8859-1");

      XMLNode root2 = XMLNodeUtilities.getRootNode(file.toURI().toURL());
      String xml = XMLNodeUtilities.print(root2, 2);

      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\"/>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      file.delete();
   }

   /**
    * Test of print method.
    */
   @Test
   public void testPrintInFile5() throws IOException {
      System.out.println("XMLNodeUtilitiesPrintTest : testPrintInFile5");
      URL xmlURL = XMLTreeHandler.class.getResource("resources/xmlPrint5.xml");
      XMLNode root = XMLNodeUtilities.getRootNode(xmlURL);

      File file = File.createTempFile("testMDIUtilities", ".xml");
      XMLNodeUtilities.print(root, 2, file);

      XMLNode root2 = XMLNodeUtilities.getRootNode(file.toURI().toURL());
      String xml = XMLNodeUtilities.print(root2, 2, "UTF-8");

      StringBuilder buf = new StringBuilder();
      buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      buf.append("<root desc=\"example\">").append("\n");
      buf.append("  <element name=\"first\">").append("\n");
      buf.append("    <element name=\"second\">").append("\n");
      buf.append("      <element name=\"fourth\"/>").append("\n");
      buf.append("    </element>").append("\n");
      buf.append("    <element name=\"third\"/>").append("\n");
      buf.append("  </element>").append("\n");
      buf.append("</root>");
      assertEquals("XML content", buf.toString(), xml);

      file.delete();
   }
}
