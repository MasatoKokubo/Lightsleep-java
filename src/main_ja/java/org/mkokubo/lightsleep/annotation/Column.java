/*
	Column.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.annotation;

import java.lang.annotation.*;

/**
	フィールドに関連するデータベース・カラム名を指定します。<br>
	カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	/** @return カラム名 */
	String value() default "";
}
