/*
	InsertProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	INSERT SQL で、フィールド値の代わりに使用される式を示します。<br>
	フィールドは、プロパティ名で指定します。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
<b>{@literal @}InsertProperty(property="created", expression="CURRENT_TIMESTAMP")</b>
<b>{@literal @}InsertProperty(property="modified", expression="CURRENT_TIMESTAMP")</b>
public class Contact {

  public Timestamp created;
  public Timestamp modified;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
INSERT INTO Contact (..., <b>created</b>, <b>modified</b>) VALUES (..., <b>CURRENT_TIMESTAMP</b>, <b>CURRENT_TIMESTAMP</b>)
</pre></div>

	@since 1.3.0
	@see Insert
	@see InsertProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(InsertProperties.class)
@Target({ElementType.TYPE})
public @interface InsertProperty {
	/** @return プロパティ名 */
	String property();

	/** @return 式 */
	String expression();
}
