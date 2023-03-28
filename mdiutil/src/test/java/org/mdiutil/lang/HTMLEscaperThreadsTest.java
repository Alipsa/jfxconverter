/*------------------------------------------------------------------------------
 * Copyright (C) 2021 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.lang;

import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 * Unit tests for the HTMLEscaper class.
 *
 * @since 1.2.25.2
 */
@RunWith(CategoryRunner.class)
@Category(cat = "lang")
public class HTMLEscaperThreadsTest {
   private volatile boolean isOK1 = true;
   private volatile boolean isOK2 = true;
   private volatile boolean isEnded1 = false;
   private volatile boolean isEnded2 = false;

   /**
    * Test of escapeToXML method, of class HTMLEscaper.
    * TODO: does not work on windows
    */
   @Ignore
   @Test
   public void testEscapeToXML() {
      System.out.println("HTMLEscaperThreadsTest : testEscapeText2");
      isOK1 = true;
      isOK2 = true;
      Thread th = new Thread(new Runnable() {
         @Override
         public void run() {
            for (int i = 0; i < 10000; i++) {
               String input = "The &lt;ref id=\"the reference\" /&gt;s";
               String output = "The &lt;ref id=\"the reference\" /&gt;s";
               isOK1 = isOK1 && output.equals(HTMLEscaper.escapeToXML(input, false, false));
               input = "The &eacute; text";
               output = "The é text";
               isOK1 = isOK1 && output.equals(HTMLEscaper.escapeToXML(input, true, false));
            };
            isEnded1 = true;
         }
      });
      Thread th2 = new Thread(new Runnable() {
         @Override
         public void run() {
            for (int i = 0; i < 10000; i++) {
               String input = "The &lt;ref id=\"the reference 2\" /&gt;s";
               String output = "The &lt;ref id=\"the reference 2\" /&gt;s";
               isOK2 = isOK2 && output.equals(HTMLEscaper.escapeToXML(input, false, false));
               input = "The &eacute; text";
               output = "The é text";
               isOK2 = isOK2 && output.equals(HTMLEscaper.escapeToXML(input, true, false));
            };
            isEnded2 = true;
         }
      });
      th.start();
      th2.start();
      int i = 0;
      while (i < 10) {
         try {
            Thread.sleep(100);
         } catch (InterruptedException ex) {
         }
         if (isEnded1 && isEnded2) {
            break;
         } else {
            i++;
         }
      }
      assertTrue("First thread must have a true result", isOK1);
      assertTrue("First thread must have a true result", isOK2);
   }
}
