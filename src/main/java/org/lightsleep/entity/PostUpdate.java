// PostInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>update(E)</b> and <b>update(Iterable)</b> methods of Sql&lt;E&gt; class invoke
 * <b>postUpdate</b> method of the entity class after executing UPDATE SQL.
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostUpdate {
	/**
	 * Invoked after executing UPDATE SQL.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	public void postUpdate(ConnectionWrapper conn);
}
