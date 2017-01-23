// PostLoad.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * If an entity class implements this interface,
 * <b>postLoad</b> method of the entity class is executed after load each row.<br>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
public interface PostLoad {
	/**
	 * This method is executed after load each row.
	 */
	void postLoad();
}
