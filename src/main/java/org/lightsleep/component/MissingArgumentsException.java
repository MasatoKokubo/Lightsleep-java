// MissingArgumentsException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

/**
 * This exception if the number of arguments does not match the number of placements in the expression.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class MissingArgumentsException extends RuntimeException {
	/**
	 * Constructs a new <b>MissingArgumentsException</b> with the specified detail message.
	 *
	 * @param message the detail message
	 *
	 * @since 2.0.0
	 */
	public MissingArgumentsException(String message) {
		super(message);
	}
}
