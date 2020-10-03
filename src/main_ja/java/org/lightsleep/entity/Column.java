// Column.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラム名を示します。
 *
 * <p>
 * カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Column("firstName")</b>
 *   public String first;
 *  <b>{@literal @}Column("lastName")</b>
 *   public String last;
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Column('firstName')</b>
 *   String first
 *  <b>{@literal @}Column('lastName')</b>
 *   String last
 * </pre></div>
 * 
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName</b>, <b>lastName</b>, ...
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see ColumnProperty
 * @see ColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    /** @return カラム名 */
    String value();
}
