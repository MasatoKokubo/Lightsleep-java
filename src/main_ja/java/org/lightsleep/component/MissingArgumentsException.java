// MissingArgumentsException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

/**
 * 式のプレースメントと引数の数が一致しない場合にこの例外がスローされます。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class MissingArgumentsException extends RuntimeException {
    /**
     * 指定の詳細メッセージで新規<b>MissingArgumentsException</b>を構築します。
     *
     * @param message 詳細メッセージ
     *
     * @since 2.0.0
     */
    public MissingArgumentsException(String message) {
        super(message);
    }
}
