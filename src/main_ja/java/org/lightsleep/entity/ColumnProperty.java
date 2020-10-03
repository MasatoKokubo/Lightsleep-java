// ColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * スーパークラスで定義されたフィールドに関連するカラム名を示します。
 * 
 * <p>
 * このアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
 * 指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
 * <b>column=""</b>を指定すると、スーパークラスでの指定が打ち消されます。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnProperty(property="name.first" column="firstName")</b>
 * <b>{@literal @}ColumnProperty(property="name.last" column="lastName")</b>
 *   public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}ColumnProperties([</b>
 *   <b>{@literal @}ColumnProperty(property='name.first' column='firstName'),</b>
 *   <b>{@literal @}ColumnProperty(property='name.last' column='lastName')</b>
 *  <b>])</b>
 *  public class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName</b>, <b>lastName</b>, ...
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Column
 * @see ColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnProperty {
    /** @return フィールドを指定するプロパティ名 */
    String property();

    /** @return カラム名 */
    String column();
}
