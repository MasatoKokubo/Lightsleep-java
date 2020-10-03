// UpdateProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an array of <b>UpdateProperty</b> annotations.
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see UpdateProperty
 * @see UpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UpdateProperties {
    /** @return the array of <b>UpdateProperty</b> annotations */
    UpdateProperty[] value();
}
