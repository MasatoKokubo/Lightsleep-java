/*
	NonSelectProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates an array of <b>NonSelectProperty</b> annotations.

	@since 1.3.0
	@see NonSelect
	@see NonSelectProperty
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonSelectProperties {
	/** @return the array of <b>NonSelectProperty</b> annotations */
	NonSelectProperty[] value();
}
