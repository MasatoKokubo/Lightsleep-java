// Update.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * UPDATE SQL で、フィールド値の代わりに使用される式を示します。
 * 
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Update("{updateCount}+1")</b>
 *   public int updateCount;
 *  <b>{@literal @}Update("CURRENT_TIMESTAMP")</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Update('{updateCount}+1')</b>
 *   int updateCount
 *  <b>{@literal @}Update("CURRENT_TIMESTAMP")</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ..., <b>updateCount=updateCount+1</b>, <b>updatedTime=CURRENT_TIMESTAMP</b> WHERE ...
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see UpdateProperty
 * @see UpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Update {
	/** @return 式 */
	String value();
}
