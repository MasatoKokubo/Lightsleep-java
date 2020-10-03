// ManyRowsException.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

/**
 * 単一行を取得するメソッドで複数行が検索された場合にこの例外がスローされます。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class ManyRowsException extends RuntimeException {
    /**
     * 新規<b>ManyRowsException</b>を構築します。
     */
    public ManyRowsException() {
    }

    /**
     * 指定の詳細メッセージで新規<b>ManyRowsException</b>を構築します。
     *
     * @param message 詳細メッセージ
     *
     * @since 1.5.0
     */
    public ManyRowsException(String message) {
    }
}
