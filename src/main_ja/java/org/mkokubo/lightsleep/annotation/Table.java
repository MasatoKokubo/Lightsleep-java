/*
	Table.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.annotation;

import java.lang.annotation.*;

/**
	クラスに関連するデータベース・テーブル名を指定します。<br>
	テーブル名がクラス名と同じであれば、このアノテーションを指定する必要はありません。

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
	/** @return テーブル名 */
	String value() default "";
}
