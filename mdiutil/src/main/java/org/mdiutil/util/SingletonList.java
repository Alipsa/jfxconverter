/*------------------------------------------------------------------------------
 * Copyright (C) 2006, 2012, 2014, 2019 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class represent a List that has only one Element.
 *
 * @version 1.1
 * @param <E> the type of elements in the list
 */
public class SingletonList<E> implements List<E> {
   private E obj = null;
   private static final String IS_EMPTY = "SingletonList is Empty";
   private static final String OUT_OF_BOUNDS = "SingletonList has only one Element";
   private static final String UNSUPPORTED = "Unsupported in SingletonList";

   public SingletonList(E obj) {
      this.obj = obj;
   }

   public SingletonList() {
   }

   /**
    * Return the unique element of the list.
    *
    * @return the unique element of the list
    */
   public E getUniqueElement() {
      return obj;
   }

   /**
    * Set the unique element of the list.
    *
    * @param obj the unique element of the list
    */
   public void setUniqueElement(E obj) {
      this.obj = obj;
   }

   @Override
   public int size() {
      if (obj == null) {
         return 0;
      } else {
         return 1;
      }
   }

   @Override
   public boolean isEmpty() {
      return obj == null;
   }

   @Override
   public boolean contains(Object o) {
      if (o == obj) {
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Iterator<E> iterator() {
      return new SingletonListIterator(this);
   }

   @Override
   public Object[] toArray() {
      Object[] array = new Object[1];
      if (obj != null) {
         array[0] = obj;
      }
      return array;
   }

   @Override
   public Object[] toArray(Object[] array) {
      if (obj != null) {
         array[0] = obj;
      }
      return array;
   }

   /**
    * Appends the specified element to the end of this list. It can only be added if the list if empty.
    *
    * @param o element to be appended to this list
    */
   @Override
   public boolean add(E o) {
      if (obj == null) {
         obj = o;
         return true;
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }

   /**
    * Removes the first occurrence of the specified element from this list, if it is present.
    *
    * @param o the object
    * @return true if if this list contained the specified element
    */
   @Override
   public boolean remove(Object o) {
      if (obj == o) {
         obj = null;
         return true;
      } else {
         return false;
      }
   }

   /**
    * Returns true if this list contains all of the elements of the specified collection.
    */
   @Override
   public boolean containsAll(Collection<?> c) {
      if (c.isEmpty()) {
         return true;
      }
      if (c.size() > 1) {
         return false;
      }
      Object o = c.iterator().next();
      if (o == obj) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Appends all of the elements in the specified collection to the end of this list, in the order that they are returned
    * by the specified collection's iterator.
    */
   @Override
   public boolean addAll(Collection<? extends E> c) {
      if (obj != null) {
         return false;
      }
      if (c.size() != 1) {
         return false;
      }
      E o = c.iterator().next();
      return add(o);
   }

   /**
    * Inserts all of the elements in the specified collection into this list at the specified position.
    */
   @Override
   public boolean addAll(int index, Collection<? extends E> c) {
      if (obj != null || index != 0) {
         return false;
      }
      if (c.size() != 1) {
         return false;
      }
      E o = c.iterator().next();
      return add(o);
   }

   /**
    * Removes from this list all of its elements that are contained in the specified collection.
    */
   @Override
   public boolean removeAll(Collection<?> c) {
      boolean isChanged = false;
      Iterator<?> it = c.iterator();
      while (it.hasNext()) {
         Object o = it.next();
         if (remove(o)) {
            isChanged = true;
         }
      }
      return isChanged;
   }

   /**
    * Unsupported operation.
    */
   @Override
   public boolean retainAll(Collection c) {
      throw new UnsupportedOperationException(UNSUPPORTED);
   }

   @Override
   public void clear() {
      obj = null;
   }

   @Override
   public E get(int index) {
      if (obj == null) {
         throw new IndexOutOfBoundsException(IS_EMPTY);
      } else if (index == 0) {
         return obj;
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }

   @Override
   public E set(int index, E element) {
      if (index == 0) {
         obj = element;
         return obj;
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }

   @Override
   public void add(int index, E element) {
      if (obj == null) {
         obj = element;
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }

   @Override
   public E remove(int index) {
      if ((obj != null) && (index == 0)) {
         E _obj = obj;
         obj = null;
         return _obj;
      } else if (obj != null) {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      } else {
         throw new IndexOutOfBoundsException(IS_EMPTY);
      }
   }

   @Override
   public int indexOf(Object o) {
      if (obj == o) {
         return 0;
      } else {
         return -1;
      }
   }

   @Override
   public int lastIndexOf(Object o) {
      if (obj == o) {
         return 0;
      } else {
         return -1;
      }
   }

   @Override
   public ListIterator<E> listIterator() {
      return new SingletonListIterator(this);
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      if (index == 0) {
         return new SingletonListIterator(this);
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      if ((fromIndex == 0) & (toIndex == 0)) {
         return this;
      } else {
         throw new IndexOutOfBoundsException(OUT_OF_BOUNDS);
      }
   }
}
