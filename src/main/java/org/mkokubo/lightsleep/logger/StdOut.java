/*
	StdOutLogger.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.mkokubo.lightsleep.logger;

import java.sql.Timestamp;
import java.util.function.Supplier;

/**
	Outputs logs to the standard output.

	@since 1.0.0
	@author Masato Kokubo
*/
public class StdOut implements Logger {
	// The message format
	private static String messageFormat = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s ";

	/**
		Constructs a new <b>StdOut</b> with the specified name.

		@param name the name

		@throws NullPointerException <b>name</b> is <b>null</b>
	*/
	public StdOut(String name) {
		if (name == null) throw new NullPointerException("StdOut.<init>: name == null");
	}

	/**
		Outputs a message to the log at <b>level</b>.

		@param level the level
		@param message a message
	*/
	private void println(String level, String message) {
		System.out.println(
			String.format(messageFormat, new Timestamp(System.currentTimeMillis()), level)
			+ message);
	}

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>level</b>.

		@param level the level
		@param message a message
		@param t a Throwable
	*/
	private void println(String level, String message, Throwable t) {
		println(level, message + " " + t.toString());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message) {
		println("TRACE", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message) {
		println("DEBUG", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message) {
		println("INFO ", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message) {
		println("WARN ", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message) {
		println("ERROR", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message) {
		println("FATAL", message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message, Throwable t) {
		println("TRACE", message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message, Throwable t) {
		println("DEBUG", message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message, Throwable t) {
		println("INFO ",message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message, Throwable t) {
		println("WARN ", message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message, Throwable t) {
		println("ERROR", message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message, Throwable t) {
		println("FATAL", message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(Supplier<String> messageSupplier) {
		trace(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(Supplier<String> messageSupplier) {
		debug(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(Supplier<String> messageSupplier) {
		info(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(Supplier<String> messageSupplier) {
		warn(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(Supplier<String> messageSupplier) {
		error(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(Supplier<String> messageSupplier) {
		fatal(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isFatalEnabled() {
		return true;
	}
}
