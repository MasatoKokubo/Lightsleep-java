// SelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * SELECT SQL で、 のカラム名の代わりに使用される式を示します。<br>
 * 対象のフィールドは、<b>property</b> で指定します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
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
 * @see Select
 * @see SelectProperties
 * @author Masato Kokubo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SelectProperties.class)
@Target({ElementType.TYPE})
public @interface SelectProperty {
	/** @return 指定対象のフィールドのプロパティ名 */
	String property();

	/** @return 式 */
	String expression();
}
