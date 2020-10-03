// ColumnProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>ColumnProperty</b> annotations.
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
    /** @return the array of <b>ColumnProperty</b> annotations */
    ColumnProperty[] value();
}
