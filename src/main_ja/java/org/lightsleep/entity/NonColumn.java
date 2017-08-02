// NonColumn.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドがどのカラムにも関連しない事を示します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *
 *  <b>{@literal @}NonColumn</b>public List&lt;Phone&gt; phones;
 *  <b>{@literal @}NonColumn</b>public List&lt;Address&gt; addresses;
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonColumnProperty
 * @see NonColumnProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonColumn {
}
