/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2016, 2018, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A generic dialog component, with Yes / No buttons. The minimal method to implement is the {@link #createPanel()} method,
 * which allows to create the main panel of the dialog.
 *
 * <h1>Using the Dialog</h1>
 * The basic way to use the dialog involve:
 * <ul>
 * <li>Creating the dialog</li>
 * <li>Showing the dialog: {@link #showDialog()}</li>
 * </ul>
 *
 * Note that the default result of clicking the "Yes" or "No" buttons is disposing of the dialog.
 * <h2>Example</h2>
 * Example of a MyDialog class usage, extending the GenericDialog class:
 * <pre>
 *  MyDialog dialog = new MyDialog(parent);
 *  dialog.setDialogTitle("My Dialog");
 *  int ret = dialog.showDialog();
 *  if (ret == JFileChooser.APPROVE_OPTION) {
 *    ... do something
 *  }
 *
 * </pre>
 *
 * <h1>Deriving the GenericDialog class</h1>
 * <h2>Basic dialog</h2>
 * The minimal method to implement is the {@link #createPanel()} method, which allows to create the main panel of the
 * dialog. In this case, the dialog will present:
 * <ul>
 * <li>The main panel</li>
 * <li>A bottom bar, with a "Yes", and "Cancel" buttons</li>
 * </ul>
 *
 * It is also advisable to set the title of the Dialog.
 *
 * <h2>Advance derivation</h2>
 * The simplest way to derive the dialog is to change the Labels for the default "Yes" and "No" buttons
 * (see {@link #setYesNoLabels(String, String)}). It is possible to change the behavior when clicking these
 * buttons by overriding the {@link #doYes()} or {@link #doCancel()} methods.
 *
 * A more advanced derivation is by creating a different bottom panel (see {@link #createYesNoPanel()}). It is also possible
 * to add a menuBar to the dialog.
 *
 * @version 1.2.25.5
 */
public abstract class GenericDialog extends JComponent {
   /**
    * The internal dialog.
    */
   protected JDialog dialog;
   /**
    * The internal dialog associated frame.
    */
   protected JFrame frame;
   /**
    * The "Yes" Label. The default value is "Yes".
    */
   protected String yesLabel = "Yes";
   /**
    * The "Ok" Label. The default value is "OK".
    */
   protected String okLabel = "OK";
   /**
    * The "No" Label. The default value is "Cancel".
    */
   protected String noLabel = "Cancel";
   private boolean resizable = false;
   /**
    * The action associated with the default "Yes" button.
    */
   protected AbstractAction yesAction;
   /**
    * The action associated with the default "No" button.
    */
   protected AbstractAction cancelAction;
   /**
    * The return value.
    */
   protected int returnValue;
   /**
    * The location of the dialog.
    */
   protected Point dialogLocation = null;
   private String title = null;
   private JMenuBar menuBar = null;
   private boolean hasApply = false;
   private boolean isModal = true;
   private boolean isAlwaysOnTop = false;
   private final List<DialogListener> listeners = new ArrayList<>();

   /**
    * Constructor.
    */
   public GenericDialog() {
   }

   /**
    * Constructor.
    *
    * @param isModal true if the dialog is modal
    */
   public GenericDialog(boolean isModal) {
      this.isModal = isModal;
   }

   /**
    * Constructor, with a title, a dialog owner, and a modality.
    *
    * @param title the title
    * @param parent the dialog owner, may be a heavyweight component (like a JFrame), or a smallweight one, the class
    * will automatically look for the heavyweight root
    * @param isModal true if the dialog is modal
    */
   public GenericDialog(String title, Component parent, boolean isModal) {
      this(title);
      this.isModal = isModal;
      this.createDialog(parent, isModal);
   }

   /**
    * Constructor, with a title.
    *
    * @param title the title
    */
   public GenericDialog(String title) {
      this.title = title;
   }

   /**
    * Constructor, with a title.
    *
    * @param title the title
    * @param isModal true if the dialog is modal
    */
   public GenericDialog(String title, boolean isModal) {
      this.title = title;
      this.isModal = isModal;
   }

   /**
    * Set the title of the Dialog.
    *
    * @param title the Dialog title
    */
   public void setDialogTitle(String title) {
      this.title = title;
   }

   /**
    * Set a MenuBar to use with this Dialog.
    *
    * @param menus the MenuBar
    */
   public void setJMenuBar(JMenuBar menus) {
      this.menuBar = menus;
   }

   /**
    * Set the modality of the dialog.
    *
    * @param isModal true if the dialog is modal
    */
   public void setModal(boolean isModal) {
      this.isModal = isModal;
   }

   /**
    * Return the modality of the dialog.
    *
    * @return true if the dialog is modal
    */
   public boolean isModal() {
      return isModal;
   }

   /**
    * Sets whether this dialog should always be above other windows.
    *
    * @param alwaysOnTop true if this dialog should always be above other windows
    */
   public void setAlwaysOnTop(boolean alwaysOnTop) {
      this.isAlwaysOnTop = alwaysOnTop;
   }

   /**
    * Return true if this dialog should always be above other windows.
    *
    * @return true if this dialog should always be above other windows
    */
   public boolean isAlwaysOnTop() {
      return isAlwaysOnTop;
   }

   /**
    * Return the MenuBar to use with this Dialog (may be null).
    *
    * @return the MenuBar
    */
   public final JMenuBar getJMenuBar() {
      return menuBar;
   }

   /**
    * Set the dialog location.
    *
    * @param location the dialog location
    */
   public final void setDialogLocation(Point location) {
      this.dialogLocation = location;
   }

   /**
    * Return the dialog location.
    *
    * @return the dialog location
    */
   public final Point getDialogLocation() {
      return dialogLocation;
   }

   /**
    * Set a dialog listener which will be fired if the dialog is applied (if clicking on "Yes") or cancelled (if
    * clicking on "Cancel" or closing the window without applying the dialog).
    *
    * @param listener the dialog listener
    */
   public void addDialogListener(DialogListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   /**
    * Remove a dialog listener.
    *
    * @param listener the dialog listener
    */
   public void removeDialogListener(DialogListener listener) {
      listeners.remove(listener);
   }

   /**
    * Return the Dialog listeners which will be fired if the dialog is applied or cancelled.
    *
    * @return the Dialog listeners
    */
   public List<DialogListener> getDialogListeners() {
      return listeners;
   }

   /**
    * Return the dialog.
    *
    * @return the dialog
    */
   public JDialog getDialog() {
      return dialog;
   }

   /**
    * Return the dialog Frame.
    *
    * @return the dialog Frame
    */
   public JFrame getFrame() {
      return frame;
   }

   /**
    * Set if the Dialog panel is resizable.
    *
    * @param isResizable true if the Dialog panel is resizable
    */
   public void setResizable(boolean isResizable) {
      this.resizable = isResizable;
      if (dialog != null) {
         dialog.setResizable(isResizable);
      } else if (frame != null) {
         frame.setResizable(isResizable);
      }
   }

   /**
    * Set the labels for the Yes / No buttons in the default exit panel.
    *
    * @param yesLabel the label for the Yes button
    * @param noLabel the label for the No button
    */
   public void setYesNoLabels(String yesLabel, String noLabel) {
      this.yesLabel = yesLabel;
      this.noLabel = noLabel;
   }

   /**
    * Set the labels for the OK buttons in the default exit panel.
    *
    * @param okLabel the label for the OK button
    */
   public void setOKLabel(String okLabel) {
      this.okLabel = okLabel;
   }

   /**
    * Create the dialog as a JFrame, as a child of its parent owner. THis method can be used when extending this class, if the
    * dialog implementer does want to use a JFrame instead of a JDialog.
    * <h1>Example</h1>
    * <pre>
    * public MyDialog(String title, Component parent) {
    *    this(title);
    *    this.createFrame(parent);
    * }
    * </pre>
    *
    * @param parent the dialog owner (may be null)
    * @return the dialog Frame
    */
   protected final JFrame createFrame(Component parent) {
      frame = new JFrame(title);
      if (title != null) {
         frame.setTitle(title);
      }
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(resizable);
      // we allow to add a menubar if it was added to the generic Dialog
      if (menuBar != null) {
         frame.setJMenuBar(menuBar);
      }

      createPanel();

      frame.pack();
      frame.setLocationRelativeTo(parent);

      frame.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            cancelImpl();
         }
      });
      returnValue = JFileChooser.ERROR_OPTION;

      return frame;
   }

   /**
    * Create the dialog, as a child of its parent owner.
    *
    * @param parent the dialog owner (may be null)
    * @return the dialog
    */
   protected JDialog createDialog(Component parent) {
      return createDialog(parent, isModal);
   }

   /**
    * Create the dialog as a JDialog, as a child of its parent owner.
    *
    * @param parent the dialog owner (may be null)
    * @param modal true if the dialog is modal
    * @return the dialog
    */
   protected final JDialog createDialog(Component parent, boolean modal) {
      Frame owner = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

      dialog = new JDialog(owner, modal);
      if (title != null) {
         dialog.setTitle(title);
      }
      dialog.setAlwaysOnTop(isAlwaysOnTop);
      dialog.setResizable(resizable);
      // we allow to add a menubar if it was added to the generic Dialog
      if (menuBar != null) {
         dialog.setJMenuBar(menuBar);
      }

      createPanel();

      dialog.pack();
      if (dialogLocation != null) {
         dialog.setLocation(dialogLocation);
      } else {
         dialog.setLocationRelativeTo(this.getParent());
      }

      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            cancelImpl();
         }
      });
      returnValue = JFileChooser.ERROR_OPTION;

      return dialog;
   }

   /**
    * Create the Yes Panel, which is part of the Yes / No panel.
    *
    * @return the Yes Panel
    */
   protected JPanel createOKPanel() {
      JPanel okPanel = new JPanel();
      okPanel.setLayout(new FlowLayout());
      yesAction = new AbstractAction(okLabel) {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doYes();
         }
      };
      okPanel.add(new JButton(yesAction));

      return okPanel;
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
      JPanel yesnopanel = new JPanel();
      yesnopanel.setLayout(new FlowLayout());
      yesAction = new AbstractAction(yesLabel) {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doYes();
         }
      };

      cancelAction = new AbstractAction(noLabel) {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doCancel();
         }
      };

      yesnopanel.add(new JButton(yesAction), BorderLayout.EAST);
      yesnopanel.add(new JButton(cancelAction), BorderLayout.WEST);

      return yesnopanel;
   }

   /**
    * Return the action associated with the default "Yes" button.
    *
    * @return the action associated with the default "Yes" button
    */
   public AbstractAction getYesAction() {
      return yesAction;
   }

   /**
    * Return the action associated with the default "No" button.
    *
    * @return the action associated with the default "No" button
    */
   public AbstractAction getCancelAction() {
      return cancelAction;
   }

   /**
    * The method to implement to create the main panel of the dialog. Note that you should create a Yes / No Panel here. For example, here is a
    * case where the sub-class will create a default panel with a text message, using the default Yes /No Panel:
    * <pre>
    * Container pane = dialog.getContentPane();
    * pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    *
    * pane.add(new JLabel("MyText");
    * pane.add(Box.createRigidArea(new Dimension(50, 20)));
    *
    * JPanel yesnopanel = this.createYesNoPanel();
    *
    * pane.add(yesnopanel);
    * </pre>
    */
   protected abstract void createPanel();

   /**
    * Show the dialog.
    *
    * @param parent the parent component
    * @return the JFileChooser return value.
    */
   public int showDialog(Component parent) {
      if (dialog == null) {
         createDialog(parent);
      }
      return showDialog();
   }

   /**
    * Show the dialog.
    *
    * @return the JFileChooser return value.
    */
   public int showDialog() {
      returnValue = JFileChooser.ERROR_OPTION;
      hasApply = false;
      if (dialog != null) {
         dialog.setVisible(true);
      } else {
         frame.setVisible(true);
      }

      return returnValue;
   }

   /**
    * Return the last value of the dialog return value.
    *
    * @return the last value of the dialog return value
    */
   public final int getReturnValue() {
      return returnValue;
   }

   /**
    * Called when applying the dialog. Do nothing by default.
    */
   protected void apply() {
   }

   /**
    * Called when cancelling the dialog. Do nothing by default.
    */
   protected void cancel() {
   }

   /**
    * Called when clicking the default "Yes" button.
    */
   protected void doYes() {
      returnValue = JFileChooser.APPROVE_OPTION;
      if (dialog != null) {
         dialog.setVisible(false);
         dialog.dispose();
      } else if (frame != null) {
         frame.dispose();
      }
      applyImpl();
   }

   private void applyImpl() {
      hasApply = true;
      apply();
      Iterator<DialogListener> it = listeners.iterator();
      while (it.hasNext()) {
         DialogListener listener = it.next();
         listener.apply(this);
      }
   }

   /**
    * Called when clicking the default "No" button.
    */
   protected void doCancel() {
      if (dialog != null) {
         dialog.dispose();
      } else if (frame != null) {
         frame.dispose();
      }
      cancelImpl();
   }

   private void cancelImpl() {
      if (!hasApply) {
         cancel();
         returnValue = JFileChooser.CANCEL_OPTION;
         Iterator<DialogListener> it = listeners.iterator();
         while (it.hasNext()) {
            DialogListener listener = it.next();
            listener.cancel(this);
         }
      }
   }

   /**
    * An interface for Dialog listener which will be fired if the dialog is applied (if clicking on "Yes") or cancelled
    * (if clicking on "Cancel" or closing the window without applying the dialog).
    *
    * @since 0.9.36
    */
   public static interface DialogListener {
      /**
       * Fired if a Dialog is applied.
       *
       * @param dialog the Dialog
       */
      public void apply(GenericDialog dialog);

      /**
       * Fired if a Dialog is cancelled.
       *
       * @param dialog the Dialog
       */
      public void cancel(GenericDialog dialog);
   }

   /**
    * A default implementation for a Dialog listener which will be fired if the dialog is applied (if clicking on "Yes") or cancelled
    * (if clicking on "Cancel" or closing the window without applying the dialog).
    *
    * @since 0.9.36
    */
   public static class DialogAdapter implements DialogListener {
      /**
       * Fired if a Dialog is applied. Do nothing by default.
       *
       * @param dialog the Dialog
       */
      @Override
      public void apply(GenericDialog dialog) {
      }

      /**
       * Fired if a Dialog is cancelled. Do nothing by default.
       *
       * @param dialog the Dialog
       */
      @Override
      public void cancel(GenericDialog dialog) {
      }
   }
}
