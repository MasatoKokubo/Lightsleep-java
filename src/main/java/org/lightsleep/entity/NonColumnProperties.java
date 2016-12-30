/*
	NonColumnProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates an array of <b>NonColumnProperty</b> annotations.

	@since 1.3.0
	@see NonColumn
	@see NonColumnProperty
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonColumnProperties {
	/** @return the array of <b>NonColumnProperty</b> annotations */
	NonColumnProperty[] value();
}
