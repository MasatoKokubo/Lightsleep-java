// NonColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * スーパークラスで定義されたフィールドがどのカラムにも関連しない事を示します。
 * 
 * <p>
 * このアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
 * 指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
 * <b>value=false</b>を指定すると、スーパークラスでの指定が打ち消されます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonColumnProperty(property="phones")</b>
 * <b>{@literal @}NonColumnProperty(property="addresses")</b>
 *  public class PersonComposite {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonColumnProperties([</b>
 *   <b>{@literal @}NonColumnProperty(property='phones'),</b>
 *   <b>{@literal @}NonColumnProperty(property='addresses')</b>
 *  <b>])</b>
 *  class PersonComposite extends PersonCompositeBase {
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonColumn
 * @see NonColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonColumnProperties.class)
@Target({ElementType.TYPE})
public @interface NonColumnProperty {
	/**
	 * @return フィールドを指定するプロパティ名
	 * @since 2.0.0
	 */
	String property();

	/** @return フィールドがどのカラムにも関連しない場合は<b>true</b>、そうでなければ<b>false</b> */
	boolean value() default true;
}
