/*
	NonColumn.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.annotation;

import java.lang.annotation.*;

/**
	Specifies that the field not related to any column.

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonColumn {
}
