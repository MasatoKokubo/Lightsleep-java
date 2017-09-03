// NonColumn.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the field not related to any column.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonColumn</b>
 *   public List&lt;Phone&gt; phones;
 *  <b>{@literal @}NonColumn</b>
 *   public List&lt;Address&gt; addresses;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonColumn</b>
 *   List&lt;Phone&gt; phones
 *  <b>{@literal @}NonColumn</b>
 *   List&lt;Address&gt; addresses
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonColumnProperty
 * @see NonColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonColumn {
// 2.0.0
	/**
	 * @return true if the field not related to any column, false otherwise
	 * @since 2.0.0
	 */
	boolean value() default true;
////
}
