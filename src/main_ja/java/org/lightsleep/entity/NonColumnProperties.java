// NonColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>NonColumnProperty</b>アノテーションの配列を示します。
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonColumn
 * @see NonColumnProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonColumnProperties {
    /** @return <b>NonColumnProperty</b>アノテーションの配列 */
    NonColumnProperty[] value();
}
