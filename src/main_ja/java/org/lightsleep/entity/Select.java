/*
	Select.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	SELECT SQL のカラム名の代わりの式を指定します。

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
	/** @return 式 */
	String value() default "";
}
