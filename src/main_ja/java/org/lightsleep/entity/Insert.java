/*
	Insert.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	INSERT SQL の挿入値の式を指定します。<br>
	このアノテーションが指定された場合、フィールドの値は使用されません。

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Insert {
	/** @return 式 */
	String value() default "";
}
