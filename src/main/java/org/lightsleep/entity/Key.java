// Key.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related to the field is part of the primary key.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Key</b>
 *   public int contactId;
 *  <b>{@literal @}Key</b>
 *   public short childIndex;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Key</b>
 *   int contactId
 *  <b>{@literal @}Key</b>
 *   short childIndex
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ... WHERE <b>contactId=100 AND childIndex=1</b>
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see KeyProperty
 * @see KeyProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Key {
    /**
     * @return true if the column related to the field is part of the primary key, false otherwise
     * @since 2.0.0
     */
    boolean value() default true;
}
