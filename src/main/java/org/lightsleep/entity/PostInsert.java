// PostInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>insert(E)</b> and <b>insert(Iterable)</b> method of Sql&lt;E&gt; class invoke
 * <b>postInsert</b> method of the entity class after executing INSERT SQL.
 *
 * <p>
 * You can use <b>postInsert</b> method to get the value automatically numbered at the time of insertion.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
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
 * <div class="exampleTitle"><span>SQL generated during INSERT</span></div>
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
     * Invoked after executing INSERT SQL.
     *
     * @param conn the connection wrapper
     *
     * @throws NullPointerException if <b>conn</b> is <b>null</b>
     */
    public void postInsert(ConnectionWrapper conn);
}
