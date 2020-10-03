// ColumnTypeProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>ColumnTypeProperty</b>アノテーションの配列を示します。
 *
 * @since 1.8.0
 * @author Masato Kokubo
 * @see ColumnType
 * @see ColumnTypeProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ColumnTypeProperties {
    /** @return <b>ColumnTypeProperty</b>アノテーションの配列 */
    ColumnTypeProperty[] value();
}
