/*
	Key.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	フィールドに関連するカラムがプライマリー・キーの一部である事を示します。

	<div class="sampleTitle"><span>使用例</span></div>
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
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Key {
}
