// Table.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the table name associated with the class.<br>
 * If the table name is the same as the class name, you do not need to specify this annotation.<br>
 * If you specify <b>{@literal @}Table("super")</b>, the class name of the superclass is the table name.
 *
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *  {@literal @}Key {@literal @}NonInsert
 *   public String id;
 *
 *  <b>{@literal @}Table("super")</b>
 *  {@literal @}InsertProperty(property="id", expression="Contact_seq.nextVal")
 *   public static class Oracle extends Contact {
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
// 1.2.0
//	String value() default "";
	String value();
////
}
