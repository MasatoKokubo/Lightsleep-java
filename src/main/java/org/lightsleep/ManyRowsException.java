// ManyRowsException.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

/**
 * This exception is thrown if more than one row is retrieved in methods to get a single row.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class ManyRowsException extends RuntimeException {
    /**
     * Constructs a new <b>ManyRowsException</b> with null as its detail message.
     */
    public ManyRowsException() {
    }

    /**
     * Constructs a new <b>ManyRowsException</b> with the specified detail message.
     *
     * @param message the detail message
     *
     * @since 1.5.0
     */
    public ManyRowsException(String message) {
        super(message);
    }
}
