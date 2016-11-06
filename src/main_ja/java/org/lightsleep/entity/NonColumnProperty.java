/*
	NonColumnProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドがどのカラムにも関連しない事を示します。<br>
	フィールドは、プロパティ名で指定します。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
<b>{@literal @}NonColumnProperty("phones")</b>
<b>{@literal @}NonColumnProperty("addresses")</b>
public class Contact {

  public List&lt;Phone&gt; phones;
  public List&lt;Address&gt; addresses;
</pre></div>

	@since 1.3.0
	@see NonColumn
	@see NonColumnProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonColumnProperties.class)
@Target({ElementType.TYPE})
public @interface NonColumnProperty {
	/** @return プロパティ名 */
	String value();
}
