// Insert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * INSERT SQL で、フィールド値の代わりに使用される式を示します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp created;
 *
 *  <b>{@literal @}Insert("CURRENT_TIMESTAMP")</b>
 *   public Timestamp modified;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * INSERT INTO Contact (..., <b>created</b>, <b>modified</b>) VALUES (..., <b>CURRENT_TIMESTAMP</b>, <b>CURRENT_TIMESTAMP</b>)
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see InsertProperty
 * @see InsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Insert {
	/** @return 式 */
	String value();
}
