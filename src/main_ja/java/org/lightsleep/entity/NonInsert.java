// NonInsert.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import java.lang.annotation.*;

/**
 * フィールドに関連するカラムが INSERT SQL で使用されない事を示します。
 *
 * <div class="sampleTitle"><span>使用例</span></div>
 * <div class="sampleCode"><pre>
 * public class Contact {
 *  {@literal @}Key<b>{@literal @}NonInsert</b>public int id;
 * </pre></div>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see NonInsertProperty
 * @see NonInsertProperties
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonInsert {
}
