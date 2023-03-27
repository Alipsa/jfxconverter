// © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html#License
/**
 *******************************************************************************
 * Copyright (C) 2005-2016, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package org.mdiutil.io.charset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mdiutil.io.FileUtilities2;

/**
 * <code>CharsetDetector</code> provides a facility for detecting the
 * charset or encoding of character data in an unknown format.
 * The input data can either be from an input stream or an array of bytes.
 * The result of the detection operation is a list of possibly matching
 * charsets, or, for simple use, you can just ask for a Java Reader that
 * will will work over the input data.
 * <p>
 * Character set detection is at best an imprecise operation. The detection
 * process will attempt to identify the charset that best matches the characteristics
 * of the byte data, but the process is partly statistical in nature, and
 * the results can not be guaranteed to always be correct.
 * <p>
 * For best accuracy in charset detection, the input data should be primarily
 * in a single language, and a minimum of a few hundred bytes worth of plain text
 * in the language are needed. The detection process will attempt to
 * ignore html or xml style markup that could otherwise obscure the content.
 *
 * @since 0.9.22
 */
public class CharsetDetector {
   private static final int BUF_SIZE = 8000;
   /*
     *  The following items are accessed by individual CharsetRecongizers during
     *     the recognition process
    */
   // The text to be checked.  Markup will have been
   //   removed if appropriate.
   byte[] fInputBytes = new byte[BUF_SIZE];

   // Length of the byte data in fInputBytes.
   int fInputLen;

   // byte frequency statistics for the input text.
   //   Value is percent, not absolute.
   //   Value is rounded up, so zero really means zero occurences.
   short fByteStats[] = new short[256];

   // True if any bytes in the range 0x80 - 0x9F are in the input;
   boolean fC1Bytes = false;

   String fDeclaredEncoding;

   byte[] fRawInput;     // Original, untouched input bytes.
   //  If user gave us a byte array, this is it.
   //  If user gave us a stream, it's read to a
   //  buffer here.
   int fRawLength;    // Length of data in fRawInput array.

   InputStream fInputStream;  // User's input stream, or null if the user
   //   gave us a byte array.

   //
   //  If true, setText() will strip tags from input text.
   //
   private boolean fStripTags = false;

   private boolean[] fEnabledRecognizers;   // If not null, active set of charset recognizers had
   // been changed from the default. The array index is
   // corresponding to ALL_RECOGNIZER. See setDetectableCharset().

//   Question: Should we have getters corresponding to the setters for input text
//   and declared encoding?
//   A thought: If we were to create our own type of Java Reader, we could defer
//   figuring out an actual charset for data that starts out with too much English
//   only ASCII until the user actually read through to something that didn't look
//   like 7 bit English.  If  nothing else ever appeared, we would never need to
//   actually choose the "real" charset.  All assuming that the application just
//   wants the data, and doesn't care about a char set name.
   /**
    * Constructor.
    */
   public CharsetDetector() {
   }

   /**
    * Return the guessed encoding of an URL.
    *
    * @param url the URL
    * @return the guessed CharsetMatch of the URL
    */
   public CharsetMatch getCharsetMatch(URL url) throws IOException {
      try (InputStream stream = url.openStream()) {
         int length = (int) FileUtilities2.getLength(url);
         byte[] content = new byte[length];
         stream.read(content);
         setText(content);
         CharsetMatch match = detect();
         return match;
      }
   }

   /**
    * Return the guessed encoding of a file.
    *
    * @param file the file
    * @return the guessed CharsetMatch of the file
    */
   public CharsetMatch getCharsetMatch(File file) throws IOException {
      try (FileInputStream stream = new FileInputStream(file)) {
         byte[] content = new byte[(int) file.length()];
         stream.read(content);
         setText(content);
         CharsetMatch match = detect();
         return match;
      }
   }

   /**
    * Return the guessed encoding of a file.
    *
    * @param file the file
    * @return the guessed CharsetMatch of the file
    */
   public static CharsetMatch detectCharsetMatch(File file) throws IOException {
      try (FileInputStream stream = new FileInputStream(file)) {
         CharsetDetector detector;
         byte[] content = new byte[(int) file.length()];
         stream.read(content);
         detector = new CharsetDetector();
         detector = detector.setText(content);
         CharsetMatch match = detector.detect();
         return match;
      }
   }

   /**
    * Return the guessed encoding of an URL.
    *
    * @param url the URL
    * @return the guessed CharsetMatch of the URL
    */
   public static CharsetMatch detectCharsetMatch(URL url) throws IOException {
      try (InputStream stream = url.openStream()) {
         CharsetDetector detector;
         int length = (int) FileUtilities2.getLength(url);
         byte[] content = new byte[length];
         stream.read(content);
         detector = new CharsetDetector();
         detector = detector.setText(content);
         CharsetMatch match = detector.detect();
         return match;
      }
   }

   /**
    * Set the declared encoding for charset detection.
    * The declared encoding of an input text is an encoding obtained
    * from an http header or xml declaration or similar source that
    * can be provided as additional information to the charset detector.
    * A match between a declared encoding and a possible detected encoding
    * will raise the quality of that detected encoding by a small delta,
    * and will also appear as a "reason" for the match.
    * <p>
    * A declared encoding that is incompatible with the input data being
    * analyzed will not be added to the list of possible encodings.
    *
    * @param encoding The declared encoding
    *
    * @return this
    */
   public CharsetDetector setDeclaredEncoding(String encoding) {
      fDeclaredEncoding = encoding;
      return this;
   }

   /**
    * Set the input text (byte) data whose charset is to be detected.
    *
    * @param in the input text of unknown encoding
    * @return This CharsetDetector
    */
   public CharsetDetector setText(byte[] in) {
      fRawInput = in;
      fRawLength = in.length;

      return this;
   }

   /**
    * Set the input text (byte) data whose charset is to be detected.
    * <p>
    * The input stream that supplies the character data must have markSupported()
    * == true; the charset detection process will read a small amount of data,
    * then return the stream to its original position via
    * the InputStream.reset() operation. The exact amount that will
    * be read depends on the characteristics of the data itself.
    *
    * @param in the input text of unknown encoding
    *
    * @return This CharsetDetector
    */
   public CharsetDetector setText(InputStream in) throws IOException {
      fInputStream = in;
      fInputStream.mark(BUF_SIZE);
      fRawInput = new byte[BUF_SIZE];   // Always make a new buffer because the
      //   previous one may have come from the caller,
      //   in which case we can't touch it.
      fRawLength = 0;
      int remainingLength = BUF_SIZE;
      while (remainingLength > 0) {
         // read() may give data in smallish chunks, esp. for remote sources.  Hence, this loop.
         int bytesRead = fInputStream.read(fRawInput, fRawLength, remainingLength);
         if (bytesRead <= 0) {
            break;
         }
         fRawLength += bytesRead;
         remainingLength -= bytesRead;
      }
      fInputStream.reset();

      return this;
   }

   /**
    * Return the charset that best matches the supplied input data.
    *
    * Note though, that because the detection
    * only looks at the start of the input data,
    * there is a possibility that the returned charset will fail to handle
    * the full set of input data.
    * <p>
    * Raise an exception if
    * <ul>
    * <li>no charset appears to match the data.</li>
    * <li>no input text has been provided</li>
    * </ul>
    *
    * @return a CharsetMatch object representing the best matching charset, or
    * <code>null</code> if there are no matches.
    */
   public CharsetMatch detect() {
//   TODO:  A better implementation would be to copy the detect loop from
//          detectAll(), and cut it short as soon as a match with a high confidence
//          is found.  This is something to be done later, after things are otherwise
//          working.
      CharsetMatch matches[] = detectAll();

      if (matches == null || matches.length == 0) {
         return null;
      }

      return matches[0];
   }

   /**
    * Return an array of all charsets that appear to be plausible
    * matches with the input data. The array is ordered with the
    * best quality match first.
    * <p>
    * Raise an exception if
    * <ul>
    * <li>no charsets appear to match the input data.</li>
    * <li>no input text has been provided</li>
    * </ul>
    *
    * @return An array of CharsetMatch objects representing possibly matching charsets.
    */
   public CharsetMatch[] detectAll() {
      ArrayList<CharsetMatch> matches = new ArrayList<CharsetMatch>();

      MungeInput();  // Strip html markup, collect byte stats.

      //  Iterate over all possible charsets, remember all that
      //    give a match quality > 0.
      for (int i = 0; i < ALL_CS_RECOGNIZERS.size(); i++) {
         CSRecognizerInfo rcinfo = ALL_CS_RECOGNIZERS.get(i);
         boolean active = (fEnabledRecognizers != null) ? fEnabledRecognizers[i] : rcinfo.isDefaultEnabled;
         if (active) {
            CharsetMatch m = rcinfo.recognizer.match(this);
            if (m != null) {
               matches.add(m);
            }
         }
      }
      Collections.sort(matches);      // CharsetMatch compares on confidence
      Collections.reverse(matches);   //  Put best match first.
      CharsetMatch[] resultArray = new CharsetMatch[matches.size()];
      resultArray = matches.toArray(resultArray);
      return resultArray;
   }

   /**
    * Autodetect the charset of an inputStream, and return a Java Reader
    * to access the converted input data.
    * <p>
    * This is a convenience method that is equivalent to
    * <code>this.setDeclaredEncoding(declaredEncoding).setText(in).detect().getReader();</code>
    * <p>
    * For the input stream that supplies the character data, markSupported()
    * must be true; the charset detection will read a small amount of data,
    * then return the stream to its original position via
    * the InputStream.reset() operation. The exact amount that will
    * be read depends on the characteristics of the data itself.
    * <p>
    * Raise an exception if no charsets appear to match the input data.
    *
    * @param in The source of the byte data in the unknown charset.
    * @param declaredEncoding A declared encoding for the data, if available,
    * or null or an empty string if none is available.
    * @return the reader
    */
   public Reader getReader(InputStream in, String declaredEncoding) {
      fDeclaredEncoding = declaredEncoding;

      try {
         setText(in);

         CharsetMatch match = detect();

         if (match == null) {
            return null;
         }

         return match.getReader();
      } catch (IOException e) {
         return null;
      }
   }

   /**
    * Autodetect the charset of an inputStream, and return a String
    * containing the converted input data.
    * <p>
    * This is a convenience method that is equivalent to
    * <code>this.setDeclaredEncoding(declaredEncoding).setText(in).detect().getString();</code>
    * <p>
    * Raise an exception if no charsets appear to match the input data.
    *
    * @param in The source of the byte data in the unknown charset.
    *
    * @param declaredEncoding A declared encoding for the data, if available,
    * or null or an empty string if none is available.
    * @return the string
    */
   public String getString(byte[] in, String declaredEncoding) {
      fDeclaredEncoding = declaredEncoding;

      try {
         setText(in);

         CharsetMatch match = detect();

         if (match == null) {
            return null;
         }

         return match.getString(-1);
      } catch (IOException e) {
         return null;
      }
   }

   /**
    * Get the names of all charsets supported by <code>CharsetDetector</code> class.
    * <p>
    * <b>Note:</b> Multiple different charset encodings in a same family may use
    * a single shared name in this implementation. For example, this method returns
    * an array including "ISO-8859-1" (ISO Latin 1), but not including "windows-1252"
    * (Windows Latin 1). However, actual detection result could be "windows-1252"
    * when the input data matches Latin 1 code points with any points only available
    * in "windows-1252".
    *
    * @return an array of the names of all charsets supported by
    * <code>CharsetDetector</code> class.
    */
   public static String[] getAllDetectableCharsets() {
      String[] allCharsetNames = new String[ALL_CS_RECOGNIZERS.size()];
      for (int i = 0; i < allCharsetNames.length; i++) {
         allCharsetNames[i] = ALL_CS_RECOGNIZERS.get(i).recognizer.getName();
      }
      return allCharsetNames;
   }

   /**
    * Test whether or not input filtering is enabled.
    *
    * @return <code>true</code> if input text will be filtered.
    * @see #enableInputFilter
    */
   public boolean inputFilterEnabled() {
      return fStripTags;
   }

   /**
    * Enable filtering of input text. If filtering is enabled,
    * text within angle brackets ("&lt;" and "&gt;") will be removed
    * before detection.
    *
    * @param filter <code>true</code> to enable input text filtering.
    * @return The previous setting
    */
   public boolean enableInputFilter(boolean filter) {
      boolean previous = fStripTags;

      fStripTags = filter;

      return previous;
   }

   /*
     *  MungeInput - after getting a set of raw input data to be analyzed, preprocess
     *               it by removing what appears to be html markup.
    */
   private void MungeInput() {
      int srci = 0;
      int dsti = 0;
      byte b;
      boolean inMarkup = false;
      int openTags = 0;
      int badTags = 0;

      //
      //  html / xml markup stripping.
      //     quick and dirty, not 100% accurate, but hopefully good enough, statistically.
      //     discard everything within < brackets >
      //     Count how many total '<' and illegal (nested) '<' occur, so we can make some
      //     guess as to whether the input was actually marked up at all.
      if (fStripTags) {
         for (srci = 0; srci < fRawLength && dsti < fInputBytes.length; srci++) {
            b = fRawInput[srci];
            if (b == (byte) '<') {
               if (inMarkup) {
                  badTags++;
               }
               inMarkup = true;
               openTags++;
            }

            if (!inMarkup) {
               fInputBytes[dsti++] = b;
            }

            if (b == (byte) '>') {
               inMarkup = false;
            }
         }

         fInputLen = dsti;
      }

      //
      //  If it looks like this input wasn't marked up, or if it looks like it's
      //    essentially nothing but markup abandon the markup stripping.
      //    Detection will have to work on the unstripped input.
      //
      if (openTags < 5 || openTags / 5 < badTags
         || (fInputLen < 100 && fRawLength > 600)) {
         int limit = fRawLength;

         if (limit > BUF_SIZE) {
            limit = BUF_SIZE;
         }

         for (srci = 0; srci < limit; srci++) {
            fInputBytes[srci] = fRawInput[srci];
         }
         fInputLen = srci;
      }

      //
      // Tally up the byte occurence statistics.
      //   These are available for use by the various detectors.
      //
      Arrays.fill(fByteStats, (short) 0);
      for (srci = 0; srci < fInputLen; srci++) {
         int val = fInputBytes[srci] & 0x00ff;
         fByteStats[val]++;
      }

      fC1Bytes = false;
      for (int i = 0x80; i <= 0x9F; i += 1) {
         if (fByteStats[i] != 0) {
            fC1Bytes = true;
            break;
         }
      }
   }

   private static class CSRecognizerInfo {
      CharsetRecognizer recognizer;
      boolean isDefaultEnabled;

      CSRecognizerInfo(CharsetRecognizer recognizer, boolean isDefaultEnabled) {
         this.recognizer = recognizer;
         this.isDefaultEnabled = isDefaultEnabled;
      }
   }

   /*
     * List of recognizers for all charsets known to the implementation.
    */
   private static final List<CSRecognizerInfo> ALL_CS_RECOGNIZERS;

   static {
      List<CSRecognizerInfo> list = new ArrayList<>();

      list.add(new CSRecognizerInfo(new CharsetRecog_UTF8(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_Unicode.CharsetRecog_UTF_16_BE(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_Unicode.CharsetRecog_UTF_16_LE(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_Unicode.CharsetRecog_UTF_32_BE(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_Unicode.CharsetRecog_UTF_32_LE(), true));

      list.add(new CSRecognizerInfo(new CharsetRecog_mbcs.CharsetRecog_sjis(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_2022.CharsetRecog_2022JP(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_2022.CharsetRecog_2022CN(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_2022.CharsetRecog_2022KR(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_gb_18030(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_jp(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_kr(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_mbcs.CharsetRecog_big5(), true));

      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_1(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_2(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_5_ru(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_6_ar(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_7_el(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_8_I_he(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_8_he(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_windows_1251(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_windows_1256(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_KOI8_R(), true));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_8859_9_tr(), true));

      // IBM 420/424 recognizers are disabled by default
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_rtl(), false));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_ltr(), false));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_rtl(), false));
      list.add(new CSRecognizerInfo(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_ltr(), false));

      ALL_CS_RECOGNIZERS = Collections.unmodifiableList(list);
   }
}
