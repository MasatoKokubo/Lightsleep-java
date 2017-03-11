// ColumnTypeProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the column type associated with the field.<br>
 * Specifies the field by <b>property</b>.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property="birthday" type=Long.class)</b>
 * public class Contact {
 *
 *   public Date birthday;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * INSERT INTO Contact (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
 * </pre></div>
 * 
 * @since 1.8.0
 * @author Masato Kokubo
 * @see ColumnType
 * @see ColumnTypeProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnTypeProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnTypeProperty {
	/** @return the property name of the specified field */
	String property();

	/** @return the column java type */
	Class<?> type();
}
