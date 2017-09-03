// NonUpdateProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related the field defined in superclass is not used in UPDATE SQL.<br>
 *
 * <p>
 * Specifies the field by <b>property</b>.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonUpdateProperty(property="createdTime")</b>
 *  public class Person extends PaersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonUpdateProperty(property='createdTime')</b>
 *  class Person extends PaersonBase {
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonUpdate
 * @see NonUpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonUpdateProperties.class)
@Target({ElementType.TYPE})
public @interface NonUpdateProperty {
	/**
	 * @return the property name that specifies the field
	 * @since 2.0.0
	 */
// 2.0.0
//	String value();
	String property();

	/** @return true if the column related the field is not used in UPDATE SQL, false otherwise */
	boolean value() default true;
////
}
