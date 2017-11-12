// NonColumn.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドがどのカラムにも関連しない事を示します。
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonColumn</b>
 *   public List&lt;Phone&gt; phones;
 *  <b>{@literal @}NonColumn</b>
 *   public List&lt;Address&gt; addresses;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}NonColumn</b>
 *   List&lt;Phone&gt; phones
 *  <b>{@literal @}NonColumn</b>
 *   List&lt;Address&gt; addresses
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonColumnProperty
 * @see NonColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonColumn {
	/**
	 * @return フィールドがどのカラムにも関連しないなら<b>true</b>、そうでなければ<b>false</b>
	 * @since 2.0.0
	 */
	boolean value() default true;
}
