// ColumnTypeProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>ColumnTypeProperty</b> annotations.
 *
 * @since 1.8.0
 * @author Masato Kokubo
 * @see ColumnType
 * @see ColumnTypeProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ColumnTypeProperties {
	/** @return the array of <b>ColumnTypeProperty</b> annotations */
	ColumnTypeProperty[] value();
}
