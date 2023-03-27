/*------------------------------------------------------------------------------
 * Copyright (C) 2007, 2008, 2010, 2012, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class represents a dialog to display an error (message + Exception).
 *
 * @version 0.9.6
 */
public class JErrorPane extends JPanel {
   /**
    * The error message.
    */
   protected String msg;
   /**
    * The stack trace.
    */
   protected String stacktrace;
   /**
    * The text area used to show the stack trace.
    */
   protected JComponent detailsArea;
   /**
    * The button used to show or not the details.
    */
   protected JButton showDetailButton;
   /**
    * The button used to save the errors.
    */
   protected JButton saveButton;
   /**
    * The button used to close the dialog.
    */
   protected JButton okButton;
   /**
    * This flag bit indicates whether or not the stack trace is shown.
    */
   protected boolean isDetailShown = false;
   /**
    * The sub panel that contains the stack trace text area.
    */
   protected JPanel subpanel;
   /**
    * The sub panel that contains the buttons.
    */
   protected JPanel buttonsPanel = null;
   private boolean firstCall = true;
   private JTextArea details = null;
   public static final int DEFAULT_ROWS = 20;

   /**
    * Constructs a new JErrorPane.
    *
    * @param th the throwable object that describes the error
    */
   public JErrorPane(Throwable th) {
      this(th, JOptionPane.ERROR_MESSAGE);
   }

   /**
    * Constructs a new JErrorPane.
    *
    * @param th the throwable object that describes the error
    * @param type the panel type. See {@link JOptionPane}
    */
   public JErrorPane(Throwable th, int type) {
      this(th, null, type);
   }

   /**
    * Constructs a new JErrorPane.
    *
    * @param th the throwable object that describes the error
    * @param title the title
    * @param type the panel type. See {@link JOptionPane}
    */
   public JErrorPane(Throwable th, String title, int type) {
      super(new BorderLayout());

      setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.msg = th.getClass().getName() + "\n\n" + th.getMessage();

      StringWriter writer = new StringWriter();
      th.printStackTrace(new PrintWriter(writer));
      writer.flush();
      this.stacktrace = writer.toString();

      JTextArea msgArea = new JTextArea();
      msgArea.setText(msg);
      msgArea.setColumns(50);
      msgArea.setFont(new JLabel().getFont());
      msgArea.setForeground(new JLabel().getForeground());
      msgArea.setOpaque(false);
      msgArea.setEditable(false);
      msgArea.setLineWrap(true);
      add(msgArea, BorderLayout.NORTH);

      // to be sure that buttonsPanel is never null
      buttonsPanel = new JPanel(new FlowLayout());
      add(createButtonsPanel(), BorderLayout.SOUTH);

      details = new JTextArea();
      details.setColumns(50);
      details.setRows(DEFAULT_ROWS);
      details.setText(stacktrace);
      details.setEditable(false);

      detailsArea = new JPanel(new BorderLayout(0, 10));
      detailsArea.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
      detailsArea.add(new JScrollPane(details), BorderLayout.CENTER);

      subpanel = new JPanel(new BorderLayout());

      add(subpanel, BorderLayout.CENTER);
   }

   private void close() {
      ((JDialog) getTopLevelAncestor()).dispose();
   }

   private void save() {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Save Error Panel");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int ret = chooser.showSaveDialog(((JDialog) getTopLevelAncestor()));
      if (ret == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(msg);
            writer.newLine();
            writer.write(stacktrace);
            writer.flush();
            writer.close();
         } catch (IOException ex) {
            System.out.println(ex.getMessage());
         }
      }
   }

   private void showDetails() {
      if (isDetailShown) {
         subpanel.remove(detailsArea);
         isDetailShown = false;
         showDetailButton.setText("Show Details");
      } else {
         subpanel.add(detailsArea, BorderLayout.CENTER);
         showDetailButton.setText("Hide Details");
         isDetailShown = true;
      }
      ((JDialog) getTopLevelAncestor()).pack();
   }

   /**
    * Return the panel used for the buttons.
    *
    * @return the panel used for the buttons
    */
   public JPanel getButtonsPanel() {
      return this.buttonsPanel;
   }

   /**
    * Set the maximum number of lines to be shown in the error pane.
    *
    * @param rows the maximum number of lines to be shown in the error pane
    */
   public void setMaximumRows(int rows) {
      details.setRows(rows);
   }

   /**
    * Do nothing by default. This is called at the beginning of the first
    * {@link #createDialog(Component, String)} method and can be used to add
    * customized content to the error panel.
    */
   protected void addCustomizedContent() {
   }

   /**
    * Creates the Dialog.
    *
    * @param owner the Dialog owner
    * @param title the title
    * @return the Dialog
    */
   public JDialog createDialog(Component owner, String title) {
      if (firstCall) {
         addCustomizedContent();
      }
      firstCall = false;
      JDialog dialog
         = new JDialog(JOptionPane.getFrameForComponent(owner), title);
      dialog.getContentPane().add(this, BorderLayout.CENTER);
      dialog.pack();
      return dialog;
   }

   /**
    * Create the Buttons panel.
    *
    * @return the panel
    */
   protected JPanel createButtonsPanel() {
      buttonsPanel = new JPanel(new FlowLayout());

      showDetailButton = new JButton("Show Details");

      showDetailButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDetails();
         }
      });

      saveButton = new JButton("Save");
      saveButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            save();
         }
      });

      buttonsPanel.add(saveButton);

      buttonsPanel.add(Box.createHorizontalStrut(30));

      buttonsPanel.add(showDetailButton);

      okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            close();
         }
      });

      buttonsPanel.add(Box.createHorizontalStrut(30));
      buttonsPanel.add(okButton);
      return buttonsPanel;
   }
}
