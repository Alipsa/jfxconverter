/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
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
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * An abstract MultilineDialog.
 *
 * @version 0.9.59
 */
public abstract class AbstractMultilineDialog extends JComponent {
   private int returnValue;
   private JDialog dialog = null;
   /**
    * The text component.
    */
   protected JTextComponent textComp;
   /**
    * The dialog dimension.
    */
   protected Dimension dim = null;
   /**
    * The Yes action.
    */
   protected AbstractAction yesaction;
   /**
    * The Cancel action.
    */
   protected AbstractAction cancelaction;
   /**
    * The import action.
    */
   protected AbstractAction importAction = null;
   /**
    * True if the dialog allow to import a text file.
    */
   protected boolean allowImport = false;
   /**
    * The default directory if the dialog allow to import a text file.
    */
   protected File dir = null;
   /**
    * The file filter if the dialog allow to import a text file.
    */
   protected ExtensionFileFilter txtfilter = null;
   public static final int APPROVE_OPTION = 0;
   public static final int CANCEL_OPTION = 1;
   private boolean isEditable = true;
   private String dialogTitle = "Edit Text";

   /**
    * Constructor.
    *
    * @param textComp the text component
    */
   public AbstractMultilineDialog(JTextComponent textComp) {
      this.textComp = textComp;
   }

   /**
    * Set if the text is editable.
    *
    * @param isEditable true if the text is editable
    */
   public void setEditable(boolean isEditable) {
      this.isEditable = isEditable;
   }

   /**
    * Return true if the text is editable.
    *
    * @return true if the text is editable
    */
   public boolean isEditable() {
      return isEditable;
   }

   /**
    * Set the dialog title.
    *
    * @param dialogTitle the dialog title
    */
   public void setDialogTitle(String dialogTitle) {
      this.dialogTitle = dialogTitle;
   }

   /**
    * Return the dialog title.
    *
    * @return the dialog title
    */
   public String getDialogTitle() {
      return dialogTitle;
   }

   /**
    * Set if the editor has an import text file option.
    *
    * @param allowImport true if the editor has an import text file option
    */
   public void setAllowImport(boolean allowImport) {
      this.allowImport = allowImport;
   }

   /**
    * Return true if the editor has an import text file option.
    *
    * @return true if the editor has an import text file option
    */
   public boolean isAllowingImport() {
      return allowImport;
   }

   /**
    * Set the default directory if the editor has an import text file option.
    *
    * @param dir the default directory if the editor has an import text file option
    */
   public void setImportDefaultDirectory(File dir) {
      this.dir = dir;
   }

   /**
    * Return the default directory if the editor has an import text file option.
    *
    * @return the default directory if the editor has an import text file option
    */
   public File getImportDefaultDirectory() {
      return dir;
   }

   /**
    * Set the file filter if the editor has an import text file option.
    *
    * @param filter the file filter if the editor has an import text file option
    */
   public void setImportFileFilter(ExtensionFileFilter filter) {
      this.txtfilter = filter;
   }

   /**
    * Return the file filter if the editor has an import text file option.
    *
    * @return the file filter if the editor has an import text file option
    */
   public ExtensionFileFilter getImportFileFilter() {
      return txtfilter;
   }

   /**
    * Set the dimension of the dialog.
    *
    * @param dim the dimension
    */
   public void setDialogDimension(Dimension dim) {
      this.dim = dim;
   }

   /**
    * Return the dimension of the dialog.
    *
    * @return the dimension
    */
   public Dimension getDialogDimension() {
      return dim;
   }

   /**
    * Show the dialog.
    *
    * @param parent the parent component
    * @return the return value
    */
   public int showDialog(Component parent) throws HeadlessException {
      dialog = this.createDialog(dialogTitle, parent);

      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = CANCEL_OPTION;
         }
      });

      dialog.setVisible(true);
      dialog.dispose();
      dialog = null;

      return returnValue;
   }

   /**
    * Create the dialog.
    *
    * @param title the dialog title
    * @param parent the parent component
    * @return the dialog
    * @throws HeadlessException
    */
   public JDialog createDialog(String title, Component parent) throws HeadlessException {
      setUp();
      Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

      dialog = new JDialog(frame, title, true);

      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            returnValue = JFileChooser.CANCEL_OPTION;
         }
      });

      Container contentPane = dialog.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(this, BorderLayout.CENTER);

      dialog.pack();

      return dialog;
   }

   /**
    * Set up the panel contents.
    */
   protected abstract void setUp();

   private void importText() {
      Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, textComp);
      if (dir == null) {
         dir = new File(System.getProperty("user.dir"));
      }
      JFileChooser chooser = new JFileChooser(dir);
      chooser.setDialogTitle("Select Text file");
      if (txtfilter != null) {
         chooser.setFileFilter(txtfilter);
      }
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      if (chooser.showOpenDialog(frame) != JFileChooser.CANCEL_OPTION) {
         File file = chooser.getSelectedFile();
         try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder buf = new StringBuilder();
            String line;
            boolean started = false;
            while (true) {
               line = reader.readLine();
               if (line == null) {
                  break;
               }
               if (started) {
                  buf.append("\n");
               } else {
                  started = true;
               }
               buf.append(line);
            }
            String str = buf.toString();
            if (!str.isEmpty()) {
               setText(str);
            }
         } catch (IOException ex) {
         }
      }
   }

   /**
    * Set the associated text.
    *
    * @param text the text
    */
   public void setText(String text) {
      textComp.setText(text);
   }

   /**
    * Create the Yes / No bottom Panel.
    *
    * Note that it is possible to define a completely different panel can be defined by overriding this method. It is
    * even possible to define a panel with a completely different content, even without Yes /No buttons.
    *
    * @return the Yes/No Panel
    */
   protected JPanel createYesNoPanel() {
      // yes/no panel
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new FlowLayout());

      if (allowImport) {
         importAction = new AbstractAction("Import Text") {
            @Override
            public void actionPerformed(ActionEvent ae) {
               importText();
            }
         };
      }

      yesaction = new AbstractAction("Yes") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doYes();
         }
      };

      cancelaction = new AbstractAction("Cancel") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doCancel();
         }
      };

      if (!textComp.isEditable()) {
         JButton okButton = new JButton(cancelaction);
         okButton.setText("OK");
         thePanel.add(okButton, BorderLayout.EAST);
      } else if (allowImport) {
         thePanel.add(new JButton(importAction), BorderLayout.WEST);
         thePanel.add(new JButton(yesaction), BorderLayout.CENTER);
         thePanel.add(new JButton(cancelaction), BorderLayout.EAST);
      } else {
         thePanel.add(new JButton(yesaction), BorderLayout.WEST);
         thePanel.add(new JButton(cancelaction), BorderLayout.EAST);
      }
      thePanel.setMaximumSize(thePanel.getMinimumSize());

      return thePanel;
   }

   /**
    * Called when the user clicks on the Yes button.
    */
   protected void doYes() {
      returnValue = APPROVE_OPTION;

      if (dialog != null) {
         dialog.setVisible(false);
      }
   }

   /**
    * Called when the user clicks on the Cancel button.
    */
   protected void doCancel() {
      returnValue = CANCEL_OPTION;
      if (dialog != null) {
         dialog.setVisible(false);
      }
   }
}
