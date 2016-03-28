/*
	Column.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.annotation;

import java.lang.annotation.*;

/**
	Specifies the column name associated with the field.<br>
	If the column name is the same as the field name, you do not need to specify this annotation.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	/** @return the column name */
	String value() default "";
}
