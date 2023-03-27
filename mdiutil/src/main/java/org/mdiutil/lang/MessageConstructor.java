/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Construct a message with a template and a series of variables.
 *
 * <h1>Examples</h1>
 * If template = "my $1 cents", and vars = {2}, the result = "my 2 cents".
 *
 * @version 1.2.16
 */
public class MessageConstructor {
   private static final Pattern PAT = Pattern.compile("\\x24\\d+");

   /**
    * Construct a message with a template and a series of variables.
    *
    * @param template the template
    * @param vars the variables
    * @return the message
    */
   public static String getText(String template, Object... vars) {
      StringBuilder buf = new StringBuilder();
      Matcher mat = PAT.matcher(template);
      int offset = 0;
      while (mat.find()) {
         int pos = mat.start();
         int end = mat.end();
         String varS = template.substring(pos + 1, end);
         int index = Integer.parseInt(varS) - 1;
         if (index == -1) {
            index = 0;
         }
         if (vars.length > index) {
            if (pos > 0) {
               buf.append(template.substring(offset, pos));
            }
            buf.append(vars[index]);
         } else {
            buf.append(template.substring(offset, pos - 1));
         }
         offset = end;
      }
      if (offset < template.length()) {
         buf.append(template.substring(offset));
      }
      return buf.toString();
   }
}
