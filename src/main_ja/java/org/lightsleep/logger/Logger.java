// Logger.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;
import java.util.function.Supplier;

/**
 * ログに出力を行うインタフェースです。
 * 特定のログに出力ライブラリに依存しないようにするため内部的に使用します。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Logger {
    /**
     * traceレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void trace(String message);

    /**
     * traceレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void trace(String message, Throwable t);

    /**
     * debugレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void debug(String message);

    /**
     * debugレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void debug(String message, Throwable t);

    /**
     * infoレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void info(String message);

    /**
     * infoレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void info(String message, Throwable t);

    /**
     * warnレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void warn(String message);

    /**
     * warnレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void warn(String message, Throwable t);

    /**
     * errorレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void error(String message);

    /**
     * errorレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void error(String message, Throwable t);

    /**
     * fatalレベルでメッセージをログに出力します。
     *
     * @param message メッセージ
     */
    void fatal(String message);

    /**
     * fatalレベルでエラーをログに出力します。
     *
     * @param message メッセージ
     * @param t 例外
     */
    void fatal(String message, Throwable t);

    /**
     * traceレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void trace(Supplier<String> messageSupplier);

    /**
     * debugレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void debug(Supplier<String> messageSupplier);

    /**
     * infoレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void info(Supplier<String> messageSupplier);

    /**
     * warnレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void warn(Supplier<String> messageSupplier);

    /**
     * errorレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void error(Supplier<String> messageSupplier);

    /**
     * fatalレベルでメッセージをログに出力します。
     *
     * @param messageSupplier メッセージサプライアー
     */
    void fatal(Supplier<String> messageSupplier);

    /**
     * traceレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isTraceEnabled();

    /**
     * debugレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isDebugEnabled();

    /**
     * infoレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isInfoEnabled();

    /**
     * warnレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isWarnEnabled();

    /**
     * errorレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isErrorEnabled();

    /**
     * fatalレベルのログ出力が有効かどうかを返します。
     *
     * @return ログ出力が有効の場合は<b>true</b>、そうでなければ<b>false</b>
     */
    boolean isFatalEnabled();
}
