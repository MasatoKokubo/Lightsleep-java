/*
	Jdk.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.lightsleep.logger;

import java.util.function.Supplier;
import java.util.logging.Level;

/**
	Outputs logs using <b>java.util.logging.Logger</b>.

	@since 1.0.0
	@author Masato Kokubo
*/
public class Jdk implements Logger {
	// The logger
	private java.util.logging.Logger logger;

	/**
		Constructs a new <b>Jdk</b> with the specified name.

		@param name the name

		@throws NullPointerException <b>name</b> is <b>null</b>
	*/
	public Jdk(String name) {
		logger = java.util.logging.Logger.getLogger(name);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message) {
		logger.log(Level.FINEST, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message) {
		logger.log(Level.FINE, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message) {
		logger.log(Level.INFO, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message) {
		logger.log(Level.WARNING, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message) {
		logger.log(Level.SEVERE, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message, Throwable t) {
		logger.log(Level.FINEST, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message, Throwable t) {
		logger.log(Level.FINE, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message, Throwable t) {
		logger.log(Level.INFO, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message, Throwable t) {
		logger.log(Level.WARNING, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message, Throwable t) {
		logger.log(Level.SEVERE, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message, Throwable t) {
		logger.log(Level.SEVERE, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(Supplier<String> messageSupplier) {
		logger.log(Level.FINEST, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(Supplier<String> messageSupplier) {
		logger.log(Level.FINE, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(Supplier<String> messageSupplier) {
		logger.log(Level.INFO, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(Supplier<String> messageSupplier) {
		logger.log(Level.WARNING, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(Supplier<String> messageSupplier) {
		logger.log(Level.SEVERE, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(Supplier<String> messageSupplier) {
		logger.log(Level.SEVERE, messageSupplier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isTraceEnabled() {
		return logger.isLoggable(Level.FINEST);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isFatalEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}
}
