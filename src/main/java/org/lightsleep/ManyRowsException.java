/*
	ManyRowsException.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep;

/**
	Throws this exception when more than one row is retrieved in methods to get a single row.

	@since 1.0

	@author Masato Kokubo
*/
@SuppressWarnings("serial")
public class ManyRowsException extends RuntimeException {
	/**
		Constructs a new ManyRowsException.
	*/
	public ManyRowsException() {
	}
}
