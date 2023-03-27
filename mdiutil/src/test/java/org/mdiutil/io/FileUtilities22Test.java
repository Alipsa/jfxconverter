/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.*;

import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for FileUtilities. Unit tests for the getChildURL method.
 *
 * @since 1.2.29
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class FileUtilities22Test {

   /**
    * Test of getChildURL method.
    * TODO: does not work on linux
    */
   @Test
   @Ignore
   public void testGetChildURL() throws IOException {
      System.out.println("FileUtilities21Test : testGetChildURL");
      File dir = new File("L:/WRK/Java/docgenerator/docgenerator");
      URL parentURL = dir.toURI().toURL();
      String path = "C:/Users/scdsahv/AppData/Local/Temp/output.tmp";
      URL url = FileUtilities.getChildURL(parentURL, path);
      File theFile = new File(path);
      URL expectedURL = theFile.toURI().toURL();
      assertEquals("The URL", expectedURL, url);
   }
}
