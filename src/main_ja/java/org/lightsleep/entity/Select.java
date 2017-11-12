// Select.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * SELECT SQLで、カラム名の代わりに使用される式を示します。
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Select("{firstName}||' '||{lastName}")</b>
 *  {@literal @}NonInsert{@literal @}NonUpdate
 *   public String fullName;
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 *  <b>{@literal @}Select("{firstName}||' '||{lastName}")</b>
 *  {@literal @}NonInsert{@literal @}NonUpdate
 *   String fullName
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT ..., <b>firstName||' '||lastName AS fullName</b>, ...
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see InsertProperty
 * @see InsertProperties
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Select {
	/** @return 式 */
	String value();
}
