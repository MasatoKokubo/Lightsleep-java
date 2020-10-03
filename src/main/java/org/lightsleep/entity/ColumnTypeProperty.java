// ColumnTypeProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the column type related to the field defined in superclass.<br>
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>type=Void.class</b> or <b>type=Void</b> (Groovy), the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property="birthday" type=Long.class)</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property='birthday' type=Long)</b>
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
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
    /** @return the property name that specifies the field */
    String property();

    /** @return the column java type */
    Class<?> type();
}
