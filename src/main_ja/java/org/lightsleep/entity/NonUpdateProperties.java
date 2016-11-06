/*
	NonUpdateProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	<b>NonUpdateProperty</b> アノテーションの配列を示します。

	@since 1.3.0
	@see NonUpdate
	@see NonUpdateProperty
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonUpdateProperties {
	/** @return <b>NonUpdateProperty</b> アノテーションの配列 */
	NonUpdateProperty[] value();
}
