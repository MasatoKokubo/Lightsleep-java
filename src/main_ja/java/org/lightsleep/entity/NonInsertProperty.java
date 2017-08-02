// NonInsertProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムが INSERT SQL で使用されない事を示します。<br>
 * 対象のフィールドは、<b>value</b> で指定します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * {@literal @}KeyProperty("id")<b>{@literal @}NonInsertProperty("id")</b>
 * public class Contact {
 *   public int id;
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonInsert
 * @see NonInsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonInsertProperties.class)
@Target({ElementType.TYPE})
public @interface NonInsertProperty {
	/** @return 指定対象のフィールドのプロパティ名 */
	String value();
}
