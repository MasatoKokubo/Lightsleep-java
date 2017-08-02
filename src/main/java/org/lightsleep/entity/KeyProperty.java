// KeyProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates that the column associated with the field is part of the primary key.
 * Specifies the field by <b>value</b>.
 *
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}KeyProperty("contactId")</b>
 * <b>{@literal @}KeyProperty("childIndex")</b>
 * public class Phone {
 *   public int contactId;
 *   public short childIndex;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * UPDATE Phone ... WHERE <b>contactId=100 AND childIndex=0</b>
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
	/** @return the property name of the specified field */
	String value();
}
