// Composite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.sql.Connection;

/**
 * エンティティ・クラスがこのインターフェースを実装している場合、
 * <b>Sql</b> クラスの
 * <b>select</b>, <b>insert</b>,
 * <b>update</b> または <b>delete</b> メソッドで、
 * 各 SQL の実行後にエンティティ・クラスの <b>postSelect</b>, <b>postInsert</b>,
 * <b>postUpdate</b> または <b>postDelete</b> メソッドがコールされます。<br>
 *
 * <p>
 * ただし <b>update</b>, <b>delete</b>
 * メソッドで、引数にエンティティがない場合は、コールされません。<br>
 *
 * エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、
 * 内包するエンティティへの SQL 処理を連動して行う事ができるようになります。
 * </p>
 * 
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * {@literal @}Table("super")
 *  public class ContactComposite extends Contact implements <b>Composite</b> {
 *   {@literal @}NonColumn
 *    public final List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
 *  
 *   {@literal @}Override
 *    <b>public void postSelect(Connection conn)</b> {
 *      if (id != 0) {
 *        new Sql&lt;&gt;(Phone.class).connection(conn)
 *          .where("{contactId}={}", id)
 *          .orderBy("{phoneNumber}")
 *          .select(phones::add);
 *      }
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postInsert(Connection conn)</b> {
 *      phones.forEach(phone -&gt; phone.contactId = id);
 *      int count = new Sql&lt;&gt;(Phone.class).connection(conn)
 *          .insert(phones);
 *      return count;
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postUpdate(Connection conn)</b> {
 *      List&lt;Integer&gt; phoneIds = phones.stream()
 *        .map(phone -&gt; phone.id)
 *        .filter(id -&gt; id != 0)
 *        .collect(Collectors.toList());
 *
 *      // Delete phones
 *      int count += new Sql&lt;&gt;(Phone.class).connection(conn)
 *        .where("{contactId}={}", id)
 *        .doIf(phoneIds.size() &gt; 0,
 *          sql -&gt; sql.and("{id} NOT IN {}", phoneIds)
 *        )
 *        .delete();
 *
 *      // Uptete phones
 *      count += new Sql&lt;&gt;(Phone.class).connection(conn)
 *        .update(phones.stream()
 *          .filter(phone -&gt; phone.id != 0)
 *          .collect(Collectors.toList()));
 *
 *      // Insert phones
 *      count += new Sql&lt;&gt;(Phone.class).connection(conn)
 *        .insert(phones.stream()
 *          .filter(phone -&gt; phone.id == 0)
 *          .collect(Collectors.toList()));
 *
 *      return count;
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postDelete(Connection conn)</b> {
 *      int count = new Sql&lt;&gt;(Phone.class).connection(conn)
 *        .where("{contactId}={}", id)
 *        .delete(conn);
 *      return count;
 *    }
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Composite {
	/**
	 * <b>postSelect</b> は行を SELECT しエンティティに値が格納された後に実行されます。
	 *
	 * @param connection データベース・コネクション
	 *
	 * @throws NullPointerException <b>connection</b> が null の場合
	 */
	void postSelect(Connection connection);

	/**
	 * <b>postInsert</b> は行の挿入後に実行されます。
	 *
	 * @param connection データベース・コネクション
	 * @return 挿入された行数
	 *
	 * @throws NullPointerException <b>connection</b> が null の場合
	 */
	int postInsert(Connection connection);

	/**
	 * <b>postUpdate</b> は行の更新後に実行されます。
	 *
	 * @param connection データベース・コネクション
	 * @return 更新された行数
	 *
	 * @throws NullPointerException <b>connection</b> が null の場合
	 */
	int postUpdate(Connection connection);

	/**
	 * <b>postDelete</b> は行の削除後に実行されます。
	 *
	 * @param connection データベース・コネクション
	 * @return 削除された行数
	 *
	 * @throws NullPointerException <b>connection</b> が null の場合
	 */
	int postDelete(Connection connection);
}
