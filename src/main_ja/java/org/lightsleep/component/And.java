// Condition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 条件の論理積(AND)を構成します。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class And extends LogicalCondition {
	/**
	 * 条件ストリームから<b>And</b>を構築します。
	 *
	 * @param conditionStream 条件ストリーム
	 *
	 * @throws NullPointerException <b>conditionStream</b>または<b>conditionStream</b>の要素がnullの場合
	 */
	public And(Stream<Condition> conditionStream) {
		super(Operator.AND, conditionStream);
	}

	/**
	 * 条件コレクションから<b>And</b>を構築します。
	 *
	 * @param conditions 条件コレクション
	 *
	 * @throws NullPointerException <b>conditions</b>または<b>conditions</b>の要素がnullの場合
	 */
	public And(Collection<Condition> conditions) {
		super(Operator.AND, Objects.requireNonNull(conditions, "Collection<Condition> conditions").stream());
	}

	/**
	 * 条件配列から<b>And</b>を構築します。
	 *
	 * @param conditions 条件配列
	 *
	 * @throws NullPointerException <b>conditions</b>または<b>conditions</b>の要素がnullの場合
	 */
	public And(Condition... conditions) {
		super(Operator.AND, Arrays.stream(Objects.requireNonNull(conditions, "Condition[] conditions")));
	}
}
