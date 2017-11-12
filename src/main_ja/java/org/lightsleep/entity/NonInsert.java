// NonInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムがINSERT SQLで使用されない事を示します。
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonInsert</b>
 *   public Timestamp createdTime;
 *  <b>{@literal @}NonInsert</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonInsert</b>
 *   Timestamp createdTime
 *  <b>{@literal @}NonInsert</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonInsertProperty
 * @see NonInsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonInsert {
	/**
	 * @return フィールドに関連するカラムがINSERT SQLで使用されないなら<b>true</b>、そうでなければ<b>false</b>
	 * @since 2.0.0
	 */
	boolean value() default true;
}
