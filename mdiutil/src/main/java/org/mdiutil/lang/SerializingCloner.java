/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class allows to clone serializable objects which are not Cloneable. It works by serializing and unserializing the object.
 *
 * @since 0.9.23
 * @param <C> the Object class
 */
public class SerializingCloner<C> {
   public SerializingCloner() {
   }

   /**
    * Deep clone an object by serializing and unserializing the object in memory.
    *
    * @param obj the object
    * @return the cloned object
    * @throws IOException if an IOException occur (mainly a {@link java.io.NotSerializableException})
    * @throws ClassNotFoundException if no class is found
    */
   public C deepClone(C obj) throws IOException, ClassNotFoundException {
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         oos = new ObjectOutputStream(bos);
         oos.writeObject(obj);
         oos.flush();
         ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
         ois = new ObjectInputStream(bin);
         return (C) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
         throw (e);
      } finally {
         oos.close();
         if (ois != null) {
            ois.close();
         }
      }
   }
}
