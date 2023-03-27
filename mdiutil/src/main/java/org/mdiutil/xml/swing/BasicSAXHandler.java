/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2008, 2009, 2011, 2012, 2016, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.mdiutil.xml.ResolverSAXHandler;
import org.xml.sax.SAXParseException;

/**
 * A default SAX handler class handles SAX error and resolution events, and is able to show a textArea
 * upon completion if some parsing errors have been detected.
 *
 * @version 1.0
 */
public class BasicSAXHandler extends ResolverSAXHandler {
   private JTextComponent areaComp = null;
   public static final int DEFAULT_ROWS = 10;
   public static final int DEFAULT_COLUMNS = 30;
   private int rows = DEFAULT_ROWS;
   private int columns = DEFAULT_COLUMNS;
   public static final int DEFAULT_SIZE = 3;
   public static final String DEFAULT_INFO_COLOR = "gray";
   private String infoHTMLColor = DEFAULT_INFO_COLOR;
   public static final String DEFAULT_WARNING_COLOR = null;
   private String warningHTMLColor = DEFAULT_WARNING_COLOR;
   public static final String DEFAULT_ERROR_COLOR = "red";
   private String errorHTMLColor = DEFAULT_ERROR_COLOR;
   private boolean filterInfos = false;

   /**
    * Create a new BasicSAXHandler with an existing list of results.
    *
    * @param results the existing list of results
    */
   public BasicSAXHandler(List<ExceptionResult> results) {
      super(results);
   }

   public BasicSAXHandler() {
      super();
   }

   /**
    * Set the html color for information messages.
    *
    * @param infoHTMLColor the html color for information messages
    */
   public void setInformationColor(String infoHTMLColor) {
      this.infoHTMLColor = infoHTMLColor;
   }

   /**
    * Set the html color for warnings.
    *
    * @param warningHTMLColor the html color for warnings
    */
   public void setWarningColor(String warningHTMLColor) {
      this.warningHTMLColor = warningHTMLColor;
   }

   /**
    * Set the html color for errors.
    *
    * @param errorHTMLColor the html color for errors
    */
   public void setErrorColor(String errorHTMLColor) {
      this.errorHTMLColor = errorHTMLColor;
   }

   /**
    * Set if the information messages have to be filtered.
    *
    * @param filterInfos true f the information messages have to be filtered
    */
   public void filterInfos(boolean filterInfos) {
      this.filterInfos = filterInfos;
   }

   /**
    * Set the dimension of the Exception dialog.
    *
    * @param rows the number of rows
    * @param columns the number of columns
    */
   public void setDimension(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
   }

   /**
    * Reset this handler.
    */
   @Override
   public void reset() {
      super.reset();
      areaComp = null;
   }

   /**
    * Returns a JScrollPane containing the list of errors encountered during parsing.
    *
    * @return the JScrollPane
    * @deprecated replaced by {@link #getScrollPane()}
    */
   public JScrollPane getLogTextArea() {
      getTextArea();
      return new JScrollPane(areaComp);
   }

   /**
    * Returns a JScrollPane containing the list of errors encountered during parsing.
    *
    * @return the JScrollPane
    */
   public JScrollPane getScrollPane() {
      getTextArea();
      return new JScrollPane(areaComp);
   }

   /**
    * Returns a JScrollPane containing the list of errors encountered during parsing. THis Text pane will present
    * the results with colors deending on the type of errors or warnings encountered.
    *
    * @return the JScrollPane
    */
   public JScrollPane getStyledScrollPane() {
      getErrorsAsTextPane();
      return new JScrollPane(areaComp);
   }

   /**
    * Return the result messages as a text pane. Note that information messages will also be added to the panel, unless
    * they has been set to be filtered (see {@link #filterInfos(boolean)}).
    * <p>
    * If you want not to have any resulting panel if
    * there are only informations, you will be able to do it by doing before getting the panel:
    * <pre>
    * if (handler.getStatus() >= BasicSAXHandler.WARNING) {
    *    JTextPane pane = handler.getErrorsAsTextPane()();
    * }
    * </pre>
    * </p>
    *
    * @return the text pane
    */
   public JTextPane getErrorsAsTextPane() {
      if ((areaComp == null) || (!(areaComp instanceof JTextPane))) {
         areaComp = constructExceptionPane();
      }
      return (JTextPane) areaComp;
   }

   /**
    * Return the result messages as a textArea. Note that information messages will also be added to the panel, unless
    * they has been set to be filtered (see {@link #filterInfos(boolean)}).
    * <p>
    * If you want not to have any resulting panel if
    * there are only informations, you will be able to do it by doing before getting the panel:
    * <pre>
    * if (handler.getStatus() >= BasicSAXHandler.WARNING) {
    *    JTextArea pane = handler.getTextArea()();
    * }
    * </pre>
    * </p>
    *
    * @return the text area
    */
   public JTextArea getTextArea() {
      if ((areaComp == null) || (!(areaComp instanceof JTextArea))) {
         areaComp = constructExceptionArea();
      }
      return (JTextArea) areaComp;
   }

   /**
    * Return the error messages as a String.
    *
    * @return the error messages as a String
    */
   public String getText() {
      if (areaComp == null) {
         areaComp = constructExceptionArea();
      }
      return areaComp.getText();
   }

   protected JTextArea constructExceptionArea() {
      JTextArea area = new JTextArea(rows, columns);

      // first sort the exceptions
      SortedMap<Integer, List<ExceptionResult>> sortedExceptions = getExceptionsResultsByLine();

      // then iterate through the results
      Iterator<List<ExceptionResult>> it = sortedExceptions.values().iterator();
      while (it.hasNext()) {
         List<ExceptionResult> list = it.next();
         Iterator<ExceptionResult> it2 = list.iterator();
         while (it2.hasNext()) {
            ExceptionResult result = it2.next();
            SAXParseException ex = result.getSAXParseException();
            int type = result.getExceptionType();
            if (type == WARNINGS) {
               area.append("WARNING: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n");
            } else if (type == ERRORS) {
               area.append("ERROR: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n");
            } else if (type == FATAL) {
               area.append("FATAL ERROR: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n");
            } else if ((type == INFO) && (result instanceof InformationResult) && (!filterInfos)) {
               InformationResult info = (InformationResult) result;
               if (info.isLocalized()) {
                  area.append("line " + info.getMessage() + "\n");
               } else {
                  area.append(info.getMessage() + "\n");
               }
            }
         }
      }
      return area;
   }

   protected JTextPane constructExceptionPane() {
      SizedTextPane area = new SizedTextPane(rows, columns);

      // first sort the exceptions
      SortedMap<Integer, List<ExceptionResult>> sortedExceptions = getExceptionsResultsByLine();

      // then iterate through the results
      Iterator<List<ExceptionResult>> it = sortedExceptions.values().iterator();
      while (it.hasNext()) {
         List<ExceptionResult> list = it.next();
         Iterator<ExceptionResult> it2 = list.iterator();
         while (it2.hasNext()) {
            ExceptionResult result = it2.next();
            SAXParseException ex = result.getSAXParseException();
            int type = result.getExceptionType();
            if (type == WARNINGS) {
               if (this.warningHTMLColor == null) {
                  area.append("WARNING: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n");
               } else {
                  area.append("WARNING: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n", warningHTMLColor);
               }
            } else if (type == ERRORS) {
               area.append("ERROR: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n", errorHTMLColor);
            } else if (type == FATAL) {
               area.append("FATAL ERROR: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n", "red");
            } else if ((type == INFO) && (result instanceof InformationResult) && (!filterInfos)) {
               InformationResult info = (InformationResult) result;
               if (info.isLocalized()) {
                  area.append("line " + info.getMessage() + "\n", infoHTMLColor);
               } else {
                  area.append(info.getMessage() + "\n", infoHTMLColor);
               }
            } else {
               area.append("Undefined: line " + ex.getLineNumber() + " : " + ex.getMessage() + "\n");
            }
         }
      }
      return area;
   }

   /**
    * A JTextPane whith a specified preferred scrollable size.
    *
    * @since 0.7
    */
   private class SizedTextPane extends JTextPane {
      private Dimension d;
      private int rows = 1;
      private int columns = 1;
      private HTMLDocument doc;
      private HTMLEditorKit kit = new HTMLEditorKit();
      private String fontFace = null;
      private int fontSize = DEFAULT_SIZE;

      /**
       * Create a new SizedTextPane.
       *
       * @param rows the number of rows of the area
       * @param columns the number of columns of the area
       */
      public SizedTextPane(int rows, int columns) {
         super();
         this.rows = rows;
         this.columns = columns;

         setSize();
         createDocument();
      }

      private void setSize() {
         JTextArea area = new JTextArea(rows, columns);
         Font font = area.getFont();
         fontFace = font.getFamily();
         d = area.getPreferredSize();
      }

      private void createDocument() {
         this.setEditorKit(kit);
         doc = (HTMLDocument) kit.createDefaultDocument();
         this.setDocument(doc);
         this.setEditable(false);
      }

      public void append(String text) {
         appendImpl("<html><font face=\"" + fontFace + "\" size=\"" + fontSize + "\">" + text + "</font></html>\n");
      }

      public void append(String text, String htmlColor) {
         appendImpl("<html><font face=\"" + fontFace + "\" size=\"" + fontSize + "\" color=" + htmlColor + "\">" + text + "</font></html>\n");
      }

      private void appendImpl(String text) {
         try {
            Reader r = new StringReader(text);
            kit.read(r, doc, doc.getLength());
            this.setCaretPosition(doc.getLength());
         } catch (BadLocationException | IOException e) {
         }
      }

      public void setPreferredScrollableViewportSize(Dimension dim) {
         d = dim;
      }

      @Override
      public Dimension getPreferredScrollableViewportSize() {
         return d;
      }
   }
}
