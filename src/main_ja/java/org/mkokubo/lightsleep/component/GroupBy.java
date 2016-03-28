/*
	GroupBy.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

import java.util.List;

import org.mkokubo.lightsleep.Sql;

/**
	SQL の GROUP BY  を構成します。

	@since 1.0
	@author Masato Kokubo
*/
public class GroupBy implements SqlComponent {
	/** 空の GroupBy */
	public static final GroupBy EMPTY = new GroupBy();

	/**
		空の GroupBy を構築します。
	*/
	public GroupBy() {
	}

	/**
		GroupBy の構成要素を追加します。

		@param expression 追加する構成要素

		@return このオブジェクト

		@throws NullPointerException <b>expression</b> が <b>null</b> の場合
	*/
	public GroupBy add(Expression expression) {
		return null;
	}

	/**
		GroupBy の構成要素のリストを返します。

		@return 構成要素のリスト
	*/
	public List<Expression> elements() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		return  null;
	}
}
