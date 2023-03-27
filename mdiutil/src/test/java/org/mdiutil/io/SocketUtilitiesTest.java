/*------------------------------------------------------------------------------
 * Copyright (C) 2016, 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Units tests for the SocketUtilities class.
 *
 * @since 0.9.48
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class SocketUtilitiesTest {

   public SocketUtilitiesTest() {
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
    * Test of getFreePort method.
    */
   @Test
   public void testGetFreePort() {
      System.out.println("SocketUtilitiesTest : testGetFreePort");
      int port = SocketUtilities.getFreePort();
      assertTrue("Port should be greater than 0", port > 0);
      try {
         ServerSocket socket = new ServerSocket(port);
         socket.close();
      } catch (IOException ex) {
         fail("Could not open the port");
      }
   }

   /**
    * Test of getFreePort method.
    */
   @Test
   public void testGetFreePort2() {
      System.out.println("SocketUtilitiesTest : testGetFreePort2");
      int port = SocketUtilities.getFreePort(4500, 5500);
      assertTrue("Port should be greater than 0", port > 0);
      assertTrue("Port should be >= minPort", port >= 4500);
      assertTrue("Port should be <= maxPort", port <= 5500);
      try {
         ServerSocket socket = new ServerSocket(port);
         socket.close();
      } catch (IOException ex) {
         fail("Could not open the port");
      }
   }
}
