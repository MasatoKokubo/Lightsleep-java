// Column.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the name of column related to the field.
 *
 * <p>
 * If the column name is the same as the field name, you do not need to specify it.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Column("firstName")</b>
 *   public String first;
 *  <b>{@literal @}Column("lastName")</b>
 *   public String last;
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Column('firstName')</b>
 *   String first
 *  <b>{@literal @}Column('lastName')</b>
 *   String last
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName</b>, <b>lastName</b>, ...
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see ColumnProperty
 * @see ColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	/** @return the column name */
	String value();
}
