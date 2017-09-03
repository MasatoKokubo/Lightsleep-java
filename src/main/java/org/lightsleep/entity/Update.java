// Update.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an expression instead of the field value used in UPDATE SQL.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Update("{updateCount}+1")</b>
 *   public int updateCount;
 *  <b>{@literal @}Update("CURRENT_TIMESTAMP")</b>
 *   public Timestamp updatedTime;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Update('{updateCount}+1')</b>
 *   int updateCount
 *  <b>{@literal @}Update("CURRENT_TIMESTAMP")</b>
 *   Timestamp updatedTime
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
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
	/** @return the expression */
	String value();
}
