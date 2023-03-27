/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2016 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.util.logging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A Logging filter which can filter depending on the path of the source class for the LogRecord.
 *
 * @version 0.9.10
 */
public class PathFilter implements Filter {
   private Set<String> paths = new HashSet<>();
   private int filterLevel = Level.INFO.intValue();
   private boolean isEmpty = true;

   /**
    * Return the filter level. Above thislevel, the Filter will accept any logRecord. Below this level, Only the classes whose
    * path begin with one of the specified {@link #getPaths()} will be loggable. The default if INFO.
    *
    * @return the filter level
    */
   public int getFilterLevel() {
      return filterLevel;
   }

   /**
    * Set the filter level. Above thislevel, the Filter will accept any logRecord. Below this level, Only the classes whose
    * path begin with one of the specified {@link #getPaths()} will be loggable. The default if INFO.
    *
    * @param filterLevel the filter Level
    */
   public void setFilterLevel(int filterLevel) {
      this.filterLevel = filterLevel;
   }

   /**
    * Return the paths which will be filtered. Only the classes whose path begin with one of the specified paths will
    * be loggable.
    *
    * @return the paths which will be filtered
    */
   public Set<String> getPaths() {
      return paths;
   }

   /**
    * Set the paths which will be filtered. Only the classes whose path begin with one of the specified paths will
    * be loggable.
    *
    * @param paths the paths to set
    */
   public void setPaths(Set<String> paths) {
      this.paths = paths;
      this.isEmpty = (paths == null) || (paths.isEmpty());
   }

   /**
    * Return true if the record if Loggable.
    * <ul>
    * <li>If the level of the LogRecord is greater or equal than {@link #getFilterLevel()}, then the method will
    * return true (the LogRecord is loggable}</li>
    * <li>Else if there is no source for the LogRecord, return true (the LogRecord is loggable}</li>
    * <li>Else return true only if the source path for the LogRecord belong to one of the paths specified by
    * {@link #setPaths(Set)}</li>
    * </ul>
    *
    * <h1>Examples</h1>
    * Consider a Module Filter with the default filter level configuration (INFO), with the following definition for the
    * modules paths:
    * <ul>
    * <li>"my.package"</li>
    * <li>"my.package2.myClass"</li>
    * </ul>
    * <br/>
    * If the LogRecord has the level WARNING, it will be loggable (the method will return true).
    * <br/>
    * If the LogRecord has the level INFO, and the source is null, it will be loggable.
    * <br/>
    * If the LogRecord has the level INFO, and the source is "my.package.anotherClass, it will be loggable.
    * <br/>
    * If the LogRecord has the level INFO, and the source is "my.package2.myClass, it will be loggable.
    * <br/>
    * If the LogRecord has the level INFO, and the source is "my.package2.anotherClass2, it will not be loggable.
    *
    * @param record the LogRecord
    * @return true if the record if Loggable
    */
   @Override
   public boolean isLoggable(LogRecord record) {
      if (record.getLevel().intValue() <= getFilterLevel()) {
         String source = record.getSourceClassName();
         if (isEmpty) {
            return true;
         } else if (source == null) {
            return true;
         } else {
            boolean ok = false;
            Iterator<String> it = getPaths().iterator();
            while (it.hasNext()) {
               String path = it.next();
               if (source.equals(path)) {
                  // the exact same paths, we must return ok
                  ok = true;
                  break;
               } else if (source.startsWith(path)) {
                  // this case if to avoid to return as loggable if we have for the path something like
                  // "org.mypackage" and the source is like "org.myPackage2.myClass" for example. We make sure that
                  // we go to the next dot if there is one
                  if (source.charAt(path.length()) == '.') {
                     ok = true;
                     break;
                  }
               }
            }
            return ok;
         }
      } else {
         return true;
      }
   }
}
