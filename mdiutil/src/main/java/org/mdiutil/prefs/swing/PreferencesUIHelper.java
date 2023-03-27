/*------------------------------------------------------------------------------
 * Copyright (C) 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.prefs.swing;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import org.mdiutil.prefs.PreferencesHelper;

/**
 * This class helps to put and get values to Preferences for Colors, which are not direcly handled in the core java Preferences class.
 *
 * @version 1.2.9
 */
public class PreferencesUIHelper extends PreferencesHelper {
   private static final String ELEMENT_RADIX = "element";

   /**
    * Associates a string representing the specified Color with the specified key
    * in this preference node.
    * <p>
    * The Color will be stored as a String with this pattern :
    * <pre>
    *     &lt;red&gt;;&lt;green&gt;;&lt;blue&gt;;&lt;alpha&gt;
    * </pre>
    * where red, green blue, and alpha are the integer values or the sRGB Color representation of the Color.</p>
    *
    * @param prefs the Preferences
    * @param key the key
    * @param color the Color
    */
   public static void putColor(Preferences prefs, String key, Color color) {
      int blue = color.getBlue();
      int green = color.getGreen();
      int red = color.getRed();
      int alpha = color.getAlpha();
      String value = Integer.toString(red) + ";" + Integer.toString(green) + ";"
         + Integer.toString(blue) + ";" + Integer.toString(alpha);
      prefs.put(key, value);
   }

   private static int normalizeColorValue(int value) {
      if (value < 0) {
         return 0;
      } else if (value > 255) {
         return 255;
      } else {
         return value;
      }
   }

   /**
    * Returns the Color represented by the string associated with the specified key
    * in this preference node.
    *
    * @param prefs the preferences node
    * @param key the key
    * @param defColor the default Color
    * @return the Color
    * @see #putColor(Preferences, String, Color)
    */
   public static Color getColor(Preferences prefs, String key, Color defColor) {
      Color color = defColor;
      try {
         String s = prefs.get(key, "");
         StringTokenizer tok = new StringTokenizer(s, ";");
         int tokens = tok.countTokens();
         if (tokens != 4) {
            throw new NumberFormatException("must have 4 tokens for Color");
         } else {
            int red = normalizeColorValue(Integer.parseInt(tok.nextToken()));
            int green = normalizeColorValue(Integer.parseInt(tok.nextToken()));
            int blue = normalizeColorValue(Integer.parseInt(tok.nextToken()));
            int alpha = normalizeColorValue(Integer.parseInt(tok.nextToken()));
            color = new Color(red, green, blue, alpha);
         }
      } catch (NumberFormatException e) {
         color = defColor;
      }

      return color;
   }
}
