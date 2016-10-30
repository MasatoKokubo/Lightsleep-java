/*
	Column.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

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
// 1.2.0
//	String value() default "";
	String value();
////
}
