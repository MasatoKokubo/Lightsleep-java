// MissingPropertyException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

/**
 * This exception is thrown if a property that does not exist in the expression is referenced.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class MissingPropertyException extends RuntimeException {
    /**
     * Constructs a new <b>MissingPropertyException</b> with the specified detail message.
     *
     * @param message the detail message
     *
     * @since 2.0.0
     */
    public MissingPropertyException(String message) {
        super(message);
    }
}
