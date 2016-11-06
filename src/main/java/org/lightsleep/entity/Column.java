/*
	Column.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates the column name associated with the field.<br>
	If the column name is the same as the field name, you do not need to specify this annotation.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact {

 <b>{@literal @}Column("family_name")</b>
  public String familyName;

 <b>{@literal @}Column("given_name")</b>
  public String givenName;
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
SELECT ..., <b>family_name</b>, <b>given_name</b>, ... FROM Contact
</pre></div>

	@since 1.0.0
	@see ColumnProperty
	@see ColumnProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	/** @return the column name */
// 1.2.0
//	String value() default "";
	String value();
////
}
