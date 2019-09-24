// PostDelete.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>delete(E)</b> and <b>delete(Iterable)</b> method of Sql&lt;E&gt; class invoke
 * <b>postDelete</b> method of the entity class after executing DELETE SQL.
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostDelete {
	/**
	 * Invoked after executing DELETE SQL.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	public void postDelete(ConnectionWrapper conn);
}
