/*
	Table.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.annotation;

import java.lang.annotation.*;

/**
	Specifies the table name associated with the class.<br>
	If the table name is the same as the class name, you do not need to specify this annotation.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
	String value() default "";
}
