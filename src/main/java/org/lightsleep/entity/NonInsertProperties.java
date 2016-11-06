/*
	NonInsertProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates an array of <b>NonInsertProperty</b> annotations.

	@since 1.3.0
	@see NonInsert
	@see NonInsertProperty
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonInsertProperties {
	/** @return the array of <b>NonInsertProperty</b> annotations */
	NonInsertProperty[] value();
}
