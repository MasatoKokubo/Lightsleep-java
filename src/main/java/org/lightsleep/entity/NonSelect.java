/*
	NonSelect.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
	Indicates that the column related the field is not used in SELECT SQL.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact {

 <b>{@literal @}NonSelect</b>{@literal @}Insert("CURRENT_TIMESTAMP"){@literal @}NonUpdate
  public Timestamp created;

 <b>{@literal @}NonSelect</b>{@literal @}Insert("CURRENT_TIMESTAMP"){@literal @}Update("CURRENT_TIMESTAMP")
  public Timestamp modified;
</pre></div>

	@since 1.0.0
	@see NonSelectProperty
	@see NonSelectProperties
	@author Masato Kokubo
*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonSelect {
}
