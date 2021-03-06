// NonColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the field defined in superclass is not related to any column.<br>
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>value=false</b>, the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonColumnProperty(property="phones")</b>
 * <b>{@literal @}NonColumnProperty(property="addresses")</b>
 *  public class PersonComposite {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonColumnProperties([</b>
 *   <b>{@literal @}NonColumnProperty(property='phones'),</b>
 *   <b>{@literal @}NonColumnProperty(property='addresses')</b>
 *  <b>])</b>
 *  class PersonComposite extends PersonCompositeBase {
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonColumn
 * @see NonColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonColumnProperties.class)
@Target({ElementType.TYPE})
public @interface NonColumnProperty {
    /**
     * @return the property name that specifies the field
     * @since 2.0.0
     */
    String property();

    /** @return true if the field not related to any column, false otherwise */
    boolean value() default true;
}
