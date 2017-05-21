// ColumnTypeProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムの型を示します。<br>
 * 対象のフィールドは、<b>property</b> で指定します。
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property="birthday" type=Long.class)</b>
 * public class Contact {
 *
 *   public Date birthday;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * INSERT INTO Contact (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
 * </pre></div>
 *
 * @since 1.8.0
 * @author Masato Kokubo
 * @see ColumnType
 * @see ColumnTypeProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnTypeProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnTypeProperty {
	/** @return 指定対象のフィールドのプロパティ名 */
	String property();

	/** @return カラムの型 */
	Class<?> type();
}
