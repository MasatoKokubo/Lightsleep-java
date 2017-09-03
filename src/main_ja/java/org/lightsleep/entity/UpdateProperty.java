// UpdateProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * UPDATE SQL で、スーパークラスで定義されたフィールドの値の代わりに使用される式を示します。
 * 
 * <p>
 * このアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
 * 指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
 * <b>expression=""</b> を指定すると、スーパークラスでの指定が打ち消されます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}UpdateProperty(property="updateCount", expression="{updateCount}+1")</b>
 * <b>{@literal @}UpdateProperty(property="updatedTime", expression="CURRENT_TIMESTAMP")</b>
 *  public class Person extends PersonKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class='exampleCode'><pre>
 * <b>{@literal @}UpdateProperties([</b>
 *   <b>{@literal @}UpdateProperty(property='updateCount', expression='{updateCount}+1'),</b>
 *   <b>{@literal @}UpdateProperty(property='updatedTime', expression='CURRENT_TIMESTAMP')</b>
 *  <b>])</b>
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成される SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ..., <b>updateCount=updateCount+1</b>, <b>updatedTime=CURRENT_TIMESTAMP</b> WHERE ...
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Update
 * @see UpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UpdateProperties.class)
@Target({ElementType.TYPE})
public @interface UpdateProperty {
	/** @return フィールドを指定するプロパティ名 */
	String property();

	/** @return 式 */
	String expression();
}
