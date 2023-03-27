/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2009-2018, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;

/**
 * File chooser containing a <var>JTextField</var> and a <var>JButton</var>.
 * <ul>
 * <li>the <var>JTextField</var> allows to write the file path</li>
 * <li>the <var>JButton</var> give access to a File Chooser</li>
 * </ul>
 *
 * The two components are connected, so when one of them is modified, the other is modified accordingly.
 *
 * @version 1.2.32
 */
public class JFileSelector extends JPanel {
   protected static volatile UnsortableFileChooser chooser = null;
   private File selectedFile;
   private File[] files;
   /**
    * The Dialog title.
    */
   protected String title = "Open File";
   private boolean fileSelectionEnabled = true;
   private boolean dirSelectionEnabled = true;
   /**
    * The file renderer.
    */
   protected FileSelectorRenderer fileRenderer = new DefaultFileSelectorRenderer();
   private final List<ActionListener> mylisteners = new ArrayList<>();
   private AbstractAction openAction;
   private int BUTTONWIDTH = 30;
   private int BUTTONHEIGHT = 25;
   private File currentDir = null;
   private File[] currentFiles = null;
   private boolean begin = true;
   private int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
   private int dialogType = JFileChooser.OPEN_DIALOG;
   private boolean multiSelectionEnabled = false;
   private boolean dragEnabled = false;
   private boolean editable = true;
   private boolean enabled = true;
   private List<FileFilter> choosableFileFilters = null;
   private static List<FileFilter> oldFileFilters = new ArrayList<>(5);
   private FileFilter filter = null;
   private TransferHandler handler = null;
   private final JTextField textopen = new JTextField();
   private boolean textMod = false;
   private JButton open;
   private static ExecutorService executor = null;
   private static FutureTask<UnsortableFileChooser> future = null;
   private boolean isDefaultSorted = true;

   /**
    * Create a new FileSelector, without defining a FileChooser name.
    */
   public JFileSelector() {
      super();
      createUI();
   }

   /**
    * Create a new FileSelector.
    *
    * @param title the FileChooser title
    */
   public JFileSelector(String title) {
      super();
      this.title = title;
      createUI();
   }

   /**
    * Create the fileChooser shared by all the instances of the fileSelector.
    *
    */
   private static void createFileChooser() {
      /*
       * This try to fix the problem of the very slow initialization of the JFileChooser the first time in cases where we have
       * disconnected network drives. Our solution is to use a future, then we can still have a delay but only when we really use the
       * underlying File chooser, if the FutureTask is still not done
       * https://stackoverflow.com/questions/12293092/how-can-i-make-jfilechooser-behave-properly-with-disconnected-network-drives
       */
      if (chooser == null) {
         future = new ProtectedFuture<>(UnsortableFileChooser::new);
         executor = Executors.newSingleThreadExecutor();
         executor.execute(future);
      }
   }

   /**
    * Return the fileChooser shared by all the instances of the fileSelector.
    *
    * @return the fileChooser shared by all the instances of the fileSelector
    */
   public static JFileChooser getFileChooser() {
      if (chooser == null) {
         if (future == null) {
            createFileChooser();
         }
         try {
            chooser = future.get();
            future = null;
         } catch (InterruptedException | ExecutionException ex) {
            chooser = new UnsortableFileChooser();
         }
      }
      return chooser;
   }

   /**
    * Set the dialog title.
    *
    * @param title the title
    */
   public void setDialogTitle(String title) {
      this.title = title;
   }

   /**
    * Return the dialog title.
    *
    * @return the title
    */
   public String getDialogTitle() {
      return title;
   }

   /**
    * Set the class responsible to render to text associated to the selected files. Note that is will set the renderer
    * to a {@link DefaultFileSelectorRenderer} if the method argument is null.
    *
    * @param fileRenderer the file Renderer
    */
   public void setFileSelectorRenderer(FileSelectorRenderer fileRenderer) {
      this.fileRenderer = fileRenderer;
      if (fileRenderer == null) {
         this.fileRenderer = new DefaultFileSelectorRenderer();
      }
   }

   /**
    * Return the class responsible to render to text associated to the selected files.
    *
    * @return the file Renderer
    */
   public FileSelectorRenderer getFileSelectorRenderer() {
      return fileRenderer;
   }

   /**
    * Set if the File Chooser use the default sorting of the JDK (alphabetical order), or if the sorting is the order of
    * file selection in the chooser.
    *
    * @param isDefaultSorted true if the File Chooser use the default sorting of the JDK (alphabetical order)
    */
   public void setDefaultSorted(boolean isDefaultSorted) {
      this.isDefaultSorted = isDefaultSorted;
   }

   /**
    * Return true if the File Chooser use the default sorting of the JDK (alphabetical order).
    *
    * @return true if the File Chooser use the default sorting of the JDK (alphabetical order)
    */
   public boolean isDefaultSorted() {
      return isDefaultSorted;
   }

   private void forceFileSelection() {
      getFileChooser();
      if (chooser != null) {
         if (chooser.isMultiSelectionEnabled()) {
            chooser.setSelectedFiles(currentFiles);
         } else {
            chooser.setSelectedFile(selectedFile);
         }
      }
   }

   private void createUI() {
      createFileChooser();
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      openAction = new AbstractAction("...") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            open();
         }
      };

      textopen.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!textMod) {
               String text = textopen.getText();
               if ((text == null) || (text.trim().length() == 0)) {
                  setSelectedFile(null);
                  forceFileSelection();
                  processEvent();
               } else {
                  File filemod = new File(text);
                  if ((filemod.exists()
                     && (filemod.isFile() && fileSelectionEnabled) || (filemod.isDirectory() && dirSelectionEnabled))
                     || (!filemod.exists())) {
                     selectedFile = new File(filemod.getAbsolutePath());
                     setSelectedFile(selectedFile);
                     forceFileSelection();
                     processEvent();
                  }
               }
            } else {
               setTextAfterFileSelection(selectedFile.getAbsolutePath());
            }
         }
      });
      open = new JButton(openAction);
      this.add(textopen);
      this.add(open);
      this.add(Box.createHorizontalGlue());
      int preftextopenwidth = 200;
      BUTTONWIDTH = (int) (open.getPreferredSize().getWidth());
      BUTTONHEIGHT = (int) (open.getPreferredSize().getHeight());
      textopen.setPreferredSize(new Dimension(preftextopenwidth, BUTTONHEIGHT));
      open.setPreferredSize(new Dimension(BUTTONWIDTH, BUTTONHEIGHT));
      textopen.setMaximumSize(new Dimension(preftextopenwidth, BUTTONHEIGHT));
      open.setMaximumSize(new Dimension(BUTTONWIDTH, BUTTONHEIGHT));
   }

   private JFileChooser setupChooser() {
      getFileChooser();
      chooser.setDialogTitle(title);
      setFileChooserOptions(chooser);
      return chooser;
   }

   /**
    * Return the associated FileChooser file selection mode.
    *
    * @return the associated FileChooser file selection mode
    * @see JFileChooser#getFileSelectionMode()
    */
   public int getFileSelectionMode() {
      return fileSelectionMode;
   }

   /**
    * Set if the drag is enabled.
    *
    * @param b true if the drag is enabled
    */
   public void setDragEnabled(boolean b) {
      this.dragEnabled = b;
   }

   /**
    * Set the FileFilter to use for the selection.
    *
    * @param filter the selected FileFilter
    */
   public void setFileFilter(FileFilter filter) {
      this.filter = filter;
   }

   /**
    * Return the currently selected FileFilter.
    *
    * @return the currently selected FileFilter
    */
   public FileFilter getFileFilter() {
      return getFileChooser().getFileFilter();
   }

   /**
    * Return the specified FileFilter.
    *
    * @return the specified FileFilter
    */
   public FileFilter getSpecifiedFileFilter() {
      return filter;
   }

   /**
    * Set the associated TransferHandler.
    *
    * @param handler the associated TransferHandler
    */
   public void setTransfertHandler(TransferHandler handler) {
      this.handler = handler;
   }

   /**
    * Return true if drag is enabled.
    *
    * @return true if drag is enabled
    */
   public boolean getDragEnabled() {
      return dragEnabled;
   }

   /**
    * Set the type of this dialog. Use OPEN_DIALOG when you want to bring up a file chooser that the user can use to
    * open a file. Use SAVE_DIALOG for letting the user choose a file for saving.
    *
    * @param dialogType the type of this dialog
    */
   public void setDialogType(int dialogType) {
      this.dialogType = dialogType;
   }

   /**
    * Return the type of this dialog.
    *
    * @return the type of this dialog
    */
   public int getDialogType() {
      return dialogType;
   }

   /**
    * Set the associated FileChooser file selection mode.
    *
    * @param mode the associated FileChooser file selection mode
    * @see JFileChooser#setFileSelectionMode(int)
    */
   public void setFileSelectionMode(int mode) {
      this.fileSelectionMode = mode;
   }

   /**
    * Enable or disable the associated FileChooser multiSelection.
    *
    * @param b true if the associated FileChooser multiSelection is enabled
    * @see JFileChooser#setMultiSelectionEnabled(boolean)
    */
   public void setMultiSelectionEnabled(boolean b) {
      this.multiSelectionEnabled = b;
   }

   private void setFileChooserOptions(JFileChooser chooser) {
      FileFilter[] _filters = chooser.getChoosableFileFilters();
      if (_filters != null) {
         for (int i = 0; i < _filters.length; i++) {
            if (oldFileFilters.contains(_filters[i])) {
               chooser.removeChoosableFileFilter(_filters[i]);
            }
         }
      }
      oldFileFilters.clear();

      chooser.setFileSelectionMode(fileSelectionMode);
      chooser.setDialogType(dialogType);
      fileSelectionEnabled = chooser.isFileSelectionEnabled();
      dirSelectionEnabled = chooser.isDirectorySelectionEnabled();
      chooser.setMultiSelectionEnabled(multiSelectionEnabled);
      chooser.setCurrentDirectory(currentDir);
      if (currentFiles != null) {
         if (currentFiles.length == 1) {
            chooser.setSelectedFile(currentFiles[0]);
         } else {
            chooser.setSelectedFiles(currentFiles);
         }
      }
      chooser.setDragEnabled(dragEnabled);
      chooser.resetChoosableFileFilters();
      if (filter != null) {
         chooser.setFileFilter(filter);
      }
      if (handler != null) {
         chooser.setTransferHandler(handler);
      }
      if (choosableFileFilters != null) {
         for (int i = 0; i < choosableFileFilters.size(); i++) {
            chooser.addChoosableFileFilter(choosableFileFilters.get(i));
            oldFileFilters.add(choosableFileFilters.get(i));
         }
      }
      open.setEnabled(enabled);
      textopen.setEnabled(enabled);
      this.applySetSelectedFile();
   }

   /**
    * Return the selected File.
    *
    * @return the selected File
    */
   public File getSelectedFile() {
      return selectedFile;
   }

   /**
    * Return the selected files.
    *
    * @return the selected files
    */
   public File[] getSelectedFiles() {
      return files;
   }

   /**
    * Set the Filefilters for this FileSelector. This is an alternative for the
    * {@link #setChoosableFileFilters(List)} method.
    *
    * The format of the argument is:
    * <pre>
    * extension1,extension2,...,extension-n;
    * </pre>
    *
    * @param filters the Filefilters
    */
   public void setChoosableFileFilters(String filters) {
      if (choosableFileFilters == null) {
         choosableFileFilters = new ArrayList<>(5);
      }
      StringTokenizer tok = new StringTokenizer(filters, ",");
      while (tok.hasMoreTokens()) {
         String extension = tok.nextToken().trim();
         ExtFileFilter _filter = new ExtFileFilter(extension);
         addChoosableFileFilter(_filter);
      }
   }

   /**
    * Set the Filefilters for this FileSelector.
    *
    * @param filters the Filefilters
    */
   public void setChoosableFileFilters(List<FileFilter> filters) {
      choosableFileFilters = filters;
   }

   /**
    * Add a Filefilter to this FileSelector.
    *
    * @param filter the Filefilter
    */
   public void addChoosableFileFilter(FileFilter filter) {
      if (choosableFileFilters == null) {
         choosableFileFilters = new ArrayList<>(5);
      }
      choosableFileFilters.add(filter);
   }

   /**
    * Remove a Filefilter to this FileSelector.
    *
    * @param filter the Filefilter
    */
   public void removeChoosableFileFilter(FileFilter filter) {
      if (choosableFileFilters != null) {
         choosableFileFilters.remove(filter);
      }
   }

   /**
    * Add an ActionListener.
    *
    * @param listener the ActionListener
    */
   public void addActionListener(ActionListener listener) {
      mylisteners.add(listener);
   }

   /**
    * Removes an ActionListener.
    *
    * @param listener the ActionListener
    */
   public void removeActionListener(ActionListener listener) {
      mylisteners.remove(listener);
   }

   /**
    * Return the associated selected File name.
    *
    * @return the associated selected File name
    */
   public String getFileName() {
      if (selectedFile == null) {
         return "";
      } else {
         return selectedFile.getName();
      }
   }

   /**
    * Return the associated selected File path.
    *
    * @return the associated selected File path
    */
   public String getFilePath() {
      if (selectedFile == null) {
         return "";
      } else {
         return selectedFile.getAbsolutePath();
      }
   }

   /**
    * Return the associated selected File name.
    *
    * @return the associated selected File name
    */
   @Override
   public String getName() {
      return getFilePath();
   }

   @Override
   public String toString() {
      return getName();
   }

   /**
    * Set the text to show after the files selection.
    *
    * @param text the text to show
    */
   protected final void setTextAfterFileSelection(String text) {
      textMod = true;
      textopen.setText(text);
      textopen.setCaretPosition(text.length());
      textopen.setHorizontalAlignment(JTextField.LEFT);
      textMod = false;
   }

   @Override
   public void doLayout() {
      if (begin) {
         begin = false;
         if (currentFiles != null) {
            doSelectFiles();
         }
      }
      int height = BUTTONHEIGHT;
      if (this.getParent() != null) {
         Container parent = this.getParent();
         if (parent instanceof CellRendererPane) {
            height = this.getHeight();
         }
      }
      textopen.setPreferredSize(new Dimension((int) (getSize().getWidth() - BUTTONWIDTH), height));
      open.setPreferredSize(new Dimension(BUTTONWIDTH, height));
      super.doLayout();
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension((int) (textopen.getPreferredSize().getWidth() + open.getPreferredSize().getWidth()), BUTTONHEIGHT);
   }

   /**
    * Set the background Color of this FileSelector.
    *
    * @param color the background Color of this FileSelector
    */
   @Override
   public void setBackground(Color color) {
      if (textopen != null) {
         textopen.setBackground(color);
      }
   }

   /**
    * Set the current Directory of this FileSelector.
    *
    * @param f the current Directory to set
    * @return true if the directory could be set
    */
   public boolean setCurrentDirectory(File f) {
      if ((f != null) && (f.exists()) && (f.isDirectory())) {
         currentDir = f;
         return true;
      } else {
         return false;
      }
   }

   /**
    * Return the current Directory of this FileSelector.
    *
    * @return the current Directory of this FileSelector
    */
   public File getCurrentDirectory() {
      return currentDir;
   }

   /**
    * Set the current selected File of this FileSelector.
    *
    * @param f the current selected File of this FileSelector
    */
   public void setSelectedFile(File f) {
      this.selectedFile = f;
      if (f != null) {
         currentFiles = new File[1];
         currentFiles[0] = f;
      } else {
         currentFiles = null;
      }
      if (f != null) {
         String text = fileRenderer.render(f);
         textopen.setText(text);
         textopen.setCaretPosition(text.length());
      } else {
         String text = fileRenderer.renderEmptyFileList();
         textopen.setText(text);
         textopen.setCaretPosition(0);
      }
   }

   private void applySetSelectedFile() {
      if ((selectedFile != null) && (selectedFile.exists())) {
         if ((selectedFile.isFile() && fileSelectionEnabled) || (selectedFile.isDirectory() && dirSelectionEnabled)) {
            currentFiles = new File[1];
            currentFiles[0] = selectedFile;
            if (!begin) {
               doSelectFiles();
            } else {
               setTextAfterFileSelection(selectedFile.getAbsolutePath());
            }
            if (selectedFile.isDirectory()) {
               setCurrentDirectory(selectedFile);
            } else {
               setCurrentDirectory(selectedFile.getParentFile());
            }
         }
      } else if (selectedFile == null) {
         currentFiles = null;
         if (!begin) {
            doSelectFiles();
         }
      }
   }

   /**
    * Used to set the current directory in the case when one of the files while handling the case when one file in the
    * array is null.
    */
   private void setCurrentDirectory(File[] f) {
      for (int i = 0; i < f.length; i++) {
         if (f[i] != null) {
            boolean ok = setCurrentDirectory(f[i].getParentFile());
            if (ok) {
               break;
            }
         }
      }
   }

   /**
    * Set the current selected Files of this FileSelector.
    *
    * @param f the current selected Files of this FileSelector
    */
   public void setSelectedFiles(File[] f) {
      if ((f != null) && (f.length > 0)) {
         currentFiles = f;
         setCurrentDirectory(f);
         if (!begin) {
            doSelectFiles();
         } else {
            setText(f);
         }
      } else {
         currentFiles = null;
         String text = fileRenderer.renderEmptyFileList();
         setTextAfterFileSelection(text);
      }
   }

   /**
    * Set if the component is editable.
    *
    * @param b true if the component is editable
    */
   public void setEditable(boolean b) {
      this.editable = b;
      textopen.setEditable(b);
   }

   /**
    * Enables or disables this component, depending on the value of the parameter b.
    */
   @Override
   public void setEnabled(boolean b) {
      this.enabled = b;
      textopen.setEnabled(b);
      open.setEnabled(b);
   }

   /**
    * Return true if the File Selector is editable.
    *
    * @return true if the File Selector is enabled
    */
   public boolean isEditable() {
      return editable;
   }

   /**
    * Return true if the File Selector is enabled.
    *
    * @return true if the File Selector is enabled
    */
   @Override
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * this private method ensures that we don't invoke the File Chooser too early. On certain cases (like when the
    * selection is performed before the chooser is effectively presented), a null pointer exception can arise when the
    * chooser directory is selected. The invocation will only be performed the first time the component is painted.
    */
   private void doSelectFiles() {
      if (currentFiles != null) {
         this.setCurrentDirectory(currentFiles);
         setText(currentFiles);
      } else {
         String text = fileRenderer.renderEmptyFileList();
         textopen.setText(text);
      }
   }

   /**
    * Set the text to show for a file.
    *
    * @param fs the files
    */
   private void setText(File file) {
      String text = fileRenderer.render(file);
      setTextAfterFileSelection(text);
   }

   /**
    * Set the text to show for a list of files.
    *
    * @param fs the files
    */
   private void setText(File[] fs) {
      String text = fileRenderer.render(fs);
      setTextAfterFileSelection(text);
   }

   private void open() {
      getFileChooser();
      Component parent = this.getParent();
      Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
      setupChooser();

      int ret;
      if (dialogType == JFileChooser.OPEN_DIALOG || !editable) {
         ret = chooser.showOpenDialog(frame);
      } else {
         ret = chooser.showSaveDialog(frame);
      }
      if (ret == JFileChooser.APPROVE_OPTION && editable) {
         if (!chooser.isMultiSelectionEnabled()) {
            selectedFile = chooser.getSelectedFile();
            setText(selectedFile);
            if ((selectedFile != null) && (selectedFile.exists())) {
               currentDir = selectedFile.getParentFile();
            }
            processEvent();
         } else {
            if (isDefaultSorted) {
               files = chooser.getSelectedFiles();
            } else {
               files = chooser.getUnsortedSelectedFiles();
            }
            setText(files);
            if (files != null && files.length >= 1) {
               File theFile = files[0];
               if ((theFile != null) && (theFile.exists())) {
                  currentDir = theFile.getParentFile();
               }
            }
            processEvent();
         }

      }
   }

   private void processEvent() {
      List<ActionListener> listeners = new ArrayList<>(mylisteners);
      Iterator<ActionListener> it = listeners.iterator();
      ActionEvent e = new ActionEvent(chooser, ActionEvent.ACTION_PERFORMED, "SelectFile");
      while (it.hasNext()) {
         it.next().actionPerformed(e);
      }
   }

   private static class ProtectedFuture<V> extends FutureTask<V> {
      public ProtectedFuture(Callable<V> callable) {
         super(callable);
      }

      @Override
      protected void setException(Throwable t) {
      }

      @Override
      public void run() {
         try {
            super.run();
         } catch (Throwable th) {
         }
      }
   }

   private class ExtFileFilter extends FileFilter {
      private final String extension;

      private ExtFileFilter(String extension) {
         this.extension = extension;
      }

      @Override
      public boolean accept(File file) {
         if (file.isDirectory()) {
            return true;
         }
         String path = file.getName();
         int index = path.lastIndexOf('.');
         if (index == -1) {
            return false;
         } else {
            if (index == path.length() - 1) {
               return false;
            } else {
               String ext = path.substring(index + 1).toLowerCase();
               return ext.equals(extension);
            }
         }
      }

      @Override
      public String getDescription() {
         return extension;
      }
   }

   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      JFileSelector fs = new JFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));

      fs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            System.out.println("Action : ");
         }
      });
      f.getContentPane().add(fs);
      f.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      f.pack();
      f.setVisible(true);
   }
}
