// Insert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an expression instead of the field value used in INSERT SQL.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp createdTime;
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Insert('CURRENT_TIMESTAMP')</b>
 *   Timestamp createdTime
 *  <b>{@literal @}Insert('CURRENT_TIMESTAMP')</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>createdTime</b>, <b>updatedTime</b>) VALUES (..., <b>CURRENT_TIMESTAMP</b>, <b>CURRENT_TIMESTAMP</b>)
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see InsertProperty
 * @see InsertProperties
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Insert {
    /** @return the expression */
    String value();
}
