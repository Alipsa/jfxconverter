/*------------------------------------------------------------------------------
* Copyright (C) 2010 Herve Girod
* 
* Distributable under the terms of either the Apache License (Version 2.0) or 
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.util.prefs.PreferencesFactory;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/** A factory object that generates "in memory" Preferences objects.
 * @since 0.4
 */
public class MemoryPreferencesFactory implements PreferencesFactory {
    private static MemoryPreferencesFactory factory = null;
    private String appli = null;
    private MemoryPreferences systemroot = null;
    private MemoryPreferences userroot = null;
    private boolean userCreation = true;
    private boolean userToImport = false;
    private boolean systemCreation = true;
    private boolean systemToImport = false;
    
    private MemoryPreferencesFactory() {    
    }
    
    /** Create the NetworkPreferencesFactory.
     *
     * @param subDir the sub-directory of userDir or systemDir, under which to create 
     * the application preferences file (if null, there will be no sub-directory definition)
     * @param appli the name that will tag the factory to create. This name is useful for the
     * user root creation, as the user preferences are serialized in 
     * an xml file in the user.home directory, under a directory construted with the 
     * name of the application
     *
     * @throws BackingStoreException if the factory have already been created, or if the userDir or 
     * systemDir are not null and are not valid or are not already existing
     */
    public static MemoryPreferencesFactory newFactory(String subDir, String appli) throws BackingStoreException {
        if (subDir == null) subDir = "";
        factory = new MemoryPreferencesFactory();
        
        return factory;
    }
    
    /** get the uniquely defined factory.
     */
    public static MemoryPreferencesFactory getFactory() {
        return factory;
    }

    /** get the name that tag the factory for the application.
     */    
    public String getApplicationName() {
        return appli;
    }
    
    /** Returns the system root preference node. (Multiple calls on this
     * method will return the same object reference).
     * <p>The backing store of the user preferences will be a file in a directory
     * with the following name ".prefs", in the user.dir directory</p> 
     * @return the system root (the returned Preferences object is a 
     * {@link NetworkPreferences}
     */
    public Preferences systemRoot() {
        /* we look if there is an existing system backing store file at the first invocation,
         * in this case we will never try to import Preferences, because if the file has been created
         * later, it will be empty
         */
        if (systemCreation) {
            systemCreation = false;
        }
         systemroot = new MemoryPreferences(appli);
        return systemroot;
    }
    
    /** convenience method that return the same object as {@link #systemRoot()}.
     */
    public NetworkPreferences systemRootAsNetwork() {
        return (NetworkPreferences)systemRoot();
    }
        
    /** Returns the user root preference node corresponding to the calling
     * user (Multiple calls on this method will return the same object reference).
     * <p>The backing store of the user preferences will be a file in a directory
     * with the following name "." <application name>, in the user.home directory. The 
     * application name is the name wich has been defined at the creation of the factory.</p> 
     * @return the system root (the returned Preferences object is a 
     * {@link NetworkPreferences}
     */
    public Preferences userRoot() {
        /* we look if there is an existing user backing store file at the first invocation,
         * in this case we will never try to import Preferences, because if the file has been created
         * later, it will be empty
         */
        if (userCreation) {
            userCreation = false;
        }
         // now create the NetworkPreferences associated with the user if it is necessary   
        if (userroot == null) {
            userroot = new MemoryPreferences(appli);
        }
        return userroot;
    }
    
    /** convenience method that return the same object as {@link #userRoot()}.
     */
    public NetworkPreferences userRootAsNetwork() {
        return (NetworkPreferences)userRoot();
    }    
}
