// MissingPropertyException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

/**
 * 式に存在しないプロパティが参照された場合にこの例外がスローされます。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class MissingPropertyException extends RuntimeException {
    /**
     * 指定の詳細メッセージで新規<b>MissingPropertyException</b>を構築します。
     *
     * @param message 詳細メッセージ
     *
     * @since 2.0.0
     */
    public MissingPropertyException(String message) {
        super(message);
    }
}
