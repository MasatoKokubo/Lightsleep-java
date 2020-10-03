// SelectProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>SelectProperty</b> annotations.
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Select
 * @see SelectProperty
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SelectProperties {
    /** @return the array of <b>SelectProperty</b> annotations */
    SelectProperty[] value();
}
