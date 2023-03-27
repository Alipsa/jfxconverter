/*
Copyright (c) 2021, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.lang;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class allows to set the <code>java.library.path</code> property after the start of the JVM.
 * Note that this method will only work for Java 8 up to Java 14.
 *
 * The <code>java.library.path</code> is read only once when the JVM starts up.
 * If you change this property using <code>System.setProperty</code>, it won't make any difference.
 *
 * The way this class work is by setting it to null prior to setting the new path. This method will unload the
 * previously specified paths and replace them by the new ones.
 *
 * Note that this method will use a different mechanism for Java 8 and Java 9, and for Java 10 up to Java 14.
 *
 * @since 1.2.18
 */
public class NativeLibraryLoader {

   public NativeLibraryLoader() {
   }

   private String constructLibPaths(List<URL> libpaths) {
      StringBuilder buf = new StringBuilder();
      Iterator<URL> it = libpaths.iterator();
      boolean first = true;
      while (it.hasNext()) {
         URL url = it.next();
         String path = url.getFile();
         if (first) {
            buf.append(path);
            first = false;
         } else {
            buf.append(";").append(path);
         }
      }
      return buf.toString();
   }

   /**
    * This method allows to set the <code>java.library.path</code> property after the start of the JVM.
    * Note that this method will only work for Java 8 up to Java 14.
    *
    * This method will unload the previously specified paths and replace them by the new ones. Note that this method
    * will use a different mechanism for Java 8 and Java 9, and for Java 10 up to Java 14.
    *
    * @param libpaths the library paths
    * @return true if the paths could be modified at runtime
    */
   public boolean loadLibraries(List<URL> libpaths) throws LoadLibraryException {
      if (!libpaths.isEmpty()) {
         // the library path is empty
         return false;
      } else if (SystemUtils.isAtLeastVersion("15")) {
         // this mechanims is not possible anymore beginning with Java 15
         throw new LoadLibraryException("Impossible to load native libraries at runtime since Java 15");
      } else if (SystemUtils.isAtLeastVersion("10")) {
         String paths = constructLibPaths(libpaths);
         return setLibpathImplJVM10(paths);
      } else {
         String paths = constructLibPaths(libpaths);
         return setLibpathImplJVM8(paths);
      }
   }

   private boolean setLibpathImplJVM10(String thePaths) throws LoadLibraryException {
      try {
         // This enables the java.library.path to be modified at runtime for Java 10 up to Java 12
         // a blog https://blog.sibvisions.com/2020/05/22/adding-new-paths-for-native-libraries-at-runtime-in-java/
         //
         if (ClassLoader.class.getDeclaredFields().length == 0) {
            Class<?> clsMHandles = Class.forName("java.lang.invoke.MethodHandles");
            Method mStaticLookup = clsMHandles.getMethod("lookup");
            Object oStaticLookup = mStaticLookup.invoke(null);

            Method mLookup = clsMHandles.getMethod("privateLookupIn", Class.class, Class.forName("java.lang.invoke.MethodHandles$Lookup"));
            Object oLookup = mLookup.invoke(null, ClassLoader.class, oStaticLookup);
            Method mFindStatic = oLookup.getClass().getMethod("findStaticVarHandle", Class.class, String.class, Class.class);
            Object oVarHandle = mFindStatic.invoke(oLookup, ClassLoader.class, "usr_paths", String[].class);

            Method mFindVirtual = oStaticLookup.getClass().getMethod("findVirtual", Class.class, String.class, Class.forName("java.lang.invoke.MethodType"));
            Class<?> clsMethodType = Class.forName("java.lang.invoke.MethodType");
            Method mMethodType = clsMethodType.getMethod("methodType", Class.class);
            Object oMethodHandleGet = mFindVirtual.invoke(oStaticLookup, Class.forName("java.lang.invoke.VarHandle"), "get", mMethodType.invoke(null, Object.class));
            Method mMethodHandleGet = oMethodHandleGet.getClass().getMethod("invokeWithArguments", Object[].class);
            String[] saPath = (String[]) mMethodHandleGet.invoke(oMethodHandleGet, new Object[] { new Object[] { oVarHandle } });
            //check if the path to add is already present
            for (String path : saPath) {
               if (path.equals(thePaths)) {
                  return false;
               }
            }

            //add the new path
            final String[] saNewPaths = Arrays.copyOf(saPath, saPath.length + 1);
            saNewPaths[saNewPaths.length - 1] = thePaths;

            mMethodType = clsMethodType.getMethod("methodType", Class.class, Class.class);
            Object oMethodHandleSet = mFindVirtual.invoke(oStaticLookup, Class.forName("java.lang.invoke.VarHandle"), "set", mMethodType.invoke(null, Void.class, Object[].class));
            Method mMethodHandleSet = oMethodHandleSet.getClass().getMethod("invokeWithArguments", Object[].class);
            mMethodHandleSet.invoke(oMethodHandleSet, new Object[] { new Object[] { oVarHandle, saNewPaths } });
            return true;
         } else {
            final Field fldUsrPaths = ClassLoader.class.getDeclaredField("usr_paths");
            fldUsrPaths.setAccessible(true);

            //get array of paths
            final String[] saPath = (String[]) fldUsrPaths.get(null);

            //check if the path to add is already present
            for (String path : saPath) {
               if (path.equals(thePaths)) {
                  return false;
               }
            }

            //add the new path
            final String[] saNewPaths = Arrays.copyOf(saPath, saPath.length + 1);
            saNewPaths[saNewPaths.length - 1] = thePaths;
            fldUsrPaths.set(null, saNewPaths);
            return true;
         }
      } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException
         | InvocationTargetException | NoSuchMethodException e) {
         throw new LoadLibraryException(e);

      }
   }

   private boolean setLibpathImplJVM8(String thePaths) throws LoadLibraryException {
      try {
         // This enables the java.library.path to be modified at runtime for Java 10 up to Java 12
         // http://fahdshariff.blogspot.com/2011/08/changing-java-library-path-at-runtime.html
         //
         Field field = ClassLoader.class.getDeclaredField("usr_paths");
         field.setAccessible(true);
         String[] paths = (String[]) field.get(null);
         for (int i = 0; i < paths.length; i++) {
            if (thePaths.equals(paths[i])) {
               // the path is already present
               return false;
            }
         }
         String[] tmp = new String[paths.length + 1];
         System.arraycopy(paths, 0, tmp, 0, paths.length);
         tmp[paths.length] = thePaths;
         //set sys_paths to null
         field.set(null, tmp);
         // set the new paths
         System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + thePaths);
         return true;
      } catch (IllegalAccessException | NoSuchFieldException e) {
         throw new LoadLibraryException(e);
      }
   }
}
