// Log4j2.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

import java.util.function.Supplier;

/**
 * <a href="http://logging.apache.org/log4j/2.x/" target="other">Log4J 2</a>
 * を使用してログを出力します。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Log4j2 implements Logger {
    /**
     * 指定の名前で<b>Logger</b>を構築します。
     *
     * @param name 名前
     *
     * @throws NullPointerException <b>name</b>がnullの場合
     */
    public Log4j2(String name) {
    }

    @Override
    public void trace(String message) {
    }

    @Override
    public void debug(String message) {
    }

    @Override
    public void info(String message) {
    }

    @Override
    public void warn(String message) {
    }

    @Override
    public void error(String message) {
    }

    @Override
    public void fatal(String message) {
    }

    @Override
    public void trace(String message, Throwable t) {
    }

    @Override
    public void debug(String message, Throwable t) {
    }

    @Override
    public void info(String message, Throwable t) {
    }

    @Override
    public void warn(String message, Throwable t) {
    }

    @Override
    public void error(String message, Throwable t) {
    }

    @Override
    public void fatal(String message, Throwable t) {
    }

    @Override
    public void trace(Supplier<String> messageSupplier) {
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
    }

    @Override
    public void warn(Supplier<String> messageSupplier) {
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
    }

    @Override
    public void fatal(Supplier<String> messageSupplier) {
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public boolean isFatalEnabled() {
        return false;
    }
}
