/*
	Logger.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.lightsleep.logger;
import java.util.function.Supplier;

/**
	An interface to perform the log output.
	It is used internally to ensure that does not depend on specific logger libraries.

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Logger {
	/**
		Outputs a message to the log at <b>trace</b> level.

		@param message a message
	*/
	void trace(String message);

	/**
		Outputs a message to the log at <b>debug</b> level.

		@param message a message
	*/
	void debug(String message);

	/**
		Outputs a message to the log at <b>info</b> level.

		@param message a message
	*/
	void info(String message);

	/**
		Outputs a message to the log at <b>warn</b> level.

		@param message a message
	*/
	void warn(String message);

	/**
		Outputs a message to the log at <b>error</b> level.

		@param message a message
	*/
	void error(String message);

	/**
		Outputs a message to the log at <b>fatal</b> level.

		@param message a message
	*/
	void fatal(String message);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>trace</b> level.

		@param message a message
		@param t a Throwable
	*/
	void trace(String message, Throwable t);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>debug</b> level.

		@param message a message
		@param t a Throwable
	*/
	void debug(String message, Throwable t);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>info</b> level.

		@param message a message
		@param t a Throwable
	*/
	void info(String message, Throwable t);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>warn</b> level.

		@param message a message
		@param t a Throwable
	*/
	void warn(String message, Throwable t);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>error</b> level.

		@param message a message
		@param t a Throwable
	*/
	void error(String message, Throwable t);

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>fatal</b> level.

		@param message a message
		@param t a Throwable
	*/
	void fatal(String message, Throwable t);

	/**
		Outputs a message to the log at <b>trace</b> level.

		@param messageSupplier the message supplier
	*/
	void trace(Supplier<String> messageSupplier);

	/**
		Outputs a message to the log at <b>debug</b> level.

		@param messageSupplier the message supplier
	*/
	void debug(Supplier<String> messageSupplier);

	/**
		Outputs a message to the log at <b>info</b> level.

		@param messageSupplier the message supplier
	*/
	void info(Supplier<String> messageSupplier);

	/**
		Outputs a message to the log at <b>warn</b> level.

		@param messageSupplier the message supplier
	*/
	void warn(Supplier<String> messageSupplier);

	/**
		Outputs a message to the log at <b>error</b> level.

		@param messageSupplier the message supplier
	*/
	void error(Supplier<String> messageSupplier);

	/**
		Outputs a message to the log at <b>fatal</b> level.

		@param messageSupplier the message supplier
	*/

	void fatal(Supplier<String> messageSupplier);


	/**
		Returns whether logging of <b>trace</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isTraceEnabled();

	/**
		Returns whether logging of <b>debug</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isDebugEnabled();

	/**
		Returns whether logging of <b>info</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isInfoEnabled();

	/**
		Returns whether logging of <b>warn</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isWarnEnabled();

	/**
		Returns whether logging of <b>error</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isErrorEnabled();

	/**
		Returns whether logging of <b>fatal</b> level is enabled.

		@return <b>true</b> if logging is enabled, <b>false</b> otherwise
	*/
	boolean isFatalEnabled();
}
