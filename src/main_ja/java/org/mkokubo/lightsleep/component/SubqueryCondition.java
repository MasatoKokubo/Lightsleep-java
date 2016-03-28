/*
	SubQueryCondition.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

import java.util.List;

import org.mkokubo.lightsleep.Sql;

/**
	サブクエリを使用する条件を構成します。

	@param <SE> サブクエリの対象テーブルに対応するエンティティの型

	@since 1.0.0
	@author Masato Kokubo
*/
public class SubqueryCondition<SE> implements Condition {
	/**
		<b>SubqueryCondition</b> を構築します。

		@param expression サブクエリの SELECT 文より左部分の式
		@param subSql サブクエリ用の <b>Sql</b> オブジェクト

		@throws NullPointerException <b>expression</b> または <b>subSql</b> が <b>null</b> の場合
	*/
	public SubqueryCondition(Expression expression, Sql<SE> subSql) {
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
		return null;
	}
}
