// Table.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * クラスに関連するデータベース・テーブル名を示します。
 *
 * <p>
 * テーブル名がクラス名と同じであれば、このアノテーションを指定する必要はありません。<br>
 * <b>@Table("super")</b> を指定した場合は、スーパークラスのクラス名がテーブル名となります。
 * </p>
 *
 * <div class="exampleTitle"><span>使用例 / Java</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}Table("Contact")</b>
 *  public class Person extends PersonBase {
 *
 *   <b>{@literal @}Table("super")</b>
 *    public static class Ex extends Person {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例 / Groovy</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}Table('Contact')</b>
 *  class Person extends PersonBase {
 *
 *   <b>{@literal @}Table('super')</b>
 *    static class Ex extends Person {
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
	/** @return テーブル名 */
	String value();
}
