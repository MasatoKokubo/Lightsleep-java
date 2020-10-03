// SelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an expression instead of the name of the column related to the field defined in superclass used in SELECT SQL.
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>expression=""</b>, the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property="fullName", expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property="fullName"){@literal @}NonUpdateProperty(property="fullName")
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property='fullName', expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property='fullName'){@literal @}NonUpdateProperty(property='fullName')
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName||' '||lastName AS fullName</b>, ...
 * </pre></div>
 * 
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Select
 * @see SelectProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SelectProperties.class)
@Target({ElementType.TYPE})
public @interface SelectProperty {
    /** @return the property name that specifies the field */
    String property();

    /** @return the expression */
    String expression();
}
