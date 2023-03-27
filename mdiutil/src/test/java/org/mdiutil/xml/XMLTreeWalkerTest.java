/*------------------------------------------------------------------------------
* Copyright (C) 2012, 2016, 2017, 2021 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import static org.junit.Assert.*;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * XMLTreeWalkerTest.
 *
 * @version 1.2.20
 */
@RunWith(CategoryRunner.class)
@Category(cat = "xml")
public class XMLTreeWalkerTest {
   private static Document doc;

   @BeforeClass
   public static void setUpClass() throws Exception {
      createDocument();
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
      doc = null;
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   private static void createDocument() {
      try {
         DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = fac.newDocumentBuilder();
         try (InputStream stream = XMLTreeWalkerTest.class.getResourceAsStream("resources/walkerTest1.xml")) {
            doc = builder.parse(stream);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   /**
    * Test nextNode() from root to Document end.
    */
   @Test
   public void testnextNode() {
      System.out.println("XMLTreeWalkerTest : testnextNode");
      XMLTreeWalker walker = new XMLTreeWalker(doc.getDocumentElement());
      // node 1
      Node node = walker.nextNode();
      // do this because there is text nodes and we don't implement filtering for now
      while (node.getNodeType() != Node.ELEMENT_NODE) {
         node = walker.nextNode();
      }
      String name = node.getNodeName();
      assertSame(name, "node1");
      // node 11
      node = walker.nextNode();
      // do this because there is text nodes and we don't implement filtering for now
      while (node.getNodeType() != Node.ELEMENT_NODE) {
         node = walker.nextNode();
      }
      name = node.getNodeName();
      assertSame(name, "node11");
   }

   /**
    * Test nextNode() from Element to Element.
    */
   public void testnextNode2() {
      System.out.println("XMLTreeWalkerTest : testnextNode2");
      Node firstNode = doc.getElementsByTagName("node12").item(0);
      Node lastNode = doc.getElementsByTagName("node22").item(0);
      XMLTreeWalker walker = new XMLTreeWalker(firstNode, lastNode);
      // node 13
      Node node = walker.nextNode();
      // do this because there is text nodes and we don't implement filtering for now
      while (node.getNodeType() != Node.ELEMENT_NODE) {
         node = walker.nextNode();
      }
      String name = node.getNodeName();
      assertSame(name, "node13");
      String lastName = "";
      while (node != null) {
         if (node.getNodeType() == Node.ELEMENT_NODE) {
            lastName = node.getNodeName();
         }
         node = walker.nextNode();
      }
      assertSame(lastName, "node21");
   }

   /**
    * Test nextNode() from Element to end of Document.
    */
   public void testnextNode3() {
      System.out.println("XMLTreeWalkerTest : testnextNode3");
      XMLTreeWalker walker
         = new XMLTreeWalker(doc.getElementsByTagName("node12").item(0), null);
      // node 1
      Node node = walker.nextNode();
      String lastName = "";
      while (node != null) {
         if (node.getNodeType() == Node.ELEMENT_NODE) {
            lastName = node.getNodeName();
         }
         node = walker.nextNode();

      }
      assertSame(lastName, "node53");
   }
}
