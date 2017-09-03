// PreInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.sql.Connection;

/**
 * エンティティ・クラスがこのインターフェースを実装している場合、
 * <b>Sql</b> クラスの <b>insert</b> メソッドで、
 * INSERT SQL 実行前に <b>preInsert</b> メソッドがコールされます。<br>
 *
 * <p>
 * <b>preInsert</b> メソッドでは、プライマリー・キーの採番の実装等を行います。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * public abstract class Common implements <b>PreInsert</b> {
 *  {@literal @}Key
 *   public int id;
 *     ...
 *
 *  {@literal @}Override
 *   <b>public int preInsert(Connection conn)</b> {
 *     id = Numbering.getNewId(conn, getClass());
 *     return 0;
 *   }
 * }
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface PreInsert {
	/**
	 * <b>preInsert</b> は行が挿入される前に実行されます。
	 *
	 * @param connection データベース・コネクション
	 * @return 挿入された行数
	 *
	 * @throws NullPointerException <b>connection</b> が null の場合
	 */
	int preInsert(Connection connection);
}
