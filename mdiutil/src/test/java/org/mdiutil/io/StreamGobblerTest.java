/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mdiutil.io;

import static org.junit.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Category;
import org.mdiutil.junit.CategoryRunner;

/**
 *
 * @since 0.9.45
 */
@RunWith(CategoryRunner.class)
@Category(cat = "io")
public class StreamGobblerTest {

   public StreamGobblerTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of the StreamGobbler class.
    */
   @Test
   public void testGobbler() {
      System.out.println("StreamGobblerTest : testGobbler");
      InputStream stream = this.getClass().getResourceAsStream("resources/file2.txt");
      BufferedInputStream bstream = new BufferedInputStream(stream);
      StreamGobbler gobbler = new StreamGobbler(bstream);
      GobblerListener listener = new GobblerListener();
      gobbler.setListener(listener);
      gobbler.start();
      try {
         Thread.sleep(100);
      } catch (InterruptedException ex) {
      }
      assertEquals("Gobbler listener content", 3, listener.lines.size());
   }

   private static class GobblerListener implements StreamGobbler.Listener {
      private final List<String> lines = new ArrayList<>();

      @Override
      public void readLine(String line) {
         lines.add(line);
      }

      @Override
      public void close() {
      }

   }
}
