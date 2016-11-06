/*
	Not.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;

/**
	否定条件を構成します。

	@since 1.0.0
	@author Masato Kokubo
*/
public class Not implements Condition {
	private Condition condition;

	/**
		Not を構築します。

		@param condition 否定対象の条件

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	public Not( Condition condition) {
	}

	/**
		否定対象の条件を返します。

		@return 否定対象の条件
	*/
	public Condition condition() {
		return condition;
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
