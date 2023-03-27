/*
Copyright (c) 2016, Herve Girod
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
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.jfxconverter.conf;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URL;

/**
 * Tests for the ConverterConfigParser class.
 *
 * @since 0.6
 */
public class ConverterConfigParserTest {

   public ConverterConfigParserTest() {
   }

   @BeforeAll
   public static void setUpClass() {
   }

   @AfterAll
   public static void tearDownClass() {
   }

   @BeforeEach
   public void setUp() {
   }

   @AfterEach
   public void tearDown() {
   }

   /**
    * Test of the configuration parsing.
    */
   @Test
   public void testParseConfiguration() throws Exception {
      System.out.println("ConverterConfigParserTest : parseConfiguration");
      URL config = this.getClass().getResource("testConfig.xml");
      ConverterConfigParser parser = new ConverterConfigParser();
      boolean result = parser.parseConfiguration(config);
      assertTrue(result, "Parsing should finish correctly");

      ConverterConfig conf = ConverterConfig.getInstance();
      assertFalse(conf.isSupportingDisabled(), "SupportDisabled");
      assertEquals(20, conf.getGrayScalePercent(), "GrayScale");
   }

}
