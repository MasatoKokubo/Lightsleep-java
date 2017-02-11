// SelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the expression instead of the column name in SELECT SQL.<br>
 * Specifies the field by <b>property</b>.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}SelectProperty(property="phoneCount", expression="(SELECT COUNT(*) FROM Phone WHERE contactId=Contact.id)")</b>
 * {@literal @}NonInsertProperty("phoneCount"){@literal @}NonUpdateProperty("phoneCount")
 * public class Contact {
 *
 *   public short phoneCount;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * SELECT ..., <b>(SELECT COUNT(*) FROM Phone P WHERE contactId=Contact.id)</b>, ... FROM Contact WHERE ...
 * </pre></div>
 * 
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Select
 * @see SelectProperties
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SelectProperties.class)
@Target({ElementType.TYPE})
public @interface SelectProperty {
	/** @return the property name of the specified field */
	String property();

	/** @return the expression */
	String expression();
}
