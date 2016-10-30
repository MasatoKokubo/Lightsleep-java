/*
	Insert.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Specifies an expression as a value of the INSERT SQL.<br>
	If this annotation is specified, the value of the field is not used.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Insert {
	/** @return the expression */
// 1.2.0
//	String value() default "";
	String value();
////
}
