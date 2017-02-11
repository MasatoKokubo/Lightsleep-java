// ColumnType.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the type of column associated with the field.<br>
 * If the field type and column type are the same type, you do not need to specify it.
 * Specify if field type (e.g. date type) and column type (e.g. numerical type) are different.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}ColumnType("Long")</b>
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
 * @see ColumnTypeProperty
 * @see ColumnTypeProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ColumnType {
	/** @return the column java type */
	Class<?> value();
}
