// SelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * SELECT SQL で、スーパークラスで定義されたフィールドに関連するカラム名の代わりに使用される式を示します。
 * 
 * <p>
 * 対象のフィールドは、<b>property</b> で指定します。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property="fullName", expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property="fullName"){@literal @}NonUpdateProperty(property="fullName")
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property='fullName', expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property='fullName'){@literal @}NonUpdateProperty(property='fullName')
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName||' '||lastName AS fullName</b>, ...
 * </pre></div>
 *
 * @since 1.3.0
 * @see Select
 * @see SelectProperties
 * @author Masato Kokubo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SelectProperties.class)
@Target({ElementType.TYPE})
public @interface SelectProperty {
	/** @return フィールドを指定するプロパティ名 */
	String property();

	/** @return 式 */
	String expression();
}
