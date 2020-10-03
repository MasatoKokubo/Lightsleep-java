// UpdateProperties.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * <b>UpdateProperty</b>アノテーションの配列を示します。
 *
 * @since 1.3.0
 * @see UpdateProperty
 * @see UpdateProperties
 * @author Masato Kokubo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UpdateProperties {
    /** @return <b>UpdateProperty</b>アノテーションの配列 */
    UpdateProperty[] value();
}
