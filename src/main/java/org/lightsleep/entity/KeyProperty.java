// KeyProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related to the field is part of the primary key.<br>
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>value=false</b>, the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}KeyProperty(property="contactId")</b>
 * <b>{@literal @}KeyProperty(property="childIndex")</b>
 *  public class Child extends ChildKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}KeyProperties([</b>
 *   <b>{@literal @}KeyProperty(property='contactId'),</b>
 *   <b>{@literal @}KeyProperty(property='childIndex')</b>
 *  <b>])</b>
 *  class Child extends ChildKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ... WHERE <b>contactId=100 AND childIndex=1</b>
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Key
 * @see KeyProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(KeyProperties.class)
@Target({ElementType.TYPE})
public @interface KeyProperty {
    /**
     * @return the property name of the specified field
     * @since 2.0.0
     */
    String property();

    /** @return true if the column related to the field is part of the primary key, false otherwise */
    boolean value() default true;
}
