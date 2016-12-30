/*
	InsertProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates an array of <b>InsertProperty</b> annotations.

	@since 1.3.0
	@see Insert
	@see InsertProperty
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InsertProperties {
	/** @return the array of <b>InsertProperty</b> annotations */
	InsertProperty[] value();
}
