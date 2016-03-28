/*
	Or.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
	条件の論理和 (OR) を構成します。

	@since 1.0.0
	@author Masato Kokubo
*/
public class Or extends LogicalCondition {
	/**
		空の <b>Or</b> を構築します。
	*/
	public Or() {
		super(Operator.OR);
	}

	/**
		条件ストリームから <b>Or</b> を構築します。

		@param conditionStream 条件ストリーム

		@throws NullPointerException <b>conditionStream</b> または <b>conditionStream</b> の要素が <b>null</b> の場合
	*/
	public Or(Stream<Condition> conditionStream) {
		super(Operator.OR, conditionStream);
	}

	/**
		条件コレクションから <b>Or</b> を構築します。

		@param conditions 条件コレクション

		@throws NullPointerException <b>conditions</b> または <b>conditions</b> の要素が <b>null</b> の場合
	*/
	public Or(Collection<Condition> conditions) {
		super(Operator.OR, conditions.stream());
	}

	/**
		条件配列から <b>Or</b> を構築します。

		@param conditions 条件配列

		@throws NullPointerException <b>conditions</b> または <b>conditions</b> の要素が <b>null</b> の場合
	*/
	public Or(Condition... conditions) {
		super(Operator.OR, Arrays.stream(conditions));
	}

}
