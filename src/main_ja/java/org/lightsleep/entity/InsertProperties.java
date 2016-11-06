/*
	InsertProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	<b>InsertProperty</b> アノテーションの配列を示します。

	@since 1.3.0
	@see Insert
	@see InsertProperty
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InsertProperties {
	/** @return <b>InsertProperty</b> アノテーションの配列 */
	InsertProperty[] value();
}
