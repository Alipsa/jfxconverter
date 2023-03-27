/*------------------------------------------------------------------------------
 * Copyright (C) 2014, 2015, 2017, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * A multiple File selector where each file can be in its own directory.
 *
 * @version 1.2.25.4
 */
public class JMultipleFileSelector extends JComponent {
   /**
    * The Dialog title.
    */
   protected String title = "Open Files";
   private File currentDir = null;
   private AbstractAction openAction;
   private JButton open;
   private final JTextField textopen = new JTextField();
   private int BUTTONWIDTH = 30;
   private int BUTTONHEIGHT = 25;
   private File[] currentFiles = null;
   /**
    * The file renderer.
    */
   protected FileSelectorRenderer fileRenderer = new DefaultFileSelectorRenderer();
   private List<FileFilter> choosableFileFilters = null;
   private int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
   private JPanel panel = null;
   private final List<JButton> addButtons = new ArrayList<>();
   private final List<JButton> removeButtons = new ArrayList<>();
   private final List<JFileSelector> selectors = new ArrayList<>();
   private final List<ActionListener> mylisteners = new ArrayList<>();
   private final SelectorListener slistener = new SelectorListener();

   /**
    * Create a new JFileMultipleSelector.
    */
   public JMultipleFileSelector() {
      super();
      createUI();
   }

   /**
    * Create a new JFileMultipleSelector.
    *
    * @param title the FileChooser title
    */
   public JMultipleFileSelector(String title) {
      super();
      this.title = title;
      createUI();
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
    * Set the class responsible to render to text associated to the selected files. Note that is will
    * set the renderer to a {@link DefaultFileSelectorRenderer} if the method argument is null.
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
    * Set the associated FileChooser file selection mode.
    *
    * @param mode the associated FileChooser file selection mode
    * @see JFileChooser#setFileSelectionMode(int)
    */
   public void setFileSelectionMode(int mode) {
      this.fileSelectionMode = mode;
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
    * Add a Filefilter to this MultipleFileSelector.
    *
    * @param filter the Filefilter
    */
   public void addChoosableFileFilter(FileFilter filter) {
      if (choosableFileFilters == null) {
         choosableFileFilters = new ArrayList(5);
      }
      choosableFileFilters.add(filter);
   }

   /**
    * Remove a Filefilter to this MultipleFileSelector.
    *
    * @param filter the Filefilter
    */
   public void removeChoosableFileFilter(FileFilter filter) {
      if (choosableFileFilters != null) {
         choosableFileFilters.remove(filter);
      }
   }

   /**
    * Add an ActionListener to this FileSelector.
    *
    * @param listener the ActionListener
    */
   public void addActionListener(ActionListener listener) {
      mylisteners.add(listener);
   }

   /**
    * Remove an ActionListener to this FileSelector.
    *
    * @param listener the ActionListener
    */
   public void removeActionListener(ActionListener listener) {
      mylisteners.remove(listener);
   }

   private void processEvent() {
      JFileSelector.getFileChooser();
      List<ActionListener> listeners = new ArrayList<>(mylisteners);
      Iterator<ActionListener> it = listeners.iterator();
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SelectFile");
      while (it.hasNext()) {
         it.next().actionPerformed(e);
      }
   }

   /**
    * Return the current selected Files of this File Selector.
    *
    * @return the current selected Files of this File Selector
    */
   public File[] getSelectedFiles() {
      return currentFiles;
   }

   /**
    * Set the current selected Files of this File Selector.
    *
    * @param f the current selected Files of this File Selector
    */
   public void setSelectedFiles(File[] f) {
      this.currentFiles = f;
      if (f != null && f.length > 0) {
         this.currentDir = f[0].getParentFile();
         setText(f);
      }
   }

   /**
    * Set the text to show for a list of files.
    *
    * @param fs the files
    */
   protected void setText(File[] fs) {
      String text = fileRenderer.render(fs);
      setTextAfterFileSelection(text);
   }

   private void createUI() {
      openAction = new AbstractAction("...") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            open();
         }
      };
      textopen.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
               String text = textopen.getText();
               if ((text == null) || (text.trim().length() == 0)) {
                  setSelectedFiles(null);
                  processEvent();
               } else {
                  File filemod = new File(text);
                  boolean fileSelectionEnabled = fileSelectionMode == JFileChooser.FILES_AND_DIRECTORIES || fileSelectionMode== JFileChooser.FILES_ONLY;
                  boolean dirSelectionEnabled = fileSelectionMode == JFileChooser.FILES_AND_DIRECTORIES || fileSelectionMode== JFileChooser.DIRECTORIES_ONLY;                  
                  if ((filemod.exists() && (filemod.isFile() && fileSelectionEnabled) || (filemod.isDirectory() && dirSelectionEnabled))
                     || (!filemod.exists())) {
                     File file  = new File(filemod.getAbsolutePath());
                     File[] files = new File[1];
                     files[0] = file;
                     setSelectedFiles(files);
                     processEvent();
                  }
               }
            }
      });      
      open = new JButton(openAction);
      this.add(textopen);
      this.add(open);
      this.setLayout(null);
      int preftextopenwidth = 200;
      BUTTONWIDTH = (int) (open.getPreferredSize().getWidth());
      BUTTONHEIGHT = (int) (open.getPreferredSize().getHeight());
      textopen.setPreferredSize(new Dimension(preftextopenwidth, BUTTONHEIGHT));
      open.setPreferredSize(new Dimension(BUTTONWIDTH, BUTTONHEIGHT));
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension((int) (textopen.getPreferredSize().getWidth() + open.getPreferredSize().getWidth()), BUTTONHEIGHT);
   }

   @Override
   public void doLayout() {
      int height = BUTTONHEIGHT;
      if (this.getParent() != null) {
         Container parent = this.getParent();
         if (parent instanceof CellRendererPane) {
            height = this.getHeight();
         }
      }
      textopen.setBounds(0, 0, (int) (this.getSize().getWidth() - BUTTONWIDTH), height);
      open.setBounds((int) (this.getSize().getWidth() - BUTTONWIDTH), 0, BUTTONWIDTH, height);
      super.doLayout();
   }

   /**
    * Set the text to show after the files selection.
    *
    * @param text the text to show
    */
   protected final void setTextAfterFileSelection(String text) {
      textopen.setText(text);
      textopen.setCaretPosition(text.length());
      textopen.setHorizontalAlignment(JTextField.LEFT);
   }

   private void open() {
      Component parent = this.getParent();
      MultipleSelectionDialog dialog = new MultipleSelectionDialog(parent);
      int ret = dialog.showDialog();
      if (ret == JFileChooser.APPROVE_OPTION) {
         currentFiles = dialog.getSelectedFiles();
         if (currentFiles != null && currentFiles.length != 0 && currentFiles[0] != null) {
            String text = fileRenderer.render(currentFiles);
            setTextAfterFileSelection(text);
            processEvent();
         }
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

   public static void main(String arg[]) {
      JFrame f = new JFrame("test");
      JMultipleFileSelector fs = new JMultipleFileSelector();
      fs.setCurrentDirectory(new File(System.getProperty("user.dir")));
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

   private class MultipleSelectionDialog extends GenericDialog {
      private List<File> selectedFiles = new ArrayList();

      public MultipleSelectionDialog(Component parent) {
         super("Files Selections", parent, true);
         this.setResizable(true);
      }

      /**
       * Show the dialog.
       *
       * @return the JFileChooser return value.
       */
      @Override
      public int showDialog() {
         if (dialog != null) {
            dialog.pack();
            dialog.setVisible(true);
         } else {
            frame.pack();
            frame.setVisible(true);
         }

         return returnValue;
      }

      /**
       * Return the selected Files.
       *
       * @return the selected Files
       */
      public File[] getSelectedFiles() {
         selectedFiles.clear();
         for (int i = 0; i < selectors.size(); i++) {
            selectedFiles.add(selectors.get(i).getSelectedFile());
         }
         currentFiles = new File[selectedFiles.size()];
         return selectedFiles.toArray(currentFiles);
      }

      @Override
      protected void createPanel() {
         if (currentFiles != null) {
            selectedFiles = Arrays.asList(currentFiles);
         } else {
            selectedFiles = new ArrayList();
         }
         // this is necessary to be sure that the selectors list is empty at the creation of the panel
         selectors.clear();

         // Options panel
         JPanel yesnopanel = this.createYesNoPanel();

         Container pane = dialog.getContentPane();

         panel = new JPanel();
         BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
         panel.setLayout(layout);

         pane.setLayout(new BorderLayout());
         pane.add(new JScrollPane(panel), BorderLayout.CENTER);
         pane.add(yesnopanel, BorderLayout.SOUTH);

         if (selectedFiles.size() > 1) {
            for (int i = 0; i < selectedFiles.size(); i++) {
               boolean hasAddRemove = i == selectedFiles.size() - 1;
               addFile(selectedFiles.get(i), i, hasAddRemove, hasAddRemove);
            }
         } else if (selectedFiles.size() == 1) {
            addFile(selectedFiles.get(0), 0, true, false);
         } else {
            addFile(null, 0, true, false);
         }
      }

      private void addFile(File file, int index, boolean hasAdd, boolean hasRemove) {
         JPanel filePanel = new JPanel();
         FlowLayout layout = new FlowLayout();
         filePanel.setLayout(layout);

         JFileSelector selector = new JFileSelector("Select File");
         selector.addActionListener(slistener);
         selectors.add(selector);
         selector.setChoosableFileFilters(choosableFileFilters);
         selector.setDialogType(JFileChooser.OPEN_DIALOG);
         selector.setFileSelectionMode(fileSelectionMode);
         selector.setMultiSelectionEnabled(false);
         if (file != null) {
            selector.setSelectedFile(file);
            selector.setCurrentDirectory(file.getParentFile());
         } else {
            selector.setCurrentDirectory(currentDir);
         }
         filePanel.add(selector);
         Dimension dim = filePanel.getPreferredSize();
         dim.setSize(dim.width, selector.getPreferredSize().height + 10);
         dim = filePanel.getMaximumSize();
         dim.setSize(dim.width, selector.getPreferredSize().height + 10);
         JButton button = new JButton("Add");
         button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     addFile(null, addButtons.size(), true, true);
                  }
               });
            }
         });
         addButtons.add(button);
         button.setEnabled(hasAdd);
         filePanel.add(button);
         button = new JButton("Remove");
         button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     removeLastFile();
                  }
               });
            }
         });
         removeButtons.add(button);
         button.setEnabled(hasRemove);
         if (index != 0) {
            panel.remove(panel.getComponentCount() - 1);
         }
         filePanel.add(button);
         if (index != 0) {
            panel.add(Box.createVerticalStrut(5));
            addButtons.get(index - 1).setEnabled(false);
            removeButtons.get(index - 1).setEnabled(false);
         }
         panel.add(filePanel);
         panel.add(Box.createGlue());
         panel.revalidate();
         packDialog();
      }

      private void packDialog() {
         if (frame != null) {
            frame.pack();
         } else if (dialog != null) {
            dialog.pack();
         }
      }

      private void removeLastFile() {
         if (addButtons.size() > 1) {
            while (true) {
               int index = panel.getComponentCount() - 1;
               Component comp = panel.getComponent(index);
               if (comp instanceof Box.Filler) {
                  panel.remove(index);
               } else if (comp instanceof JPanel) {
                  panel.remove(index);
                  break;
               }
            }
            selectors.remove(selectors.size() - 1);
            addButtons.remove(addButtons.size() - 1);
            removeButtons.remove(removeButtons.size() - 1);
            addButtons.get(addButtons.size() - 1).setEnabled(true);
            if (removeButtons.size() > 1) {
               removeButtons.get(removeButtons.size() - 1).setEnabled(true);
            }
            panel.revalidate();
            panel.repaint();
            packDialog();

         }
      }
   }

   private class SelectorListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if (e.getSource() instanceof JFileChooser) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            if (file != null && file.exists()) {
               currentDir = file.getParentFile();
            }
         }
      }

   }
}
