// ColumnTypeProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * スーパークラスで定義されたフィールドに関連するカラムの型を示します。
 * 
 * <p>
 * 対象のフィールドは、<b>property</b> で指定します。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property="birthday" type=Long.class)</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnTypeProperty(property='birthday' type=Long)</b>
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * INSERT INTO ... (..., <b>birthday</b>, ...) VALUES (..., <b>1486210201099</b>, ...)
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
	/** @return フィールドを指定するプロパティ名 */
	String property();

	/** @return カラムの型 */
	Class<?> type();
}
