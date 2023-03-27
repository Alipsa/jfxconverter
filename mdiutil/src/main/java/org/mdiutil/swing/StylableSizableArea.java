/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016, 2018 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * A stylable text area whose preferred scrollable size can be forced to any value.
 *
 * @version 0.9.44
 */
public class StylableSizableArea extends JTextPane {
   public static final int DEFAULT_MAXIMUM_LINES = 500;
   private Dimension d;
   private int rows = 1;
   private int columns = 1;
   private HTMLDocument doc;
   private final HTMLEditorKit kit = new HTMLEditorKit();
   private final List<Integer> lines = new ArrayList<>();
   private int maximumLines = -1;
   private static final String NEWLINE = System.getProperty("line.separator");

   /**
    * Create a new SizableArea.
    *
    * @param rows the number of rows of the area
    */
   public StylableSizableArea(int rows) {
      super();
      this.rows = rows;
      setMySize();
      createDocument();
   }

   /**
    * Constructor.
    *
    * @param rows the number of rows of the area
    * @param font the Font used to set the text of the area
    */
   public StylableSizableArea(int rows, Font font) {
      super();
      this.rows = rows;
      this.setFont(font);
      JTextArea area = new JTextArea(rows, 1);

      setMySize();
      createDocument();
      area.setFont(font);
   }

   /**
    * Constructor.
    *
    * @param columns the number of columns of the area
    * @param rows the number of rows of the area
    */
   public StylableSizableArea(int rows, int columns) {
      super();
      this.rows = rows;
      this.columns = columns;
      JTextArea area = new JTextArea(rows, columns);

      setMySize();
      createDocument();
      area.setFont(getFont());
   }

   /**
    * Constructor.
    *
    * @param columns the number of columns of the area
    * @param rows the number of rows of the area
    * @param font the Font used to set the text of the area
    */
   public StylableSizableArea(int columns, int rows, Font font) {
      super();
      this.rows = rows;
      this.columns = columns;
      this.setFont(font);
      setMySize();
      createDocument();
      this.setFont(getFont());
   }

   private void setMySize() {
      JTextArea area = new JTextArea(rows, columns);
      this.d = area.getPreferredSize();
   }

   private void createDocument() {
      this.setEditorKit(kit);
      doc = (HTMLDocument) kit.createDefaultDocument();
      this.setDocument(doc);
      this.setEditable(false);
   }

   /**
    * Set the maximum number of lines to be shown in the panel. If the number of lines exceed this number, the
    * first lines will disappear, as in a FIFO stack.
    *
    * @param maximumLines the maximum number of lines
    */
   public void setMaximumLines(int maximumLines) {
      this.maximumLines = maximumLines;
      if ((maximumLines != -1) && (lines.size() > maximumLines)) {
         removeOverhead(lines.size() - maximumLines);
      }
   }

   /**
    * Set if the maximum lines will be shown in the panel or not.
    *
    * @param hasMaximumLines true if the maximum lines will be shown in the panel or not
    */
   public void setMaximumLinesBehavior(boolean hasMaximumLines) {
      if (hasMaximumLines) {
         this.maximumLines = DEFAULT_MAXIMUM_LINES;
      } else {
         this.maximumLines = -1;
      }
   }

   /**
    * Return the maximum lines to be shown, or -1 if there is not limitation.
    *
    * @return the maximum lines to be shown
    */
   public int getMaximumLines() {
      return maximumLines;
   }

   /**
    * Return true if there is a number of lines limitation.
    *
    * @return true if there is a number of lines limitation
    */
   public boolean hasMaximumLines() {
      return maximumLines != -1;
   }

   public void initDocument() {
      lines.clear();
      createDocument();
   }

   /**
    * Remove the first lines of the text if there is more lines than allowed.
    */
   private void removeOverhead(int numberToRemove) {
      try {
         int offset = 0;
         for (int i = 0; i < numberToRemove; i++) {
            if (lines.size() > i + 1) {
               offset += lines.get(i);
            }
         }
         doc.remove(0, offset);

         // this is bad, but sometimes the first line of the document
         // would begin with a newLine. Do this in order not to begin
         // with a blanck line after a removal
         if (doc.getText(0, 1).equals("\n")) {
            doc.remove(0, 1);
         }
         for (int i = 0; i < numberToRemove; i++) {
            if (!lines.isEmpty()) {
               lines.remove(0);
            }
         }
      } catch (BadLocationException e) {
      }
   }

   /**
    * Append a text to the area, with a specific html Color.
    *
    * @param text the text to append
    * @param htmlColor the color of the text
    */
   public void appendText(String text, String htmlColor) {
      appendText("<html><font color=" + htmlColor + ">" + text + "</font>\n");
   }

   /**
    * Append a text to the area, whith a default Color.
    *
    * @param text the text
    */
   public synchronized void appendText(String text) {
      try {
         StringBuilder textToAppend = new StringBuilder();
         // check if we must delete the first lines of the text
         boolean hasMaximum = maximumLines != -1;
         StringTokenizer tk = new StringTokenizer(text, "\n");
         int offset = 0;
         int numberOfLines = tk.countTokens();

         if (hasMaximum && (lines.size() + numberOfLines > maximumLines)) {
            removeOverhead(lines.size() + numberOfLines - maximumLines);
         }
         while (tk.hasMoreTokens()) {
            String s = tk.nextToken();

            if (offset != 0) {
               lines.add(offset);
            }
            offset = s.length() + 1;
            textToAppend.append(s);
            if (tk.hasMoreTokens()) {
               textToAppend.append("\n<br/>");
            }
         }
         if (offset != 0) {
            lines.add(offset);
         }

         Reader r = new StringReader(textToAppend.toString());

         kit.read(r, doc, doc.getLength());
         this.setCaretPosition(doc.getLength());
      } catch (BadLocationException | IOException e) {
      }
   }

   /**
    * This method allows to set the preferred viewport scrolling size.
    *
    * @param dim the preferred size of the viewport
    */
   public void setPreferredScrollableViewportSize(Dimension dim) {
      this.d = dim;
   }

   /**
    * Returns the preferred size of the viewport for a view component.
    *
    * @return the preferred size of the viewport
    */
   @Override
   public Dimension getPreferredScrollableViewportSize() {
      return d;
   }
}
