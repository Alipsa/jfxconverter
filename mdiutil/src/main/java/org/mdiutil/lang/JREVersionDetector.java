/*------------------------------------------------------------------------------
 * Copyright (C) 2018, 2019, 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mdiutil.io.StreamGobbler;

/**
 * This class allows to get the version of a JRE at a specified location, or check that this version is at least equal
 * to a specified version, or between two specified versions.
 *
 * The version is identical to the String returned if calling <code>System.getProperty("java.version")</code> on the JRE at
 * the specified location.
 *
 * Note that the location is the location of the JRE. The class will find by itself the path of the corresponding
 * Java executable, depending on the platform.
 *
 * @version 1.2.39.2
 */
public class JREVersionDetector {
   /**
    * The type for all architectures.
    */
   public static final short ARCH_ALL = 0;
   /**
    * The type for 32 bits architectures.
    */
   public static final short ARCH_32BIT = 1;
   /**
    * The type for 64 bits architectures.
    */
   public static final short ARCH_64BIT = 2;
   private static final Pattern PAT_VERSION = Pattern.compile("java|openjdk version \"([0-9\\.A-Za-z_]+)\"");
   // TODO: does not work on openjdk
   private static final Pattern PAT_ARCH = Pattern.compile("java ([0-9\\-\\(\\)a-z_]+) ([0-9\\-\\(\\)a-z_]+) .*");


   private static long DEFAULT_TIME_OUT;
   private boolean isVersion = false;
   private Process process = null;
   private CountDownLatch latch = null;
   private String atLeastVersion = null;
   private String atMostVersion = null;
   private boolean strict = false;
   private volatile int arch = ARCH_ALL;
   private volatile boolean isArch = true;
   private volatile int countLines = 0;
   private long timeOut = DEFAULT_TIME_OUT;

   static {
      resetToOriginalDefaultTimeout();
   }



   /**
    * Constructor, with a default timeOut of 200 ms.
    */
   public JREVersionDetector() {
   }

   /**
    * Constructor.
    *
    * @param timeOut the timeOut in ms
    */
   public JREVersionDetector(long timeOut) {
      this.timeOut = timeOut;
   }

   /**
    * Set the default timeOut.
    *
    * @param timeOut the timeOut in ms
    */
   public static void setDefaultTimeOut(long timeOut) {
      DEFAULT_TIME_OUT = timeOut;
   }

   /**
    * Return the default timeOut.
    *
    * @return the default timeOut in ms
    */
   public static long getDefaultTimeOut() {
      return DEFAULT_TIME_OUT;
   }

   public static void resetToOriginalDefaultTimeout() {
      DEFAULT_TIME_OUT = 200;
   }

   /**
    * Set the timeOut.
    *
    * @param timeOut the timeOut in ms
    */
   public void setTimeOut(long timeOut) {
      this.timeOut = timeOut;
   }

   /**
    * Return the timeOut.
    *
    * @return the timeOut in ms
    */
   public long getTimeOut() {
      return timeOut;
   }

   /**
    * Return the version of the JRE at a specified location. The version is identical to the String returned if calling
    * <code>System.getProperty("java.version")</code> on the JRE at the specified location.
    *
    * @param jvmLocation the JRE location
    * @return the version
    */
   public String getVersion(File jvmLocation) {
      if (jvmLocation == null) {
         return null;
      }
      String exe;
      if (SystemUtils.isUnixPlatform()) {
         exe = "java";
      } else {
         exe = "java.exe";
      }
      String javaBin = jvmLocation.getAbsolutePath() + File.separator + "bin" + File.separator + exe;
      System.out.println("Running " + javaBin + " -version");
      ProcessBuilder builder = new ProcessBuilder(javaBin, "-version");
      try {
         process = builder.start();
         latch = new CountDownLatch(1);
         redirectProcessIO2(process);
         latch.await(timeOut, TimeUnit.MILLISECONDS);
         return atLeastVersion;
      } catch (IOException | InterruptedException ex) {
         return null;
      }
   }

   /**
    * Return true if the version of the JRE at a specified location is at least equal to the specified version. See
    * {@link SystemUtils#isAtLeastVersion(String, String)}.
    *
    * @param jvmLocation the JRE location
    * @param version the specified Java version
    * @return true if the version of the JRE at a specified location is at least equal to the specified version
    */
   public boolean isAtLeastVersion(File jvmLocation, String version) {
      return isBetweenVersions(jvmLocation, version, null);
   }

   /**
    * Return true if the version of the JRE at a specified location is at least equal to the specified version. See
    * {@link SystemUtils#isAtLeastVersion(String, String)}.
    *
    * Note: the architecture argument can have one of the following values:
    * <ul>
    * <li>{@link #ARCH_32BIT}: for a 32 bit architecture</li>
    * <li>{@link #ARCH_64BIT}: for a 64 bit architecture</li>
    * <li>{@link #ARCH_ALL}: for any architecture</li>
    * </ul>
    *
    * @param jvmLocation the JRE location
    * @param version the specified Java version
    * @param arch the architecture
    * @return true if the version of the JRE at a specified location is at least equal to the specified version
    */
   public boolean isAtLeastVersion(File jvmLocation, String version, short arch) {
      return isBetweenVersions(jvmLocation, version, null, arch, false);
   }

   /**
    * Return true if the version of the JRE at a specified location is between the atLeastVersion and atMostVersion. The comparison includes the versions
    * themselves. See {@link SystemUtils#isBetweenVersions(String, String, String)}.
    *
    * @param jvmLocation the JRE location
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public boolean isBetweenVersions(File jvmLocation, String atLeastVersion, String atMostVersion) {
      return isBetweenVersions(jvmLocation, atLeastVersion, atMostVersion, false);
   }

   /**
    * Return true if the version of the JRE at a specified location is between the atLeastVersion and atMostVersion.
    * The comparison includes the versions themselves is the <code>strict</code> parameter is false.
    * See {@link SystemUtils#isBetweenVersions(String, String, String, boolean)}.
    *
    * @param jvmLocation the JRE location
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @param strict true if the at most Java version is excluded
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public boolean isBetweenVersions(File jvmLocation, String atLeastVersion, String atMostVersion, boolean strict) {
      return isBetweenVersions(jvmLocation, atLeastVersion, atMostVersion, ARCH_ALL, strict);
   }

   /**
    * Return true if the version of the JRE at a specified location is between the atLeastVersion and atMostVersion.
    * The comparison includes the versions themselves is the <code>strict</code> parameter is false.
    * See {@link SystemUtils#isBetweenVersions(String, String, String, boolean)}.
    *
    * Note: the architecture argument can have one of the following values:
    * <ul>
    * <li>{@link #ARCH_32BIT}: for a 32 bit architecture</li>
    * <li>{@link #ARCH_64BIT}: for a 64 bit architecture</li>
    * <li>{@link #ARCH_ALL}: for any architecture</li>
    * </ul>
    *
    * @param jvmLocation the JRE location
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @param arch the architecture
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public boolean isBetweenVersions(File jvmLocation, String atLeastVersion, String atMostVersion, short arch) {
      return isBetweenVersions(jvmLocation, atLeastVersion, atMostVersion, arch, false);
   }

   /**
    * Return true if the version of the JRE at a specified location is between the atLeastVersion and atMostVersion.
    * The comparison includes the versions themselves is the <code>strict</code> parameter is false.
    * See {@link SystemUtils#isBetweenVersions(String, String, String, boolean)}.
    *
    * Note: the architecture argument can have one of the following values:
    * <ul>
    * <li>{@link #ARCH_32BIT}: for a 32 bit architecture</li>
    * <li>{@link #ARCH_64BIT}: for a 64 bit architecture</li>
    * <li>{@link #ARCH_ALL}: for any architecture</li>
    * </ul>
    *
    * @param jvmLocation the JRE location
    * @param atLeastVersion the specified at least Java version
    * @param atMostVersion the specified at most Java version
    * @param arch the architecture
    * @param strict true if the at most Java version is excluded
    * @return true if the current Java version is between the atLeastVersion and atMostVersion
    */
   public boolean isBetweenVersions(File jvmLocation, String atLeastVersion, String atMostVersion, short arch, boolean strict) {
      if (jvmLocation == null) {
         return false;
      }
      this.atLeastVersion = atLeastVersion;
      this.atMostVersion = atMostVersion;
      this.strict = strict;
      this.arch = arch;
      String java = SystemUtils.isWindowsPlatform() ? "java.exe" : "java";
      String javaBin = jvmLocation.getAbsolutePath() + File.separator + "bin" + File.separator + java;
      ProcessBuilder builder = new ProcessBuilder(javaBin, "-version");
      try {
         process = builder.start();
         latch = new CountDownLatch(1);
         redirectProcessIO(process);
         latch.await(timeOut, TimeUnit.MILLISECONDS);
         return isVersion;
      } catch (IOException | InterruptedException ex) {
         return false;
      }
   }

   private void redirectProcessIO(final Process process) {
      isArch = true;
      countLines = 0;
      StreamGobbler errorgobbler = new StreamGobbler(process.getErrorStream());
      errorgobbler.setListener(new StreamGobbler.Listener() {
         @Override
         public void readLine(String line) {
            line = line.toLowerCase();
            Matcher m = PAT_VERSION.matcher(line);
            if (m.matches()) {
               String theVersion = m.group(1);
               if (atMostVersion == null) {
                  isVersion = SystemUtils.isAtLeastVersion(theVersion, atLeastVersion);
               } else {
                  isVersion = SystemUtils.isBetweenVersions(theVersion, atLeastVersion, atMostVersion, strict);
               }
               countLines++;
            } else if (arch != ARCH_ALL) {
               if (line.contains("64-bit") || line.contains("32-bit")) {
                  if ((line.contains("64-bit") && arch != ARCH_64BIT) || (line.contains("32-bit") && arch != ARCH_32BIT)) {
                     isArch = false;
                  }
                  countLines++;
               }
            }
            if (countLines == 2 || (countLines == 1 && arch == ARCH_ALL)) {
               isVersion = isVersion & isArch;
               latch.countDown();
               process.destroyForcibly();
            }
         }

         @Override
         public void close() {
         }
      });
      errorgobbler.start();
   }

   private void redirectProcessIO2(final Process process) {
      StreamGobbler errorgobbler = new StreamGobbler(process.getErrorStream());
      errorgobbler.setListener(new StreamGobbler.Listener() {
         @Override
         public void readLine(String line) {
            line = line.toLowerCase();
            Matcher m = PAT_VERSION.matcher(line);
            if (m.matches()) {
               atLeastVersion = m.group(1);
            }
            latch.countDown();
            process.destroyForcibly();
         }

         @Override
         public void close() {
         }
      });
      errorgobbler.start();
   }
}
