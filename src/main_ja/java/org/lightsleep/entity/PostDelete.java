// PostDelete.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>delete(E)</b>および<b>delete(Iterable)</b>メソッドから
 * DELETE SQLの実行後に<b>postDelete</b>メソッドが呼び出されます。
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostDelete {
	/**
	 * DELETE SQLの実行後に呼び出されます。
	 *
	 * @param connection コネクション･ラッパー
	 * @return このメソッドで削除した行数
	 *
	 * @throws NullPointerException <b>connection</b>がnullの場合
	 */
	int postDelete(ConnectionWrapper connection);
}
