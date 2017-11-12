// Key.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムがプライマリー･キーの一部である事を示します。
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Key</b>
 *   public int contactId;
 *  <b>{@literal @}Key</b>
 *   public short childIndex;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Key</b>
 *   int contactId
 *  <b>{@literal @}Key</b>
 *   short childIndex
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ... WHERE <b>contactId=100 AND childIndex=1</b>
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see KeyProperty
 * @see KeyProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Key {
	/**
	 * @return フィールドに関連するカラムがキーの一部であれば<b>true</b>、そうでなければ<b>false</b>
	 * @since 2.0.0
	 */
	boolean value() default true;
}
