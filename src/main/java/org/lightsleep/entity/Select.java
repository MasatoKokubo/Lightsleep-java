// Select.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates ta column expression instead of the column name in SELECT SQL.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Select("{firstName}||' '||{lastName}")</b>
 *  {@literal @}NonInsert{@literal @}NonUpdate
 *   public String fullName;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Select("{firstName}||' '||{lastName}")</b>
 *  {@literal @}NonInsert{@literal @}NonUpdate
 *   String fullName
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName||' '||lastName AS fullName</b>, ...
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see SelectProperty
 * @see SelectProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
    /** @return the expression */
    String value();
}
