// ColumnType.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the type of column related to the field.
 *
 * <p>
 * If the field type and column type are the same type, you do not need to specify it.
 * Specify if field type (e.g. date type) and column type (e.g. numerical type) are different.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}ColumnType(Long.class)</b>
 *   public Date birthday;
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}ColumnType(Long)</b>
 *   Date birthday
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
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
