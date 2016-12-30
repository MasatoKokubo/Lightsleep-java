/*
	NonInsert.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates that the column related the field is not used in INSERT SQL.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact {
  {@literal @}Key<b>{@literal @}NonInsert</b>public int id;
</pre></div>

	@since 1.0.0
	@see NonInsertProperty
	@see NonInsertProperties
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonInsert {
}
