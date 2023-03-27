/*------------------------------------------------------------------------------
* Copyright (C) 2008, 2016 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/** A simple factory object that generates network Preferences objects, backed in a simple
 * File in the File System. These preferences
 * have the following properties :
 * <ul>
 * <li>the user preferences are serialized in an xml file, under a directory specific of the application</li>
 * <li>there are no system preferences</li>
 * </ul>
 * <ul>
 * </ul>
 * <p>
 * The effective backing stores will only be created after the invocation of {@link #systemRoot()} or {@link #userRoot()}</p>
 * <p>
 * For a user preference:
 * <ul>
 * <li>the backing store will be : &lt;userDir&gt;/subDir/.&lt;appli&gt;/pref.xml</li>
 * <li>if userDir is null : &lt;userDir&gt; = user.home</li>
 * <li>if subDir is null : subDir = ""</li>
 * </ul></p>
 *
 * @version 0.9.6
 */
public class FilePreferencesFactory implements PreferencesFactory {
   private File userFile = null;
   private NetworkPreferences userroot = null;
   private boolean userCreation = true;
   private boolean userToImport = false;

   public FilePreferencesFactory(File userFile) {
      this.userFile = userFile;
   }

   /**
    * Return the File used for the factory.
    *
    * @return the File
    */
   public File getFile() {
      return userFile;
   }

   /**
    * Always return null.
    *
    * @return null
    */
   @Override
   public Preferences systemRoot() {
      return null;
   }

   /**
    * Returns the user root preference node corresponding to the calling
    * user (Multiple calls on this method will return the same object reference).
    * <br/>
    * The backing store of the user preferences will be a file in a directory
    * with the following name "." &lt;application name&gt;, in the user.home directory. The
    * application name is the name wich has been defined at the creation of the factory.
    *
    * @return the user root preference node
    */
   @Override
   public Preferences userRoot() {
      /* we look if there is an existing user backing store file at the first invocation,
         * in this case we will never try to import Preferences, because if the file has been created
         * later, it will be empty
       */
      if (userCreation) {
         userCreation = false;
         userToImport = (userFile != null);
      }
      // now create the NetworkPreferences associated with the user if it is necessary

      if (userroot == null) {
         userroot = new NetworkPreferences(userFile);
      }
      if (userToImport) {
         userroot.importPreferences();
      }
      return userroot;
   }

   /**
    * A Convenience method that return the same object as {@link #userRoot()}.
    *
    * @return the userRoot
    */
   public NetworkPreferences userRootAsNetwork() {
      return (NetworkPreferences) userRoot();
   }

   NetworkPreferences getInternalUserRoot() {
      if (userroot == null) {
         userroot = new NetworkPreferences(userFile);
      }
      return (NetworkPreferences) userroot;
   }
}
