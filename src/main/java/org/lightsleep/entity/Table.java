/*
	Table.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Specifies the table name associated with the class.<br>
	If the table name is the same as the class name, you do not need to specify this annotation.<br>
	If you specify <b>@Table("super")</b>, the class name of the superclass is the table name.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
// 1.2.0
//	String value() default "";
	String value();
////
}
