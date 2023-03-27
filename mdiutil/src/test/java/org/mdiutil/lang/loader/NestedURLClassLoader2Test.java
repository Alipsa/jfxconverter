/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URLClassLoader;
import org.junit.runner.RunWith;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @version 1.2.23
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class NestedURLClassLoader2Test {

   /**
    * Test of Class.forName method, of class NestedURLClassLoader.
    */
   @Test
   public void testLoadClass() throws Exception {
      System.out.println("NestedURLClass2Loader : testLoadClass");
      URL embedZipURL = this.getClass().getResource("/testutils/testEmbedZipped.zip");
      assertNotNull("Failed to find testEmbedZipped.zip", embedZipURL);
      URL jarURL = FileUtilities.getJarEntryURL(embedZipURL, "TestEmbed.jar");

      URL[] urls = new URL[1];
      urls[0] = jarURL;
      NestedURLClassLoader loader = new NestedURLClassLoader(urls);
      Class clazz = Class.forName("org.testutils.embed.EmbeddedClass2", true, loader);
      Object o = clazz.newInstance();
      Class[] params = new Class[0];
      Method m = clazz.getMethod("getFilteredClass", params);
      Object[] args = new Object[0];
      Object o2 = m.invoke(o, args);
      clazz = o2.getClass();
      assertTrue("FilteredClass ClassLoader", clazz.getClassLoader() instanceof NestedURLClassLoader);
      m = clazz.getMethod("getValue", params);
      args = new Object[0];
      Object value = m.invoke(o2, args);
      assertEquals("Value", 2, value);
   }
}
