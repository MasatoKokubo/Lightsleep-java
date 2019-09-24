// Composite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * If an entity class implements this interface,
 * <b>select</b>, <b>insert(E)</b>, <b>update(E)</b> and <b>delete(E)</b> methods of
 * <b>Sql&lt;E&gt;</b> class invoke
 * <b>postSelect</b>, <b>postInsert</b>, <b>postUpdate</b> or <b>postDelete</b> method of the entity class
 * after the execution of each SQL.<br>
 *
 * <p>
 * @deprecated As of release 3.2.0,
 * instead use {@link PostSelect}, {@link PreInsert}, {@link PostUpdate} and {@link PostDelete} interfaces
 * </p>
 *
 * <p>
 *  If an entity is enclose another entity, by implementing this interface,
 * 	You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.
 * </p>
 * 
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * {@literal @}Table("super")
 *  public class ContactComposite extends Contact implements <b>Composite</b> {
 *   {@literal @}NonColumn
 *    public final List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
 *  
 *   {@literal @}Override
 *    <b>public void postSelect(ConnectionWrapper conn)</b> {
 *      if (id != 0) {
 *        new Sql&lt;&gt;(Phone.class)
 *          .where("{contactId}={}", id)
 *          .orderBy("{phoneNumber}")
 *          .connection(conn)
 *          .select(phones::add);
 *      }
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postInsert(ConnectionWrapper conn)</b> {
 *      phones.forEach(phone -&gt; phone.contactId = id);
 *      new Sql&lt;&gt;(Phone.class)
 *          .connection(conn)
 *          .insert(phones);
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postUpdate(ConnectionWrapper conn)</b> {
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
 *        .connection(conn)
 *        .delete();
 *
 *      // Uptete phones
 *      new Sql&lt;&gt;(Phone.class)
 *        .connection(conn)
 *        .update(phones.stream()
 *          .filter(phone -&gt; phone.id != 0)
 *          .collect(Collectors.toList()));
 *
 *      // Insert phones
 *      new Sql&lt;&gt;(Phone.class)
 *        .connection(conn)
 *        .insert(phones.stream()
 *          .filter(phone -&gt; phone.id == 0)
 *          .collect(Collectors.toList()));
 *    }
 *  
 *   {@literal @}Override
 *    <b>public int postDelete(ConnectionWrapper conn)</b> {
 *      new Sql&lt;&gt;(Phone.class)
 *        .where("{contactId}={}", id)
 *        .connection(conn)
 *        .delete(connection);
 *    }
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 */
// 3.2.0
//public interface Composite {
//	/**
//	 * <b>postSelect</b> is executed after select a row and set it to the entity.
//	 *
//	 * @param conn the connection wrapper
//	 *
//	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
//	 */
//	void postSelect(ConnectionWrapper conn);
//
//	/**
//	 * <b>postInsert</b> is executed after a row has been inserted.
//	 *
//	 * @param conn the connection wrapper
//	 * @return the number of inserted rows
//	 *
//	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
//	 */
//	int postInsert(ConnectionWrapper conn);
//
//	/**
//	 * <b>postUpdate</b> is executed after a row has been updated.
//	 *
//	 * @param conn the connection wrapper
//	 * @return the number of updated rows
//	 *
//	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
//	 */
//	int postUpdate(ConnectionWrapper conn);
//
//	/**
//	 * <b>postDelete</b> is executed after a row has been deleted.
//	 *
//	 * @param conn the connection wrapper
//	 * @return the number of deleted rows
//	 *
//	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
//	 */
//	int postDelete(ConnectionWrapper conn);
@Deprecated
public interface Composite extends PostSelect, PostInsert, PostUpdate, PostDelete {
////
}
