/*
	NonInsertProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	<b>KeyProperty</b> アノテーションの配列を示します。

	@since 1.3.0
	@see Key
	@see KeyProperty
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface KeyProperties {
	/** @return <b>KeyProperty</b> アノテーションの配列 */
	KeyProperty[] value();
}
