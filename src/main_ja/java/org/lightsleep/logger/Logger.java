/*
	Logger.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.lightsleep.logger;
import java.util.function.Supplier;

/**
	ログに出力を行うインタフェースです。
	特定のログに出力ライブラリに依存しないようにするため内部的に使用します。

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Logger {
	/**
		trace レベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void trace(String message);

	/**
		trace レベルでエラーをログに出力します。

		@param message メッセージ
		@param t 例外
	*/
	void trace(String message, Throwable t);

	/**
		debug ログレベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void debug(String message);

	/**
		debug レベルでエラーをログに出力します。

		@param message メッセージ
		@param t 例外
	*/
	void debug(String message, Throwable t);

	/**
		info レベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void info(String message);

	/**
		info レベルでエラーをログに出力します。

		@param message メッセージ
		@param t 例外
	*/
	void info(String message, Throwable t);

	/**
		warn レベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void warn(String message);

	/**
		warn レベルでエラーをログに出力します。 

		@param message メッセージ
		@param t 例外
	*/
	void warn(String message, Throwable t);

	/**
		error レベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void error(String message);

	/**
		error レベルでエラーをログに出力します。

		@param message メッセージ
		@param t 例外
	*/
	void error(String message, Throwable t);

	/**
		fatal レベルでメッセージをログに出力します。

		@param message メッセージ
	*/
	void fatal(String message);

	/**
		fatal レベルでエラーをログに出力します。

		@param message メッセージ
		@param t 例外
	*/
	void fatal(String message, Throwable t);

	/**
		trace レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー
	*/
	void trace(Supplier<String> messageSupplier);

	/**
		debug レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー
	*/
	void debug(Supplier<String> messageSupplier);

	/**
		info レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー
	*/
	void info(Supplier<String> messageSupplier);

	/**
		warn レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー
	*/
	void warn(Supplier<String> messageSupplier);

	/**
		error レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー
	*/
	void error(Supplier<String> messageSupplier);

	/**
		fatal レベルでメッセージをログに出力します。

		@param messageSupplier メッセージサプライアー

	*/
	void fatal(Supplier<String> messageSupplier);

	/**
		trace レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isTraceEnabled();

	/**
		debug レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isDebugEnabled();

	/**
		info レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isInfoEnabled();

	/**
		warn レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isWarnEnabled();

	/**
		error レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isErrorEnabled();

	/**
		fatal レベルのログ出力が有効かどうかを返します。

		@return ログ出力が有効な場合 <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isFatalEnabled();
}
