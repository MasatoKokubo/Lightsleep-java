// InsertProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>InsertProperty</b> annotations.
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Insert
 * @see InsertProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InsertProperties {
    /** @return the array of <b>InsertProperty</b> annotations */
    InsertProperty[] value();
}
