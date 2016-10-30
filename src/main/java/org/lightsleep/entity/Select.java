/*
	Select.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Specifies a column expression instead of the column name of the SELECT SQL.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
	/** @return the expression */
// 1.2.0
//	String value() default "";
	String value();
////
}
