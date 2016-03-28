/*
	RuntimeSQLException.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep;

/**
	In this library, Uses RuntimeSQLException instead of SQLException.
	If a SQLException is thrown while accessing the database, replaces it with this exception and throws.
	Original SQLException is stored as the cause.

	@since 1.0

	@author Masato Kokubo
*/
@SuppressWarnings("serial")
public class RuntimeSQLException extends RuntimeException {
	/**
		Constructs a new RuntimeSQLException.

		@param cause the cause (or null if unknown)
	*/
	public RuntimeSQLException(Throwable cause) {
		super(cause);
	}
}
