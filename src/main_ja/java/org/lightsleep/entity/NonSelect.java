// NonSelect.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムが SELECT SQL で使用されない事を示します。
 * 
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}NonSelect</b>{@literal @}Insert("CURRENT_TIMESTAMP"){@literal @}NonUpdate
 *   public Timestamp created;
 *
 *  <b>{@literal @}NonSelect</b>{@literal @}Insert("CURRENT_TIMESTAMP"){@literal @}Update("CURRENT_TIMESTAMP")
 *   public Timestamp modified;
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonSelectProperty
 * @see NonSelectProperties
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonSelect {
}
