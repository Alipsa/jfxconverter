/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2019, 2021, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mdiutil.xml.ResolverSAXHandler;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Escape String with HTML escape sequences to be usable in XML content. Note that this class works in a multi-thread environment.
 *
 * @version 1.2.40
 */
public class HTMLEscaper {
   private static final ThreadLocal<HTMLEscaper> THREAD_LOCAL = ThreadLocal.withInitial(() -> new HTMLEscaper());
   private volatile Map<Integer, Map<Character, Object>> escapingTree = null;
   private volatile Map<Character, String> invEscaping = null;
   private static final Object SYNC = new Object();

   private HTMLEscaper() {
      synchronized (SYNC) {
         if (escapingTree == null) {
            escapingTree = new ConcurrentHashMap<>();
            invEscaping = new ConcurrentHashMap<>();
            XMLSAXParser parser = new XMLSAXParser();
            parser.setValidating(false);
            parser.setHandler(new EntitiesHandler());
            URL xml = HTMLEscaper.class.getResource("htmlEntities.xml");
            parser.parse(xml);
         }
      }
   }

   private void addHTMLText(String html, String unicode) {
      if (!html.startsWith("&") || !html.endsWith(";")) {
         return;
      }
      html = html.substring(1, html.length() - 1);
      int length = html.length();
      Map<Character, Object> subtree;
      if (escapingTree.containsKey(length)) {
         subtree = escapingTree.get(length);
      } else {
         subtree = new HashMap<>();
         escapingTree.put(length, subtree);
      }
      for (int i = 0; i < length; i++) {
         Character c = html.charAt(i);
         if (i == length - 1) {
            subtree.put(c, unicode);
         } else if (!subtree.containsKey(c)) {
            Map<Character, Object> childtree = new HashMap<>();
            subtree.put(c, childtree);
            subtree = childtree;
         } else {
            Object o = subtree.get(c);
            if (o instanceof Map) {
               subtree = (Map<Character, Object>) o;
            }
         }
      }
   }

   private void addInverseHTMLText(String html, String unicode) {
      char hexID = (char) Integer.parseInt(unicode, 16);
      invEscaping.put(hexID, html);
   }

   /**
    * Convert a text with HTML entities to be usable in XML. HTML entities are converted to the associated XML content.
    * The special XML characters (&amp;, &gt; and &lt) will not be escaped.
    *
    * For example:
    * <ul>
    * <li><code>The &eacute; or &egrave;</code> text will be converted to <code>The &amp;#x00E9; or &amp;#x00E8;</code> text</li>
    * </ul>
    *
    * @param text the text
    * @return the text usable in XML
    */
   public static String escapeToXML(String text) {
      return escapeToXML(text, false);
   }

   /**
    * Convert a text with HTML entities to be usable in XML. HTML entities are converted to the associated XML content.
    *
    * For example:
    * <ul>
    * <li><code>The &eacute; or &egrave;</code> text will be converted to <code>The &amp;#x00E9; or &amp;#x00E8;</code> text</li>
    * </ul>
    *
    * @param text the text
    * @param escapeSpecial true if the special XML characters (&amp;, &gt; and &lt) must be escaped
    * @return the text usable in XML
    */
   public static String escapeToXML(String text, boolean escapeSpecial) {
      return escapeToXML(text, escapeSpecial, true);
   }

   /**
    * Convert a text with HTML entities to be usable in XML. HTML entities are converted to the associated XML content.
    *
    * For example (if <code>toXMLEscape</code> is true):
    * <ul>
    * <li><code>The &eacute; or &egrave;</code> text will be converted to <code>The &amp;#x00E9; or &amp;#x00E8;</code> text</li>
    * </ul>
    *
    * @param text the text
    * @param escapeSpecial true if the special XML characters (&amp;, &gt; and &lt) must be escaped
    * @param toXMLEscape true if non ASCII characters must be escaped with XML escape sequences
    * @return the text usable in XML
    */
   public static String escapeToXML(String text, boolean escapeSpecial, boolean toXMLEscape) {
      return THREAD_LOCAL.get().escapeImpl(text, escapeSpecial, toXMLEscape);
   }

   /**
    * Convert a text with XML escaped characters to non-escaped text.
    *
    * @param text the text with XML escaped characters
    * @return the unescaped text
    */
   public static String escapeXMLToText(String text) {
      return THREAD_LOCAL.get().escapeXMLToTextImpl(text);
   }
   
   /**
    * Escape a text to be usable in XML CDATA.
    *
    * @param text the text
    * @return the escaped text
    */
   public static String escapeXMLCDATA(String text) {
      return THREAD_LOCAL.get().escapeXMLCDATAImpl(text);
   }   
   
   /**
    * Escape a text to be usable in XML. XML entites are converted to the their escaped values.
    *
    * @param text the text
    * @return the escaped text
    */
   private String escapeXMLCDATAImpl(String text) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
          String s = text.substring(i, i +1);
          switch (s) {
              case "<":
                  buf.append("&lt;");
                  break;
              case ">":
                  buf.append("&gt;");
                  break;                  
              case "&":
                  buf.append("&amp;");
                  break;                        
              default:
                  buf.append(s);
                  break;
          }
      }     
      return buf.toString();
   }   
   
   /**
    * Escape a text to be usable in XML attributes.
    *
    * @param text the text
    * @return the escaped text
    */
   public static String escapeXMLAttribute(String text) {
      return THREAD_LOCAL.get().escapeXMLAttrImpl(text);
   }      
   
   /**
    * Escape a text to be usable in XML. XML entites are converted to the their escaped values.
    *
    * @param text the text
    * @return the escaped text
    */
   private String escapeXMLAttrImpl(String text) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
          String s = text.substring(i, i +1);
          switch (s) {
              case "<":
                  buf.append("&lt;");
                  break;     
              case ">":
                  buf.append("&gt;");
                  break;                    
              case "&":
                  buf.append("&amp;");
                  break;   
              case "'":
                  buf.append("&#39;");
                  break;      
              case "\"":
                  buf.append("&#34;");
                  break;                      
              default:
                  buf.append(s);
                  break;
          }
      }     
      return buf.toString();
   }      

   /**
    * Convert a text by converting XML escaped characters to corresponding HTML entities.
    *
    * For example:
    * <ul>
    * <li><code>The \u00E9; or \u00E8;</code> text will be converted to <code>The &eacute; or &egrave;</code> text</li>
    * </ul>
    *
    * @param text the text
    * @return the text with HTML entities
    */
   public static String escapeToHTML(String text) {
      return THREAD_LOCAL.get().escapeHTMLImpl(text);
   }

   private boolean isSpecialXMLCharacter(String maybeWord) {
      return maybeWord.equals("lt") || maybeWord.equals("gt") || maybeWord.equals("amp");
   }

   /**
    * Convert a text with HTML entities to be usable in XML. HTML entites are converted to the associated XML content.
    *
    * For example (if <code>toXMLEscape</code> is true):
    * <ul>
    * <li><code>The &eacute; or &egrave; text</code> will be converted to <code>The &amp;#x00E9; or &amp;#x00E8; text</code></li>
    * </ul>
    *
    * @param text the text
    * @param escapeSpecial true if the special XML characters (&amp;, &gt; and &lt) must be escaped
    * @param toXMLEscape true if non ASCII characters must be escaped with XML escape sequences
    * @return the text usable in XML
    */
   private String escapeImpl(String text, boolean escapeComp, boolean toXMLEscape) {
      StringBuilder buf = new StringBuilder();
      int offset = 0;
      int length = text.length();
      while (true) {
         int indexAmp = text.indexOf('&', offset);
         if (indexAmp == -1) {
            buf.append(text.substring(offset));
            break;
         } else {
            int indexSemi = text.indexOf(';', indexAmp);
            if (indexSemi == -1) {
               buf.append(text.substring(offset));
               break;
            } else {
               String maybeWord = text.substring(indexAmp + 1, indexSemi);
               int wordLength = maybeWord.length();
               if (escapingTree.containsKey(wordLength)) {
                  Map<Character, Object> subtree = escapingTree.get(wordLength);
                  for (int i = 0; i < maybeWord.length(); i++) {
                     Character c = maybeWord.charAt(i);
                     if (subtree.containsKey(c)) {
                        Object o = subtree.get(c);
                        if (o instanceof Map) {
                           subtree = (Map<Character, Object>) o;
                        } else {
                           String unicode = (String) o;
                           if (indexAmp > 0) {
                              buf.append(text.substring(offset, indexAmp));
                           }
                           if (escapeComp || !isSpecialXMLCharacter(maybeWord)) {
                              if (toXMLEscape) {
                                 buf.append(unicode);
                              } else {
                                 unicode = unicode.substring(7, unicode.length() - 1);
                                 int unicodeI = Integer.parseInt(unicode, 16);
                                 char[] chars = Character.toChars(unicodeI);
                                 String s = new String(chars);
                                 buf.append(s);
                              }
                           } else {
                              buf.append("&").append(maybeWord).append(";");
                           }
                           offset = indexSemi + 1;
                           break;
                        }
                     } else {
                        buf.append(text.substring(offset, indexSemi + 1));
                        offset = indexSemi + 1;
                        break;
                     }
                  }
               } else {
                  buf.append(text.substring(offset));
                  break;
               }
            }
            if (offset >= length) {
               break;
            }
         }
      }
      return buf.toString();
   }

   /**
    * Convert a text by converting XML escaped characters to corresponding HTML entities.
    *
    * For example:
    * <ul>
    * <li><code>The \u00E9; or \u00E8; text</code> will be converted to <code>The &eacute; or &egrave; text</code></li>
    * </ul>
    *
    * @param text the text
    * @return the text with HTML entities
    */
   private String escapeHTMLImpl(String text) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (invEscaping.containsKey(c)) {
            if (c == '&' && text.length() > i + 2) {
               String cont = text.substring(i + 1);
               if (cont.startsWith("lt") || cont.startsWith("gt") || cont.startsWith("amp")) {
                  buf.append(c);
                  continue;
               }
            }
            String html = invEscaping.get(c);
            buf.append(html);
         } else {
            buf.append(c);
         }
      }
      return buf.toString();
   }

   private String escapeXMLToTextImpl(String text) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (invEscaping.containsKey(c)) {
            String html = invEscaping.get(c);
            buf.append(html);
         } else {
            buf.append(c);
         }
      }
      return buf.toString();
   }

   private class EntitiesHandler extends ResolverSAXHandler {
      @Override
      public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
         super.startElement(uri, localname, qname, attr);
         if (qname.equals("entity")) {
            parseEntity(attr);
         }
      }

      private void parseEntity(Attributes attr) {
         String html = null;
         String unicode = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("html")) {
               html = attrvalue;
            } else if (attrname.equals("unicode")) {
               unicode = attrvalue;
            }
         }
         if (html != null && unicode != null) {
            String text4HTML;
            if (unicode.length() == 1) {
               text4HTML = unicode;
            } else {
               text4HTML = "&amp;#x" + unicode + ";";
            }
            addHTMLText("&" + html + ";", text4HTML);
            addInverseHTMLText("&" + html + ";", unicode);
         }
      }
   }
}
