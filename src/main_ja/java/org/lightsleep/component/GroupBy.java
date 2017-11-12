// GroupBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * SQLのGROUP BYを構成します。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class GroupBy implements SqlComponent, Cloneable {
	/**
	 * 空のGroupByを構築します。
	 */
	public GroupBy() {
	}

	/**
	 * GroupByの構成要素を追加します。
	 *
	 * @param expression 追加する構成要素
	 *
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>expression</b>がnullの場合
	 */
	public GroupBy add(Expression expression) {
		return null;
	}

	/**
	 * GroupByの構成要素を追加します。
	 *
	 * @param content 式の内容
	 * @param arguments 式の引数
	 * @return this object
	 *
	 * @throws NullPointerException <b>content</b> or <b>arguments</b> is null
	 *
	 * @since 1.9.1
	 */
	public GroupBy add(String content, Object... arguments) {
		return null;
	}

	/**
	 * GroupByの構成要素のリストを返します。
	 *
	 * @return 構成要素のリスト
	 */
	public List<Expression> elements() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		return  null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public GroupBy clone() {
		return  null;
	}
}
