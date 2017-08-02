// NonColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the field not related to any column.<br>
 * Specifies the field by <b>value</b>.
 *
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}NonColumnProperty("phones")</b>
 * <b>{@literal @}NonColumnProperty("addresses")</b>
 * public class Contact {
 *
 *   public List&lt;Phone&gt; phones;
 *   public List&lt;Address&gt; addresses;
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
	/** @return the property name of the specified field */
	String value();
}
