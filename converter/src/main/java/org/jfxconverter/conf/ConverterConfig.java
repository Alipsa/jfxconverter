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

/**
 * The JFXConverter configuration. It is possible to set directly the conversion configuration parameters by
 * this class or by parsing an XML configuration URL with the {@link ConverterConfigParser} class.
 *
 * @since 0.6
 */
public class ConverterConfig {
   private static ConverterConfig conf = null;
   private boolean isSupportingDisabled = true;
   private int grayScalePercent = 40;

   private ConverterConfig() {
   }

   /**
    * Unique access to the ConverterConfiguration.
    *
    * @return the ConverterConfiguration
    */
   public static ConverterConfig getInstance() {
      if (conf == null) {
         conf = new ConverterConfig();
      }
      return conf;
   }

   /**
    * Resets the configuration.
    */
   public void reset() {
      isSupportingDisabled = true;
      grayScalePercent = 40;
   }

   /**
    * Set if the Disabled state for Nodes is supported. If supported, the color of the Nodes will be
    * gray-scaled.
    *
    * @param isSupportingDisabled true if the Disabled state for Nodes is supported
    */
   public void setSupportDisabled(boolean isSupportingDisabled) {
      this.isSupportingDisabled = isSupportingDisabled;
   }

   /**
    * Return true if the Disabled state for Nodes is supported. If supported, the color of the Nodes will be
    * gray-scaled. It is supported by default.
    *
    * @return true true if the Disabled state for Nodes is supported
    */
   public boolean isSupportingDisabled() {
      return isSupportingDisabled;
   }

   /**
    * Set the gray-scale parcent for the disabled Nodes if the Disabled state for Nodes is supported.
    *
    * @param percent the gray-scale parcent for the disabled Nodes
    */
   public void setGrayScalePercent(int percent) {
      this.grayScalePercent = percent;
   }

   /**
    * Return the gray-scale parcent for the disabled Nodes if the Disabled state for Nodes is supported.
    * It is 40 by default.
    *
    * @return the gray-scale parcent for the disabled Nodes
    */
   public int getGrayScalePercent() {
      return grayScalePercent;
   }
}
