// PreUpdate.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>update(E)</b>および<b>update(Iterable)</b>メソッドから
 * UPDATE SQLの実行前に<b>preUpdate</b>メソッドが呼び出されます。
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PreUpdate {
	/**
	 * UPDATE SQLの実行前に呼び出されます。
	 *
	 * @param connection コネクション･ラッパー
	 * @return このメソッドで挿入または更新した行数
	 *
	 * @throws NullPointerException <b>connection</b>がnullの場合
	 */
	int preUpdate(ConnectionWrapper connection);
}
