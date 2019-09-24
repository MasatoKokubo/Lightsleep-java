// PreInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>insert(E)</b> and <b>insert(Iterable)</b> methods of Sql&lt;E&gt; class invoke
 * <b>preInsert</b> method of the entity class before executing INSERT SQL.
 *
 * <p>
 * You can implement primary key numbering using <b>preInsert</b> method.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * public abstract class Common implements <b>PreInsert</b> {
 *  {@literal @}Key
 *   public int id;
 *     ...
 *
 *  {@literal @}Override
 *   <b>public void preInsert(ConnectionWrapper conn)</b> {
 *     id = Numbering.getNewId(conn, getClass());
 *   }
 * }
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface PreInsert {
	/**
	 * Invoked before executing INSERT SQL.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
// 3.2.0
//	int preInsert(ConnectionWrapper conn);
	public void preInsert(ConnectionWrapper conn);
////
}
