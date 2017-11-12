// NonSelect.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムがSELECT SQLで使用されない事を示します。
 * 
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonSelect</b>
 *   public Timestamp createdTime;
 *  <b>{@literal @}NonSelect</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class='exampleCode'><pre>
 *  <b>{@literal @}NonSelect</b>
 *   Timestamp createdTime
 *  <b>{@literal @}NonSelect</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonSelectProperty
 * @see NonSelectProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonSelect {
	/**
	 * @return フィールドに関連するカラムがSELECT SQLで使用されないなら<b>true</b>、そうでなければ<b>false</b>
	 * @since 2.0.0
	 */
	boolean value() default true;
}
