/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A concurrent HashSet implementation. This class is Thread-safe but do not entail locking. It uses internally a
 * <code>ConcurrentHashMap</code>.
 *
 * @param <E> the type of element in the Set
 * @since 1.2.25.2
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
   private transient ConcurrentHashMap<E, Object> map;

   // Dummy value to associate with an Object in the backing Map
   private static final Object PRESENT = new Object();

   /**
    * Constructs a new, empty set. The backing <tt>HashMap</tt> instance has default initial capacity (16) and load factor (0.75).
    */
   public ConcurrentHashSet() {
      map = new ConcurrentHashMap<>();
   }

   /**
    * Constructs a new set containing the elements in the specified collection. The <tt>HashMap</tt> is created with default load factor (0.75) and an
    * initial capacity sufficient to contain the elements in the specified collection.
    *
    * @param c the collection whose elements are to be placed into this set
    * @throws NullPointerException if the specified collection is null
    */
   public ConcurrentHashSet(Collection<? extends E> c) {
      map = new ConcurrentHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
      addAll(c);
   }

   /**
    * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has the specified initial capacity and the specified load factor.
    *
    * @param initialCapacity the initial capacity of the hash map
    * @param loadFactor the load factor of the hash map
    * @throws IllegalArgumentException if the initial capacity is less than zero, or if the load factor is nonpositive
    */
   public ConcurrentHashSet(int initialCapacity, float loadFactor) {
      map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
   }

   /**
    * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has the specified initial capacity and default load factor (0.75).
    *
    * @param initialCapacity the initial capacity of the hash table
    * @throws IllegalArgumentException if the initial capacity is less than zero
    */
   public ConcurrentHashSet(int initialCapacity) {
      map = new ConcurrentHashMap<>(initialCapacity);
   }

   /**
    * Returns an iterator over the elements in this set. The elements are returned in no particular order.
    *
    * @return an Iterator over the elements in this set
    */
   @Override
   public Iterator<E> iterator() {
      return map.keySet().iterator();
   }

   /**
    * Returns the number of elements in this set (its cardinality).
    *
    * @return the number of elements in this set (its cardinality)
    */
   @Override
   public int size() {
      return map.size();
   }

   /**
    * Returns <tt>true</tt> if this set contains no elements.
    *
    * @return <tt>true</tt> if this set contains no elements
    */
   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   /**
    * Returns <tt>true</tt> if this set contains the specified element.
    *
    * @param o element whose presence in this set is to be tested
    * @return <tt>true</tt> if this set contains the specified element
    */
   @Override
   public boolean contains(Object o) {
      return map.containsKey(o);
   }

   /**
    * Adds the specified element to this set if it is not already present.
    *
    * @param e element to be added to this set
    * @return <tt>true</tt> if this set did not already contain the specified element
    */
   @Override
   public boolean add(E e) {
      return map.put(e, PRESENT) == null;
   }

   /**
    * Removes the specified element from this set if it is present.
    *
    * @param o object to be removed from this set, if present
    * @return <tt>true</tt> if the set contained the specified element
    */
   @Override
   public boolean remove(Object o) {
      return map.remove(o) == PRESENT;
   }

   /**
    * Removes all of the elements from this set. The set will be empty after this call returns.
    */
   @Override
   public void clear() {
      map.clear();
   }

   /**
    * Returns a shallow copy of this <tt>HashSet</tt> instance: the elements themselves are not cloned.
    *
    * @return a shallow copy of this set
    */
   public Object clone() {
      try {
         ConcurrentHashSet<E> newSet = (ConcurrentHashSet<E>) super.clone();
         newSet.map = new ConcurrentHashMap<>(map);
         return newSet;
      } catch (CloneNotSupportedException e) {
         throw new InternalError(e);
      }
   }
}
