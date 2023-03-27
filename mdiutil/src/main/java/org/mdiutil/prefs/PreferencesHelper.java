/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2010, 2011, 2013, 2015, 2019, 2020, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.xml.UTF8Reader;

/**
 * This class helps to put and get values to Preferences for Files, and String arrays,
 * which are not direcly handled in the core java Preferences class.
 *
 * @version 1.2.10
 */
public class PreferencesHelper {
   private static final String ELEMENT_RADIX = "element";

   /**
    * Associates a string representing the specified File with the specified key
    * in this preference node.
    * The File will be stored with its relative or absolute pathname String, depending on if
    * it is under the reference directory (even under one of its sub-directories) or not.
    * It is guaranteed that the associated stored value will be a valid escaped UTF8 character
    * sequence.
    *
    * @param prefs the preferences node
    * @param key the key
    * @param file the File
    * @param dir the reference directory
    */
   public static void putFileRelativeTo(Preferences prefs, String key, File file, File dir) {
      if (file != null) {
         if ((dir != null) && (FileUtilities.isRelativeTo(dir, file))) {
            String path = FileUtilities.getRelativePath(dir, file);
            prefs.put(key, path);
         } else {
            prefs.put(key, fileToPath(file.getAbsolutePath()));
         }
      } else {
         prefs.put(key, "");
      }
   }

   /**
    * Associates a string representing the specified File with the specified key
    * in this preference node.
    * The File will be stored with its absolute pathname String.
    * <p>
    * It is guaranteed that the associated stored value will be a valid escaped UTF8 character
    * sequence.</p>
    *
    * @param prefs the preferences node
    * @param key the key
    * @param file the File
    */
   public static void putFile(Preferences prefs, String key, File file) {
      if (file != null) {
         prefs.put(key, fileToPath(file.getAbsolutePath()));
      } else {
         prefs.put(key, "");
      }
   }

   /**
    * Associates a string representing the specified File name with the specified key
    * in this preference node.
    *
    * @param prefs the preferences node
    * @param key the key
    * @param fileName the File name
    */
   public static void putFileName(Preferences prefs, String key, String fileName) {
      prefs.put(key, fileToPath(fileName));
   }

   /**
    * Returns the FileName represented by the string associated with the specified key
    * in this preference node. In this case, if the key does not exist, the FileName will be
    * replaced by the default pathname.
    *
    * @param prefs the preferences node
    * @param key the key
    * @param path the default path
    * @return the File name
    * @see #putFileName(Preferences, String, String)
    */
   public static String getFileName(Preferences prefs, String key, String path) {
      String fileName = pathToFile(prefs.get(key, prefs.get(key, path)));
      if (fileName == null) {
         return null;
      } else {
         return fileName.replace("%20", " ");
      }
   }

   /**
    * Returns the File represented by the string associated with the specified key
    * in this preference node. In this case, if the key does not exist, the File will be
    * replaced by the default File pathname.
    *
    * @param prefs the preferences node
    * @param key the key
    * @param defFile the default file to use if the value is not found in the Preferences
    * @param dir the parent directory to use for relative files
    * @return the File
    * @see #putFile(Preferences, String, File)
    */
   public static File getFileRelativeTo(Preferences prefs, String key, File defFile, File dir) {
      String s;
      if (defFile != null) {
         s = prefs.get(key, defFile.getAbsolutePath());
      } else {
         s = prefs.get(key, "");
      }
      if (s.equals("")) {
         return null;
      } else {
         String path = s.replace("%20", " ");
         File file = new File(path);
         if (file.isAbsolute() || (dir == null)) {
            return file;
         } else {
            return new File(dir, path);
         }
      }
   }

   /**
    * Returns the File represented by the string associated with the specified key
    * in this preference node. In this case, if the key does not exist, the File will be
    * replaced by the "" pathname.
    *
    * @param prefs the Preferences Node
    * @param key the key
    * @param defFile the default file
    * @return the File represented by the string associated with the specified key
    */
   public static File getFile(Preferences prefs, String key, File defFile) {
      return getFileRelativeTo(prefs, key, defFile, null);
   }

   /**
    * Returns the File represented by the string associated with the specified key
    * in this preference node. In this case, if the key does not exist, the File will be
    * replaced by the "" pathname.
    *
    * @param prefs the Preferences Node
    * @param key the key
    * @return the File represented by the string associated with the specified key
    * @see #getFile(Preferences, String, File)
    */
   public static File getFile(Preferences prefs, String key) {
      File file;
      String s = prefs.get(key, "");
      if (s.equals("")) {
         file = null;
      } else {
         file = new File(pathToFile(s).replace("%20", " "));
      }
      return file;
   }

   private static String fileToPath(String fileName) {
      UTF8Reader reader = UTF8Reader.getReader();
      return reader.stringToHex(fileName);
   }

   private static String pathToFile(String path) {
      UTF8Reader reader = UTF8Reader.getReader();
      return reader.hexToString(path);
   }

   /**
    * Creates a new preferences node that will store the associated Files under the current Node. The files will
    * be stored relative to a directory if possible.
    * <ul>
    * <li>The Files will be stored using the {@link #putFile(Preferences, String, File)} under this node</li>
    * <li>the template for the name of the keys for each file will be "element" &lt;file index&gt;, with index
    * going from 0 to the files count in the Files input</li>
    * </ul>
    *
    * @param prefs the preferences
    * @param node the Node
    * @param files the files array
    * @param dir the reference directory
    * @return the node under which the file keys are created
    */
   public static Preferences putFilesRelativeTo(Preferences prefs, String node, File[] files, File dir) {
      // FIXED: the list contained more than it should because we only addd to it
      // add the files nodes, removing the existing files under this node before
      Preferences filesNode = prefs.node(node);
      try {
         if (prefs.nodeExists(node)) {
            filesNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      filesNode = prefs.node(node);

      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            String key = ELEMENT_RADIX + i;
            putFileRelativeTo(filesNode, key, files[i], dir);
         }
      }

      return filesNode;
   }

   /**
    * Creates a new preferences node that will store the associated Files under the current Node.
    * <ul>
    * <li>The Files will be stored using the {@link #putFile(Preferences, String, File)} under this node</li>
    * <li>the template for the name of the keys for each file will be "element" &lt;file index&gt;, with index
    * going from 0 to the files count in the Files input</li>
    * </ul>
    *
    * @param prefs the preferences
    * @param node the Node
    * @param files the files array
    * @return the node under which the file keys are created
    */
   public static Preferences putFiles(Preferences prefs, String node, File[] files) {
      // FIXED SVGLab 101: the list contained more than it should because we only addd to it
      // add the files nodes, removing the existing files under this node before
      Preferences filesNode = prefs.node(node);
      try {
         if (prefs.nodeExists(node)) {
            filesNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      filesNode = prefs.node(node);

      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            String key = ELEMENT_RADIX + i;
            putFile(filesNode, key, files[i]);
         }
      }

      return filesNode;
   }

   /**
    * Returns the File[] represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the Files will be
    * replaced by the default files.
    * It is guaranteed that the ordering of the returned files will be identical to the order
    * in which they where initially put in the Preferences.
    *
    * @param prefs the preferences
    * @param node the Node
    * @param defFiles the default files array
    * @return the files
    * @see #putFiles(Preferences, String, File[])
    */
   public static File[] getFiles(Preferences prefs, String node, File[] defFiles) {
      return getFilesRelativeTo(prefs, node, defFiles, null);
   }

   /**
    * Returns the File[] represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the Files will be
    * replaced by the default files.
    * <p>
    * It is guaranteed that the ordering of the returned files will be identical to the order
    * in which they where initially put in the Preferences.</p>
    *
    * @param prefs the Preferences Node
    * @param key the key
    * @param defFiles the default files
    * @param dir the reference directory.
    * @return the files
    * @see #putFiles(Preferences, String, File[])
    */
   public static File[] getFilesRelativeTo(Preferences prefs, String key, File[] defFiles, File dir) {
      try {
         if (prefs.nodeExists(key)) {
            Preferences filesNode = prefs.node(key);
            String[] keys = filesNode.keys();

            // done to get the same ordering after retrieving the keys
            Index[] indexes = new Index[keys.length];
            for (int i = 0; i < keys.length; i++) {
               indexes[i] = new Index(keys[i]);
            }
            Arrays.sort(indexes);

            // now we can get the Files
            File[] outFiles = new File[keys.length];
            for (int i = 0; i < keys.length; i++) {
               outFiles[i] = getFileRelativeTo(filesNode, indexes[i].key, null, dir);
            }
            return outFiles;
         } else {
            return defFiles;
         }
      } catch (BackingStoreException e) {
         return defFiles;
      }
   }

   /**
    * Creates a new preferences node that will store the associated String array under the current Node.
    * <ul>
    * <li>The Strings will be stored as values under this node</li>
    * <li>the template for the name of the keys for each String will be "element" &lt;string index&gt;, with index
    * going from 0 to the strings count in the strings input</li>
    * </ul>
    *
    * @param prefs the preferences
    * @param node the Node
    * @param array the String array
    * @return the node under which the string keys are created
    */
   public static Preferences putStringArray(Preferences prefs, String node, String[] array) {
      // FIXED SVGLab 101: the list contained more than it should because we only add to it
      // add the files nodes, removing the existing files under this node before
      Preferences arrayNode = prefs.node(node);
      try {
         if (prefs.nodeExists(node)) {
            arrayNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      arrayNode = prefs.node(node);

      if (array != null) {
         for (int i = 0; i < array.length; i++) {
            String key = ELEMENT_RADIX + i;
            if (array[i] != null) {
               arrayNode.put(key, array[i]);
            }
         }
      }
      return arrayNode;
   }

   /**
    * Returns the String[] represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the Strings will be
    * replaced by the default string (an array with only one dimension).
    * It is guaranteed that the ordering of the returned Strings will be identical to the order
    * in which they where initially put in the Preferences.
    *
    * @param prefs the preferences
    * @param node the Node
    * @param def the default String used for every element in the array
    * @return the String array
    * @see #putStringArray(Preferences, String, String[])
    */
   public static String[] getStringArray(Preferences prefs, String node, String def) {
      String[] defArray = new String[1];
      defArray[0] = def;
      try {
         if (prefs.nodeExists(node)) {
            Preferences arrayNode = prefs.node(node);
            String[] keys = arrayNode.keys();

            // done to get the same ordering after retrieving the keys
            Index[] indexes = new Index[keys.length];
            for (int i = 0; i < keys.length; i++) {
               indexes[i] = new Index(keys[i]);
            }
            Arrays.sort(indexes);

            // now we can get the Files
            String[] out = new String[keys.length];
            for (int i = 0; i < keys.length; i++) {
               out[i] = arrayNode.get(indexes[i].key, def);
            }
            return out;
         } else {
            return defArray;
         }
      } catch (BackingStoreException e) {
         return defArray;
      }
   }

   /**
    * Creates a new preferences node that will store the associated boolean array under the current Node.
    * <ul>
    * <li>The booleans will be stored as values under this node</li>
    * <li>the template for the name of the keys for each boolean will be "element" &lt;string index&gt;, with index
    * going from 0 to the boolean count in the booleans input</li>
    * </ul>
    *
    * @param prefs the preferences
    * @param node the Node
    * @param array the boolean array
    * @return the node under which the string keys are created
    */
   public static Preferences putBooleanArray(Preferences prefs, String node, boolean[] array) {
      Preferences arrayNode = prefs.node(node);
      try {
         if (prefs.nodeExists(node)) {
            arrayNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      arrayNode = prefs.node(node);

      if (array != null) {
         for (int i = 0; i < array.length; i++) {
            String key = ELEMENT_RADIX + i;
            arrayNode.putBoolean(key, array[i]);
         }
      }
      return arrayNode;
   }

   /**
    * Returns the boolean[] represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the booleans will be
    * replaced by the default boolean (an array with only one dimension).
    * <p>
    * It is guaranteed that the ordering of the returned booleans will be identical to the order
    * in which they where initially put in the Preferences.</p>
    *
    * @param prefs the preferences
    * @param node the Node
    * @param def the default boolean used for every element in the array
    * @return the String array
    * @see #putBooleanArray(Preferences, String, boolean[])
    */
   public static boolean[] getBooleanArray(Preferences prefs, String node, boolean def) {
      boolean[] defArray = new boolean[1];
      defArray[0] = def;
      try {
         if (prefs.nodeExists(node)) {
            Preferences arrayNode = prefs.node(node);
            String[] keys = arrayNode.keys();

            // done to get the same ordering after retrieving the keys
            Index[] indexes = new Index[keys.length];
            for (int i = 0; i < keys.length; i++) {
               indexes[i] = new Index(keys[i]);
            }
            Arrays.sort(indexes);

            // now we can get the Files
            boolean[] out = new boolean[keys.length];
            for (int i = 0; i < keys.length; i++) {
               out[i] = arrayNode.getBoolean(indexes[i].key, def);
            }
            return out;
         } else {
            return defArray;
         }
      } catch (BackingStoreException e) {
         return defArray;
      }
   }

   /**
    * Creates a new preferences node that will store the associated Map under the current Node.
    * <ul>
    * <li>The Map elements will be stored as pairs of (key, String values) under this node</li>
    * </ul>
    *
    * @param <C> the class type
    * @param prefs the Preferences
    * @param key the key
    * @param map the Map
    * @return the node under which the Map as been created
    */
   public static <C> Preferences putMapForClass(Preferences prefs, String key, Map<String, C> map) {
      Preferences mapNode = prefs.node(key);
      try {
         if (prefs.nodeExists(key)) {
            mapNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      mapNode = prefs.node(key);

      if (map != null) {
         Iterator<String> it = map.keySet().iterator();
         while (it.hasNext()) {
            String _key = it.next();
            Object value = map.get(_key);
            mapNode.put(_key, value.toString());
         }
      }
      return mapNode;
   }

   /**
    * Creates a new preferences node that will store the associated Map under the current Node.
    * <ul>
    * <li>The Map elements will be stored as pairs of (key, String values) under this node</li>
    * </ul>
    *
    * @param prefs the Preferences
    * @param key the key
    * @param map the Map
    * @return the node under which the Map as been created
    */
   public static Preferences putMap(Preferences prefs, String key, Map<String, Object> map) {
      Preferences mapNode = prefs.node(key);
      try {
         if (prefs.nodeExists(key)) {
            mapNode.removeNode();
         }
      } catch (BackingStoreException e) {
      }
      mapNode = prefs.node(key);

      if (map != null) {
         Iterator<String> it = map.keySet().iterator();
         while (it.hasNext()) {
            String _key = it.next();
            Object value = map.get(_key);
            mapNode.put(_key, value.toString());
         }
      }
      return mapNode;
   }

   /**
    * Returns the List of Numbers represented by the string associated with the specified key in this preference node.
    *
    * @param prefs the Preferences
    * @param key the key
    * @param clazz the Number class to use
    * @return the List of Numbers
    */
   public static List<Number> getNumbers(Preferences prefs, String key, Class clazz) {
      List<Number> numbers = new ArrayList<>();
      try {
         String s = prefs.get(key, "");
         StringTokenizer tok = new StringTokenizer(s, ";");
         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken();
            Number field;
            if (clazz == Integer.class) {
               field = Integer.parseInt(tk);
            } else if (clazz == Short.class) {
               field = Short.parseShort(tk);
            } else if (clazz == Float.class) {
               field = Float.parseFloat(tk);
            } else if (clazz == Double.class) {
               field = Double.parseDouble(tk);
            } else {
               field = Integer.parseInt(tk);
            }
            numbers.add(field);
         }
      } catch (NumberFormatException e) {
      }

      return numbers;
   }

   /**
    * Creates a new preferences node that will store a list of numbers under the current Node.
    * The list of Numbers will be stored as a String with this pattern:
    * <pre>
    *     &lt;number&gt;;&lt;number&gt;;&lt;number&gt;;&lt;number&gt;
    * </pre>
    *
    * @param prefs the Preferences
    * @param key the key
    * @param numbers the numbers
    */
   public static void putNumbers(Preferences prefs, String key, List<Number> numbers) {
      StringBuilder buf = new StringBuilder();
      Iterator<Number> it = numbers.iterator();
      while (it.hasNext()) {
         Number number = it.next();
         buf.append(number);
         if (it.hasNext()) {
            buf.append(";");
         }
      }
      prefs.put(key, buf.toString());
   }

   /**
    * Creates a new preferences node that will store a list of numbers under the current Node.
    *
    * The list of Numbers will be stored as a String with this pattern:
    * <pre>
    *     &lt;number&gt;;&lt;number&gt;;&lt;number&gt;;&lt;number&gt;
    * </pre>
    *
    * @param prefs the Preferences
    * @param key the key
    * @param numbers the numbers
    */
   public static void putNumbers(Preferences prefs, String key, Number... numbers) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < numbers.length; i++) {
         buf.append(numbers[i]);
         if (i < numbers.length - 1) {
            buf.append(";");
         }
      }
      prefs.put(key, buf.toString());
   }

   /**
    * Creates a new preferences node that will store a serialized Object the current Node. Note that the object must
    * be serializable.
    *
    * @param prefs the Preferences
    * @param key the key
    * @param o the Object
    * @return the preference node.
    */
   public static Preferences putObject(Preferences prefs, String key, Object o) {
      if (o != null) {
         try {
            ByteArrayOutputStream array = new ByteArrayOutputStream(1024);
            try (ObjectOutputStream stream = new ObjectOutputStream(array)) {
               stream.writeObject(o);
               stream.flush();
               prefs.putByteArray(key, array.toByteArray());
            }
         } catch (IOException e) {
         }
      }
      return prefs;
   }

   /**
    * Returns the Object value represented by the string associated with the specified key
    * in this preference node. Same as
    * {@link #getObject(Preferences, String, Object) } will a null Object.
    *
    * @param prefs the preferences
    * @param key the key
    * @return the associated Object
    */
   public static Object getObject(Preferences prefs, String key) {
      return getObject(prefs, key, null);
   }

   /**
    * Returns the Object value represented by the string associated with the specified key
    * in this preference node.
    *
    * @param prefs the preferences
    * @param key the key
    * @param def the default Object
    * @return the associated Object
    */
   public static Object getObject(Preferences prefs, String key, Object def) {
      Object o = null;
      try {
         byte[] array = null;
         if (def != null) {
            ByteArrayOutputStream barray = new ByteArrayOutputStream(1024);
            ObjectOutputStream stream = new ObjectOutputStream(barray);
            stream.writeObject(def);
            stream.flush();
            array = barray.toByteArray();
         }

         array = prefs.getByteArray(key, array);
         ByteArrayInputStream btarray = new ByteArrayInputStream(array);
         try (ObjectInputStream stream = new ObjectInputStream(btarray)) {
            o = stream.readObject();
         }
         if ((def != null) && ((o == null) || (!(o.getClass() == def.getClass())))) {
            throw new Exception("The Object for the " + key + " key is not of the " + def.getClass() + "class");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return o;
   }

   /**
    * Returns the Map represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the Map will be replaced by an empty Map.
    *
    * @param <C> the class type
    * @param prefs the preferences
    * @param node the Node
    * @param clazz the Object class to use for the Map
    * @param def the default Object
    * @return the Map
    * @see #putBooleanArray(Preferences, String, boolean[])
    */
   public static <C> Map<String, C> getMapForClass(Preferences prefs, String node, Class<C> clazz, Object def) {
      Map<String, C> map = new TreeMap<>();
      try {
         if (prefs.nodeExists(node)) {
            Preferences mapNode = prefs.node(node);
            String[] keys = mapNode.keys();

            // now we can get the map
            for (int i = 0; i < keys.length; i++) {
               String value;
               String key = keys[i];
               if (def == null) {
                  value = mapNode.get(key, null);
               } else {
                  value = mapNode.get(key, def.toString());
               }
               try {
                  if (clazz == Boolean.class) {
                     map.put(key, clazz.cast(Boolean.parseBoolean(value)));
                  } else if (clazz == Integer.class) {
                     map.put(key, clazz.cast(Integer.valueOf(value)));
                  } else if (clazz == Long.class) {
                     map.put(key, clazz.cast(Long.valueOf(value)));
                  } else if (clazz == Short.class) {
                     map.put(key, clazz.cast(Short.valueOf(value)));
                  } else if (clazz == Float.class) {
                     map.put(key, clazz.cast(Float.valueOf(value)));
                  } else if (clazz == Double.class) {
                     map.put(key, clazz.cast(Double.valueOf(value)));
                  } else if (clazz == String.class) {
                     map.put(key, clazz.cast(value));
                  }
               } catch (NumberFormatException e) {
               }
            }
            return map;
         } else {
            return map;
         }
      } catch (BackingStoreException e) {
         return map;
      }
   }

   /**
    * Returns the Map represented by the string associated with the specified node
    * in this preference node. If this subnode does not exist, the Map will be
    * replaced by an empty Map.
    *
    * @param prefs the preferences
    * @param node the Node
    * @param clazz the Object class to use for the Map
    * @param def the default Object
    * @return the Map
    * @see #putBooleanArray(Preferences, String, boolean[])
    */
   public static Map<String, Object> getMap(Preferences prefs, String node, Class clazz, Object def) {
      Map<String, Object> map = new TreeMap<>();
      try {
         if (prefs.nodeExists(node)) {
            Preferences mapNode = prefs.node(node);
            String[] keys = mapNode.keys();

            // now we can get the map
            for (int i = 0; i < keys.length; i++) {
               String value;
               String key = keys[i];
               if (def == null) {
                  value = mapNode.get(key, null);
               } else {
                  value = mapNode.get(key, def.toString());
               }
               try {
                  if (clazz == Boolean.class) {
                     map.put(key, Boolean.parseBoolean(value));
                  } else if (clazz == Integer.class) {
                     map.put(key, Integer.valueOf(value));
                  } else if (clazz == Long.class) {
                     map.put(key, Long.valueOf(value));
                  } else if (clazz == Short.class) {
                     map.put(key, Short.valueOf(value));
                  } else if (clazz == Float.class) {
                     map.put(key, Float.valueOf(value));
                  } else if (clazz == Double.class) {
                     map.put(key, Double.valueOf(value));
                  } else if (clazz == String.class) {
                     map.put(key, value);
                  }
               } catch (NumberFormatException e) {
               }
            }
            return map;
         } else {
            return map;
         }
      } catch (BackingStoreException e) {
         return map;
      }
   }

   /**
    * This class ensures that the order of returned files will be the same as their initial order when
    * put in the Preference.
    */
   static class Index implements Comparable {
      /* the index is the number that appear after the FILE_RADIX of the key. The order of these number
       * is identical to the order of the files that are put in the Preference, thanks to the "put" process
       * in the helper main class.
       */
      private final int ind;
      String key;

      Index(String key) {
         this.ind = Integer.parseInt(key.substring(ELEMENT_RADIX.length()));
         this.key = key;
      }

      /**
       * the ordering of Index classes is the ordering of their internal indexes.
       */
      @Override
      public int compareTo(Object o) {
         Index index = (Index) o;
         if (ind == index.ind) {
            return 0;
         } else if (ind > index.ind) {
            return 1;
         } else {
            return -1;
         }
      }
   }
}
