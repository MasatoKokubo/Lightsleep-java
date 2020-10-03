// Insert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * INSERT SQLで、フィールド値の代わりに使用される式を示します。
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp createdTime;
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Insert('CURRENT_TIMESTAMP')</b>
 *   Timestamp createdTime
 *  <b>{@literal @}Insert('CURRENT_TIMESTAMP')</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
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
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Insert {
    /** @return 式 */
    String value();
}
