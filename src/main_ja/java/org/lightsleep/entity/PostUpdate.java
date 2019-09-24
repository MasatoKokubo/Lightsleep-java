// PostUpdate.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>update(E)</b>および<b>update(Iterable)</b>メソッドから
 * UPDATE SQLの実行後に<b>postUpdate</b>メソッドが呼び出されます。
 * 
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostUpdate {
	/**
	 * UPDATE SQLの実行後に呼び出されます。
	 *
	 * @param connection コネクション･ラッパー
	 * @return このメソッドで挿入または更新した行数
	 *
	 * @throws NullPointerException <b>connection</b>がnullの場合
	 */
	int postUpdate(ConnectionWrapper connection);
}
