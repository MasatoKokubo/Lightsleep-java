/*
	NonUpdate.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドに関連するカラムが UPDATE SQL で使用されない事を示します。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
public class Contact {

 {@literal @}Insert("CURRENT_TIMESTAMP")<b>{@literal @}NonUpdate</b>
  public Timestamp created;
</pre></div>

	@since 1.0.0
	@see NonUpdateProperty
	@see NonUpdateProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonUpdate {
}
