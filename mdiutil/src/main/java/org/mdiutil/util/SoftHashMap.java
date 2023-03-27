/*------------------------------------------------------------------------------
 * Copyright (C) 2017 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Map implementation backed by soft references for its values.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> {
   private Map<K, SoftValue> map = new HashMap<>();
   private final ReferenceQueue<? super V> queue = new ReferenceQueue<>();

   public SoftHashMap() {
   }

   /**
    * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
    *
    * @param key the key
    * @return the value
    */
   @Override
   public V get(Object key) {
      V res = null;
      SoftReference<V> sr = (SoftReference) map.get(key);
      if (sr != null) {
         res = sr.get();
         if (res == null) {
            map.remove((K) key);
         }
      }
      return res;
   }

   /**
    * Associates the specified value with the specified key in this map.
    *
    * @param key the key
    * @param value the value
    * @return the value
    */
   @Override
   public V put(K key, V value) {
      // throw out garbage collected values first
      processQueue();
      map.put(key, new SoftValue(value, key, queue));
      return value;
   }

   /**
    * Removes the mapping for a key from this map if it is present.
    *
    * @param key the key
    * @return the value
    */
   @Override
   public V remove(Object key) {
      // throw out garbage collected values first
      processQueue();
      SoftValue value = map.remove(key);
      return value != null ? value.get() : null;
   }

   /**
    * Returns true if this map contains a mapping for the specified key.
    *
    * @param key the key
    * @return true if this map contains a mapping for the specified key
    */
   @Override
   public boolean containsKey(Object key) {
      // throw out garbage collected values first
      processQueue();
      return map.containsKey(key);
   }

   /**
    * Returns true if this map maps one or more keys to the specified value.
    *
    * @param value the value
    * @return true if this map contains a mapping for the specified key
    */
   @Override
   public boolean containsValue(Object value) {
      // throw out garbage collected values first
      processQueue();
      Collection values = values();
      return values != null && values.contains(value);
   }

   /**
    * Copies all of the mappings from the specified map to this map.
    *
    * @param map the map
    */
   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      if (map == null || map.isEmpty()) {
         // throw out garbage collected values first
         processQueue();
         return;
      }
      for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
         put(entry.getKey(), entry.getValue());
      }
   }

   /**
    * Returns a {@link Set} view of the keys contained in this map..
    *
    * @return the Set
    */
   @Override
   public Set<K> keySet() {
      // throw out garbage collected values first
      processQueue();
      return map.keySet();
   }

   /**
    * Returns true if this map contains no key-value mappings.
    *
    * @return true if this map contains no key-value mappings
    */
   @Override
   public boolean isEmpty() {
      // throw out garbage collected values first
      processQueue();
      return map.isEmpty();
   }

   /**
    * Clear the content of this map.
    */
   @Override
   public void clear() {
      // throw out garbage collected values first
      processQueue();
      map.clear();
   }

   /**
    * Return the size of this map.
    *
    * @return the size
    */
   @Override
   public int size() {
      // throw out garbage collected values first
      processQueue();
      return map.size();
   }

   /**
    * Returns a {@link Set} view of the mappings contained in this map.
    *
    * @return the entry set
    */
   @Override
   public Set<Entry<K, V>> entrySet() {
      // throw out garbage collected values first
      processQueue();
      Collection<K> keys = map.keySet();
      if (keys.isEmpty()) {
         return Collections.EMPTY_SET;
      }

      Map<K, V> pairs = new HashMap<>(keys.size());
      for (K key : keys) {
         V v = get(key);
         if (v != null) {
            pairs.put(key, v);
         }
      }
      return pairs.entrySet();
   }

   private void processQueue() {
      while (true) {
         SoftValue sv = (SoftValue) queue.poll();
         if (sv != null) {
            map.remove(sv.key);
         } else {
            break;
         }
      }
   }

   /**
    * Return a collection view of the values contained in this map.
    *
    * @return the view of the values contained in this map
    */
   @Override
   public Collection<V> values() {
      processQueue();
      Collection<K> keys = map.keySet();
      if (keys.isEmpty()) {
         return Collections.EMPTY_SET;
      }
      Collection<V> values = new ArrayList<>(keys.size());
      for (K key : keys) {
         V v = get(key);
         if (v != null) {
            values.add(v);
         }
      }
      return values;
   }

   /**
    * The SoftReference value.
    */
   private class SoftValue extends SoftReference<V> {
      private final K key;

      private SoftValue(V referent, K key, ReferenceQueue<? super V> q) {
         super(referent, q);
         this.key = key;
      }
   }
}
