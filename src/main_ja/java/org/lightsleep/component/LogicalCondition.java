// LogicalCondition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;
import java.util.stream.Stream;

import org.lightsleep.Sql;

/**
 * <b>And</b> および <b>Or</b> の抽象スーパークラスです。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public abstract class LogicalCondition implements Condition {
	/**
	 * 論理演算子です。
	 */
	protected enum Operator {
		/** AND */
		AND(" AND "),

		/** OR */
		OR (" OR ");

		private Operator(String sql) {
		}

		/**
		 * SQL 表現文字列を返します。
		 *
		 * @return SQL 表現文字列
		 */
		public String sql() {
			return null;
		}
	}

	/**
	 * 条件ストリームの各条件からなる <b>LogicalCondition</b> を構築します。
	 *
	 * @param operator 論理演算子
	 * @param conditionStream 条件ストリーム
	 *
	 * @throws NullPointerException <b>operator</b>, <b>conditionStream</b> または <b>conditionStream</b> の要素が null の場合
	 */
	public LogicalCondition(Operator operator, Stream<Condition> conditionStream) {
	}

	/**
	 * 構成要素の不変リストを返します。
	 *
	 * @return 構成要素のリスト
	 */
	public List<Condition> conditions() {
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
	 * この条件の構成要素を最適化して返します。<br>
	 * 具体的には、
	 * <b>conditions.isEmpty()</b> なら<b>Condition.EMPTY</b>、
	 * <b>conditions.size() == 1</b> なら<b>conditions.get(0)</b>、
	 * そうでなければ <b>this</b> を返します。
	 *
	 * @return この条件の構成要素を最適化した条件
	 *
	 * @since 1.8.8
	 */
	public Condition optimized() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		return null;
	}
}
