// InsertProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an expression instead of the value of the field defined in superclass used in INSERT SQL.
 *
 * <p>
 * Specifies the field by <b>property</b>.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}InsertProperty(property="createdTime", expression="CURRENT_TIMESTAMP")</b>
 * <b>{@literal @}InsertProperty(property="updatedTime", expression="CURRENT_TIMESTAMP")</b>
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}InsertProperties([</b>
 *   <b>{@literal @}InsertProperty(property='createdTime', expression='CURRENT_TIMESTAMP'),</b>
 *   <b>{@literal @}InsertProperty(property='updatedTime', expression='CURRENT_TIMESTAMP')</b>
 *  <b>])</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>createdTime</b>, <b>updatedTime</b>) VALUES (..., <b>CURRENT_TIMESTAMP</b>, <b>CURRENT_TIMESTAMP</b>)
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Insert
 * @see InsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(InsertProperties.class)
@Target({ElementType.TYPE})
public @interface InsertProperty {
	/** @return the property name that specifies the field */
	String property();

	/** @return the expression */
	String expression();
}
