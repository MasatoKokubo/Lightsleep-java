/*
	NonSelectProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドに関連するカラムが SELECT SQL で使用されない事を示します。<br>
	フィールドは、プロパティ名で指定します。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
<b>{@literal @}NonSelectProperty("created")</b>
{@literal @}InsertProperty(property="created", expression="CURRENT_TIMESTAMP")
{@literal @}NonUpdateProperty("created")
<b>{@literal @}NonSelectProperty("modified")</b>
{@literal @}InsertProperty(property="modified", expression="CURRENT_TIMESTAMP")
{@literal @}UpdateProperty(property="modified", expression="CURRENT_TIMESTAMP")
public class Contact {

  public Timestamp created;
  public Timestamp modified;
</pre></div>

	@since 1.3.0
	@see NonSelect
	@see NonSelectProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonSelectProperties.class)
@Target({ElementType.TYPE})
public @interface NonSelectProperty {
	/** @return プロパティ名 */
	String value();
}
