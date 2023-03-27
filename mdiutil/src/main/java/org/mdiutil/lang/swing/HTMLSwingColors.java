/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang.swing;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mdiutil.lang.HTMLColors;

/**
 * This class concerts a color represented as an html name or an hexadecimal representation to an awt color.
 *
 * For example:
 * <pre>
 * String color = HTMLColors.decodeColor("00CC00"); // return new Color(0, 204, 0)
 * String color = HTMLColors.decodeColor("#00CC00"); // return new Color(0, 204, 0)
 * color = HTMLColors.decodeColor("black"); // return Color(0, 0, 0)
 * color = HTMLColors.decodeColor("white"); // return Color(255, 255, 255)
 * color = HTMLColors.decodeColor("darkorange"); // return Color(255, 140, 0)
 * </pre>
 *
 * @since 1.2.35
 */
public class HTMLSwingColors {
   private static final Pattern COLORPAT = Pattern.compile("#([0-9A-Fa-f]+)");

   private HTMLSwingColors() {
   }

   /**
    * Decode a color. Will return a default color if the color does not exist.
    *
    * @param color the color as a String
    * @param defaultColor the default color
    * @return the color
    */
   public static Color decodeColor(String color, Color defaultColor) {
      String htmlColor = HTMLColors.decodeColor(color);
      if (htmlColor == null) {
         return defaultColor;
      }
      Matcher m = COLORPAT.matcher(htmlColor);
      if (!m.matches()) {
         return defaultColor;
      }
      String hexa = m.group(1);
      if (hexa == null || hexa.length() != 6) {
         return defaultColor;
      }
      String redRep = hexa.substring(0, 2);
      String greenRep = hexa.substring(2, 4);
      String blueRep = hexa.substring(4, 6);
      int red = Integer.parseInt(redRep, 16);
      int green = Integer.parseInt(greenRep, 16);
      int blue = Integer.parseInt(blueRep, 16);
      return new Color(red, green, blue);
   }

   /**
    * Decode a color. Will return null if the color does not exist.
    *
    * @param color the color
    * @return the color
    */
   public static Color decodeColor(String color) {
      return decodeColor(color, null);
   }
}
