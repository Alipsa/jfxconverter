/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Maintain a list of all html colors and allows to convert a color to its hexadecimal representation.
 *
 * For example:
 * <pre>
 * String color = HTMLColors.decodeColor("00CC00"); // return "#00CC00"
 * String color = HTMLColors.decodeColor("#00CC00"); // return "#00CC00"
 * color = HTMLColors.decodeColor("black"); // return "#000000"
 * color = HTMLColors.decodeColor("white"); // return "#FFFFFF"
 * color = HTMLColors.decodeColor("darkorange"); // return "#FF8C00"
 * </pre>
 *
 * @version 1.2.35
 */
public class HTMLColors {
   private static HTMLColors htmlColors = null;
   private final Map<String, String> colors = new HashMap<>();
   private static final Pattern COLORPAT = Pattern.compile("#[0-9A-Fa-f]+");
   private static final Pattern COLORPAT2 = Pattern.compile("[0-9A-Fa-f]+");

   private HTMLColors() {
      parseColors();
   }

   private void parseColors() {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      try {
         SAXParser parser = factory.newSAXParser();
         URL url = HTMLColors.class.getResource("htmlColors.xml");
         parser.parse(url.openStream(), new ColorsHandler());
      } catch (ParserConfigurationException | SAXException | IOException ex) {
      }
   }

   /**
    * Decode a color. Will return null if the color does not exist.
    *
    * @param color the color
    * @return the color as an hexadecimal representation
    */
   public static String decodeColor(String color) {
      if (htmlColors == null) {
         htmlColors = new HTMLColors();
      }
      return htmlColors.decodeColorImpl(color);
   }

   /**
    * Decode a color.
    *
    * @param color the color
    * @return the color as an hexadecimal representation
    */
   private String decodeColorImpl(String color) {
      if (color == null || color.length() < 2) {
         return null;
      }
      Matcher m = COLORPAT.matcher(color);
      if (m.matches()) {
         return color.toUpperCase();
      }
      color = color.toLowerCase();
      if (colors.containsKey(color)) {
         return "#" + colors.get(color);
      }
      m = COLORPAT2.matcher(color);
      if (m.matches()) {
         return "#" + color.toUpperCase();
      }
      return null;
   }

   private class ColorsHandler extends DefaultHandler {
      @Override
      public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
         if (qName.equals("color")) {
            parseColor(attr);
         }
      }

      private void parseColor(Attributes attr) {
         String color = null;
         String hexID = null;
         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("name")) {
               color = attrvalue;
            } else if (attrname.equals("hex")) {
               hexID = attrvalue;
            }
         }
         if (color != null && hexID != null) {
            colors.put(color, hexID);
         }
      }
   }
}
