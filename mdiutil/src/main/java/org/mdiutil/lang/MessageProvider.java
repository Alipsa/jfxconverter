/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2016, 2019, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides several bundles of messages. It manages several bundle of messages, which are simply property files.
 * The way to use this class is:
 * <ul>
 * <li>Create one or several bundles. For example:
 * <pre>
 * MessageProvider provider = MessageProvider.getInstance();
 * provider.createBundle("aKey", "org/myresources/", "messages.properties");
 * </pre>
 * or
 * <pre>
 * MessageProvider provider = MessageProvider.getInstance();
 * provider.createBundle("anotherKey", url);
 * </pre>
 * </li>
 * <li>Reference one bundle and get some messages anywhere in the code:
 * <pre>
 * MessageBundle bundle = MessageProvider.getBundle("aKey");
 * String message = bundle.getMessage("propertyName");
 * </pre>
 * </li>
 * </ul>
 *
 * @version 1.2.8
 */
public class MessageProvider {
   private static MessageProvider provider = null;
   private final ErrorBundle errorBundle = new ErrorBundle();
   private final Map<String, MessageBundle> bundles = new HashMap<>();

   private MessageProvider() {
   }

   /**
    * Return the default bundle defined in the case where the bundle with a specified name does not exist.
    *
    * @return the default bundle
    */
   protected MessageBundle getErrorBundle() {
      return errorBundle;
   }

   /**
    * Clear the bundles.
    */
   public void clear() {
      bundles.clear();
   }

   /**
    * Create a bundle with a specified key, resource file path and the context class loader.
    *
    * @param key the bundle key
    * @param pack the resource package
    * @param path the resource path
    * @return the bundle
    *
    * @see ResourceLoader
    */
   public MessageBundle createBundle(String key, String pack, String path) {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      return createBundle(key, pack, path, loader);
   }

   /**
    * Create a bundle with a specified key, resource file path and class loader.
    *
    * @param key the bundle key
    * @param pack the resource package
    * @param path the resource path
    * @param loader the class loader to use to retrieve the resource
    * @return the bundle
    *
    * @see ResourceLoader
    */
   public MessageBundle createBundle(String key, String pack, String path, ClassLoader loader) {
      if (bundles.containsKey(key)) {
         return bundles.get(key);
      } else {
         // load resources
         ResourceLoader rloader = new ResourceLoader(pack, loader);
         URL url = rloader.getURL(path);
         MessageBundle bundle = new MessageBundle();
         bundle.initialize(url);
         bundles.put(key, bundle);
         return bundle;
      }
   }

   /**
    * Create a bundle with a specified key, and resource URL.
    *
    * @param key the bundle key
    * @param url the resource URL
    * @return the bundle
    */
   public MessageBundle createBundle(String key, URL url) {
      if (bundles.containsKey(key)) {
         return bundles.get(key);
      } else {
         MessageBundle bundle = new MessageBundle();
         bundle.initialize(url);
         bundles.put(key, bundle);
         return bundle;
      }
   }

   /**
    * Return the Map of bundles.
    */
   private Map<String, MessageBundle> getBundles() {
      return bundles;
   }

   /**
    * Return the single provider instance.
    *
    * @return the provider
    */
   public static MessageProvider getInstance() {
      if (provider == null) {
         provider = new MessageProvider();
      }
      return provider;
   }

   /**
    * Return the bundle with a specified key. If there is no bundle with the specified key, an empty bundle will be returned
    * instead.
    *
    * @param key the bundle key
    * @return the bundle
    */
   public static MessageBundle getBundle(String key) {
      if (provider == null) {
         provider = new MessageProvider();
      }
      MessageBundle bundle = provider.getBundles().get(key);
      if (bundle == null) {
         return provider.getErrorBundle();
      }
      return bundle;
   }

   /**
    * The empty error bundle, used in the case where no bundle is found.
    *
    * @version 0.8.6
    */
   private static class ErrorBundle extends MessageBundle {
      /**
       * Do nothing.
       */
      @Override
      protected void initialize(URL url) {
      }

      /**
       * Return an error message.
       */
      @Override
      public String getMessage(String key) {
         return "Error, no bundle with the specified name";
      }

      /**
       * Return an error message.
       */
      @Override
      public String getMessage(String key, Object... vars) {
         return "Error, no bundle with the specified name";
      }
   }
}
