// PreStore.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * If an entity class implements this interface,
 * <b>insert(E)</b> and <b>insert(Iterable)</b>,
 * <b>update(E)</b> and <b>update(Iterable)</b> methods of Sql&lt;E&gt; class invoke
 * <b>preStore</b> method of the entity class before executing INSERT and UPDATE SQL.
 *
 * <p>
 * @deprecated As of release 3.2.0, instead use both {@link PreInsert} and {@link PreUpdate} interfaces
 * </p>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
@Deprecated
public interface PreStore {
	/**
	 * Invoked before executing INSERT and UPDATE SQL.
	 */
	public void preStore();
}
