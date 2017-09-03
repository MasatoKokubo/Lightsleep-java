// UpdateProperty.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * Indicates an expression instead of the value of the field defined in superclass used in UPDATE SQL.
 *
 * <p>
 * Specifies the field by <b>property</b>.
 * </p>
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>{@literal @}UpdateProperty(property="updateCount", expression="{updateCount}+1")</b>
 * <b>{@literal @}UpdateProperty(property="updatedTime", expression="CURRENT_TIMESTAMP")</b>
 *  public class Person extends PersonKey {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class='exampleCode'><pre>
 * <b>{@literal @}UpdateProperties([</b>
 *   <b>{@literal @}UpdateProperty(property='updateCount', expression='{updateCount}+1'),</b>
 *   <b>{@literal @}UpdateProperty(property='updatedTime', expression='CURRENT_TIMESTAMP')</b>
 *  <b>])</b>
 *  class Person extends PersonBase {
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * UPDATE ..., <b>updateCount=updateCount+1</b>, <b>updatedTime=CURRENT_TIMESTAMP</b> WHERE ...
 * </pre></div>
 *
 * @since 1.3.0
 * @author Masato Kokubo
 * @see Update
 * @see UpdateProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UpdateProperties.class)
@Target({ElementType.TYPE})
public @interface UpdateProperty {
	/** @return the property name that specifies the field */
	String property();

	/** @return the expression */
	String expression();
}
