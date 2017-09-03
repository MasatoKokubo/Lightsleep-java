// ColumnType.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムの型を示します。<br>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}ColumnType(Long.class)</b>
 *   public Date birthday;
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}ColumnType(Long)</b>
 *   Date birthday
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
 * </pre></div>
 *
 * @since 1.8.0
 * @author Masato Kokubo
 * @see ColumnTypeProperty
 * @see ColumnTypeProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ColumnType {
	/** @return カラムの型 */
	Class<?> value();
}
