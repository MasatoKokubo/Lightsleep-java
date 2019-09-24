// PreInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>insert(E)</b>および<b>insert(Iterable)</b>メソッドから
 * INSERT SQLの実行前に<b>preInsert</b>メソッドが呼び出されます。
 *
 * <p>
 * <b>preInsert</b>メソッドを使用して、プライマリー･キーの採番の実装をする事ができます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * public abstract class Common implements <b>PreInsert</b> {
 *  {@literal @}Key
 *   public int id;
 *
 *  {@literal @}Override
 *   <b>public void preInsert(Connection connection)</b> {
 *     id = Numbering.getNewId(connection, getClass());
 *   }
 * }
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface PreInsert {
	/**
	 * INSERT SQLの実行前に呼び出されます。
	 *
	 * @param connection コネクション･ラッパー
	 *
	 * @throws NullPointerException <b>connection</b>がnullの場合
	 */
	public void preInsert(ConnectionWrapper connection);
}
