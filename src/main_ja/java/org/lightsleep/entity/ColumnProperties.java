// ColumnProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>ColumnProperty</b>アノテーションの配列を示します。
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Column
 * @see ColumnProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ColumnProperties {
    /** @return <b>ColumnProperty</b>アノテーションの配列 */
    ColumnProperty[] value();
}
