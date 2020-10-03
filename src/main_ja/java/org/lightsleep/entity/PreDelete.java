// PreDelete.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティクラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>delete(E)</b>および<b>delete(Iterable)</b>メソッドから
 * DELETE SQLの実行前に<b>preDelete</b>メソッドが呼び出されます。
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PreDelete {
    /**
     * DELETE SQLの実行前に呼び出されます。
     *
     * @param connection コネクションラッパー
     * @return このメソッドで削除した行数
     *
     * @throws NullPointerException <b>connection</b>がnullの場合
     */
    int preDelete(ConnectionWrapper connection);
}
