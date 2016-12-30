/*
	SelectProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates an array of <b>SelectProperty</b> annotations.

	@since 1.3.0
	@see Select
	@see SelectProperty
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SelectProperties {
	/** @return the array of <b>SelectProperty</b> annotations */
	SelectProperty[] value();
}
