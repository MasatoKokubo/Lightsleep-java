// Table.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates the table name related to the class.
 *
 * <p>
 * If the table name is the same as the class name, you do not need to specify this annotation.<br>
 * If you specify <b>{@literal @}Table("super")</b>, the class name of the superclass is the table name.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}Table("Contact")</b>
 *  public class Person extends PersonBase {
 *
 *   <b>{@literal @}Table("super")</b>
 *    public static class Ex extends Person {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}Table('Contact')</b>
 *  class Person extends PersonBase {
 *
 *   <b>{@literal @}Table('super')</b>
 *    static class Ex extends Person {
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    String value();
}
