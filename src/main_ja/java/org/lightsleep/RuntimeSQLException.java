// RuntimeSQLException.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

/**
 * このライブラリでは、<b>RuntimeSQLException</b>を<b>SQLException</b>の代わりに使用します。
 * データベースのアクセス中に<b>SQLException</b>がスローされた場合、この例外に置き換えてスローします。
 * 元の<b>SQLException</b>は、<b>cause</b>に格納されます。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class RuntimeSQLException extends RuntimeException {
    /**
     * <b>RuntimeSQLException</b>を構築します。
     *
     * @param cause 原因(不明の場合はnull)
     */
    public RuntimeSQLException(Throwable cause) {
    }

    /**
     * <b>RuntimeSQLException</b>を構築します。
     *
     * @param message 詳細メッセージ
     * @param cause 原因(不明の場合はnull)
     *
     * @since 2.1.1
     */
    public RuntimeSQLException(String message, Throwable cause) {
    }
}
