// NonUpdate.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムがUPDATE SQLで使用されない事を示します。
 * 
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonUpdate</b>
 *   public Timestamp createdTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonUpdate</b>
 *   Timestamp createdTime
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonUpdateProperty
 * @see NonUpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonUpdate {
	/**
	 * @return フィールドに関連するカラムがUPDATE SQLで使用されないなら<b>true</b>、そうでなければ<b>false</b>
	 * @since 2.0.0
	 */
	boolean value() default true;
}
