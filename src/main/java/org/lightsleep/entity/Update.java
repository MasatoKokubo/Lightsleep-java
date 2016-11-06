/*
	Update.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates the expression instead of the field value used in UPDATE SQL.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact {

 <b>{@literal @}Update("updateCount=updateCount+1")</b>
  public Integer updateCount;

 <b>{@literal @}Update("CURRENT_TIMESTAMP")</b>
  public Timestamp modified;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
UPDATE Contact ..., <b>updateCount=updateCount+1</b>, <b>modified=CURRENT_TIMESTAMP</b> WHERE ...
</pre></div>

	@since 1.0.0
	@see UpdateProperty
	@see UpdateProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Update {
	/** @return the expression */
// 1.2.0
//	String value() default "";
	String value();
////
}
