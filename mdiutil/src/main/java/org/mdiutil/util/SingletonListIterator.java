/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This class represents a ListIterator that iterate on SingletonLists.
 *
 * @version 0.7.20
 * @param <E> the type of elements in the list
 */
public class SingletonListIterator<E> implements ListIterator<E> {
   private static final String OUT_OF_BOUNDS = "SingletonList has only one Element";
   private boolean first = true;
   private SingletonList<E> list = null;

   /**
    * Constructor.
    * 
    * @param list the SingletonList
    */
   public SingletonListIterator(SingletonList<E> list) {
      this.list = list;
      if (list.isEmpty()) {
         first = false;
      }
   }

   /**
    * Return true if the iterator has just been created (the {@link #next()} method has not been called).
    *
    * @return true if the iterator has just been created (the {@link #next()} method has not been called)
    */
   @Override
   public boolean hasNext() {
      return first;
   }

   /**
    * Return the only element in the list if the iterator has just been created (the {@link #next()} method has not been called),
    * or a {@link NoSuchElementException} else.
    *
    * @return the only element in the list
    */
   @Override
   public E next() {
      if (first) {
         first = false;
         return list.getUniqueElement();
      } else {
         throw new NoSuchElementException(OUT_OF_BOUNDS);
      }
   }

   /**
    * Return false.
    */
   @Override
   public boolean hasPrevious() {
      return false;
   }

   /**
    * Return a {@link NoSuchElementException}.
    */
   @Override
   public E previous() {
      throw new NoSuchElementException(OUT_OF_BOUNDS);
   }

   /**
    * Return 0 if the iterator has just been created (the {@link #next()} method has not been called), or 1 else.
    *
    * @return -1
    */
   @Override
   public int nextIndex() {
      if (first) {
         return 0;
      } else {
         return 1;
      }
   }

   /**
    * Return -1.
    *
    * @return -1
    */
   @Override
   public int previousIndex() {
      return -1;
   }

   /**
    * Remove the unique element in the list. Will return an {@link IllegalStateException} if the list is not empty.
    */
   @Override
   public void remove() {
      if (list.getUniqueElement() == null) {
         throw new IllegalStateException("Empty SingletonList");
      } else {
         list.setUniqueElement(null);
      }
   }

   /**
    * Replace the unique element in the list by the object. Will return an {@link IllegalStateException}
    * if the list is not empty.
    *
    * @param o the object
    */
   @Override
   public void set(E o) {
      if (list.getUniqueElement() == null) {
         throw new IllegalStateException("Empty SingletonList");
      } else {
         list.setUniqueElement(o);
      }
   }

   /**
    * Set the unique element in the list. Will return an {@link IllegalStateException} if the list is not empty.
    *
    * @param o the object
    */
   @Override
   public void add(E o) {
      if (list.getUniqueElement() != null) {
         throw new IllegalStateException("Impossible to add more than one element in the SingletonList");
      } else {
         list.setUniqueElement(o);
      }
   }
}
