// Select.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the expression instead of the column name in SELECT SQL.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}Select("(SELECT COUNT(*) FROM Phone WHERE contactId=Contact.id)")</b>
 *  {@literal @}NonInsert {@literal @}NonUpdate
 *   public short phoneCount;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * SELECT ..., <b>(SELECT COUNT(*) FROM Phone WHERE contactId=Contact.id)</b>, ... FROM Contact WHERE ...
 * </pre></div>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see SelectProperty
 * @see SelectProperties
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
	/** @return the expression */
// 1.2.0
//	String value() default "";
	String value();
////
}
