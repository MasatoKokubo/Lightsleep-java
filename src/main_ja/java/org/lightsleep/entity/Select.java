// Select.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * SELECT SQL で、 のカラム名の代わりに使用される式を示します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
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
 * @see InsertProperty
 * @see InsertProperties
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
	/** @return 式 */
	String value();
}
