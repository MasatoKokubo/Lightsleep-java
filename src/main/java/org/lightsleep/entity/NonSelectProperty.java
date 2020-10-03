// NonSelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column related the field defined in superclass is not used in SELECT SQL.<br>
 *
 * <p>
 * This annotation is used to specify for fields defined in superclass.
 * The specified contents also affects subclasses, but specifications in the subclass takes precedence.
 * If you specify <b>value=false</b>, the specification in the superclass is canceled.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonSelectProperty(property="createdTime")</b>
 * <b>{@literal @}NonSelectProperty(property="updatedTime")</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonSelectProperties([</b>
 *   <b>{@literal @}NonSelectProperty(property='createdTime'),</b>
 *   <b>{@literal @}NonSelectProperty(property='updatedTime')</b>
 *  <b>])</b>
 *  class Person extends PersonBase {
 * </pre></div>
 * 
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonSelect
 * @see NonSelectProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonSelectProperties.class)
@Target({ElementType.TYPE})
public @interface NonSelectProperty {
    /**
     * @return the property name that specifies the field
     * @since 2.0.0
     */
    String property();

    /** @return true if the column related the field is not used in SELECT SQL, false otherwise */
    boolean value() default true;
}
