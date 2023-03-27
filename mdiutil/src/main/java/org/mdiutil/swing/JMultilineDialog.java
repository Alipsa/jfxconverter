/*------------------------------------------------------------------------------
 * Copyright (C) 2017, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

/**
 * A default MultilineDialog.
 *
 * @version 0.9.59
 */
public class JMultilineDialog extends AbstractMultilineDialog {
   /**
    * The Yes / Cancel Panel.
    */
   protected JPanel yesnopanel;

   /**
    * Constructor.
    *
    * @param textComp the text component
    */
   public JMultilineDialog(JTextComponent textComp) {
      super(textComp);
   }

   /**
    * Set up the panel contents.
    */
   @Override
   protected void setUp() {
      // yes/no panel
      yesnopanel = createYesNoPanel();

      // layout of all panels
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      if (dim != null) {
         textComp.setPreferredSize(dim);
      }
      this.add(new JScrollPane(textComp));
      this.add(Box.createVerticalGlue());
      this.add(yesnopanel);
   }
}
