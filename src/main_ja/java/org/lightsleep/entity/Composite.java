// Composite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの
 * <b>select</b>, <b>insert(E)</b>, <b>update(E)</b>および<b>delete(E)</b>メソッドの各SQLの実行後に
 * <b>postSelect</b>, <b>postInsert</b>, <b>postUpdate</b>または<b>postDelete</b>メソッドが呼び出されます。<br>
 *
 * <p>
 * @deprecated リリース 3.2.0 より。
 * 代わりに{@link PostSelect}, {@link PostInsert}, {@link PostUpdate}および{@link PostDelete}インターフェースを使用してください。
 * </p>
 *
 * <p>
 * エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、
 * 内包するエンティティへのSQL処理を連動して行う事ができます。
 * </p>
 * 
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * {@literal @}Table("super")
 *  public class ContactComposite extends Contact implements <b>Composite</b> {
 *   {@literal @}NonColumn
 *    public final List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
 *  
 *   {@literal @}Override
 *    <b>public void postSelect(ConnectionWrapper connection)</b> {
 *      if (id != 0) {
 *        new Sql&lt;&gt;(Phone.class)
 *          .where("{contactId}={}", id)
 *          .orderBy("{phoneNumber}")
 *          .connection(connection)
 *          .select(phones::add);
 *      }
 *    }
 *
 *   {@literal @}Override
 *    <b>public void postInsert(ConnectionWrapper connection)</b> {
 *      phones.forEach(phone -&gt; phone.contactId = id);
 *      new Sql&lt;&gt;(Phone.class)
 *          .connection(connection)
 *          .insert(phones);
 *    }
 *  
 *   {@literal @}Override
 *    <b>public void postUpdate(ConnectionWrapper connection)</b> {
 *      List&lt;Integer&gt; phoneIds = phones.stream()
 *        .map(phone -&gt; phone.id)
 *        .filter(id -&gt; id != 0)
 *        .collect(Collectors.toList());
 *
 *      // Delete phones
 *      new Sql&lt;&gt;(Phone.class)
 *        .where("{contactId}={}", id)
 *        .doIf(phoneIds.size() &gt; 0,
 *          sql -&gt; sql.and("{id} NOT IN {}", phoneIds)
 *        )
 *        .connection(connection)
 *        .delete();
 *
 *      // Uptete phones
 *      new Sql&lt;&gt;(Phone.class)
 *        .connection(connection)
 *        .update(phones.stream()
 *          .filter(phone -&gt; phone.id != 0)
 *          .collect(Collectors.toList()));
 *
 *      // Insert phones
 *      new Sql&lt;&gt;(Phone.class)
 *        .connection(connection)
 *        .insert(phones.stream()
 *          .filter(phone -&gt; phone.id == 0)
 *          .collect(Collectors.toList()));
 *    }
 *  
 *   {@literal @}Override
 *    <b>public void postDelete(ConnectionWrapper connection)</b> {
 *      new Sql&lt;&gt;(Phone.class)
 *        .where("{contactId}={}", id)
 *        .connection(connection)
 *        .delete(connection);
 *    }
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 */
@Deprecated
public interface Composite extends PostSelect, PostInsert, PostUpdate, PostDelete {
}
