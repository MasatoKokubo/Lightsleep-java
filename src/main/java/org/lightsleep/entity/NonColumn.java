// NonColumn.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the field not related to any column.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}NonColumn</b>public List&lt;Phone&gt; phones;
 *  <b>{@literal @}NonColumn</b>public List&lt;Address&gt; addresses;
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonColumnProperty
 * @see NonColumnProperties
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonColumn {
}
