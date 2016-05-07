/*
	NonInsert.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドに関連するカラムが INSERT SQL で使用されない事を指定します。

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonInsert {
}
