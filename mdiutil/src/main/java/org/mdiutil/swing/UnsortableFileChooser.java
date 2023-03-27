/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 *
 * A JFileChooser which is able to return Files in the order that they were added by the user.
 *
 * @version 1.2.32
 */
public class UnsortableFileChooser extends JFileChooser {
   private final Set<File> previousSelection = new HashSet();
   private List<File> selectedFiles = new ArrayList<>();

   public UnsortableFileChooser() {
      super();
   }

   /**
    * Constructor.
    *
    * @param currentDirectory the current directory
    */
   public UnsortableFileChooser(File currentDirectory) {
      super(currentDirectory);
   }

   /**
    * Constructor.
    *
    * @param currentDirectoryPath the current directory path
    */
   public UnsortableFileChooser(String currentDirectoryPath) {
      super(currentDirectoryPath);
   }

   @Override
   protected JDialog createDialog(Component parent) throws HeadlessException {
      selectedFiles.clear();
      previousSelection.clear();
      return super.createDialog(parent);
   }

   private void checkSelectedFiles(File[] newSelection) {
      Set<File> newFiles = new HashSet();
      // first add at the end the files which were not previously present in the selection list
      for (int i = 0; i < newSelection.length; i++) {
         if (!previousSelection.contains(newSelection[i])) {
            selectedFiles.add(newSelection[i]);
            previousSelection.add(newSelection[i]);
         }
         if (!newFiles.contains(newSelection[i])) {
            newFiles.add(newSelection[i]);
         }
      }
      // now remove the files which are not present anymore in the selection list
      List<File> selectedFiles2 = new ArrayList(selectedFiles);
      Iterator<File> it = selectedFiles.iterator();
      while (it.hasNext()) {
         File file = it.next();
         if (!newFiles.contains(file)) {
            selectedFiles2.remove(file);
         }
      }
      selectedFiles = selectedFiles2;
   }

   /**
    * Return the selected files in the order that they were selected by the user.
    *
    * @return the selected files in the order that they were selected by the user
    */
   public File[] getUnsortedSelectedFiles() {
      return selectedFiles.toArray(new File[selectedFiles.size()]);
   }

   /**
    * Return the selected files with the default sorting of the JDK (alphabetical order).
    *
    * @return the selected files with the default sorting of the JDK (alphabetical order)
    */
   @Override
   public File[] getSelectedFiles() {
      File[] files = super.getSelectedFiles();
      checkSelectedFiles(files);
      return files;
   }

   public static void main(String[] args) {
      UnsortableFileChooser fileChooser = new UnsortableFileChooser();
      fileChooser.setDialogTitle("select file");
      fileChooser.setMultiSelectionEnabled(true);
      if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
         File[] files = fileChooser.getUnsortedSelectedFiles();
         String[] fileNames = new String[files.length];
         for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getPath();
            System.out.println(fileNames[i]);
         }
      }
   }
}
