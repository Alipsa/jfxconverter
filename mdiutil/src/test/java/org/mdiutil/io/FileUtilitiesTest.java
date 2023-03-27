/*------------------------------------------------------------------------------
 * Copyright (C) 2011 , 2012, 2013, 2015, 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;
import org.mdiutil.lang.SystemUtils;

/**
 * Unit tests for FileUtilities.
 *
 * @version 0.9.25
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilitiesTest {

   /**
    * Test of isHttp method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testIsHTTPProtocol() {
      System.out.println("FileUtilitiesTest : testIsHTTPProtocol");
      URL url = null;
      try {
         url = new URL("http://someOtherWebAddress/applet/test");
      } catch (MalformedURLException ex) {
         ex.printStackTrace();
         fail("Unable to create URL");
      }
      boolean isHTTP = FileUtilities.isHTTPProtocol(url);
      assertTrue("URL must be HTTP", isHTTP);
   }

   /**
    * Test of isHttp method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testIsHTTPProtocol2() {
      System.out.println("FileUtilitiesTest : testIsHTTPProtocol2");
      URL url = null;
      try {
         url = new URL("jar:http://someOtherWebAddress/applet/test!/entry");
      } catch (MalformedURLException ex) {
         ex.printStackTrace();
         fail("Unable to create URL");
      }
      boolean isHTTP = FileUtilities.isHTTPProtocol(url);
      assertTrue("URL must be HTTP", isHTTP);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetChildURL() {
      System.out.println("FileUtilitiesTest : testGetChildURL");
      URL url = null;
      try {
         url = new URL("file:/data/app/arinc661.server.android-1.apk!/arinc661/server/android/resources");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "DefGraphics.xml";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetChildURL2() {
      System.out.println("FileUtilitiesTest : testGetChildURL2");
      URL url = null;
      try {
         url = new URL("file:/data/app%20/arinc661.server.android-1.apk!/arinc661/server/android/resources");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "DefGraphics.xml";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
      String path = childURL.getPath();
      assertEquals("Child URL", "/data/app%20/arinc661.server.android-1.apk!/arinc661/server/android/resources/DefGraphics.xml", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetChildURL3() {
      System.out.println("FileUtilitiesTest : testGetChildURL3");
      URL url = null;
      try {
         url = new URL("http://someWebAddress/applet/test");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "http://someOtherWebAddress/applet/test";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      assertEquals("Child URL protocol", "http", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "someOtherWebAddress", host);
      String path = childURL.getPath();
      assertEquals("Child URL path", "/applet/test", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetChildURL4() {
      System.out.println("FileUtilitiesTest : testGetChildURL4");
      URL url = null;
      try {
         url = new URL("http://someWebAddress/applet");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "test";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      assertEquals("Child URL protocol", "http", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "someWebAddress", host);
      String path = childURL.getPath();
      assertEquals("Child URL path", "/applet/test", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetChildURL5() {
      System.out.println("FileUtilitiesTest : testGetChildURL5");
      if (!SystemUtils.isWindowsPlatform()) {
         System.out.println("FileUtilities.getChildURL() only works on windows, skipping");
         return;
      }
      URL url = null;
      try {
         url = new URL("http://someWebAddress/applet");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "/test";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      System.out.println("ChildUrl = " + childURL);
      assertEquals("Child URL protocol", "http", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "someWebAddress", host);
      String path = childURL.getPath();
      assertEquals("Child URL path", "/applet/test", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetChildURL6() {
      System.out.println("FileUtilitiesTest : testGetChildURL6");
      URL url = null;
      try {
         url = new URL("http://someWebAddress/applet/android.apk!/arinc661/server/android/resources");
      } catch (MalformedURLException ex) {
         fail("Unable to create URL");
      }
      String childName = "DefGraphics.xml";
      URL childURL = FileUtilities.getChildURL(url, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      assertEquals("Child URL protocol", "http", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "someWebAddress", host);
      String path = childURL.getPath();
      assertEquals("Child URL path", "/applet/android.apk!/arinc661/server/android/resources/DefGraphics.xml", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetChildURL7() {
      System.out.println("FileUtilitiesTest : testGetChildURL7");
      String childName = "http://someWebAddress/applet/android.jar";
      URL childURL = FileUtilities.getChildURL(null, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      assertEquals("Child URL protocol", "http", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "someWebAddress", host);
      String path = childURL.getPath();
      assertEquals("Child URL path", "/applet/android.jar", path);
   }

   /**
    * Test of GetChildURL method, of class FileUtilities.
    */
   @Test
   public void testGetChildURL8() throws IOException {
      System.out.println("FileUtilitiesTest : testGetChildURL8");
      String path = "file://mnt/hgfs/ATL2/ARINCServeur/a661/IHM_ATL2/data/myFile.xml";
      URL lookURL = new URL(path);
      URL parentURL = FileUtilities.getParentURL(lookURL);
      String childName = "test.css";
      URL childURL = FileUtilities.getChildURL(parentURL, childName);
      assertNotNull("Child URL must not be null", childURL);
      String protocol = childURL.getProtocol();
      assertEquals("Child URL protocol", "file", protocol);
      String host = childURL.getHost();
      assertEquals("Child URL host", "mnt", host);
      String childPath = childURL.getPath();
      assertEquals("Child URL path", "/hgfs/ATL2/ARINCServeur/a661/IHM_ATL2/data/test.css", childPath);
   }

   /**
    * Test of getParentURL method, of class FileUtilities. We test in the context of URLs paths.
    */
   @Test
   public void testGetParentURL() throws IOException {
      System.out.println("FileUtilitiesTest : testGetParentURL");
      URL url = new URL("file:/L:/WRK//toto/test/resources/myFile.xml");
      URL parentURL = FileUtilities.getParentURL(url);
      assertNotNull("parentURL must not be null", parentURL);
      String protocol = parentURL.getProtocol();
      assertEquals("parentURL protocol", "file", protocol);
      String host = parentURL.getHost();
      assertEquals("parentURL host", "", host);
      String path = parentURL.getPath();
      assertEquals("parentURL path", "/L:/WRK/toto/test/resources", path);
   }

   /**
    * Test of sameURL method, of class FileUtilities.
    */
   @Test
   public void testSameURL() throws IOException {
      System.out.println("FileUtilitiesTest : testSameURL");
      URL url1 = new URL("file:/L:/WRK//toto/test/resources/myFile.xml");
      URL url2 = new URL("file:/L:/WRK//toto/test/resources/myFile.xml");
      boolean isEquals = FileUtilities.sameURL(url1, url2);
      assertTrue("URL 1 must be equal to URL2", isEquals);
   }

   /**
    * Test of sameURL method, of class FileUtilities.
    */
   @Test
   public void testSameURL2() throws IOException {
      System.out.println("FileUtilitiesTest : testSameURL2");
      URL url1 = new URL("file:/L:/WRK/toto/test/resources/myFile.xml");
      URL url2 = new URL("file:/L:/WRK/titi/test/resources/myFile.xml");
      boolean isEquals = FileUtilities.sameURL(url1, url2);
      assertFalse("URL 1 must not be equal to URL2", isEquals);
   }

   /**
    * Test of sameURL method, of class FileUtilities.
    */
   @Test
   public void testSameURL3() throws IOException {
      System.out.println("FileUtilitiesTest : testSameURL3");
      URL url1 = new URL("file", null, "/L:/WRK/toto\\test/resources/myFile.xml");
      URL url2 = new URL("file:/L:/WRK/toto/test/resources/myFile.xml");
      boolean isEquals = FileUtilities.sameURL(url1, url2);
      assertTrue("URL 1 must be equal to URL2", isEquals);
   }
}
