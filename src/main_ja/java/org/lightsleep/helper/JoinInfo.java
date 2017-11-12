// JoinInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import org.lightsleep.component.Condition;

/**
 * 結合テーブルおよび条件の情報を持ちます。
 *
 * @param <JE> 結合するテーブルに対応するエンティティの型
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class JoinInfo<JE> implements SqlEntityInfo<JE> {
	/**
	 * 結合タイプ
	 */
	public enum JoinType {
		/** INNER JOIN */
		INNER(" INNER JOIN "),

		/** LEFT OUTER JOIN */
		LEFT(" LEFT OUTER JOIN "),

		/** RIGHT OUTER JOIN */
		RIGHT(" RIGHT OUTER JOIN ");

		private JoinType(String sql) {
		}

		/**
		 * SQL文字列を返します。
		 *
		 * @return SQL文字列
		 */
		public String sql() {
			return null;
		}
	}

	/**
	 * <b>JoinInfo</b>を構築します。
	 *
	 * @param joinType 結合タイプ
	 * @param entityInfo 結合テーブルに対応するエンティティ情報
	 * @param tableAlias 結合テーブルの別名
	 * @param on 結合条件
	 *
	 * @throws NullPointerException <b>joinType</b>, <b>entityInfo</b>, <b>tableAlias</b> または <b>on</b>がnullの場合
	 */
	public JoinInfo(JoinType joinType, EntityInfo<JE> entityInfo, String tableAlias, Condition on) {
	}

	/**
	 * 結合タイプを返します。
	 *
	 * @return 結合タイプ
	 */
	public JoinType joinType() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityInfo<JE> entityInfo() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String tableAlias() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JE entity() {
		return null;
	}

	/**
	 * 結合条件を返します。
	 *
	 * @return 結合条件
	 */
	public Condition on() {
		return null;
	}
}
