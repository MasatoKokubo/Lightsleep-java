// PostInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * If an entity class implements this interface,
 * <b>select</b> methods of Sql&lt;E&gt; class invoke <b>postSelect</b> method
 * of the entity class after each entity is retrieved by executing SELECT SQL.
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostSelect {
	/**
	 * Invoked after each entity is retrieved by executing SELECT SQL.
	 *
	 * @param conn the connection wrapper
	 *
	 * @throws NullPointerException if <b>conn</b> is <b>null</b>
	 */
	public void postSelect(ConnectionWrapper conn);
}
