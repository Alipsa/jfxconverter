/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2010, 2015, 2016, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * A factory object that generates network Preferences objects. These preferences have the following properties :
 * <ul>
 * <li>the user preferences are serialized in an xml file, under
 * a directory specific of the application</li>
 * <li>the system preferences are serialized in an xml file in the user.dir directory</li>
 * </ul>
 * <p>
 * This ensures that the preferences will follow a "nomad" user.</p>
 * A NetworkPreferencesFactory is unique and have the following characteristics :
 * <ul>
 * <li>a user directory root, under which all user {@link NetworkPreferences} must be stored.
 * If the corresponding file is null, the user.home directory will be used</li>
 * <li>a system directory root, under which all system {@link NetworkPreferences} must be stored.
 * If the corresponding file is null, the system.home directory will be used</li>
 * <li>an sub-directory under which to put the specific application system and user direcrories</li>
 * <li>the application name</li>
 * </ul>
 * <p>
 * The effective backing stores will only be created after the invocation of {@link #systemRoot()}
 * or {@link #userRoot()}</p>
 * <p>
 * For a user preference :
 * <ul>
 * <li>the backing store will be : &lt;userDir&gt;/subDir/.&lt;appli&gt;/pref.xml</li>
 * <li>if userDir is null : &lt;userDir&gt; = user.home</li>
 * <li>if subDir is null : subDir = ""</li>
 * </ul></p>
 * <p>
 * For a system preference :
 * <ul>
 * <li>the backing store will be : &lt;systemDir&gt;/subDir/pref.xml</li>
 * <li>if systemDir is null, the backing store will be : user.dir/pref.xml</li>
 * <li>if subDir is null : subDir = ""</li>
 * </ul></p>
 * <p>
 * Examples:
 * <ul>
 * <li>for <code>NetworkPreferencesFactory(null, null, null, "test")</code> :
 * <ul>
 * <li>user preferences backing store : user.home/.test/pref.xml</li>
 * <li>system preferences backing store : user.dir/pref.xml</li>
 * </ul>
 * </li>
 * <li>for <code>NetworkPreferencesFactory(new File("c:/user"), new File("c:/system"), "subDir", "test")</code> :
 * <ul>
 * <li>user preferences backing store : c:/user/subDir/.test/pref.xml</li>
 * <li>system preferences backing store : c:/system/subDir/.test/pref.xml</li>
 * </ul>
 * </li>
 * <li>for <code>NetworkPreferencesFactory(null, null, "subDir", "test")</code> :
 * <ul>
 * <li>user preferences backing store : user.home/subDir/.test/pref.xml</li>
 * <li>system preferences backing store : user.dir/pref.xml</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 *
 * @version 1.2.9
 */
public class NetworkPreferencesFactory implements PreferencesFactory {
   private static NetworkPreferencesFactory factory = null;
   private String appli = null;
   private File systemDir = null;
   private File userDir = null;
   private NetworkPreferences systemroot = null;
   private NetworkPreferences userroot = null;
   private boolean userCreation = true;
   private boolean userToImport = false;
   private boolean systemCreation = true;
   private boolean systemToImport = false;

   private NetworkPreferencesFactory(File userDir, File systemDir, String appli) {
      this.userDir = userDir;
      this.systemDir = systemDir;
      this.appli = appli;
   }

   /**
    * Create the factory.
    *
    * @param userDir the root directory under which will be stored the user preferences (null if it is the user.home directory)
    * @param systemDir the root directory under which will be stored the system preferences (null if it is the user.dir directory)
    * @param subDir the sub-directory of userDir or systemDir, under which to create the application preferences
    * file (if null, there will be no sub-directory definition)
    * @param appli the name that will tag the factory to create. This name is useful for the
    * user root creation, as the user preferences are serialized in an xml file in the user.home directory,
    * under a directory construted with the name of the application
    * @return the NetworkPreferencesFactory
    *
    * @throws BackingStoreException if the factory have already been created, or if the userDir or
    * systemDir are not null and are not valid or are not already existing
    */
   public static NetworkPreferencesFactory newFactory(File userDir, File systemDir, String subDir, String appli) throws BackingStoreException {
      if (factory != null) {
         throw new BackingStoreException("Factory already created");
      } else if ((userDir != null) && (!userDir.isDirectory()) && userDir.exists()) {
         throw new BackingStoreException("No valid user root");
      } else if ((systemDir != null) && (!systemDir.isDirectory()) && systemDir.exists()) {
         throw new BackingStoreException("No valid system root");
      } else {
         if (subDir == null) {
            subDir = "";
         }
         if (userDir == null) {
            userDir = new File(System.getProperty("user.home"), subDir);
         } else {
            userDir = new File(userDir, subDir);
         }
         if (systemDir == null) {
            systemDir = new File(System.getProperty("user.dir"));
         } else {
            systemDir = new File(systemDir, subDir);
         }

         factory = new NetworkPreferencesFactory(userDir, systemDir, appli);
      }

      return factory;
   }

   /**
    * Reset the factory. This will set the internally defined {@link #getFactory()} to null.
    */
   public static void reset() {
      factory = null;
   }

   /**
    * Return the uniquely defined factory.
    *
    * @return the uniquely defined factory
    */
   public static NetworkPreferencesFactory getFactory() {
      return factory;
   }

   /**
    * Return the name that tag the factory for the application.
    *
    * @return the name that tag the factory for the application
    */
   public String getApplicationName() {
      return appli;
   }

   /**
    * Return the system root directory.
    *
    * @return the system root directory
    */
   public File getSystemDirectory() {
      return systemDir;
   }

   /**
    * Return the user root directory.
    *
    * @return the user root directory
    */
   public File getUserDirectory() {
      return userDir;
   }

   /**
    * Returns the system root preference node. (Multiple calls on this
    * method will return the same object reference).
    * <p>
    * The backing store of the user preferences will be a file in a directory
    * with the following name ".prefs", in the user.dir directory</p>
    *
    * @return the system root (the returned Preferences object is a
    * {@link NetworkPreferences}
    */
   @Override
   public Preferences systemRoot() {
      /* we look if there is an existing system backing store file at the first invocation,
       * in this case we will never try to import Preferences, because if the file has been created
       * later, it will be empty
       */
      if (systemCreation) {
         systemCreation = false;
         systemToImport = NetworkPreferences.hasRootImpl(systemDir, appli);
      }
      // now create the NetworkPreferences associated with the system if it is necessary
      if (systemroot == null) {
         systemroot = new NetworkPreferences(NetworkPreferences.SYSTEM_NODE, systemDir, appli);
      } else if (!systemroot.getImpl().exists()) {
         systemroot = new NetworkPreferences(NetworkPreferences.SYSTEM_NODE, systemDir, appli);
      }
      if (systemToImport) {
         systemroot.importPreferences();
      }
      return systemroot;
   }

   public static Preferences systemNodeForPackage(Class<?> c) {
      if (c == NetworkPreferencesFactory.class) {
         if (factory == null) {
            // Not sure if this is correct
            try {
               newFactory(null, null, null, c.getName());
            } catch (BackingStoreException e) {
               throw new RuntimeException(e);
            }
         }
         return factory.systemRoot();
      } else {
         return null;
      }
   }

   /**
    * Convenience method that return the same object as {@link #systemRoot()}.
    *
    * @return the System root
    */
   public NetworkPreferences systemRootAsNetwork() {
      return (NetworkPreferences) systemRoot();
   }

   /**
    * Returns the user root preference node corresponding to the calling
    * user (Multiple calls on this method will return the same object reference).
    * <p>
    * The backing store of the user preferences will be a file in a directory
    * with the following name "." &lt;application name&gt;, in the user.home directory. The
    * application name is the name wich has been defined at the creation of the factory.</p>
    *
    * @return the system root (the returned Preferences object is a
    * {@link NetworkPreferences}
    */
   @Override
   public Preferences userRoot() {
      /* we look if there is an existing user backing store file at the first invocation,
       * in this case we will never try to import Preferences, because if the file has been created
       * later, it will be empty
       */
      if (userCreation) {
         userCreation = false;
         userToImport = NetworkPreferences.hasRootImpl(userDir, appli);
      }
      // now create the NetworkPreferences associated with the user if it is necessary
      if (userroot == null) {
         userroot = new NetworkPreferences(NetworkPreferences.USER_NODE, userDir, appli);
      } else if (!userroot.fileExists()) {
         userroot = new NetworkPreferences(NetworkPreferences.USER_NODE, userDir, appli);
      }
      if (userToImport) {
         userroot.importPreferences();
      }
      return userroot;
   }

   /**
    * Convenience method that return the same object as {@link #userRoot()}.
    *
    * @return the user root
    */
   public NetworkPreferences userRootAsNetwork() {
      return (NetworkPreferences) userRoot();
   }

   NetworkPreferences getInternalUserRoot(File file) {
      if (userroot == null) {
         if (file == null) {
            userroot = new NetworkPreferences(NetworkPreferences.USER_NODE, userDir, appli);
         } else {
            userroot = new NetworkPreferences(NetworkPreferences.USER_NODE, file);
         }
      }
      return (NetworkPreferences) userroot;
   }

   NetworkPreferences getInternalSystemRoot(File file) {
      if (systemroot == null) {
         if (file == null) {
            systemroot = new NetworkPreferences(NetworkPreferences.SYSTEM_NODE, systemDir, appli);
         } else {
            systemroot = new NetworkPreferences(NetworkPreferences.SYSTEM_NODE, file);
         }
      }
      return (NetworkPreferences) systemroot;
   }
}
