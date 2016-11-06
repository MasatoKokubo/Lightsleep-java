/*
	SelectProperties.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	<b>SelectProperty</b> アノテーションの配列を示します。

	@since 1.3.0
	@see Select
	@see SelectProperty
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SelectProperties {
	/** @return <b>SelectProperty</b> アノテーションの配列 */
	SelectProperty[] value();
}
