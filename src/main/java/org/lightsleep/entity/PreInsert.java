// PreInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>insert</b> method of Sql class calls <b>preInsert</b> method
 * of the entity class before executing INSERT SQL.<br>
 *
 * <p>
 * In <b>preInsert method,</b> do the implementation of the numbering of the primary key or etc.
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
	 * This method is execute before executing INSERT SQL.
	 *
	 * @param conn the connection wrapper
	 * @return the number of inserted rows
	 *
	 * @throws NullPointerException if <b>conn</b> is null
	 */
	int preInsert(ConnectionWrapper conn);
}
