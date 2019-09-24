// PostInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>insert(E)</b>および<b>insert(Iterable)</b>メソッドから
 * INSERT SQLの実行後に<b>postInsert</b>メソッドが呼び出されます。
 *
 * <p>
 * <b>postInsert</b>メソッドを使用して、挿入時に自動採番された値を取得する事ができます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * public abstract class Common implements <b>PostInsert</b> {
 *  {@literal @}Key
 *  {@literal @}NonInsert
 *   public int id;
 *
 *  {@literal @}Override
 *   <b>public void postInsert(ConnectionWrapper conn)</b> {
 *     Class&lt;? extends Common&gt; entityClass = getClass();
 *     if (PostSelect.class.isAssignableFrom(entityClass))
 *       entityClass = (Class&lt;? extends Common&gt;)entityClass.getSuperclass();
 *     new Sql&lt;&gt;(entityClass)
 *       .columns("id")
 *       .where("id=",
 *         new Sql&lt;&gt;(entityClass)
 *           .columns("id")
 *           .expression("id", "MAX({id})")
 *       )
 *       .connection(conn)
 *       .select(entity -&gt; id = entity.id);
 *   }
 * }
 *
 * public class Contact extends Common {
 *   public String firstName;
 *   public String lastName;
 *   public Date birthday;
 * }
 * </pre></div>
 *
 * <div class="exampleTitle"><span>INSERT時に生成されるSQL/Java</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO Contact (firstName, lastName, birthday) VALUES ('Yukari', 'Apple', DATE'2001-01-10')
 * SELECT id FROM Contact WHERE id= (SELECT MAX(id) id FROM Contact)
 * </pre></div>
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostInsert {
	/**
	 * INSERT SQLの実行後に呼び出されます。
	 *
	 * @param connection コネクション･ラッパー
	 * @return このメソッドで挿入または更新した行数
	 *
	 * @throws NullPointerException <b>connection</b>がnullの場合
	 */
	int postInsert(ConnectionWrapper connection);
}
