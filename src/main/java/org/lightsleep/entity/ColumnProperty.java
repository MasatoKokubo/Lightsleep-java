// ColumnProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the column name associated with the field.<br>
 * Specifies the field by <b>property</b>.
 *
 * <div class="sampleTitle"><span>Example of use</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}ColumnProperty(property="familyName" column="family_name")</b>
 * <b>{@literal @}ColumnProperty(property="givenName" column="given_name")</b>
 * public class Contact {
 *
 *   public String familyName;
 *   public String givenName;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * SELECT ..., <b>family_name</b>, <b>given_name</b>, ... FROM Contact
 * </pre></div>
 * 
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Column
 * @see ColumnProperties
 */
@Documented
// @Inherited // 1.5.1
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnProperties.class)
@Target({ElementType.TYPE})
public @interface ColumnProperty {
	/** @return the property name of the specified field */
	String property();

	/** @return the column name */
	String column();
}
