// NonInsertProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>NonInsertProperty</b> annotations.
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonInsert
 * @see NonInsertProperty
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonInsertProperties {
	/** @return the array of <b>NonInsertProperty</b> annotations */
	NonInsertProperty[] value();
}
