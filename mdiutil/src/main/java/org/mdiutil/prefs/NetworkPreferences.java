/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2008, 2015, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides a Network aware implementation of the java.util.prefs.Preferences
 * class. These preferences have the following properties :
 * <ul>
 * <li>the user preferences are serialized in an xml file in a directory</li>
 * <li>the system preferences are serialized in an xml file in a directory</li>
 * </ul>
 * <p>
 * This ensures that the preferences will follow the "nomad" user.</p>
 *
 * @see NetworkPreferencesFactory
 *
 * @version 1.2.9
 */
public class NetworkPreferences extends AbstractPreferences {
   /**
    * The user node type.
    */
   public static final int USER_NODE = 0;
   /**
    * The system node type.
    */
   public static final int SYSTEM_NODE = 1;
   protected File prefImpl = null;
   protected NetworkPreferences myparent = null;
   private File dir = null;
   private int handle = USER_NODE;
   private Map<String, Object> map = new TreeMap();
   protected Map<String, Preferences> nodes = new TreeMap<>();
   private static final String FILE_PREF_NAME = "pref.xml";

   /**
    * Create a new user Node NetworkPreferences, using a File as root.
    *
    * @param root the File root
    */
   protected NetworkPreferences(File root) {
      super(null, "");
      try {
         prefImpl = root;
         if (!prefImpl.exists()) {
            prefImpl.createNewFile();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Create a new NetworkPreferences, using a File as root.
    *
    * @param root the File root
    * @param appli the Application tag
    */
   protected NetworkPreferences(File root, String appli) {
      super(null, "");

      if (appli == null) {
         System.out.println("Can't create preferences with no application name");
      } else {
         try {
            prefImpl = root;
            if (!prefImpl.exists()) {
               prefImpl.createNewFile();
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   /**
    * Create a new NetworkPreferences.
    * <ul>
    * <li>If the backing store file does not exist, create it</li>
    * </ul>
    *
    * @param handle the handle
    * @param file the backing store file
    */
   protected NetworkPreferences(int handle, File file) {
      super(null, "");
      this.handle = handle;
      dir = file.getParentFile();
      prefImpl = file;
   }

   /**
    * Create a new NetworkPreferences.
    * <ul>
    * <li>If the backing store file does not exist, create it</li>
    * </ul>
    *
    * @param handle the handle
    * @param rootDir the backing store root directory
    * @param appli the Application tag
    */
   protected NetworkPreferences(int handle, File rootDir, String appli) {
      super(null, "");
      if (appli == null) {
         System.out.println("Can't create preferences with no application name");
      } else {
         this.handle = handle;
         // create the backing store preference file if it doesn't exist
         try {
            /* we create a system or user preference file, this file will be created
             * directly under the root/appli directory.
             * the name of the preference file will be "pref.xml".
             * It is necessary, because in some cases (definition of a sub-directory in the factory),
             * the root can have to be created if it does not exist
             */
            if (!rootDir.exists()) {
               rootDir.mkdir();
            }
            dir = getPrefDirectory(rootDir, appli);
            if (!dir.exists()) {
               dir.mkdir();
            }

            prefImpl = new File(dir, FILE_PREF_NAME);
            if (!prefImpl.exists()) {
               prefImpl.createNewFile();
            }
         } catch (IOException e) {
            System.out.println(e.getMessage());
         }
      }
   }

   /**
    * Return true if the backing store file exists.
    *
    * @return true if the backing store file exists
    */
   public boolean fileExists() {
      return prefImpl.exists() && prefImpl.length() > 0;
   }

   /**
    * Import the preferences in the prefImpl backing store file to this preference.
    */
   void importPreferences() {
      try {
         if (fileExists()) {
            try (FileInputStream stream = new FileInputStream(prefImpl)) {
               importPreferences(stream);
            }
         }
      } catch (IOException e) {
         System.out.println(e.getMessage());
      } catch (InvalidPreferencesFormatException e) {
         if (prefImpl.exists()) {
            prefImpl.delete();
         }
         System.out.println(e.getMessage());
      }
   }

   /**
    * Creates a preference node with the specified parent and the specified name relative to its parent.
    *
    * @param parent the parent of this preference node, or null if this is the root.
    * @param name the name of this preference node, relative to its parent, or <tt>""</tt> if this is the root.
    * @throws IllegalArgumentException if <tt>name</tt> contains a slash (<tt>'/'</tt>), or <tt>parent</tt> is <tt>null</tt> and
    * name isn't <tt>""</tt>.
    */
   protected NetworkPreferences(NetworkPreferences parent, String name) {
      // Need to do this and not super(null, "") else the remove() method return
      // an Exception, which is normal because in that case it is assumed that the node has
      // no parent and so is the root!!
      super(parent, name);
      parent.nodes.put(name, this);
      this.myparent = parent;
      if (parent.isUserNode()) {
         handle = NetworkPreferences.USER_NODE;
      } else {
         handle = NetworkPreferences.SYSTEM_NODE;
      }
   }

   /**
    * Put the given key-value association into this preference node.
    */
   @Override
   protected void putSpi(String key, String value) {
      map.put(key, value);
   }

   @Override
   public void put(String key, String value) {
      putSpi(key, value);
   }

   /**
    * Return the value associated with the specified key at this preference
    * node, or <tt>null</tt> if there is no association for this key, or the
    * association cannot be determined at this time.
    *
    * @return the value associated with the specified key at this preference
    * node, or <tt>null</tt> if there is no association for this
    * key, or the association cannot be determined at this time.
    */
   @Override
   protected String getSpi(String key) {
      return (String) (map.get(key));
   }

   /**
    * Remove the association (if any) for the specified key at this preference node.
    */
   @Override
   protected void removeSpi(String key) {
      map.remove(key);
   }

   /**
    * Removes this preference node, invalidating it and any preferences that
    * it contains. The named child will have no descendants at the time this
    * invocation is made (i.e., the {@link Preferences#removeNode()} method
    * invokes this method repeatedly in a bottom-up fashion, removing each of
    * a node's descendants before removing the node itself).
    *
    * <p>
    * The removal of a node will not become persistent until the
    * <tt>flush</tt> method is invoked on this node (or an ancestor).
    *
    * @throws BackingStoreException if this operation cannot be completed
    * due to a failure in the backing store, or inability to
    * communicate with it.
    */
   @Override
   protected void removeNodeSpi() throws BackingStoreException {
      NetworkPreferences parent = (NetworkPreferences) parent();
      parent.nodes.remove(name());
   }

   /**
    * Returns all of the keys that have an associated value in this
    * preference node. (The returned array will be of size zero if
    * this node has no preferences).
    *
    * <p>
    * If this node throws a <tt>BackingStoreException</tt>, the exception
    * will propagate out beyond the enclosing {@link #keys()} invocation.
    *
    * @return an array of the keys that have an associated value in this
    * preference node.
    * @throws BackingStoreException if this operation cannot be completed
    * due to a failure in the backing store, or inability to
    * communicate with it.
    */
   @Override
   protected String[] keysSpi() throws BackingStoreException {
      try {
         String[] s = (String[]) map.keySet().toArray(new String[map.size()]);
         return s;
      } catch (NullPointerException | ClassCastException e) {
         throw new BackingStoreException(e.getMessage());
      }
   }

   /**
    * Returns the names of the children of this preference node. (The
    * returned array will be of size zero if this node has no children.)
    *
    * <p>
    * If this node throws a <tt>BackingStoreException</tt>, the exception
    * will propagate out beyond the enclosing {@link #childrenNames()}
    * invocation.
    *
    * @return an array containing the names of the children of this
    * preference node.
    * @throws BackingStoreException if this operation cannot be completed
    * due to a failure in the backing store, or inability to
    * communicate with it.
    */
   @Override
   protected String[] childrenNamesSpi() throws BackingStoreException {
      try {
         String[] s = (String[]) nodes.keySet().toArray(new String[nodes.size()]);
         return s;
      } catch (NullPointerException | ClassCastException e) {
         throw new BackingStoreException(e.getMessage());
      }
   }

   /**
    * Returns the named child of this preference node, creating it if it does
    * not already exist.
    *
    * @param name The name of the child node to return, relative to
    * this preference node.
    * @return The named child node.
    */
   @Override
   protected AbstractPreferences childSpi(String name) {
      Object obj = nodes.get(name);
      if (obj != null) {
         return (NetworkPreferences) obj;
      } else {
         NetworkPreferences child = new NetworkPreferences(this, name);
         return child;
      }
   }

   /**
    * Empty, never used implementation of AbstractPreferences.syncSpi().
    */
   @Override
   protected void syncSpi() throws BackingStoreException {
   }

   /**
    * Identical to flush().
    */
   @Override
   public void sync() throws BackingStoreException {
      flush();
   }

   /**
    * Empty, never used implementation of AbstractPreferences.flushSpi().
    */
   @Override
   protected void flushSpi() throws BackingStoreException {
   }

   @Override
   public void flush() throws BackingStoreException {
      try {
         NetworkPreferences pref = this;
         NetworkPreferences oldpref = this;
         // only flush the whole preferences tree
         while (pref != null && pref.getParent() != null) {
            pref = (NetworkPreferences) (pref.getParent());
            if (pref != null) {
               oldpref = (NetworkPreferences) pref;
            }
         }
         if (pref != null) {
            try (FileOutputStream stream = new FileOutputStream(oldpref.prefImpl)) {
               pref.exportSubtree(stream);
            }
         }
      } catch (FileNotFoundException e) {
         throw new BackingStoreException(e.getMessage());
      } catch (IOException e) {
         throw new BackingStoreException(e.getMessage());
      }
   }

   // METHODS FIXING THE CORE JAVA PREFERENCES DEPENDENCE PROBLEM
   /**
    * @return true if this Preference is in the user preference tree.
    */
   @Override
   public boolean isUserNode() {
      if (handle == NetworkPreferences.USER_NODE) {
         return true;
      } else {
         return false;
      }
   }

   public Object getLock() {
      return lock;
   }

   public boolean isInRemoveLock() {
      return isRemoved();
   }

   NetworkPreferences getParent() {
      return myparent;
   }

   /**
    * Implements the <tt>exportNode</tt> method as per the specification in
    * {@link Preferences#exportNode(OutputStream)}.
    *
    * @param os the output stream on which to emit the XML document.
    * @throws IOException if writing to the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws BackingStoreException if preference data cannot be read from
    * backing store.
    */
   @Override
   public void exportNode(OutputStream os) throws IOException, BackingStoreException {
      XMLParser.export(os, this, false);
   }

   /**
    * Implements the <tt>exportSubtree</tt> method as per the specification in
    * {@link Preferences#exportSubtree(OutputStream)}.
    *
    * @param os the output stream on which to emit the XML document.
    * @throws IOException if writing to the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws BackingStoreException if preference data cannot be read from
    * backing store.
    */
   @Override
   public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
      XMLParser.export(os, this, true);
   }

   /**
    * Imports all of the preferences represented by the XML document on the
    * specified File. The document may represent user preferences or
    * system preferences. If it represents user preferences, the preferences
    * will be imported into the calling user's preference tree (even if they
    * originally came from a different user's preference tree). If any of
    * the preferences described by the document inhabit preference nodes that
    * do not exist, the nodes will be created.
    *
    * @param file the File from which to read the XML document.
    * @throws IOException if reading from the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws InvalidPreferencesFormatException Data on input stream does not
    * constitute a valid XML document with the mandated document type.
    */
   public static void importPreferences(File file)
      throws IOException, InvalidPreferencesFormatException {
      XMLParser.importPreferences(file);
   }

   /**
    * Imports all of the preferences represented by the XML document on the
    * specified input stream. The document may represent user preferences or
    * system preferences. If it represents user preferences, the preferences
    * will be imported into the calling user's preference tree (even if they
    * originally came from a different user's preference tree). If any of
    * the preferences described by the document inhabit preference nodes that
    * do not exist, the nodes will be created.
    *
    * @param is the input stream from which to read the XML document.
    * @throws IOException if reading from the specified output stream
    * results in an <tt>IOException</tt>.
    * @throws InvalidPreferencesFormatException Data on input stream does not
    * constitute a valid XML document with the mandated document type.
    */
   public static void importPreferences(InputStream is)
      throws IOException, InvalidPreferencesFormatException {
      XMLParser.importPreferences(is);
   }

   public static Preferences userNodeForPackage(Class c) {
      NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
      NetworkPreferences prefs = (NetworkPreferences) (fac.userRoot());
      String name = myNodeName(c);
      NetworkPreferences newPref;
      if (prefs.getNodeImpl().containsKey(name)) {
         newPref = (NetworkPreferences) (prefs.getNodeImpl().get(name));
      } else {
         newPref = new NetworkPreferences(prefs, myNodeName(c));
      }

      return newPref;
   }

   public static Preferences systemNodeForPackage(Class c) {
      NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
      NetworkPreferences prefs = (NetworkPreferences) (fac.systemRoot());
      String name = myNodeName(c);
      NetworkPreferences newPref;
      if (prefs.getNodeImpl().containsKey(name)) {
         newPref = (NetworkPreferences) (prefs.getNodeImpl().get(name));
      } else {
         newPref = new NetworkPreferences(prefs, myNodeName(c));
      }

      return newPref;
   }

   /**
    * Returns the absolute path name of the node corresponding to the package
    * of the specified object.
    *
    * @throws IllegalArgumentException if the package has node preferences
    * node associated with it.
    */
   private static String myNodeName(Class c) {
      if (c.isArray()) {
         throw new IllegalArgumentException("Arrays have no associated preferences node.");
      }
      String className = c.getName();
      int pkgEndIndex = className.lastIndexOf('.');
      if (pkgEndIndex < 0) {
         return "unnamed";
      }
      String packageName = className.substring(0, pkgEndIndex);
      return packageName;
   }

   // SPECIFIC METHODS
   /**
    * Return the backstore Hashtable implementation for the nodes.
    * this is not used by this implementation, but can be used by subclasses.
    *
    * @return the backstore Hashtable implementation for the nodes
    */
   protected Map<String, Preferences> getNodeImpl() {
      return nodes;
   }

   /**
    * Return the backstore Hashtable implementation for the (key, value) map.
    * this is not used by this implementation, but can be used by subclasses.
    *
    * @return the backstore Hashtable implementation for the (key, value) map
    */
   protected Map<String, Object> getMapImpl() {
      return map;
   }

   /**
    * Return the backing store file for this preference.
    *
    * @return the backing store file for this preference
    */
   public File getImpl() {
      return prefImpl;
   }

   @Override
   public Preferences node(String name) {
      Preferences pref = nodes.get(name);
      if (pref != null) {
         return nodes.get(name);
      } else {
         return childSpi(name);
      }
   }

   /**
    * Return true if there is a user backing store file. This is the case when :
    * <ul>
    * <li>there is a user preference directory for this application</li>
    * <li>there is an XML preference file under this directory</li>
    * </ul>
    */
   static boolean hasRootImpl(File dir, String appli) {
      File rootDir = getPrefDirectory(dir, appli);
      if ((!rootDir.exists()) || (!rootDir.isDirectory())) {
         return false;
      } else {
         File impl = new File(rootDir, FILE_PREF_NAME);
         if (!impl.exists() || impl.length() == 0) {
            return false;
         }
         return true;
      }
   }

   private static File getPrefDirectory(File root, String appli) {
      File pref = new File(root, "." + appli);
      return pref;
   }
}
