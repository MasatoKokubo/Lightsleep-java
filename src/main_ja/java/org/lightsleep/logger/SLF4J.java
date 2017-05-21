// SLF4J.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

import java.util.function.Supplier;

/**
 * <a href="http://www.slf4j.org" target="other">SLF4J</a>
 * を使用してログを出力します。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class SLF4J implements Logger {
	/**
	 * 指定の名前で <b>Logger</b> を構築します。
	 *
	 * @param name 名前
	 *
	 * @throws NullPointerException <b>name</b> が null の場合
	 */
	public SLF4J(String name) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(String message, Throwable t) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(Supplier<String> messageSupplier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFatalEnabled() {
		return false;
	}
}
