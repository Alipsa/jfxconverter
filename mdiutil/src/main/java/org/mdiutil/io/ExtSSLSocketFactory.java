/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.SSLSocketFactory;

/**
 * An SSLSocketFactory which adds some cipher suites.
 *
 * @since 0.9.58
 */
class ExtSSLSocketFactory extends SSLSocketFactory {
   private static final String PREFERRED_CIPHER_SUITE = "TLS_RSA_WITH_AES_128_CBC_SHA";
   private final SSLSocketFactory delegate;

   public ExtSSLSocketFactory(SSLSocketFactory delegate) {
      this.delegate = delegate;
   }

   @Override
   public String[] getDefaultCipherSuites() {
      return setupPreferredDefaultCipherSuites(this.delegate);
   }

   @Override
   public String[] getSupportedCipherSuites() {
      return setupPreferredSupportedCipherSuites(this.delegate);
   }

   @Override
   public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
      Socket socket = this.delegate.createSocket(host, port);
      String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
      ((javax.net.ssl.SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

      return socket;
   }

   @Override
   public Socket createSocket(InetAddress host, int port) throws IOException {
      Socket socket = this.delegate.createSocket(host, port);
      String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
      ((javax.net.ssl.SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

      return socket;
   }

   @Override
   public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
      Socket socket = this.delegate.createSocket(s, host, port, autoClose);
      String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
      ((javax.net.ssl.SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

      return socket;
   }

   @Override
   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
      Socket socket = this.delegate.createSocket(host, port, localHost, localPort);
      String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
      ((javax.net.ssl.SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

      return socket;
   }

   @Override
   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      Socket socket = this.delegate.createSocket(address, port, localAddress, localPort);
      String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
      ((javax.net.ssl.SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

      return socket;
   }

   private static String[] setupPreferredDefaultCipherSuites(SSLSocketFactory sslSocketFactory) {
      String[] defaultCipherSuites = sslSocketFactory.getDefaultCipherSuites();
      ArrayList<String> suitesList = new ArrayList<>(Arrays.asList(defaultCipherSuites));
      suitesList.remove(PREFERRED_CIPHER_SUITE);
      suitesList.add(0, PREFERRED_CIPHER_SUITE);

      return suitesList.toArray(new String[suitesList.size()]);
   }

   private static String[] setupPreferredSupportedCipherSuites(SSLSocketFactory sslSocketFactory) {
      String[] supportedCipherSuites = sslSocketFactory.getSupportedCipherSuites();
      ArrayList<String> suitesList = new ArrayList<>(Arrays.asList(supportedCipherSuites));
      suitesList.remove(PREFERRED_CIPHER_SUITE);
      suitesList.add(0, PREFERRED_CIPHER_SUITE);

      return suitesList.toArray(new String[suitesList.size()]);
   }
}
