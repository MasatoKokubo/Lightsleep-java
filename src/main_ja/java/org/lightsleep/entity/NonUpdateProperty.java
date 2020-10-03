// NonUpdateProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * スーパークラスで定義されたフィールドに関連するカラムがUPDATE SQLで使用されない事を示します。
 * 
 * <p>
 * このアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
 * 指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
 * <b>value=false</b>を指定すると、スーパークラスでの指定が打ち消されます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonUpdateProperty(property="createdTime")</b>
 *  public class Person extends PaersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}NonUpdateProperty(property='createdTime')</b>
 *  class Person extends PaersonBase {
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see NonUpdate
 * @see NonUpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonUpdateProperties.class)
@Target({ElementType.TYPE})
public @interface NonUpdateProperty {
    /**
     * @return フィールドを指定するプロパティ名
     * @since 2.0.0
     */
    String property();

    /** @return フィールドに関連するカラムがUPDATE SQLで使用されない場合は<b>true</b>、そうでなければ<b>false</b> */
    boolean value() default true;
}
