// NonInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related the field is not used in INSERT SQL.
 *
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *   {@literal @}Key<b>{@literal @}NonInsert</b>public int id;
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonInsertProperty
 * @see NonInsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonInsert {
}
