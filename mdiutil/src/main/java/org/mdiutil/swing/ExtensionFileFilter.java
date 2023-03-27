/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2009, 2010, 2011, 2012, 2015, 2016, 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import org.mdiutil.io.ExtensionFilenameFilter;

/**
 * This class is FileFilter which uses the File extension to filter the file. It is compatible with all
 * the JDK classes filtering Files.
 *
 * Note that the extension is not case sensitive.
 *
 * @version 0.9.59
 */
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter
   implements java.io.FilenameFilter, java.io.FileFilter {
   private SortedSet<String> extensions = new TreeSet<>();
   private String description = "";

   /**
    * Constructor.
    *
    * @param desc the description of the FileFilter
    */
   public ExtensionFileFilter(String desc) {
      super();
      description = desc;
   }

   /**
    * Constructor.
    *
    * <h1>Usage</h1>
    * <pre>
    *     String[] ext = {"jpg","jpeg","tiff","png","svg"};
    *     graphicfilter = new ExtensionFileFilter(ext,"JPEG,PNG,TIFF,SVG images");
    * </pre>
    *
    * @param ext the extensions
    * @param desc the description of the FileFilter
    */
   public ExtensionFileFilter(String[] ext, String desc) {
      super();
      int i;
      for (i = 0; i < ext.length; i++) {
         extensions.add(ext[i].toLowerCase());
      }
      description = desc;
   }

   /**
    * Constructor. Beware that every change in the extensions will
    * also change the input Set of extensions for its FileFilter.
    *
    * <h1>Usage</h1>
    * <pre>
    *     String[] ext = {"jpg","jpeg","tiff","png","svg"};
    *     graphicfilter = new ExtensionFileFilter(ext,"JPEG,PNG,TIFF,SVG images");
    * </pre>
    *
    * @param filter the ExtensionFilenameFilter
    */
   public ExtensionFileFilter(ExtensionFilenameFilter filter) {
      super();
      this.extensions = filter.getExtensions();
      this.description = filter.getDescription();
   }

   /**
    * Add an extension to the <var>ExtensionFileFilter</var>.
    * Note that the extension is not case sensitive.
    *
    * @param ext the extension
    */
   public void add(String ext) {
      extensions.add(ext.toLowerCase());
   }

   /**
    * Returns true if this FileFilter accept one unique extension.
    *
    * @return true if this FileFilter accept one unique extension
    */
   public boolean hasUniqueExtension() {
      if (extensions.size() == 1) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * If this FileFilter accept one unique extension, returns this extension, else return null.
    *
    * @return the unique extension or null if there are more than one extension managed by this FileFilter
    */
   public String getUniqueExtension() {
      if (extensions.size() != 1) {
         return null;
      } else {
         return extensions.first();
      }
   }

   /**
    * Create a File compatible with this FileFilter extension, if the input file has no extension.
    * <ul>
    * <li>If the input file has already one extension, return this file</li>
    * <li>Else :</li>
    * <ul>
    * <li>If this FileFilter accepts one unique extension, add the extension to the input file name</li>
    * <li>Else return the input file</li>
    * </ul>
    * </ul>
    *
    * @param in the File
    * @return the compatible File
    */
   public File getCompatibleFile(File in) {
      if ((ExtensionFileFilter.getSuffix(in).equals("")) && (hasUniqueExtension())) {
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
    * @return the File name prefix (without the extension)
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
    * @param f the File
    * @return the File name suffix (the extension)
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
    * Return whether the given file is accepted by this filter.
    * The file is accepted if its suffix corresponds with the accepted extensions of the FileFilter.
    *
    * @param f the File
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
    * <p>
    * The file of name <var>name</var> is accepted if its suffix corresponds
    * with the accepted extensions of the FileFilter</p>
    */
   @Override
   public boolean accept(File dir, String name) {
      return accept(new File(name));
   }

   /**
    * Return the description of this FileFilter. For example: "JPG and GIF Images"</p>
    *
    * @return the description of this FileFilter
    */
   @Override
   public String getDescription() {
      return description;
   }
}
