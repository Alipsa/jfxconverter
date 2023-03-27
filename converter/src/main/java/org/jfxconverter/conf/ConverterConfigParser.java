/*
Copyright (c) 2016, 2020 Herve Girod
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A parser which populates the JFXConverter configuration with the content of a XML configuration file.
 *
 * <h1>Schema </h1>
 * <pre>
 * &lt;xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
 *   &lt;xs:element name="properties"&gt;
 *     &lt;xs:complexType&gt;
 *       &lt;xs:sequence&gt;
 *         &lt;xs:element minOccurs="0" ref="globals" /&gt;
 *       &lt;/xs:sequence&gt;
 *       &lt;xs:attribute name="version" type="xs:string" /&gt;
 *       &lt;xs:attribute name="date" type="xs:string" /&gt;
 *     &lt;/xs:complexType&gt;
 *   &lt;/xs:element&gt;
 *   &lt;xs:element name="globals"&gt;
 *     &lt;xs:complexType&gt;
 *       &lt;xs:sequence&gt;
 *         &lt;xs:choice&gt;
 *           &lt;xs:element minOccurs="0" ref="supportDisabled" /&gt;
 *           &lt;xs:element minOccurs="0" ref="grayScalePercent" /&gt;
 *         &lt;/xs:choice&gt;
 *       &lt;/xs:sequence&gt;
 *     &lt;/xs:complexType&gt;
 *   &lt;/xs:element&gt;
 *   &lt;xs:element name="supportDisabled"&gt;
 *     &lt;xs:complexType&gt;
 *       &lt;xs:attribute name="value" type="xs:boolean" use="required"/&gt;
 *     &lt;/xs:complexType&gt;
 *   &lt;/xs:element&gt;
 *   &lt;xs:element name="grayScalePercent"&gt;
 *     &lt;xs:complexType&gt;
 *       &lt;xs:attribute name="value" type="xs:nonNegativeInteger" use="required" /&gt;
 *     &lt;/xs:complexType&gt;
 *   &lt;/xs:element&gt;
 * &lt;/xs:schema&gt;
 * </pre>
 *
 * @version 0.24
 */
public class ConverterConfigParser {
   // Configuration Schema
   private static URL confXSD;

   static {

   }

   public ConverterConfigParser() {
      confXSD = getClass().getResource("config.xsd");
   }

   /**
    * Parse the configuration.
    *
    * @param config the configuration.
    * @return true if the parsing succeeded
    * @throws java.io.IOException if the URL could not be accesed
    */
   public boolean parseConfiguration(URL config) throws IOException {
      ConfigurationHandler handler = new ConfigurationHandler();
      SchemaFactory scFac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      try {
         Schema schema = scFac.newSchema(confXSD);
         Validator validator = schema.newValidator();
         validator.setErrorHandler(handler);
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(config.openStream()))) {
            InputSource source = new InputSource(reader);
            SAXSource saxSource = new SAXSource(source);
            validator.validate(saxSource);
         }

         SAXParser parser = getSAXParser();
         try (InputStream stream = config.openStream()) {
            parser.parse(stream, handler);
         }
         return true;
      } catch (ParserConfigurationException | SAXException e) {
         return false;
      }
   }

   private SAXParser getSAXParser() throws ParserConfigurationException, SAXException {
      SAXParserFactory fac = SAXParserFactory.newInstance();
      fac.setValidating(true);
      SAXParser parser = fac.newSAXParser();
      return parser;
   }

   /**
    * SAX handler for the configuration XML file.
    */
   private class ConfigurationHandler extends DefaultHandler {
      private ConverterConfig conf = ConverterConfig.getInstance();

      public ConfigurationHandler() {
         super();

      }

      @Override
      public void startElement(String uri, String localname, String qname, Attributes attr) {
         if (qname.equals("supportDisabled")) {
            parseSupportDisabled(attr);
         } else if (qname.equals("grayScalePercent")) {
            parseGrayScalePercent(attr);
         }
      }

      private void parseSupportDisabled(Attributes attr) {
         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("value")) {
               conf.setSupportDisabled(attrvalue.equals("true"));
            }
         }
      }

      private void parseGrayScalePercent(Attributes attr) {
         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("value")) {
               try {
                  int percent = Integer.parseInt(attrvalue);
                  conf.setGrayScalePercent(percent);
               } catch (NumberFormatException e) {
               }
            }
         }
      }
   }
}
