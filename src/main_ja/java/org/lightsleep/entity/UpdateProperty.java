/*
	UpdateProperty.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * UPDATE SQL で、フィールド値の代わりに使用される式を示します。
 * 対象のフィールドは、<b>property</b> で指定します。
 * 
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * <b>{@literal @}UpdateProperty(property="updateCount", expression="updateCount=updateCount+1")</b>
 * <b>{@literal @}UpdateProperty(property="modified", expression="CURRENT_TIMESTAMP")</b>
 * public class Contact {
 *
 *   public Integer updateCount;
 *   public Timestamp modified;
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * UPDATE Contact ..., <b>updateCount=updateCount+1</b>, <b>modified=CURRENT_TIMESTAMP</b> WHERE ...
 * </pre></div>
 * 
 * @since 1.3.0
 * @see Update
 * @see UpdateProperties
 * @author Masato Kokubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UpdateProperties.class)
@Target({ElementType.TYPE})
public @interface UpdateProperty {
	/** @return 指定対象のフィールドのプロパティ名 */
	String property();

	/** @return 式 */
	String expression();
}
