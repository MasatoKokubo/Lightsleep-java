// KeyProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * プロパティに関連するカラムがプライマリー・キーの一部である事を示します。<br>
 *
 * <p>
 * 対象のフィールドは、<b>property</b> で指定します。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}KeyProperty(property="contactId")</b>
 * <b>{@literal @}KeyProperty(property="childIndex")</b>
 *  public class Child extends ChildKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}KeyProperties([</b>
 *   <b>{@literal @}KeyProperty(property='contactId'),</b>
 *   <b>{@literal @}KeyProperty(property='childIndex')</b>
 *  <b>])</b>
 *  class Child extends ChildKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ... WHERE <b>contactId=100 AND childIndex=1</b>
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Key
 * @see KeyProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(KeyProperties.class)
@Target({ElementType.TYPE})
public @interface KeyProperty {
	/**
	 * @return フィールドを指定するプロパティ名
	 * @since 2.0.0
	 */
	String property();

	/** @return フィールドに関連するカラムがキーの一部であれば true、そうでなければ false */
	String value();
}
