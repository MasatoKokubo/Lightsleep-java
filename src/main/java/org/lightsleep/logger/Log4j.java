// Log4j.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

import java.util.function.Supplier;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;

/**
 * Outputs logs using
 * <a href="http://logging.apache.org/log4j/1.2/" target="other">Log4J</a>.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Log4j implements Logger {
	// The logger
	private org.apache.log4j.Logger logger;

	/**
	 * Constructs a new <b>Log4j</b> with the specified name.
	 *
	 * @param name the name
	 *
	 * @throws NullPointerException <b>name</b> is null
	 */
	public Log4j(String name) {
		logger = LogManager.getLogger(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(String message) {
		logger.trace(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(String message) {
		logger.info(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(String message) {
		logger.error(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(String message) {
		logger.fatal(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(String message, Throwable t) {
		logger.trace(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(String message, Throwable t) {
		logger.debug(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(String message, Throwable t) {
		logger.info(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(String message, Throwable t) {
		logger.warn(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(String message, Throwable t) {
		logger.error(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(String message, Throwable t) {
		logger.fatal(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(Supplier<String> messageSupplier) {
		if (isTraceEnabled())
			logger.trace(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(Supplier<String> messageSupplier) {
		if (isDebugEnabled())
			logger.debug(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(Supplier<String> messageSupplier) {
		if (isInfoEnabled())
			logger.info(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(Supplier<String> messageSupplier) {
		if (isWarnEnabled())
			logger.warn(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(Supplier<String> messageSupplier) {
		if (isErrorEnabled())
			logger.error(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(Supplier<String> messageSupplier) {
		if (isFatalEnabled())
			logger.fatal(messageSupplier.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWarnEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isErrorEnabled() {
		return logger.isEnabledFor(Level.ERROR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFatalEnabled() {
		return logger.isEnabledFor(Level.FATAL);
	}
}
