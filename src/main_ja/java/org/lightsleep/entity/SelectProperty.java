// SelectProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * SELECT SQLで、スーパークラスで定義されたフィールドに関連するカラム名の代わりに使用される式を示します。
 * 
 * <p>
 * このアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
 * 指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
 * <b>expression=""</b>を指定すると、スーパークラスでの指定が打ち消されます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property="fullName", expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property="fullName"){@literal @}NonUpdateProperty(property="fullName")
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}SelectProperty(property='fullName', expression="{firstName}||' '||{lastName}")</b>
 * {@literal @}NonInsertProperty(property='fullName'){@literal @}NonUpdateProperty(property='fullName')
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
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
