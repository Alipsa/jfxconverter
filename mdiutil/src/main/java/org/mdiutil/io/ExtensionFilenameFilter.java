/*------------------------------------------------------------------------------
 * Copyright (C) 2011, 2016, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A FileFilter utility class simplifying the creation of File filters.
 *
 * @version 1.0
 */
public class ExtensionFilenameFilter implements FilenameFilter, FileFilter {
   private SortedSet<String> extensions = new TreeSet<>();
   private String description = "";

   /**
    * Constructor. This filter is not case sensitive.
    *
    * @param desc the filter description
    */
   public ExtensionFilenameFilter(String desc) {
      super();
      description = desc;
   }

   /**
    * Constructor.
    *
    * <h1>Example of use:</h1>
    * <pre>
    *     String[] ext = {"jpg","jpeg","tiff","png","svg"};
    *     graphicfilter = new ExtensionFileFilter(ext,"JPEG,PNG,TIFF,SVG images");
    * </pre>
    *
    * @param ext the array of extensions
    * @param desc the filter description
    */
   public ExtensionFilenameFilter(String[] ext, String desc) {
      super();
      int i;
      for (i = 0; i < ext.length; i++) {
         extensions.add(ext[i].toLowerCase());
      }
      description = desc;
   }

   /**
    * Constructor. Beware that if the set given as input is a <code>SortedSet</code>,
    * it will back this fileFilter. In this case, every change in the extensions will
    * also change the input Set.
    * <p>
    * Example of use:</p>
    * <pre>
    *     Set&lt;String&gt; ext = {"jpg","jpeg","tiff","png","svg"};
    *     graphicfilter = new ExtensionFileFilter(ext,"JPEG,PNG,TIFF,SVG images");
    * </pre>
    *
    * @param exts the Set of extensions
    * @param desc the filter description
    */
   public ExtensionFilenameFilter(Set<String> exts, String desc) {
      super();
      if (exts instanceof SortedSet) {
         this.extensions = (SortedSet) exts;
      } else {
         Iterator<String> it = exts.iterator();
         while (it.hasNext()) {
            extensions.add(it.next().toLowerCase());
         }
      }
      description = desc;
   }

   /**
    * Add an extension to the previously created <var>ExtensionFileFilter</var>.
    *
    * @param ext the extension
    */
   public void add(String ext) {
      extensions.add(ext.toLowerCase());
   }

   public SortedSet<String> getExtensions() {
      return extensions;
   }

   /**
    * Returns True if this FileFilter accept one unique extension.
    *
    * @return true if this FileFilter accept one unique extensio
    */
   public boolean hasUniqueExtension() {
      if (extensions.size() == 1) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * If this FileFilter accept one unique extension, returns this extension.
    *
    * @return the unique extension or the first one
    */
   public String getUniqueExtension() {
      if (extensions.size() != 1) {
         return null;
      } else {
         return extensions.first();
      }
   }

   /**
    * Creates a File compatible with this FileFilter extension, if the input file has no extension.
    * <ul>
    * <li>If the input file has already one extension, return this file</li>
    * <li>Else :</li>
    * <ul>
    * <li>If this FileFilter accepts one unique extension, add the extension to the input file name</li>
    * <li>Else return the input file</li>
    * </ul>
    * </ul>
    *
    * @param in the input file
    * @return a file compatible with the extension filter
    */
   public File getCompatibleFile(File in) {
      if ((ExtensionFilenameFilter.getSuffix(in).equals("")) && (hasUniqueExtension())) {
         String s = getUniqueExtension();
         return new File(in.getAbsolutePath() + "." + s);
      } else {
         return in;
      }
   }

   /**
    * Utility method to retrieve the name prefix of a file.
    * <ul>
    * <li>If the file is named "&lt;name&gt;.&lt;suffix&gt;", the String &lt;name&gt; is returned</li>
    * <li>Else the file name is returned</li>
    * </ul>
    *
    * @param f the File
    * @return the file name prefix
    */
   public static String getNamePrefix(File f) {
      String name = f.getName();
      int idx = name.lastIndexOf(".");
      if (idx == -1) {
         return name;
      } else {
         return name.substring(0, idx);
      }
   }

   /**
    * Utility method to retrieve the suffix of a file.
    * <ul>
    * <li>If the file is named "*.&lt;suffix&gt;", the String &lt;suffix&gt; is returned</li>
    * <li>Else an empty String ("") si returned</li>
    * </ul>
    *
    * @param f the file
    * @return the file suffix
    */
   public static String getSuffix(File f) {
      String name = f.getName();
      int idx = name.lastIndexOf(".");
      if (idx == -1) {
         return "";
      } else {
         return name.substring(idx + 1, name.length());
      }
   }

   /**
    * Return true if the given file is accepted by this filter.
    *
    * The file is accepted if its suffix corresponds with the accepted extensions of the FileFilter.
    *
    * @param f the file
    * @return true if the given file is accepted by this filter
    */
   @Override
   public boolean accept(File f) {
      boolean r;
      String name = f.getName();
      String ext = name.substring(name.lastIndexOf(".") + 1, name.length());
      if (f.isDirectory()) {
         r = true;
      } else if (extensions.contains(ext.toLowerCase())) {
         r = true;
      } else {
         r = false;
      }
      return r;
   }

   /**
    * Tests if a specified file should be included in a file list.
    *
    * The file of name <code>name</code> is accepted if its suffix corresponds
    * with the accepted extensions of the FileFilter.
    *
    * @param name the file name
    * @return true if the given file is accepted by this filter
    */
   @Override
   public boolean accept(File dir, String name) {
      return accept(new File(name));
   }

   /**
    * Return the description of this FileFilter.
    *
    * For example: "JPG and GIF Images"
    *
    * @return the description of this FileFilter
    */
   public String getDescription() {
      return description;
   }
}
