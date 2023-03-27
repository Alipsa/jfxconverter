/*------------------------------------------------------------------------------
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides several System utilities.
 *
 * @version 1.2.18
 */
public class SystemUtils {
   private static final Pattern VERSION = Pattern.compile("(\\d{1,2})\\.?(\\d)?(\\.(\\d))?(_\\d+)?(-\\S+)?");
   private static final short OS_UNDEFINED = -1;
   /**
    * The type for OS which are neither Mac OS X, Windows, Linux, or Unix.
    */
   public static final short OS_OTHER = 0;
   /**
    * The type for MAC OS X OS.
    */
   public static final short OS_MACOSX = 1;
   /**
    * The type for Windows OS.
    */
   public static final short OS_WINDOWS = 2;
   /**
    * The type for Linux OS.
    */
   public static final short OS_LINUX = 3;
   /**
    * The type for Unix OS.
    */
   public static final short OS_UNIX = 4;
   private static final short ARCH_UNDEFINED = -1;
   /**
    * The type for unknown processor architectures.
    */
   public static final short ARCH_OTHER = 0;
   /**
    * The type for 32 bits x86 architectures.
    */
   public static final short ARCH_X86_32 = 1;
   /**
    * The type for 64 bits x86 architectures.
    */
   public static final short ARCH_X86_64 = 2;
   /**
    * The type for 32 bits PPC architectures.
    */
   public static final short ARCH_PPC_32 = 3;
   /**
    * The type for 64 bits PPC architectures.
    */
   public static final short ARCH_PPC_64 = 4;
   /**
    * The type for 32 bits SPARC architectures.
    */
   public static final short ARCH_SPARC_32 = 5;
   /**
    * The type for 64 bits SPARC architectures.
    */
   public static final short ARCH_SPARC_64 = 6;
   /**
    * The type for 32 bits ARM architectures.
    */
   public static final short ARCH_ARM_32 = 7;
   /**
    * The type for 64 bits ARM architectures.
    */
   public static final short ARCH_ARM_64 = 8;
   private static short osType = OS_UNDEFINED;
   private static short archType = ARCH_UNDEFINED;

   private SystemUtils() {
   }

   /**
    * Return the OS type. The supported OS are:
    * <ul>
    * <li>{@link #OS_MACOSX}</li>
    * <li>{@link #OS_WINDOWS}</li>
    * <li>{@link #OS_LINUX}</li>
    * <li>{@link #OS_UNIX} for Unix systems (AIX, SunOS, hp-ux)</li>
    * <li>{@link #OS_OTHER} for unknown OS types</li>
    * </ul>
    *
    * @return the OS type
    */
   public static final short getPlatformType() {
      if (osType == OS_UNDEFINED) {
         String osName = System.getProperty("os.name").toLowerCase();
         if (osName.startsWith("mac os x")) {
            osType = OS_MACOSX;
         } else if (osName.startsWith("windows")) {
            osType = OS_WINDOWS;
         } else if (osName.startsWith("linux")) {
            osType = OS_LINUX;
         } else if (osName.startsWith("unix")) {
            osType = OS_UNIX;
         } else if (osName.startsWith("aix")) {
            osType = OS_UNIX;
         } else if (osName.startsWith("sunos")) {
            osType = OS_UNIX;
         } else if (osName.startsWith("hp-ux")) {
            osType = OS_UNIX;
         } else {
            osType = OS_OTHER;
         }
         return osType;
      } else {
         return osType;
      }
   }

   /**
    * Return the JVM Platform architecture. Note that a 64 bits platform with a 32 bits JVM will be detected as 32 bits.
    * <ul>
    * <li>{@link #ARCH_X86_32} and {@link #ARCH_X86_64} for x86 architectures</li>
    * <li>{@link #ARCH_PPC_32} and {@link #ARCH_PPC_64} for PPC architectures</li>
    * <li>{@link #ARCH_SPARC_32} and {@link #ARCH_SPARC_64} for SPARC architectures</li>
    * <li>{@link #ARCH_ARM_32} and {@link #ARCH_ARM_64} for ARM architectures</li>
    * <li>{@link #ARCH_OTHER} for unknown architectures</li>
    * </ul>
    *
    * @return the JVM Platform architecture
    */
   public static final short getArchitecture() {
      if (archType == ARCH_UNDEFINED) {
         String osName = System.getProperty("os.arch").toLowerCase();
         if (osName.equals("x86") || osName.equals("ia32") || osName.equals("i386") || osName.equals("i486") || osName.equals("i586")
            || osName.equals("i686") || osName.equals("x8632") || osName.equals("x32")) {
            archType = ARCH_X86_32;
         } else if (osName.equals("x86_64") || osName.equals("ia64") || osName.equals("amd64") || osName.equals("x8664")
            || osName.equals("itanium64") || osName.equals("x64")) {
            archType = ARCH_X86_64;
         } else if (osName.equals("powerpc") || osName.equals("ppc")) {
            archType = ARCH_PPC_32;
         } else if (osName.equals("ppc64")) {
            archType = ARCH_PPC_64;
         } else if (osName.equals("sparc") || osName.equals("sparc32")) {
            archType = ARCH_SPARC_32;
         } else if (osName.equals("sparcv9") || osName.equals("sparc64")) {
            archType = ARCH_SPARC_64;
         } else if (osName.equals("arm") || osName.equals("arm32")) {
            archType = ARCH_ARM_32;
         } else if (osName.equals("aarch_64")) {
            archType = ARCH_ARM_64;
         } else {
            archType = ARCH_OTHER;
         }
      }
      return archType;
   }

   /**
    * Return true if the JVM Platform architecture is detected as 64 bits.
    * Note that a 64 bits platform with a 32 bits JVM will be detected as 32 bits.
    *
    * @return true if the JVM Platform architecture is detected as 64 bits
    */
   public static final boolean is64Bits() {
      getArchitecture();
      return archType == ARCH_X86_64 || archType == ARCH_PPC_64 || archType == ARCH_SPARC_64 || archType == ARCH_ARM_64;
   }

   /**
    * Return true if the JVM Platform architecture is detected as 32 bits.
    * Note that a 64 bits platform with a 32 bits JVM will be detected as 32 bits.
    *
    * @return true if the JVM Platform architecture is detected as 32 bits
    */
   public static final boolean is32Bits() {
      getArchitecture();
      return archType == ARCH_X86_32 || archType == ARCH_PPC_32 || archType == ARCH_SPARC_32 || archType == ARCH_ARM_32;
   }

   /**
    * Return true if the platform is an UNIX-like OS.
    *
    * @return true if the platform is an UNIX-like OS
    */
   public static boolean isUnixPlatform() {
      getPlatformType();
      return osType == OS_UNIX || osType == OS_LINUX || osType == OS_MACOSX;
   }

   /**
    * Return true if the platform is a Windows OS.
    *
    * @return true if the platform is a Windows OS
    */
   public static boolean isWindowsPlatform() {
      getPlatformType();
      return osType == OS_WINDOWS;
   }

   /**
    * Return true if an executable file is compatible with the OS architecture used by the JVM. The formats which are supported are:
    * <ul>
    * <li>Portable Executable Format executable on Windows for 32 bits or 64 bits</li>
    * <li>Executable and Linkable Format (elf) executable on Unix-like systems for 32 bits or 64 bits</li>
    * <li>Mach-O executable on Mac OS X for 32 bits or 64 bits (Universal binaries are not supported)</li>
    * </ul>
    *
    * @param file the executable file
    * @param strict if the type must strictly be the same as the OS architecture
    * @return true if the executable file is compatible with the OS architecture used by the JVM
    */
   public static final boolean isCompatible(File file, boolean strict) {
      if (is64Bits()) {
         short type = getExecutableType(file);
         if (!strict) {
            return type == HeaderDecoder.EXE_32BITS || type == HeaderDecoder.EXE_64BITS;
         } else {
            return type == HeaderDecoder.EXE_64BITS;
         }
      } else if (is32Bits()) {
         short type = getExecutableType(file);
         if (!strict) {
            if (type == HeaderDecoder.EXE_INVALID) {
               return false;
            } else if (type == HeaderDecoder.EXE_32BITS) {
               return true;
            } else if (type == HeaderDecoder.EXE_64BITS) {
               return false;
            } else {
               return false;
            }
         } else {
            return type == HeaderDecoder.EXE_32BITS;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the type of an executable. The possible returned values are:
    * <ul>
    * <li>{@link HeaderDecoder#EXE_INVALID}: the value for invalid executable Files (can not exist,
    * not being readable, or not being a valid executable File for the Platform)</li>
    * <li>{@link HeaderDecoder#EXE_32BITS}: the value for 32 bits executables</li>
    * <li>{@link HeaderDecoder#EXE_64BITS}: the value for 64 bits executables</li>
    * <li>{@link HeaderDecoder#EXE_UNIVERSAL}: the value for universal binaries executables</li>
    * </ul>
    *
    * @param file the executable file
    * @return the type of the executable
    */
   public static final short getExecutableType(File file) {
      if (isWindowsPlatform()) {
         HeaderDecoder decoder = new PEHeaderDecoder(file);
         return decoder.getExecutableType();
      } else {
         short platform = getPlatformType();
         if (platform == OS_MACOSX) {
            HeaderDecoder decoder = new MachOHeaderDecoder(file);
            return decoder.getExecutableType();
         } else if (isUnixPlatform()) {
            HeaderDecoder decoder = new ELFHeaderDecoder(file);
            return decoder.getExecutableType();
         } else {
            // maybe not invalid, but we don't have a decoder for it
            return HeaderDecoder.EXE_INVALID;
         }
      }
   }

   /**
    * Return true if the current Java version is at least equal to the specified version. The algorithm take care of the
    * pattern for the String-naming convention for the version, taking into account all digits, including those used for the
    * maintenance version but not using the last part used for non-GA versions
    * (see <a href="http://www.oracle.com/technetwork/java/javase/versioning-naming-139433.html">J2SE SDK/JRE Version String Naming Convention</a>).
    * <p>
    * For example, if the current version is 1.7.0_13, the result would be, depending on the specified Java version:
    * <ul>
    * <li>1.7: as 1.7.0_13 &ge; 1.7, the method would return true</li>
    * <li>1.7.0_14: as 1.7.0_13 &lt; 1.7.0_14, the method would return false</li>
    * <li>1.7.0_12: as 1.7.0_13 &ge; 1.7.0_12, the method would return true</li>
    * </ul>
    * </p>
    *
    * @param javaVersion the specified Java version
    * @return true if the current Java version is at least equal to the specified version
    */
   public static boolean isAtLeastVersion(String javaVersion) {
      String curVersion = System.getProperty("java.version");
      return isAtLeastVersion(curVersion, javaVersion);
   }

   /**
    * Return true if the current Java version is between the atLeastVersion and atMostVersion. The comparison includes the versions
    * themselves.
    *
    * <p>
    * For example, if the current version is 1.7.0_13, the result would be, depending on the specified Java version:
    * <ul>
    * <li>[1.7, 1.7]: as 1.7.0_13 &ge; 1.7, the method would return true</li>
    * <li>[1.7.0_14, 1.8]: as 1.7.0_13 &lt; 1.7.0_14, the method would return false</li>
    * <li>[1.7, 1.7.0_12]: as 1.7.0_13 &ge; 1.7.0_12, the method would return true</li>
    * </ul>
    * </p>
    *
    * @param currentVersion the current Java version
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public static final boolean isBetweenVersions(String currentVersion, String atLeastVersion, String atMostVersion) {
      return isBetweenVersions(currentVersion, atLeastVersion, atMostVersion, false);
   }

   /**
    * Return true if the current Java version is between the atLeastVersion and atMostVersion. The comparison includes the versions
    * themselves is the <code>strict</code> parameter is false.
    *
    * <p>
    * For example, if <code>strict</code> is false, if the current version is 1.7.0_13, the result would be, depending on the specified Java version:
    * <ul>
    * <li>[1.7, 1.7]: as 1.7.0_13 &ge; 1.7, the method would return true</li>
    * <li>[1.7.0_14, 1.8]: as 1.7.0_13 &lt; 1.7.0_14, the method would return false</li>
    * <li>[1.7, 1.7.0_12]: as 1.7.0_13 &ge; 1.7.0_12, the method would return true</li>
    * </ul>
    *
    * If <code>strict</code> is true, if the current version is 1.8, the result would be, depending on the specified Java version:
    * <ul>
    * <li>[1.7, 1.8]: as 1.8 &eq; 1.8, the method would return false</li>
    * </ul>
    * </p>
    *
    * @param currentVersion the current Java version
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @param strict true if the at most Java version is excluded
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public static final boolean isBetweenVersions(String currentVersion, String atLeastVersion, String atMostVersion, boolean strict) {
      boolean atLeast = isVersion(currentVersion, atLeastVersion, true, false);
      boolean atMost = isVersion(currentVersion, atMostVersion, false, strict);
      return atLeast && atMost;
   }

   /**
    * Return true if the current Java version is at least equal to the specified version.
    *
    * @param currentVersion the current Java version
    * @param javaVersion the specified Java version
    * @return true if the current Java version is at least equal to the specified version
    */
   public static final boolean isAtLeastVersion(String currentVersion, String javaVersion) {
      return isVersion(currentVersion, javaVersion, true, false);
   }

   /**
    * Return true if the current Java version is at least equal to the specified version.
    *
    * @param currentVersion the current Java version
    * @param javaVersion the specified Java version
    * @param atLeast true if atLeast, false if atMost
    * @param strict true if the version is excluded
    * @return true if the current Java version is at least equal to the specified version
    */
   private static boolean isVersion(String currentVersion, String javaVersion, boolean atLeast, boolean strict) {
      //System.out.println("Comparing " + currentVersion + " with " + javaVersion);
      Matcher curMat = VERSION.matcher(currentVersion);
      Matcher mat = VERSION.matcher(javaVersion);
      // if none of the pattern match, it means that one of them is incorrect, return true by default
      if (!curMat.matches() || !mat.matches()) {
         return true;
      }
      // else we check for the first index
      // note that beginning with Java 9, it will not being with 1
      // for example:
      //  1.8.1 for Java 8
      //  9.0.1 for Java 9
      int curIndex = Integer.parseInt(curMat.group(1));
      int index = Integer.parseInt(mat.group(1));
      if (curIndex < index) {
         return false;
      } else if (curIndex > index) {
         return true;
      }

      index = 0;
      curIndex = 0;
      // then the second index, the most important of all (for example the 7 of 1.7)
      if (curMat.groupCount() >= 2) {
         String g = curMat.group(2);
         if (g != null) {
            curIndex = Integer.parseInt(g);
         }
      }
      if (mat.groupCount() >= 2) {
         String g = mat.group(2);
         if (g != null) {
            index = Integer.parseInt(g);
         }
      }
      // we will only continue to check if the indexes are identical of course
      if (curIndex < index && (atLeast) || curIndex > index && (!atLeast)) {
         return false;
      } else if (curIndex > index && atLeast || curIndex < index && (!atLeast)) {
         return true;
      }
      if (mat.groupCount() < 3) {
         return checkVersion(atLeast, strict, index, curIndex);
      }
      // in some cases, a null group can be found, it is equivalent to no group at all
      String group = mat.group(3);
      if (group == null) {
         return checkVersion(atLeast, strict, index, curIndex);
      }
      // in some cases, a null group can be found, it is equivalent to no group at all
      String curGroup = curMat.group(3);
      if (curGroup == null) {
         return checkVersion(atLeast, strict, index, curIndex);
      }
      // then the maintenance version
      curIndex = Integer.parseInt(curGroup.substring(1));
      index = Integer.parseInt(group.substring(1));
      // we will only continue to check if the indexes are identical of course
      if (curIndex < index) {
         return false;
      } else if (curIndex > index) {
         return true;
      }

      if (mat.groupCount() < 5) {
         return true;
      }
      // in some cases, a null group can be found, it is equivalent to no group at all
      group = mat.group(5);
      if (group == null) {
         return true;
      }
      if (curMat.groupCount() < 5) {
         return false;
      }
      // in some cases, a null group can be found, it is equivalent to no group at all
      curGroup = curMat.group(5);
      if (curGroup == null) {
         return false;
      }
      // now the update index
      curIndex = Integer.parseInt(curGroup.substring(1));
      index = Integer.parseInt(group.substring(1));
      return checkVersion(atLeast, strict, index, curIndex);
      // note that we don't care about the last part of the version, the identifier
   }

   private static boolean checkVersion(boolean atLeast, boolean strict, int index, int curIndex) {
      if (strict) {
         return atLeast ? curIndex > index : curIndex < index;
      } else {
         return atLeast ? curIndex >= index : curIndex <= index;
      }
   }

   /**
    * Return the Location of a Class. It will be the parent directory if the Class is not in a Jar file,
    * or the parent directory of the Jar file if it is in a Jar file.
    *
    * @param clazz the Class
    * @return the Location of a Class
    */
   public static final URL getLocation(Class<?> clazz) {
      CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
      // CodeSource is null for fundamental Classes of the JDK
      if (codeSource == null) {
         return null;
      } else {
         return codeSource.getLocation();
      }
   }
}
