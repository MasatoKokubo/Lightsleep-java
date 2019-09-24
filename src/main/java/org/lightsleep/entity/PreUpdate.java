// PreUpdate.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>update(E)</b> and <b>update(Iterable)</b> methods of Sql&lt;E&gt; class invoke
 * <b>preUpdate</b> method of the entity class before executing UPDATE SQL.
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PreUpdate {
	/**
	 * Invoked before executing UPDATE SQL.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	void preUpdate(ConnectionWrapper conn);
}
