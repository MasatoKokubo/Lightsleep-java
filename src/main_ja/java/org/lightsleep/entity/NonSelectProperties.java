// NonSelectProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>NonSelectProperty</b>アノテーションの配列を示します。
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonSelect
 * @see NonSelectProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NonSelectProperties {
    /** @return <b>NonSelectProperty</b>アノテーションの配列 */
    NonSelectProperty[] value();
}
