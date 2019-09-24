// PostLoad.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * If an entity class implements this interface,
 * <b>select</b> methods of Sql&lt;E&gt; class invoke <b>postLoad</b> method
 * of the entity class after each entity is retrieved by executing SELECT SQL.
 *
 * <p>
 * @deprecated As of release 3.2.0, instead use {@link PostSelect} interface
 * </p>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
@Deprecated
public interface PostLoad {
	/**
	 * This method is executed after load each row.
	 */
	public void postLoad();
}
