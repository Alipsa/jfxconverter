/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
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
 * Unit tests for FileUtilities. Unit tests for the isAbsolute method.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities21Test {

   /**
    * Test of isAbsolute method.
    */
   @Test
   public void testIsAbsolute() throws IOException {
      System.out.println("FileUtilities21Test : testIsAbsolute");
      boolean isAbsolute = FileUtilities.isAbsolute("L:/WRK/Java/toto/model/core/myFile.html");
      if(SystemUtils.isWindowsPlatform())
         assertTrue("The path must be absolute", isAbsolute);
      else
         assertFalse("The path must not be absolute", isAbsolute);
   }

   /**
    * Test of isAbsolute method.
    */
   @Test
   public void testIsAbsolute2() throws IOException {
      System.out.println("FileUtilities21Test : testIsAbsolute2");
      boolean isAbsolute = FileUtilities.isAbsolute("/model/core/myFile.html");
      if (SystemUtils.isWindowsPlatform())
         assertFalse("The path must not be absolute", isAbsolute);
      else
         assertTrue("The path must be absolute", isAbsolute);
   }

   /**
    * Test of isAbsolute method.
    */
   @Test
   public void testIsAbsolute3() throws IOException {
      System.out.println("FileUtilities21Test : testIsAbsolute3");
      boolean isAbsolute = FileUtilities.isAbsolute("http://model/core/myFile.html");
      assertTrue("The path must be absolute", isAbsolute);
   }

   /**
    * Test of isAbsolute method.
    */
   @Test
   public void testIsAbsolute4() throws IOException {
      System.out.println("FileUtilities21Test : testIsAbsolute4");
      boolean isAbsolute = FileUtilities.isAbsolute("http://someOtherWebAddress/applet/test.jar");
      assertTrue("The path must be absolute", isAbsolute);
   }
}
