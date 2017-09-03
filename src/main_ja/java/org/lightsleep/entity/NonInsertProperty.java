// NonInsertProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * スーパークラスで定義されたフィールドに関連するカラムが INSERT SQL で使用されない事を示します。
 * 
 * <p>
 * 対象のフィールドは、<b>property</b> で指定します。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonInsertProperty(property="createdTime")</b>
 * <b>{@literal @}NonInsertProperty(property="updatedTime")</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonInsertProperties([</b>
 *   <b>{@literal @}NonInsertProperty(property='createdTime'),</b>
 *   <b>{@literal @}NonInsertProperty(property='updatedTime')</b>
 *  <b>])</b>
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonInsert
 * @see NonInsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonInsertProperties.class)
@Target({ElementType.TYPE})
public @interface NonInsertProperty {
	/**
	 * @return フィールドを指定するプロパティ名
	 * @since 2.0.0
	 */
	String property();

	/** @return フィールドに関連するカラムが INSERT SQL で使用されないなら true、そうでなければ false */
	String value();
}
