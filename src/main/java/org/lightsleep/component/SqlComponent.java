// SqlComponent.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * An interface of SQL components.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface SqlComponent {
	/**
	 * Returns whether this component is empty.
	 *
	 * @return <b>true</b> if empty, <b>false</b> otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns a SQL string representation of this object.<br>
	 * If uses a parameter character (<b>?</b>) at generating character string,
	 * add the parameter object to <b>parameters</b>.
	 *
	 * @param <E> the type of the entity
	 * @param sql the <b>Sql</b> object
	 * @param database the database handler
	 * @param parameters a list of parameters
	 *
	 * @return a SQL string representation
	 *
	 * @throws NullPointerException if <b>sql</b> or <b>parameters</b> is null
	 */
// 2.1.0
//	<E> String toString(Sql<E> sql, List<Object> parameters);
	<E> String toString(Database database, Sql<E> sql, List<Object> parameters);
////
}
