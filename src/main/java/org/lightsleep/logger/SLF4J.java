/*
	SLF4J.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.lightsleep.logger;

import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

/**
	Outputs logs using
	<a href="http://www.slf4j.org" target="other">SLF4J</a>.

	@since 1.0.0
	@author Masato Kokubo
*/
public class SLF4J implements Logger {
	// The logger
	private org.slf4j.Logger logger;

	/**
		Constructs a new <b>SLF4J</b> with the specified name.

		@param name the name

		@throws NullPointerException <b>name</b> is <b>null</b>
	*/
	public SLF4J(String name) {
		logger = LoggerFactory.getLogger(name);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message) {
		logger.trace(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message) {
		logger.info(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message) {
		logger.error(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message) {
		logger.error(message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message, Throwable t) {
		logger.trace(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message, Throwable t) {
		logger.debug(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message, Throwable t) {
		logger.info(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message, Throwable t) {
		logger.warn(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message, Throwable t) {
		logger.error(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message, Throwable t) {
		logger.error(message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(Supplier<String> messageSupplier) {
		if (isTraceEnabled())
			logger.trace(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(Supplier<String> messageSupplier) {
		if (isDebugEnabled())
			logger.debug(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(Supplier<String> messageSupplier) {
		if (isInfoEnabled())
			logger.info(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(Supplier<String> messageSupplier) {
		if (isWarnEnabled())
			logger.warn(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(Supplier<String> messageSupplier) {
		if (isErrorEnabled())
			logger.error(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(Supplier<String> messageSupplier) {
		if (isErrorEnabled())
			logger.error(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isFatalEnabled() {
		return logger.isErrorEnabled();
	}
}
