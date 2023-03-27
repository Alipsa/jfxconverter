/*
 MODIFIED BSD LICENSE
 ***** BEGIN LICENSE BLOCK *****

 Copyright (c) 2013 Dassault Aviation
 Copyright (c) 2014 Hervé Girod
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the <organization>.
 4. Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 ***** END LICENSE BLOCK *****
 */
package org.mdiutil.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Ignore;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A JUnit runner which allows to skip tests which don't belong in specified categories.
 *
 * @version 0.9.49
 * @see JUnitCategoryWrapper
 */
public class CategoryRunner extends BlockJUnit4ClassRunner {
   private static final EmptyStatement EMPTY_STATEMENT = new EmptyStatement();
   private Accept acceptState = Accept.DEFAULT;
   private Set<String> categories = new HashSet<>();
   private Set<String> excludedCategories = new HashSet<>();
   private boolean isStrict = false;
   private boolean showSkippedClasses = true;
   private static boolean initialized = false;

   public CategoryRunner(Class<?> klass) throws InitializationError {
      super(klass);
      // the default status, because I dont know if the withBeforeClasses method will be computed if the "BeforeClass" method is empty.
      this.acceptState = Accept.DEFAULT;

      synchronized (CategoryRunner.class) {
         if (!initialized) {
            JUnitCategoryWrapper wrapper = JUnitCategoryWrapper.getInstance();
            showSkippedClasses = wrapper.isShowingSkippedClasses();
            isStrict = wrapper.isStrict();
            categories = wrapper.getCategories();
            excludedCategories = wrapper.getExcludedCategories();
            initialized = true;
         }
      }
   }

   /**
    * Return the original annotated "BeforeClass" statement if the status of the class indicates that the Unit Tests in the class should be
    * computed, else return an empty statement. This avoid to execute the associated method, whose would not be used if the Unit tests in the class are
    * not executed.
    *
    * @param statement the original statement
    * @return the original statement or an empty statement
    */
   @Override
   protected Statement withBeforeClasses(Statement statement) {
      if (acceptState == Accept.DEFAULT) {
         if (acceptClass()) {
            this.acceptState = Accept.ACCEPT;
            return super.withBeforeClasses(statement);
         } else {
            this.acceptState = Accept.SKIP;
            return EMPTY_STATEMENT;
         }
      } else if (acceptState == Accept.ACCEPT) {
         return super.withBeforeClasses(statement);
      } else {
         return EMPTY_STATEMENT;
      }
   }

   /**
    * Return the original annotated "AfterClass" statement if the status of the class indicates that the Unit Tests in the class should be
    * computed, else return an empty statement. This avoid to execute the associated method, which would possibly throw an exception because all
    * the other test methods would not have been executed prior to this one.
    *
    * @param statement the original statement
    * @return the original statement or an empty statement
    */
   @Override
   protected Statement withAfterClasses(Statement statement) {
      if (acceptState == Accept.ACCEPT) {
         return super.withAfterClasses(statement);
      } else {
         return EMPTY_STATEMENT;
      }
   }

   /**
    * Return true if all Unit tests are accepted by the wrapper.
    *
    * @return true if all Unit tests are accepted by the wrapper
    */
   private boolean acceptAll() {
      return categories.isEmpty() && excludedCategories.isEmpty();
   }

   /**
    * Return true if an annotated Unit test is accepted by the wrapper.
    *
    * @param anno the annotation
    * @return true if the annotated Unit test is accepted by the wrapper
    */
   public boolean accept(Category anno) {
      String cat = anno.cat();
      if (categories.isEmpty()) {
         if (excludedCategories.isEmpty()) {
            return true;
         } else {
            return !excludedCategories.contains(cat);
         }
      }
      if (isStrict) {
         return categories.size() == 1 && categories.contains(cat);
      } else {
         return categories.contains(cat);
      }
   }

   /**
    * Return true if a Unit test annotated by a repeating annotation is accepted by the wrapper.
    *
    * @param annoArray the array of repeating annotation
    * @return true if the Unit test annotated by a repeating annotation is accepted by the wrapper
    */
   public boolean accept(Category[] annoArray) {
      if (categories.isEmpty() && excludedCategories.isEmpty()) {
         return true;
      }
      if (isStrict) {
         /*
          * if the mode is strict, then we should at least have all the annotations defined in the categories list
          * for the Unit Test class. We can have more of course, for example:
          * If the categories are: (server, swing), and the Unit Test class has the annotations (server), it
          * will not be accepted, but if it has (server, swing, runtime), it will be accepted
          */
         if (annoArray.length < categories.size()) {
            // if we have less annotations than categories, we are sure we won't accept the Unit Test class
            return false;
         }
         // else we check that we at least have each of the categories in the Unit Test class annotations
         Set<String> categToCheck = new HashSet<>(categories);
         for (Category element : annoArray) {
            String cat = element.cat();
            if (categToCheck.isEmpty()) {
               break;
            }
            categToCheck.remove(cat);
         }
         return categToCheck.isEmpty();
      } else {
         // the "not strict mode" is much more simple, we only need to have at least one of the categories for our
         // Unit Test class
         boolean res = false;
         if (!excludedCategories.isEmpty()) {
            for (Category element : annoArray) {
               String cat = element.cat();
               if (excludedCategories.contains(cat)) {
                  return false;
               }
            }
         }
         for (Category element : annoArray) {
            String cat = element.cat();
            if (categories.contains(cat)) {
               res = true;
               break;
            }
         }
         return res;
      }
   }

   /**
    * Return true if the Unit tests in the class should be run.
    *
    * @return true if the Unit tests in the class should be run
    */
   private boolean acceptClass() {
      if (acceptAll()) {
         acceptState = Accept.ACCEPT;
         // if there are no categories defined for the wrapper, then run all the tests
         return true;
      } else {
         // else get the annotation for the Category annotation, of for the wrapper Categories annotation (repeating annotation).
         Class<?> clazz = getTestClass().getJavaClass();
         if (clazz.isAnnotationPresent(Category.class)) {
            // case where there is only one Category annotation
            Category anno = clazz.getDeclaredAnnotation(Category.class);
            if (accept(anno)) {
               return true;
            } else {
               return false;
            }
         } else if (clazz.isAnnotationPresent(Categories.class)) {
            // case where there is a repeating Category annotation
            Categories annos = clazz.getDeclaredAnnotation(Categories.class);
            Category[] annoArray = annos.value();
            if (accept(annoArray)) {
               return true;
            } else {
               return false;
            }
         } else {
            // default case : don't run anything
            return false;
         }
      }
   }

   @Override
   protected List<FrameworkMethod> getChildren() {
      if (acceptAll()) {
         acceptState = Accept.ACCEPT;
         // if there are no categories defined for the wrapper, then run all the tests
         return super.getChildren();
      } else {
         if (acceptState == Accept.DEFAULT) {
            if (acceptClass()) {
               acceptState = Accept.ACCEPT;
            } else {
               acceptState = Accept.SKIP;
            }
         }
         if (acceptState == Accept.ACCEPT) {
            return super.getChildren();
         } else {
            if (showSkippedClasses) {
               return super.getChildren();
            } else {
               return JUnitCategoryWrapper.EMPTY_LIST;
            }
         }
      }
   }

   /**
    * Evaluates whether {@link FrameworkMethod}s are ignored based on the {@link Ignore} annotation.
    *
    * @param child the FrameworkMethod
    * @return true if a FrameworkMethod is ignored
    */
   @Override
   protected boolean isIgnored(FrameworkMethod child) {
      if (acceptState != Accept.SKIP) {
         return super.isIgnored(child);
      } else {
         return true;
      }
   }

   /**
    * And empty JUnit Statement doing nothing.
    */
   private static class EmptyStatement extends Statement {
      @Override
      public void evaluate() throws Throwable {
      }
   }

   /**
    * The status for Unit tests classes, defining if:
    * <ul>
    * <li>Their Unit test methods should be run</li>
    * <li>Their Unit test methods should be skipped</li>
    * <li>The default state if their status has not been computed</li>
    * </ul>
    */
   private static enum Accept {
      /**
       * The default status for Unit tests, before it has been computed.
       */
      DEFAULT,
      /**
       * The accept status for Unit tests, specigying that the Unit tests of the test class should be run.
       */
      ACCEPT,
      /**
       * The skip status for Unit tests, specigying that the Unit tests of the test class should not be run.
       */
      SKIP
   }
}
