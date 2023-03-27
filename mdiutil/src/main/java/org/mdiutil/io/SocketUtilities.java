/*------------------------------------------------------------------------------
* Copyright (C) 2018 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class provides several socket utilities.
 *
 * @since 0.9.48
 */
public class SocketUtilities {
   /**
    * Return a free available port. Return -1 is no available port can be found. Note that the implementation creates a temporary ServerSocket with specifying the
    * 0 port to get any available port.
    *
    * @return the free available port
    */
   public static int getFreePort() {
      try (ServerSocket socket = new ServerSocket(0)) {
         return socket.getLocalPort();
      } catch (IOException e) {
         return -1;
      }
   }

   /**
    * Return a free available port between two ports. The initial port to be chosen will be a random port between minPort and maxPort. Also only at maximum
    * <code>maxPort - minPort</code> trials will be tried. The method will return -1 if no port between min and max are available.
    *
    * @param minPort the minimum port
    * @param maxPort the maximum port
    * @return the free available port
    */
   public static int getFreePort(int minPort, int maxPort) {
      return getFreePort(minPort, maxPort, maxPort - minPort);
   }

   /**
    * Return a free available port between two ports. The initial port to be chosen will be a random port between minPort and maxPort. Also only at maximum
    * <code>maxTries</code> trials will be tried. The method will return -1 if no port between min and max are available.
    *
    * @param minPort the minimum port
    * @param maxPort the maximum port
    * @param maxTries the maximum number of tries to get the free port
    * @return the free available port
    */
   public static int getFreePort(int minPort, int maxPort, int maxTries) {
      if (minPort > maxPort) {
         return -1;
      }
      int trials = 0;
      Random r = new Random();
      int port = r.nextInt(maxPort - minPort) + minPort;
      while (true) {
         if (isLocalPortFree(port)) {
            return port;
         } else if (trials < maxTries) {
            port = ThreadLocalRandom.current().nextInt(minPort, maxPort);
            trials++;
         } else {
            return -1;
         }
      }
   }

   private static boolean isLocalPortFree(int port) {
      try {
         new ServerSocket(port).close();
         return true;
      } catch (IOException e) {
         return false;
      }
   }
}
