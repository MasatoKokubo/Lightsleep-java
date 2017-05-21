// Column.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラム名を示します。<br>
 * カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}Column("family_name")</b>
 *   public String familyName;
 *
 *  <b>{@literal @}Column("given_name")</b>
 *   public String givenName;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * SELECT ..., <b>family_name</b>, <b>given_name</b>, ... FROM Contact
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see ColumnProperty
 * @see ColumnProperties
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	/** @return カラム名 */
	String value();
}
