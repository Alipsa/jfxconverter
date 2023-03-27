/*------------------------------------------------------------------------------
 * Copyright (C) 2019, 2020 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

/**
 * The process manager is able to manage external processes.
 *
 * @version 1.2.5
 */
public class ProcessManager {
   private short platformType = SystemUtils.OS_OTHER;
   private PlatformImpl platformImpl = null;
   private boolean filterSystem = true;
   private long timeOut = -1;
   private int timeOutCount = 1;
   private static final Timer TIMER = new Timer();

   public ProcessManager() {
      platformType = SystemUtils.getPlatformType();
      switch (platformType) {
         case SystemUtils.OS_WINDOWS:
            platformImpl = new Windows();
            break;
         case SystemUtils.OS_LINUX:
            platformImpl = new Linux();
            break;
         case SystemUtils.OS_MACOSX:
            platformImpl = new Linux();
            break;
         case SystemUtils.OS_UNIX:
            platformImpl = new Linux();
            break;
         default:
            break;
      }
   }

   /**
    * Set a timeOut for the killing of a process. The manager will wait to check if the process have been killed.
    *
    * @param timeOut the timeOut
    * @see #kill(ExternalProcess)
    */
   public void setKillProcessTimeOut(long timeOut) {
      this.timeOut = timeOut;
   }

   /**
    * Return a timeOut for the killing of a process. The manager will wait until this timeOut to check if the process have been killed.
    *
    * @return the timeOut
    * @see #setKillProcessTimeOut(long)
    */
   public long getKillProcessTimeOut() {
      return timeOut;
   }

   /**
    * Set a timeOut count for the killing of a process.
    *
    * @param timeOutCount the timeOutCount
    * @see #kill(ExternalProcess)
    */
   public void setKillProcessTimeOutCount(int timeOutCount) {
      if (timeOutCount >= 1) {
         this.timeOutCount = timeOutCount;
      }
   }

   /**
    * Return the timeOut count for the killing of a process.
    *
    * @return the timeOutCount
    * @see #kill(ExternalProcess)
    */
   public int getKillProcessTimeOutCount() {
      return timeOutCount;
   }

   /**
    * Filter system processes when trying to kill them. If the argument is true, then the {@link #kill(ExternalProcess)}
    * ethod will not kill processes which are:
    * <ul>
    * <li>"Services" processes on Windows</li>
    * <li>"root" processes on Linux</li>
    * </ul>
    * Note that by default system processes are filtered
    *
    * @param filterSystem true if filtering system processes when trying to kill them
    */
   public void filterSystem(boolean filterSystem) {
      this.filterSystem = filterSystem;
   }

   /**
    * Return true if system processes are filtered when trying to kill them.
    * Note that by default system processes are filtered
    *
    * @return true if filtering system processes when trying to kill them
    */
   public boolean isFilteringSystem() {
      return filterSystem;
   }

   /**
    * Return true if the current platform is handled. Currently Windows, Linux, MAC OS X, and UNIX are handled.
    *
    * @return true if the current platform is handled
    */
   public boolean isHandlingPlatform() {
      return platformImpl != null;
   }

   /**
    * Return true if a process is currently running.
    *
    * @param pid the process pid
    * @return true if the process is currently running
    */
   public boolean isRunning(int pid) {
      if (platformImpl != null) {
         return platformImpl.isRunning(pid);
      } else {
         return false;
      }
   }

   /**
    * Return true if a process is currently running. The name is normally the name of the executable.
    *
    * @param processName the process name
    * @return true if the process is currently running
    */
   public boolean isRunning(String processName) {
      if (platformImpl != null) {
         return platformImpl.isRunning(processName);
      } else {
         return false;
      }
   }

   /**
    * Return the map of running processes, sorted by their creation date. The time is consistent with the <code>System.currentTimeMillis()</code> method.
    *
    * @return the map of running processes
    */
   public SortedMap<Long, List<ExternalProcess>> getProcessByCreationDate() {
      return getProcessByCreationDate(null);
   }

   /**
    * Return the map of running processes, sorted by their creation date. The time is consistent with the <code>System.currentTimeMillis()</code> method.
    *
    * @param processName the process name
    * @return the map of running processes of this name
    */
   public SortedMap<Long, List<ExternalProcess>> getProcessByCreationDate(String processName) {
      if (platformImpl != null) {
         List<ExternalProcess> processes = getProcesses(processName);
         SortedMap<Long, List<ExternalProcess>> map = new TreeMap<>();
         Iterator<ExternalProcess> it = processes.iterator();
         while (it.hasNext()) {
            ExternalProcess process = it.next();
            List<ExternalProcess> list;
            if (map.containsKey(process.getTime())) {
               list = map.get(process.getTime());
            } else {
               list = new ArrayList<>();
               map.put(process.getTime(), list);
            }
            list.add(process);
         }
         return map;
      } else {
         return new TreeMap<>();
      }
   }

   /**
    * Return the map of running processes, sorted by their process name.
    *
    * @return the map of running processes
    */
   public Map<String, List<ExternalProcess>> getProcessMap() {
      if (platformImpl != null) {
         List<ExternalProcess> processes = getProcesses(null);
         Map<String, List<ExternalProcess>> map = new HashMap<>();
         Iterator<ExternalProcess> it = processes.iterator();
         while (it.hasNext()) {
            ExternalProcess process = it.next();
            List<ExternalProcess> list;
            if (map.containsKey(process.getName())) {
               list = map.get(process.getName());
            } else {
               list = new ArrayList<>();
               map.put(process.getName(), list);
            }
            list.add(process);
         }
         return map;
      } else {
         return new HashMap<>();
      }
   }

   /**
    * Return the list of running processes which have a specific proces name. For example, if you have several Java processes currently
    * running on Windows, calling this method with "java.exe" will return a list with all of the Java processes actually running.
    *
    * @param processName the process name
    * @return the list of running processes (can be an empty list)
    */
   public List<ExternalProcess> getProcesses(String processName) {
      if (platformImpl != null) {
         List<ExternalProcess> processes = platformImpl.getProcesses(processName);
         return processes;
      } else {
         return new ArrayList<>();
      }
   }

   /**
    * Return the list of all running processes.
    *
    * @return the list of running processes
    */
   public List<ExternalProcess> getProcesses() {
      return getProcesses(null);
   }

   /**
    * Return the first running proces which has a specific proces name.
    *
    * @param processName the process name
    * @return the of proces (can be null if there is no running process with this name)
    */
   public ExternalProcess getProcess(String processName) {
      if (platformImpl != null) {
         List<ExternalProcess> processes = platformImpl.getProcesses(processName);
         if (processes.isEmpty()) {
            return null;
         } else {
            return processes.get(0);
         }
      } else {
         return null;
      }
   }

   /**
    * Kill an external process. The manager will wait to check if the process have been killed.
    *
    * <h1>Algorithm</h1>
    * The algorithm which will be used by the manager to kill the process is the following:
    * <ul>
    * <li>If {@link #getKillProcessTimeOut()} &le; 0, then no wait will be performed after the execution of the KILL command</li>
    * <li>else:
    * <ul>
    * <li>For i = 1 to {@link #getKillProcessTimeOutCount()}:
    * <ul>
    * <li>Wait until {@link #getKillProcessTimeOut()}</li>
    * <li>If the process is not running anymore, then exit</li>
    * </ul>
    * </li>
    * </ul>
    * </li>
    * </ul>
    *
    * @param process the process
    * @param recursive true if the children processes must be killed too
    * @return true if the process could be killed
    */
   public boolean kill(ExternalProcess process, boolean recursive) {
      if (platformImpl != null) {
         return platformImpl.kill(process, recursive);
      } else {
         return false;
      }
   }

   /**
    * Kill an external process without its child processes. The manager will wait to check if the process have been killed.
    *
    * @param process the process
    * @return true if the process could be killed
    */
   public boolean kill(ExternalProcess process) {
      return kill(process, false);
   }

   private abstract class PlatformImpl {
      /**
       * Return true if a process is currently running. The name is normally the name of the executable.
       *
       * @param processName the process name
       * @return true if the process is currently running
       */
      protected abstract boolean isRunning(String processName);

      /**
       * Return true if a process is currently running.
       *
       * @param pid the process pid
       * @return true if the process is currently running
       */
      protected abstract boolean isRunning(int pid);

      /**
       * Return the list of running processes which have a specific proces name.
       *
       * @param processName the process name
       * @return the list of running processes (can be an empty list)
       */
      protected abstract List<ExternalProcess> getProcesses(String processName);

      private boolean kill(ExternalProcess process, boolean recursive) {
         boolean killedCalled = platformImpl.killImpl(process, recursive);
         if (!killedCalled) {
            return false;
         } else if (timeOut > 0) {
            return waitForKilled(process);
         } else {
            return true;
         }
      }

      /**
       * Kill the children processes of a process.
       *
       * @param process the process
       * @throws IOException
       */
      protected void killChildrenImpl(ExternalProcess process) throws IOException {
         List<ExternalProcess> children = process.getChildren();
         Iterator<ExternalProcess> it = children.iterator();
         while (it.hasNext()) {
            ExternalProcess child = it.next();
            killImpl(child, true);
         }
      }

      private boolean waitForKilled(ExternalProcess process) {
         boolean isRunning = true;
         for (int i = 0; i < timeOutCount; i++) {
            final Semaphore s = new Semaphore(1);
            TimerTask task = new TimerTask() {
               public void run() {
                  s.release();
               }
            };

            TIMER.schedule(task, timeOut); //this starts the task
            try {
               s.acquire();
            } catch (InterruptedException ex) {
               task.cancel();
            }
            isRunning = process.isRunning();
            if (!isRunning) {
               break;
            }
         }
         return !isRunning;
      }

      /**
       * Kill an external process.
       *
       * @param process the process
       * @return true if the process could be killed (ie it was found)
       */
      protected abstract boolean killImpl(ExternalProcess process, boolean recursive);
   }

   private class Windows extends PlatformImpl {
      private static final String TASKLIST = "tasklist";
      private static final String KILL = "taskkill /F /IM ";
      private static final String WMIC = "wmic process get processid,parentprocessid,creationdate";

      /**
       * Return true if a process is currently running.
       *
       * @param pid the process pid
       * @return true if the process is currently running
       */
      @Override
      public boolean isRunning(int pid) {
         try {
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (line.trim().isEmpty()) {
                  continue;
               }
               StringTokenizer tok = new StringTokenizer(line, " ");
               tok.nextToken(); // the name
               String pidS = tok.nextToken();
               try {
                  int _pid = Integer.parseInt(pidS);
                  if (pid == _pid) {
                     return true;
                  }
               } catch (NumberFormatException e) {
               }
            }
         } catch (IOException e) {
         }
         return false;
      }

      @Override
      public boolean isRunning(String processName) {
         try {
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (line.contains(processName)) {
                  return true;
               }
            }
         } catch (IOException e) {
         }
         return false;
      }

      @Override
      public List<ExternalProcess> getProcesses(String processName) {
         List<ExternalProcess> list = new ArrayList<>();
         try {
            Map<Integer, ExternalProcess> processes = new HashMap<>();
            Process p = Runtime.getRuntime().exec(TASKLIST);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
               String line;
               while ((line = reader.readLine()) != null) {
                  if (processName == null || line.contains(processName)) {
                     if (line.trim().isEmpty()) {
                        continue;
                     }
                     StringTokenizer tok = new StringTokenizer(line, " ");
                     String theName = tok.nextToken();
                     String pidS = tok.nextToken();
                     String systemS = tok.nextToken();
                     boolean isSystem = systemS.equalsIgnoreCase("Services");
                     try {
                        int pid = Integer.parseInt(pidS);
                        ExternalProcess process = new ExternalProcess(this, theName, pid, isSystem);
                        list.add(process);
                        processes.put(process.getPID(), process);
                     } catch (NumberFormatException e) {
                     }
                  }
               }
            }

            p = Runtime.getRuntime().exec(WMIC);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
               String line;
               while ((line = reader.readLine()) != null) {
                  StringTokenizer tok = new StringTokenizer(line, " ");
                  if (line.isEmpty() || tok.countTokens() < 3) {
                     continue;
                  }
                  String wmicDate = tok.nextToken();
                  String parentidS = tok.nextToken();
                  String pidS = tok.nextToken();
                  try {
                     long time = DateConverter.convertWMIC(wmicDate);
                     int parentid = Integer.parseInt(parentidS);
                     int pid = Integer.parseInt(pidS);
                     if (processes.containsKey(pid)) {
                        ExternalProcess child = processes.get(pid);
                        child.setTime(time);
                        if (processes.containsKey(parentid)) {
                           ExternalProcess parent = processes.get(parentid);
                           child.setParentProcess(parent);
                        }
                     }
                  } catch (NumberFormatException e) {
                  }
               }
            }
         } catch (IOException e) {
         }
         return list;
      }

      @Override
      public boolean killImpl(ExternalProcess process, boolean recursive) {
         if (process.isSystemProcess() && filterSystem) {
            return false;
         }
         try {
            Runtime.getRuntime().exec(KILL + process.getName());
            if (recursive) {
               killChildrenImpl(process);
            }
            return true;
         } catch (IOException e) {
            return false;
         }
      }
   }

   private class Linux extends PlatformImpl {
      private static final String TASKLIST = "ps xao user,pid,ppid,lstart,comm";
      private static final String TASKLIST_RUNNING = "ps aux";
      private static final String KILL = "kill -9";

      /**
       * Return true if a process is currently running.
       *
       * @param pid the process pid
       * @return true if the process is currently running
       */
      @Override
      public boolean isRunning(int pid) {
         try {
            Process p = Runtime.getRuntime().exec(TASKLIST_RUNNING);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (line.trim().isEmpty()) {
                  continue;
               }
               StringTokenizer tok = new StringTokenizer(line, " ");
               tok.nextToken(); // the user
               tok.nextToken(); // the name
               String pidS = tok.nextToken();
               try {
                  int _pid = Integer.parseInt(pidS);
                  if (pid == _pid) {
                     return true;
                  }
               } catch (NumberFormatException e) {
               }
            }
         } catch (IOException e) {
         }
         return false;
      }

      @Override
      public boolean isRunning(String processName) {
         try {
            Process p = Runtime.getRuntime().exec(TASKLIST_RUNNING);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (line.contains(processName)) {
                  return true;
               }
            }
         } catch (IOException e) {
         }
         return false;
      }

      @Override
      public List<ExternalProcess> getProcesses(String processName) {
         List<ExternalProcess> list = new ArrayList<>();
         Map<Integer, ExternalProcess> mapP = new HashMap<>();
         Map<Integer, Integer> toParent = new HashMap<>();
         try {
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (processName == null || line.contains(processName)) {
                  if (line.trim().isEmpty()) {
                     continue;
                  }
                  if (line.contains("USER") && line.contains("STARTED")) {
                     // this is the header line, skip it
                     continue;
                  }
                  StringTokenizer tok = new StringTokenizer(line, " ");
                  String rootS = tok.nextToken(); // user
                  boolean isSystem = rootS.equalsIgnoreCase("root");
                  String pidS = tok.nextToken(); // pid
                  String ppidS = tok.nextToken(); // ppid
                  tok.nextToken(); // day name
                  String monthS = tok.nextToken(); // month name
                  String dayS = tok.nextToken(); // day of the month
                  String hms = tok.nextToken();  // time as hour:minutes:seconds
                  String yearS = tok.nextToken();  // year

                  String theName = tok.nextToken(); // comm

                  try {
                     int pid = Integer.parseInt(pidS);
                     int ppid = Integer.parseInt(ppidS);
                     long time = DateConverter.convertFromlStart(monthS, dayS, hms, yearS);
                     ExternalProcess process = new ExternalProcess(this, theName, pid, isSystem);
                     process.setTime(time);
                     list.add(process);
                     if (pid != 0) {
                        mapP.put(pid, process);
                     }
                     if (ppid != 0 && pid != 0) {
                        toParent.put(pid, ppid);
                     }
                  } catch (NumberFormatException e) {
                  }
               }
            }
         } catch (IOException e) {
         }
         Iterator<Integer> it = mapP.keySet().iterator();
         while (it.hasNext()) {
            int pid = it.next();
            ExternalProcess process = mapP.get(pid);
            if (toParent.containsKey(pid)) {
               int ppid = toParent.get(pid);
               if (mapP.containsKey(ppid)) {
                  ExternalProcess parent = mapP.get(ppid);
                  process.setParentProcess(parent);
               }
            }
         }
         return list;
      }

      @Override
      public boolean killImpl(ExternalProcess process, boolean recursive) {
         if (process.isSystemProcess() && filterSystem) {
            return false;
         }
         try {
            Runtime.getRuntime().exec(KILL + process.getName());
            if (recursive) {
               killChildrenImpl(process);
            }
            return true;
         } catch (IOException e) {
            return false;
         }
      }

   }

   /**
    * Represents an external process.
    *
    * @version 1.2.5
    */
   public class ExternalProcess {
      private int pid = -1;
      private boolean isSystem = false;
      private PlatformImpl managerImpl = null;
      private String name = null;
      private long time = 0;
      private ExternalProcess parent = null;
      private final List<ExternalProcess> children = new ArrayList<>();

      private ExternalProcess(PlatformImpl managerImpl, String name, int pid, boolean isSystem) {
         this.pid = pid;
         this.managerImpl = managerImpl;
         this.name = name;
         this.isSystem = isSystem;
      }

      private void setTime(long time) {
         this.time = time;
      }

      private void setParentProcess(ExternalProcess parent) {
         this.parent = parent;
         parent.children.add(this);
      }

      /**
       * Return the children processes.
       *
       * @return the children processes
       */
      public List<ExternalProcess> getChildren() {
         return children;
      }

      /**
       * Return the parent process.
       *
       * @return the parent process
       */
      public ExternalProcess getParent() {
         return parent;
      }

      /**
       * Return true if the process is a system process ("Services" on Windows, "root" on Linux).
       *
       * @return true if the process is a system process
       */
      public boolean isSystemProcess() {
         return isSystem;
      }

      /**
       * Return the upstart time in ms since EPOCH for this process. The precision is for the moment milliseconds on Windows and seconds on other platforms.
       * This value is consistent with <code>System.currentTimeMillis()</code>.
       *
       * @return the upstart time in ms since EPOCH for this process
       */
      public long getTime() {
         return time;
      }

      /**
       * Return true if the process is currently running.
       *
       * @return true if the process is currently running
       */
      public boolean isRunning() {
         return managerImpl.isRunning(pid);
      }

      /**
       * Kill this process, without killing the children processes. The manager will wait to check if the process have been killed.
       *
       * @return true if the process could be killed (ie it was found)
       * @see ProcessManager#kill(ExternalProcess)
       */
      public boolean kill() {
         return managerImpl.kill(this, false);
      }

      /**
       * Kill this process. The manager will wait to check if the process have been killed.
       *
       * @param recursive true if the children processes lmsut be killed too
       * @return true if the process could be killed (ie it was found)
       * @see ProcessManager#kill(ExternalProcess)
       */
      public boolean kill(boolean recursive) {
         return managerImpl.kill(this, recursive);
      }

      /**
       * Return the process name.
       *
       * @return the process name
       */
      public String getName() {
         return name;
      }

      /**
       * Return the process PID.
       *
       * @return the process PID
       */
      public int getPID() {
         return pid;
      }
   }
}
