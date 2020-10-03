// Jdk.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Outputs logs using <b>java.util.logging.Logger</b>.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Jdk implements Logger {
    // The logger
    private java.util.logging.Logger logger;

    /**
     * Constructs a new <b>Jdk</b> with the specified name.
     *
     * @param name the name
     *
     * @throws NullPointerException <b>name</b> is <b>null</b>
     */
    public Jdk(String name) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public void trace(String message) {
        logger.log(Level.FINEST, message);
    }

    @Override
    public void debug(String message) {
        logger.log(Level.FINE, message);
    }

    @Override
    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    @Override
    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void fatal(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void trace(String message, Throwable t) {
        logger.log(Level.FINEST, message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.log(Level.FINE, message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.log(Level.INFO, message, t);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.log(Level.WARNING, message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }

    @Override
    public void trace(Supplier<String> messageSupplier) {
        logger.log(Level.FINEST, messageSupplier);
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        logger.log(Level.FINE, messageSupplier);
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        logger.log(Level.INFO, messageSupplier);
    }

    @Override
    public void warn(Supplier<String> messageSupplier) {
        logger.log(Level.WARNING, messageSupplier);
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
        logger.log(Level.SEVERE, messageSupplier);
    }

    @Override
    public void fatal(Supplier<String> messageSupplier) {
        logger.log(Level.SEVERE, messageSupplier);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }
}
