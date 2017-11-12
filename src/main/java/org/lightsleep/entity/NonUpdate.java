// NonUpdate.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related the field is not used in UPDATE SQL.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonUpdate</b>
 *   public Timestamp createdTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Example of Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonUpdate</b>
 *   Timestamp createdTime
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonUpdateProperty
 * @see NonUpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonUpdate {
	/**
	 * @return true if the column related the field is not used in UPDATE SQL, false otherwise
	 * @since 2.0.0
	 */
	boolean value() default true;
}
