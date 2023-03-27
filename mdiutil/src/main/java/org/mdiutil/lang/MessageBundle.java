/*------------------------------------------------------------------------------
 * Copyright (C) 2012, 2013, 2016, 2019, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

/**
 * A message provider bundle.
 *
 * @version 1.2.39.2
 */
public class MessageBundle {
   private String defaultMessage = "Undefined message for key $1";
   private final Map<String, String> messages = new HashMap<>();

   MessageBundle() {
   }

   /**
    * Set the default message to use when the key does not exist.
    *
    * @param message the default message
    */
   public void setDefaultMessage(String message) {
      this.defaultMessage = message;
   }

   /**
    * Return the default message to use when the key does not exist. The default value is "Undefined message for key $1".
    *
    * @return the default message
    */   
   public String getDefaultMessage() {
      return defaultMessage;
   }

   /**
    * Initialize the bundle with a specified URL.
    *
    * @param url the URL used for the bundle creation
    */
   protected void initialize(URL url) {
      ResourceLoader loader = new ResourceLoader(url);
      PropertyResourceBundle prb = loader.getPropertyResourceBundle();
      Enumeration<String> it = prb.getKeys();
      while (it.hasMoreElements()) {
         String key = it.nextElement();
         String message = prb.getString(key);
         messages.put(key, message);
      }
   }

   /**
    * Return a message without variables. This is valid for messages in the list without any variables declaration. A key which does not exist in the
    * bundle will return the message "Undefined message for key" followed by the key.
    * 
    * For example:
    * <ul>
    * <li>The message for the key "grammar" is "Wrong File grammar"</li>
    * <li><code>getMessage("grammar")</code> returns "Wrong File grammar"</li>
    * </ul>
    *
    * @param key the message key
    * @return the message
    */
   public String getMessage(String key) {
      if (messages.containsKey(key)) {
         return messages.get(key);
      } else {
         String message = MessageConstructor.getText(defaultMessage, key);
         return message;
      }
   }

   /**
    * Return a message with variables. This is valid for messages in the list with variables declaration. A key which does not exist in the bundle
    * will return the message "Undefined message for key" followed by the key.
    *
    * For example:
    * <ul>
    * <li>The message for the key "type.undef" is "No type of name $1"</li>
    * <li><code>getMessage("type.undef", "myValue")</code> returns "No type of name myValue"</li>
    * </ul>
    *
    * <h1>Message elaboration</h1>
    * Any String can be used for the value of the property bundle for the key. Tags $&lt;n&gt; represent the n-th variable in the vararg variables
    * array. For example:
    * <ul>
    * <li>The message for the key "my.tag" is "No type of name $1 or name $2"</li>
    * <li><code>getMessage("type.undef", "myValue", 3)</code> returns "No type of name myValue or 3"</li>
    * </ul>
    *
    * If there are more variables than n, the remaining variables will be omitted in the result. For example:
    * <ul>
    * <li>The message for the key "my.tag" is "No type of name $1"</li>
    * <li><code>getMessage("type.undef", "myValue", 3)</code> returns "No type of name myValue"</li>
    * </ul>
    * If there are less variables than n, the remaining tags will be omitted in the result. For example:
    * <ul>
    * <li>The message for the key "my.tag" is "No type of name $1 or $2"</li>
    * <li><code>getMessage("type.undef", "myValue")</code> returns "No type of name myValue or"</li>
    * </ul>
    *
    * @param key the message key
    * @param vars the varargs variables array. The String representation of each variable will be used
    * @return the message
    */
   public String getMessage(String key, Object... vars) {
      if (vars == null || vars.length== 0) {
         return getMessage(key);
      } 
      if (messages.containsKey(key)) {
         String message = messages.get(key);
         return MessageConstructor.getText(message, vars);
      } else {
         String message = MessageConstructor.getText(defaultMessage, key);
         return message;
      }
   }
}
