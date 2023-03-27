/*------------------------------------------------------------------------------
* Copyright (C) 2006, 2016, 2019, 2020 Herve Girod
*
* Distributable under the terms of either the Apache License (Version 2.0) or
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Writes a Node (or XML Document) as text output. This is -not- a full XML printout implementation.
 * However, this class can process correctly text (in ATTR or TEXT nodes) which is not plain ASCII.
 *
 * For example:
 * <ul>
 * <li>Unicode characters outside of ASCII values are corresly escaped</li>
 * <li>Quoted attributes are correctly handled</li>
 * </ul>
 *
 * @version 1.2.7.7
 */
public class XmlWriter {
   private static String EOL;
   private static final String TAG_END = " />";
   private static final String TAG_START = "</";
   private static final String SPACE = " ";

   // error constants
   private static final String ERR_PROXY = "proxy should not be null";
   private static final String INVALID_NODE = "Unable to write node of type ";

   static {
      String temp;
      try {
         temp = System.getProperty("line.separator", "\n");
      } catch (SecurityException e) {
         temp = "\n";
      }
      EOL = temp;
   }

   protected boolean escapeHex = true;
   protected String publicID = null;
   protected String systemID = null;
   protected String doctype;
   private IndentWriter out;

   class IndentWriter extends Writer {
      private Writer proxied;
      private int indentLevel;

      public IndentWriter(Writer proxied) {
         if (proxied == null) {
            throw new XmlWriterRuntimeException(ERR_PROXY);
         }

         this.proxied = proxied;
      }

      public void setIndentLevel(int indentLevel) {
         this.indentLevel = indentLevel;
      }

      public int getIndentLevel() {
         return indentLevel;
      }

      public void printIndent() throws IOException {
         proxied.write(EOL);
         int temp = indentLevel;
         while (temp > 0) {
            proxied.write(' ');
            temp--;
         }
      }

      public Writer getProxied() {
         return proxied;
      }

      @Override
      public void write(int c) throws IOException {
         proxied.write(c);
      }

      @Override
      public void write(char cbuf[]) throws IOException {
         proxied.write(cbuf);
      }

      @Override
      public void write(char cbuf[], int off, int len) throws IOException {
         proxied.write(cbuf, off, len);
      }

      @Override
      public void write(String str) throws IOException {
         proxied.write(str);
      }

      @Override
      public void write(String str, int off, int len) throws IOException {
         proxied.write(str, off, len);
      }

      @Override
      public void flush() throws IOException {
         proxied.flush();
      }

      @Override
      public void close() throws IOException {
         proxied.close();
      }
   }

   public XmlWriter() {
   }

   /**
    * Create a new XmlWriter, capable to write a XML DOM document to a file., with escaped hexadecimal sequences.
    *
    * @param doctype the document type
    * @param publicID the publicID of the document
    * @param systemID the systemID of the document
    */
   public XmlWriter(String doctype, String publicID, String systemID) {
      this.doctype = doctype;
      this.publicID = publicID;
      this.systemID = systemID;
   }

   /**
    * Create a new XmlWriter, capable to write a XML DOM document to a file.
    *
    * @param doctype the document type
    * @param publicID the publicID of the document
    * @param systemID the systemID of the document
    * @param escapeHex defines if non ASCII characters are writen as UTF-8 chars (false)
    * or escaped XML hexadecimal sequences (true)
    * @see #setPublicSystemID(String, String, String) for rules for giving these parameters
    */
   public XmlWriter(String doctype, String publicID, String systemID, boolean escapeHex) {
      this(doctype, publicID, systemID);
      this.escapeHex = escapeHex;
   }

   /**
    * Set the public and system ID of the written XML Document.
    *
    * You must be aware that XML constructs force you to have a systemID literal if
    * you provide a publicID identifier, or the document header will not be valid.
    *
    * @param doctype the doctype
    * @param publicID the publicID
    * @param systemID the systemID
    */
   public void setPublicSystemID(String doctype, String publicID, String systemID) {
      this.doctype = doctype;
      this.publicID = publicID;
      this.systemID = systemID;
   }

   /**
    * Return the publicID.
    *
    * @return the publicID
    */
   public String getPublicID() {
      return publicID;
   }

   /**
    * Return the doctype.
    *
    * @return the doctype
    */
   public String getDoctype() {
      return doctype;
   }

   /**
    * Return the systemID.
    *
    * @return the systemID
    */
   public String getSystemID() {
      return systemID;
   }

   public void flush() throws IOException {
      out.flush();
   }

   private void writeXml(Attr attr) throws IOException {
      String name = attr.getName();
      out.write(name);
      out.write("=\"");
      writeChildrenXml(attr);
      out.write('"');
   }

   /**
    * Writes the attribute's value.
    * FIXED : managing of Unicode characters > 7F
    * FIXED : writing of attrs descriptors which are enclosed in " ' " characters)
    */
   private void writeChildrenXml(Attr attr) throws IOException {
      String value = attr.getValue();
      /* if the attribute value is enclosed in quotes, we will not transformed these quotes to
       * &apos; elements, but we will keep them like that
       */
      boolean quoted = false;
      if ((value.length() > 2)
         && (value.charAt(0) == '\'') && (value.charAt(value.length() - 1) == '\'')) {
         quoted = true;
         out.write('\'');
         value = value.substring(1, value.length() - 1);
      }
      // ensure that each remaining characters are correctly written
      for (int i = 0; i < value.length(); i++) {
         int c = value.charAt(i);
         switch (c) {
            case '<':
               out.write("&lt;");
               continue;
            case '>':
               out.write("&gt;");
               continue;
            case '&':
               out.write("&amp;");
               continue;
            case '\'':
               out.write("&apos;");
               continue;
            case '"':
               out.write("&quot;");
               continue;
            case '\r':
            case '\n':
               out.write("&#" + Integer.toString(c) + ";");
               continue;
            default: {
               if (escapeHex && (c > 0x007F)) {
                  String hex = "0000" + Integer.toHexString(c);
                  out.write("&#x" + hex.substring(hex.length() - 4) + ";");
               } else {
                  out.write(c);
               }
               continue;
            }
         }
      }
      // then if the attribute was quoted, add the end quote...
      if (quoted) {
         out.write('\'');
      }
   }

   /**
    * Writes out the comment. Note that spaces may be added to
    * prevent illegal comments: between consecutive dashes ("--")
    * or if the last character of the comment is a dash.
    */
   private void writeXml(Comment comment)
      throws IOException {
      char data[] = comment.getData().toCharArray();
      out.write("<!--");
      if (data != null) {
         boolean sawDash = false;
         int length = data.length;

         // "--" illegal in comments, expand it
         for (int i = 0; i < length; i++) {
            if (data[i] == '-') {
               if (sawDash) {
                  out.write(' ');
               } else {
                  sawDash = true;
                  out.write('-');
                  continue;
               }
            }
            sawDash = false;
            out.write(data[i]);
         }
         if (data[data.length - 1] == '-') {
            out.write(' ');
         }
      }
      out.write("-->");
   }

   /**
    * Writes the Text value.
    * FIXED : managing of Unicode characters > 7F
    */
   private void writeXml(Text text) throws IOException {
      char data[] = text.getData().toCharArray();
      int start = 0, last = 0;

      // saw this once -- being paranoid
      if (data == null) {
         System.err.println("Null text data??");
         return;
      }

      while (last < data.length) {
         char c = data[last];

         switch (c) {
            case '<':
               out.write("&lt;");
            case '>':
               out.write("&gt;");
            case '&':
               out.write("&amp;");
            case '\'':
               out.write("&apos;");
            case '"':
               out.write("&quot;");
            case '\r':
            case '\n':
               out.write("&#" + Integer.toString(c) + ";");
            default: {
               if (escapeHex && (c > 0x007F)) {
                  String hex = "0000" + Integer.toHexString(c);
                  out.write("&#x" + hex.substring(hex.length() - 4) + ";");
               } else {
                  out.write(c);
               }
            }
         }
         last++;
      }
   }

   private void writeXml(CDATASection cdataSection) throws IOException {
      char[] data = cdataSection.getData().toCharArray();
      out.write("<![CDATA[");
      for (int i = 0; i < data.length; i++) {
         char c = data[i];

         // embedded "]]>" needs to be split into adjacent
         // CDATA blocks ... can be split at either point
         if (c == ']') {
            if ((i + 2) < data.length
               && data[i + 1] == ']'
               && data[i + 2] == '>') {
               out.write("]]]]><![CDATA[>");
               continue;
            }
         }
         out.write(c);
      }
      out.write("]]>");
   }

   private void writeXml(Element element) throws IOException, XmlWriterIOException {
      out.write(TAG_START, 0, 1);    // "<"
      out.write(element.getTagName());

      NamedNodeMap attributes = element.getAttributes();
      if (attributes != null) {
         int nAttr = attributes.getLength();
         for (int i = 0; i < nAttr; i++) {
            Attr attr = (Attr) attributes.item(i);
            out.write(' ');
            writeXml(attr);
         }
      }

      //
      // Write empty nodes as "<EMPTY />" to make sure version 3
      // and 4 web browsers can read empty tag output as HTML.
      // XML allows "<EMPTY/>" too, of course.
      //
      if (!element.hasChildNodes()) {
         out.write(TAG_END, 0, 3);   // " />"
      } else {
         out.write(TAG_END, 2, 1);   // ">"
         writeChildrenXml(element);
         out.write(TAG_START, 0, 2);        // "</"
         out.write(element.getTagName());
         out.write(TAG_END, 2, 1);  // ">"
      }
   }

   private void writeChildrenXml(Element element) throws IOException, XmlWriterIOException {
      NodeList children = element.getChildNodes();
      if (children == null) {
         return;
      }

      int length = children.getLength();
      int oldIndent = 0;
      boolean preserve = true;
      boolean pureText = true;

      oldIndent = out.getIndentLevel();
      try {
         out.setIndentLevel(oldIndent + 2);
         for (int i = 0; i < length; i++) {
            if (children.item(i).getNodeType() != Node.TEXT_NODE) {
               out.printIndent();
               pureText = false;
            }
            writeXml(children.item(i), out);
         }
      } finally {
         out.setIndentLevel(oldIndent);
         if (length > 0 && children.item(length - 1).getNodeType() != Node.TEXT_NODE) {
            out.printIndent();          // for ETag
         }
      }
   }

   private void writeDocumentHeader() throws IOException {
      String encoding = null;

      if (out.getProxied() instanceof OutputStreamWriter) {
         encoding = java2std(((OutputStreamWriter) out.getProxied()).getEncoding());
      }

      out.write("<?xml version=\"1.0\"");
      if (encoding != null) {
         out.write(" encoding=\"");
         out.write(encoding);
         out.write('\"');
      }
      out.write("?>");
      out.write(EOL);

      // Write DOCTYPE declaration here.
      if ((publicID != null) || (systemID != null)) {
         StringBuilder s = new StringBuilder("<!DOCTYPE " + doctype);
         // if there is a PUBLIC id, then we must also have the corresponding system litteral
         if (publicID != null) {
            if (systemID == null) {
               throw new IOException("you must also have a system litteral with a publid ID");
            } else {
               s = s.append(" PUBLIC '").append(publicID).append("' '").append(systemID).append("'");
            }
         } else if (systemID != null) {
            s = s.append(" SYSTEM '").append(systemID).append("'");
         }
         s.append(">");
         out.write(s.toString());
         out.write(EOL);
      }
   }

   private void writeXml(Document document) throws IOException, XmlWriterIOException {
      writeDocumentHeader();
      NodeList childList = document.getChildNodes();
      writeXml(childList);
   }

   public void writeDocument(Document doc, Writer writer) throws IOException, XmlWriterIOException {
      try {
         out = null;
         if (writer instanceof IndentWriter) {
            out = (IndentWriter) writer;
         } else {
            out = new IndentWriter(writer);
         }
         if (doc.getDoctype() != null) {
            DocumentType type = doc.getDoctype();
            this.doctype = type.getName();
            this.publicID = type.getPublicId();
            this.systemID = type.getSystemId();
         }
         writeXml(doc);
         out.flush();
      } catch (IOException io) {
         throw new XmlWriterIOException(io);
      }
   }

   private void writeXml(NodeList childList)
      throws IOException, XmlWriterIOException {
      int length = childList.getLength();

      if (length == 0) {
         return;
      }
      for (int i = 0; i < length; i++) {
         Node child = childList.item(i);
         doWriteXmlNode(child);
         out.write(EOL);
      }
   }

   private String java2std(String encodingName) {
      if (encodingName == null) {
         return null;
      }

      //
      // ISO-8859-N is a common family of 8 bit encodings;
      // N=1 is the eight bit subset of UNICODE, and there
      // seem to be at least drafts for some N >10.
      //
      // JDK 1.2
      if (encodingName.startsWith("ISO8859_")) {
         return "ISO-8859-" + encodingName.substring(8);
      }
      // JDK 1.1
      if (encodingName.startsWith("8859_")) {
         return "ISO-8859-" + encodingName.substring(5);
      }

      // XXX seven bit encodings ISO-2022-* ...
      // XXX EBCDIC encodings ...
      if ("ASCII7".equalsIgnoreCase(encodingName) || "ASCII".equalsIgnoreCase(encodingName)) {
         return "US-ASCII";
      }

      //
      // All XML parsers _must_ support UTF-8 and UTF-16.
      if ("UTF8".equalsIgnoreCase(encodingName)) {
         return "UTF-8";
      }
      if (encodingName.startsWith("Unicode")) {
         return "UTF-16";
      }

      //
      // Some common Japanese character sets.
      //
      if ("SJIS".equalsIgnoreCase(encodingName)) {
         return "Shift_JIS";
      }
      if ("JIS".equalsIgnoreCase(encodingName)) {
         return "ISO-2022-JP";
      }
      if ("EUCJIS".equalsIgnoreCase(encodingName)) {
         return "EUC-JP";
      }

      // else we force to UTF-8
      return "UTF-8";
   }

   private void doWriteXmlNode(Node node) throws IOException {
      switch (node.getNodeType()) {
         case Node.ATTRIBUTE_NODE:
            writeXml((Attr) node);
            break;
         case Node.COMMENT_NODE:
            writeXml((Comment) node);
            break;
         case Node.TEXT_NODE:
            writeXml((Text) node);
            break;
         case Node.CDATA_SECTION_NODE:
            writeXml((CDATASection) node);
            break;
         case Node.DOCUMENT_NODE:
            writeXml((Document) node);
            break;
         case Node.DOCUMENT_FRAGMENT_NODE:
            writeDocumentHeader();
            NodeList childList = node.getChildNodes();
            writeXml(childList);
            break;
         case Node.ELEMENT_NODE:
            writeXml((Element) node);
            break;
         case Node.DOCUMENT_TYPE_NODE:
            break;
         default:
            throw new XmlWriterRuntimeException(INVALID_NODE
               + node.getClass().
                  getName());
      }
   }

   public void writeXml(Node node, Writer writer) throws XmlWriterIOException {
      try {
         out = null;
         if (writer instanceof IndentWriter) {
            out = (IndentWriter) writer;
         } else {
            out = new IndentWriter(writer);
         }

         doWriteXmlNode(node);
      } catch (IOException io) {
         throw new XmlWriterIOException(io);
      }
   }
}
