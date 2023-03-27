/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Presents a scrollable logging text area for messages.
 *
 * @version 0.9.6
 */
public class HTMLLogger extends JPanel {
   /**
    * The default height of the Message Area.
    */
   public static final int DEFAULT_ROWS = 6;
   private StylableSizableArea area;

   public HTMLLogger() {
      super();
      initializeHTMLContent();
      this.setLayout(new BorderLayout());
      this.add(new JScrollPane(area), BorderLayout.CENTER);
   }

   /**
    * Return the underlying text area.
    *
    * @return the underlying text area
    */
   public StylableSizableArea getTextArea() {
      return area;
   }

   private void initializeHTMLContent() {
      area = new StylableSizableArea(40, 80);
      area.initDocument();
   }

   /**
    * Reset the position of the caret to the beginning of the text.
    */
   public void resetPosition() {
      area.setCaretPosition(0);
   }

   /**
    * Append a line of text in the message area, with a default color.
    *
    * @param txt the text
    */
   public void append(String txt) {
      if (txt != null) {
         area.appendText(txt);
      }
   }

   /**
    * Append a line of text in the message area, with a specific color.
    *
    * @param txt the text
    * @param htmlColor the html color
    */
   public void append(String txt, String htmlColor) {
      append("<font color=" + htmlColor + ">" + txt + "</font>");
   }

   /**
    * Append a line of text in the message area, colored in red (for errors).
    *
    * @param txt the text
    */
   public void appendError(String txt) {
      if (txt != null) {
         append(txt, "red");
      }
   }
}
