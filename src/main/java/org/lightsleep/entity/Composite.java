// Composite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>select</b>, <b>insert</b>,
 * <b>update</b> or <b>delete</b> method of <b>Sql</b> class calls
 * <b>postSelect</b>, <b>postInsert</b>,
 * <b>postUpdate</b> or <b>postDelete</b> method of the entity class
 * after the execution of each execute SQL.<br>
 *
 * <p>
 * However if <b>update</b> or <b>delete</b> method dose not have entity parameter, dose not call.
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
	 * <b>postSelect</b> is executed after select a row and set it to the entity.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	void postSelect(ConnectionWrapper conn);

	/**
	 * <b>postInsert</b> is executed after a row has been inserted.
	 *
	 * @param conn the connection wrapper
	 * @return the number of inserted rows
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	int postInsert(ConnectionWrapper conn);

	/**
	 * <b>postUpdate</b> is executed after a row has been updated.
	 *
	 * @param conn the connection wrapper
	 * @return the number of updated rows
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	int postUpdate(ConnectionWrapper conn);

	/**
	 * <b>postDelete</b> is executed after a row has been deleted.
	 *
	 * @param conn the connection wrapper
	 * @return the number of deleted rows
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	int postDelete(ConnectionWrapper conn);
}
