/*
	UpdateProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates the expression instead of the field value used in UPDATE SQL.<br>
	Specifies the field by the property name.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
<b>{@literal @}UpdateProperty(property="updateCount", expression="updateCount=updateCount+1")</b>
<b>{@literal @}UpdateProperty(property="modified", expression="CURRENT_TIMESTAMP")</b>
public class Contact {

  public Integer updateCount;
  public Timestamp modified;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
UPDATE Contact ..., <b>updateCount=updateCount+1</b>, <b>modified=CURRENT_TIMESTAMP</b> WHERE ...
</pre></div>

	@since 1.3.0
	@see Update
	@see UpdateProperties
	@author Masato Kokubo
*/
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UpdateProperties.class)
@Target({ElementType.TYPE})
public @interface UpdateProperty {
	/** @return the property name */
	String property();

	/** @return the expression */
	String expression();
}
