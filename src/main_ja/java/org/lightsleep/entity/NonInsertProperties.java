// NonInsertProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>NonInsertProperty</b>アノテーションの配列を示します。
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonInsert
 * @see NonInsertProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonInsertProperties {
	/** @return <b>NonInsertProperty</b>アノテーションの配列 */
	NonInsertProperty[] value();
}
