/*------------------------------------------------------------------------------
* Copyright (C) 2006 Herve Girod
* 
* Distributable under the terms of either the Apache License (Version 2.0) or 
* the GNU Lesser General Public License, as specified in the COPYING file.
------------------------------------------------------------------------------*/
package org.mdiutil.xml;

import java.io.IOException;

/**
 * Thrown when an XMLWriter receives an illegal argument in parameter.
 *
 * @version 0.2
 */
public class XmlWriterIOException extends IOException {
    /** The enclosed exception. */
    private IOException embedded;

    /**
     * Constructs a new <code>XmlWriterIOException</code> with the
     * specified detail message.
     * @param s the detail message of this exception
     */
    public XmlWriterIOException(String s) {
        this(s, null);
    }

    /**
     * Constructs a new <code>XmlWriterIOException</code> with the
     * specified detail message.
     * @param ex the enclosed exception
     */
    public XmlWriterIOException(IOException ex) {
        this(null, ex);
    }

    /**
     * Constructs a new <code>XmlWriterIOException</code> with the
     * specified detail message.
     * @param s the detail message of this exception
     * @param ex the original exception
     */
    public XmlWriterIOException(String s, IOException ex) {
        super(s);
        embedded = ex;
    }

    /**
     * Returns the message of this exception. If an error message has
     * been specified, returns that one. Otherwise, return the error message
     * of enclosed exception or null if any.
     */
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        } else if (embedded != null) {
            return embedded.getMessage();
        } else {
            return null;
        }
    }

    /**
     * Returns the original enclosed exception or null if any.
     */
    public IOException getException() {
        return embedded;
    }
}

