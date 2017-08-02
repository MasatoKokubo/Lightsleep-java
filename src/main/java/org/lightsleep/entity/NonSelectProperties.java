// NonSelectProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>NonSelectProperty</b> annotations.
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonSelect
 * @see NonSelectProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonSelectProperties {
	/** @return the array of <b>NonSelectProperty</b> annotations */
	NonSelectProperty[] value();
}
