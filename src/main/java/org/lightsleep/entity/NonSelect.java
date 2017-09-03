// NonSelect.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related the field is not used in SELECT SQL.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonSelect</b>
 *   public Timestamp createdTime;
 *  <b>{@literal @}NonSelect</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class='exampleTitle'><span>Groovy Example</span></div>
 * <div class='exampleCode'><pre>
 *  <b>{@literal @}NonSelect</b>
 *   Timestamp createdTime
 *  <b>{@literal @}NonSelect</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonSelectProperty
 * @see NonSelectProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonSelect {
// 2.0.0
	/**
	 * @return true if the column related the field is not used in SELECT SQL, false otherwise
	 * @since 2.0.0
	 */
	boolean value() default true;
////
}
