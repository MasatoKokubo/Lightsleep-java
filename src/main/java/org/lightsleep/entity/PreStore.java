// PreStore.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * If an entity class implements this interface,
 * <b>preStore</b> method of the entity class is executed before executing INSERT or UPDATE SQL.<br>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
public interface PreStore {
	/**
	 * This method is executed before executing INSERT or UPDATE SQL.
	 */
	void preStore();
}
