/*
	Key.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates that the column associated with the field is part of the primary key.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Phone {
 <b>{@literal @}Key</b>public int contactId;
 <b>{@literal @}Key</b>public short childIndex;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
UPDATE Phone ... WHERE <b>contactId=100 AND childIndex=0</b>
</pre></div>

	@since 1.0.0
	@see KeyProperty
	@see KeyProperties
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Key {
}
