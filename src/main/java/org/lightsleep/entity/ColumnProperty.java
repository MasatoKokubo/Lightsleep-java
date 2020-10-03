// ColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the column name related to the field defined in superclass.
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>column=""</b>, the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnProperty(property="name.first" column="firstName")</b>
 * <b>{@literal @}ColumnProperty(property="name.last" column="lastName")</b>
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnProperties([</b>
 *   <b>{@literal @}ColumnProperty(property='name.first' column='firstName'),</b>
 *   <b>{@literal @}ColumnProperty(property='name.last' column='lastName')</b>
 *  <b>])</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName</b>, <b>lastName</b>, ...
 * </pre></div>
 * 
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Column
 * @see ColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnProperty {
    /** @return the property name that specifies the field */
    String property();

    /** @return the column name */
    String column();
}
