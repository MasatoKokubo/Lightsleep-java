/*
	Table.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	クラスに関連するデータベース・テーブル名を示します。<br>
	テーブル名がクラス名と同じであれば、このアノテーションを指定する必要はありません。<br>
	<b>@Table("super")</b> を指定した場合は、スーパークラスのクラス名がテーブル名となります。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
public class Contact {
 {@literal @}Key {@literal @}NonInsert
  public String id;

 <b>{@literal @}Table("super")</b>
 {@literal @}InsertProperty(property="id", expression="Contact_seq.nextVal")
  public static class Oracle extends Contact {
</pre></div>

	@since 1.0.0
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
	/** @return テーブル名 */
	String value();
}
