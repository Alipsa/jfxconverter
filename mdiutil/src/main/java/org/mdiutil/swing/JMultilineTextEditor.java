/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.mdiutil.lang.SerializingCloner;

/**
 * A Text editor containing a <var>JTextField</var> and a <var>JTextComponent</var> which can be edited through a dialog.
 *
 * @version 0.9.59
 */
public class JMultilineTextEditor extends JComponent {
   private final List<ActionListener> mylisteners = new ArrayList<>();
   private AbstractAction openAction;
   private int BUTTONWIDTH = 30;
   private int BUTTONHEIGHT = 25;
   private final JTextField textopen = new JTextField();
   protected Document doc = null;
   /**
    * The text component used in the dialog.
    */
   protected JTextComponent textComp;
   private JButton open;
   /**
    * The Dialog dimension. The default value is null.
    */
   protected Dimension dim = null;
   private boolean isEditable = true;
   private boolean allowDirectEdit = true;
   private boolean isEditing = false;
   private boolean allowImport = false;
   private File dir = null;
   private ExtensionFileFilter txtfilter = null;
   private static final SerializingCloner<Document> CLONER = new SerializingCloner<>();

   /**
    * Constructor.
    */
   public JMultilineTextEditor() {
      this(new JEditorPane());
   }

   /**
    * Constructor.
    *
    * @param comp the text component
    */
   public JMultilineTextEditor(JTextComponent comp) {
      this.textComp = comp;
      setup();
   }

   /**
    * Constructor.
    *
    * @param columns the number of columns of the text component
    */
   public JMultilineTextEditor(int columns) {
      this();
      textopen.setColumns(columns);
   }

   /**
    * Constructor.
    *
    * @param comp the text component
    * @param columns the number of columns of the text component
    */
   public JMultilineTextEditor(JTextComponent comp, int columns) {
      this(comp);
      textopen.setColumns(columns);
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
    * Return the dimension of the dialog.
    *
    * @return the dimension
    */
   public Dimension getDialogDimension() {
      return dim;
   }

   /**
    * Set if the text is editable.
    *
    * @param isEditable true if the text is editable
    */
   public void setEditable(boolean isEditable) {
      this.isEditable = isEditable;
      textopen.setEditable(isEditable && allowDirectEdit);
      textComp.setEditable(isEditable);
   }

   /**
    * Set if the text in the component can be edited directly (default is true). If false, editing the text can only
    * be performed by opening the edition dialog.
    *
    * @param allowDirectEdit true if the text in the component can be edited directly
    */
   public void allowDirectEdit(boolean allowDirectEdit) {
      this.allowDirectEdit = allowDirectEdit;
      textopen.setEditable(isEditable && allowDirectEdit);
   }

   /**
    * Return true if the text in the component can be edited directly (default is true). If false, editing the text can only
    * be performed by opening the edition dialog.
    *
    * @return if the text in the component can be edited directly
    */
   public boolean isAllowingDirectEdit() {
      return allowDirectEdit;
   }

   /**
    * Return true if the text is editable.
    *
    * @return true if the text is editable
    */
   public boolean isEditable() {
      return isEditable;
   }

   private void setup() {
      openAction = new AbstractAction("...") {
         public void actionPerformed(ActionEvent ae) {
            open();
         }
      };
      textopen.addKeyListener(new KeyListener() {
         @Override
         public void keyTyped(KeyEvent e) {
         }

         @Override
         public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               try {
                  doc.remove(0, doc.getLength());
                  doc.insertString(0, textopen.getText(), null);
               } catch (BadLocationException ex) {
               }
               textComp.setText(textopen.getText());
               processEvent();
            }
         }

         @Override
         public void keyReleased(KeyEvent e) {
         }
      });
      cloneDocument(textComp.getDocument());
      setText();

      open = new JButton(openAction);
      this.add(textopen);
      this.add(open);
      this.setLayout(null);
      //int preftextopenwidth = (int)(textopen.getPreferredSize().getWidth());
      int preftextopenwidth = 200;
      BUTTONWIDTH = (int) (open.getPreferredSize().getWidth());
      BUTTONHEIGHT = (int) (open.getPreferredSize().getHeight());
      textopen.setPreferredSize(new Dimension(preftextopenwidth, BUTTONHEIGHT));
      open.setPreferredSize(new Dimension(BUTTONWIDTH, BUTTONHEIGHT));
   }

   public JTextComponent getTextComponent() {
      return textComp;
   }

   private void cloneDocument(Document doc) {
      try {
         this.doc = CLONER.deepClone(doc);
      } catch (IOException | ClassNotFoundException e) {
      }
   }

   /**
    * Set the text component to use with the text editor.
    *
    * @param comp the text component
    */
   public void setTextComponent(JTextComponent comp) {
      this.textComp = comp;
   }

   /**
    * Return the associated Document.
    *
    * @return the associated Document
    */
   public Document getDocument() {
      return doc;
   }

   /**
    * Return the assocuated text.
    *
    * @return the assocuated text
    */
   public String getText() {
      return getDocumentText();
   }

   /**
    * Set the associated Document.
    *
    * @param doc the Document
    */
   public void setDocument(Document doc) {
      this.doc = doc;
      try {
         textopen.setText(doc.getText(0, doc.getLength()));
      } catch (BadLocationException ex) {
      }
   }

   /**
    * Set the associated text.
    *
    * @param text the text
    */
   public void setText(String text) {
      textComp.setText(text);
      textopen.setText(text);
      try {
         doc.remove(0, doc.getLength());
         doc.insertString(0, text, null);
      } catch (BadLocationException ex) {
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

   @Override
   public String toString() {
      return getDocumentText();
   }

   /**
    * Resets the selector to the values of thetext component.
    */
   public void reset() {
      setText();
      this.repaint();
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
      open.setSize(BUTTONWIDTH, height);
      open.setPreferredSize(new Dimension(BUTTONWIDTH, height));
      super.doLayout();
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension((int) (textopen.getPreferredSize().getWidth() + open.getPreferredSize().getWidth()), BUTTONHEIGHT);
   }

   @Override
   public void setBackground(Color color) {
      textopen.setBackground(color);
   }

   /**
    * Enables or disables this component.
    *
    * @param enabled true if this component should be enabled, false otherwise
    */
   @Override
   public void setEnabled(boolean enabled) {
      open.setEnabled(enabled);
      textopen.setEnabled(enabled);
   }

   /**
    * Return true if the List Selector is enabled.
    *
    * @return true if the List Selector is enabled
    */
   @Override
   public boolean isEnabled() {
      return open.isEnabled();
   }

   private String getDocumentText() {
      String text = "";
      try {
         text = doc.getText(0, doc.getLength());
      } catch (BadLocationException ex) {
      }
      return text;
   }

   /**
    * Set the text in the component.
    */
   protected void setText() {
      String text = getDocumentText();
      if (textopen.getColumns() > 0 && text.length() > textopen.getColumns()) {
         text = text.substring(0, textopen.getColumns() - 3) + "...";
      }
      textopen.setText(text);
   }

   /**
    * Create the dialog which will be used to edit the text.
    *
    * @return the dialog
    */
   protected AbstractMultilineDialog createMultilineDialog() {
      JMultilineDialog dialog = new JMultilineDialog(textComp);
      dialog.setAllowImport(allowImport);
      dialog.setImportDefaultDirectory(dir);
      dialog.setImportFileFilter(txtfilter);
      if (dim != null) {
         dialog.setDialogDimension(dim);
      }
      return dialog;
   }

   private void open() {
      AbstractMultilineDialog dialog = createMultilineDialog();
      isEditing = true;
      textComp.setDocument(doc);
      int retour = dialog.showDialog(this);
      if (retour == JListChooser.APPROVE_OPTION) {
         processOpen();
      }
      isEditing = false;
   }

   private void processOpen() {
      if (isEditing) {
         cloneDocument(textComp.getDocument());
         setText();
         processEvent();
      }
   }

   private void processEvent() {
      Iterator<ActionListener> it = mylisteners.iterator();
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ChangeText");
      while (it.hasNext()) {
         it.next().actionPerformed(e);
      }
   }
}
