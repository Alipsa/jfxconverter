/*------------------------------------------------------------------------------
* Copyright (C) 2010 Herve Girod
* 
* Distributable under the terms of either the Apache License (Version 2.0) or 
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.HashMap;
import java.util.Map;

/** This class provides an in memory implementation of the java.util.prefs.Preferences
 * class.
 *
 * @since 0.4
 */
public class MemoryPreferences extends AbstractPreferences {
    public static final int USER_NODE = 0;
    public static final int SYSTEM_NODE = 1;
    protected MemoryPreferences myparent = null;
    private String name = null;
    private int handle = USER_NODE;
    private Map<String, Object> map = new HashMap(10);
    protected Map<String, Preferences> nodes = new HashMap(1);
    
    /** create a new user Node NetworkPreferences. 
     */
    protected MemoryPreferences(String name) {
        super(null, name);
        this.name = name;
    }    
    
    /**
     * Creates a preference node with the specified parent and the specified
     * name relative to its parent.
     *
     * @param parent the parent of this preference node, or null if this
     *               is the root.
     * @param name the name of this preference node, relative to its parent,
     *             or <tt>""</tt> if this is the root.
     * @throws IllegalArgumentException if <tt>name</tt> contains a slash
     *          (<tt>'/'</tt>),  or <tt>parent</tt> is <tt>null</tt> and
     *          name isn't <tt>""</tt>.  
     */
    protected MemoryPreferences(MemoryPreferences parent, String name) {
        // FIXED SVGLab 101: need to do this and not super(null, "") else the remove() method return 
        // an Exception, which is normal because in that case it is assumed that the node has 
        // no parent and so is the root !!
        super(parent, name);
        parent.nodes.put(name, this);
        this.myparent = parent;
        if (parent.isUserNode()) handle = MemoryPreferences.USER_NODE;
        else handle = MemoryPreferences.SYSTEM_NODE;
    }
    
    /**
     * Put the given key-value association into this preference node.
     */
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
     *          node, or <tt>null</tt> if there is no association for this
     *          key, or the association cannot be determined at this time.
     */
    protected String getSpi(String key) {
        return (String)(map.get(key));
    }

    /**
     * Remove the association (if any) for the specified key at this 
     * preference node.
     */
    protected void removeSpi(String key) {
        map.remove(key);
    }

    /** 
     * Removes this preference node, invalidating it and any preferences that
     * it contains.  The named child will have no descendants at the time this
     * invocation is made (i.e., the {@link Preferences#removeNode()} method
     * invokes this method repeatedly in a bottom-up fashion, removing each of
     * a node's descendants before removing the node itself).
     *
     * <p>The removal of a node will not become persistent until the
     * <tt>flush</tt> method is invoked on this node (or an ancestor).
     *
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to 
     *         communicate with it.
     */
    protected void removeNodeSpi() throws BackingStoreException {
        MemoryPreferences parent = (MemoryPreferences)parent();
        parent.nodes.remove(name());
    }

    /**
     * Returns all of the keys that have an associated value in this
     * preference node.  (The returned array will be of size zero if
     * this node has no preferences).
     *
     * <p>If this node throws a <tt>BackingStoreException</tt>, the exception
     * will propagate out beyond the enclosing {@link #keys()} invocation.
     *
     * @return an array of the keys that have an associated value in this
     *         preference node.
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to 
     *         communicate with it.
     */
    protected String[] keysSpi() throws BackingStoreException {
        try {
            String[] s = (String[])map.keySet().toArray(new String[map.size()]);
            return s;
        } catch (NullPointerException e) {
            throw new BackingStoreException(e.getMessage());
        } catch (ClassCastException e) {
            throw new BackingStoreException(e.getMessage());            
        }
    }

    /**
     * Returns the names of the children of this preference node.  (The
     * returned array will be of size zero if this node has no children.)
     *
     * <p>If this node throws a <tt>BackingStoreException</tt>, the exception
     * will propagate out beyond the enclosing {@link #childrenNames()}
     * invocation.
     *
     * @return an array containing the names of the children of this
     *         preference node.
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to 
     *         communicate with it.
     */
    protected String[] childrenNamesSpi() throws BackingStoreException {
        try {
            String[] s = (String[])nodes.keySet().toArray(new String[nodes.size()]);
            return s;
        } catch (NullPointerException e) {
            throw new BackingStoreException(e.getMessage());
        } catch (ClassCastException e) {
            throw new BackingStoreException(e.getMessage());            
        }
    }

    /** 
     * Returns the named child of this preference node, creating it if it does
     * not already exist.
     *
     * @param name The name of the child node to return, relative to
     *        this preference node.
     * @return The named child node.
     */
    protected AbstractPreferences childSpi(String name) {
        Object obj = nodes.get(name);
        if (obj != null) return (MemoryPreferences)obj;
        else {
            MemoryPreferences child = new MemoryPreferences(this, name);
            return child;
        }
    }

   /** Empty, never used implementation of AbstractPreferences.syncSpi(). 
    */
    protected void syncSpi() throws BackingStoreException {
    }

   /** Identical to flush(). 
    */
    @Override    
    public void sync() throws BackingStoreException {
        flush();
    }
    
   /** Empty, never used implementation of AbstractPreferences.flushSpi(). 
    */
    protected void flushSpi() throws BackingStoreException {
    }    
    
    @Override    
    public void flush() throws BackingStoreException {
    }   
    
    // METHODS FIXING THE CORE JAVA PREFERENCES DEPENDENCE PROBLEM
    
    /** @return true if this Preference is in the user preference tree.
     */
    @Override    
    public boolean isUserNode() {
        if (handle == MemoryPreferences.USER_NODE) return true;
        else return false;
    }
    
   public Object getLock() {
       return lock;
   }
   
   public boolean isInRemoveLock() {
       return isRemoved();
   }
   
   MemoryPreferences getParent() {
       return myparent;
   }
   
    /**
     * Implements the <tt>exportNode</tt> method as per the specification in
     * {@link Preferences#exportNode(OutputStream)}.
     *
     * @param os the output stream on which to emit the XML document.
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws BackingStoreException if preference data cannot be read from
     *         backing store.
     */
    @Override   
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {   
    }

    /**
     * Implements the <tt>exportSubtree</tt> method as per the specification in
     * {@link Preferences#exportSubtree(OutputStream)}.
     *
     * @param os the output stream on which to emit the XML document.
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws BackingStoreException if preference data cannot be read from
     *         backing store.
     */
    @Override    
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {       
    } 
    
    /**
     * Imports all of the preferences represented by the XML document on the
     * specified input stream.  The document may represent user preferences or
     * system preferences.  If it represents user preferences, the preferences
     * will be imported into the calling user's preference tree (even if they
     * originally came from a different user's preference tree).  If any of
     * the preferences described by the document inhabit preference nodes that
     * do not exist, the nodes will be created.
     *
     * @param is the input stream from which to read the XML document.
     * @throws IOException if reading from the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws InvalidPreferencesFormatException Data on input stream does not
     *         constitute a valid XML document with the mandated document type.
     */
    public static void importPreferences(InputStream is) 
        throws IOException, InvalidPreferencesFormatException {
        XMLParser.importPreferences(is);
    }    
    
    public static Preferences userNodeForPackage(Class c) {
        NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
        MemoryPreferences prefs = (MemoryPreferences)(fac.userRoot());
        String name = myNodeName(c);
        MemoryPreferences newPref;
        if (prefs.getNodeImpl().containsKey(name)) {
            newPref = (MemoryPreferences)(prefs.getNodeImpl().get(name));
        } else {
            newPref = new MemoryPreferences(prefs, myNodeName(c));
        }
        
        return newPref;
    }

    public static Preferences systemNodeForPackage(Class c) {
        NetworkPreferencesFactory fac = NetworkPreferencesFactory.getFactory();
        MemoryPreferences prefs = (MemoryPreferences)(fac.systemRoot());
        String name = myNodeName(c);
        MemoryPreferences newPref;
        if (prefs.getNodeImpl().containsKey(name)) {
            newPref = (MemoryPreferences)(prefs.getNodeImpl().get(name));
        } else {
            newPref = new MemoryPreferences(prefs, myNodeName(c));
        }
        
        return newPref;
    }    
    
    /**
     * Returns the absolute path name of the node corresponding to the package
     * of the specified object.
     *
     * @throws IllegalArgumentException if the package has node preferences
     *         node associated with it. 
     */
    private static String myNodeName(Class c) {
        if (c.isArray())
            throw new IllegalArgumentException(
                "Arrays have no associated preferences node.");
        String className = c.getName();
        int pkgEndIndex = className.lastIndexOf('.');
        if (pkgEndIndex < 0)
            return "unnamed";
        String packageName = className.substring(0, pkgEndIndex);
        return packageName;
    }
        
    // SPECIFIC METHODS
    
    /** return the backstore Hashtable implementation for the nodes.
     * this is not used by this implementation, but can be used by subclasses.
     */
    protected Map getNodeImpl() {
        return nodes;
    }

    /** return the backstore Hashtable implementation for the (key, value) map.
     * this is not used by this implementation, but can be used by subclasses.
     */
    protected Map getMapImpl() {
        return map;
    }

    @Override
    public Preferences node(String name) {
        Preferences pref = nodes.get(name);
        if (pref != null) return nodes.get(name);
        else {
             return childSpi(name);
        }
    }
    
    private static File getPrefDirectory(File root, String appli) {
        File pref = null;
        pref = new File(root, "." + appli);
        return pref;        
    }                
}
