/*
	ColumnProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドに関連するカラム名を示します。<br>
	フィールドは、プロパティ名で指定します。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
<b>{@literal @}ColumnProperty(property="familyName" column="family_name")</b>
<b>{@literal @}ColumnProperty(property="givenName" column="given_name")</b>
public class Contact {

  public String familyName;
  public String givenName;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
SELECT ..., <b>family_name</b>, <b>given_name</b>, ... FROM Contact
</pre></div>

	@since 1.3.0
	@see Column
	@see ColumnProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnProperty {
	/** @return プロパティ名 */
	String property();

	/** @return カラム名 */
	String column();
}
