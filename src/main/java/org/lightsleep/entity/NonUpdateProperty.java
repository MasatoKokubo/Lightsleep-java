/*
	NonUpdateProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates that the column related the field is not used in UPDATE SQL.<br>
	Specifies the field by the property name.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
{@literal @}InsertProperty(property="created", expression="CURRENT_TIMESTAMP")
<b>{@literal @}NonUpdateProperty("created")</b>
public class Contact {

  public Timestamp created;
</pre></div>

	@since 1.3.0
	@see NonUpdate
	@see NonUpdateProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NonUpdateProperties.class)
@Target({ElementType.TYPE})
public @interface NonUpdateProperty {
	/** @return the property name */
	String value();
}
